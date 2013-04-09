package za.ac.sun.cs.intlola;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import za.ac.sun.cs.intlola.gui.LoginDialog;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public class Intlola extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "za.ac.sun.cs.goanna";

	private static final QualifiedName RECORD_KEY = new QualifiedName(
			"intlola", "record");

	private static final Boolean RECORD_ON = new Boolean(true);

	public static final int LAUNCHED = -6666;

	private static final int ZIP_BUFFER_SIZE = 2048;

	private static Intlola plugin;

	public static Intlola getDefault() {
		return Intlola.plugin;
	}

	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Intlola.PLUGIN_ID,
				path);
	}

	public static IProject getSelectedProject(final ExecutionEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		final Object element = selection.getFirstElement();
		IProject ret = null;
		if (element instanceof IProject) {
			ret = (IProject) element;
		}
		if (element instanceof IAdaptable) {
			final IAdaptable adaptable = (IAdaptable) element;
			final Object adapter = adaptable.getAdapter(IProject.class);
			ret = (IProject) adapter;
		}
		return ret;
	}

	private static IntlolaSender getSender(final String project) {
		final SendMode mode = SendMode.getMode(Intlola.getDefault()
				.getPreferenceStore().getString(PreferenceConstants.P_SEND));
		final String uname = Intlola.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_UNAME);
		final String address = Intlola.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_ADDRESS);
		final int port = Intlola.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.P_PORT);
		return new IntlolaSender(uname, project, mode, address, port);
	}

	public static IWorkspace getWorkspace() {
		return org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
	}

	public static void log(final Exception e, final Object... msgs) {
		if (Intlola.getDefault() != null) {
			Intlola.getDefault()._log(e, msgs);
		}
	}

	private static boolean login(final Shell shell) {
		if (Intlola.sender.openConnection()) {
			final LoginDialog dialog = new LoginDialog(shell,
					Intlola.sender.getUsername());
			while (!Intlola.sender.loggedIn()) {
				final int code = dialog.open();
				if (code == Window.OK) {
					Intlola.sender.login(dialog.getUserName(),
							dialog.getPassword());
				} else {
					break;
				}
			}
		}
		return Intlola.sender.loggedIn();
	}

	public static void removeDir(final String dirname, final boolean removeRoot) {
		Intlola.removeDirRecur(new File(dirname), removeRoot);
	}

	public static void stopRecord(final IProject project, final Shell shell) {
		Intlola.log(null, "Intlola record stopping", project.getName());
		if (sender.mode.equals(SendMode.SINGLE)) {
			sendZip(shell);
		} else if(sender.mode.equals(SendMode.MULTIPLE)) {
			Intlola.sender.logout();
		}
		try {
			project.setSessionProperty(Intlola.RECORD_KEY, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private static void sendZip(final Shell shell) {
		String filename = Intlola.sender.getProject() + ".zip";
		try {
			final FileOutputStream outfile = new FileOutputStream(filename);
			final BufferedOutputStream out = new BufferedOutputStream(outfile);
			final ZipOutputStream outzip = new ZipOutputStream(out);
			Intlola.zipDir(outzip, Intlola.plugin.getStateLocation().toFile());
			outzip.close();
			out.flush();
			out.close();
			Intlola.sender.sendFile(filename, FileType.ZIP);
			Intlola.sender.logout();
		} catch (final FileNotFoundException e) {
			MessageDialog.openError(shell, "Problem", "Could not open file \""
					+ filename + "\".");
			Intlola.log(e);
		} catch (final IOException e) {
			MessageDialog.openError(shell, "Problem",
					"IO error during zip file creation (" + e.getMessage()
							+ ")");
			Intlola.log(e);
		}
	}

	private static void zipDir(final ZipOutputStream outzip, final File dirfile) {
		for (final File file : dirfile.listFiles()) {
			if (file.isDirectory()) {
				Intlola.zipDir(outzip, file);
				continue;
			}
			try {
				final byte[] data = new byte[Intlola.ZIP_BUFFER_SIZE];
				final FileInputStream origin = new FileInputStream(file);
				outzip.putNextEntry(new ZipEntry(file.getName()));
				int count;
				while ((count = origin.read(data, 0, Intlola.ZIP_BUFFER_SIZE)) != -1) {
					outzip.write(data, 0, count);
				}
				outzip.closeEntry();
				origin.close();
			} catch (final IOException e) {
				Intlola.log(e);
			}
		}
	}

	private IResourceChangeListener changeListener = null;

	private boolean listenersAdded = false;

	protected static IntlolaSender sender;

	public static boolean getRecordStatus(final IProject project) {
		try {
			final Boolean record = (Boolean) project
					.getSessionProperty(Intlola.RECORD_KEY);
			return record == Intlola.RECORD_ON;
		} catch (final CoreException e) {
			Intlola.log(e);
			return false;
		}
	}

	private static void removeDirRecur(final File file, final boolean removeRoot) {
		if (file.isDirectory()) {
			for (final File f : file.listFiles()) {
				Intlola.removeDirRecur(f, true);
			}
			if (removeRoot) {
				file.delete();
			}
		} else {
			file.delete();
		}
	}

	public static void startRecord(final IProject project, final Shell shell) {
		try {
			Intlola.removeDir(Intlola.plugin.getStateLocation().toString(),
					false);
			Intlola.sender = Intlola.getSender(project.getName());
			if (Intlola.login(shell)) {
				project.setSessionProperty(Intlola.RECORD_KEY,
						Intlola.RECORD_ON);
			}
		} catch (final CoreException e) {
			Intlola.log(e);
		}
	}

	public Intlola() {
		Intlola.plugin = this;
	}

	public void _log(final Exception e, final Object... msgs) {
		String logMsg = "";
		for (Object msg : msgs) {
			if (msg == null) {
				msg = "NULL";
			}
			logMsg += " " + msg.toString();
		}
		getLog().log(
				new Status(IStatus.INFO, Intlola.PLUGIN_ID, IStatus.OK, logMsg,
						e));
	}

	@Override
	public void earlyStartup() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		if (!listenersAdded) {
			changeListener = new IntlolaListener();
			Intlola.getWorkspace().addResourceChangeListener(changeListener,
					IResourceChangeEvent.POST_CHANGE);
			listenersAdded = true;
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		Intlola.plugin = null;
		super.stop(context);
	}
}

package za.ac.sun.cs.intlola;

import java.io.File;
import java.util.Arrays;

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
import za.ac.sun.cs.intlola.processing.Processor;

public class Intlola extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "za.ac.sun.cs.goanna";
	public static String STORE_PATH;
	// public static String FILE_DIR;

	private static final QualifiedName RECORD_KEY = new QualifiedName(
			"intlola", "record");

	private static final Boolean RECORD_ON = new Boolean(true);

	public static final int LAUNCHED = -6666;

	private static Intlola plugin;

	protected static Processor proc;

	public static Intlola getDefault() {
		return Intlola.plugin;
	}

	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Intlola.PLUGIN_ID,
				path);
	}

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

	public static IProject getSelectedProject(final ExecutionEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		for (Object element : selection.toList()) {
			if (element instanceof IProject) {
				return (IProject) element;
			}
			if (element instanceof IAdaptable) {
				final IAdaptable adaptable = (IAdaptable) element;
				final Object adapter = adaptable.getAdapter(IProject.class);
				return (IProject) adapter;
			}
		}
		return null;
	}

	public static IWorkspace getWorkspace() {
		return org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
	}

	public static void log(final Exception e, final Object... msgs) {
		if (Intlola.getDefault() != null) {
			Intlola.getDefault()._log(e, msgs);
		} else {
			System.out.println(Arrays.toString(msgs) + e.getMessage());
		}
	}

	public static void removeDir(final String dirname, final boolean removeRoot) {
		Intlola.removeDirRecur(new File(dirname), removeRoot);
	}

	public static void startRecord(final IProject project, final Shell shell) {
		try {
			Intlola.removeDir(Intlola.plugin.getStateLocation().toString(),
					false);
			proc = getProcessor(project.getName());
			if (Intlola.login(shell)) {
				project.setSessionProperty(Intlola.RECORD_KEY,
						Intlola.RECORD_ON);
			}
		} catch (final CoreException e) {
			Intlola.log(e);
		}
	}

	public static void stopRecord(final IProject project, final Shell shell) {
		Intlola.log(null, "Intlola record stopping", project.getName());
		if (proc.getMode().isArchive()) {
			proc.handleArchive(STORE_PATH, STORE_PATH);
		} else if (proc.getMode().isRemote()) {
			Intlola.proc.logout();
		}
		try {
			project.setSessionProperty(Intlola.RECORD_KEY, null);
		} catch (final CoreException e) {
			e.printStackTrace();
		}

	}

	private static Processor getProcessor(final String project) {
		final IntlolaMode mode = IntlolaMode.getMode(Intlola.getDefault()
				.getPreferenceStore().getString(PreferenceConstants.P_MODE));
		final String uname = Intlola.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_UNAME);
		final String address = Intlola.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_ADDRESS);
		final int port = Intlola.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.P_PORT);
		return new Processor(uname, project, mode, address, port);
	}

	private static boolean login(final Shell shell) {
		final LoginDialog dialog = new LoginDialog(shell,
				"Intlola login configuration", Intlola.proc.getUsername(),
				proc.getProject(), proc.getMode(), proc.getAddress(),
				proc.getPort());
		IntlolaError err = IntlolaError.DEFAULT;
		while (!err.equals(IntlolaError.SUCCESS)) {
			final int code = dialog.open(err);
			if (code == Window.OK) {
				err = Intlola.proc
						.login(dialog.getUserName(), dialog.getPassword(),
								dialog.getProject(), dialog.getMode(),
								dialog.getAddress(), dialog.getPort());
			} else {
				break;
			}
		}
		return err.equals(IntlolaError.SUCCESS);
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

	private IResourceChangeListener changeListener = null;

	private boolean listenersAdded = false;

	public Intlola() {
		Intlola.plugin = this;
		STORE_PATH = Intlola.getDefault().getStateLocation().toOSString();
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

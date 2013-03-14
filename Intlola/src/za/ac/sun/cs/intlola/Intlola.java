package za.ac.sun.cs.intlola;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public class Intlola extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "za.ac.sun.cs.goanna";

	private static final QualifiedName RECORD_KEY = new QualifiedName(
			"intlola", "record");

	private static final Boolean RECORD_ON = new Boolean(true);

	public static final int LAUNCHED = -6666;

	private static final int ZIP_BUFFER_SIZE = 2048;

	private static final String PATTERN = "([^/]*/)*[-a-zA-z0-9]+([.][-a-zA-z0-9]+)*[+][0-9]+";

	private static Intlola plugin = new Intlola();

	private IResourceChangeListener changeListener = null;

	private boolean listenersAdded = false;

	protected static IntlolaSender sender;

	/*public Intlola() {
		plugin = this;
	}*/

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		if (!listenersAdded) {
			changeListener = new IntlolaListener();
			getWorkspace().addResourceChangeListener(changeListener,
					IResourceChangeEvent.POST_CHANGE);
			listenersAdded = true;
		}
	}

	private static IntlolaSender getSender(String project) {
		SendMode mode = SendMode.getMode(getDefault().getPreferenceStore()
				.getString(PreferenceConstants.P_SEND));
		String uname = getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_UNAME);
		String passwd = getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_PASSWD);
		String address = getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_ADDRESS);
		int port = getDefault().getPreferenceStore().getInt(
				PreferenceConstants.P_PORT);
		return new IntlolaSender(uname, passwd, project, mode, address, port);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Intlola getDefault() {
			return plugin;
	}

	public static IWorkspace getWorkspace() {
		return org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
	}

	public static boolean getRecordStatus(IProject project) {
		try {
			Boolean record = (Boolean) project.getSessionProperty(RECORD_KEY);
			return record == RECORD_ON;
		} catch (CoreException e) {
			return false;
		}
	}

	public static void startRecord(IProject project) {
		try {
			removeDir(plugin.getStateLocation().toString(), false);
			project.setSessionProperty(RECORD_KEY, RECORD_ON);
			sender = getSender(project.getName());
		} catch (CoreException e) {
		}
	}

	private static void removeDirRecur(File file, boolean removeRoot) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				removeDirRecur(f, true);
			}
			if (removeRoot) {
				file.delete();
			}
		} else {
			file.delete();
		}
	}

	public static void removeDir(String dirname, boolean removeRoot) {
		removeDirRecur(new File(dirname), removeRoot);
	}

	private static String getFilename(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFileName(sender.project + ".zip");
		String filename = null;
		boolean isDone = false;
		while (!isDone) {
			filename = dialog.open();
			if (filename == null) {
				isDone = true;
			} else {
				File file = new File(filename);
				if (file.exists()) {
					if (file.isFile()) {
						isDone = MessageDialog
								.openQuestion(
										shell,
										"Replace?",
										"File \""
												+ filename
												+ "\" already exists.  Do you want to replace it?");
					} else {
						MessageDialog
								.openInformation(
										shell,
										"Irregular file",
										"File \""
												+ filename
												+ "\" exists, but it is not a regular file.  Please choose another file.");
					}
				} else {
					isDone = true;
				}
			}
		}
		return filename;
	}

	private static void zipDir(ZipOutputStream outzip, File dirfile) {
		for (File file : dirfile.listFiles()) {
			if (file.isDirectory()) {
				zipDir(outzip, file);
				continue;
			}
			try {
				byte[] data = new byte[ZIP_BUFFER_SIZE];
				FileInputStream origin = new FileInputStream(file);
				outzip.putNextEntry(new ZipEntry(file.getName()));
				int count;
				while ((count = origin.read(data, 0, ZIP_BUFFER_SIZE)) != -1) {
					outzip.write(data, 0, count);
				}
				outzip.closeEntry();
				origin.close();
			} catch (IOException e) {
			}
		}
	}

	public static void stopRecord(IProject project, Shell shell) {
		boolean isDone = false;
		while (!isDone) {
			String filename = getFilename(shell);
			if (filename == null) {
				MessageDialog.openInformation(shell, "Unsaved data",
						"Intlola data not saved - still recording.");
				return;
			} else if (filename.matches(PATTERN)) {
				String hostname = filename.replaceAll(PATTERN, "$2");
				int port = Integer.parseInt(filename.replaceAll(PATTERN, "$4"));
				try {
					Socket requestSocket = new Socket(hostname, port);
					BufferedOutputStream out = new BufferedOutputStream(
							requestSocket.getOutputStream());
					ZipOutputStream outzip = new ZipOutputStream(out);
					zipDir(outzip, plugin.getStateLocation().toFile());
					outzip.close();
					out.flush();
					out.close();
					requestSocket.close();
					project.setSessionProperty(RECORD_KEY, null);
					isDone = true;
				} catch (CoreException e) {
				} catch (UnknownHostException e) {
					MessageDialog.openError(shell, "Problem",
							"Could not connect to \"" + hostname + ":" + port
									+ "\".");
				} catch (IOException e) {
					MessageDialog.openError(
							shell,
							"Problem",
							"IO error during zip file transmission ("
									+ e.getMessage() + ")");
				}
			} else {
				try {
					FileOutputStream outfile = new FileOutputStream(filename);
					BufferedOutputStream out = new BufferedOutputStream(outfile);
					ZipOutputStream outzip = new ZipOutputStream(out);
					zipDir(outzip, plugin.getStateLocation().toFile());
					outzip.close();
					out.flush();
					out.close();
					project.setSessionProperty(RECORD_KEY, null);
					isDone = true;
					sender.send(SendMode.ONSTOP, filename);
				} catch (CoreException e) {
				} catch (FileNotFoundException e) {
					MessageDialog.openError(shell, "Problem",
							"Could not open file \"" + filename + "\".");
				} catch (IOException e) {
					MessageDialog.openError(
							shell,
							"Problem",
							"IO error during zip file creation ("
									+ e.getMessage() + ")");
				}
			}
		}
	}

	public static void log(Object msg) {
		getDefault().log(msg, null);
	}

	public void log(Object msg, Exception e) {
		if (msg == null) {
			msg = "NULL";
		}
		getLog().log(
				new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg.toString(),
						e));
	}

	public static IProject getSelectedProject(ExecutionEvent event) {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		Object element = selection.getFirstElement();
		IProject ret = null;
		if (element instanceof IProject) {
			ret = (IProject) element;
		}
		if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			Object adapter = adaptable.getAdapter(IProject.class);
			ret = (IProject) adapter;
		}
		return ret;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	public void earlyStartup() {
	}

}

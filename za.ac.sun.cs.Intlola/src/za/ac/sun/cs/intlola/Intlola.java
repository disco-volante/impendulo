package za.ac.sun.cs.intlola;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class Intlola extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "za.ac.sun.cs.goanna";

	private static final QualifiedName RECORD_KEY = new QualifiedName(
			"intlola", "record");

	private static final QualifiedName LOCAL_KEY = new QualifiedName("intlola",
			"local");

	private static final QualifiedName REMOTE_KEY = new QualifiedName(
			"intlola", "remote");

	private static final Boolean RECORD_ON = new Boolean(true);

	private static final Boolean LOCAL_ON = new Boolean(true);

	private static final Boolean REMOTE_ON = new Boolean(true);

	public static final int LAUNCHED = -6666;

	private static final int ZIP_BUFFER_SIZE = 2048;

	private static final String ADDRESS = "localhost";

	private static final int PORT = 9998;

	private static Intlola plugin;

	private IResourceChangeListener changeListener = null;

	private boolean listenersAdded = false;

	public Intlola() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		if (!listenersAdded) {
			changeListener = new IntlolaListener();
			getWorkspace().addResourceChangeListener(changeListener,
					IResourceChangeEvent.POST_CHANGE);
			listenersAdded = true;
		}
	}

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

	public static boolean getLocalStatus(IProject project) {
		try {
			Boolean record = (Boolean) project.getSessionProperty(LOCAL_KEY);
			return record == LOCAL_ON;
		} catch (CoreException e) {
			return false;
		}
	}

	public static boolean getRemoteStatus(IProject project) {
		try {
			Boolean record = (Boolean) project.getSessionProperty(REMOTE_KEY);
			return record == REMOTE_ON;
		} catch (CoreException e) {
			return false;
		}
	}

	public static void startLocalRecord(IProject project) {
		try {
			removeDir(plugin.getStateLocation().toString(), false);
			project.setSessionProperty(RECORD_KEY, RECORD_ON);
			project.setSessionProperty(LOCAL_KEY, LOCAL_ON);
		} catch (CoreException e) {
			// do nothing
		}
	}

	public static void startRemoteRecord(IProject project) {
		try {
			project.setSessionProperty(RECORD_KEY, RECORD_ON);
			project.setSessionProperty(REMOTE_KEY, REMOTE_ON);
		} catch (CoreException e) {
			// do nothing
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
		dialog.setFileName("intlola.zip");
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
				// do nothing
			}
		}
	}

	public static void stopLocalRecord(IProject project, Shell shell) {
		boolean isDone = false;
		while (!isDone) {
			String filename = getFilename(shell);
			if (filename == null) {
				MessageDialog.openInformation(shell, "Unsaved data",
						"Intlola data not saved - still recording.");
				return;
			} else if (filename
					.matches("([^/]*/)*[-a-zA-z0-9]+([.][-a-zA-z0-9]+)*[+][0-9]+")) {
				String hostname = filename
						.replaceAll(
								"([^/]*/)*([-a-zA-z0-9]+([.][-a-zA-z0-9]+)*)[+]([0-9]+)",
								"$2");
				int port = Integer
						.parseInt(filename
								.replaceAll(
										"([^/]*/)*([-a-zA-z0-9]+([.][-a-zA-z0-9]+)*)[+]([0-9]+)",
										"$4"));
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
					project.setSessionProperty(LOCAL_KEY, null);
					project.setSessionProperty(REMOTE_KEY, null);
					isDone = true;
				} catch (CoreException e) {
					// do nothing
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
					project.setSessionProperty(LOCAL_KEY, null);
					project.setSessionProperty(REMOTE_KEY, null);
					isDone = true;
					sendFile(filename);
				} catch (CoreException e) {
					// do nothing
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

	private static void sendFile(String filename) throws UnknownHostException,
			IOException {
		String fname = filename;
		byte[] fbytes = new byte[1024], sbytes = new byte[1024];
		OutputStream snd = null;
		FileInputStream fis = null;
		Socket sock = null;
		InputStream rcv = null;
		try {
			sock = new Socket(ADDRESS, PORT);
			snd = sock.getOutputStream();
			snd.write("CONNECT".getBytes());
			 log("Sent: "+new String ("CONNECT".getBytes()));
			rcv = sock.getInputStream();
			rcv.read(sbytes);
			 log( "Received: "+new String (sbytes));
			snd.write(("files.zip").getBytes());
			 log("Sent: "+new String ("files.zip".getBytes()));
			int count;
			fis = new FileInputStream(fname);
			while ((count = fis.read(fbytes)) >= 0) {
				snd.write(fbytes, 0, count);
				 log("Sent: "+new String (fbytes));

			}
			log("COMPLETE");
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				snd.close();
				rcv.close();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private static void log(String msg) {
		plugin.log(msg, null);
	}

	private void log(String msg, Exception e) {
		getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, e));
	}

	public static void stopRemoteRecord(IProject project) {
		try {
			project.setSessionProperty(RECORD_KEY, null);
			project.setSessionProperty(LOCAL_KEY, null);
			project.setSessionProperty(REMOTE_KEY, null);
		} catch (CoreException e) {
			// do nothing
		}
	}

	public static void stopRecord(IProject project) {
		try {
			project.setSessionProperty(RECORD_KEY, null);
			project.setSessionProperty(LOCAL_KEY, null);
			project.setSessionProperty(REMOTE_KEY, null);
		} catch (CoreException e) {
			// do nothing
		}
	}

	public static IProject getSelectedProject(ExecutionEvent event) {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		Object element = selection.getFirstElement();
		if (element instanceof IProject) {
			return (IProject) element;
		}
		if (!(element instanceof IAdaptable)) {
			return null;
		}
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IProject.class);
		return (IProject) adapter;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void earlyStartup() {
	}

}

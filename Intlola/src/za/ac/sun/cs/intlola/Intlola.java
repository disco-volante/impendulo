package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import za.ac.sun.cs.intlola.file.FileUtils;
import za.ac.sun.cs.intlola.gui.ModeDialog;
import za.ac.sun.cs.intlola.gui.ProjectDialog;
import za.ac.sun.cs.intlola.gui.LoginDialog;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;
import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;
import za.ac.sun.cs.intlola.processing.Processor;

public class Intlola extends AbstractUIPlugin implements IStartup {
	public static final int LAUNCHED = -6666;

	private static Intlola plugin;
	protected static final String PLUGIN_ID = "za.ac.sun.cs.intlola";
	private static final QualifiedName RECORD_KEY = new QualifiedName(
			"intlola", "record");

	private static final Boolean RECORD_ON = new Boolean(true);

	public static Intlola getActive() {
		return Intlola.plugin;
	}

	private static String getLogMessage(final Exception e, final Object[] msgs) {
		String logMsg = "";
		for (Object msg : msgs) {
			if (msg == null) {
				msg = "NULL";
			}
			logMsg += " " + msg.toString();
		}
		if (e != null) {
			logMsg += e.getMessage();
		}
		return logMsg;
	}

	public static boolean getRecordStatus(final IProject project) {
		try {
			final Boolean record = (Boolean) project
					.getSessionProperty(RECORD_KEY);
			return record == RECORD_ON;
		} catch (final CoreException e) {
			Intlola.log(e);
			return false;
		}
	}

	public static void log(final Exception e, final Object... msgs) {
		if (getActive() != null) {
			getActive().getLog().log(
					new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK,
							getLogMessage(e, msgs), e));
		} else {
			System.out.println(getLogMessage(e, msgs));
		}
	}

	public static void startRecord(final IProject project, final Shell shell) {
		Intlola.log(null, "Intlola record started", project.getName());
		getActive().setProcessor();
		if (getActive().setup(shell)) {
			try {
				project.setSessionProperty(RECORD_KEY, RECORD_ON);
				PluginUtils.touchAll(project);
			} catch (final CoreException e) {
				Intlola.log(e);
			}
		}
	}

	public static void stopRecord(final IProject project, final Shell shell) {
		getActive().stop(shell);
		try {
			project.setSessionProperty(Intlola.RECORD_KEY, null);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		Intlola.log(null, "Intlola record stopping", project.getName());
	}

	private IResourceChangeListener changeListener = null;

	private boolean listenersAdded = false;

	private Processor proc;

	public Intlola() {
		Intlola.plugin = this;
	}

	@Override
	public void earlyStartup() {
	}

	public Processor getProcessor() {
		return proc;
	}

	private void setProcessor() {
		final IntlolaMode mode = IntlolaMode.getMode(getPreferenceStore()
				.getString(PreferenceConstants.P_MODE));
		final String uname = getPreferenceStore().getString(
				PreferenceConstants.P_UNAME);
		final String address = getPreferenceStore().getString(
				PreferenceConstants.P_ADDRESS);
		final int port = getPreferenceStore()
				.getInt(PreferenceConstants.P_PORT);
		proc = new Processor(uname, mode, address, port);
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		if (!listenersAdded) {
			changeListener = new IntlolaListener();
			PluginUtils.getWorkspace().addResourceChangeListener(
					changeListener, IResourceChangeEvent.POST_CHANGE);
			listenersAdded = true;
		}
	}

	private boolean setup(Shell shell) {
		proc.setMode(chooseMode(shell));
		return (proc.getMode().equals(IntlolaMode.ARCHIVE_LOCAL))
				|| (proc.getMode().isRemote() && login(shell) && createSubmission(shell));
	}

	private IntlolaMode chooseMode(Shell shell) {
		final ModeDialog dialog = new ModeDialog(shell, proc.getMode());
		final int code = dialog.open();
		if (code == Window.OK) {
			return dialog.getMode();
		} else {
			return IntlolaMode.NONE;
		}
	}

	private boolean login(final Shell shell) {
		final LoginDialog dialog = new LoginDialog(shell, "Intlola login",
				proc.getUsername(), proc.getAddress(), proc.getPort());
		IntlolaError err = IntlolaError.DEFAULT;
		while (!err.equals(IntlolaError.SUCCESS)) {
			final int code = dialog.open(err);
			if (code == Window.OK) {
				err = proc.login(dialog.getUserName(), dialog.getPassword(),
						dialog.getAddress(), dialog.getPort());
			} else {
				break;
			}
		}
		return err.equals(IntlolaError.SUCCESS);
	}

	private boolean createSubmission(final Shell shell) {
		IntlolaError err = IntlolaError.DEFAULT;
		try {
			proc.loadProjects();
			ProjectDialog projDlg = new ProjectDialog(shell, "Choose project",
					proc.getProjects());
			final int code = projDlg.open();
			if (code == Window.OK) {
				err = proc.createSubmission(projDlg.getProject());
			} else {
				err = IntlolaError.CORE.specific("User exited.");
			}
		} catch (IOException e) {
			err = IntlolaError.SOCKET
					.specific("IO error encontered while loading projects: "
							+ e.getMessage());
			e.printStackTrace();
		}
		if (err.equals(IntlolaError.SUCCESS)) {
			return true;
		} else {
			MessageDialog
					.openError(shell, err.toString(), err.getDescription());
			return false;
		}
	}

	private void stop(Shell shell) {
		if (proc.getMode().isArchive()) {
			String zipName = proc.getMode().isRemote() ? zipName = getStorePath()
					+ File.separator + "intlola.zip"
					: FileUtils.getFilename(shell);
			proc.handleArchive(getActive().getStorePath(), zipName);
		} else if (proc.getMode().isRemote()) {
			proc.logout();
		}
	}

	public String getStorePath() {
		return getStateLocation().toOSString();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		Intlola.plugin = null;
		super.stop(context);
	}

}

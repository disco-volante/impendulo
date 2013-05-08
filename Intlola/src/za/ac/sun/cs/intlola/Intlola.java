package za.ac.sun.cs.intlola;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import za.ac.sun.cs.intlola.gui.LoginDialog;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;
import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;
import za.ac.sun.cs.intlola.processing.Processor;

public class Intlola extends AbstractUIPlugin implements IStartup {
	public static String				STORE_PATH;

	public static final int				LAUNCHED	= -6666;
	protected static final String		PLUGIN_ID	= "za.ac.sun.cs.goanna";
	private static final QualifiedName	RECORD_KEY	= new QualifiedName(
															"intlola", "record");

	private static final Boolean		RECORD_ON	= new Boolean(true);

	private static Intlola				plugin;

	private Processor					proc;

	public static Intlola getActive() {
		return Intlola.plugin;
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

	private static String getLogMessage(Exception e, Object[] msgs) {
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

	public static void startRecord(final IProject project, final Shell shell) {
		getActive().setProcessor(project.getName());
		if (getActive().start(shell)) {
			try {
				project.setSessionProperty(RECORD_KEY,
						RECORD_ON);
			} catch (CoreException e) {
				Intlola.log(e);
			}
		}
	}

	private boolean start(final Shell shell) {
		LoginDialog dialog = new LoginDialog(shell,
				"Intlola login configuration", proc.getUsername(),
				proc.getProject(), proc.getMode(), proc.getAddress(),
				proc.getPort());
		IntlolaError err = IntlolaError.DEFAULT;
		while (!err.equals(IntlolaError.SUCCESS)) {
			final int code = dialog.open(err);
			if (code == Window.OK) {
				err = proc.login(dialog.getUserName(), dialog.getPassword(),
						dialog.getProject(), dialog.getMode(),
						dialog.getAddress(), dialog.getPort());
			} else {
				break;
			}
		}
		return err.equals(IntlolaError.SUCCESS);
	}

	public static void stopRecord(final IProject project, final Shell shell) {
		Intlola.log(null, "Intlola record stopping", project.getName());
		getActive().stop();
		try {
			project.setSessionProperty(Intlola.RECORD_KEY, null);
		} catch (final CoreException e) {
			e.printStackTrace();
		}

	}

	private void stop() {
		if (proc.getMode().isArchive()) {
			proc.handleArchive(STORE_PATH, STORE_PATH);
		} else if (proc.getMode().isRemote()) {
			proc.logout();
		}
	}

	private void setProcessor(final String project) {
		final IntlolaMode mode = IntlolaMode.getMode(getPreferenceStore()
				.getString(PreferenceConstants.P_MODE));
		final String uname = getPreferenceStore().getString(
				PreferenceConstants.P_UNAME);
		final String address = getPreferenceStore().getString(
				PreferenceConstants.P_ADDRESS);
		final int port = getPreferenceStore()
				.getInt(PreferenceConstants.P_PORT);
		proc = new Processor(uname, project, mode, address, port);
	}

	private IResourceChangeListener	changeListener	= null;

	private boolean					listenersAdded	= false;

	public Intlola() {
		Intlola.plugin = this;
		STORE_PATH = Intlola.getActive().getStateLocation().toOSString();
	}

	public void earlyStartup() {
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

	@Override
	public void stop(final BundleContext context) throws Exception {
		Intlola.plugin = null;
		super.stop(context);
	}

	public Processor getProcessor() {
		return proc;
	}
}

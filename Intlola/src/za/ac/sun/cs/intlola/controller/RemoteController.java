package za.ac.sun.cs.intlola.controller;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import za.ac.sun.cs.intlola.gui.LoginDialog;
import za.ac.sun.cs.intlola.gui.SubmissionDialog;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;
import za.ac.sun.cs.intlola.processing.Processor;
import za.ac.sun.cs.intlola.processing.ProcessorFactory;
import za.ac.sun.cs.intlola.processing.RemoteProcessor;
import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.util.IntlolaError;
import za.ac.sun.cs.intlola.util.InvalidModeException;

public abstract class RemoteController extends Controller {

	protected RemoteProcessor proc;

	public RemoteController(IPreferenceStore store) {
		super(store);
	}

	public Processor getProcessor() {
		return proc;
	}

	protected void createProcessor(IPaths paths) throws IOException,
			InvalidModeException {
		proc = ProcessorFactory.remote(getMode(), paths);
	}

	protected void openConnection(Shell shell) throws LoginException {
		if (!login(shell)) {
			throw new LoginException("Could not login to server.");
		} else {
			store.setValue(PreferenceConstants.P_ADDRESS, proc.getAddress());
			store.setValue(PreferenceConstants.P_MODE, proc.getMode()
					.toString());
			store.setValue(PreferenceConstants.P_PORT, proc.getPort());
			store.setValue(PreferenceConstants.P_UNAME, proc.getUsername());
		}
		if (!startSubmission(shell)) {
			throw new LoginException("Could not start submission.");
		}
	}

	/**
	 * login requests the user's login details and logs them into Impendulo.
	 * 
	 * @param shell
	 * @return
	 */
	public boolean login(final Shell shell) {
		final String uname = store.getString(PreferenceConstants.P_UNAME);
		final String address = store.getString(PreferenceConstants.P_ADDRESS);
		final int port = store.getInt(PreferenceConstants.P_PORT);
		final LoginDialog dialog = new LoginDialog(shell, "Intlola login",
				uname, address, port);
		IntlolaError err = IntlolaError.DEFAULT;
		while (!err.equals(IntlolaError.SUCCESS)) {
			final int code = dialog.open(err);
			if (code == Window.OK) {
				err = proc.login(dialog.getRequest(), dialog.getUserName(),
						dialog.getPassword(), dialog.getAddress(),
						dialog.getPort());
			} else {
				break;
			}
		}
		return err.equals(IntlolaError.SUCCESS);
	}

	/**
	 * StartSubmission allows the user to create a new submission or to continue
	 * with an old submission.
	 * 
	 * @param shell
	 * @return
	 */
	public boolean startSubmission(final Shell shell) {
		IntlolaError err = IntlolaError.DEFAULT;
		SubmissionDialog subDlg = new SubmissionDialog(shell,
				proc.getProjects());
		final int code = subDlg.open();
		if (code == Window.OK) {
			if (subDlg.isCreate()) {
				err = proc.createSubmission(subDlg.getProject(),
						subDlg.getAssignment());
			} else {
				err = proc.continueSubmission(subDlg.getProject(),
						subDlg.getSubmission());
			}
		} else {
			err = IntlolaError.USER;
		}
		if (err.equals(IntlolaError.SUCCESS)) {
			return true;
		} else {
			if (!err.equals(IntlolaError.USER)) {
				MessageDialog.openError(shell, err.toString(),
						err.getDescription());
			}
			return false;
		}
	}

}

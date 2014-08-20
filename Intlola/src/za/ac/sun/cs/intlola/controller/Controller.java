package za.ac.sun.cs.intlola.controller;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import za.ac.sun.cs.intlola.gui.ModeDialog;
import za.ac.sun.cs.intlola.processing.Processor;
import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.util.IntlolaMode;
import za.ac.sun.cs.intlola.util.InvalidModeException;

public abstract class Controller {
	protected IPreferenceStore store;

	public Controller(IPreferenceStore store) {
		this.store = store;
	}

	/**
	 * chooseMode allows the user to choose the mode to run Intlola in. The user
	 * can choose to send each snapshot as it is recording, to send all
	 * snapshots at the end of the session or to store the snapshots locally.
	 * 
	 * @param shell
	 * @return
	 */
	public static IntlolaMode chooseMode(Shell shell, IntlolaMode defualt) {
		final ModeDialog dialog = new ModeDialog(shell, defualt);
		final int code = dialog.open();
		if (code == Window.OK) {
			return dialog.getMode();
		} else {
			return IntlolaMode.NONE;
		}
	}

	/**
	 * login requests the user's login details and logs them into Impendulo.
	 * 
	 * @param shell
	 * @return
	 * @throws IOException
	 * @throws InvalidModeException
	 */
	public abstract boolean start(final Shell shell, final IPaths paths)
			throws IOException, InvalidModeException;

	/**
	 * end ends a submission. It logs the user out of Impendulo.
	 * 
	 * @param shell
	 */
	public abstract boolean end(Shell shell);

	public abstract IntlolaMode getMode();

	public abstract Processor getProcessor();

}

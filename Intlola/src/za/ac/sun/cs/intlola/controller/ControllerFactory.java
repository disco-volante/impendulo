package za.ac.sun.cs.intlola.controller;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;
import za.ac.sun.cs.intlola.util.IntlolaMode;
import za.ac.sun.cs.intlola.util.InvalidModeException;

public class ControllerFactory {
	public static Controller create(Shell shell, IPreferenceStore store) throws InvalidModeException {
		IntlolaMode mode = Controller.chooseMode(shell, IntlolaMode
				.getMode(store.getString(PreferenceConstants.P_MODE)));
		switch (mode) {
		case FILE_REMOTE:
			return new FileController(store);
		case ARCHIVE_REMOTE:
			return new ArchiveController(store);
		case ARCHIVE_LOCAL:
			return new LocalController(store);
		default:
			throw new InvalidModeException(mode);
		}
	}

}

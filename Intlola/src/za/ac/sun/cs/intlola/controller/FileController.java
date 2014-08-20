package za.ac.sun.cs.intlola.controller;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.util.IntlolaMode;
import za.ac.sun.cs.intlola.util.InvalidModeException;

public class FileController extends RemoteController {

	public FileController(IPreferenceStore store) {
		super(store);
	}

	@Override
	public boolean start(Shell shell, IPaths paths) throws IOException,
			InvalidModeException {
		createProcessor(paths);
		try {
			openConnection(shell);
		} catch (LoginException le) {
			Intlola.log(le);
			return false;
		}
		return true;
	}

	@Override
	public boolean end(Shell shell) {
		proc.stop();
		return true;
	}

	@Override
	public IntlolaMode getMode() {
		return IntlolaMode.FILE_REMOTE;
	}

}

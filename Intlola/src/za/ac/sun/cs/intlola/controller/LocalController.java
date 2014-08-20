package za.ac.sun.cs.intlola.controller;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;

import za.ac.sun.cs.intlola.processing.LocalProcessor;
import za.ac.sun.cs.intlola.processing.Processor;
import za.ac.sun.cs.intlola.processing.ProcessorFactory;
import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.util.IO;
import za.ac.sun.cs.intlola.util.IntlolaMode;

public class LocalController extends Controller {

	private LocalProcessor proc;

	public LocalController(IPreferenceStore store) {
		super(store);
	}

	@Override
	public boolean start(Shell shell, IPaths paths) throws IOException {
		proc = ProcessorFactory.local(paths);
		return true;
	}

	@Override
	public boolean end(Shell shell) {
		proc.saveArchive(IO.getFilename(shell));
		proc.stop();
		return true;
	}

	@Override
	public IntlolaMode getMode() {
		return IntlolaMode.ARCHIVE_LOCAL;
	}

	@Override
	public Processor getProcessor() {
		return proc;
	}

}

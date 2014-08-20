package za.ac.sun.cs.intlola.processing;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.processing.json.Ignore;
import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.util.IO;
import za.ac.sun.cs.intlola.util.IntlolaMode;

public abstract class Processor {
	protected Ignore ignore;
	protected final ExecutorService executor;
	protected IPaths paths;

	public Processor(IPaths paths) throws IOException {
		this.paths = paths;
		this.ignore = Ignore.create(paths.ignorePath());
		this.ignore.build(paths.projectPath());
		this.executor = Executors.newFixedThreadPool(1);
	}

	public abstract IntlolaMode getMode();

	public abstract void stop();

	/**
	 * processChanges processes a new snapshot and then either sends it or
	 * stores it if it is valid.
	 * 
	 * @param path
	 * @param sendContents
	 * @param kind
	 * @throws IOException
	 */
	public void processChanges(final String path, final int kind)
			throws IOException {
		char kindSuffix = IO.getKind(kind);
		if (!IO.shouldSend(kindSuffix, path)) {
			return;
		}
		String tipe = Const.LAUNCH;
		boolean isSrc = IO.isSrc(path);
		if (isSrc) {
			if (ignore.contains(path)) {
				return;
			}
			tipe = Const.SRC;
		}
		processFile(path, kindSuffix, tipe);
	}

	public abstract void processFile(String path, char kindSuffix, String tipe)
			throws IOException;
}

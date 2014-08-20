package za.ac.sun.cs.intlola.processing;

import java.io.IOException;

import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.util.IntlolaMode;
import za.ac.sun.cs.intlola.util.InvalidModeException;

public class ProcessorFactory {

	public static Processor create(IntlolaMode mode, IPaths paths)
			throws InvalidModeException, IOException {
		switch (mode) {
		case FILE_REMOTE:
			return new FileProcessor(paths);
		case ARCHIVE_REMOTE:
			return new ArchiveProcessor(paths);
		case ARCHIVE_LOCAL:
			return new LocalProcessor(paths);
		default:
			throw new InvalidModeException(mode);
		}
	}

	public static RemoteProcessor remote(IntlolaMode mode,
			IPaths paths) throws IOException, InvalidModeException {
		switch (mode) {
		case FILE_REMOTE:
			return new FileProcessor(paths);
		case ARCHIVE_REMOTE:
			return new ArchiveProcessor(paths);
		default:
			throw new InvalidModeException(mode);
		}
	}

	public static LocalProcessor local(IPaths paths)
			throws IOException {
		return new LocalProcessor(paths);
	}

}

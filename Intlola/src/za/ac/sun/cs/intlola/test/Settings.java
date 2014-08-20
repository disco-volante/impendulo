package za.ac.sun.cs.intlola.test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import za.ac.sun.cs.intlola.util.IO;

public class Settings {
	protected static final String PROJECT_LOCATION = "/home/godfried/dev/java/ImpenduloProjects/Triangle";
	protected static final int THREAD_COUNT = 10;
	protected static final int FILE_COUNT = 10;
	protected static final String FILE_NAME = "/home/godfried/dev/java/ImpenduloProjects/Triangle/src/triangle/Triangle.java";
	protected static final long SLEEP_DURATION = 10;
	protected static final String PROJECT_NAME = "Triangle";
	protected static final String USER_NAME = "pjordaan";
	protected static final String PASSWORD = "1brandwag";
	protected static final String ADDRESS = "localhost";
	protected static final int PORT = 8010;

	protected static String archiveName() throws IOException {
		String dirName = IO.joinPath(System.getProperty("java.io.tmpdir"),
				"intlola_test");
		File d = new File(dirName);
		if (!d.exists() && !d.mkdirs()) {
			throw new IOException(String.format(
					"Could not create directory %s", dirName));
		}
		return IO.joinPath(dirName, "archive" + UUID.randomUUID().toString()
				+ ".zip");
	}

}

package za.ac.sun.cs.intlola.processing.paths;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import za.ac.sun.cs.intlola.util.IO;

public class TestPaths implements IPaths {

	private final String ignorePath, projectPath, storePath, archivePath;

	public TestPaths(String projectLocation) throws IOException {
		this.ignorePath = IO.joinPath(projectLocation, IGNORE_NAME);
		this.projectPath = projectLocation;
		this.storePath = IO.joinPath(projectLocation, STORE_NAME, UUID
				.randomUUID().toString());
		this.archivePath = IO.joinPath(this.storePath, ARCHIVE_NAME);
		File dir = new File(archivePath);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Could not create plugin directory.");
		}
	}

	public String projectPath() {
		return projectPath;
	}

	public String ignorePath() {
		return ignorePath;
	}

	public String archivePath() {
		return archivePath;
	}

	public String storePath() {
		return storePath;
	}
}

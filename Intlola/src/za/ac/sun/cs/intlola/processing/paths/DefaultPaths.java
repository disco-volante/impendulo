package za.ac.sun.cs.intlola.processing.paths;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.eclipse.core.resources.IProject;

import za.ac.sun.cs.intlola.util.IO;

public class DefaultPaths implements IPaths{
	public static final String IGNORE_NAME = ".impendulo_info.json",
			STORE_NAME = ".intlola", ARCHIVE_NAME = "archive";
	private final String ignorePath, projectPath, storePath, archivePath;

	public DefaultPaths(IProject project) throws IOException {
		this.ignorePath = IO.joinPath(project.getLocation().toOSString(),
				IGNORE_NAME);
		this.projectPath = project.getLocation().toOSString();
		this.storePath = IO.joinPath(project.getLocation().toOSString(),
				STORE_NAME, UUID.randomUUID().toString());
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

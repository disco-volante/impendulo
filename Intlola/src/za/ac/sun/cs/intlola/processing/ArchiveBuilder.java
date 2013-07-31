package za.ac.sun.cs.intlola.processing;

import java.io.IOException;

import za.ac.sun.cs.intlola.file.FileUtils;

public class ArchiveBuilder implements Runnable {
	private final String fileLocation, savePath;

	public ArchiveBuilder(final String fileLocation, final String savePath) {
		this.fileLocation = fileLocation;
		this.savePath = savePath;
	}

	@Override
	public void run() {
		try {
			FileUtils.createZip(fileLocation, savePath);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}

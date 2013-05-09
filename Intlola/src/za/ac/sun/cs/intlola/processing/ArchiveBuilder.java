package za.ac.sun.cs.intlola.processing;

import za.ac.sun.cs.intlola.file.FileUtils;

public class ArchiveBuilder implements Runnable {
	private final String	fileLocation, savePath;

	public ArchiveBuilder(final String fileLocation, final String savePath) {
		this.fileLocation = fileLocation;
		this.savePath = savePath;
	}

	@Override
	public void run() {
		FileUtils.createZip(fileLocation, savePath);
	}

}

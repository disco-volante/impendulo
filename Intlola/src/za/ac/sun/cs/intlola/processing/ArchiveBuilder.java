package za.ac.sun.cs.intlola.processing;

import java.io.IOException;

public class ArchiveBuilder implements Runnable {
	private final String fileLocation, savePath;

	public ArchiveBuilder(final String fileLocation, final String savePath) {
		this.fileLocation = fileLocation;
		this.savePath = savePath;
	}

	@Override
	public void run() {
		try {
			IOUtils.createZip(fileLocation, savePath);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}

package za.ac.sun.cs.intlola.processing;

import za.ac.sun.cs.intlola.Utils;

public class ArchiveBuilder implements Runnable {
	private String fileLocation, savePath;
	public ArchiveBuilder(String fileLocation, String savePath){
		this.fileLocation = fileLocation;
		this.savePath = savePath;
	}
	@Override
	public void run() {
		Utils.createZip(fileLocation, savePath);
	}

}

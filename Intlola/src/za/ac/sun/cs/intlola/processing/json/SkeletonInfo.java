package za.ac.sun.cs.intlola.processing.json;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.processing.IOUtils;

public class SkeletonInfo {
	public String name = "";
	public FileInfo[] tests, files;
	public DataInfo[] data;
	public Map<String, String> sendPaths;

	public void buildSendPaths(String base) throws IOException {
		sendPaths = new HashMap<String, String>();
		for (FileInfo t : tests) {
			addPath(base, t, Const.TEST);
		}
		for (FileInfo f : files) {
			addPath(base, f, Const.SRC);
		}
	}

	private void addPath(final String base, FileInfo fi, String tipe) throws IOException {
		if (fi.send) {
			String dir = IOUtils.joinPath(fi.folder, fi.pkg.split("\\."));
			String path = IOUtils.joinPath(base, dir, fi.name);
			if (!new File(path).exists()) {
				throw new IOException(
						String.format(
								"Could not locate file %s specified in project skeleton info file.",
								path));
			}
			sendPaths.put(path, tipe);
		}
	}
}

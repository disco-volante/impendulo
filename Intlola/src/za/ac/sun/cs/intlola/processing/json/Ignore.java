package za.ac.sun.cs.intlola.processing.json;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import za.ac.sun.cs.intlola.processing.IOUtils;

public class Ignore {
	public String name = "";
	public PathInfo[] paths;
	private Set<String> ignore;

	public boolean contains(String path) {
		return ignore != null && ignore.contains(path);
	}

	public void build(String base) throws IOException {
		ignore = new HashSet<String>();
		if (paths == null) {
			return;
		}
		for (PathInfo p : paths) {
			addPath(base, p);
		}
	}

	private void addPath(final String base, PathInfo fi) throws IOException {
		String dir = IOUtils.joinPath(fi.folder, fi.pkg.split("\\."));
		String path = IOUtils.joinPath(base, dir, fi.name);
		if (!new File(path).exists()) {
			throw new IOException(
					String.format(
							"Could not locate path %s specified in project skeleton info file.",
							path));
		}
		ignore.add(path);
	}
}

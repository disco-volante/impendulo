package za.ac.sun.cs.intlola.processing.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

import za.ac.sun.cs.intlola.util.IO;

public class Ignore {
	public String name = "";
	public String[] paths;
	private Set<String> ignore;

	public static Ignore create(String filename) throws IOException {
		File f = new File(filename);
		if (!f.exists() || f.isDirectory()) {
			return new Ignore();
		}
		InputStream fin = new FileInputStream(f);
		final String data = IO.read(fin);
		Gson gson = new Gson();
		Ignore si = gson.fromJson(data, new Ignore().getClass());
		return si;
	}

	public boolean contains(String path) {
		return ignore != null && ignore.contains(path);
	}

	public void build(String base) throws IOException {
		ignore = new HashSet<String>();
		if (paths == null) {
			return;
		}
		for (String p : paths) {
			addPath(base, p);
		}
	}

	private void addPath(final String base, String p) throws IOException {
		String path = IO.joinPath(base, p);
		if (!new File(path).exists()) {
			throw new IOException(
					String.format(
							"Could not locate path %s specified in project skeleton info file.",
							path));
		}
		ignore.add(path);
	}
}

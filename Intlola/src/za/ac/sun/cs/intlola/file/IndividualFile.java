package za.ac.sun.cs.intlola.file;

import java.io.File;
import java.util.Calendar;

import com.google.gson.JsonObject;

public class IndividualFile implements IntlolaFile {
	public static String getPackage(final String[] holder, final int len) {
		String pkg = "";
		boolean start = false;
		for (int i = 0; i < len; i++) {
			if (holder[i].equals("src")) {
				start = true;
			}
			if (start) {
				pkg += holder[i];
				if (i < len) {
					pkg += ".";
				}
			}
		}
		return pkg;
	}

	public static IntlolaFile read(final String path) {
		String[] elems = path.split(File.separator);
		final String fdata = elems[elems.length - 1];
		elems = fdata.split("_");
		final char mod = elems[elems.length - 1].charAt(0);
		final int num = Integer.parseInt(elems[elems.length - 2]);
		final long time = Long.parseLong(elems[elems.length - 3]);
		final String name = elems[elems.length - 4];
		final String pkg = IndividualFile.getPackage(elems, elems.length - 4);
		return new IndividualFile(path, name, pkg, mod, num, time);
	}

	private final String path, name, pkg;
	private final char mod;
	private final int num;

	private final long time;

	private final boolean hasContents;

	public IndividualFile(final String path, final char mod, final int num,
			final boolean hasContents) {
		this.path = path;
		this.mod = mod;
		this.num = num;
		this.hasContents = hasContents;
		time = Calendar.getInstance().getTimeInMillis();
		final String[] spd = path.split(File.separator);
		name = spd[spd.length - 1];
		pkg = getPackage(spd, spd.length - 1);
	}

	public IndividualFile(final String path, final String name,
			final String pkg, final char mod, final int num, final long time) {
		this.path = path;
		this.mod = mod;
		this.num = num;
		hasContents = true;
		this.time = time;
		this.name = name;
		this.pkg = pkg;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean hasContents() {
		return hasContents;
	}

	@Override
	public JsonObject toJSON() {
		final JsonObject ret = new JsonObject();
		ret.addProperty("type", "file");
		ret.addProperty("name", name);
		if (hasContents()) {
			String ext = name.split(".")[1];
			ret.addProperty("ftype", ext);
		} else {
			ret.addProperty("ftype", "mod");
		}
		ret.addProperty("pkg", pkg);
		ret.addProperty("mod", mod);
		ret.addProperty("num", num);
		ret.addProperty("time", time);
		return ret;
	}
}

package za.ac.sun.cs.intlola.file;

import java.io.File;
import java.util.Calendar;

import za.ac.sun.cs.intlola.processing.IOUtils;

import com.google.gson.JsonObject;

public class IndividualFile implements IntlolaFile {

	private final boolean sendContents;
	private final char mod;

	private final String path, name, pkg;

	private final long time;

	public IndividualFile(final String path, final char mod,
			final boolean sendContents) {
		this.path = path;
		this.mod = mod;
		this.sendContents = sendContents;
		time = Calendar.getInstance().getTimeInMillis();
		final String[] spd = path.split(File.separator);
		name = spd[spd.length - 1];
		pkg = IOUtils.getPackage(spd, IOUtils.NAME_SEP);
	}

	public IndividualFile(final String path, final String name,
			final String pkg, final long time, final char mod,
			final boolean sendContents) {
		this.path = path;
		this.mod = mod;
		this.sendContents = sendContents;
		this.time = time;
		this.name = name;
		this.pkg = pkg;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean sendContents() {
		return sendContents;
	}

	@Override
	public JsonObject toJSON() {
		final JsonObject ret = new JsonObject();
		if (name.endsWith(Const.JAVA)) {
			ret.addProperty(Const.TYPE, Const.SRC);
		} else {
			ret.addProperty(Const.TYPE, Const.LAUNCH);
		}
		ret.addProperty(Const.NAME, name);
		ret.addProperty(Const.PKG, pkg);
		ret.addProperty(Const.TIME, time);
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (sendContents ? 1231 : 1237);
		result = prime * result + mod;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndividualFile other = (IndividualFile) obj;
		if (sendContents != other.sendContents)
			return false;
		if (mod != other.mod)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (pkg == null) {
			if (other.pkg != null)
				return false;
		} else if (!pkg.equals(other.pkg))
			return false;
		if (time != other.time)
			return false;
		return true;
	}

}

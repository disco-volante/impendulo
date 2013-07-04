package za.ac.sun.cs.intlola.file;

import java.io.File;
import java.util.Calendar;

import com.google.gson.JsonObject;

public class IndividualFile implements IntlolaFile {

	private final boolean	hasContents;
	private final char		mod;
	private final int		num;

	private final String	path, name, pkg;

	private final long		time;

	public IndividualFile(final String path, final char mod, final int num,
			final boolean hasContents) {
		this.path = path;
		this.mod = mod;
		this.num = num;
		this.hasContents = hasContents;
		time = Calendar.getInstance().getTimeInMillis();
		final String[] spd = path.split(File.separator);
		name = spd[spd.length - 1];
		pkg = FileUtils.getPackage(spd, FileUtils.NAME_SEP);
	}

	public IndividualFile(final String path, final String name,
			final String pkg, final long time, final int num, final char mod,
			final boolean hasContents) {
		this.path = path;
		this.mod = mod;
		this.num = num;
		this.hasContents = hasContents;
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
		ret.addProperty(Const.NAME, name);
		if (hasContents()) {
			final String ext = name.split("\\.")[1];
			ret.addProperty(Const.FTYPE, ext);
			if (ext.equals(Const.JAVA)) {
				ret.addProperty(Const.TYPE, Const.SRC);
			} else if (ext.equals(Const.CLASS)) {
				ret.addProperty(Const.TYPE, Const.EXEC);
			}
		} else {
			ret.addProperty(Const.FTYPE, Const.EMPTY);
			ret.addProperty(Const.TYPE, Const.CHANGE);
		}
		ret.addProperty(Const.PKG, pkg);
		ret.addProperty(Const.MOD, mod);
		ret.addProperty(Const.NUM, num);
		ret.addProperty(Const.TIME, time);
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasContents ? 1231 : 1237);
		result = prime * result + mod;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + num;
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
		if (hasContents != other.hasContents)
			return false;
		if (mod != other.mod)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (num != other.num)
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

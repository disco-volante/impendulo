package za.ac.sun.cs.intlola.file;

import com.google.gson.JsonObject;

public class ArchiveFile implements IntlolaFile {
	private final String	path;

	public ArchiveFile(final String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean hasContents() {
		return true;
	}

	@Override
	public JsonObject toJSON() {
		final JsonObject ret = new JsonObject();
		ret.addProperty(Const.TYPE, Const.ARCHIVE);
		ret.addProperty(Const.FTYPE, Const.ZIP);
		return ret;
	}

}

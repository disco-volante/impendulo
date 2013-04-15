package za.ac.sun.cs.intlola.file;

import java.io.File;
import java.util.Calendar;

import com.google.gson.JsonObject;

public class TestFile implements IntlolaFile {
	private final String path;
	private final String name;
	private final long time;

	public TestFile(final String path) {
		this.path = path;
		final String[] spd = path.split(File.separator);
		name = spd[spd.length - 1];
		time = Calendar.getInstance().getTimeInMillis();
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
		ret.addProperty("type", "test");
		ret.addProperty("name", name);
		ret.addProperty("ftype", "zip");
		ret.addProperty("time", time);
		return ret;
	}

}

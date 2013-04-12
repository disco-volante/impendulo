package za.ac.sun.cs.intlola;

import java.io.File;
import java.util.Calendar;

import com.google.gson.JsonObject;

public class SingleFile implements IntlolaFile {
	private String path, name, pkg;
	private char mod;
	private int num;
	private long time;

	public SingleFile(String path, char mod, int num) {
		this.path = path;
		this.mod = mod;
		this.num = num;
		this.time = Calendar.getInstance().getTimeInMillis();
		String[] spd = path.split(File.separator);
		this.name = spd[spd.length - 1];
		this.pkg = "";
		boolean start = false;
		for (int i = 0; i < spd.length - 1; i++) {
			if (spd[i].equals("src")) {
				start = true;
			}
			if (start) {
				this.pkg += spd[i];
				if (i < spd.length - 1) {
					this.pkg += ".";
				}
			}
		}
	}

	public JsonObject toJSON() {
		JsonObject ret = new JsonObject();
		ret.addProperty("type", "file");
		ret.addProperty("name", name);
		ret.addProperty("ftype", "java");
		ret.addProperty("pkg", pkg);
		ret.addProperty("mod", mod);
		ret.addProperty("num", num);
		ret.addProperty("time", time);
		return ret;
	}

	public String getPath() {
		return path;
	}
}

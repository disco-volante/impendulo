package za.ac.sun.cs.intlola.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TestFile implements IntlolaFile {
	private final String path, project;
	private final String[] names;

	public TestFile(final String path, final String project,
			final String[] names) {
		this.path = path;
		this.project = project;
		this.names = names;
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
		ret.addProperty(Const.PROJECT, project);
		ret.addProperty(Const.LANG, Const.JAVA);
		JsonArray jsonNames = new JsonArray();
		for(String name : names){
			jsonNames.add(new JsonPrimitive(name));
		}
		ret.add(Const.NAMES, jsonNames);
		return ret;
	}

}

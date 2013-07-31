package za.ac.sun.cs.intlola.file;

import com.google.gson.JsonObject;

public interface IntlolaFile {

	String getPath();

	boolean sendContents();

	JsonObject toJSON();

}

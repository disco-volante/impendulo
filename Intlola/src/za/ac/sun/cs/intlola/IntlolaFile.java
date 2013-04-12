package za.ac.sun.cs.intlola;

import com.google.gson.JsonObject;

public interface IntlolaFile {

	JsonObject toJSON();

	String getPath();

}

package fmv.db;

import java.util.ArrayList;
import java.util.HashMap;

import fmv.ProjectData;

public class Project {
	public String name;
	public Long[] dates;
	public String[] users;
	public HashMap<Object, ArrayList<String>> tokens;
	public HashMap<String, ProjectData> tokenData;
	public boolean loaded;

	public Project(String name) {
		this.name = name;
		tokenData = new HashMap<String, ProjectData>();
		tokens = new HashMap<Object, ArrayList<String>>();
		loaded = false;
	}

	public String[] getTokens(Object param) {
		ArrayList<String> temp = tokens.get(param);
		return temp.toArray(new String[temp.size()]);
	}

	
}

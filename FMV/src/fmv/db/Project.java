package fmv.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fmv.Archive;

public class Project {
	public final String name;
	public String[] users;
	public Map<String, ArrayList<Submission>> submissions;
	public Map<Submission, Archive> submissionData;
	public boolean loaded;

	public Project(String name) {
		this.name = name;
		submissions = new HashMap<String, ArrayList<Submission>>();
		submissionData = new HashMap<Submission, Archive>();
		loaded = false;
	}

	public Submission[] getSubmissions(String user) {
		ArrayList<Submission> temp = submissions.get(user);
		return temp.toArray(new Submission[temp.size()]);
	}

	
}

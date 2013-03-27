package fmv.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
	private final String name;
	private String[] users;
	private Map<String, ArrayList<Submission>> submissions;
	private boolean loaded;

	public Project(final String name) {
		this.name = name;
		submissions = new HashMap<String, ArrayList<Submission>>();
		loaded = false;
	}

	public Submission[] getSubmissions(final String user) {
		final ArrayList<Submission> temp = submissions.get(user);
		return temp.toArray(new Submission[temp.size()]);
	}

	public String[] getUsers() {
		return users;
	}

	public String getName() {
		return name;
	}

	public Submission getSubmission(String user, int index) {
		return submissions.get(user).get(index);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void addUsers(List<String> projectUsers) {
		users = projectUsers.toArray(new String[projectUsers.size()]);
	}

	public void addSubmissions(Map<String, ArrayList<Submission>> subs) {
		submissions.putAll(subs);
	}

	public void setLoaded(boolean b) {
		loaded = true;
	}

}

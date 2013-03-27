package fmv.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
	private final String name;
	private String[] users;
	private final Map<String, ArrayList<Submission>> submissions;
	private boolean loaded;

	public Project(final String name) {
		this.name = name;
		submissions = new HashMap<String, ArrayList<Submission>>();
		loaded = false;
	}

	public void addSubmissions(final Map<String, ArrayList<Submission>> subs) {
		submissions.putAll(subs);
	}

	public void addUsers(final List<String> projectUsers) {
		users = projectUsers.toArray(new String[projectUsers.size()]);
	}

	public String getName() {
		return name;
	}

	public Submission getSubmission(final String user, final int index) {
		return submissions.get(user).get(index);
	}

	public Submission[] getSubmissions(final String user) {
		final ArrayList<Submission> temp = submissions.get(user);
		return temp.toArray(new Submission[temp.size()]);
	}

	public String[] getUsers() {
		return users;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(final boolean b) {
		loaded = true;
	}

}

package fmv.db;

import java.util.ArrayList;

public class Project {
	private final String name;
	private final ArrayList<Submission> submissions;
	private boolean loaded;

	public Project(final String name) {
		this.name = name;
		submissions = new ArrayList<Submission>();
		loaded = false;
	}

	public void addSubmissions(final ArrayList<Submission> subs) {
		submissions.addAll(subs);
	}

	public String getName() {
		return name;
	}

	public Submission getSubmission(final int index) {
		return submissions.get(index);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(final boolean b) {
		loaded = true;
	}

	public Object[] getSubmissions() {
		return submissions.toArray();
	}

}

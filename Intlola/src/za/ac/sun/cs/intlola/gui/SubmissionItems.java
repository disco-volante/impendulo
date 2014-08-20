package za.ac.sun.cs.intlola.gui;

import java.util.HashMap;

import za.ac.sun.cs.intlola.processing.json.Submission;

public class SubmissionItems {
	private String[] subStrings;
	private HashMap<String, Submission> subMap;
	private String submissionName;

	public SubmissionItems(Submission[] subs) {
		subStrings = new String[subs.length];
		subMap = new HashMap<String, Submission>((int) (subs.length * 1.4));
		for (int j = 0; j < subs.length; j++) {
			subStrings[j] = subs[j].toString();
			subMap.put(subStrings[j], subs[j]);
		}

	}

	public String[] getNames() {
		return subStrings;
	}

	public void setSubmission(String name) {
		this.submissionName = name;
	}

	public Submission getSubmission() {
		return subMap.get(submissionName);
	}
}

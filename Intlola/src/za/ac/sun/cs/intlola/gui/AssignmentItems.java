package za.ac.sun.cs.intlola.gui;

import java.util.HashMap;

import za.ac.sun.cs.intlola.processing.json.Assignment;
import za.ac.sun.cs.intlola.processing.json.AssignmentInfo;
import za.ac.sun.cs.intlola.processing.json.Submission;

public class AssignmentItems {
	private String[] assStrings;
	private HashMap<String, SubmissionItems> subItems;
	private HashMap<String, Assignment> assMap;
	private String assignmentName;

	public AssignmentItems(AssignmentInfo[] ais) {
		assStrings = new String[ais.length];
		subItems = new HashMap<String, SubmissionItems>(
				(int) (ais.length * 1.4));
		assMap = new HashMap<String, Assignment>((int) (ais.length * 1.4));

		for (int j = 0; j < ais.length; j++) {
			assStrings[j] = ais[j].getAssignment().toString();
			subItems.put(assStrings[j],
					new SubmissionItems(ais[j].getSubmissions()));
			assMap.put(assStrings[j], ais[j].getAssignment());
		}

	}

	public String[] getNames() {
		return assStrings;
	}

	public void setSubmission(String name) {
		if (assignmentName == null) {
			return;
		}
		this.subItems.get(assignmentName).setSubmission(name);
	}

	public String[] getSubmissionNames() {
		SubmissionItems items = subItems.get(assignmentName);
		if (items == null) {
			return new String[] {};
		}
		return items.getNames();
	}

	public Submission getSubmission() {
		if (assignmentName == null) {
			return null;
		}
		return subItems.get(assignmentName).getSubmission();
	}

	public void setAssignment(String name) {
		this.assignmentName = name;
	}

	public Assignment getAssignment() {
		return assMap.get(assignmentName);
	}
}

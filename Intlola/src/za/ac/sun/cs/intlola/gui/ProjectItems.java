package za.ac.sun.cs.intlola.gui;

import java.util.HashMap;

import za.ac.sun.cs.intlola.processing.json.Assignment;
import za.ac.sun.cs.intlola.processing.json.Project;
import za.ac.sun.cs.intlola.processing.json.ProjectInfo;
import za.ac.sun.cs.intlola.processing.json.Submission;

public class ProjectItems {
	private String[] projectStrings;
	private HashMap<String, Project> projectMap;
	private HashMap<String, AssignmentItems> assItems;
	private String projectName;

	public ProjectItems(ProjectInfo[] projectInfos) {
		projectStrings = new String[projectInfos.length];
		assItems = new HashMap<String, AssignmentItems>(
				(int) (projectInfos.length * 1.4));
		projectMap = new HashMap<String, Project>(
				(int) (projectInfos.length * 1.4));
		for (int i = 0; i < projectInfos.length; i++) {
			projectStrings[i] = projectInfos[i].getProject().toString();
			assItems.put(projectStrings[i],
					new AssignmentItems(projectInfos[i].getAssignments()));
			projectMap.put(projectStrings[i], projectInfos[i].getProject());
		}
	}

	public String[] getNames() {
		return projectStrings;
	}

	public void setProject(String projectName) {
		this.projectName = projectName;
	}

	public Project getProject() {
		return projectMap.get(projectName);
	}

	public String[] getAssignmentNames() {
		AssignmentItems items = assItems.get(projectName);
		if (items == null) {
			return new String[] {};
		}
		return items.getNames();
	}

	public void setAssignment(String name) {
		if (projectName == null) {
			return;
		}
		assItems.get(projectName).setAssignment(name);
	}

	public Assignment getAssignment() {
		if (projectName == null) {
			return null;
		}
		return assItems.get(projectName).getAssignment();
	}

	public String[] getSubmissionNames() {
		AssignmentItems items = assItems.get(projectName);
		if (items == null) {
			return new String[] {};
		}
		return items.getSubmissionNames();
	}

	public void setSubmission(String name) {
		if (projectName == null) {
			return;
		}
		assItems.get(projectName).setSubmission(name);
	}

	public Submission getSubmission() {
		if (projectName == null) {
			return null;
		}
		return assItems.get(projectName).getSubmission();
	}
}

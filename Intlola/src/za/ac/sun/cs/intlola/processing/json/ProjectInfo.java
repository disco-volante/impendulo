package za.ac.sun.cs.intlola.processing.json;

public class ProjectInfo {
	Project Project;
	AssignmentInfo[] Assignments;

	public ProjectInfo() {
	}

	public ProjectInfo(Project p, AssignmentInfo[] as) {
		Project = p;
		Assignments = as;
	}

	public Project getProject() {
		return Project;
	}

	public AssignmentInfo[] getAssignments() {
		return Assignments;
	}
}

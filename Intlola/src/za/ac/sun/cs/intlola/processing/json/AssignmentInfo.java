package za.ac.sun.cs.intlola.processing.json;

public class AssignmentInfo {
	Assignment Assignment;
	Submission[] Submissions;

	public AssignmentInfo() {
	}

	public AssignmentInfo(Assignment a, Submission[] subs) {
		Assignment = a;
		Submissions = subs;
	}

	public Assignment getAssignment() {
		return Assignment;
	}

	public Submission[] getSubmissions() {
		if (Submissions == null) {
			return new Submission[0];
		}
		return Submissions;
	}
}

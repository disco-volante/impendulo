package za.ac.sun.cs.intlola.processing;

import java.io.Serializable;
import java.util.Date;

public class Submission implements Serializable {

	private static final long serialVersionUID = -7128594023385132073L;
	String Id, ProjectId, User, Mode;
	long Time;

	@Override
	public int hashCode() {
		return Id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Submission other = (Submission) obj;
		if (Id == null) {
			if (other.Id != null) {
				return false;
			}
		} else if (!Id.equals(other.Id)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "User: " + User + ", Date: " + new Date(Time);
	}
}
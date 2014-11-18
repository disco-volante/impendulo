package za.ac.sun.cs.intlola.processing.json;

import java.io.Serializable;
import java.util.UUID;

import za.ac.sun.cs.intlola.util.Misc;

public class Assignment implements Serializable {
	private static final long serialVersionUID = -7128594023385132073L;
	public String Id;
	public String ProjectId;
	public String Name;
	public String User;
	public long Start;
	public long End;

	public Assignment() {
	}

	public Assignment(String n, String u, long start, long end) {
		Id = UUID.randomUUID().toString();
		Name = n;
		User = u;
		Start = start;
		End = end;
	}

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
		Assignment other = (Assignment) obj;
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
		return Name + " " + Misc.TimeString(Start) + " \u2192 "
				+ Misc.TimeString(End);
	}
}

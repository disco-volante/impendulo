package za.ac.sun.cs.intlola.processing;

import java.io.Serializable;
import java.util.Date;

public class Project implements Serializable {
	private static final long serialVersionUID = -7128594023385132073L;
	String Id, Name, User, Lang;
	long Time;
	byte [] Skeleton;

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
		Project other = (Project) obj;
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
		return "Name: " + Name + ", User: " + User + ", Language: " + Lang
				+ ", Date: " + new Date(Time);
	}

}
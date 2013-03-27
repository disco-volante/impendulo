package fmv.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Submission {
	Object id;
	int number;
	long date;
	private DateFormat df = new SimpleDateFormat("HH:mm, d MMM yyyy");

	public Submission(Object id, int number, long date) {
		this.id = id;
		this.number = number;
		this.date = date;
	}

	public String toString() {
		return "Submission " + number + " at " + df.format(new Date(date));
	}
}
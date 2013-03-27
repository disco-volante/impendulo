package fmv.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Submission {
	private final Object id;
	private final int number;
	private final long date;
	private final String format;
	private final DateFormat df = new SimpleDateFormat("HH:mm, d MMM yyyy");

	public Submission(final Object id, final int number, final long date,
			final String format) {
		this.id = id;
		this.number = number;
		this.date = date;
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public Object getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Submission " + number + " at " + df.format(new Date(date));
	}
}
package fmv.db;

import org.bson.types.ObjectId;


public final class Submission {
	private final ObjectId id;
	private final String user;
	private final int number;
	private final String format;
	public Submission(final ObjectId id, final String user, final int number,
			final String format) {
		this.id = id;
		this.user = user;
		this.number = number;
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return user+" " + number;
	}
	
	
}
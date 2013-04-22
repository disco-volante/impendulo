package fmv.db;

import java.util.Date;
import java.util.NavigableMap;

import org.bson.types.ObjectId;

import fmv.Version;

public final class Submission {
	private final ObjectId id;
	private final String user;
	private final long time;
	private final String format;

	public Submission(final ObjectId id, final String user, final long time,
			final String format) {
		this.id = id;
		this.user = user;
		this.time = time;
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
		return user + " " + time;
	}

	public NavigableMap<Date, Version> getFiles() {
		// TODO Auto-generated method stub
		return null;
	}

}
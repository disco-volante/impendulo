package fmv.db;

import java.util.Date;

public class DBFile implements Comparable<DBFile> {
	private final byte[] data;

	private final String name;
	private final long date;

	public DBFile(final String name, final long date, final byte[] data) {
		this.name = name;
		this.date = date;
		this.data = data;
	}

	@Override
	public int compareTo(final DBFile o) {
		int ret = 0;
		if (o == null || date < o.date) {
			ret = -1;
		} else if (date > o.date) {
			ret = 1;
		}
		return ret;
	}

	public byte[] getBytes() {
		return data;
	}

	public Date getDate() {
		return new Date(date);
	}

	public String getName() {
		return name;
	}
}

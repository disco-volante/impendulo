package fmv.db;

import java.util.Date;

public class DBFile implements Comparable<DBFile> {
	public byte[] data;

	public String name;
	public Date date;

	public DBFile(final String name, final Date date, final byte[] data) {
		this.name = name;
		this.date = date;
		this.data = data;
	}

	@Override
	public int compareTo(final DBFile o) {
		int ret = 0;
		if (o == null || date.before(o.date)) {
			ret = -1;
		} else if (date.after(o.date)) {
			ret = 1;
		}
		return ret;
	}
}

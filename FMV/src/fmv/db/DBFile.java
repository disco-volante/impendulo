package fmv.db;

import java.util.Date;

public class DBFile implements Comparable<DBFile>{
	public DBFile(String name, Date date, byte[] data) {
		this.name = name;
		this.date = date;
		this.data = data;
	}

	public byte[] data;
	public String name;
	public Date date;

	@Override
	public int compareTo(DBFile o) {
		int ret = 0;
		if(o == null || date.before(o.date)){
			ret = -1;
		}
		else if(date.after(o.date)){
			ret = 1;
		}
		return ret;
	}
}

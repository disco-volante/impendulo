package fmv.db;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class DBFile {
	private final String name;
	private final byte[] data;
	DBObject info;
	BasicDBList results;

	public DBFile(String name, DBObject info, byte[] data, BasicDBList results) {
		this.name = name;
		this.info = info;
		this.data = data;
		this.results = results;
	}

	public byte[] getBytes() {
		return data;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInfo(String key, Class<T> clazz) {
		return (T) info.get(key);
	}
}

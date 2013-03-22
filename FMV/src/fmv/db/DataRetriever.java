package fmv.db;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DataRetriever {
	private MongoClient client;
	private DB db;
	private static final String DB_NAME = "impendulo";
	private static final String PROJECTS = "projects";
	private static final String USERS = "users";

	public DataRetriever(String url) throws UnknownHostException {
		client = new MongoClient(url);
		db = client.getDB(DB_NAME);
	}

	public static void main(String args[]) {
		try {
			DataRetriever ret = new DataRetriever("localhost");
			List<String> projects = ret.getProjects();
			for (String p : projects) {
				System.out.println(p);
				System.out.println(ret.getProjectDates(p));
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private List<?> getDistinct(String col, String key) {
		DBCollection collection = db.getCollection(col);
		return collection.distinct(key);
	}

	private DBCursor getMatching(String col, DBObject matching, DBObject ref) {
		DBCollection collection = db.getCollection(col);
		return collection.find(matching, ref);
	}

	@SuppressWarnings("unchecked")
	public List<String> getProjects() {
		return (List<String>) getDistinct(PROJECTS, "name");
	}

	@SuppressWarnings("unchecked")
	public List<String> getUsers() {
		return (List<String>) getDistinct(USERS, "_id");
	}

	public List<Date> getProjectDates(String project) {
		DBObject ref = new BasicDBObject("date", 1).append("_id", 0);
		DBObject matcher = new BasicDBObject("name", project);
		DBCursor cursor = getMatching(PROJECTS, matcher, ref);
		List<Date> ret = new ArrayList<Date>();
		for (DBObject o : cursor) {
			ret.add(new Date((Long) o.get("date")));
		}
		return ret;
	}

	public List<String> getProjectUsers(String project) {
		DBObject ref = new BasicDBObject("user", 1).append("_id", 0);
		DBObject matcher = new BasicDBObject("name", project);
		DBCursor cursor = getMatching(PROJECTS, matcher, ref);
		List<String> ret = new ArrayList<String>();
		for (DBObject o : cursor) {
			ret.add((String) o.get("user"));
		}
		return ret;
	}

	public List<String> getProjectTokens(String project, String user, Date date) {
		DBObject ref = new BasicDBObject("token", 1).append("_id", 0);
		BasicDBObject matcher = new BasicDBObject("name", project);
		if (user != null) {
			matcher.append("user", user);
		}
		if (date != null) {
			matcher.append("date", date.getTime());
		}
		DBCursor cursor = getMatching(PROJECTS, matcher, ref);
		List<String> ret = new ArrayList<String>();
		for (DBObject o : cursor) {
			ret.add((String) o.get("token"));
		}
		return ret;
	}

	public List<DBFile> retrieveFiles(String token) {
		DBObject ref = new BasicDBObject("files", 1).append("_id", 0);
		DBObject matcher = new BasicDBObject("token", token);
		DBCursor cursor = getMatching(PROJECTS, matcher, ref);
		List<DBFile> files = new ArrayList<DBFile>();
		for (DBObject o : cursor) {
			System.out.println(o);
			BasicDBList list = (BasicDBList) o.get("files");
			for (Object file : list) {
				String name = (String) ((DBObject) file).get("name");
				Date date = new Date((Long) ((DBObject) file).get("date"));
				byte[] data = ((byte[]) ((DBObject) file).get("data"));
				files.add(new DBFile(name, date, data));
			}
		}
		Collections.sort(files);
		return files;
	}
}

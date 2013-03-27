package fmv.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DataRetriever {
	private final MongoClient client;
	private final DB db;
	private static final String DB_NAME = "impendulo";
	private static final String PROJECTS = "projects";
	private static final String USERS = "users";

	@SuppressWarnings("resource")
	public static void main(final String args[]) {
		try {
			final DataRetriever ret = new DataRetriever("localhost");
			final List<String> projects = ret.getProjects();
			for (final String p : projects) {
				System.out.println(p);
				System.out.println(ret.getProjectDates(p));
				byte[] bytes = ret.getTests(p);
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(new File("tests.zip"));
					fos.write(bytes);
					Enumeration<? extends ZipEntry> entries = new ZipFile(
							"tests.zip").entries();
					while (entries.hasMoreElements()) {
						System.out.println(entries.nextElement());
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public DataRetriever(final String url) throws UnknownHostException {
		client = new MongoClient(url);
		db = client.getDB(DataRetriever.DB_NAME);
	}

	private List<?> getDistinct(final String col, final String key) {
		final DBCollection collection = db.getCollection(col);
		return collection.distinct(key);
	}

	private DBCursor getMatching(final String col, final DBObject matching,
			final DBObject ref) {
		final DBCollection collection = db.getCollection(col);
		return collection.find(matching, ref);
	}

	public List<Long> getProjectDates(final String project) {
		final DBObject ref = new BasicDBObject("date", 1).append("_id", 0);
		final DBObject matcher = new BasicDBObject("name", project);
		final DBCursor cursor = getMatching(DataRetriever.PROJECTS, matcher,
				ref);
		final List<Long> ret = new ArrayList<Long>();
		for (final DBObject o : cursor) {
			ret.add(((Long) o.get("date")));
		}
		return ret;
	}

	public byte[] getTests(String project) {
		final DBObject ref = new BasicDBObject("tests", 1).append("_id", 0);
		final DBObject matcher = new BasicDBObject("project", project);
		final DBCursor cursor = getMatching(DataRetriever.PROJECTS, matcher,
				ref);
		for (final DBObject o : cursor) {
			return (byte[]) o.get("tests");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> getProjects() {
		return (List<String>) getDistinct(DataRetriever.PROJECTS, "name");
	}

	public List<String> getProjectTokens(final String project,
			final String user, final Date date) {
		final DBObject ref = new BasicDBObject("token", 1).append("_id", 0);
		final BasicDBObject matcher = new BasicDBObject("name", project);
		if (user != null) {
			matcher.append("user", user);
		}
		if (date != null) {
			matcher.append("date", date.getTime());
		}
		final DBCursor cursor = getMatching(DataRetriever.PROJECTS, matcher,
				ref);
		final List<String> ret = new ArrayList<String>();
		for (final DBObject o : cursor) {
			ret.add((String) o.get("token"));
		}
		return ret;
	}

	public List<String> getProjectUsers(final String project) {
		final DBObject ref = new BasicDBObject("user", 1).append("_id", 0);
		final DBObject matcher = new BasicDBObject("name", project);
		final DBCursor cursor = getMatching(DataRetriever.PROJECTS, matcher,
				ref);
		final List<String> ret = new ArrayList<String>();
		for (final DBObject o : cursor) {
			ret.add((String) o.get("user"));
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<String> getUsers() {
		return (List<String>) getDistinct(DataRetriever.USERS, "_id");
	}

	public List<DBFile> retrieveFiles(final Submission sub) {
		final DBObject ref = new BasicDBObject("files", 1).append("_id", 0);
		final DBObject matcher = new BasicDBObject("_id", sub.id);
		final DBCursor cursor = getMatching(DataRetriever.PROJECTS, matcher,
				ref);
		final List<DBFile> files = new ArrayList<DBFile>();

		final DBObject o = cursor.curr();
		final BasicDBList list = (BasicDBList) o.get("files");
		for (final Object file : list) {
			final String name = (String) ((DBObject) file).get("name");
			final Date date = new Date((Long) ((DBObject) file).get("date"));
			final byte[] data = (byte[]) ((DBObject) file).get("data");
			files.add(new DBFile(name, date, data));
		}
		Collections.sort(files);
		return files;
	}

	public Map<String, ArrayList<Submission>> getSubmissions(String project) {
		final DBObject ref = new BasicDBObject("submission_number", 1)
				.append("_id", 1).append("date", 1).append("user", 1);
		final BasicDBObject matcher = new BasicDBObject("name", project);
		final DBCursor cursor = getMatching(DataRetriever.PROJECTS, matcher,
				ref);
		final Map<String, ArrayList<Submission>> ret = new HashMap<String, ArrayList<Submission>>();
		for (final DBObject o : cursor) {
			String key = (String) o.get("user");
			if (!ret.containsKey(key)) {
				ret.put(key, new ArrayList<Submission>());
			}
			Submission submission = new Submission(o.get("_id"),
					(Integer) o.get("submission_number"), (Long) o.get("date"));
			ret.get(key).add(submission);
		}
		return ret;
	}
}

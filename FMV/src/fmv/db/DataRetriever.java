package fmv.db;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class DataRetriever {
	private final Mongo client;
	private final DB db;

	public static void main(final String args[]) {
		try {
			final DataRetriever ret = new DataRetriever(Vals.ADDRESS);
			final List<String> projects = ret.getProjects();
			for (final String p : projects) {
				ArrayList<Submission> subs = ret.getSubmissions(p);
				System.out.println(subs);
				for (Submission sub : subs) {
					ret.retrieveFiles(sub);
				}
			}

		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public DataRetriever(final String url) throws UnknownHostException {
		client = new Mongo(url);
		db = client.getDB(Vals.DB_NAME);
	}

	private List<?> getDistinct(final String col, final String key) {
		final DBCollection collection = db.getCollection(col);
		return collection.distinct(key);
	}

	private DBCursor getMultiple(final String col, final DBObject matching,
			final DBObject ref) {
		final DBCollection collection = db.getCollection(col);
		return collection.find(matching, ref);
	}

	private DBObject getSingle(final String col, final DBObject matching,
			final DBObject ref) {
		final DBCollection collection = db.getCollection(col);
		return collection.findOne(matching, ref);
	}

	@SuppressWarnings("unchecked")
	public List<String> getProjects() {
		return (List<String>) getDistinct(Vals.SUBS, Vals.PROJECT);
	}

	public ArrayList<Submission> getSubmissions(final String project) {
		final DBObject ref = new BasicDBObject();
		final BasicDBObject matcher = new BasicDBObject(Vals.PROJECT, project)
				.append(Vals.MODE, Vals.INDIVIDUAL);
		final DBCursor cursor = getMultiple(Vals.SUBS, matcher, ref);
		final ArrayList<Submission> ret = new ArrayList<Submission>();
		for (final DBObject o : cursor) {
			final Submission submission = new Submission(
					(ObjectId) o.get(Vals.ID), (String) o.get(Vals.USER),
					(Long) o.get(Vals.TIME), (String) o.get(Vals.MODE));
			ret.add(submission);
		}
		return ret;
	}

	public byte[] getTests(final String project) {
		byte[] data = null;
		final DBObject sub = getSingle(Vals.SUBS, new BasicDBObject(
				Vals.PROJECT, project).append(Vals.MODE, Vals.TEST),
				new BasicDBObject());
		List<DBObject> list = getFiles(new BasicDBObject(Vals.SUBID,
				sub.get(Vals.ID)));
		if (list != null && list.size() > 0) {
			DBObject obj = ((DBObject) list.get(0));
			if (obj != null) {
				data = (byte[]) obj.get(Vals.DATA);
			}
		}
		return data;
	}

	public List<DBObject> getFiles(BasicDBObject matcher) {
		final DBObject ref = new BasicDBObject();
		final DBCursor o = getMultiple(Vals.FILES, matcher, ref);
		return o.toArray();
	}

	public List<DBFile> retrieveFiles(final Submission sub) {
		final List<DBObject> list = getFiles(new BasicDBObject(Vals.SUBID,
				sub.getId()));
		final List<DBFile> files = new ArrayList<DBFile>();
		if (list != null) {
			for (final DBObject file : list) {
				final String name = (String) file.get(Vals.NAME);
				DBObject info = (DBObject) file.get(Vals.INFO);
				final byte[] data = (byte[]) file.get(Vals.DATA);
				BasicDBList results = (BasicDBList) file.get(Vals.RESULTS);
				files.add(new DBFile(name, info, data, results));
				System.out.println(files.get(files.size() - 1)
						.getInfo(Vals.TIME, Double.class).longValue());
				// files.add(new DBFile(name, date, data));
			}
		}
		return files;
	}
}

package fmv.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
	private static final String DB_NAME = "impendulo";
	private static final String PROJECTS = "projects";

	@SuppressWarnings("resource")
	public static void main(final String args[]) {
		try {
			final DataRetriever ret = new DataRetriever("localhost");
			final List<String> projects = ret.getProjects();
			for (final String p : projects) {
				final byte[] bytes = ret.getTests(p);
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(new File("tests.zip"));
					fos.write(bytes);
					final Enumeration<? extends ZipEntry> entries = new ZipFile(
							"tests.zip").entries();
					while (entries.hasMoreElements()) {
						System.out.println(entries.nextElement());
					}
				} catch (final FileNotFoundException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}

			}

		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public DataRetriever(final String url) throws UnknownHostException {
		client = new Mongo(url);
		db = client.getDB(DataRetriever.DB_NAME);
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
		return (List<String>) getDistinct(DataRetriever.PROJECTS, "project");
	}

	public ArrayList<Submission> getSubmissions(final String project) {
		final DBObject ref = new BasicDBObject();
		final BasicDBObject matcher = new BasicDBObject("project", project);
		final DBCursor cursor = getMultiple(DataRetriever.PROJECTS, matcher,
				ref);
		final ArrayList<Submission> ret = new ArrayList<Submission>();
		for (final DBObject o : cursor) {
			final Submission submission = new Submission(
					(ObjectId) o.get("_id"), (String) o.get("user"),
					(Integer) o.get("number"), (String) o.get("mode"));
			ret.add(submission);
		}
		return ret;
	}

	public byte[] getTests(final String project) {
		byte[] data = null;
		final BasicDBList list = getFiles(new BasicDBObject("project", project)
				.append("mode", "TEST"));
		if (list != null && list.size() > 0) {
			DBObject obj = ((DBObject) list.get(0));
			if (obj != null) {
				data = (byte[]) obj.get("data");
			}
		}
		return data;
	}

	public BasicDBList getFiles(BasicDBObject matcher) {
		final DBObject ref = new BasicDBObject("files", 1);
		final DBObject o = getSingle(DataRetriever.PROJECTS, matcher, ref);
		BasicDBList list = null;
		if (o != null) {
			list = (BasicDBList) o.get("files");
		}
		return list;
	}

	public List<DBFile> retrieveFiles(final Submission sub) {
		final BasicDBList list = getFiles(new BasicDBObject("_id", sub.getId()));
		final List<DBFile> files = new ArrayList<DBFile>();
		if (list != null) {
			for (final Object file : list) {
				final String name = (String) ((DBObject) file).get("name");
				final long date = (Long) ((DBObject) file).get("date");
				final byte[] data = (byte[]) ((DBObject) file).get("data");
				files.add(new DBFile(name, date, data));
			}
			Collections.sort(files);
		}
		return files;
	}
}

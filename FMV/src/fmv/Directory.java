package fmv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.swing.DefaultListModel;

public class Directory {

	/**
	 * Name of the directory.
	 */
	private String dir = null;

	/**
	 * The model for the list of zip files in the directory.
	 */
	@SuppressWarnings("rawtypes")
	private DefaultListModel listModel = null;

	private Properties properties = null;

	/**
	 * Construct a directory given the name.
	 * 
	 * @param dir
	 *            name of the directory
	 */
	@SuppressWarnings("rawtypes")
	public Directory(final String dir) {
		this.dir = dir;
		properties = new Properties();
		final File f = new File(dir + File.separator + "fmv.xml");
		if (f.exists()) {
			try {
				properties.loadFromXML(new FileInputStream(f));
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		listModel = new DefaultListModel();
	}

	/**
	 * Add a new archive to this directory. When the directory is opened, the
	 * main application will enumerate its contents and call this routine for
	 * each of the entries.
	 * 
	 * @param name
	 *            name of the next entry in the directory
	 */
	@SuppressWarnings("unchecked")
	public void addArchive(final String name) {
		listModel.addElement(new ProjectData(dir, name));
	}

	public ProjectData getArchive(final int index) {
		return (ProjectData) listModel.getElementAt(index);
	}

	/**
	 * Returns the model for the directory contents.
	 * 
	 * @return list of directory entries
	 */
	@SuppressWarnings("rawtypes")
	public DefaultListModel getModel() {
		return listModel;
	}

	/**
	 * Returns the model for the i-th zip file entry in the directory.
	 * 
	 * @param index
	 *            index of the entry in the directory
	 * @return list of files in the index-th entry in directory
	 */
	@SuppressWarnings("rawtypes")
	public DefaultListModel getModel(final int index) {
		return ((ProjectData) listModel.getElementAt(index)).getModel(this);
	}

	public String getXArchiveProperty(final ProjectData archive, final String key,
			final String defualt) {
		if (key == null) {
			return properties.getProperty(archive.toString(), defualt);
		} else {
			return properties.getProperty(archive.toString() + "." + key,
					defualt);
		}
	}

	public String getXDirectoryProperty(final String key, final String defualt) {
		return properties.getProperty(key, defualt);
	}

	public String getXVersionProperty(final ProjectData archive,
			final Source source, final Date date, final String key,
			final String defualt) {
		final String prefix = archive.toString() + "." + source.getPath() + "."
				+ date.getTime() + "." + key;
		return properties.getProperty(prefix, defualt);
	}

	public void saveXProperties() {
		final File f = new File(dir + File.separator + "fmv.xml");
		try {
			for (final Object value : properties.values()) {
				FMV.log(Directory.class.getName(), value.toString());
			}
			properties.storeToXML(new FileOutputStream(f), "FMV properties ("
					+ new Date() + ")");
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void setDiff(final int i, final int j) {
		final ProjectData a = (ProjectData) listModel.getElementAt(i);
		a.setDiff(j);
	}

	public void setXArchiveProperty(final ProjectData archive, final String key,
			final String value) {
		if (key == null) {
			properties.setProperty(archive.toString(), value);
		} else {
			properties.setProperty(archive.toString() + "." + key, value);
		}
	}

	public void setXDirectoryProperty(final String key, final String value) {
		properties.setProperty(key, value);
	}

	public void setXVersionProperty(final ProjectData archive, final Source source,
			final Date date, final String key, final String value) {
		final String prefix = archive.toString() + "." + source.getPath() + "."
				+ date.getTime() + "." + key;
		properties.setProperty(prefix, value);
	}

}

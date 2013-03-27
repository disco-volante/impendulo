package fmv;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import fmv.TablePane.ArchiveData;
import fmv.db.DBFile;

public class Archive {

	/**
	 * Flag to tell if this archive has been viewed and therefore extracted
	 * before.
	 */
	private boolean isSetup = false;

	private boolean isCompiled = false;

	private final boolean isArchive;
	/**
	 * The model for the list of ZIP files. (Think MVC.)
	 */
	@SuppressWarnings("rawtypes")
	private DefaultListModel listModel = null;

	/**
	 * Name of the directory containing the ZIP file.
	 */
	private String dir = null;

	/**
	 * Name of the ZIP file.
	 */
	private String name = null;

	/**
	 * Tree of items contained in the ZIP file.
	 */
	private final Source rootItem = new Source();

	private List<DBFile> files;

	private String tests;

	/**
	 * Constructor. Stores the name of the ZIP file.
	 */
	@SuppressWarnings("rawtypes")
	public Archive(final String dir, final String name) {
		this.dir = dir;
		this.name = name;
		tests = dir + File.separator + "TESTING.zip";
		listModel = new DefaultListModel();
		isArchive = true;
		if ("true".equals(FMV.getArchiveProperty(this, "false"))) {
			final ArchiveData data = new ArchiveData();
			data.readProperties(this);
			FMV.tablePane.addData(name, data);
			isCompiled = true;
		}
	}

	/**
	 * Constructor. Stores the name of the ZIP file.
	 * 
	 * @param s
	 */
	@SuppressWarnings("rawtypes")
	public Archive(final String project, final String name,
			final List<DBFile> files, boolean archive) {
		this.files = files;
		listModel = new DefaultListModel();
		isArchive = archive;
		if (isArchive) {
			this.name = writeZip(files.get(0));
		} else{
			this.name = name;
		}
		if ("true".equals(FMV.getArchiveProperty(this, "false"))) {
			final ArchiveData data = new ArchiveData();
			data.readProperties(this);
			FMV.tablePane.addData(name, data);
			isCompiled = true;
		} else {
			tests = FMV.getTests(project);
		}
	}

	private String writeZip(DBFile dbFile) {
		String outfile = FMV.ZIP_DIR + File.separator + dbFile.getName();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(outfile));
			fos.write(dbFile.getBytes());
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return outfile;
	}

	/**
	 * Add a new item to the archive. This will succeed if the new item is some
	 * file or directory in the "src" hierarchy. It if succeeds, the hierarchy,
	 * time stamp, and contents of the file is recorded in the archive.
	 * 
	 * @param name
	 *            the name of the item to add. This includes the file path, but
	 *            separated by "_" instead of the usual directory separators. It
	 *            also ends with the time stamp, counter stamp, and a letter to
	 *            indicate if the item has been added, changed, or removed.
	 */
	private void addItem(final String name, final InputStream in) {
		final String[] components = name.split("_");
		final int last = components.length - 4;
		int first = last;
		for (int i = 0; i <= last; i++) {
			if (components[i].equals("src")
					&& !components[i + 1].equals("testing")) {
				first = i + 1;
				break;
			}
		}
		if (first >= last) {
			return;
		}
		Source r = rootItem;
		for (int i = first; i <= last; i++) {
			r = r.addChild(components[i]);
		}
		r.addDetails(new Date(Long.parseLong(components[last + 1])), in);
	}

	@SuppressWarnings("rawtypes")
	public DefaultListModel getModel(final Directory directory) {
		if (!isSetup()) {
			setup();
		}
		return listModel;
	}

	public boolean isCompiled() {
		return isCompiled;
	}

	public boolean isSetup() {
		return isSetup;
	}

	public void runTool(final String tool) {
		ToolRunner runner;
		if (tool.equals("Tests")) {
			runner = new ToolRunner(rootItem, FMV.toolrunner, "Tests", tests);
		} else {
			runner = new ToolRunner(rootItem, FMV.toolrunner, tool);

		}
		runner.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent e) {
				if ("progress".equals(e.getPropertyName())) {
					FMV.toolrunner.setProgress((Integer) e.getNewValue());
				} else if ("state".equals(e.getPropertyName())
						&& SwingWorker.StateValue.DONE == e.getNewValue()) {
					FMV.toolrunner.deactivate(false);
				}
			}
		});
		runner.execute();
		FMV.toolrunner.activate(this, rootItem);
	}

	public void setCompiled() {
		isCompiled = true;
	}

	public void setDiff(final int j) {
		final Source s = (Source) listModel.getElementAt(j);
		FMV.diffPane.setItem(this, s);
		FMV.timeGraph.setSource(this, s);
	}

	public void setup() {
		if (isArchive) {
			ZipFile z;
			try {
				z = new ZipFile(dir + File.separator + name);
				final Enumeration<? extends ZipEntry> zz = z.entries();
				while (zz.hasMoreElements()) {
					final ZipEntry e = zz.nextElement();
					if (e.isDirectory()) {
						continue;
					}
					addItem(e.getName(), z.getInputStream(e));
				}
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		} else {
			for (final DBFile file : files) {
				addItem(file.getName(),
						new ByteArrayInputStream(file.getBytes()));
			}
		}
		rootItem.canonize();
		listModel.clear();
		rootItem.addToListModel(listModel, this);
		isSetup = true;
	}

	/**
	 * Return the file name for display in a list of ZIP files.
	 */
	@Override
	public String toString() {
		return name;
	}

}

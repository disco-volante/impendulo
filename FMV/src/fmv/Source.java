package fmv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.TextRegion;
import javax.swing.text.BadLocationException;

import fmv.DiffAction.Operation;
import fmv.TablePane.ArchiveData;
import fmv.tools.Tools;

/**
 * An archive item represents one file or directory inside a ZIP file.
 */
public class Source {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss.SSS");

	private Source parent = null;

	private List<Source> children = new ArrayList<Source>();

	private String name = "";

	private String path = "";

	private NavigableMap<Date, Version> versions = null;

	private Map<String, Integer> forward = null;

	private Map<Integer, String> backward = null;

	public Source() {
		name = "ROOT";
	}

	public Source(final Source parent, final String name) {
		this.parent = parent;
		children = new ArrayList<Source>();
		this.name = name;
		path = parent.getPath() + File.separator + name;
		versions = new TreeMap<Date, Version>();
		forward = new HashMap<String, Integer>();
		backward = new HashMap<Integer, String>();
	}

	public Source addChild(final String child) {
		for (final Source c : children) {
			if (c.getName().equals(child)) {
				return c;
			}
		}
		final Source c = new Source(this, child);
		children.add(c);
		return c;
	}

	public void addDetails(final Date date, final InputStream in) {
		final List<Integer> lineList = new ArrayList<Integer>();
		try {
			boolean prevEmpty = false;
			final BufferedReader bin = new BufferedReader(
					new InputStreamReader(in));
			while (true) {
				final String s = bin.readLine();
				if (s == null) {
					break;
				}
				if (s.matches("[ \t]*")) {
					if (prevEmpty) {
						continue;
					}
					prevEmpty = true;
				} else {
					prevEmpty = false;
				}
				if (!forward.containsKey(s)) {
					final int n = forward.size();
					forward.put(s, n);
					backward.put(n, s);
				}
				final int mapping = forward.get(s);
				lineList.add(mapping);
			}
		} catch (final IOException e) {
			FMV.log(Source.class.getName(), e.getMessage());
		}
		versions.put(date, new Version(lineList));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addToListModel(final DefaultListModel model,
			final Archive archive) {
		if (parent != null && children.size() == 0) {
			model.addElement(this);
			for (Map.Entry<Date, Version> e = versions.firstEntry(); e != null; e = versions
					.higherEntry(e.getKey())) {
				e.getValue().setStatus(
						FMV.getVersionProperty(archive, this, e.getKey(),
								"status", "unknown"));
				e.getValue().setOutput(
						FMV.getVersionProperty(archive, this, e.getKey(),
								"output", ""));
				for (final String tool : Tools.getTools()) {
					e.getValue().setReport(
							tool,
							FMV.getVersionProperty(archive, this, e.getKey(),
									tool + " report", ""));
				}
				e.getValue().setAnnotation(
						FMV.getVersionProperty(archive, this, e.getKey(),
								"note", ""));
			}
		}
		for (final Source c : children) {
			c.addToListModel(model, archive);
		}
	}

	public void canonize() {
		if (parent != null && children.size() == 0) {
			final Iterator<Version> i = versions.values().iterator();
			if (i.hasNext()) {
				Version prev = i.next();
				while (i.hasNext()) {
					final Version next = i.next();
					final List<DiffAction> actions = diff(prev.getLineList(),
							next.getLineList());
					if (actions.size() > 1
							|| actions.get(0).getOp() != DiffAction.Operation.SKIP) {
						prev = next;
						next.setDiff(actions);
					} else {
						i.remove();
					}
				}
			}
		}
		for (final Source c : children) {
			c.canonize();
		}
	}

	public List<DiffAction> diff(final List<Integer> x, final List<Integer> y) {
		final List<DiffAction.Operation> ops = getOperations(x, y);
		final List<DiffAction> actions = getActions(ops);
		return finalizeActions(actions);
	}

	public void extractProperties(final Archive archive, final ArchiveData data) {
		if (parent != null && children.size() == 0) {
			for (Map.Entry<Date, Version> e = versions.firstEntry(); e != null; e = versions
					.higherEntry(e.getKey())) {
				e.getValue().getStatus().setData(data);
				FMV.setVersionProperty(archive, this, e.getKey(), "status", e
						.getValue().getStatus().getName());
				FMV.setVersionProperty(archive, this, e.getKey(), "output", e
						.getValue().getOutput());
				for (final Map.Entry<String, String> re : e.getValue()
						.getReports().entrySet()) {
					FMV.setVersionProperty(archive, this, e.getKey(),
							re.getKey() + " report", re.getValue());
				}
			}
		}
		for (final Source c : children) {
			c.extractProperties(archive, data);
		}
	}

	private List<DiffAction> finalizeActions(final List<DiffAction> actions) {
		final List<DiffAction> finalacts = new ArrayList<DiffAction>();
		DiffAction prevAct = null;
		for (final DiffAction a : actions) {
			if (prevAct == null) {
				prevAct = a;
			} else if (prevAct.getCount() != a.getCount()) {
				finalacts.add(prevAct);
				prevAct = a;
			} else if (prevAct.getOp() == DiffAction.Operation.ADD
					&& a.getOp() == DiffAction.Operation.DEL) {
				prevAct = new DiffAction(DiffAction.Operation.CHANGE,
						a.getCount());
			} else if (prevAct.getOp() == DiffAction.Operation.DEL
					&& a.getOp() == DiffAction.Operation.ADD) {
				prevAct = new DiffAction(DiffAction.Operation.CHANGE,
						a.getCount());
			} else {
				finalacts.add(prevAct);
				prevAct = a;
			}
		}
		if (prevAct != null) {
			finalacts.add(prevAct);
		}
		return finalacts;
	}

	public String formatLineNr(final int x) {
		if (x < 10) {
			return "   " + x + " ";
		} else if (x < 100) {
			return "  " + x + " ";
		} else if (x < 1000) {
			return " " + x + " ";
		} else {
			return "" + x + " ";
		}
	}

	private List<DiffAction> getActions(final List<Operation> ops) {
		final List<DiffAction> acts = new ArrayList<DiffAction>();
		DiffAction.Operation prev = DiffAction.Operation.NADA;
		int count = 0;
		for (final DiffAction.Operation op : ops) {
			if (op == prev) {
				count++;
			} else {
				if (prev != DiffAction.Operation.NADA) {
					acts.add(new DiffAction(prev, count));
				}
				prev = op;
				count = 1;
			}
		}
		if (prev != DiffAction.Operation.NADA) {
			acts.add(new DiffAction(prev, count));
		}
		return acts;

	}

	public Set<Date> getKeys() {
		final Set<Date> keys = new TreeSet<Date>();
		if (parent != null && children.size() == 0) {
			keys.addAll(versions.keySet());
		}
		for (final Source c : children) {
			keys.addAll(c.getKeys());
		}
		return keys;
	}

	public String getName() {
		return name;
	}

	private List<Operation> getOperations(final List<Integer> x,
			final List<Integer> y) {
		final List<DiffAction.Operation> ops = new ArrayList<DiffAction.Operation>();
		final int m = x.size();
		final int n = y.size();
		final int[][] opt = new int[m + 1][n + 1];
		for (int i = m - 1; i >= 0; i--) {
			for (int j = n - 1; j >= 0; j--) {
				if (x.get(i).equals(y.get(j))) {
					opt[i][j] = opt[i + 1][j + 1] + 1;
				} else {
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
				}
			}
		}
		int i = 0, j = 0;
		while (i < m && j < n) {
			if (x.get(i).equals(y.get(j))) {
				i++;
				j++;
				ops.add(DiffAction.Operation.SKIP);
			} else if (opt[i + 1][j] >= opt[i][j + 1]) {
				i++;
				ops.add(DiffAction.Operation.DEL);
			} else {
				j++;
				ops.add(DiffAction.Operation.ADD);
			}
		}
		return ops;
	}

	public String getPath() {
		return path;
	}

	public NavigableMap<Date, Version> getVersions() {
		return versions;
	}

	public void insertDiff(final boolean onLeft, final List<Integer> lines,
			final List<DiffAction> diffs) throws BadLocationException {
		final TextRegion text = FMV.diffPane.getText(onLeft);
		text.clearDoc();
		int n = 1;
		if (diffs == null) {
			for (final int l : lines) {
				text.append(formatLineNr(n++) + backward.get(l) + "\n", null);
			}
		} else {
			final DiffAction.Operation ch = onLeft ? DiffAction.Operation.ADD
					: DiffAction.Operation.DEL;
			final Iterator<Integer> liter = lines.iterator();
			for (final DiffAction d : diffs) {
				int c = d.getCount();
				final DiffAction.Operation o = d.getOp();
				if (o == DiffAction.Operation.SKIP) {
					while (c-- > 0) {
						final int l = liter.next();
						text.append(formatLineNr(n++) + backward.get(l) + "\n",
								null);
					}
				} else if (o == ch) {
					while (c-- > 0) {
						text.append("\n", null);
					}
				} else if (o == DiffAction.Operation.CHANGE) {
					while (c-- > 0) {
						final int l = liter.next();
						text.append(formatLineNr(n++) + backward.get(l) + "\n",
								TextRegion.changed);
					}
				} else {
					while (c-- > 0) {
						final int l = liter.next();
						text.append(formatLineNr(n++) + backward.get(l) + "\n",
								TextRegion.delta);
					}
				}
			}
		}
	}

	public void setReport(final Date date, final String tool,
			final String report) {
		if (parent != null && children.size() == 0) {
			final Version v = versions.get(date);
			if (v != null) {
				v.setReport(tool, report);
			}
		}
		for (final Source c : children) {
			c.setReport(date, tool, report);
		}
	}

	public void setStatus(final Date date, final Status status,
			final String output) {
		if (parent != null && children.size() == 0) {
			final Version v = versions.get(date);
			if (v != null) {
				v.setStatus(status);
				v.setOutput(output);
			}
		}
		for (final Source c : children) {
			c.setStatus(date, status, output);
		}
	}

	public void showEmpty(final boolean onLeft) {
		final TextRegion text = FMV.diffPane.getText(onLeft);
		text.clearDoc();
		FMV.diffPane.setLabel(onLeft, "");
		FMV.diffPane.setButton(onLeft, false);
	}

	public void showItem(final boolean onLeft,
			final Map.Entry<Date, Version> version, final List<DiffAction> diffs) {
		try {
			insertDiff(onLeft, version.getValue().getLineList(), diffs);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
		final String s = version.getValue().getStatus().getMessage();
		final String d = Source.dateFormat.format(version.getKey());
		FMV.diffPane.setLabel(onLeft, d + " " + s + " (FB)");
		FMV.diffPane.setButton(onLeft, true);
	}

	@Override
	public String toString() {
		return path;
	}

	public File unpack(final Date date, final String dir) {
		File file = null;
		if (parent != null && children.size() == 0) {
			final Map.Entry<Date, Version> e = versions.floorEntry(date);
			if (e == null) {
				return file;
			}
			final Version v = e.getValue();
			final List<Integer> lines = v.getLineList();
			if (lines == null) {
				return file;
			}
			file = new File(dir + getPath());
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				final FileOutputStream to = new FileOutputStream(file);
				for (final int l : lines) {
					final String s = backward.get(l).trim() + "\n";
					to.write(s.getBytes());
				}
				to.close();
			} catch (final FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (final IOException e2) {
				e2.printStackTrace();
			}
		} else {
			for (final Source c : children) {
				final File tfile = c.unpack(date, dir);
				if (tfile != null) {
					file = tfile;
					break;
				}
			}
		}
		return file;
	}

}
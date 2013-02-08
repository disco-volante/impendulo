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

import fmv.TablePane.ArchiveData;

/**
 * An archive item represents one file or directory inside a ZIP file.
 */
public class Source {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

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

	public Source(Source parent, String name) {
		this.parent = parent;
		children = new ArrayList<Source>();
		this.name = name;
		path = parent.getPath() + File.separator + name;
		versions = new TreeMap<Date, Version>();
		forward = new HashMap<String, Integer>();
		backward = new HashMap<Integer, String>();
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public NavigableMap<Date, Version> getVersions() {
		return versions;
	}

	public String toString() {
		return path;
	}

	public Source addChild(String child) {
		for (Source c : children) {
			if (c.getName().equals(child)) {
				return c;
			}
		}
		Source c = new Source(this, child);
		children.add(c);
		return c;
	}

	public List<DiffAction> diff(List<Integer> x, List<Integer> y) {
		List<DiffAction.Operation> ops = new ArrayList<DiffAction.Operation>();
		int M = x.size();
		int N = y.size();
		int[][] opt = new int[M + 1][N + 1];
		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (x.get(i).equals(y.get(j))) {
					opt[i][j] = opt[i + 1][j + 1] + 1;
				} else {
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
				}
			}
		}
		int i = 0, j = 0;
		while ((i < M) && (j < N)) {
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
		List<DiffAction> acts = new ArrayList<DiffAction>();
		DiffAction.Operation prev = DiffAction.Operation.NADA;
		int count = 0;
		for (DiffAction.Operation op : ops) {
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
		List<DiffAction> finalacts = new ArrayList<DiffAction>();
		DiffAction prevAct = null;
		for (DiffAction a : acts) {
			if (prevAct == null) {
				prevAct = a;
			} else if (prevAct.getCount() != a.getCount()) {
				finalacts.add(prevAct);
				prevAct = a;
			} else if ((prevAct.getOp() == DiffAction.Operation.ADD) && (a.getOp() == DiffAction.Operation.DEL)) {
				prevAct = new DiffAction(DiffAction.Operation.CHANGE, a.getCount());
			} else if ((prevAct.getOp() == DiffAction.Operation.DEL) && (a.getOp() == DiffAction.Operation.ADD)) {
				prevAct = new DiffAction(DiffAction.Operation.CHANGE, a.getCount());
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

	public void canonize() {
		if ((parent != null) && (children.size() == 0)) {
			Iterator<Version> i = versions.values().iterator();
			if (i.hasNext()) {
				Version prev = i.next();
				while (i.hasNext()) {
					Version next = i.next();
					List<DiffAction> actions = diff(prev.getLineList(), next
							.getLineList());
					if ((actions.size() > 1)
							|| (actions.get(0).getOp() != DiffAction.Operation.SKIP)) {
						prev = next;
						next.setDiff(actions);
					} else {
						i.remove();
					}
				}
			}
		}
		for (Source c : children) {
			c.canonize();
		}
	}

	public Set<Date> getKeys() {
		Set<Date> keys = new TreeSet<Date>();
		if ((parent != null) && (children.size() == 0)) {
			keys.addAll(versions.keySet());
		}
		for (Source c : children) {
			keys.addAll(c.getKeys());
		}
		return keys;
	}

	public void unpack(Date date, String dir) {
		if ((parent != null) && (children.size() == 0)) {
			Map.Entry<Date, Version> e = versions.floorEntry(date);
			if (e == null) {
				return;
			}
			Version v = e.getValue();
			List<Integer> lines = v.getLineList();
			File file = new File(dir + getPath());
			if (lines == null) {
				return;
			}
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				FileOutputStream to = new FileOutputStream(file);
				for (int l : lines) {
					String s = backward.get(l).trim() + "\n";
					to.write(s.getBytes());
				}
				to.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		for (Source c : children) {
			c.unpack(date, dir);
		}
	}

	public void setStatus(Date date, Status status, String output) {
		if ((parent != null) && (children.size() == 0)) {
			Version v = versions.get(date);
			if (v != null) {
				v.setStatus(status);
				v.setOutput(output);
			}
		}
		for (Source c : children) {
			c.setStatus(date, status, output);
		}
	}

	public void setReport(Date date, String report, int warningCount) {
		if ((parent != null) && (children.size() == 0)) {
			Version v = versions.get(date);
			if (v != null) {
				v.setReport(report, warningCount);
			}
		}
		for (Source c : children) {
			c.setReport(date, report, warningCount);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addToListModel(DefaultListModel model, Archive archive) {
		if ((parent != null) && (children.size() == 0)) {
			model.addElement(this);
			for (Map.Entry<Date, Version> e = versions.firstEntry(); e != null; e = versions.higherEntry(e.getKey())) {
				e.getValue().setStatus(FMV.getVersionProperty(archive, this, e.getKey(), "status", "unknown"));
				e.getValue().setOutput(FMV.getVersionProperty(archive, this, e.getKey(), "output", ""));
				int wc = Integer.parseInt(FMV.getVersionProperty(archive, this, e.getKey(), "warningCount", "0"));
				e.getValue().setReport(FMV.getVersionProperty(archive, this, e.getKey(), "report", ""), wc);
				e.getValue().setAnnotation(FMV.getVersionProperty(archive, this, e.getKey(), "note", ""));
			}
		}
		for (Source c : children) {
			c.addToListModel(model, archive);
		}
	}

	public void extractProperties(Archive archive, ArchiveData data) {
		if ((parent != null) && (children.size() == 0)) {
			for (Map.Entry<Date, Version> e = versions.firstEntry(); e != null; e = versions.higherEntry(e.getKey())) {
				e.getValue().getStatus().setData(data);
				FMV.setVersionProperty(archive, this, e.getKey(), "status", e.getValue().getStatus().getName());
				FMV.setVersionProperty(archive, this, e.getKey(), "output", e.getValue().getOutput());
				FMV.setVersionProperty(archive, this, e.getKey(), "report", e.getValue().getReport());
				FMV.setVersionProperty(archive, this, e.getKey(), "warningCount", "" + e.getValue().getWarningCount());
			}
		}
		for (Source c : children) {
			c.extractProperties(archive, data);
		}
	}

	public void addDetails(Date date, InputStream in) {
		List<Integer> lineList = new ArrayList<Integer>();
		try {
			boolean prevEmpty = false;
			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
			while (true) {
				String s = bin.readLine();
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
					int n = forward.size();
					forward.put(s, n);
					backward.put(n, s);
				}
				int mapping = forward.get(s);
				lineList.add(mapping);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		versions.put(date, new Version(lineList));
	}

	public String formatLineNr(int x) {
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

	public void insertDiff(boolean onLeft, List<Integer> lines, List<DiffAction> diffs)
			throws BadLocationException {
		TextRegion text = FMV.diffPane.getText(onLeft);
		text.clearDoc();
		int n = 1;
		if (diffs == null) {
			for (int l : lines) {
				text.append(formatLineNr(n++) + backward.get(l) + "\n", null);
			}
		} else {
			DiffAction.Operation ch = onLeft ? DiffAction.Operation.ADD : DiffAction.Operation.DEL;
			Iterator<Integer> liter = lines.iterator();
			for (DiffAction d : diffs) {
				int c = d.getCount();
				DiffAction.Operation o = d.getOp();
				if (o == DiffAction.Operation.SKIP) {
					while (c-- > 0) {
						int l = liter.next();
						text.append(formatLineNr(n++) + backward.get(l) + "\n", null);						
					}
				} else if (o == ch) {
					while (c-- > 0) {
						text.append("\n", null);						
					}
				} else if (o == DiffAction.Operation.CHANGE) {
					while (c-- > 0) {
						int l = liter.next();
						text.append(formatLineNr(n++) + backward.get(l) + "\n", TextRegion.changed);						
					}
				} else {
					while (c-- > 0) {
						int l = liter.next();
						text.append(formatLineNr(n++) + backward.get(l) + "\n", TextRegion.delta);						
					}
				}
			}
		}
	}

	public void showEmpty(boolean onLeft) {
		TextRegion text = FMV.diffPane.getText(onLeft);
		text.clearDoc();
		FMV.diffPane.setLabel(onLeft, "");
		FMV.diffPane.setButton(onLeft, false);
	}

	public void showItem(boolean onLeft, Map.Entry<Date, Version> version, List<DiffAction> diffs) {
		try {
			insertDiff(onLeft, version.getValue().getLineList(), diffs);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		String s = version.getValue().getStatus().getMessage();
		String d = dateFormat.format(version.getKey());
		int wc = version.getValue().getWarningCount();
		FMV.diffPane.setLabel(onLeft, d + " " + s + " (" + wc + " FB)");
		FMV.diffPane.setButton(onLeft, true);
	}

}
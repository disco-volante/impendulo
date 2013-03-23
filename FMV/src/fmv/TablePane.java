package fmv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class TablePane extends JPanel {

	public static class ArchiveData {
		int nocomp = -1;
		int eerrs = -1;
		int efail = -1;
		int aerrs = -1;
		int afail = -1;
		int timeout = -1;
		int ok = -1;
		int total = -1;
		double minTime = -1.0;
		double maxTime = -1.0;
		double aveTime = -1.0;
		double totTime = -1.0;

		public void readProperties(final Archive archive) {
			nocomp = Integer.parseInt(FMV.getArchiveProperty(archive, "nocomp",
					"-1"));
			eerrs = Integer.parseInt(FMV.getArchiveProperty(archive, "eerrs",
					"-1"));
			efail = Integer.parseInt(FMV.getArchiveProperty(archive, "efail",
					"-1"));
			aerrs = Integer.parseInt(FMV.getArchiveProperty(archive, "aerrs",
					"-1"));
			afail = Integer.parseInt(FMV.getArchiveProperty(archive, "afail",
					"-1"));
			timeout = Integer.parseInt(FMV.getArchiveProperty(archive,
					"timeout", "-1"));
			ok = Integer.parseInt(FMV.getArchiveProperty(archive, "ok", "-1"));
			total = Integer.parseInt(FMV.getArchiveProperty(archive, "total",
					"-1"));
			minTime = Double.parseDouble(FMV.getArchiveProperty(archive,
					"minTime", "-1.0"));
			maxTime = Double.parseDouble(FMV.getArchiveProperty(archive,
					"maxTime", "-1.0"));
			aveTime = Double.parseDouble(FMV.getArchiveProperty(archive,
					"aveTime", "-1.0"));
			totTime = Double.parseDouble(FMV.getArchiveProperty(archive,
					"totTime", "-1.0"));
		}

		public void writeProperties(final Archive archive) {
			FMV.setArchiveProperty(archive, "nocomp", "" + nocomp);
			FMV.setArchiveProperty(archive, "eerrs", "" + eerrs);
			FMV.setArchiveProperty(archive, "efail", "" + efail);
			FMV.setArchiveProperty(archive, "aerrs", "" + aerrs);
			FMV.setArchiveProperty(archive, "afail", "" + afail);
			FMV.setArchiveProperty(archive, "timeout", "" + timeout);
			FMV.setArchiveProperty(archive, "ok", "" + ok);
			FMV.setArchiveProperty(archive, "total", "" + total);
			FMV.setArchiveProperty(archive, "minTime", "" + minTime);
			FMV.setArchiveProperty(archive, "maxTime", "" + maxTime);
			FMV.setArchiveProperty(archive, "aveTime", "" + aveTime);
			FMV.setArchiveProperty(archive, "totTime", "" + totTime);
		}

	}

	private class ArchiveTableModel extends AbstractTableModel {

		/**
		 * Apparently we need a version number because we are exending
		 * AbstractTableModel.
		 */
		private static final long serialVersionUID = -8324415962017297601L;

		private final String[] columnNames = { "Archive", "No compile",
				"E-errors", "E-failures", "A-errors", "A-failures", "Timeout",
				"OK", "Total", "Min time", "Max time", "Ave. time", "Tot. time" };

		private final Map<Integer, String> keymap = new TreeMap<Integer, String>();

		private final Map<String, ArchiveData> datamap = new TreeMap<String, ArchiveData>();

		public void addData(final String name, final ArchiveData data) {
			if (!keymap.containsValue(name)) {
				keymap.put(keymap.size(), name);
				datamap.put(name, new ArchiveData());
			}
			final ArchiveData d = datamap.get(name);
			d.nocomp = data.nocomp;
			d.eerrs = data.eerrs;
			d.efail = data.efail;
			d.aerrs = data.aerrs;
			d.afail = data.afail;
			d.timeout = data.timeout;
			d.ok = data.ok;
			d.total = data.total;
			d.minTime = data.minTime;
			d.maxTime = data.maxTime;
			d.aveTime = data.aveTime;
			d.totTime = data.totTime;
			fireTableRowsInserted(0, keymap.size() - 1);
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(final int col) {
			Class ret = null;
			if (col == 0) {
				ret = String.class;
			} else if (col > 0 && col < 9) {
				ret = Integer.class;
			} else if (col < 12) {
				ret = Double.class;
			} else if (col == 12) {
				ret = TimeString.class;
			}
			return ret;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(final int col) {
			return columnNames[col];
		}

		@Override
		public int getRowCount() {
			return keymap.size();
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			final String key = keymap.get(row);
			if (key == null) {
				return null;
			}
			final ArchiveData d = datamap.get(key);
			switch (col) {
			case 0:
				return key;
			case 1:
				return d.nocomp < 0 ? null : d.nocomp;
			case 2:
				return d.eerrs < 0 ? null : d.eerrs;
			case 3:
				return d.efail < 0 ? null : d.efail;
			case 4:
				return d.aerrs < 0 ? null : d.aerrs;
			case 5:
				return d.afail < 0 ? null : d.afail;
			case 6:
				return d.timeout < 0 ? null : d.timeout;
			case 7:
				return d.ok < 0 ? null : d.ok;
			case 8:
				return d.total < 0 ? null : d.total;
			case 9:
				return d.minTime < 0 ? null : d.minTime;
			case 10:
				return d.maxTime < 0 ? null : d.maxTime;
			case 11:
				return d.aveTime < 0 ? null : d.aveTime;
			case 12:
				return new TimeString(d.totTime);
			default:
				return null;
			}
		}

		public void removeRows() {
			keymap.clear();
			datamap.clear();
		}

	}

	private static class DoubleRenderer extends DefaultTableCellRenderer {

		/**
		 * Apparently we need a version number because we are exending
		 * DefaultTableCellRenderer.
		 */
		private static final long serialVersionUID = 9217096188877846218L;

		private static final Color LIGHT_GREEN = new Color(0.8157f, 0.9059f,
				0.6275f);

		private final DecimalFormat formatter = new DecimalFormat("0.00");

		public DoubleRenderer() {
			super();
			setHorizontalAlignment(SwingConstants.RIGHT);
		}

		@Override
		public void setValue(final Object value) {
			setText(value == null ? "" : formatter.format(value));
			setBackground(DoubleRenderer.LIGHT_GREEN);
		}
	}

	private static class StringRenderer extends DefaultTableCellRenderer {

		/**
		 * Apparently we need a version number because we are exending
		 * DefaultTableCellRenderer.
		 */
		private static final long serialVersionUID = -2795234369794554187L;

		private static final Color LIGHT_CREAM = new Color(0.9059f, 0.8392f,
				0.6667f);

		public StringRenderer() {
			super();
			setHorizontalAlignment(SwingConstants.LEFT);
		}

		@Override
		public void setValue(final Object value) {
			setText(value == null ? "" : (String) value);
			setBackground(StringRenderer.LIGHT_CREAM);
		}
	}

	private static class TimeString {

		private final double value;

		public TimeString(final double value) {
			this.value = value;
		}

		@Override
		public String toString() {
			final long tt = (long) value;
			return value < 0 ? null : "" + tt / 60 + "m " + tt % 60 + "s";
		}

	}

	private static class TimeStringRenderer extends DefaultTableCellRenderer {

		/**
		 * Apparently we need a version number because we are exending
		 * DefaultTableCellRenderer.
		 */
		private static final long serialVersionUID = -2795234369794554187L;

		private static final Color LIGHT_GREEN = new Color(0.8157f, 0.9059f,
				0.6275f);

		public TimeStringRenderer() {
			super();
			setHorizontalAlignment(SwingConstants.RIGHT);
		}

		@Override
		public void setValue(final Object value) {
			setText(value == null ? "" : ((TimeString) value).toString());
			setBackground(TimeStringRenderer.LIGHT_GREEN);
		}
	}

	/**
	 * Apparently we need a version number because we are exending JPanel.
	 */
	private static final long serialVersionUID = 5637999515232549263L;

	private final ArchiveTableModel tableModel;

	private final JTable table;

	public TablePane() {
		super(new BorderLayout());
		tableModel = new ArchiveTableModel();
		table = new JTable(tableModel);
		table.setAutoCreateRowSorter(true);
		table.setDefaultRenderer(Double.class, new DoubleRenderer());
		table.setDefaultRenderer(String.class, new StringRenderer());
		table.setDefaultRenderer(TimeString.class, new TimeStringRenderer());
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i)
					.setPreferredWidth(i == 0 ? 100 : 50);
		}
		final JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
	}

	public void addData(final String name, final ArchiveData data) {
		tableModel.addData(name, data);
	}

	public void clear() {
		tableModel.removeRows();
	}

}

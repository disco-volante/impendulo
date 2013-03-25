package fmv;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DirectoryPane extends JSplitPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3366664508279486942L;

	private DirectoryEntry directoryEntry;
	private Directory directory;

	@SuppressWarnings("rawtypes")
	protected JList sourceList, directoryList;;

	public DirectoryPane() {
		super(JSplitPane.HORIZONTAL_SPLIT);
		createGUI();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createGUI() {
		final Dimension d = new Dimension(150, 400);

		directoryList = new JList();
		directoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		directoryList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					final int i = directoryList.getSelectedIndex();
					if (i != -1) {
						sourceList.setModel(directory.getModel(i));
						sourceList.setSelectedIndex(0);
						final ProjectData archive = directory.getArchive(i);
						if (archive.isCompiled()) {
							directory.setDiff(i, 0);
						}
					}
				}
			}
		});
		directoryEntry = new DirectoryEntry();
		directoryList.setCellRenderer(directoryEntry);
		final JScrollPane directoryListScrollPane = new JScrollPane(
				directoryList);
		directoryListScrollPane.setMinimumSize(d);
		directoryListScrollPane.setPreferredSize(d);
		setLeftComponent(directoryListScrollPane);

		sourceList = new JList();
		sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					final int i = directoryList.getSelectedIndex();
					if (i != -1) {
						final int j = sourceList.getSelectedIndex();
						if (j != -1) {
							directory.setDiff(i, j);
						}
					}
				}
			}
		});
		final JScrollPane sourcceListScrollPane = new JScrollPane(sourceList);
		sourcceListScrollPane.setMinimumSize(d);
		sourcceListScrollPane.setPreferredSize(d);
		setRightComponent(sourcceListScrollPane);

		setOneTouchExpandable(true);
		setResizeWeight(0);
		setDividerLocation(150);
	}

	public void setDirectory(final Directory dir) {
		directory = dir;
	}

}

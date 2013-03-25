package fmv;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

@SuppressWarnings("rawtypes")
public class DirectoryEntry extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1317074593620304250L;

	private final ImageIcon openIcon = new ImageIcon("images/compiled.gif");

	private final ImageIcon closedIcon = new ImageIcon("images/uncompiled.gif");

	public DirectoryEntry() {
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getListCellRendererComponent(final JList list,
			final Object value, final int index, final boolean isSelected,
			final boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setHorizontalAlignment(SwingConstants.LEFT);
		setFont(list.getFont());
		if (value instanceof ProjectData) {
			final ProjectData a = (ProjectData) value;
			setText(a.toString());
			setIcon(a.isCompiled() ? openIcon : closedIcon);
		} else if (value instanceof String) {
			final String s = (String) value;
			setText(s);
			setIcon(closedIcon);
		}
		return this;
	}

}

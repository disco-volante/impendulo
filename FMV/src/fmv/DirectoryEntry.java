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

	private ImageIcon openIcon = new ImageIcon("images/compiled.gif");

	private ImageIcon closedIcon = new ImageIcon("images/uncompiled.gif");

	public DirectoryEntry() {
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setHorizontalAlignment(SwingConstants.LEFT);
		setFont(list.getFont());
		if (value instanceof Archive) {
			Archive a = (Archive) value;
			setText(a.toString());
			setIcon(a.isCompiled() ? openIcon : closedIcon);
		}else if(value instanceof String){
			String s = (String) value;
			setText(s);
			setIcon(closedIcon);
		}
		return this;
	}

}

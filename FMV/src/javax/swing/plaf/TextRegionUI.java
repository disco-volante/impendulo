package javax.swing.plaf;

import javax.swing.JComponent;
import javax.swing.TextRegion;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.RegionView;
import javax.swing.text.View;
import javax.swing.text.WrappedPlainView;

public class TextRegionUI extends BasicTextAreaUI {

	/**
	 * Creates a UI for a JTextArea.
	 * 
	 * @param ta
	 *            a text area
	 * @return the UI
	 */
	public static ComponentUI createUI(JComponent ta) {
		return new TextRegionUI();
	}

	/**
	 * Fetches the name used as a key to look up properties through the
	 * UIManager. This is used as a prefix to all the standard text properties.
	 * 
	 * @return the name ("TextArea")
	 */
	protected String getPropertyPrefix() {
		return "TextRegion";
	}

	/**
	 * Creates the view for an element. Returns a WrappedPlainView or PlainView.
	 * 
	 * @param elem
	 *            the element
	 * @return the view
	 */
	public View create(Element elem) {
		JTextComponent c = getComponent();
		if (c instanceof TextRegion) {
			TextRegion area = (TextRegion) c;
			View v;
			if (area.getLineWrap()) {
				v = new WrappedPlainView(elem, area.getWrapStyleWord());
			} else {
				v = new RegionView(elem);
			}
			return v;
		}
		return null;
	}

}

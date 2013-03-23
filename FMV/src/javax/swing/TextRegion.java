package javax.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

public class TextRegion extends JTextArea {

	private static final long serialVersionUID = 1280941363515217364L;

	public static final SimpleAttributeSet changed = new SimpleAttributeSet();

	public static final SimpleAttributeSet delta = new SimpleAttributeSet();

	static {
		TextRegion.changed.addAttribute("name", "changed");
		TextRegion.delta.addAttribute("name", "delta");
	}

	public TextRegion(final DefaultStyledDocument doc) {
		super(doc);
	}

	public void append(final String str, final AttributeSet attr) {
		final Document doc = getDocument();
		if (doc != null) {
			try {
				doc.insertString(doc.getLength(), str, attr);
			} catch (final BadLocationException e) {
			}
		}
	}

	public void clearDoc() {
		final Document doc = getDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}

	/*
	 * public void insert(String str, int pos, String style) { Document doc =
	 * getDocument(); if (doc != null) { try { doc.insertString(pos, str, null);
	 * } catch (BadLocationException e) { throw new
	 * IllegalArgumentException(e.getMessage()); } } }
	 */

}

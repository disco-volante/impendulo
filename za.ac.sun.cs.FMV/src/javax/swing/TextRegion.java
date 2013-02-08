package javax.swing;

import javax.swing.JTextArea;
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
		changed.addAttribute("name", "changed");
		delta.addAttribute("name", "delta");
	}

	public TextRegion(DefaultStyledDocument doc) {
		super(doc);
	}

	public void clearDoc() {
		Document doc = getDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void append(String str, AttributeSet attr) {
        Document doc = getDocument();
        if (doc != null) {
            try {
                doc.insertString(doc.getLength(), str, attr);
            } catch (BadLocationException e) {
            }
        }
	}

	/*
	 * public void insert(String str, int pos, String style) { Document doc =
	 * getDocument(); if (doc != null) { try { doc.insertString(pos, str, null);
	 * } catch (BadLocationException e) { throw new
	 * IllegalArgumentException(e.getMessage()); } } }
	 */

}

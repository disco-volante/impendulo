package javax.swing.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainView;
import javax.swing.text.TabExpander;
import javax.swing.text.View;

public class RegionView extends PlainView implements TabExpander {

	protected Color verylightgray = new Color(240, 240, 240);

	public RegionView(Element elem) {
		super(elem);
	}

	private void paintElement(Graphics g, Element e, Segment s, int x, int y) {
		int flushLen = 0;
		int flushIndex = s.offset;
		char[] txt = s.array;
		int n = s.offset + s.count;
		AttributeSet attr = e.getAttributes();
		g.setColor("delta".equals(attr.getAttribute("name")) ? Color.red : "changed".equals(attr.getAttribute("name")) ? Color.magenta : Color.black);
		for (int i = s.offset; i < n; i++) {
			if (txt[i] == '\t') {
				if (flushLen > 0) {
					g.drawChars(txt, flushIndex, flushLen, x, y);
					x += metrics.charsWidth(txt, flushIndex, flushLen);
					flushLen = 0;
				}
				flushIndex = i + 1;
				if (txt[i] == '\t') {
					if (e != null) {
						x = (int) this.nextTabStop((float) x, i);
					} else {
						x += metrics.charWidth(' ');
					}
				} else if (txt[i] == ' ') {
					x += metrics.charWidth(' ') + 1;
				}
			} else if ((txt[i] == '\n') || (txt[i] == '\r')) {
				if (flushLen > 0) {
					g.drawChars(txt, flushIndex, flushLen, x, y);
					x += metrics.charsWidth(txt, flushIndex, flushLen);
					flushLen = 0;
				}
				flushIndex = i + 1;
			} else {
				flushLen += 1;
			}
		}
		if (flushLen > 0) {
			g.drawChars(txt, flushIndex, flushLen, x, y);
		}
	}

	/**
	 * Renders using the given rendering surface and area on that surface. The
	 * view may need to do layout and create child views to enable itself to
	 * render into the given allocation.
	 * 
	 * @param g
	 *            the rendering surface to use
	 * @param a
	 *            the allocated region to render into
	 * 
	 * @see View#paint
	 */
	public void paint(Graphics g, Shape a) {
		Rectangle alloc = (Rectangle) a;
		JTextComponent host = (JTextComponent) getContainer();
		g.setFont(host.getFont());
		Rectangle clip = g.getClipBounds();
		int fontHeight = metrics.getHeight();
		int heightBelow = (alloc.y + alloc.height) - (clip.y + clip.height);
		int linesBelow = Math.max(0, heightBelow / fontHeight);
		int heightAbove = clip.y - alloc.y;
		int linesAbove = Math.max(0, heightAbove / fontHeight - 1);
		int linesTotal = alloc.height / fontHeight;
		if (alloc.height % fontHeight != 0) {
			linesTotal++;
		}
		Rectangle lineArea = lineToRect(a, linesAbove);
		int y = lineArea.y + metrics.getAscent();
		int x = lineArea.x;
		Element map = getElement();
		int endLine = Math.min(map.getElementCount(), linesTotal - linesBelow);
		Document doc = getDocument();
		for (int line = linesAbove; line <= endLine; line++) {
			g.setColor((line % 2 == 0) ? Color.white : verylightgray);
			g.fillRect(x, y - fontHeight + 3, alloc.width, fontHeight);
			try {
				Element elem = map.getElement(line);
				if (elem != null) {
					Segment s = new Segment();
					if (elem.isLeaf()) {
						int n = elem.getEndOffset() - elem.getStartOffset();
						doc.getText(elem.getStartOffset(), n, s);
						paintElement(g, elem, s, x, y);
					}
					else {
						int count = elem.getElementCount();
						for(int i = 0; i < count; i++) {
							Element nest = elem.getElement(i);
							int n = nest.getEndOffset() - nest.getStartOffset();
							doc.getText(nest.getStartOffset(), n, s);
							paintElement(g, nest, s, x, y);
						}
					}
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			y += fontHeight;
		}
	}

}

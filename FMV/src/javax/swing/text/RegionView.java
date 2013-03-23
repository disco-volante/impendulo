package javax.swing.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

public class RegionView extends PlainView implements TabExpander {

	protected Color verylightgray = new Color(240, 240, 240);

	public RegionView(final Element elem) {
		super(elem);
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
	@Override
	public void paint(final Graphics g, final Shape a) {
		final Rectangle alloc = (Rectangle) a;
		final JTextComponent host = (JTextComponent) getContainer();
		g.setFont(host.getFont());
		final Rectangle clip = g.getClipBounds();
		final int fontHeight = metrics.getHeight();
		final int heightBelow = alloc.y + alloc.height - (clip.y + clip.height);
		final int linesBelow = Math.max(0, heightBelow / fontHeight);
		final int heightAbove = clip.y - alloc.y;
		final int linesAbove = Math.max(0, heightAbove / fontHeight - 1);
		int linesTotal = alloc.height / fontHeight;
		if (alloc.height % fontHeight != 0) {
			linesTotal++;
		}
		final Rectangle lineArea = lineToRect(a, linesAbove);
		int y = lineArea.y + metrics.getAscent();
		final int x = lineArea.x;
		final Element map = getElement();
		final int endLine = Math.min(map.getElementCount(), linesTotal
				- linesBelow);
		final Document doc = getDocument();
		for (int line = linesAbove; line <= endLine; line++) {
			g.setColor(line % 2 == 0 ? Color.white : verylightgray);
			g.fillRect(x, y - fontHeight + 3, alloc.width, fontHeight);
			try {
				final Element elem = map.getElement(line);
				if (elem != null) {
					final Segment s = new Segment();
					if (elem.isLeaf()) {
						final int n = elem.getEndOffset()
								- elem.getStartOffset();
						doc.getText(elem.getStartOffset(), n, s);
						paintElement(g, elem, s, x, y);
					} else {
						final int count = elem.getElementCount();
						for (int i = 0; i < count; i++) {
							final Element nest = elem.getElement(i);
							final int n = nest.getEndOffset()
									- nest.getStartOffset();
							doc.getText(nest.getStartOffset(), n, s);
							paintElement(g, nest, s, x, y);
						}
					}
				}
			} catch (final BadLocationException e) {
				e.printStackTrace();
			}
			y += fontHeight;
		}
	}

	private void paintElement(final Graphics g, final Element e,
			final Segment s, int x, final int y) {
		int flushLen = 0;
		int flushIndex = s.offset;
		final char[] txt = s.array;
		final int n = s.offset + s.count;
		final AttributeSet attr = e.getAttributes();
		g.setColor("delta".equals(attr.getAttribute("name")) ? Color.red
				: "changed".equals(attr.getAttribute("name")) ? Color.magenta
						: Color.black);
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
						x = (int) nextTabStop(x, i);
					} else {
						x += metrics.charWidth(' ');
					}
				} else if (txt[i] == ' ') {
					x += metrics.charWidth(' ') + 1;
				}
			} else if (txt[i] == '\n' || txt[i] == '\r') {
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

}

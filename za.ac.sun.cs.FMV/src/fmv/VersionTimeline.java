package fmv;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import javax.swing.JComponent;

public class VersionTimeline extends JComponent implements ActionListener,
		MouseListener {

	/**
	 * Apparently we need a version number because we are exending JDialog.
	 */
	private static final long serialVersionUID = -7221372214235334011L;

	private static final Font graphFont = new Font(Font.SANS_SERIF, Font.PLAIN,
			12);

	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	/**
	 * Size of the circles that represent versions.
	 */
	private static final int RADIUS = 5;

	/**
	 * Size of the inner circles to represent errors.
	 */
	private static final int SEMIRADIUS = 2;

	/**
	 * Borders at the sides of the timeline.
	 */
	private static final int BORDER = RADIUS + 3;

	/**
	 * Size of the dashes.
	 */
	private static final int DASH_SIZE = 5;

	/**
	 * Size of the spaces between the dashes.
	 */
	private static final int DASH_GAP = 3;

	/**
	 * The source file that this timeline is representing.
	 */
	private Source source;

	private Archive archive;

	/**
	 * Whether or not the timeline is flat or has some height.
	 */
	private boolean isFlat = false;

	/**
	 * The time of the first version of the current source file.
	 */
	private long firstTime = 0;

	/**
	 * The time of the last version of the current source file.
	 */
	private long finalTime = 0;

	/**
	 * The time from the first to the final time.
	 */
	private long timeSpan = 0;

	/**
	 * The current time selected.
	 */
	private long currTime = 0;

	private Map.Entry<Date, Version> leftVersion = null, rightVersion = null;

	/**
	 * Construct a new timeline. If this timeline is flat, we make it sensitive
	 * to mouse events.
	 * 
	 * @param isFlat
	 *            whether or not this timeline is flat
	 */
	public VersionTimeline(boolean isFlat) {
		this.isFlat = isFlat;
		if (isFlat) {
			addMouseListener(this);
		} else {
			setToolTipText("date");
		}
	}

	public void setSource(Archive archive, Source source) {
		this.archive = archive;
		this.source = source;
		firstTime = source.getVersions().firstKey().getTime();
		finalTime = source.getVersions().lastKey().getTime();
		timeSpan = finalTime - firstTime;
		repaint();
		if (timeSpan > 0) {
			setDate(new Date(this.source.getVersions().firstKey().getTime() + 1));
		}
	}

	public void paintComponent(Graphics g) {
		if (source == null) {
			return;
		}
		int h = getHeight();
		int w = getWidth();
		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);
		g.setColor(Color.black);
		int firstX = BORDER;
		int finalX = w - BORDER;
		int xSpan = finalX - firstX;
		int midY = h / 2;
		if (isFlat) {
			g.drawLine(firstX, midY, finalX, midY);
			if (timeSpan > 0) {
				if ((currTime >= firstTime) && (currTime <= finalTime)) {
					int x = (int) (firstX + xSpan * (currTime - firstTime)
							/ timeSpan);
					g.drawLine(x, 0, x, h - 1);
				}
				for (Map.Entry<Date, Version> e : source.getVersions()
						.entrySet()) {
					int x = (int) (firstX + xSpan
							* (e.getKey().getTime() - firstTime) / timeSpan);
					Version v = e.getValue();
					g.setColor(v.getStatus().getColor());
					g.fillOval(x - RADIUS, midY - RADIUS, RADIUS * 2,
							RADIUS * 2);
					int wc = v.getWarningCount();
					if (wc > 0) {
						g.setColor(Color.black);
						// g.drawLine(x - RADIUS, midY, x + RADIUS, midY);
						g.drawLine(x, midY - RADIUS, x, midY + RADIUS);
					}
					Status s = v.getStatus();
					if ((s == Status.E_ERRS) || (s == Status.A_ERRS)) {
						g.setColor(Color.red);
						g.fillOval(x - SEMIRADIUS, midY - SEMIRADIUS,
								SEMIRADIUS * 2, SEMIRADIUS * 2);
					}
					g.setColor(Color.black);
					g.drawOval(x - RADIUS, midY - RADIUS, RADIUS * 2,
							RADIUS * 2);
				}
			}
		} else {
			if (timeSpan > 0) {
				g.setFont(graphFont);
				g.setColor(Color.darkGray);
				for (Status s : Status.values()) {
					if (s == Status.UNKNOWN) {
						continue;
					}
					int x = 0 + DASH_SIZE, y = (int) (h * s.getY());
					while (x < w) {
						g.drawLine(x - DASH_SIZE, y, x, y);
						x += DASH_SIZE + DASH_GAP;
					}
					if (x - DASH_SIZE < w) {
						g.drawLine(x - DASH_SIZE, y, finalX, y);
					}
					g.drawString(s.getMessage(), 1, y - 1);
				}
				FontMetrics fm = g.getFontMetrics();
				int dh = fm.getAscent(); // + fm.getDescent();
				String d = sdf.format(new Date(firstTime));
				g.drawString(d, firstX, dh + 2);
				d = sdf.format(new Date(finalTime));
				int dw = g.getFontMetrics().stringWidth(d);
				g.drawString(d, finalX - dw - 2, dh + 2);
				d = sdf.format(new Date((firstTime + finalTime) / 2));
				dw = g.getFontMetrics().stringWidth(d);
				g.drawString(d, (finalX + firstX - dw) / 2, dh + 2);
				g.setColor(Color.black);
				int px = -1, py = 0;
				for (Map.Entry<Date, Version> e : source.getVersions()
						.entrySet()) {
					int x = (int) (firstX + xSpan
							* (e.getKey().getTime() - firstTime) / timeSpan);
					Version v = e.getValue();
					int y = (int) (h * v.getStatus().getY());
					if (px != -1) {
						g.drawLine(px, py, x, y);
					}
					px = x;
					py = y;
				}
				for (Map.Entry<Date, Version> e : source.getVersions()
						.entrySet()) {
					int x = (int) (firstX + xSpan
							* (e.getKey().getTime() - firstTime) / timeSpan);
					Version v = e.getValue();
					int y = (int) (h * v.getStatus().getY());
					g.setColor(v.getStatus().getColor());
					g.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
					Status s = v.getStatus();
					if ((s == Status.E_ERRS) || (s == Status.A_ERRS)) {
						g.setColor(Color.red);
						g.fillOval(x - SEMIRADIUS, y - SEMIRADIUS,
								SEMIRADIUS * 2, SEMIRADIUS * 2);
					}
					g.setColor(Color.black);
					g.drawOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
				}
			}
		}
	}

	public boolean contains(int x, int y) {
		int h = getHeight();
		int w = getWidth();
		if (isFlat) {
			return (x >= 0) && (x < w) && (y >= 0) && (y < h);
		}
		if ((source != null) && (timeSpan > 0)) {
			int firstX = BORDER;
			int finalX = w - BORDER;
			int xSpan = finalX - firstX;
			NavigableMap<Date, Version> m = source.getVersions();
			for (Map.Entry<Date, Version> e = m.lastEntry(); e != null; e = m
					.lowerEntry(e.getKey())) {
				int ex = (int) (firstX + xSpan
						* (e.getKey().getTime() - firstTime) / timeSpan);
				if (x > ex + RADIUS) {
					return false;
				}
				if (x < ex - RADIUS) {
					continue;
				}
				int ey = (int) (h * e.getValue().getStatus().getY());
				if (y > ey + RADIUS) {
					continue;
				}
				if (y < ey - RADIUS) {
					continue;
				}
				setToolTipText("<html>" + sdf.format(e.getKey()) + "<br/>"
						+ e.getValue().getStatus().getMessage() + "<br/>"
						+ e.getValue().getAnnotation() + "</html>");
				return true;
			}
		}
		return false;
	}

	public void setDate(Date date) {
		leftVersion = source.getVersions().floorEntry(date);
		rightVersion = source.getVersions().ceilingEntry(date);
		if ((leftVersion == null) && (rightVersion == null)) {
			return;
		} else if (leftVersion == null) {
			currTime = ((firstTime + rightVersion.getKey().getTime()) / 2);
			source.showItem(false, rightVersion, null);
			source.showEmpty(true);
		} else if (rightVersion == null) {
			currTime = ((finalTime + leftVersion.getKey().getTime()) / 2);
			source.showItem(true, leftVersion, null);
			source.showEmpty(false);
		} else {
			currTime = ((leftVersion.getKey().getTime() + rightVersion.getKey()
					.getTime()) / 2);
			List<DiffAction> diffs = rightVersion.getValue().getDiff();
			source.showItem(true, leftVersion, diffs);
			source.showItem(false, rightVersion, diffs);
		}
		repaint();
		FMV.diffPane.scrollToTop();
	}

	public void showPrev() {
		if (leftVersion != null) {
			setDate(new Date(leftVersion.getKey().getTime() - 1));
		}
	}

	public void showNext() {
		if (rightVersion != null) {
			setDate(new Date(rightVersion.getKey().getTime() + 1));
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (source != null) {
			setDate(new Date(((long) (firstTime + timeSpan
					* (e.getX() - BORDER) / (getWidth() - 2 * BORDER)))));
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		if ("leftshow".equals(e.getActionCommand())) {
			if (leftVersion != null) {
				FMV.output.activate(leftVersion.getValue().getOutput());
			}
		} else if ("leftfb".equals(e.getActionCommand())) {
			if (leftVersion != null) {
				FMV.output.activate(leftVersion.getValue().getReport());
			}
		} else if ("rightshow".equals(e.getActionCommand())) {
			if (rightVersion != null) {
				FMV.output.activate(rightVersion.getValue().getOutput());
			}
		} else if ("rightfb".equals(e.getActionCommand())) {
			if (rightVersion != null) {
				FMV.output.activate(rightVersion.getValue().getReport());
			}
		} else if ("leftedit".equals(e.getActionCommand())) {
			if (leftVersion != null) {
				FMV.annotateDialog.activate(archive, source, leftVersion);
			}
		} else if ("rightedit".equals(e.getActionCommand())) {
			if (rightVersion != null) {
				FMV.annotateDialog.activate(archive, source, rightVersion);
			}
		}
	}

}
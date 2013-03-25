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
	private static final int BORDER = VersionTimeline.RADIUS + 3;

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

	private ProjectData archive;

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
	public VersionTimeline(final boolean isFlat) {
		this.isFlat = isFlat;
		if (isFlat) {
			addMouseListener(this);
		} else {
			setToolTipText("date");
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if ("leftshow".equals(e.getActionCommand())) {
			if (leftVersion != null) {
				FMV.getDialog().activate(leftVersion.getValue().getOutput());
			}
		} else if ("rightshow".equals(e.getActionCommand())) {
			if (rightVersion != null) {
				FMV.getDialog().activate(rightVersion.getValue().getOutput());
			}
		} else if ("leftedit".equals(e.getActionCommand())) {
			if (leftVersion != null) {
				FMV.annotateDialog.activate(archive, source, leftVersion);
			}
		} else if ("rightedit".equals(e.getActionCommand())) {
			if (rightVersion != null) {
				FMV.annotateDialog.activate(archive, source, rightVersion);
			}
		} else if ("tool".equals(e.getActionCommand())) {
			final String tool = FMV.diffPane.getCurrentTool();
			String left = "", right = "";
			if (leftVersion != null) {
				left = leftVersion.getValue().getReport(tool);
			}
			if (rightVersion != null) {
				right = rightVersion.getValue().getReport(tool);
			}
			FMV.getSplitDialog()
					.setContents(SplitDialog.LEFT,
							leftVersion.getKey().toString(), left)
					.setContents(SplitDialog.RIGHT,
							rightVersion.getKey().toString(), right)
					.activate(tool + " Report");
		}
	}

	@Override
	public boolean contains(final int x, final int y) {
		final int h = getHeight();
		final int w = getWidth();
		if (isFlat) {
			return x >= 0 && x < w && y >= 0 && y < h;
		}
		if (source != null && timeSpan > 0) {
			final int firstX = VersionTimeline.BORDER;
			final int finalX = w - VersionTimeline.BORDER;
			final int xSpan = finalX - firstX;
			final NavigableMap<Date, Version> m = source.getVersions();
			for (Map.Entry<Date, Version> e = m.lastEntry(); e != null; e = m
					.lowerEntry(e.getKey())) {
				final int ex = (int) (firstX + xSpan
						* (e.getKey().getTime() - firstTime) / timeSpan);
				if (x > ex + VersionTimeline.RADIUS) {
					return false;
				}
				if (x < ex - VersionTimeline.RADIUS) {
					continue;
				}
				final int ey = (int) (h * e.getValue().getStatus().getY());
				if (y > ey + VersionTimeline.RADIUS) {
					continue;
				}
				if (y < ey - VersionTimeline.RADIUS) {
					continue;
				}
				setToolTipText("<html>"
						+ VersionTimeline.sdf.format(e.getKey()) + "<br/>"
						+ e.getValue().getStatus().getMessage() + "<br/>"
						+ e.getValue().getAnnotation() + "</html>");
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (source != null) {
			setDate(new Date(firstTime + timeSpan
					* (e.getX() - VersionTimeline.BORDER)
					/ (getWidth() - 2 * VersionTimeline.BORDER)));
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (source == null) {
			return;
		}
		final int h = getHeight();
		final int w = getWidth();
		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);
		g.setColor(Color.black);
		final int firstX = VersionTimeline.BORDER;
		final int finalX = w - VersionTimeline.BORDER;
		final int xSpan = finalX - firstX;
		final int midY = h / 2;
		if (isFlat) {
			g.drawLine(firstX, midY, finalX, midY);
			if (timeSpan > 0) {
				if (currTime >= firstTime && currTime <= finalTime) {
					final int x = (int) (firstX + xSpan
							* (currTime - firstTime) / timeSpan);
					g.drawLine(x, 0, x, h - 1);
				}
				for (final Map.Entry<Date, Version> e : source.getVersions()
						.entrySet()) {
					final int x = (int) (firstX + xSpan
							* (e.getKey().getTime() - firstTime) / timeSpan);
					final Version v = e.getValue();
					g.setColor(v.getStatus().getColor());
					g.fillOval(x - VersionTimeline.RADIUS, midY
							- VersionTimeline.RADIUS,
							VersionTimeline.RADIUS * 2,
							VersionTimeline.RADIUS * 2);
					final Status s = v.getStatus();
					if (s == Status.E_ERRS || s == Status.A_ERRS) {
						g.setColor(Color.red);
						g.fillOval(x - VersionTimeline.SEMIRADIUS, midY
								- VersionTimeline.SEMIRADIUS,
								VersionTimeline.SEMIRADIUS * 2,
								VersionTimeline.SEMIRADIUS * 2);
					}
					g.setColor(Color.black);
					g.drawOval(x - VersionTimeline.RADIUS, midY
							- VersionTimeline.RADIUS,
							VersionTimeline.RADIUS * 2,
							VersionTimeline.RADIUS * 2);
				}
			}
		} else {
			if (timeSpan > 0) {
				g.setFont(VersionTimeline.graphFont);
				g.setColor(Color.darkGray);
				for (final Status s : Status.values()) {
					if (s == Status.UNKNOWN) {
						continue;
					}
					int x = 0 + VersionTimeline.DASH_SIZE;
					final int y = (int) (h * s.getY());
					while (x < w) {
						g.drawLine(x - VersionTimeline.DASH_SIZE, y, x, y);
						x += VersionTimeline.DASH_SIZE
								+ VersionTimeline.DASH_GAP;
					}
					if (x - VersionTimeline.DASH_SIZE < w) {
						g.drawLine(x - VersionTimeline.DASH_SIZE, y, finalX, y);
					}
					g.drawString(s.getMessage(), 1, y - 1);
				}
				final FontMetrics fm = g.getFontMetrics();
				final int dh = fm.getAscent();
				String d = VersionTimeline.sdf.format(new Date(firstTime));
				g.drawString(d, firstX, dh + 2);
				d = VersionTimeline.sdf.format(new Date(finalTime));
				int dw = g.getFontMetrics().stringWidth(d);
				g.drawString(d, finalX - dw - 2, dh + 2);
				d = VersionTimeline.sdf.format(new Date(
						(firstTime + finalTime) / 2));
				dw = g.getFontMetrics().stringWidth(d);
				g.drawString(d, (finalX + firstX - dw) / 2, dh + 2);
				g.setColor(Color.black);
				int px = -1, py = 0;
				for (final Map.Entry<Date, Version> e : source.getVersions()
						.entrySet()) {
					final int x = (int) (firstX + xSpan
							* (e.getKey().getTime() - firstTime) / timeSpan);
					final Version v = e.getValue();
					final int y = (int) (h * v.getStatus().getY());
					if (px != -1) {
						g.drawLine(px, py, x, y);
					}
					px = x;
					py = y;
				}
				for (final Map.Entry<Date, Version> e : source.getVersions()
						.entrySet()) {
					final int x = (int) (firstX + xSpan
							* (e.getKey().getTime() - firstTime) / timeSpan);
					final Version v = e.getValue();
					final int y = (int) (h * v.getStatus().getY());
					g.setColor(v.getStatus().getColor());
					g.fillOval(x - VersionTimeline.RADIUS, y
							- VersionTimeline.RADIUS,
							VersionTimeline.RADIUS * 2,
							VersionTimeline.RADIUS * 2);
					final Status s = v.getStatus();
					if (s == Status.E_ERRS || s == Status.A_ERRS) {
						g.setColor(Color.red);
						g.fillOval(x - VersionTimeline.SEMIRADIUS, y
								- VersionTimeline.SEMIRADIUS,
								VersionTimeline.SEMIRADIUS * 2,
								VersionTimeline.SEMIRADIUS * 2);
					}
					g.setColor(Color.black);
					g.drawOval(x - VersionTimeline.RADIUS, y
							- VersionTimeline.RADIUS,
							VersionTimeline.RADIUS * 2,
							VersionTimeline.RADIUS * 2);
				}
			}
		}
	}

	public void setDate(final Date date) {
		leftVersion = source.getVersions().floorEntry(date);
		rightVersion = source.getVersions().ceilingEntry(date);
		if (leftVersion == null && rightVersion == null) {
			return;
		} else if (leftVersion == null) {
			currTime = (firstTime + rightVersion.getKey().getTime()) / 2;
			source.showItem(false, rightVersion, null);
			source.showEmpty(true);
		} else if (rightVersion == null) {
			currTime = (finalTime + leftVersion.getKey().getTime()) / 2;
			source.showItem(true, leftVersion, null);
			source.showEmpty(false);
		} else {
			currTime = (leftVersion.getKey().getTime() + rightVersion.getKey()
					.getTime()) / 2;
			final List<DiffAction> diffs = rightVersion.getValue().getDiff();
			source.showItem(true, leftVersion, diffs);
			source.showItem(false, rightVersion, diffs);
		}
		repaint();
		FMV.diffPane.scrollToTop();
	}

	public void setSource(final ProjectData archive, final Source source) {
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

	public void showNext() {
		if (rightVersion != null) {
			setDate(new Date(rightVersion.getKey().getTime() + 1));
		}
	}

	public void showPrev() {
		if (leftVersion != null) {
			setDate(new Date(leftVersion.getKey().getTime() - 1));
		}
	}

}
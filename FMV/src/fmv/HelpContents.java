package fmv;

import java.awt.Container;
import java.awt.Cursor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

public class HelpContents extends JFrame {

	private class HtmlPane extends JScrollPane implements HyperlinkListener {

		/**
		 * temporary class that loads synchronously (although later than the
		 * request so that a cursor change can be done).
		 */
		private class PageLoader implements Runnable {

			private URL url;

			private final Cursor cursor;

			PageLoader(final URL url, final Cursor cursor) {
				this.url = url;
				this.cursor = cursor;
			}

			@Override
			public void run() {
				if (url == null) {
					html.setCursor(cursor);
					final Container parent = html.getParent();
					parent.repaint();
				} else {
					final Document doc = html.getDocument();
					try {
						html.setPage(url);
					} catch (final IOException ioe) {
						html.setDocument(doc);
						getToolkit().beep();
					} finally {
						url = null;
						SwingUtilities.invokeLater(this);
					}
				}
			}

		}

		/**
		 * Apparently we need a version number because we are exending
		 * JScrollPane.
		 */
		private static final long serialVersionUID = -7130829246091721025L;

		private JEditorPane html;

		public HtmlPane() {
			try {
				final URL url = Thread.currentThread().getContextClassLoader()
						.getResource("helpfiles/index.html");
				html = new JEditorPane(url);
				html.setEditable(false);
				html.addHyperlinkListener(this);
				html.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
						Boolean.TRUE);
				final JViewport vp = getViewport();
				vp.add(html);
			} catch (final MalformedURLException e) {
				System.out.println("Malformed URL: " + e);
			} catch (final IOException e) {
				System.out.println("IOException: " + e);
			}
		}

		/**
		 * Notification of a change relative to a hyperlink.
		 */
		@Override
		public void hyperlinkUpdate(final HyperlinkEvent event) {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				linkActivated(event.getURL());
			}
		}

		/**
		 * Follows the reference in an link. The given url is the requested
		 * reference. By default this calls <a href="#setPage">setPage</a>, and
		 * if an exception is thrown the original previous document is restored
		 * and a beep sounded. If an attempt was made to follow a link, but it
		 * represented a malformed url, this method will be called with a null
		 * argument.
		 * 
		 * @param u
		 *            the URL to follow
		 */
		protected void linkActivated(final URL u) {
			final Cursor c = html.getCursor();
			final Cursor waitCursor = Cursor
					.getPredefinedCursor(Cursor.WAIT_CURSOR);
			html.setCursor(waitCursor);
			SwingUtilities.invokeLater(new PageLoader(u, c));
		}
	}

	/**
	 * Apparently we need a version number because we are exending JFrame.
	 */
	private static final long serialVersionUID = -5104152410192182582L;

	public HelpContents(final JFrame parent) {
		super("FMV Help");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(parent);
		setIconImage(FMV.getMyImage("fmv.gif"));
		final HtmlPane html = new HtmlPane();
		setContentPane(html);
	}

}

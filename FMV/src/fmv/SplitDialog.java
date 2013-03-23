package fmv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class SplitDialog extends JDialog {

	static class Report extends JPanel {
		private static final long serialVersionUID = -8175231359242624865L;
		private final JTextArea text;
		private final JLabel label;

		public Report() {
			super(new BorderLayout());
			text = new JTextArea(20, 50);
			text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			text.setBorder(BorderFactory.createLineBorder(Color.black));
			final JScrollPane scroll = new JScrollPane(text);
			label = new JLabel();
			add(label, BorderLayout.NORTH);
			add(scroll, BorderLayout.CENTER);
		}
	}

	/**
	 * Apparently we need a version number because we are exending JDialog.
	 */
	private static final long serialVersionUID = -3623944591434750373L;
	/**
	 * Text component for display the actual text.
	 */
	public static final int LEFT = 0;

	public static final int RIGHT = 1;

	private final Report left, right;

	/**
	 * Construct a dialog for showing the compiler/tester output.
	 * 
	 * @param parent
	 *            the parent frame
	 */
	public SplitDialog(final JFrame parent) {
		super(parent, true);
		left = new Report();
		right = new Report();
		final JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, left, right);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0);
		final JPanel obuttons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		final JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				deactivate();
			}

		});
		obuttons.add(okButton, BorderLayout.PAGE_END);
		final JPanel opane = new JPanel(new BorderLayout());
		opane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		opane.add(splitPane, BorderLayout.CENTER);
		opane.add(obuttons, BorderLayout.PAGE_END);
		add(opane, BorderLayout.CENTER);
	}

	/**
	 * Activate the dialog by making it visible.
	 * 
	 * @param text
	 *            text to insert into dialog
	 */
	public void activate(final String title) {
		setTitle(title);
		pack();
		setVisible(true);
	}

	/**
	 * Deactivate the dialog by making it invisible.
	 */
	public void deactivate() {
		setVisible(false);
		dispose();
	}

	public SplitDialog setContents(final int side, final String title,
			final String text) {
		Report report;
		if (side == SplitDialog.LEFT) {
			report = left;
		} else {
			report = right;
		}
		try {
			final Document doc = report.text.getDocument();
			doc.remove(0, doc.getLength());
			doc.insertString(0, text, null);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
		report.label.setText(title);
		return this;
	}

}

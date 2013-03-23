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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class OutputDialog extends JDialog {

	/**
	 * Apparently we need a version number because we are exending JDialog.
	 */
	private static final long serialVersionUID = -3623944591434750373L;

	/**
	 * Text component for display the actual text.
	 */
	private static JTextArea outputText;

	/**
	 * Construct a dialog for showing the compiler/tester output.
	 * 
	 * @param parent
	 *            the parent frame
	 */
	public OutputDialog(final JFrame parent) {
		super(parent, "Output", true);

		final JPanel opane = new JPanel(new BorderLayout());
		opane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		OutputDialog.outputText = new JTextArea(20, 140);
		OutputDialog.outputText.setFont(new Font(Font.MONOSPACED, Font.PLAIN,
				12));
		OutputDialog.outputText.setBorder(BorderFactory
				.createLineBorder(Color.black));
		final JScrollPane oscroll = new JScrollPane(OutputDialog.outputText);
		opane.add(oscroll, BorderLayout.CENTER);
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
		opane.add(obuttons, BorderLayout.PAGE_END);

		add(opane, BorderLayout.CENTER);
	}

	/**
	 * Activate the dialog by making it visible.
	 * 
	 * @param text
	 *            text to insert into dialog
	 */
	public void activate(final String text) {
		final Document doc = OutputDialog.outputText.getDocument();
		try {
			doc.remove(0, doc.getLength());
			doc.insertString(0, text, null);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
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

}

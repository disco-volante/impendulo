package fmv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class AnnotateDialog extends JDialog {

	/**
	 * Apparently we need a version number because we are exending JDialog.
	 */
	private static final long serialVersionUID = 8098993729076143997L;

	/**
	 * Text component for editing the annotation text.
	 */
	private final JTextArea noteText;

	private ProjectData archive;

	private Source source;

	private Map.Entry<Date, Version> version;

	/**
	 * Construct a dialog for showing the compiler/tester output.
	 * 
	 * @param parent
	 *            the parent frame
	 */
	public AnnotateDialog(final JFrame parent) {
		super(parent, "Edit annotation", true);

		// Create a pane with a text and OK button.
		final JPanel opane = new JPanel(new BorderLayout());
		opane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		noteText = new JTextArea(5, 60);
		noteText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		noteText.setBorder(BorderFactory.createLineBorder(Color.black));
		final JScrollPane oscroll = new JScrollPane(noteText);
		opane.add(oscroll, BorderLayout.CENTER);
		final JPanel obuttons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		final JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				version.getValue().setAnnotation(noteText.getText());
				FMV.setVersionProperty(archive, source, version.getKey(),
						"note", noteText.getText());
				FMV.saveProperties();
				deactivate();
			}

		});
		obuttons.add(okButton);
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				deactivate();
			}

		});
		obuttons.add(cancelButton);
		opane.add(obuttons, BorderLayout.PAGE_END);
		add(opane, BorderLayout.CENTER);
	}

	/**
	 * Activate the dialog by making it visible.
	 * 
	 * @param text
	 *            text to insert into dialog
	 */
	public void activate(final ProjectData archive, final Source source,
			final Map.Entry<Date, Version> version) {
		this.archive = archive;
		this.source = source;
		this.version = version;
		final Document doc = noteText.getDocument();
		try {
			doc.remove(0, doc.getLength());
			doc.insertString(0, version.getValue().getAnnotation(), null);
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

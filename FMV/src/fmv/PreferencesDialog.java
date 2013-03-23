package fmv;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PreferencesDialog extends JDialog implements ActionListener {

	/**
	 * Apparently we need a version number because we are exending JDialog.
	 */
	private static final long serialVersionUID = 9102953580130214522L;

	private String compilerCmd, interpreterCmd, findbugsJar, findbugsSrc,
			dbHost;

	private boolean removeSrc = true;

	private final JTextField compilerCmdText, interpreterCmdText,
			findbugsJarText;

	private JTextField findbugsSrcText;

	private final JTextField dbHostText;

	/**
	 * Check box for removing the source directory.
	 */
	private final JCheckBox removeSrcCheckBox;

	/**
	 * A file selection dialog.
	 */
	private JFileChooser fileChooser = null;

	/**
	 * Construct a dialog for the preferences.
	 * 
	 * @param parent
	 *            the parent frame
	 */
	public PreferencesDialog(final JFrame parent) {
		super(parent, "Preferences", true);
		setDefaults();
		compilerCmdText = new JTextField(compilerCmd, 60);
		interpreterCmdText = new JTextField(interpreterCmd, 60);
		findbugsJarText = new JTextField(findbugsJar, 60);
		dbHostText = new JTextField(dbHost, 60);
		removeSrcCheckBox = new JCheckBox("Remove source directory", removeSrc);

		final JPanel cfield = createField(compilerCmdText, "compilerBrowse");

		final JPanel ifield = createField(interpreterCmdText,
				"interpreterBrowse");

		final JPanel fbjfield = createField(findbugsJarText,
				"findbugsJarBrowse");

		final JPanel rfield = new JPanel(new FlowLayout(FlowLayout.LEADING));
		rfield.add(removeSrcCheckBox);

		final JPanel dbfield = new JPanel(new FlowLayout(FlowLayout.LEADING));
		dbfield.add(dbHostText);

		final JLabel clabel = new JLabel("Compiler");
		clabel.setLabelFor(cfield);
		final JLabel ilabel = new JLabel("Interpreter");
		ilabel.setLabelFor(ifield);
		final JLabel fbjlabel = new JLabel("Findbugs");
		fbjlabel.setLabelFor(fbjfield);
		final JLabel rlabel = new JLabel("");
		rlabel.setLabelFor(rfield);
		final JLabel dblabel = new JLabel("Database host url");
		dblabel.setLabelFor(dbfield);

		final JPanel ppane = new JPanel();
		final GridBagLayout gridbag = new GridBagLayout();
		ppane.setLayout(gridbag);
		final JLabel[] labels = { clabel, ilabel, fbjlabel, rlabel, dblabel };
		final JPanel[] fields = { cfield, ifield, fbjfield, rfield, dbfield };
		addLabelTextRows(labels, fields, gridbag, ppane);

		final JPanel bpane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		bpane.setOpaque(true);
		final JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		bpane.add(okButton);
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		bpane.add(cancelButton);

		add(ppane, BorderLayout.CENTER);
		add(bpane, BorderLayout.PAGE_END);
	}

	/**
	 * Respond to a button. Either "OK" or "Cancel", or one of the buttons to
	 * browse for a file.
	 * 
	 * @param event
	 *            the event that caused this action
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {
		if ("ok".equals(event.getActionCommand())) {
			compilerCmd = compilerCmdText.getText();
			interpreterCmd = interpreterCmdText.getText();
			findbugsJar = findbugsJarText.getText();
			findbugsSrc = findbugsSrcText.getText();
			removeSrc = removeSrcCheckBox.isSelected();
			dbHost = dbHostText.getText();
			deactivate();
		} else if ("cancel".equals(event.getActionCommand())) {
			deactivate();
		} else if ("compilerBrowse".equals(event.getActionCommand())) {
			if (fileChooser == null) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					compilerCmdText.setText(fileChooser.getSelectedFile()
							.getCanonicalPath());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		} else if ("interpreterBrowse".equals(event.getActionCommand())) {
			if (fileChooser == null) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					interpreterCmdText.setText(fileChooser.getSelectedFile()
							.getCanonicalPath());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		} else if ("findbugsJarBrowse".equals(event.getActionCommand())) {
			if (fileChooser == null) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					findbugsJarText.setText(fileChooser.getSelectedFile()
							.getCanonicalPath());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Activate the dialog by making it visible.
	 */
	public void activate() {
		compilerCmdText.setText(compilerCmd);
		interpreterCmdText.setText(interpreterCmd);
		findbugsJarText.setText(findbugsJar);
		findbugsSrcText.setText(findbugsSrc);
		removeSrcCheckBox.setSelected(removeSrc);
		dbHostText.setText(dbHost);
		setLocationRelativeTo(getParent());
		pack();
		setVisible(true);
	}

	/**
	 * Add the labels and fields for the preferences using a gridbag layout.
	 * 
	 * @param labels
	 *            the labels to add
	 * @param fields
	 *            the fields to add
	 * @param gridbag
	 *            the gridbag layout to use
	 * @param container
	 *            the container in which to place the labels and fields
	 */
	private void addLabelTextRows(final JLabel[] labels, final JPanel[] fields,
			final GridBagLayout gridbag, final Container container) {
		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		for (int i = 0; i < labels.length; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			container.add(fields[i], c);
		}
	}

	private JPanel createField(final JTextField text, final String command) {
		final JPanel field = new JPanel(new FlowLayout(FlowLayout.LEADING));
		field.add(text);
		final JButton button = new JButton("Browse");
		button.setActionCommand(command);
		button.addActionListener(this);
		field.add(button);
		return field;

	}

	/**
	 * Deactivate the dialog by making it invisible.
	 */
	public void deactivate() {
		setVisible(false);
		dispose();
	}

	/**
	 * Returns the string that stores the path and command for the Java
	 * compiler.
	 * 
	 * @return the command for the compiler
	 */
	public String getCompilerCmd() {
		return compilerCmd;
	}

	public String getDBHost() {
		return dbHost;
	}

	/**
	 * Returns the string that stores the path of the findbugs jar file.
	 * 
	 * @return the location of the findbugs jar file
	 */
	public String getFindbugsJar() {
		return findbugsJar;
	}

	/**
	 * Returns the string that stores the main class that findbugs should check.
	 * 
	 * @return the location of the main class
	 */
	public String getFindbugsSrc() {
		return findbugsSrc;
	}

	/**
	 * Returns the string that stores the path and command for the Java
	 * interpreter.
	 * 
	 * @return the command for the interpreter
	 */
	public String getInterpreterCmd() {
		return interpreterCmd;
	}

	/**
	 * Returns the boolean value that tells whether or not the source directory
	 * should be removed everytime a new version of the files are unpacked.
	 * 
	 * @return the boolean value of the flag
	 */
	public boolean getRemoveSrc() {
		return removeSrc;
	}

	public void loadProperties() {
		compilerCmd = FMV
				.getDirectoryProperty("prefs.compilerCmd", compilerCmd);
		interpreterCmd = FMV.getDirectoryProperty("prefs.interpreterCmd",
				interpreterCmd);
		findbugsJar = FMV
				.getDirectoryProperty("prefs.findbugsJar", findbugsJar);
		findbugsSrc = FMV
				.getDirectoryProperty("prefs.findbugsSrc", findbugsSrc);
		if (removeSrc) {
			final String s = FMV
					.getDirectoryProperty("prefs.removeSrc", "true");
			removeSrc = s.equals("true");
		} else {
			final String s = FMV.getDirectoryProperty("prefs.removeSrc",
					"false");
			removeSrc = s.equals("true");
		}
		dbHost = FMV.getDirectoryProperty("prefs.dbhost", "http://localhost");
		setDefaults();
	}

	public void saveProperties() {
		FMV.setDirectoryProperty("prefs.compilerCmd", compilerCmd);
		FMV.setDirectoryProperty("prefs.interpreterCmd", interpreterCmd);
		FMV.setDirectoryProperty("prefs.findbugsJar", findbugsJar);
		FMV.setDirectoryProperty("prefs.findbugsSrc", findbugsSrc);
		FMV.setDirectoryProperty("prefs.removeSrc", removeSrc ? "true"
				: "false");
		FMV.setDirectoryProperty("prefs.dbhost", dbHost);
	}

	private void setDefaults() {
		if (compilerCmd == null || compilerCmd.isEmpty()) {
			compilerCmd = "/usr/bin/javac";
		}
		if (interpreterCmd == null || interpreterCmd.isEmpty()) {
			interpreterCmd = "/usr/bin/java";
		}
		if (findbugsJar == null || findbugsJar.isEmpty()) {
			findbugsJar = "~/findbugs/findbugs-2.0.2/lib/findbugs.jar";
		}
		if (findbugsSrc == null || findbugsSrc.isEmpty()) {
			findbugsSrc = "program";
		}
		if (dbHost == null || dbHost.isEmpty()) {
			dbHost = "http://localhost";
		}
	}

}

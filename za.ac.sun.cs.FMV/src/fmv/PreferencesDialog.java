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

	private String compilerCmd = null;// System.getProperty("java.compiler");

	private String interpreterCmd = null;// System.getProperty("java.vm");

	private String findbugsJar = null;

	private String findbugsSrc = null;

	private boolean removeSrc = true;

	/**
	 * Text field for the compiler command.
	 */
	private JTextField compilerCmdText;

	/**
	 * Text field for the interpreter command.
	 */
	private JTextField interpreterCmdText;

	/**
	 * Text field for the findbugs command.
	 */
	private JTextField findbugsJarText;

	/**
	 * Text field for the class we want findbugs to check.
	 */
	private JTextField findbugsSrcText;

	/**
	 * Check box for removing the source directory.
	 */
	private JCheckBox removeSrcCheckBox;

	/**
	 * A file selection dialog.
	 */
	private JFileChooser fileChooser = null;

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
			String s = FMV.getDirectoryProperty("prefs.removeSrc", "true");
			removeSrc = s.equals("true");
		} else {
			String s = FMV.getDirectoryProperty("prefs.removeSrc", "false");
			removeSrc = s.equals("true");
		}
	}

	public void saveProperties() {
		FMV.setDirectoryProperty("prefs.compilerCmd", compilerCmd);
		FMV.setDirectoryProperty("prefs.interpreterCmd", interpreterCmd);
		FMV.setDirectoryProperty("prefs.findbugsJar", findbugsJar);
		FMV.setDirectoryProperty("prefs.findbugsSrc", findbugsSrc);
		FMV.setDirectoryProperty("prefs.removeSrc", removeSrc ? "true"
				: "false");
	}

	/**
	 * Construct a dialog for the preferences.
	 * 
	 * @param parent
	 *            the parent frame
	 */
	public PreferencesDialog(JFrame parent) {
		super(parent, "Preferences", true);

		if ((compilerCmd == null) || compilerCmd.isEmpty()) {
			compilerCmd = "/usr/bin/javac";
		}
		if ((interpreterCmd == null) || interpreterCmd.isEmpty()) {
			interpreterCmd = "/usr/bin/java";
		}
		if ((findbugsJar == null) || findbugsJar.isEmpty()) {
			findbugsJar = "~/findbugs/findbugs-2.0.2/lib/findbugs.jar";
		}
		if ((findbugsSrc == null) || findbugsSrc.isEmpty()) {
			findbugsSrc = "program";
		}

		// Compiler text field + browse button.
		JPanel cfield = new JPanel(new FlowLayout(FlowLayout.LEADING));
		compilerCmdText = new JTextField(compilerCmd, 60);
		cfield.add(compilerCmdText);
		JButton cbutton = new JButton("Browse");
		cbutton.setActionCommand("compilerBrowse");
		cbutton.addActionListener(this);
		cfield.add(cbutton);

		// Interpreter text field + browse button.
		JPanel ifield = new JPanel(new FlowLayout(FlowLayout.LEADING));
		interpreterCmdText = new JTextField(interpreterCmd, 60);
		ifield.add(interpreterCmdText);
		JButton ibutton = new JButton("Browse");
		ibutton.setActionCommand("interpreterBrowse");
		ibutton.addActionListener(this);
		ifield.add(ibutton);

		// Findbugs text field + browse button.
		JPanel fbjfield = new JPanel(new FlowLayout(FlowLayout.LEADING));
		findbugsJarText = new JTextField(findbugsJar, 60);
		fbjfield.add(findbugsJarText);
		JButton fbjbutton = new JButton("Browse");
		fbjbutton.setActionCommand("findbugsJarBrowse");
		fbjbutton.addActionListener(this);
		fbjfield.add(fbjbutton);

		// Findbugs text field + browse button.
		JPanel fbsfield = new JPanel(new FlowLayout(FlowLayout.LEADING));
		findbugsSrcText = new JTextField(findbugsSrc, 60);
		fbsfield.add(findbugsSrcText);

		// Remove source directory checkbox.
		JPanel rfield = new JPanel(new FlowLayout(FlowLayout.LEADING));
		removeSrcCheckBox = new JCheckBox("Remove source directory", removeSrc);
		rfield.add(removeSrcCheckBox);
		rfield.add(removeSrcCheckBox);

		// Labels.
		JLabel clabel = new JLabel("Compiler");
		clabel.setLabelFor(cfield);
		JLabel ilabel = new JLabel("Interpreter");
		ilabel.setLabelFor(ifield);
		JLabel fbjlabel = new JLabel("Findbugs");
		fbjlabel.setLabelFor(fbjfield);
		JLabel fbslabel = new JLabel("Source dir");
		fbslabel.setLabelFor(fbsfield);
		JLabel rlabel = new JLabel("");
		rlabel.setLabelFor(rfield);

		// Place the labels and fields in a panel.
		JPanel ppane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		ppane.setLayout(gridbag);
		JLabel[] labels = { clabel, ilabel, fbjlabel, fbslabel, rlabel };
		JPanel[] fields = { cfield, ifield, fbjfield, fbsfield, rfield };
		addLabelTextRows(labels, fields, gridbag, ppane);

		// Create the "OK" and "Cancel" buttons.
		JPanel bpane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		bpane.setOpaque(true);
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		bpane.add(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		bpane.add(cancelButton);

		// Add the labels, fields, and buttons to the dialog.
		add(ppane, BorderLayout.CENTER);
		add(bpane, BorderLayout.PAGE_END);
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
	private void addLabelTextRows(JLabel[] labels, JPanel[] fields,
			GridBagLayout gridbag, Container container) {
		GridBagConstraints c = new GridBagConstraints();
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

	/**
	 * Returns the string that stores the path and command for the Java
	 * compiler.
	 * 
	 * @return the command for the compiler
	 */
	public String getCompilerCmd() {
		return compilerCmd;
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
	 * Returns the boolean value that tells whether or not the source directory
	 * should be removed everytime a new version of the files are unpacked.
	 * 
	 * @return the boolean value of the flag
	 */
	public boolean getRemoveSrc() {
		return removeSrc;
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
		setLocationRelativeTo(getParent());
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

	/**
	 * Respond to a button. Either "OK" or "Cancel", or one of the buttons to
	 * browse for a file.
	 * 
	 * @param event
	 *            the event that caused this action
	 */
	public void actionPerformed(ActionEvent event) {
		if ("ok".equals(event.getActionCommand())) {
			compilerCmd = compilerCmdText.getText();
			interpreterCmd = interpreterCmdText.getText();
			findbugsJar = findbugsJarText.getText();
			findbugsSrc = findbugsSrcText.getText();
			removeSrc = removeSrcCheckBox.isSelected();
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
				} catch (IOException e) {
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
				} catch (IOException e) {
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

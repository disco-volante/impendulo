package fmv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import fmv.TablePane.ArchiveData;

public class CompTestDialog extends JDialog {

	/**
	 * Apparently we need a version number because we are exending JDialog.
	 */
	private static final long serialVersionUID = 5123992312788517712L;

	/**
	 * Progress bar.
	 */
	private JProgressBar comptestBar;

	private JLabel messageLabel;

	private Source rootItem;

	private Archive archive;

	/**
	 * Construct a dialog for showing the compiler/tester progress.
	 * 
	 * @param parent
	 *            the parent frame
	 */
	public CompTestDialog(JFrame parent) {
		super(parent, "Compiling and testing", true);
		addWindowListener(new WindowCloser(this));
		JPanel comptestPane = new JPanel();
		comptestPane.setLayout(new BoxLayout(comptestPane, BoxLayout.PAGE_AXIS));
		comptestPane.setBorder(BorderFactory.createEmptyBorder(10, 2, 2, 2));

		// Create and add a label to the content pane.
		JLabel comptestLabel = new JLabel("Unpacking, compiling, and testing... (may take a while)");
		comptestLabel.setAlignmentX(LEFT_ALIGNMENT);
		comptestPane.add(comptestLabel);
		comptestPane.add(Box.createRigidArea(new Dimension(0,5)));

		// Create and add a label to the content pane.
		messageLabel = new JLabel("|");
		messageLabel.setAlignmentX(LEFT_ALIGNMENT);
		comptestPane.add(messageLabel);
		comptestPane.add(Box.createRigidArea(new Dimension(0,5)));

		// Create and add the progress bar.
		comptestBar = new JProgressBar(0, 100);
		comptestPane.add(comptestBar);
		comptestPane.add(Box.createRigidArea(new Dimension(0,5)));

		// Create and add the cancel buttom.
		JPanel comptestButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				deactivate(true);
			}

		});
		comptestButtons.add(cancelButton);
		comptestPane.add(comptestButtons);

		// Add the pane to the dialog.
		add(comptestPane, BorderLayout.CENTER);
	}

	/**
	 * Activate the dialog by making it visible.
	 */
	public void activate(Archive archive, Source rootItem) {
		this.archive = archive;
		this.rootItem = rootItem;
		comptestBar.setValue(comptestBar.getMinimum());
		setLocationRelativeTo(getParent());
		pack();
		setVisible(true);
	}

	/**
	 * Deactivate the dialog by making it invisible.
	 */
	public void deactivate(boolean isCancel) {
		setVisible(false);
		dispose();
		if (!isCancel) {
		ArchiveData data = new ArchiveData();
		rootItem.extractProperties(archive, data);
		SortedSet<Date> dates = new TreeSet<Date>(rootItem.getKeys());
		Date prev = null, first = null, second = null;
		for (Date d : dates) {
			if (first == null) {
				first = d;
			} else if (second == null) {
				second = d;
				data.minTime = (second.getTime() - first.getTime()) * 0.001;
			}
			if (prev != null) {
				double t = (d.getTime() - prev.getTime()) * 0.001;
				if (t > data.maxTime) { data.maxTime = t; }
				if (t < data.minTime) { data.minTime = t; }
			}
			prev = d;
		}
		data.aveTime = (prev.getTime() - first.getTime()) * 0.001 / dates.size();
		data.totTime = (prev.getTime() - first.getTime()) * 0.001;
		data.writeProperties(archive);
		FMV.tablePane.addData(archive.toString(), data);
		FMV.setArchiveProperty(archive, "true");
		FMV.saveProperties();
		archive.setCompiled();
		}
	}

	/**
	 * Set the value for the progress bar.
	 * 
	 * @param value
	 *            the value of the progress bar (0..100)
	 */
	public void setProgress(int value) {
		comptestBar.setValue(value);
	}

	public void setMessage(String s) {
		messageLabel.setText(s);
	}

	private static class WindowCloser extends WindowAdapter {

		private CompTestDialog dialog = null;
		
		public WindowCloser(CompTestDialog dialog) {
			this.dialog = dialog;
		}

		public void windowClosing(WindowEvent event) {
			dialog.deactivate(true);
		}

	}

}

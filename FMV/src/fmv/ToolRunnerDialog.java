package fmv;

import java.awt.BorderLayout;
import java.awt.Component;
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

public class ToolRunnerDialog extends JDialog {

	private static class WindowCloser extends WindowAdapter {

		private ToolRunnerDialog dialog = null;

		public WindowCloser(final ToolRunnerDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void windowClosing(final WindowEvent event) {
			dialog.deactivate(true);
		}

	}

	/**
	 * Apparently we need a version number because we are exending JDialog.
	 */
	private static final long serialVersionUID = 5123992312788517712L;

	/**
	 * Progress bar.
	 */
	private final JProgressBar comptestBar;

	private final JLabel messageLabel;

	private Source rootItem;

	private ProjectData archive;

	/**
	 * Construct a dialog for showing the compiler/tester progress.
	 * 
	 * @param parent
	 *            the parent frame
	 */
	public ToolRunnerDialog(final JFrame parent) {
		super(parent, "Compiling and testing", true);
		addWindowListener(new WindowCloser(this));
		final JPanel comptestPane = new JPanel();
		comptestPane
				.setLayout(new BoxLayout(comptestPane, BoxLayout.PAGE_AXIS));
		comptestPane.setBorder(BorderFactory.createEmptyBorder(10, 2, 2, 2));

		final JLabel comptestLabel = new JLabel(
				"Unpacking, compiling, and testing... (may take a while)");
		comptestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		comptestPane.add(comptestLabel);
		comptestPane.add(Box.createRigidArea(new Dimension(0, 5)));

		messageLabel = new JLabel("|");
		messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		comptestPane.add(messageLabel);
		comptestPane.add(Box.createRigidArea(new Dimension(0, 5)));

		comptestBar = new JProgressBar(0, 100);
		comptestPane.add(comptestBar);
		comptestPane.add(Box.createRigidArea(new Dimension(0, 5)));

		final JPanel comptestButtons = new JPanel(new FlowLayout(
				FlowLayout.CENTER));
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				deactivate(true);
			}

		});
		comptestButtons.add(cancelButton);
		comptestPane.add(comptestButtons);

		add(comptestPane, BorderLayout.CENTER);
	}

	/**
	 * Activate the dialog by making it visible.
	 */
	public void activate(final ProjectData archive, final Source rootItem) {
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
	public void deactivate(final boolean isCancel) {
		setVisible(false);
		dispose();
		if (!isCancel) {
			final ArchiveData data = new ArchiveData();
			rootItem.extractProperties(archive, data);
			final SortedSet<Date> dates = new TreeSet<Date>(rootItem.getKeys());
			Date prev = null, first = null, second = null;
			for (final Date d : dates) {
				if (first == null) {
					first = d;
				} else if (second == null) {
					second = d;
					data.minTime = (second.getTime() - first.getTime()) * 0.001;
				}
				if (prev != null) {
					final double t = (d.getTime() - prev.getTime()) * 0.001;
					if (t > data.maxTime) {
						data.maxTime = t;
					}
					if (t < data.minTime) {
						data.minTime = t;
					}
				}
				prev = d;
			}
			data.aveTime = (prev.getTime() - first.getTime()) * 0.001
					/ dates.size();
			data.totTime = (prev.getTime() - first.getTime()) * 0.001;
			data.writeProperties(archive);
			FMV.tablePane.addData(archive.toString(), data);
			FMV.setArchiveProperty(archive, "true");
			FMV.saveProperties();
			archive.setCompiled();
		}
	}

	public void setMessage(final String s) {
		messageLabel.setText(s);
	}

	/**
	 * Set the value for the progress bar.
	 * 
	 * @param value
	 *            the value of the progress bar (0..100)
	 */
	public void setProgress(final int value) {
		comptestBar.setValue(value);
	}

}

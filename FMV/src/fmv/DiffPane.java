package fmv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.TextRegion;
import javax.swing.plaf.TextRegionUI;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import fmv.tools.Tools;

public class DiffPane extends JPanel implements ActionListener {

	/**
	 * Apparently we need a version number because we are exending JPanel.
	 */
	private static final long serialVersionUID = -2944488635268985223L;

	/**
	 * "Previous" button at the top of the difference pane.
	 */
	private final JButton prevButton;

	/**
	 * "Next" button at the top of the difference pane.
	 */
	private final JButton nextButton;

	/**
	 * "Show" button on the left.
	 */
	private final JButton leftOutputButton;

	/**
	 * "Annotation" button on the left.
	 */
	private final JButton leftAnnoteButton;

	/**
	 * "Show" button on the right.
	 */
	private final JButton rightOutputButton;

	/**
	 * "Annotation" button on the right.
	 */
	private final JButton rightAnnoteButton;

	/**
	 * Time and status message on the left.
	 */
	private final JLabel leftLabel;

	/**
	 * Time and status message on the right.
	 */
	private final JLabel rightLabel;

	/**
	 * The text on the left.
	 */
	private final TextRegion leftText;

	/**
	 * The text on the right.
	 */
	private final TextRegion rightText;

	/**
	 * Scrollbar for the text on the left.
	 */
	private final JScrollBar leftBar;

	/**
	 * Scrollbar for the text on the right.
	 */
	private final JScrollBar rightBar;
	private final JButton toolButton;
	private final JComboBox<String> toolBox;

	private static VersionTimeline timeline;

	public DiffPane() {
		super(new BorderLayout());
		final Dimension d = new Dimension(24, 24);

		DiffPane.timeline = new VersionTimeline(true);
		DiffPane.timeline.setOpaque(true);
		final Dimension d1 = new Dimension(500, 40);
		DiffPane.timeline.setMinimumSize(d1);
		DiffPane.timeline.setPreferredSize(d1);

		leftText = new TextRegion(new DefaultStyledDocument());
		leftText.setEditable(false);
		leftText.setFont(new Font("Monospaced", Font.PLAIN, 12));
		leftText.setTabSize(3);
		createStyles((StyledDocument) leftText.getDocument());
		leftText.setUI(new TextRegionUI());
		final JScrollPane leftTextScrollPane = new JScrollPane(leftText);
		leftBar = leftTextScrollPane.getVerticalScrollBar();
		leftBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(final AdjustmentEvent event) {
				rightBar.setValue(leftBar.getValue());
			}
		});

		final JPanel leftTopPane = new JPanel(
				new FlowLayout(FlowLayout.LEADING));
		leftTopPane.setOpaque(true);
		leftOutputButton = new JButton(FMV.getMyImageIcon("output.gif"));
		leftOutputButton.setToolTipText("Show output");
		leftOutputButton.setActionCommand("leftshow");
		leftOutputButton.addActionListener(DiffPane.timeline);
		leftOutputButton.setPreferredSize(d);
		leftTopPane.add(leftOutputButton);
		leftAnnoteButton = new JButton(FMV.getMyImageIcon("annotate.gif"));
		leftAnnoteButton.setToolTipText("Edit annotation");
		leftAnnoteButton.setActionCommand("leftedit");
		leftAnnoteButton.addActionListener(DiffPane.timeline);
		leftAnnoteButton.setPreferredSize(d);
		leftTopPane.add(leftAnnoteButton);
		leftLabel = new JLabel("");
		leftTopPane.add(leftLabel);
		final JPanel leftPane = new JPanel(new BorderLayout());
		leftPane.add(leftTopPane, BorderLayout.PAGE_START);
		leftPane.add(leftTextScrollPane, BorderLayout.CENTER);

		rightText = new TextRegion(new DefaultStyledDocument());
		rightText.setEditable(false);
		rightText.setFont(new Font("Monospaced", Font.PLAIN, 12));
		rightText.setTabSize(3);
		createStyles((StyledDocument) rightText.getDocument());
		rightText.setUI(new TextRegionUI());
		final JScrollPane rightTextScrollPane = new JScrollPane(rightText);
		rightBar = rightTextScrollPane.getVerticalScrollBar();
		rightBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(final AdjustmentEvent event) {
				leftBar.setValue(rightBar.getValue());
			}
		});

		final JPanel rightTopPane = new JPanel(new FlowLayout(
				FlowLayout.LEADING));
		rightTopPane.setOpaque(true);
		rightOutputButton = new JButton(FMV.getMyImageIcon("output.gif"));
		rightOutputButton.setToolTipText("Show output");
		rightOutputButton.setActionCommand("rightshow");
		rightOutputButton.addActionListener(DiffPane.timeline);
		rightOutputButton.setPreferredSize(d);
		rightTopPane.add(rightOutputButton);
		rightAnnoteButton = new JButton(FMV.getMyImageIcon("annotate.gif"));
		rightAnnoteButton.setToolTipText("Edit annotation");
		rightAnnoteButton.setActionCommand("rightedit");
		rightAnnoteButton.addActionListener(DiffPane.timeline);
		rightAnnoteButton.setPreferredSize(d);
		rightTopPane.add(rightAnnoteButton);
		rightLabel = new JLabel("");
		rightTopPane.add(rightLabel);
		final JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(rightTopPane, BorderLayout.PAGE_START);
		rightPane.add(rightTextScrollPane, BorderLayout.CENTER);

		final JPanel textsPane = new JPanel(new GridLayout(1, 2));
		textsPane.setOpaque(true);
		textsPane.add(leftPane);
		textsPane.add(rightPane);

		final JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		buttonPane.setOpaque(true);
		prevButton = new JButton("Previous", FMV.getMyImageIcon("prev.gif"));
		prevButton.setToolTipText("Previous");
		prevButton.setActionCommand("showprev");
		prevButton.addActionListener(this);
		buttonPane.add(prevButton);
		nextButton = new JButton("Next", FMV.getMyImageIcon("next.gif"));
		nextButton.setToolTipText("Next");
		nextButton.setActionCommand("shownext");
		nextButton.addActionListener(this);
		buttonPane.add(nextButton);
		toolBox = new JComboBox<String>(Tools.getTools());
		buttonPane.add(toolBox);
		toolButton = new JButton("Show Results");
		toolButton.setActionCommand("tool");
		toolButton.addActionListener(DiffPane.timeline);
		buttonPane.add(toolButton);

		setOpaque(true);
		add(textsPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_START);
		add(DiffPane.timeline, BorderLayout.PAGE_END);
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		if ("showprev".equals(event.getActionCommand())) {
			if (DiffPane.timeline != null) {
				DiffPane.timeline.showPrev();
			}
		} else if ("shownext".equals(event.getActionCommand())) {
			if (DiffPane.timeline != null) {
				DiffPane.timeline.showNext();
			}
		}
	}

	/**
	 * Create styles for a document that display differences.
	 * 
	 * @param doc
	 *            the document to apply the styles to
	 */
	private void createStyles(final StyledDocument doc) {
		final Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		final Style normal = doc.addStyle("normal", def);
		doc.addStyle("delta", normal);
		doc.addStyle("changed", normal);
	}

	public String getCurrentTool() {
		return (String) toolBox.getSelectedItem();
	}

	public TextRegion getText(final boolean onLeft) {
		if (onLeft) {
			return leftText;
		} else {
			return rightText;
		}
	}

	public void scrollToTop() {
		leftBar.revalidate();
		rightBar.revalidate();
		leftBar.setValue(leftBar.getMinimum());
		rightBar.setValue(rightBar.getMinimum());
		leftBar.revalidate();
		rightBar.revalidate();
	}

	public void setButton(final boolean onLeft, final boolean enabled) {
		if (onLeft) {
			prevButton.setEnabled(enabled);
			leftOutputButton.setEnabled(enabled);
			leftAnnoteButton.setEnabled(enabled);
		} else {
			nextButton.setEnabled(enabled);
			rightOutputButton.setEnabled(enabled);
			rightAnnoteButton.setEnabled(enabled);
		}
	}

	public void setItem() {
		DiffPane.timeline.repaint();
	}

	public void setLabel(final boolean onLeft, final String label) {
		if (onLeft) {
			leftLabel.setText(label);
		} else {
			rightLabel.setText(label);
		}
	}

}

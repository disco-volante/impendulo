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

public class DiffPane extends JPanel implements ActionListener {

	/**
	 * Apparently we need a version number because we are exending JPanel.
	 */
	private static final long serialVersionUID = -2944488635268985223L;

	/**
	 * "Previous" button at the top of the difference pane.
	 */
	private JButton prevButton;

	/**
	 * "Next" button at the top of the difference pane.
	 */
	private JButton nextButton;

	/**
	 * "Show" button on the left.
	 */
	private JButton leftOutputButton;

	/**
	 * "Findbugs" button on the left.
	 */
	private JButton leftFindbugsButton;

	/**
	 * "Annotation" button on the left.
	 */
	private JButton leftAnnoteButton;

	/**
	 * "Show" button on the right.
	 */
	private JButton rightOutputButton;

	/**
	 * "Findbugs" button on the right.
	 */
	private JButton rightFindbugsButton;

	/**
	 * "Annotation" button on the right.
	 */
	private JButton rightAnnoteButton;

	/**
	 * Time and status message on the left.
	 */
	private JLabel leftLabel;

	/**
	 * Time and status message on the right.
	 */
	private JLabel rightLabel;

	/**
	 * The text on the left.
	 */
	private TextRegion leftText;

	/**
	 * The text on the right.
	 */
	private TextRegion rightText;

	/**
	 * Scrollbar for the text on the left.
	 */
	private JScrollBar leftBar;

	/**
	 * Scrollbar for the text on the right.
	 */
	private JScrollBar rightBar;

	private static VersionTimeline timeline;

	public DiffPane() {
		super(new BorderLayout());
		Dimension d = new Dimension(24, 24);

		timeline = new VersionTimeline(true);
		timeline.setOpaque(true);
		Dimension d1 = new Dimension(500, 40);
		timeline.setMinimumSize(d1);
		timeline.setPreferredSize(d1);

		leftText = new TextRegion(new DefaultStyledDocument());
		leftText.setEditable(false);
		leftText.setFont(new Font("Monospaced", Font.PLAIN, 12));
		leftText.setTabSize(3);
		createStyles((StyledDocument) leftText.getDocument());
		leftText.setUI(new TextRegionUI());
		JScrollPane leftTextScrollPane = new JScrollPane(leftText);
		leftBar = leftTextScrollPane.getVerticalScrollBar();
		leftBar.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent event) {
				rightBar.setValue(leftBar.getValue());
			}
		});

		JPanel leftTopPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftTopPane.setOpaque(true);
		leftOutputButton = new JButton(FMV.getMyImageIcon("output.gif"));
		leftOutputButton.setToolTipText("Show output");
		leftOutputButton.setActionCommand("leftshow");
		leftOutputButton.addActionListener(timeline);
		leftOutputButton.setPreferredSize(d);
		leftTopPane.add(leftOutputButton);
		leftFindbugsButton = new JButton(FMV.getMyImageIcon("findbugs.gif"));
		leftFindbugsButton.setToolTipText("Show findbugs output");
		leftFindbugsButton.setActionCommand("leftfb");
		leftFindbugsButton.addActionListener(timeline);
		leftFindbugsButton.setPreferredSize(d);
		leftTopPane.add(leftFindbugsButton);
		leftAnnoteButton = new JButton(FMV.getMyImageIcon("annotate.gif"));
		leftAnnoteButton.setToolTipText("Edit annotation");
		leftAnnoteButton.setActionCommand("leftedit");
		leftAnnoteButton.addActionListener(timeline);
		leftAnnoteButton.setPreferredSize(d);
		leftTopPane.add(leftAnnoteButton);
		leftLabel = new JLabel("");
		leftTopPane.add(leftLabel);
		JPanel leftPane = new JPanel(new BorderLayout());
		leftPane.add(leftTopPane, BorderLayout.PAGE_START);
		leftPane.add(leftTextScrollPane, BorderLayout.CENTER);

		rightText = new TextRegion(new DefaultStyledDocument());
		rightText.setEditable(false);
		rightText.setFont(new Font("Monospaced", Font.PLAIN, 12));
		rightText.setTabSize(3);
		createStyles((StyledDocument) rightText.getDocument());
		rightText.setUI(new TextRegionUI());
		JScrollPane rightTextScrollPane = new JScrollPane(rightText);
		rightBar = rightTextScrollPane.getVerticalScrollBar();
		rightBar.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent event) {
				leftBar.setValue(rightBar.getValue());
			}
		});

		JPanel rightTopPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		rightTopPane.setOpaque(true);
		rightOutputButton = new JButton(FMV.getMyImageIcon("output.gif"));
		rightOutputButton.setToolTipText("Show output");
		rightOutputButton.setActionCommand("rightshow");
		rightOutputButton.addActionListener(timeline);
		rightOutputButton.setPreferredSize(d);
		rightTopPane.add(rightOutputButton);
		rightFindbugsButton = new JButton(FMV.getMyImageIcon("findbugs.gif"));
		rightFindbugsButton.setToolTipText("Show findbugs output");
		rightFindbugsButton.setActionCommand("rightfb");
		rightFindbugsButton.addActionListener(timeline);
		rightFindbugsButton.setPreferredSize(d);
		rightTopPane.add(rightFindbugsButton);
		rightAnnoteButton = new JButton(FMV.getMyImageIcon("annotate.gif"));
		rightAnnoteButton.setToolTipText("Edit annotation");
		rightAnnoteButton.setActionCommand("rightedit");
		rightAnnoteButton.addActionListener(timeline);
		rightAnnoteButton.setPreferredSize(d);
		rightTopPane.add(rightAnnoteButton);
		rightLabel = new JLabel("");
		rightTopPane.add(rightLabel);
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(rightTopPane, BorderLayout.PAGE_START);
		rightPane.add(rightTextScrollPane, BorderLayout.CENTER);

		JPanel textsPane = new JPanel(new GridLayout(1, 2));
		textsPane.setOpaque(true);
		textsPane.add(leftPane);
		textsPane.add(rightPane);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
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

		setOpaque(true);
		add(textsPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_START);
		add(timeline, BorderLayout.PAGE_END);
	}

	/**
	 * Create styles for a document that display differences.
	 * 
	 * @param doc the document to apply the styles to
	 */
	private void createStyles(StyledDocument doc) {
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style normal = doc.addStyle("normal", def);
		doc.addStyle("delta", normal);
		doc.addStyle("changed", normal);
	}

	public TextRegion getText(boolean onLeft) {
		if (onLeft) {
			return leftText;
		} else {
			return rightText;
		}
	}

	public void setItem(Archive archive, Source source) {
		timeline.setSource(archive, source);
		timeline.repaint();
	}

	public void setLabel(boolean onLeft, String label) {
		if (onLeft) {
			leftLabel.setText(label);
		} else {
			rightLabel.setText(label);
		}
	}

	public void setButton(boolean onLeft, boolean enabled) {
		if (onLeft) {
			prevButton.setEnabled(enabled);
			leftOutputButton.setEnabled(enabled);
			leftFindbugsButton.setEnabled(enabled);
			leftAnnoteButton.setEnabled(enabled);
		} else {
			nextButton.setEnabled(enabled);
			rightOutputButton.setEnabled(enabled);
			rightFindbugsButton.setEnabled(enabled);
			rightAnnoteButton.setEnabled(enabled);
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

	public void actionPerformed(ActionEvent event) {
		if ("showprev".equals(event.getActionCommand())) {
			if (timeline != null) {
				timeline.showPrev();
			}
		} else if ("shownext".equals(event.getActionCommand())) {
			if (timeline != null) {
				timeline.showNext();
			}
		}
	}

}

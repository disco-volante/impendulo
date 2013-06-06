package za.ac.sun.cs.intlola.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import za.ac.sun.cs.intlola.processing.IntlolaMode;

public class InfoDialog extends Dialog {

	private IntlolaMode			mode;

	private SelectionListener	modeListener;

	private String[]				projects;

	private final String		title;

	private Combo projectList;


	public InfoDialog(final Shell parentShell, final String title,
			final String[] projects,
			final IntlolaMode mode) {
		super(parentShell);
		setFields(projects, mode);
		this.title = title;

	}

	@Override
	public void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	@Override
	public Control createButtonBar(final Composite parent) {
		final Control ret = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText("Login");
		return ret;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);

		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;

		final Label projectLabel = new Label(comp, SWT.RIGHT);
		projectLabel.setText("Project:");

		projectList = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		projectList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectList.setItems(projects);
		
		final Label modeLabel = new Label(comp, SWT.RIGHT);
		modeLabel.setText("Select the mode to run Intlola in:");
		new Label(comp, SWT.RIGHT);

		new Label(comp, SWT.RIGHT);
		final Button btnFR = new Button(comp, SWT.RADIO);
		btnFR.setText(IntlolaMode.FILE_REMOTE.getDescription());
		btnFR.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(comp, SWT.RIGHT);
		final Button btnAR = new Button(comp, SWT.RADIO);
		btnAR.setText(IntlolaMode.ARCHIVE_REMOTE.getDescription());
		btnAR.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(comp, SWT.RIGHT);
		final Button btnAL = new Button(comp, SWT.RADIO);
		btnAL.setText(IntlolaMode.ARCHIVE_LOCAL.getDescription());
		btnAL.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		modeListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (btnFR.getSelection()) {
					btnAR.setSelection(false);
					btnAL.setSelection(false);
					mode = IntlolaMode.FILE_REMOTE;
				} else if (btnAR.getSelection()) {
					btnFR.setSelection(false);
					btnAL.setSelection(false);
					mode = IntlolaMode.ARCHIVE_REMOTE;
				} else if (btnAL.getSelection()) {
					btnFR.setSelection(false);
					btnAR.setSelection(false);
					mode = IntlolaMode.ARCHIVE_LOCAL;
				}
			}
		};

		btnFR.addSelectionListener(modeListener);
		btnAR.addSelectionListener(modeListener);
		btnAL.addSelectionListener(modeListener);

		switch (mode) {
			case FILE_REMOTE:
				btnFR.setSelection(true);
				break;
			case ARCHIVE_LOCAL:
				btnAL.setSelection(true);
				break;
			case ARCHIVE_REMOTE:
				btnAR.setSelection(true);
				break;
			case ARCHIVE_TEST:
				break;
			default:
				break;

		}
		return comp;
	}

	public IntlolaMode getMode() {
		return mode;
	}

	public String getProject() {
		return projectList.getText().trim();
	}

	public void setFields(final String[] projects, final IntlolaMode mode) {
		this.mode = mode;
		this.projects = projects;
	}

}

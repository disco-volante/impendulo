package za.ac.sun.cs.intlola.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

import za.ac.sun.cs.intlola.processing.Project;
import za.ac.sun.cs.intlola.processing.Submission;

public class SubmissionDialog extends Dialog {
	private Project project;
	private Submission submission;
	private Map<Project, ArrayList<Submission>> history;
	private Project[] histProjects, retProjects;
	private String[] histProjectItems, retProjectItems;
	private Map<String, String[]> subItems;

	protected boolean create;

	public SubmissionDialog(final Shell parentShell,
			final Project[] retProjects,
			Map<Project, ArrayList<Submission>> history) {
		super(parentShell);
		this.retProjects = retProjects;
		retProjectItems = new String[retProjects.length];
		int i = 0;
		for (Project p : retProjects) {
			retProjectItems[i++] = p.toString();
		}
		this.history = history;
		histProjects = history.keySet().toArray(new Project[history.size()]);
		histProjectItems = new String[history.size()];
		subItems = new HashMap<String, String[]>();
		int j = 0;
		for (Entry<Project, ArrayList<Submission>> e : history.entrySet()) {
			int k = 0;
			String[] subs = new String[e.getValue().size()];
			for (Submission s : e.getValue()) {
				subs[k++] = s.toString();
			}
			String proj = e.getKey().toString();
			histProjectItems[j++] = proj;
			subItems.put(proj, subs);
		}
	}

	@Override
	public void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Choose submission mode.");
	}

	@Override
	public Control createButtonBar(final Composite parent) {
		final Control ret = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText("Confirm");
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return ret;
	}
	

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);

		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;

		final Button btnNew = new Button(comp, SWT.RADIO);
		btnNew.setText("Create new submission.");
		btnNew.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button btnContinue = new Button(comp, SWT.RADIO);
		btnContinue.setText("Continue with existing submission.");
		btnContinue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label projectLabel = new Label(comp, SWT.RIGHT);
		projectLabel.setText("Project:");

		final Combo projectList = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		projectList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label subLabel = new Label(comp, SWT.RIGHT);
		subLabel.setText("Submission:");

		final Combo submissionList = new Combo(comp, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		submissionList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Add listeners

		SelectionListener choiceListener = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				create = btnNew.getSelection();
				if (create) {
					btnContinue.setSelection(false);
					projectList.setItems(retProjectItems);
					submissionList.setVisible(false);
					subLabel.setVisible(false);
				} else {
					btnNew.setSelection(false);
					projectList.setItems(histProjectItems);
					submissionList.setVisible(true);
					subLabel.setVisible(true);
				}
			}
		};

		btnNew.addSelectionListener(choiceListener);
		btnContinue.addSelectionListener(choiceListener);

		projectList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				int index = projectList.getSelectionIndex();
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				if (index >= 0) {
					if (create) {
						project = retProjects[index];
						getButton(IDialogConstants.OK_ID).setEnabled(true);
					} else {
						project = histProjects[index];
						submissionList.setItems(subItems.get(projectList
								.getText()));
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

		submissionList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				if (!create) {
					submission = history.get(project).get(
							submissionList.getSelectionIndex());
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

		// Initialise
		create = true;
		btnNew.setSelection(true);
		projectList.setItems(retProjectItems);
		submissionList.setVisible(false);
		subLabel.setVisible(false);

		return comp;
	}

	public Project getProject() {
		return project;
	}

	public Submission getSubmission() {
		return submission;
	}

	public boolean isCreate() {
		return create;
	}
}

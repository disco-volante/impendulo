//Copyright (c) 2013, The Impendulo Authors
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification,
//are permitted provided that the following conditions are met:
//
//  Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  Redistributions in binary form must reproduce the above copyright notice, this
//  list of conditions and the following disclaimer in the documentation and/or
//  other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
//ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
//ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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

import za.ac.sun.cs.intlola.processing.json.Assignment;
import za.ac.sun.cs.intlola.processing.json.AssignmentInfo;
import za.ac.sun.cs.intlola.processing.json.Project;
import za.ac.sun.cs.intlola.processing.json.ProjectInfo;
import za.ac.sun.cs.intlola.processing.json.Submission;

/**
 * SubmissionDialog is used to determine whether the user wants to create a new
 * submission or continue with an existing one.
 * 
 * @author godfried
 * 
 */
public class SubmissionDialog extends Dialog {
	private ProjectItems projectItems;

	protected boolean create;

	public SubmissionDialog(final Shell parentShell,
			final ProjectInfo[] projectInfos) {
		super(parentShell);
		projectItems = new ProjectItems(projectInfos);
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

		final Label assLabel = new Label(comp, SWT.RIGHT);
		assLabel.setText("Assignment:");

		final Combo assList = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		assList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

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
					submissionList.setVisible(false);
					subLabel.setVisible(false);
				} else {
					btnNew.setSelection(false);
					submissionList.setVisible(true);
					submissionList.setItems(projectItems.getSubmissionNames());
					subLabel.setVisible(true);
				}
			}
		};

		btnNew.addSelectionListener(choiceListener);
		btnContinue.addSelectionListener(choiceListener);

		projectList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				projectItems.setProject(projectList.getText());
				assList.setItems(projectItems.getAssignmentNames());
				submissionList.setItems(new String[] {});
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

		assList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				projectItems.setAssignment(assList.getText());
				if (create) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				} else {
					submissionList.setItems(projectItems.getSubmissionNames());
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
					projectItems.setSubmission(submissionList.getText());
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
		projectList.setItems(projectItems.getNames());
		submissionList.setVisible(false);
		subLabel.setVisible(false);

		return comp;
	}

	public Assignment getAssignment() {
		return projectItems.getAssignment();
	}

	public Submission getSubmission() {
		return projectItems.getSubmission();
	}
	public Project getProject() {
		return projectItems.getProject();
	}
	
	public boolean isCreate() {
		return create;
	}

	public static void main(String[] args) {
		Project[] ps = new Project[] {
				new Project("Triangle", "Java", System.currentTimeMillis()),
				new Project("TriType", "Java", System.currentTimeMillis()),
				new Project("KSelect", "Java", System.currentTimeMillis()) };
		Assignment[] as = new Assignment[] {
				new Assignment("Honours", "user", System.currentTimeMillis(),
						System.currentTimeMillis() + 1000),
				new Assignment("Masters", "user", System.currentTimeMillis(),
						System.currentTimeMillis() + 2000),
				new Assignment("RW344", "user", System.currentTimeMillis(),
						System.currentTimeMillis() + 3000), };
		Submission[] ss = new Submission[] {
				new Submission("user 1", System.currentTimeMillis()),
				new Submission("user 2", System.currentTimeMillis()),
				new Submission("user 3", System.currentTimeMillis()) };
		ProjectInfo[] infos = new ProjectInfo[ps.length];
		for (int i = 0; i < infos.length; i++) {
			AssignmentInfo[] assInfos = new AssignmentInfo[as.length];
			for (int j = 0; j < assInfos.length; j++) {
				assInfos[j] = new AssignmentInfo(as[j], ss);
			}
			infos[i] = new ProjectInfo(ps[i], assInfos);
		}
		Dialog d = new SubmissionDialog(new Shell(), infos);
		d.open();
	}


}

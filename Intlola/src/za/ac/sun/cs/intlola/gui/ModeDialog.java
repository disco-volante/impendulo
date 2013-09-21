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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import za.ac.sun.cs.intlola.processing.IntlolaMode;

/**
 * ModeDialog is used to determine which mode the user wishes to run Intlola in.
 * 
 * @author godfried
 * 
 */
public class ModeDialog extends Dialog {
	private IntlolaMode mode;

	public ModeDialog(final Shell parentShell, final IntlolaMode mode) {
		super(parentShell);
		this.mode = mode;

	}

	@Override
	public Control createButtonBar(final Composite parent) {
		final Control ret = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText("Select");
		return ret;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);
		final Label modeLabel = new Label(comp, SWT.RIGHT);
		modeLabel.setText("Select the mode to run Intlola in:");
		final Button btnFR = new Button(comp, SWT.RADIO);
		btnFR.setText(IntlolaMode.FILE_REMOTE.getDescription());
		btnFR.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button btnAR = new Button(comp, SWT.RADIO);
		btnAR.setText(IntlolaMode.ARCHIVE_REMOTE.getDescription());
		btnAR.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button btnAL = new Button(comp, SWT.RADIO);
		btnAL.setText(IntlolaMode.ARCHIVE_LOCAL.getDescription());
		btnAL.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		SelectionListener modeListener = new SelectionListener() {
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
		default:
			break;
		}
		return comp;
	}

	public IntlolaMode getMode() {
		return mode;
	}

	public static void main(String[] args) {
		ModeDialog dlg = new ModeDialog(new Shell(), IntlolaMode.ARCHIVE_LOCAL);
		dlg.open();
	}
}

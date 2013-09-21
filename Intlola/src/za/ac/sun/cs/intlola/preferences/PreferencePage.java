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

package za.ac.sun.cs.intlola.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.processing.IntlolaMode;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(Intlola.getActive().getPreferenceStore());
		setDescription("Intlola configuration");
	}

	@Override
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.P_UNAME,
				"Username:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_ADDRESS,
				"Server address:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.P_PORT,
				"Server port:", getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(PreferenceConstants.P_MODE,
				"Select the mode to run Intlola in:", 1, new String[][] {
						{ IntlolaMode.FILE_REMOTE.getDescription(),
								PreferenceConstants.FILE_REMOTE },
						{ IntlolaMode.ARCHIVE_REMOTE.getDescription(),
								PreferenceConstants.ARCHIVE_REMOTE },
						{ IntlolaMode.ARCHIVE_LOCAL.getDescription(),
								PreferenceConstants.ARCHIVE_LOCAL } },
				getFieldEditorParent()));
	}

	@Override
	public void init(final IWorkbench workbench) {
	}

}
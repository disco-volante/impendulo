package za.ac.sun.cs.intlola.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import za.ac.sun.cs.intlola.Intlola;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Intlola.getDefault().getPreferenceStore());
		setDescription("Intlola configuration");
	}

	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.P_UNAME,
				"Username:", getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.P_SEND,
				"Send data to server:",
				1,
				new String[][] {
						{ "After every save", PreferenceConstants.SAVE },
						{ "When recording is stopped", PreferenceConstants.STOP } },
				getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

}
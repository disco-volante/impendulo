package za.ac.sun.cs.intlola.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import za.ac.sun.cs.intlola.Intlola;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(Intlola.getDefault().getPreferenceStore());
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
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.P_SEND,
				"Send data to server:",
				1,
				new String[][] {
						{ "After every save", PreferenceConstants.MULTIPLE },
						{ "When recording is stopped", PreferenceConstants.SINGLE },
						{ "Never", PreferenceConstants.NEVER } },
				getFieldEditorParent()));
	}

	@Override
	public void init(final IWorkbench workbench) {
	}

}
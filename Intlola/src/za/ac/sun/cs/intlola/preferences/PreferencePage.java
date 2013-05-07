package za.ac.sun.cs.intlola.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.IntlolaMode;

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

	public void init(final IWorkbench workbench) {
	}

}
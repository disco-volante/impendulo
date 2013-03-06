package za.ac.sun.cs.intlola.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import za.ac.sun.cs.intlola.Intlola;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Intlola.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_SEND, PreferenceConstants.STOP);
		store.setDefault(PreferenceConstants.P_UNAME, PreferenceConstants.DEFAULT);
	}

}

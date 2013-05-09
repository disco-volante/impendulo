package za.ac.sun.cs.intlola.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import za.ac.sun.cs.intlola.Intlola;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Intlola.getActive().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_MODE,
				PreferenceConstants.FILE_REMOTE);
		store.setDefault(PreferenceConstants.P_UNAME,
				PreferenceConstants.DEFAULT);
		store.setDefault(PreferenceConstants.P_ADDRESS,
				PreferenceConstants.REMOTE_ADDRESS);
		store.setDefault(PreferenceConstants.P_PORT, PreferenceConstants.PORT);
	}

}

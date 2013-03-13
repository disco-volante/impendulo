package za.ac.sun.cs.intlola;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public enum SendMode {
	ONSAVE, ONSTOP, NEVER, LOGIN;

	public static SendMode getMode(String mpref) {
		SendMode ret = null;
		if (mpref.equals(PreferenceConstants.SAVE)) {
			ret = ONSAVE;
		} else if (mpref.equals(PreferenceConstants.STOP)) {
			ret = ONSTOP;
		} else if(mpref.equals(PreferenceConstants.NEVER)){
			ret = NEVER;
		}
		return ret;
	}
}
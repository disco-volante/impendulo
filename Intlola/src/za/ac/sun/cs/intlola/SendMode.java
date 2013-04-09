package za.ac.sun.cs.intlola;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public enum SendMode {
	MULTIPLE, SINGLE, TEST, NEVER;

	public static SendMode getMode(final String mpref) {
		SendMode ret = null;
		if (mpref.equals(PreferenceConstants.MULTIPLE)) {
			ret = MULTIPLE;
		} else if (mpref.equals(PreferenceConstants.SINGLE)) {
			ret = SINGLE;
		} else if (mpref.equals(PreferenceConstants.TEST)) {
			ret = TEST;
		} else if (mpref.equals(PreferenceConstants.NEVER)) {
			ret = NEVER;
		} else {
			throw new EnumConstantNotPresentException(SendMode.class, mpref);
		}
		return ret;
	}
}
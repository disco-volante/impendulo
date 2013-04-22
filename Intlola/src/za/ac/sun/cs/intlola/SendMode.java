package za.ac.sun.cs.intlola;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public enum SendMode {
	ARCHIVE, INDIVIDUAL, TEST, NEVER;

	public static SendMode getMode(final String mpref) {
		SendMode ret = null;
		if (mpref.equals(PreferenceConstants.INDIVIDUAL)) {
			ret = INDIVIDUAL;
		} else if (mpref.equals(PreferenceConstants.ARCHIVE)) {
			ret = ARCHIVE;
		} else if (mpref.equals(PreferenceConstants.TEST)) {
			ret = TEST;
		} else if (mpref.equals(PreferenceConstants.NEVER)) {
			ret = NEVER;
		} else {
			throw new EnumConstantNotPresentException(SendMode.class, mpref);
		}
		return ret;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
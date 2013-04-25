package za.ac.sun.cs.intlola;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public enum IntlolaMode {
	ARCHIVE_REMOTE, ARCHIVE_LOCAL, FILE_REMOTE, ARCHIVE_TEST;

	public static IntlolaMode getMode(final String mpref) {
		IntlolaMode ret = null;
		if (mpref.equals(PreferenceConstants.FILE_REMOTE)) {
			ret = FILE_REMOTE;
		} else if (mpref.equals(PreferenceConstants.ARCHIVE_REMOTE)) {
			ret = ARCHIVE_REMOTE;
		} else if (mpref.equals(PreferenceConstants.ARCHIVE_TEST)) {
			ret = ARCHIVE_TEST;
		} else if (mpref.equals(PreferenceConstants.ARCHIVE_LOCAL)) {
			ret = ARCHIVE_LOCAL;
		} else {
			throw new EnumConstantNotPresentException(IntlolaMode.class, mpref);
		}
		return ret;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public boolean isRemote() {
		return this.equals(ARCHIVE_REMOTE) || this.equals(FILE_REMOTE) || this.equals(ARCHIVE_TEST);
	}

	public boolean isArchive() {
		return this.equals(ARCHIVE_REMOTE) || this.equals(ARCHIVE_LOCAL) || this.equals(ARCHIVE_TEST);
	}
}
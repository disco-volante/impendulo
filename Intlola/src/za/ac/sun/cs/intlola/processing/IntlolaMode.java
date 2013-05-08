package za.ac.sun.cs.intlola.processing;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public enum IntlolaMode {
	ARCHIVE_REMOTE("Send all snapshots to server once recording has stopped."), ARCHIVE_LOCAL(
			"Save all snapshots locally in archive."), FILE_REMOTE(
			"Continuously send snapshots to server."), ARCHIVE_TEST(
			"Send project tests to server.");

	private String description;

	IntlolaMode(String description) {
		this.description = description;
	}

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

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public boolean isRemote() {
		return this.equals(ARCHIVE_REMOTE) || this.equals(FILE_REMOTE)
				|| this.equals(ARCHIVE_TEST);
	}

	public boolean isArchive() {
		return this.equals(ARCHIVE_REMOTE) || this.equals(ARCHIVE_LOCAL)
				|| this.equals(ARCHIVE_TEST);
	}
}
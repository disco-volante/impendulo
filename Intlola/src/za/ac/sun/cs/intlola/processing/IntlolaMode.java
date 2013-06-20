package za.ac.sun.cs.intlola.processing;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public enum IntlolaMode {
	ARCHIVE_LOCAL("Save all snapshots locally in archive."), ARCHIVE_REMOTE(
			"Send all snapshots to server once recording has stopped."), FILE_REMOTE(
			"Continuously send snapshots to server."), NONE("None");

	public static IntlolaMode getMode(final String mpref) {
		IntlolaMode ret = null;
		if (mpref.equals(PreferenceConstants.FILE_REMOTE)) {
			ret = FILE_REMOTE;
		} else if (mpref.equals(PreferenceConstants.ARCHIVE_REMOTE)) {
			ret = ARCHIVE_REMOTE;
		} else if (mpref.equals(PreferenceConstants.ARCHIVE_LOCAL)) {
			ret = ARCHIVE_LOCAL;
		} else {
			throw new EnumConstantNotPresentException(IntlolaMode.class, mpref);
		}
		return ret;
	}

	private String	description;

	IntlolaMode(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public boolean isArchive() {
		return equals(ARCHIVE_REMOTE) || equals(ARCHIVE_LOCAL);
	}

	public boolean isRemote() {
		return equals(ARCHIVE_REMOTE) || equals(FILE_REMOTE);
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
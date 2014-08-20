//Copyright (c) 2013, The Impendulo Authors
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification,
//are permitted provided that the following conditions are met:
//
//  Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  Redistributions in binary form must reproduce the above copyright notice, this
//  list of conditions and the following disclaimer in the documentation and/or
//  other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
//ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
//ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package za.ac.sun.cs.intlola.util;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

/**
 * IntlolaMode represents the mode in which Intlola is run.
 * 
 * @author godfried
 * 
 */
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

	private String description;

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
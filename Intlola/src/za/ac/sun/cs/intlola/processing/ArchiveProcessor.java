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

package za.ac.sun.cs.intlola.processing;

import java.io.IOException;
import java.util.Calendar;

import za.ac.sun.cs.intlola.file.ArchiveFile;
import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.processing.tasks.ArchiveBuilder;
import za.ac.sun.cs.intlola.processing.tasks.FileSender;
import za.ac.sun.cs.intlola.processing.tasks.SessionEnder;
import za.ac.sun.cs.intlola.util.IO;
import za.ac.sun.cs.intlola.util.IntlolaMode;

public class ArchiveProcessor extends RemoteProcessor {

	public ArchiveProcessor(final IPaths paths) throws IOException {
		super(paths);
	}

	public IntlolaMode getMode() {
		return IntlolaMode.ARCHIVE_REMOTE;
	}

	/**
	 * logout ends communication with Impendulo and shuts down the processor.
	 */
	public void stop() {
		String zipName = IO.joinPath(paths.storePath(), "intlola.zip");
		executor.execute(new ArchiveBuilder(paths.archivePath(), zipName));
		executor.execute(new FileSender(new ArchiveFile(zipName), sock, snd,
				rcv));
		IO.delete(zipName);
		executor.execute(new SessionEnder(sock, snd, rcv));
		executor.shutdown();
	}

	public static void save(String dest, String origin, final char kindSuffix,
			String tipe) throws IOException {
		dest = IO.joinPath(dest, IO.encodeName(origin, Calendar.getInstance()
				.getTimeInMillis(), kindSuffix));
		if (IO.isSrc(origin)) {
			IO.copy(origin, dest);
		} else {
			IO.touch(dest);
		}
	}

	public void processFile(final String path, final char kindSuffix,
			String tipe) throws IOException {
		ArchiveProcessor.save(paths.archivePath(), path, kindSuffix, tipe);
	}
}

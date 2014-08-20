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

import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.processing.tasks.ArchiveBuilder;
import za.ac.sun.cs.intlola.util.IntlolaMode;
import za.ac.sun.cs.intlola.util.InvalidModeException;

/**
 * Processor interacts with Impendulo over TCP. It logs the user in to
 * Impendulo, retrieves available projects, sends files and logs them out.
 * 
 * @author godfried
 * 
 */
public class LocalProcessor extends Processor {

	/**
	 * Construct processor with default values.
	 * 
	 * @param username
	 * @param project
	 * @param mode
	 * @param address
	 * @param port
	 * @throws InvalidModeException
	 * @throws IOException
	 */
	public LocalProcessor(final IPaths paths) throws IOException {
		super(paths);
	}

	public IntlolaMode getMode() {
		return IntlolaMode.ARCHIVE_LOCAL;
	}

	/**
	 * handleLocalArchive shuts down the processor if the snapshots are being
	 * stored locally.
	 * 
	 * @param zipName
	 */
	public void saveArchive(final String name) {
		executor.execute(new ArchiveBuilder(paths.archivePath(), name));
	}

	@Override
	public void stop() {
		executor.shutdown();
	}

	@Override
	public void processFile(String path, char kindSuffix, String tipe)
			throws IOException {
		ArchiveProcessor.save(paths.archivePath(), path, kindSuffix, tipe);
	}

}

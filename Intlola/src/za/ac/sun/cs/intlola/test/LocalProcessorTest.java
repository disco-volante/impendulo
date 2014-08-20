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

package za.ac.sun.cs.intlola.test;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResourceDelta;

import za.ac.sun.cs.intlola.processing.LocalProcessor;
import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.processing.paths.TestPaths;
import za.ac.sun.cs.intlola.util.InvalidModeException;

public class LocalProcessorTest extends Thread {

	public static void main(String[] args) throws InterruptedException {
		Thread t = new LocalProcessorTest();
		t.start();
		t.join();
	}

	public void run() {
		Thread[] runners = new Thread[Settings.THREAD_COUNT];
		for (int i = 0; i < Settings.THREAD_COUNT; i++) {
			try {
				runners[i] = new LocalArchiver();
				runners[i].start();
			} catch (InvalidModeException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (Thread runner : runners) {
			try {
				runner.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static class LocalArchiver extends Thread {
		private LocalProcessor proc;
		private IPaths paths;

		public LocalArchiver() throws InvalidModeException, IOException {
			paths = new TestPaths(Settings.PROJECT_LOCATION);

			proc = new LocalProcessor(paths);
		}

		public void run() {
			int count = 0;
			while (count < Settings.FILE_COUNT) {
				try {
					sleep(Settings.SLEEP_DURATION);
					proc.processChanges(Settings.FILE_NAME,
							IResourceDelta.CHANGED);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				count++;
			}
			int fileCount = new File(paths.archivePath()).listFiles().length;
			if (fileCount != Settings.FILE_COUNT) {
				System.err.println(String.format(
						"Expected %d files but found %d in archive.",
						Settings.FILE_COUNT, fileCount));
			}
			try {
				proc.saveArchive(Settings.archiveName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			proc.stop();
		}
	}
}

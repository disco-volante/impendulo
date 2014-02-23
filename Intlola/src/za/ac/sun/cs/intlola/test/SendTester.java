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

import java.io.IOException;
import java.util.Calendar;

import org.eclipse.core.resources.IResourceDelta;

import za.ac.sun.cs.intlola.processing.IOUtils;
import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;
import za.ac.sun.cs.intlola.processing.InvalidModeException;
import za.ac.sun.cs.intlola.processing.Processor;
import za.ac.sun.cs.intlola.processing.json.Project;
import za.ac.sun.cs.intlola.processing.json.ProjectInfo;

public class SendTester {
	private static final int runnerCount = 10;

	public static void main(String[] args) {
		Thread[] runners = new Thread[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			IntlolaMode mode = i % 2 == 0 ? IntlolaMode.FILE_REMOTE
					: IntlolaMode.ARCHIVE_REMOTE;
			try {
				runners[i] = new FileSender(mode);
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

	public static class FileSender extends Thread {
		private static final int sendCount = 5;
		private Processor proc;

		public FileSender(IntlolaMode mode) throws InvalidModeException, IOException {
			String storePath = IOUtils.joinPath(System.getProperty("java.io.tmpdir"),String.valueOf(Calendar.getInstance().getTimeInMillis()));
			String projectLocation = "/home/godfried/dev/java/ImpenduloProjects/Triangle";
			String skeletonInfoPath = IOUtils.joinPath(projectLocation, ".impendulo_info.json");
			proc = new Processor(mode, projectLocation, storePath, skeletonInfoPath);
		}

		public void run() {
			IntlolaError error = proc.login("pjordaan", "1brandwag",
					"localhost", 8010);
			if (!error.equals(IntlolaError.SUCCESS)) {
				System.err.println(error.getDescription());
				return;
			}
			Project selected = null;
			for (ProjectInfo pi : proc.getProjects()) {
				if (pi.getProject().Name.equals("Triangle")) {
					selected = pi.getProject();
					break;
				}
			}
			error = proc.createSubmission(selected);
			if (!error.equals(IntlolaError.SUCCESS)) {
				System.err.println(error.getDescription());
				return;
			}
			int count = 0;
			while (count < sendCount) {
				try {
					sleep(10);
					proc.processChanges(
							"/home/godfried/dev/java/ImpenduloProjects/Triangle/src/triangle/Triangle.java",
							IResourceDelta.CHANGED);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				count++;
			}
			if (proc.getMode().isRemote()) {
				proc.logout();
			}
		}
	}
}

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

import org.eclipse.core.resources.IResourceDelta;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.processing.FileProcessor;
import za.ac.sun.cs.intlola.processing.json.Assignment;
import za.ac.sun.cs.intlola.processing.json.AssignmentInfo;
import za.ac.sun.cs.intlola.processing.json.Project;
import za.ac.sun.cs.intlola.processing.json.ProjectInfo;
import za.ac.sun.cs.intlola.processing.paths.TestPaths;
import za.ac.sun.cs.intlola.util.IntlolaError;
import za.ac.sun.cs.intlola.util.InvalidModeException;

public class FileProcessorTest extends Thread {
	public static void main(String[] args) throws InterruptedException {
		Thread t = new FileProcessorTest();
		t.start();
		t.join();
	}

	public void run() {
		Thread[] runners = new Thread[Settings.THREAD_COUNT];
		for (int i = 0; i < Settings.THREAD_COUNT; i++) {
			try {
				runners[i] = new FileSender();
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
		private FileProcessor proc;

		public FileSender() throws InvalidModeException, IOException {
			proc = new FileProcessor(new TestPaths(Settings.PROJECT_LOCATION));

		}

		public void run() {
			IntlolaError error = proc.login(Const.LOGIN, Settings.USER_NAME,
					Settings.PASSWORD, Settings.ADDRESS, Settings.PORT);
			if (!error.equals(IntlolaError.SUCCESS)) {
				System.err.println(error.getDescription());
			}
			Project p = null;
			Assignment a = null;
			for (ProjectInfo pi : proc.getProjects()) {
				if (pi.getProject().Name.equals(Settings.PROJECT_NAME)) {
					AssignmentInfo[] as = pi.getAssignments();
					if (as.length > 0) {
						a = as[0].getAssignment();
						p = pi.getProject();
					}
					break;
				}
			}
			if (p != null && a != null) {
				error = proc.createSubmission(p, a);
				if (!error.equals(IntlolaError.SUCCESS)) {
					System.err.println(error.getDescription());
				}
			} else {
				System.err.println("Could not load project and/or assignment");
			}
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
			proc.stop();
		}
	}
}

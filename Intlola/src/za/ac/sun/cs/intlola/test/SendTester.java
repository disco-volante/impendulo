package za.ac.sun.cs.intlola.test;

import java.io.IOException;

import org.eclipse.core.resources.IResourceDelta;

import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;
import za.ac.sun.cs.intlola.processing.Processor;

public class SendTester {
	private static final int runnerCount = 2;

	public static void main(String[] args) {
		Thread[] runners = new Thread[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			IntlolaMode mode = IntlolaMode.FILE_REMOTE;
			runners[i] = new FileSender(mode);
			runners[i].start();
		}
		for (Thread runner : runners) {
			try {
				runner.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			;
		}
	}

	public static class FileSender extends Thread {
		private static final int sendCount = 2;
		private Processor proc;

		public FileSender(IntlolaMode mode) {
			proc = new Processor(mode, System.getProperty("java.io.tmpdir"));
		}

		public void run() {
			IntlolaError error = proc.login("username", "password",
					"localhost", 8010);
			if (!error.equals(IntlolaError.SUCCESS)) {
				System.err.println(error.getDescription());
				return;
			}
			error = proc.createSubmission(proc.getAvailableProjects()[0]);
			if (!error.equals(IntlolaError.SUCCESS)) {
				System.err.println(error.getDescription());
				return;
			}
			while (proc.getFileCounter() < sendCount) {
				try {
					proc.processChanges(
							"/home/godfried/dev/java/intlola/Triangle/src/triangle/Triangle.java",
							true, IResourceDelta.CHANGED);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (proc.getMode().isRemote()) {
				proc.logout();
			}
		}
	}
}

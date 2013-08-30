package za.ac.sun.cs.intlola.test;

import java.io.IOException;

import org.eclipse.core.resources.IResourceDelta;

import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;
import za.ac.sun.cs.intlola.processing.Processor;
import za.ac.sun.cs.intlola.processing.Project;

public class SendTester {
	private static final int runnerCount = 20;
	public static void main(String[] args) {
		Thread[] runners = new Thread[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			IntlolaMode mode = i % 2 == 0 ? IntlolaMode.FILE_REMOTE : IntlolaMode.ARCHIVE_REMOTE;
			runners[i] = new FileSender(mode);
			runners[i].start();
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
			Project selected = null;
			for(Project p : proc.getAvailableProjects()){
				if(p.Name.equals("Triangle")){
					selected = p;
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
					proc.processChanges(
							"/home/godfried/dev/java/ImpenduloProjects/Triangle/src/triangle/Triangle.java",
							true, IResourceDelta.CHANGED);
				} catch (IOException e) {
					e.printStackTrace();
				}
				count ++;
			}
			if (proc.getMode().isRemote()) {
				proc.logout();
			}
		}
	}
}

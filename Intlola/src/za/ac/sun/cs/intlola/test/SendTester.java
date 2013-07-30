package za.ac.sun.cs.intlola.test;

import za.ac.sun.cs.intlola.file.IndividualFile;
import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;
import za.ac.sun.cs.intlola.processing.Processor;

public class SendTester{
	private static final int runnerCount = 1;
	public static void main(String[] args){
		Thread[] runners = new Thread[runnerCount]; 
		for(int i = 0; i < runnerCount; i ++){
			runners[i] = new FileSender();
			runners[i].start();
		}
		for(Thread runner : runners){
			try {
				runner.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
		}
	}
	
	public static class FileSender extends Thread {
		private static final int sendCount = 2;
		private Processor proc;
		private int count;

		public FileSender() {
			proc = new Processor("username", IntlolaMode.FILE_REMOTE,
					"localhost", 8010);
			count = 0;
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
			while (count < sendCount) {
				proc.sendFile(new IndividualFile(
						"/home/godfried/dev/java/intlola/Triangle/src/triangle/Triangle.java",
						'c', count++, true));
			}
			proc.logout();
		}
	}
}

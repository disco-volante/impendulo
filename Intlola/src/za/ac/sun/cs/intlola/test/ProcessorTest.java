package za.ac.sun.cs.intlola.test;

public class ProcessorTest {
	public static void main(String[] args) throws Exception {
		Thread[] tests = new Thread[] { new FileProcessorTest(),
				new ArchiveProcessorTest(), new LocalProcessorTest() };
		for (Thread t : tests) {
			t.start();
		}
		for (Thread t : tests) {
			t.join();
		}
	}
}

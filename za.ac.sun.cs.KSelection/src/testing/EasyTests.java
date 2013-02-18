package testing;

import kselection.KSelection;
import junit.framework.Test;
import junit.framework.TestSuite;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;

import junit.framework.TestCase;

public class EasyTests {

	public static long startTime = -1;

	public static long timeLimit = 2 * 60 * 1000;

	private static class FileTest extends TestCase {

		protected int inputK = 0;

		protected int inputData[] = null;

		protected long outputValue = 0;

		protected boolean brokenTest = false;

		public FileTest(String s) {
			super(s);
			try {
				BufferedReader r = new BufferedReader(new FileReader(getName()));
				StreamTokenizer t = new StreamTokenizer(r);
				t.parseNumbers();
				t.nextToken();
				inputK = (int) t.nval;
				t.nextToken();
				int n = (int) t.nval;
				inputData = new int[2 * n];
				for (int i = 0; i < 2 * n; i++) {
					t.nextToken();
					inputData[i] = (int) t.nval;
				}
				t.nextToken();
				outputValue = (int) t.nval;
			} catch (Exception e) {
				e.printStackTrace();
				brokenTest = true;
			}
		}

		public void runTest() {
			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			}
			else if (System.currentTimeMillis() - startTime > timeLimit) {
				assertTrue("Out of time", false);
			}
			else {
				assertFalse(brokenTest);
				KSelection ks = new KSelection();
				int answer = ks.kselect(inputK, inputData);
				assertTrue("Wrong answer " + answer + ", should be " + outputValue, answer == outputValue);
			}
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for k-selection");
		File f = new File("src/testing/data");
		String s[] = f.list();
		for (int i = 0; i < s.length; i++) {
			String n = s[i];
			if (n.endsWith(".etxt")) {
				suite.addTest(new FileTest("src/testing/data/" + n));
			}
		}
		return suite;
	}

}

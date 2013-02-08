package testing;

import triangle.Triangle;
import junit.framework.Test;
import junit.framework.TestSuite;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;

import junit.framework.TestCase;

public class AllTests {

	public static long startTime = -1;

	public static long timeLimit = 8 * 60 * 1000;

	private static class FileTest extends TestCase {

		protected int inputData[][] = null;

		protected long outputValue = 0;

		protected boolean brokenTest = false;

		public FileTest(String s) {
			super(s);
			try {
				BufferedReader r = new BufferedReader(new FileReader(getName()));
				StreamTokenizer t = new StreamTokenizer(r);
				t.parseNumbers();
				t.nextToken();
				int h = (int) t.nval;
				inputData = new int[h][];
				for (int i = 0; i < h; i++) {
					inputData[i] = new int[i + 1]; 
					for (int j = 0; j <= i; j++) {
						t.nextToken();
						inputData[i][j] = (int) t.nval;
					}
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
				Triangle tri = new Triangle();
				int answer = tri.maxpath(inputData);
				assertTrue("Wrong answer " + answer + ", should be " + outputValue, answer == outputValue);
			}
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for WTC");
		File f = new File("src/testing/data");
		String s[] = f.list();
		for (int i = 0; i < s.length; i++) {
			String n = s[i];
			if (n.endsWith(".txt")) {
				suite.addTest(new FileTest("src/testing/data/" + n));
			}
		}
		return suite;
	}

}

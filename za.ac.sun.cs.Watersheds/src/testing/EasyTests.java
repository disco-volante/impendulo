package testing;

import watersheds.Watersheds;
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

		protected int inputH = 0;

		protected int inputW = 0;

		protected int inputAlt[][] = null;

		protected char outputValue[][] = null;

		protected boolean brokenTest = false;

		public FileTest(String s) {
			super(s);
			try {
				BufferedReader r = new BufferedReader(new FileReader(getName()));
				StreamTokenizer t = new StreamTokenizer(r);
				t.parseNumbers();
				t.nextToken();
				inputW = (int) t.nval;
				t.nextToken();
				inputH = (int) t.nval;
				inputAlt = new int[inputH][];
				for (int i = 0; i < inputH; i++) {
					inputAlt[i] = new int[inputW]; 
					for (int j = 0; j < inputW; j++) {
						t.nextToken();
						inputAlt[i][j] = (int) t.nval;
					}
				}
				for (int i = 0; i < inputH; i++) {
					inputAlt[i] = new int[inputW]; 
					for (int j = 0; j < inputW; j++) {
						t.nextToken();
						System.out.println("*** (" + t.sval + ")");
						outputValue[i][j] = t.sval.charAt(0);
					}
				}
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
				Watersheds ws = new Watersheds();
				char answer[][] = new char[inputH][];
				for (int i = 0; i < inputH; i++) {
					answer[i] = new char[inputW];
					for (int j = 0; j < inputW; j++) {
						answer[i][j] = '?';
					}
				}
				ws.drainage(inputH, inputW, inputAlt, answer);
				for (int i = 0; i < inputH; i++) {
					for (int j = 0; j < inputW; j++) {
						assertTrue("Wrong answer (" + j + ", " + i + "), should be " + outputValue[i][j] + " not " + answer[i][j], answer[i][j] == outputValue[i][j]);
					}
				}
			}
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for triangle");
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

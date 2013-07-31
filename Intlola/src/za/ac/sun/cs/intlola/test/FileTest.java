package za.ac.sun.cs.intlola.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Calendar;

import org.junit.Test;

import za.ac.sun.cs.intlola.file.FileUtils;
import za.ac.sun.cs.intlola.file.IndividualFile;
import za.ac.sun.cs.intlola.file.IntlolaFile;
/**
 * Tests for {@link FileUtils}.
 * @author disco
 *
 */
public class FileTest {
	private static final long TIME_1 = Calendar.getInstance().getTimeInMillis();
	private static final int NUM_1 = 100;
	private static final char MOD_1 = FileUtils.SAVE;
	private static final String FNAME_1 = "Intlola.java";
	private static final String PKG_1 = "za" + FileUtils.NAME_SEP + "sun"
			+ FileUtils.NAME_SEP + "cs" + FileUtils.NAME_SEP + "intlola";
	private static final String PKG_PATH_1 = "za" + File.separator + "sun"
			+ File.separator + "cs" + File.separator + "intlola";
	private static final String ENC_PKG_1 = "za" + FileUtils.COMPONENT_SEP
			+ "sun" + FileUtils.COMPONENT_SEP + "cs" + FileUtils.COMPONENT_SEP
			+ "intlola";
	private static final String PREFIX_1 = "bla" + FileUtils.COMPONENT_SEP
			+ "src";
	private static final String PREFIX_2 = "bla" + File.separator + "src";
	private static final String ENC_NAME_1 = PREFIX_1 + FileUtils.COMPONENT_SEP
			+ ENC_PKG_1 + FileUtils.COMPONENT_SEP + FNAME_1
			+ FileUtils.COMPONENT_SEP + TIME_1 + FileUtils.COMPONENT_SEP
			+ NUM_1 + FileUtils.COMPONENT_SEP + MOD_1;
	private static final String ENC_NAME_2 = ENC_PKG_1
			+ FileUtils.COMPONENT_SEP + FNAME_1 + FileUtils.COMPONENT_SEP
			+ TIME_1 + FileUtils.COMPONENT_SEP + NUM_1
			+ FileUtils.COMPONENT_SEP + MOD_1;
	private static final IntlolaFile IFILE_1 = new IndividualFile(ENC_NAME_1,
			FNAME_1, PKG_1, TIME_1, NUM_1, MOD_1, true);
	private static final String PATH_1 = PREFIX_2 + File.separator + PKG_PATH_1
			+ File.separator + FNAME_1;
	private static final String PATH_2 = PKG_PATH_1 + File.separator + FNAME_1;
	private static final String PATH_3 = PREFIX_2 + File.separator + FNAME_1;
	private static final String EMPTY = "";

	@Test
	public void testCopy() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateZip() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecodeName() {
		IntlolaFile actual = FileUtils.decodeName(ENC_NAME_1);
		assertEquals(IFILE_1, actual);
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncodeName() {
		String actual = FileUtils.encodeName(PATH_1, TIME_1, NUM_1, MOD_1);
		assertEquals(ENC_NAME_2, actual);
	}

	@Test
	public void testGetFileName() {
		String res1 = FileUtils.getFileName(ENC_NAME_1
				.split(FileUtils.COMPONENT_SEP));
		String res2 = FileUtils.getFileName(PKG_1.split(FileUtils.NAME_SEP));
		assertEquals(FNAME_1, res1);
		assertEquals(EMPTY, res2);
	}

	@Test
	public void testGetPackage() {
		String res1 = FileUtils.getPackage(PATH_1.split(File.separator),
				FileUtils.COMPONENT_SEP);
		String res2 = FileUtils.getPackage(PATH_1.split(File.separator),
				FileUtils.NAME_SEP);
		String res3 = FileUtils.getPackage(PATH_2.split(File.separator),
				FileUtils.NAME_SEP);
		String res4 = FileUtils.getPackage(PATH_3.split(File.separator),
				FileUtils.NAME_SEP);
		assertEquals(ENC_PKG_1, res1);
		assertEquals(PKG_1, res2);
		assertEquals(EMPTY, res3);
		assertEquals(EMPTY, res4);

	}

	@Test
	public void testSaveString() {
		fail("Not yet implemented");
	}

	@Test
	public void testTouch() {
		fail("Not yet implemented");
	}

}

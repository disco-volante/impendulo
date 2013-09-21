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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Calendar;

import org.junit.Test;

import za.ac.sun.cs.intlola.file.IndividualFile;
import za.ac.sun.cs.intlola.file.IntlolaFile;
import za.ac.sun.cs.intlola.processing.IOUtils;

/**
 * Tests for {@link IOUtils}.
 * 
 * @author disco
 * 
 */
public class FileTest {
	private static final long TIME_1 = Calendar.getInstance().getTimeInMillis();
	private static final int NUM_1 = 100;
	private static final char MOD_1 = IOUtils.SAVE;
	private static final String FNAME_1 = "Intlola.java";
	private static final String PKG_1 = "za" + IOUtils.NAME_SEP + "sun"
			+ IOUtils.NAME_SEP + "cs" + IOUtils.NAME_SEP + "intlola";
	private static final String PKG_PATH_1 = "za" + File.separator + "sun"
			+ File.separator + "cs" + File.separator + "intlola";
	private static final String ENC_PKG_1 = "za" + IOUtils.COMPONENT_SEP
			+ "sun" + IOUtils.COMPONENT_SEP + "cs" + IOUtils.COMPONENT_SEP
			+ "intlola";
	private static final String PREFIX_1 = "bla" + IOUtils.COMPONENT_SEP
			+ "src";
	private static final String PREFIX_2 = "bla" + File.separator + "src";
	private static final String ENC_NAME_1 = PREFIX_1 + IOUtils.COMPONENT_SEP
			+ ENC_PKG_1 + IOUtils.COMPONENT_SEP + FNAME_1
			+ IOUtils.COMPONENT_SEP + TIME_1 + IOUtils.COMPONENT_SEP + NUM_1
			+ IOUtils.COMPONENT_SEP + MOD_1;
	private static final String ENC_NAME_2 = ENC_PKG_1 + IOUtils.COMPONENT_SEP
			+ FNAME_1 + IOUtils.COMPONENT_SEP + TIME_1 + IOUtils.COMPONENT_SEP
			+ NUM_1 + IOUtils.COMPONENT_SEP + MOD_1;
	private static final IntlolaFile IFILE_1 = new IndividualFile(ENC_NAME_1,
			FNAME_1, PKG_1, TIME_1, MOD_1, true);
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
		IntlolaFile actual = IOUtils.decodeName(ENC_NAME_1);
		assertEquals(IFILE_1, actual);
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncodeName() {
		String actual = IOUtils.encodeName(PATH_1, TIME_1, MOD_1);
		assertEquals(ENC_NAME_2, actual);
	}

	@Test
	public void testGetFileName() {
		String res1 = IOUtils.getFileName(ENC_NAME_1
				.split(IOUtils.COMPONENT_SEP));
		String res2 = IOUtils.getFileName(PKG_1.split(IOUtils.NAME_SEP));
		assertEquals(FNAME_1, res1);
		assertEquals(EMPTY, res2);
	}

	@Test
	public void testGetPackage() {
		String res1 = IOUtils.getPackage(PATH_1.split(File.separator),
				IOUtils.COMPONENT_SEP);
		String res2 = IOUtils.getPackage(PATH_1.split(File.separator),
				IOUtils.NAME_SEP);
		String res3 = IOUtils.getPackage(PATH_2.split(File.separator),
				IOUtils.NAME_SEP);
		String res4 = IOUtils.getPackage(PATH_3.split(File.separator),
				IOUtils.NAME_SEP);
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

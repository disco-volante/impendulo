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

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.IndividualFile;
import za.ac.sun.cs.intlola.file.IntlolaFile;
import za.ac.sun.cs.intlola.util.IO;
import za.ac.sun.cs.intlola.util.InvalidArgumentException;

/**
 * Tests for {@link IO}.
 * 
 * @author disco
 * 
 */
public class IOTest {
	private static final long TIME_1 = Calendar.getInstance().getTimeInMillis();
	private static final char MOD_1 = IO.SAVE;
	private static final String FNAME_1 = "Intlola.java";
	private static final String PKG_1 = "za" + IO.NAME_SEP + "sun"
			+ IO.NAME_SEP + "cs" + IO.NAME_SEP + "intlola";
	private static final String PKG_PATH_1 = "za" + File.separator + "sun"
			+ File.separator + "cs" + File.separator + "intlola";
	private static final String ENC_PKG_1 = "za" + IO.COMPONENT_SEP
			+ "sun" + IO.COMPONENT_SEP + "cs" + IO.COMPONENT_SEP
			+ "intlola";
	private static final String PREFIX_1 = "bla" + IO.COMPONENT_SEP
			+ "src";
	private static final String PREFIX_2 = "bla" + File.separator + "src";
	private static final String ENC_NAME_1 = PREFIX_1 + IO.COMPONENT_SEP
			+ ENC_PKG_1 + IO.COMPONENT_SEP + FNAME_1
			+ IO.COMPONENT_SEP + TIME_1 + IO.COMPONENT_SEP + MOD_1;
	private static final String ENC_NAME_2 = ENC_PKG_1 + IO.COMPONENT_SEP
			+ FNAME_1 + IO.COMPONENT_SEP + TIME_1 + IO.COMPONENT_SEP
			+ MOD_1;
	private static final IntlolaFile IFILE_1 = new IndividualFile(ENC_NAME_1,
			FNAME_1, PKG_1, TIME_1, MOD_1, Const.SRC);
	private static final String PATH_1 = PREFIX_2 + File.separator + PKG_PATH_1
			+ File.separator + FNAME_1;
	private static final String PATH_2 = PKG_PATH_1 + File.separator + FNAME_1;
	private static final String PATH_3 = PREFIX_2 + File.separator + FNAME_1;
	private static final String EMPTY = "";
	
	@Before
	public void setup(){
		try {
			IO.setExtension("java");
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDecodeName() {
		IntlolaFile actual = IO.decodeName(ENC_NAME_1);
		assertEquals(IFILE_1, actual);
	}

	@Test
	public void testEncodeName() {
		String actual = IO.encodeName(PATH_1, TIME_1, MOD_1);
		assertEquals(ENC_NAME_2, actual);
	}

	@Test
	public void testGetFileName() {
		String res1 = IO.getFileName(ENC_NAME_1
				.split(IO.COMPONENT_SEP));
		String res2 = IO.getFileName(PKG_1.split(IO.NAME_SEP));
		assertEquals(FNAME_1, res1);
		assertEquals(EMPTY, res2);
	}

	@Test
	public void testGetPackage() {
		String res1 = IO.getPackage(PATH_1.split(File.separator),
				IO.COMPONENT_SEP);
		String res2 = IO.getPackage(PATH_1.split(File.separator),
				IO.NAME_SEP);
		String res3 = IO.getPackage(PATH_2.split(File.separator),
				IO.NAME_SEP);
		String res4 = IO.getPackage(PATH_3.split(File.separator),
				IO.NAME_SEP);
		assertEquals(ENC_PKG_1, res1);
		assertEquals(PKG_1, res2);
		assertEquals(EMPTY, res3);
		assertEquals(EMPTY, res4);

	}

	@Test
	public void testIgnore() {
		Map<String, Boolean> tests = new HashMap<String, Boolean>() {
			private static final long serialVersionUID = 1L;

			{
				put("/src/Triangle", false);
				put("/src/.Triangle", true);
				put("/src/bin", true);
				put("/src/testing", false);
				put("/src/lib/Triangle.java", false);
			}
		};
		for (Entry<String, Boolean> test : tests.entrySet()) {
			assertEquals(test.getValue(), IO.ignore(test.getKey()));
		}
	}
}

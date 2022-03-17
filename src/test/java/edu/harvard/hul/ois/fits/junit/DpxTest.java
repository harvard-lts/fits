/* 
 * Copyright 2022 Harvard University Library
 * 
 * This file is part of FITS (File Information Tool Set).
 * 
 * FITS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FITS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FITS.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.hul.ois.fits.junit;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;


public class DpxTest extends AbstractLoggingTest {

	/*
	 *  Only one Fits instance is needed to run all tests.
	 *  This also speeds up the tests.
	 */
	private static Fits fits;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// Set up FITS for entire class.
		fits = new Fits();
	}

	@AfterClass
	public static void afterClass() {
		fits = null;
	}

	@Test
	public void testDpxFormatDetection() throws Exception {
		String inputFilename = "00266.dpx";
		File input = new File("testfiles/" + inputFilename);
		FitsOutput fitsOut = fits.examine(input);
		fitsOut.saveToDisk("test-generated-output/" + inputFilename + OUTPUT_FILE_SUFFIX);

		List <FitsIdentity> identities = fitsOut.getIdentities();

		for (FitsIdentity identity : identities) {
			if (!identity.getMimetype().contains("image/x-dpx")) {
				fail(inputFilename + " should be identified as a DPX file with mimetype of image/x-dpx");
			}
		}
	}

}

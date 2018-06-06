/* 
 * Copyright 2014 Harvard University Library
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

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;

public class VideoStdSchemaTest extends AbstractLoggingTest {
	
	/*
	 *  Only one Fits instance is needed to run all tests.
	 *  This also speeds up the tests.
	 */
	private static Fits fits;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// Set up FITS for entire class.
		fits = new Fits();
// Use the following two lines to turn on tool output
//		File fitsConfigFile = new File("testfiles/properties/fits-full-with-tool-output.xml");
//		fits = new Fits(null, fitsConfigFile);
	}
	
	@AfterClass
	public static void afterClass() {
		fits = null;
	}
    
    @Test  
	public void testVideo_DV() throws Exception {   

		String fileName = "FITS-SAMPLE-26.mov";
    	File input = new File("testfiles/" + fileName);
    	FitsOutput fitsOut = fits.examine(input);
		fitsOut.addStandardCombinedFormat();
		fitsOut.saveToDisk("test-generated-output/" + fileName + "_Output.xml");
	}    
    
    @Test
    public void testMxfVideo() throws Exception {
    	
    	// Here we want a specific fits.xml file so instantiate a new Fits class.
    	Fits fits = new Fits(null, new File("testfiles/properties/fits-full-with-tool-output.xml"));

    	String fileName = "freeMXF-mxf1a.mxf";
    	File input = new File("testfiles/" + fileName);
    	FitsOutput fitsOut = fits.examine(input);
		fitsOut.addStandardCombinedFormat();
		fitsOut.saveToDisk("test-generated-output/" + fileName + "_Output.xml");
	}

}

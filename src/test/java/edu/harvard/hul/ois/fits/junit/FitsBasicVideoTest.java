/* 
 * Copyright 2009 Harvard University Library
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
import edu.harvard.hul.ois.fits.tools.Tool;

public class FitsBasicVideoTest extends AbstractLoggingTest {
	
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
	public void testFitsVideo_AVC() throws Exception {	

    	File input = new File("testfiles/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4");
    	
    	for(Tool t : fits.getToolbelt().getTools()) {
    		if(t.getToolInfo().getName().equals("Jhove")) {
    			//t.setEnabled(false);
    		}
    		if(t.getToolInfo().getName().equals("Exiftool")) {
    			//t.setEnabled(false);
    		}
    	}
    	
    	FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/fitsBasicVideoTestOutput.xml");
    	
	}
	
	@Test
	public void testFitsVideo_DV() throws Exception {	

    	File input = new File("testfiles/FITS-SAMPLE-26.mov");
    	
    	for(Tool t : fits.getToolbelt().getTools()) {
    		if(t.getToolInfo().getName().equals("Jhove")) {
    			//t.setEnabled(false);
    		}
    		if(t.getToolInfo().getName().equals("Exiftool")) {
    			//t.setEnabled(false);
    		}
    	}
    	
    	FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/fitsBasicVideoTestOutput.xml");
    	
	}

}

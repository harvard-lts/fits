/* 
 * Copyright 2015 Harvard University Library
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
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.*;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import static org.junit.Assert.*;
import org.junit.*;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;

public class VideoStdSchemaTestXmlUnit_NoMD5 {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.out.println("Moving test fits.xml in");
		
		// Copy fits.xml to fits_back.xml
	    FileUtils.copyFile(		
	      FileUtils.getFile("xml/fits.xml"), 
	      FileUtils.getFile("xml/fits_back.xml"));
		
	    // Copy in test fits.xml
		FileUtils.copyFile(
				FileUtils.getFile("testfiles/properties/fits_no_md5_video.xml"), 
				FileUtils.getFile("xml/fits.xml"));
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		System.out.println("Resetting fits.xml");
		
		// Set the backup to the original fits.xml
	    FileUtils.copyFile(		
	  	      FileUtils.getFile("xml/fits_back.xml"), 
	  	      FileUtils.getFile("xml/fits.xml"));
	    
	    // Remove the temp file
	    FileUtils.deleteQuietly(FileUtils.getFile("xml/fits_back.xml"));
	}
	
	@Test  
	public void testVideoXmlUnitFitsOutput_AVC_NO_MD5() throws Exception {
		
		Fits fits = new Fits();
		
		// First generate the FITS output
		File input = new File("testfiles/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4");
		FitsOutput fitsOut = fits.examine(input);

		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());

		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1_mp4_FITS_NO_MD5.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		// Set up XMLUnit
		XMLUnit.setIgnoreWhitespace(true); 
		XMLUnit.setNormalizeWhitespace(true); 		

		Diff diff = new Diff(expectedXmlStr,actualXmlStr);

		// Initialize attributes or elements to ignore for difference checking
		diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener(
				"version",
				"toolversion",
				"dateModified",
				"fslastmodified",
				"startDate",
				"startTime",
				"timestamp", 
				"fitsExecutionTime",
				"executionTime",
				"filepath",
				"location"));

		DetailedDiff detailedDiff = new DetailedDiff(diff);

		// Display any Differences
		List<Difference> diffs = detailedDiff.getAllDifferences();
		if (!diff.identical()) { 
			StringBuffer differenceDescription = new StringBuffer(); 
			differenceDescription.append(diffs.size()).append(" differences"); 
			
			System.out.println(differenceDescription.toString());
			for(Difference difference : diffs) {
				System.out.println(difference.toString());
			}

		}
		
		assertTrue("Differences in XML", diff.identical());
	}

}

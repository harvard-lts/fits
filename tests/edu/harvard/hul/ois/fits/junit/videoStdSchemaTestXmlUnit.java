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

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.util.List;
import java.util.Scanner;
//import javax.xml.stream.XMLOutputFactory;
//import javax.xml.stream.XMLStreamWriter;

import org.custommonkey.xmlunit.*;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
//import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;


public class videoStdSchemaTestXmlUnit extends XMLTestCase {

	@Test  
	public void testVideoXmlUnitFitsOutput_AVC() throws Exception {

		Fits fits = new Fits();
		
		// First generate the FITS output
		File input = new File("testfiles/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4");
		FitsOutput fitsOut = fits.examine(input);

		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());

		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1_mp4_FITS.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		// Set up XMLUnit
		XMLUnit.setIgnoreWhitespace(true); 
		XMLUnit.setNormalizeWhitespace(true); 		

		Diff diff = new Diff(expectedXmlStr,actualXmlStr);

		// Initialize attributes or elements to ignore for difference checking
		diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener(
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
	
	@Test  
	public void testVideoXmlUnitStandardOutput_AVC() throws Exception {
		
		// First generate the FITS output
		File input = new File("testfiles/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4");
		Fits fits = new Fits();	
		FitsOutput fitsOut = fits.examine(input);
		
		// Output stream for FITS to write to 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Create standard output in the stream passed in
		Fits.outputStandardSchemaXml(fitsOut, out);
		
		// Turn output stream into a String HtmlUnit can use
		String actualXmlStr = new String(out.toByteArray(),"UTF-8");
		
		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1_mp4_FITS_Standard.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		// Set up XMLUnit
		XMLUnit.setIgnoreWhitespace(true); 
		XMLUnit.setNormalizeWhitespace(true);
		
		Diff diff = new Diff(expectedXmlStr,actualXmlStr);

		// Initialize attributes or elements to ignore for difference checking
		diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener(
				"timestamp", 
				"fitsExecutionTime",
				"executionTime",
				// Not in Standard Output
				//"filepath",
				//"location",
				"ebucore:locator"));

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
	
	
	@Test  
	public void testVideoXmlUnitCombinedOutput_AVC() throws Exception {
		
		File input = new File("testfiles/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4");
		Fits fits = new Fits();	
		FitsOutput fitsOut = fits.examine(input);
		
		// Output stream for FITS to write to 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Create combined output in the stream passed in
		Fits.outputStandardCombinedFormat(fitsOut, out);
		
		// Turn output stream into a String HtmlUnit can use
		String actualXmlStr = new String(out.toByteArray(),"UTF-8");
		
		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				//"testfiles/output/AVPS-sample_14-aja-2vuy-8bit_mov_Combined.xml"));
	            "testfiles/output/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1_mp4_Combined.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		// Set up XMLUnit
		XMLUnit.setIgnoreWhitespace(true); 
		XMLUnit.setNormalizeWhitespace(true);
		
		Diff diff = new Diff(expectedXmlStr,actualXmlStr);

		// Initialize attributes or elements to ignore for difference checking
		diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener(
				"timestamp", 
				"fitsExecutionTime",
				"executionTime",
				"filepath",
				"location",
				"ebucore:locator"));

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

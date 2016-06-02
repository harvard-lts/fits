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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLIdentical;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;


public class TextMDXmlUnitTest {
	
	private static final String ACTUAL_OUTPUT_FILE_SUFFIX = "_XmlUnitActualOutput.xml";
	private static final String EXPECTED_OUTPUT_FILE_SUFFIX = "_XmlUnitExpectedOutput.xml";
	private static final String[] IGNORED_XML_ELEMENTS = {
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
			"location",
			"lastmodified"};
	
	/*
	 *  Only one Fits instance is needed to run all tests.
	 *  This also speeds up the tests.
	 */
	private static Fits fits;
	
	@AfterClass
	public static void afterClass() {
		fits = null;
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		// Set up XMLUnit and FITS for entire class.
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalizeWhitespace(true);
		fits = new Fits();
	}
    
	@Test
	public void testTextMD() throws Exception {	

		String inputFilename = "utf16.txt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	fitsOut.addStandardCombinedFormat();
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());

		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/" + inputFilename + EXPECTED_OUTPUT_FILE_SUFFIX));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}
    
	@Test
	public void testCsv() throws Exception {	
    	
    	String inputFilename = "random_data.csv";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	fitsOut.addStandardCombinedFormat();
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());

		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/" + inputFilename + EXPECTED_OUTPUT_FILE_SUFFIX));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}


	/*
	 * This method performs the actual test of actual FITS output against expected.
	 */
	private void testActualAgainstExpected(String actualXmlStr, String expectedXmlStr, String inputFilename)
			throws SAXException, IOException {
		Diff diff = new Diff(expectedXmlStr,actualXmlStr);

		// Initialize attributes or elements to ignore for difference checking
		diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener(IGNORED_XML_ELEMENTS));

		DetailedDiff detailedDiff = new DetailedDiff(diff);

		// Display any Differences
		@SuppressWarnings("unchecked")
		List<Difference> diffs = detailedDiff.getAllDifferences();
		if (!diff.identical()) { 
			StringBuffer differenceDescription = new StringBuffer(); 
			differenceDescription.append(diffs.size()).append(" differences"); 
			
			System.out.println(differenceDescription.toString());
			for(Difference difference : diffs) {
				System.out.println(difference.toString());
			}

		}
		assertXMLIdentical("Differences in XML for file: " + inputFilename, diff, true);
	}
}

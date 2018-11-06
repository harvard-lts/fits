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
package edu.harvard.hul.ois.fits.junit.service;

import java.io.File;
import java.util.Scanner;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractXmlUnitTest;

/**
 * These tests compare actual FITS output with expected output on image files.
 * NOTE: This is an integration test that requires a running web application with the
 * FITS Service running.
 * 
 * @author dan179
 */
@Ignore
public class MixXmlUnitServiceTest extends AbstractXmlUnitTest {

	@BeforeClass
	public static void initializeHttpClient() throws Exception {
		AbstractXmlUnitTest.beforeServiceTest();
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		AbstractXmlUnitTest.afterServiceTest();
	}
    
	@Test
	public void testMIX() throws Exception {	

		String inputFilename = "topazscanner.tif";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());

		fitsOut.addStandardCombinedFormat(); // output all data to file
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);

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
	public void testUncompressedTif() throws Exception {
		
		String inputFilename = "4072820.tif";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();

    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);

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
	public void testJpgExif() throws Exception {	

		String inputFilename = "ICFA.KC.BIA.1524-small.jpg";
    	File input = new File("testfiles/" + inputFilename);
    	
    	FitsOutput fitsOut = super.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());

		fitsOut.addStandardCombinedFormat(); // output all data to file
		fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);

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
	public void testJpgExif2() throws Exception {	

		String inputFilename = "gps.jpg";
    	File input = new File("testfiles/" + inputFilename);
    	
    	FitsOutput fitsOut = super.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		
		fitsOut.addStandardCombinedFormat(); // output all data to file
		fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);

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
	public void testJpgJfif() throws Exception {	

		String inputFilename = "GLOBE1.JPG";
    	File input = new File("testfiles/" + inputFilename);
    	
    	FitsOutput fitsOut = super.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		
		fitsOut.addStandardCombinedFormat(); // output all data to file
		fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);

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
	public void testJpg2() throws Exception {

		String inputFilename = "JPEGTest_20170591--JPEGTest_20170591.jpeg";
    	File input = new File("testfiles/" + inputFilename);
    	
    	FitsOutput fitsOut = super.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		
		fitsOut.addStandardCombinedFormat(); // output all data to file
		fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);

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
	public void testTwoPageTiff() throws Exception {

		String inputFilename = "W00EGS1016782-I01JW30--I01JW300001__0001.tif";
    	File input = new File("testfiles/" + inputFilename);
    	
    	FitsOutput fitsOut = super.examine(input);
    	
		fitsOut.addStandardCombinedFormat(); // output all data to file
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
	public void testJp2_1() throws Exception {

		String inputFilename = "test.jp2";
    	File input = new File("testfiles/" + inputFilename);
    	
    	FitsOutput fitsOut = super.examine(input);
		
		fitsOut.addStandardCombinedFormat(); // output all data to file
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
	public void testJp2_2() throws Exception {

		String inputFilename = "006607203_00018.jp2";
    	File input = new File("testfiles/" + inputFilename);
    	
    	FitsOutput fitsOut = super.examine(input);
		
		fitsOut.addStandardCombinedFormat(); // output all data to file
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

}

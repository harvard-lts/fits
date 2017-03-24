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
import java.util.Scanner;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractXmlUnitTest;

public class VideoStdSchemaTestXmlUnit extends AbstractXmlUnitTest {
	
	// These override the values in the parent class.
	private static final String[] OVERRIDING_IGNORED_XML_ELEMENTS = {
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
			"lastmodified",
			"ebucore:locator"};

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
	public void testVideoXmlUnitFitsOutput_AVC() throws Exception {
		
    	String inputFilename = "FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4";
    	File input = new File("testfiles/" + inputFilename);
		FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/testVideoXmlUnitFitsOutput_AVC_Output.xml");

		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());

		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1_mp4_FITS.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}
	
	@Test  
	public void testVideoXmlUnitFitsOutput_DV() throws Exception {
		
    	String inputFilename = "FITS-SAMPLE-26.mov";
    	File input = new File("testfiles/" + inputFilename);
		FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/testVideoXmlUnitFitsOutput_DV_Output.xml");

		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());

		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/FITS-SAMPLE-26_mov_FITS.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}	
	
	@Test  
	public void testVideoXmlUnitStandardOutput_AVC() throws Exception {
		
    	String inputFilename = "FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4";
    	File input = new File("testfiles/" + inputFilename);
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

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}
	
	@Test  
	public void testVideoXmlUnitStandardOutput_DV() throws Exception {
		
    	String inputFilename = "FITS-SAMPLE-26.mov";
    	File input = new File("testfiles/" + inputFilename);
		FitsOutput fitsOut = fits.examine(input);
		
		// Output stream for FITS to write to 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Create standard output in the stream passed in
		Fits.outputStandardSchemaXml(fitsOut, out);
		
		// Turn output stream into a String HtmlUnit can use
		String actualXmlStr = new String(out.toByteArray(),"UTF-8");
		
		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/FITS-SAMPLE-26_mov_Standard.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}	
	
	@Test  
	public void testVideoXmlUnitCombinedOutput_AVC() throws Exception {
		
    	String inputFilename = "FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4";
    	File input = new File("testfiles/" + inputFilename);
		FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/testVideoXmlUnitCombinedOutput_AVC_Output.xml");
		
		// Output stream for FITS to write to 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Create combined output in the stream passed in
		Fits.outputStandardCombinedFormat(fitsOut, out);
		
		// Turn output stream into a String HtmlUnit can use
		String actualXmlStr = new String(out.toByteArray(),"UTF-8");
		
		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
	            "testfiles/output/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1_mp4_Combined.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}
	
	@Test  
	public void testVideoXmlUnitCombinedOutput_DV() throws Exception {
		
    	String inputFilename = "FITS-SAMPLE-26.mov";
    	File input = new File("testfiles/" + inputFilename);
		FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/testVideoXmlUnitCombinedOutput_DV_Output.xml");
		
		// Output stream for FITS to write to 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Create combined output in the stream passed in
		Fits.outputStandardCombinedFormat(fitsOut, out);
		
		// Turn output stream into a String HtmlUnit can use
		String actualXmlStr = new String(out.toByteArray(),"UTF-8");
		
		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
	            "testfiles/output/FITS-SAMPLE-26_mov_Combined.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}
	
	/**
	 * Tests that the output from FITS matches the expected output.
	 */
	@Test
	public void testVideoXmlUnitOutput_MXF() throws Exception {
		
    	String inputFilename = "freeMXF-mxf1a.mxf";

    	File input = new File("testfiles/" + inputFilename);
		FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + ACTUAL_OUTPUT_FILE_SUFFIX);
		
		// Output stream for FITS to write to 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Create combined output in the stream passed in
		Fits.outputStandardCombinedFormat(fitsOut, out);
		
		// Turn output stream into a String HtmlUnit can use
		String actualXmlStr = new String(out.toByteArray(),"UTF-8");
		
		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
	            "testfiles/output/" + inputFilename + EXPECTED_OUTPUT_FILE_SUFFIX));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}
	
	@Test  
	public void testVideoXmlUnitCombinedOutput_MPEG2() throws Exception {
		
    	String inputFilename = "FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_4_8_1_2_1_1.mov";
    	File input = new File("testfiles/" + inputFilename);
		FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/testVideoXmlUnitCombinedOutput_MPEG2_Output.xml");
		
		// Output stream for FITS to write to 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Create combined output in the stream passed in
		Fits.outputStandardCombinedFormat(fitsOut, out);
		
		// Turn output stream into a String HtmlUnit can use
		String actualXmlStr = new String(out.toByteArray(),"UTF-8");
		
		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
	            "testfiles/output/FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_4_8_1_2_1_1_mov_mpeg2_combined.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
	}	

	/**
	 * Send in set of XML elements to ignore that overrides the default.
	 */
	@Override
	protected String[] getIgnoredXmlElements() {
		return OVERRIDING_IGNORED_XML_ELEMENTS;
	}
}

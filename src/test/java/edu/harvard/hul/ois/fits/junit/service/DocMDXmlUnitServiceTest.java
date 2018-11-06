/* 
 * Copyright 2016 Harvard University Library
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
 * These tests compare actual FITS output with expected output on various word processing documents.
 * These tests should be run with &lt;display-tool-output&gt;false&lt;/display-tool-output&gt; in fits.xml.
 * NOTE: This is an integration test that requires a running web application with the
 * FITS Service running.
 * 
 * @author dan179
 */
@Ignore
public class DocMDXmlUnitServiceTest extends AbstractXmlUnitTest {

	@BeforeClass
	public static void initializeHttpClient() throws Exception {
		AbstractXmlUnitTest.beforeServiceTest();
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		AbstractXmlUnitTest.afterServiceTest();
	}
	
	@Test
	public void testWordDocUrlEmbeddedResources() throws Exception {

    	String inputFilename = "Word2003_has_URLs_has_embedded_resources.doc";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocGraphics() throws Exception {

    	String inputFilename = "Word2003_many_graphics.doc";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocPasswordProtected() throws Exception {

    	String inputFilename = "Word2003PasswordProtected.doc";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDoc2011() throws Exception {

    	String inputFilename = "Word2011_Has_Outline.doc";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocLibreOffice() throws Exception {

    	String inputFilename = "LibreOffice.doc";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocHyperlinks() throws Exception {

    	String inputFilename = "Word2003_has_table_of_contents.doc";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocPasswordAndEncrypted() throws Exception {

    	String inputFilename = "Word_protected_encrypted.doc";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocMacMSWord() throws Exception {

    	String inputFilename = "MacMSWORD_4-5.doc";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocV2() throws Exception {

    	String inputFilename = "NEWSSLID_Word2_0.DOC";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testOpenOfficeDoc() throws Exception {

    	String inputFilename = "LibreODT-Ur-doc.odt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testOpenOfficeDocEmbeddedResources() throws Exception {

    	String inputFilename = "LibreODTwFormulas.odt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testOpenOfficeDocHasTables() throws Exception {

    	String inputFilename = "LibreODT-hasTables.odt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testOpenOfficeDocUnparseableDate() throws Exception {

    	String inputFilename = "UnparseableDate.odt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocxOutput() throws Exception {

    	String inputFilename = "Word_has_index.docx";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	 * This output of this document produces what looks to be invalid output.
	 */
	@Test
	public void testWordDocxPasswordProtected() throws Exception {

    	String inputFilename = "WordPasswordProtected.docx";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testWordDocmOutput() throws Exception {

    	String inputFilename = "Document_Has_Form_Controls.docm";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testEpubOutput() throws Exception {

    	// process multiple files to examine different types of output
    	String[] inputFilenames = {"Winnie-the-Pooh-protected.epub", // not properly identified as epub mimetype
    			"GeographyofBliss_oneChapter.epub",
    			"aliceDynamic_images_metadata_tableOfContents.epub", // not properly identified as epub mimetype
    			"epub30-test-font-embedding-obfuscation.epub",
    			"Calibre_hasTable_of_Contents.epub"};

    	for (String inputFilename : inputFilenames) {
    		
        	File input = new File("testfiles/" + inputFilename);
        	FitsOutput fitsOut = super.examine(input);
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
	}
	
	@Test
	public void testWPDOutput() throws Exception {
    	
    	// process multiple files to examine different types of output
    	String[] inputFilenames = { //"WordPerfect6-7.wpd",
    			"WordPerfect4_2.wp",
    			"WordPerfect5_0.wp",
    			"WordPerfect5_2.wp" };
//    			"WordPerfectCompoundFile.wpd"};  // (not identified as a WordPerfect document)

    	for (String inputFilename : inputFilenames) {
        	File input = new File("testfiles/" + inputFilename);
        	FitsOutput fitsOut = super.examine(input);
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
	}
	
	@Test
	public void testRtfOutput() throws Exception {

    	String inputFilename = "TestDoc.rtf";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testRtfWithCompanyOutput() throws Exception {

    	String inputFilename = "Doc2.rtf";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = super.examine(input);
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
	public void testPdf() throws Exception {
		
    	String[] inputFilenames = {"PDF_embedded_resources.pdf",
    			"HasChangeHistory.pdf",
    			"PDF_eng.pdf",
    			"HasAnnotations.pdf"};

    	for (String inputFilename : inputFilenames) {
    		File input = new File("testfiles/" + inputFilename);
	    	FitsOutput fitsOut = super.examine(input);
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
	}
    
	@Test
	public void testPdfA() throws Exception {
		
    	String[] inputFilenames = {"PDFa_equations.pdf",
    			"PDFa_multiplefonts.pdf",
    			"PDFa_has_form.pdf",
    			"PDFa_has_table_of_contents.pdf",
    			"PDFa_has_tables.pdf",
    			"PDFA_Document with tables.pdf",
    			"PDFa_embedded_resources.pdf"};

    	for (String inputFilename : inputFilenames) {
    		File input = new File("testfiles/" + inputFilename);
	    	FitsOutput fitsOut = super.examine(input);
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
	}
    
	@Test
	public void testPdfX() throws Exception {
		
    	String[] inputFilenames = {
    			"altona_technical_1v2_x3_has_annotations.pdf",
    			"Book_pdfx1a.pdf", // converts to PDF/A
    			"PDFx3.pdf" // converts to PDF/A
    			};

    	for (String inputFilename : inputFilenames) {
    		File input = new File("testfiles/" + inputFilename);
	    	FitsOutput fitsOut = super.examine(input);
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
	}

}

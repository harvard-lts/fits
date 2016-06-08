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

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.custommonkey.xmlunit.XMLUnit;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.ots.schemas.DocumentMD.DocumentMD;

/**
 * This class generates FITS and Standard output for inspection during development.
 * It does not actually perform any JUnit test assertions.
 * 
 * @author dan179
 */
public class DocMDTest {

	/*
	 *  Only one Fits instance is needed to run all tests.
	 *  This also speeds up the tests.
	 */
	private static Fits fits;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// Set up XMLUnit and FITS for entire class.
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalizeWhitespace(true);
		File fitsConfigFile = new File("testfiles/properties/fits-full-with-tool-output.xml");
		fits = new Fits(null, fitsConfigFile);
	}
	
	@AfterClass
	public static void afterClass() {
		fits = null;
	}
    
	@Test
	public void testWordDocUrlEmbeddedResources() throws Exception {	

    	File input = new File("testfiles/Word2003_has_URLs_has_embedded_resources.doc");
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/Word2003_has_URLs_has_embedded_resources-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocGraphics() throws Exception {	

		File input = new File("testfiles/Word2003_many_graphics.doc");
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		fitsOut.saveToDisk("test-generated-output/Word2003_many_graphics-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocPasswordProtected() throws Exception {	

		File input = new File("testfiles/Word2003PasswordProtected.doc");
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/Word2003PasswordProtected-DOC_Output.xml");
	}
    
	@Test
	public void testWordDoc2011() throws Exception {	

    	File input = new File("testfiles/Word2011_Has_Outline.doc");
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();		
		serializer.output(fitsOut.getFitsXml(), System.out);
    	fitsOut.saveToDisk("test-generated-output/Word2011_Has_Outline-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocLibreOffice() throws Exception {	

    	File input = new File("testfiles/LibreOffice.doc");
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/LibreOffice-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocHyperlinks() throws Exception {	

    	File input = new File("testfiles/Word2003_has_table_of_contents.doc");
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/Word2003_has_table_of_contents-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocPasswordAndEncrypted() throws Exception {	

    	File input = new File("testfiles/Word_protected_encrypted.doc");
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/Word_protected_encrypted-DOC_Output.xml");
	}
    
	@Test
	public void testOpenOfficeDoc() throws Exception {	

    	String inputFilename = "LibreODT-Ur-doc.odt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
    
	@Test
	public void testOpenOfficeDocEmbeddedResources() throws Exception {	

    	String inputFilename = "LibreODTwFormulas.odt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
    
	@Test
	public void testOpenOfficeDocHasTables() throws Exception {	

    	String inputFilename = "LibreODT-hasTables.odt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
    
	@Test
	public void testOpenOfficeDocUnparseableDate() throws Exception {	

    	String inputFilename = "UnparseableDate.odt";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
	
	@Test
	public void testWordDocxOutput() throws Exception {

    	String inputFilename = "Word_has_index.docx";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
	
	@Test
	public void testWordDocxPasswordProtected() throws Exception {

    	String inputFilename = "WordPasswordProtected.docx";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
	
	@Test
	public void testWordDocmOutput() throws Exception {

    	String inputFilename = "Document_Has_Form_Controls.docm";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
			docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
	
	@Test
	public void testEpubOutput() throws Exception {

    	// process multiple files to examine different types of output
    	String[] inputFilenames = {"Winnie-the-Pooh-protected.epub",
    			"GeographyofBliss_oneChapter.epub",
    			"aliceDynamic_images_metadata_tableOfContents.epub",
    			"epub30-test-font-embedding-obfuscation.epub",
    			"Calibre_hasTable_of_Contents.epub"};

    	for (String inputFilename : inputFilenames) {
    		
    		String outputFilename = "test-generated-output/"+ inputFilename + "_Output.xml";
    		File input = new File("testfiles/" + inputFilename);
    		FitsOutput fitsOut = fits.examine(input);
    		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    		fitsOut.addStandardCombinedFormat();
    		serializer.output(fitsOut.getFitsXml(), System.out);
    		fitsOut.saveToDisk(outputFilename);
    	}
	}
	
	@Test
	public void testWPDOutput() throws Exception {
    	
    	// process multiple files to examine different types of output
    	String[] inputFilenames = { "WordPerfect6-7.wpd",
    			"WordPerfect4_2.wp",
    			"WordPerfect5_0.wp",
    			"WordPerfect5_2.wp" };
//    			"WordPerfectCompoundFile.wpd"};  // (not identified as a WordPerfect document)

    	for (String inputFilename : inputFilenames) {
    		String outputFilename = "test-generated-output/"+ inputFilename + "_Output.xml";
    		File input = new File("testfiles/" + inputFilename);
    		FitsOutput fitsOut = fits.examine(input);
    		
    		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    		fitsOut.addStandardCombinedFormat();
    		serializer.output(fitsOut.getFitsXml(), System.out);
    		
    		fitsOut.saveToDisk(outputFilename);

    		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
    		
    		if(docmd != null) {
    			docmd.setRoot(true);
    			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
    			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
    			
    			docmd.output(writer);
    		}
    	}
	}
	
	@Test
	public void testRtfOutput() throws Exception {

    	String inputFilename = "TestDoc.rtf";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	fitsOut.addStandardCombinedFormat();
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
	
	@Test
	public void testRtfWithCompanyOutput() throws Exception {

    	String inputFilename = "Doc2.rtf";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	fitsOut.addStandardCombinedFormat();
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
    
	@Test
	public void testPdf() throws Exception {
		
    	String[] inputFilenames = {"PDF_embedded_resources.pdf",
    			"PDF_equations.pdf",
    			"HasChangeHistory.pdf",
    			"PDF_eng.pdf",
    			"HasAnnotations.pdf"};

    	for (String inputFilename : inputFilenames) {
    		String outputFilename = "test-generated-output/"+ inputFilename + "_Output.xml";
    		File input = new File("testfiles/" + inputFilename);
    		FitsOutput fitsOut = fits.examine(input);
        	fitsOut.addStandardCombinedFormat();
    		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    		// write to console
    		serializer.output(fitsOut.getFitsXml(), System.out);
    		fitsOut.saveToDisk(outputFilename);
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
    		String outputFilename = "test-generated-output/"+ inputFilename + "_Output.xml";
    		File input = new File("testfiles/" + inputFilename);
    		FitsOutput fitsOut = fits.examine(input);
        	fitsOut.addStandardCombinedFormat();
    		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    		// write to console
    		serializer.output(fitsOut.getFitsXml(), System.out);
    		fitsOut.saveToDisk(outputFilename);
    	}
	}
    
	@Test
	public void testPdfX() throws Exception {
		
    	String[] inputFilenames = {"altona_technical_1v2_x3_has_annotations.pdf",
    			"Book_pdfx1a.pdf", // converts to PDF/A
    			"PDFx3.pdf"}; // converts to PDF/A

    	for (String inputFilename : inputFilenames) {
    		String outputFilename = "test-generated-output/"+ inputFilename + "_Output.xml";
    		File input = new File("testfiles/" + inputFilename);
    		FitsOutput fitsOut = fits.examine(input);
        	fitsOut.addStandardCombinedFormat();
    		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    		// write to console
    		serializer.output(fitsOut.getFitsXml(), System.out);
    		fitsOut.saveToDisk(outputFilename);
    	}
	}

}

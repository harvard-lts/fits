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

import org.custommonkey.xmlunit.XMLTestCase;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
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
public class DocMDTest extends XMLTestCase {
    
	@Test
	public void testOds() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/test.ods");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output//test-ODS_Output.xml");
	}
    
	@Test
	public void testWordDocUrlEmbeddedResources() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/Word2003_has_URLs_has_embedded_resources.doc");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output//Word2003_has_URLs_has_embedded_resources-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocGraphics() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/Word2003_many_graphics.doc");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output//Word2003_many_graphics-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocPasswordProtected() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/Word2003PasswordProtected.doc");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output//Word2003PasswordProtected-DOC_Output.xml");
	}
    
	@Test
	public void testWordDoc2011() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/Word2011_Has_Outline.doc");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output//Word2011_Has_Outline-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocHyperlinks() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/Word2003_has_table_of_contents.doc");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output//Word2003_has_table_of_contents-DOC_Output.xml");
	}
    
	@Test
	public void testWordDocPasswordAndEncrypted() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/Word_protected_encrypted.doc");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output//Word_protected_encrypted-DOC_Output.xml");
	}
    
	@Test
	public void testOpenOfficeDoc() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/LibreODT-Ur-doc.odt");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/LibreODT-Ur-ODT_Output.xml");
	}
    
	@Test
	public void testOpenOfficeDocPasswordProtected() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/LibreODT_protected.odt");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/LibreODT_protected_ODT_Output.xml");
	}
    
	@Test
	public void testOpenOfficeDocUnparseableDate() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/UnparseableDate.odt");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/UnparseableDate_ODT_Output.xml");
	}
	
	@Test
	public void testWordDocxOutput() throws Exception {
    	Fits fits = new Fits();
    	File input = new File("testfiles/Word_has_index.docx");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/Word_has_index_DOCX_Output.xml");
	}
	
	@Test
	public void testEpubOutput() throws Exception {
    	Fits fits = new Fits();

    	// process multiple files to examine different types of output
    	String[] inputFilenames = {"Winnie-the-Pooh-protected.epub",
    			"GeographyofBliss_oneChapter.epub",
    			"aliceDynamic_images_metadata_tableOfContents.epub",
    			"epub30-test-font-embedding-obfuscation.epub"};

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
	public void testWPOutput() throws Exception {
    	Fits fits = new Fits();
    	File input = new File("testfiles/WordPerfect5_2.wp");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		fitsOut.addStandardCombinedFormat();
		DocumentMD docmd = (DocumentMD)fitsOut.getStandardXmlContent();
		
		if(docmd != null) {
		docmd.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			docmd.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/WordPerfect5_2_Output.xml");
	}
	
	@Test
	public void testWPDOutput() throws Exception {
    	Fits fits = new Fits();
    	
    	// process multiple files to examine different types of output
    	String[] inputFilenames = {"WordPerfect6-7.wpd",
    			"WordPerfect4_2.wp",
    			"WordPerfect5_0.wp",
    			"WordPerfect5_2.wp",
    			"WordPerfectCompoundFile.wpd"};

    	for (String inputFilename : inputFilenames) {
    		String outputFilename = "test-generated-output/"+ inputFilename + "_Output.xml";
    		File input = new File("testfiles/" + inputFilename);
    		FitsOutput fitsOut = fits.examine(input);
    		
    		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    		serializer.output(fitsOut.getFitsXml(), System.out);
    		
    		fitsOut.addStandardCombinedFormat();
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
    	Fits fits = new Fits();
    	File input = new File("testfiles/TestDoc.rtf");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	fitsOut.addStandardCombinedFormat();
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
    	fitsOut.saveToDisk("test-generated-output/TestDoc_RTF_Output.xml");
	}
    
	@Test
	public void testPdf() throws Exception {
		
    	Fits fits = new Fits();
    	File input = new File("testfiles/PDF_embedded_resources.pdf");
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		// write to console
		Fits.outputStandardCombinedFormat(fitsOut, System.out);
		
		// write to file
		fitsOut.saveToDisk("test-generated-output/PDF_embedded_resources_Output.xml");
	}
    
	@Test
	public void testPdfA() throws Exception {
		
    	Fits fits = new Fits();
    	File input = new File("testfiles/PDFa_equations.pdf");
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		// write to console
		Fits.outputStandardCombinedFormat(fitsOut, System.out);
		
		// write to file
		fitsOut.saveToDisk("test-generated-output/PDFa_equations_Output.xml");
	}
    
	@Test
	public void testPdfX() throws Exception {
		
    	Fits fits = new Fits();
    	File input = new File("testfiles/altona_technical_1v2_x3_has_annotations.pdf");
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		// write to console
		Fits.outputStandardCombinedFormat(fitsOut, System.out);
		
		// write to file
		fitsOut.saveToDisk("test-generated-output/altona_technical_1v2_x3_has_annotations_PDFX_Output.xml");
	}

}

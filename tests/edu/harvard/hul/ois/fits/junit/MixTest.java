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

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import edu.harvard.hul.ois.ots.schemas.MIX.Mix;

public class MixTest extends AbstractLoggingTest {

	/*
	 *  Only one Fits instance is needed to run all tests.
	 *  This also speeds up the tests.
	 */
	private static Fits fits;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// Set up FITS for entire class.
		fits = new Fits();
		// Use this instead of above line to turn on tool output.
//		File fitsConfigFile = new File("testfiles/properties/fits-full-with-tool-output.xml");
//		fits = new Fits(null, fitsConfigFile);
	}
	
	@AfterClass
	public static void afterClass() {
		fits = null;
	}
    
	@Test
	public void testMIX() throws Exception {	

		String inputFilename = "topazscanner.tif";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		Mix mix = (Mix)fitsOut.getStandardXmlContent();
		mix.setRoot(true);
				
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
		
		mix.output(writer);
		fitsOut.addStandardCombinedFormat(); // output all data to file
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
	
	@Test
	public void testUncompressedTif() throws Exception {
		
		String inputFilename = "4072820.tif";
    	File input = new File("testfiles/" + inputFilename);
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		fitsOut.addStandardCombinedFormat();
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		Mix mix = (Mix)fitsOut.getStandardXmlContent();
		
		if(mix != null) {
			mix.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			mix.output(writer);
		}
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_Output.xml");
	}
    
	@Test
	public void testMixJpg() throws Exception {	

		String filename = "3426592.jpg";
    	File input = new File("testfiles/" + filename);
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		Mix mix = (Mix)fitsOut.getStandardXmlContent();
		mix.setRoot(true);
				
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
		
		mix.output(writer);
		fitsOut.addStandardCombinedFormat(); // output all data to file
		fitsOut.saveToDisk("test-generated-output/" + filename + "_Output.xml");

	}
    
	@Test
	public void testJpgExif() throws Exception {	

		String filename = "ICFA.KC.BIA.1524-small.jpg";
    	File input = new File("testfiles/" + filename);
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
				
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
		
		fitsOut.addStandardCombinedFormat(); // output all data to file
		fitsOut.saveToDisk("test-generated-output/" + filename + "_Output.xml");

	}
    
	@Test
	public void testJpgJfif() throws Exception {	

		String filename = "gps.jpg";
    	File input = new File("testfiles/" + filename);
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
				
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
		
		fitsOut.addStandardCombinedFormat(); // output all data to file
		fitsOut.saveToDisk("test-generated-output/" + filename + "_Output.xml");

	}
    
	@Test
	public void testJpg2() throws Exception {	

		String filename = "JPEGTest_20170591--JPEGTest_20170591.jpeg";
    	File input = new File("testfiles/" + filename);
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
				
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		
		fitsOut.addStandardCombinedFormat(); // output all data to file
		fitsOut.saveToDisk("test-generated-output/" + filename + "_Output.xml");

	}
}

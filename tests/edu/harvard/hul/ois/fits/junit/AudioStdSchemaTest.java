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
import java.util.List;
import java.util.Scanner;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

public class AudioStdSchemaTest {

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
		fits = new Fits();
	}
	
	@AfterClass
	public static void afterClass() {
		fits = null;
	}

    @Test  
	public void testAudioMD() throws Exception {   

    	File input = new File("testfiles/test.wav");
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		XmlContent xml = fitsOut.getStandardXmlContent();
		
		if(xml != null) {
			xml.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			xml.output(writer);
		}
	}
    
    @Test  
	public void testAudioChunk() throws Exception {   

    	File input = new File("testfiles/testchunk.wav");
    	FitsOutput fitsOut = fits.examine(input);
    	fitsOut.saveToDisk("test-generated-output/testchunk_wav_Output.xml");
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());

		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/testchunk.wav.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		// Set up XMLUnit
		XMLUnit.setIgnoreWhitespace(true); 
		XMLUnit.setNormalizeWhitespace(true); 		

		Diff diff = new Diff(expectedXmlStr,actualXmlStr);

		// Initialize attributes or elements to ignore for difference checking
		diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener(
				"version",		// fits[@version]
				"toolversion",
				"dateModified",
				"fslastmodified",
				"startDate",
				"startTime",
				"timestamp", 
				"fitsExecutionTime",
				"executionTime",
				"filepath",
				"lastmodified",				
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
		
		assertXMLIdentical("Differences in XML", diff, true);    	
	}    

}


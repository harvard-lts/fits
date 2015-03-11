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

//import javax.xml.stream.XMLOutputFactory;
//import javax.xml.stream.XMLStreamWriter;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
//import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

import org.custommonkey.xmlunit.*;

public class videoStdSchemaTestXmlUnit extends XMLTestCase {

	@Test  
	public void testVideoXmlUnit() throws Exception {

		Fits fits = new Fits();
		
		// MPEG-4
		File input = new File("/Users/dab980/downloads/remade-video-files/AVPS-sample_14-aja-2vuy-8bit.mov");
		FitsOutput fitsOut = fits.examine(input);
		
	//	// Write the Output to disk
	//	fitsOut.saveToDisk("videoStdSchemaTestOutput.xml");

		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());
		
//		System.out.println("\n\nActual XML Returned:\n" + actualXmlStr);
//
//		// Standard Output
//		System.out.println("\n\n======================================");
//		XmlContent xml = fitsOut.getStandardXmlContent();
//		if(xml != null) {
//			xml.setRoot(true);
//			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
//			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
//			
//			xml.output(writer);
//		}

		// Read in the expected XML file
		Scanner scan = new Scanner(new File(
				"testfiles/output/AVPS-sample_14-aja-2vuy-8bit_mov.xml"));
		String expectedXmlStr = scan.
				useDelimiter("\\Z").next();
		scan.close();

		// Set up XMLUnit
		XMLUnit.setIgnoreWhitespace(true); 
		XMLUnit.setNormalizeWhitespace(true); 		

		Diff diff = new Diff(expectedXmlStr,actualXmlStr);

		// Initialize attributes to ignore for difference checking
		diff.overrideDifferenceListener(new IgnoreNamedAttributesDifferenceListener(
				"timestamp", 
				"fitsExecutionTime",
				"executionTime"));

		DetailedDiff detailedDiff = new DetailedDiff(diff);

		// Display any Differences
		List<Difference> diffs = detailedDiff.getAllDifferences();
		//if (!diff.similar()) {
		if (!diff.identical()) { 
			StringBuffer differenceDescription = new StringBuffer(); 
			differenceDescription.append(diffs.size()).append(" differences"); 
			//org.junit.Assert.fail(differenceDescription.toString());
			
			System.out.println(differenceDescription.toString());
			for(Difference difference : diffs) {
				System.out.println(difference.toString());
			}

		}
		
		assertTrue("Differences in XML", diff.identical());
	}

}

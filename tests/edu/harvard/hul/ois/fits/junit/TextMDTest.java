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
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.ots.schemas.TextMD.TextMD;


public class TextMDTest {
    
	@Test
	public void testTextMD() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/utf16.txt");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		TextMD textmd = (TextMD)fitsOut.getStandardXmlContent();
		
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 

		if (textmd != null) {
			textmd.setRoot(true);
			textmd.output(writer);
		}
	}
    
	@Test
	public void testCsv() throws Exception {	
    	Fits fits = new Fits();
    	File input = new File("testfiles/random_data.csv");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		TextMD textmd = (TextMD)fitsOut.getStandardXmlContent();
		
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 

		if (textmd != null) {
			textmd.setRoot(true);
			textmd.output(writer);
		}
	}

}

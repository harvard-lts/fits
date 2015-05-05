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
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.ots.schemas.MIX.Mix;

import org.custommonkey.xmlunit.*;


public class MixTest extends XMLTestCase {

    
	@Test
	public void testMIX() throws Exception {	
    	Fits fits = new Fits("");
    	//File input = new File("testfiles/IMG_5075.jpg");
    	File input = new File("testfiles/topazscanner.tif");
    	
    	for(Tool t : fits.getToolbelt().getTools()) {
    		if(t.getToolInfo().getName().equals("Jhove")) {
    			//t.setEnabled(false);
    		}
    		if(t.getToolInfo().getName().equals("Exiftool")) {
    			//t.setEnabled(false);
    		}
    	}
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		Mix mix = (Mix)fitsOut.getStandardXmlContent();
		mix.setRoot(true);
				
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
		
		mix.output(writer);

	}

}

/* 
 * Copyright 2014 Harvard University Library
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
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

import org.custommonkey.xmlunit.*;

public class videoStdSchemaTest extends XMLTestCase {

    @Test  
	public void testVideo() throws Exception {
    	
//        options.addOption( "i", true, "input file or directory" );
//        options.addOption( "r", false, "process directories recursively when -i is a directory " );
//        options.addOption( "o", true, "output file or directory if -i is a directory" );
//        options.addOption( "h", false, "print this message" );
//        options.addOption( "v", false, "print version information" );
//        OptionGroup outputOptions = new OptionGroup();
//        Option stdxml = new Option( "x", false, "convert FITS output to a standard metadata schema" );
//        Option combinedStd = new Option( "xc", false, "output using a standard metadata schema and include FITS xml" );
//        outputOptions.addOption( stdxml );   	
//    	
    	
boolean doMain = true;
if(doMain) {
    	// Call Fits main
    	// 
    	String[] args = {
    			
    			"-i",
    			//
    			//"testfiles/test.wav",
    			//
    			//
    			//"/Users/dab980/Documents/video/samples/10BV-16-48000A-UNCOMPRESSED.mov", 
    			//
    			//"/Users/dab980/Documents/video/samples/10BV-24-48000A-PRORES422HQj 1.mov",
    			//"/Users/dab980/Documents/video/samples/10BV-24-48000A-PRORES422i.mov",
    			//"/Users/dab980/Documents/video/samples/10BV-24-48000A-PRORES422LTj 1.mov",
    			//"/Users/dab980/Documents/video/samples/10BV-24-48000A-PRORES4444i 1.mov",
    			// empty
    			// "/Users/dab980/Documents/video/samples/10BV-24-48000A-PRORES4444i.mov",
    			//
    			//"/Users/dab980/Documents/video/samples/10BV-24-48000A-PRORESPROXYi.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-DV-NTSC 1.mov",
    			//"/Users/dab980/Documents/video/samples/10BV-24-48000A-UNCOMPRESSED.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-DV-NTSC-HIGH-COMPESSION.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-DV-NTSC-MEDCOMPESSION.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-DV-NTSC.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-DVCPRO50-NTSC 1.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-DVCPRO50-NTSC.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-H264 1.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-H264-HIGH -COMP.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-PRORESi.mov",
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-PRORESi_Theora.mov",
    			//
    			//"/Users/dab980/Documents/video/samples/8BV-24-48000A-UNCOMPRESSED.mov",
    			
    			//"/Users/dab980/downloads/remade-video-files/AVPS-sample_14-aja-2vuy-8bit.mov",
    			
    			// Taken from http://www.divx.com/en/devices/profiles/video
    			"/Users/dab980/downloads/Sintel_DivXPlus_6500kbps.mkv",
    			
    			// empty
    		    //"/Users/dab980/Documents/video/samples/10BV-24-48000A-PRORES422HQj.mov",
    			//
    			//
    			//"/Users/dab980/downloads/drop.avi",
    			//"/Users/dab980/downloads/centaur_1.mpg",
    			//"/Users/dab980/downloads/big_buck_bunny.ogv",
    			//"/Users/dab980/downloads/Jellyfish-3-Mbps.mkv",
    			//"/Users/dab980/downloads/test-jpeg2000-color-wsound1.mxf",
    			//"/Users/dab980/downloads/spacetestSMALL.wmv",
    			//"/Users/dab980/downloads/8BV-24-48000A-DV-NTSC-MEDCOMPESSION.mov",
    			//"/Users/dab980/downloads/Media-Convert_test2.mp4",
    			//
    			//"testfiles/10BV-24-48000A-PRORES422HQj 1.mov",
    			//"/Users/dab980/downloads/Sintel_DivXPlus_6500kbps.mkv",
      			//
    			//"testfiles/test.wav",
    			//"testfiles/006607203_00018.jp2",
    			//
    			// No command line arguments, FITS output ONLY
    			//
    			// -xc --> Outputs the FITS output plus the FITS output transformed into standard XML schemas
    			//"-xc",
    			//
    			// -x --> Transforms the FITS output into standard XML schemas (EBUCore)
    			"-x", 
    			//
    			"-o fits_output.xml"
    			
    		
    	};

    	Fits.main(args);
} // doMain  	

boolean doFits = false;
if (doFits) {

    	Fits fits = new Fits();
//    	File input = new File("testfiles/10BV-24-48000A-PRORES422HQj 1.mov");
    	File input = new File("testfiles/test.wav");
    	
    	
    	FitsOutput fitsOut = fits.examine(input);
    	
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		serializer.output(fitsOut.getFitsXml(), System.out);
		
		XmlContent xml = fitsOut.getStandardXmlContent();
		assertNotNull(xml);
		
		if(xml != null) {
			xml.setRoot(true);
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
			
			xml.output(writer);
		}
    	
	}

} // doFits


}

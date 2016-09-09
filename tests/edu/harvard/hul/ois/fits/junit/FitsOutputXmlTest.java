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
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import edu.harvard.hul.ois.fits.tests.IgnoreAttributeValuesDifferenceListener;

/*
 * BROKEN TEST
 */
@Ignore
@RunWith(value=Parameterized.class)
public class FitsOutputXmlTest extends AbstractLoggingTest {
	
    private FitsOutput expected;
    private FitsOutput actual;
    private XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    
    public FitsOutputXmlTest() {
    	super();
    	// must set output values if using this constructor
//    	this.expected = ???;
//    	this.actual = ???;
    }

    public FitsOutputXmlTest(FitsOutput expected, FitsOutput value) {
    	super();
        this.expected = expected;
        this.actual = value;
    }
    
    @Parameters
    public static Collection<FitsOutput[]> data() throws Exception {	
    	Fits fits = new Fits("");
    	SAXBuilder builder = new SAXBuilder();
//		List<FitsOutput[]> inputs = new ArrayList<String[][]>();
        List<FitsOutput[]> inputs = new ArrayList<FitsOutput[]>();
	
		File inputDir =new File("tests/input");
		File outputDir =new File("tests/output");
				
		for(File input : inputDir.listFiles()) {
			//skip directories
			if(input.isDirectory()) {
				continue;
			}
			FitsOutput fitsOut = fits.examine(input);
			
			File outputFile = new File(outputDir + File.separator + input.getName()+".xml");
			if(!outputFile.exists()) {
				System.err.println("Not Found: "+outputFile.getPath());
				continue;
			}
			Document expectedXml = builder.build(new FileInputStream(outputFile));
			FitsOutput expectedFits = new FitsOutput(expectedXml);
			
			FitsOutput[][] tmp = new FitsOutput[][]{{expectedFits,fitsOut}};
			inputs.add(tmp[0]);
		}
        return inputs;
    }
    
	@Test
	public void testEquality() throws Exception {	
		//convert FitsOutput xml doms to strings for the diff	
		StringWriter sw = new StringWriter();
		Document expectedDom = expected.getFitsXml();
		Document actualDom = actual.getFitsXml();
		serializer.output(actualDom, sw);
		String actualStr = sw.toString(); 
		sw = new StringWriter();
		serializer.output(expectedDom, sw);
		String expectedStr = sw.toString();
		
		//get the file name in case of a failure
		FitsMetadataElement item = actual.getMetadataElement("filename");
	    DifferenceListener myDifferenceListener = new IgnoreAttributeValuesDifferenceListener();
	    Diff diff = new Diff(expectedStr,actualStr);
	    diff.overrideDifferenceListener(myDifferenceListener);

	    assertXMLIdentical("Error comparing: "+item.getValue(), diff, true);
	}
	
}

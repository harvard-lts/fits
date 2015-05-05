package edu.harvard.hul.ois.fits.junit;

import java.io.File;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;
import junit.framework.TestCase;

public class FitsOutputTest extends TestCase {
	/*
	public void testFitsOutput() throws FitsException  {
		Fits fits = new Fits();
		
		ArrayList<String> testFiles = new ArrayList<String>();
		testFiles.add("testfiles/faust1.pdf");
		testFiles.add("testfiles/test.pdf");
		testFiles.add("testfiles/test.jpg");
		
		for(String file : testFiles) {	
			FitsOutput output = fits.examine(new File(file));		
			
			assertNotNull(output.getFileInfoElements());
			assertNotNull(output.getFileStatusElements());
			assertNotNull(output.getTechMetadataElements());
			assertNotNull(output.getTechMetadataType());
			assertNotNull(output.getErrorMessages());
			assertNotNull(output.checkWellFormed());
			assertNotNull(output.checkValid());
			assertNotNull(output.getMetadataElement("md5checksum").getValue());
			
			String errorText = output.getErrorMessages();
			System.out.println(errorText);
			System.out.println("valid = "+output.checkValid());
			System.out.println("well-formed = "+output.checkWellFormed());
			System.out.println(output.getMetadataElement("md5checksum").getValue());
		}
		
	}*/
	
	public void testFitsOutput2() throws FitsException, XMLStreamException  {
		Fits fits = new Fits();
		
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		
		
		File problemsDir = new File("testfiles/problems");
		if(!problemsDir.exists()) {
			System.out.println("No files in the problem directory!");
			return;
		}
		for(File f : problemsDir.listFiles()) {
			if(f.getName().equals("CVS")) {
				continue;
			}
			FitsOutput output = fits.examine(f);		
			/*
			assertNotNull(output.getFileInfoElements());
			assertNotNull(output.getFileStatusElements());
			assertNotNull(output.getTechMetadataElements());
			assertNotNull(output.getTechMetadataType());
			assertNotNull(output.getErrorMessages());
			assertNotNull(output.checkWellFormed());
			assertNotNull(output.checkValid());
			assertNotNull(output.getMetadataElement("md5checksum").getValue());
			*/
			String errorText = output.getErrorMessages();
			XmlContent xml = output.getStandardXmlContent();
			if(xml != null) {
				XMLStreamWriter writer = xmlof.createXMLStreamWriter(System.out); 
				xml.output(writer);
				writer.close();
			}
			System.out.println(errorText);
			System.out.println("valid = "+output.checkValid());
			System.out.println("well-formed = "+output.checkWellFormed());
			System.out.println(output.getMetadataElement("md5checksum").getValue());
		}
		

		
	}
}

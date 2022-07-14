package edu.harvard.hul.ois.fits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FitsOutputVersionTest extends AbstractLoggingTest {
	
	private static Logger logger = LoggerFactory.getLogger(FitsOutputVersionTest.class);

	/**
	 * Test output of Fits version from the input file to FitsOutput.
	 */
	@Test
	public void testFitsOutputVersion() {

		try {
			Reader in = new FileReader(new File("testfiles/FitsOutputTest.xml"));
			SAXBuilder builder = new SAXBuilder();
			Document fitsXml = builder.build(in);
			FitsOutput fitsOutput = new FitsOutput(fitsXml);
			assertNotNull(fitsOutput);
			String inputVersion = fitsOutput.getFitsVersion();
			assertNotNull(inputVersion);
			assertEquals("FitsOutput version not the same as input file version", "1.1.0", inputVersion);
		} catch (JDOMException | IOException e) {
			fail("Could not parse input document: " + e.getClass().getSimpleName() + " -- " + e.getMessage());
		}
	}
		
}

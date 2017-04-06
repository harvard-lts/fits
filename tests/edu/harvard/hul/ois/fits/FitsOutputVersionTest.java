package edu.harvard.hul.ois.fits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.junit.Test;

import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;

public class FitsOutputVersionTest extends AbstractLoggingTest {
	
	private static Logger logger = LogManager.getLogger(FitsOutputVersionTest.class);

	/**
	 * Test to make sure Fits version initialization is passed along to FitsOutput class.
	 */
	@Test
	public void testFitsVersion() {
		// Must instantiate class to initialize version number.
		try {
			// Must instantiate class to initialize version number.
			@SuppressWarnings("unused")
			Fits fits = new Fits();
		} catch (FitsConfigurationException e1) {
			fail("Could not properly initialize Fits.java");
		}
		logger.info("Fits version: " + Fits.VERSION);
		FitsOutput fitsOutput = null;
		try {
			fitsOutput = new FitsOutput("<test></test>");
		} catch (JDOMException | IOException e) {
			fail("Could not parse input document");
		}
		assertNotNull(Fits.VERSION);
		assertNotNull(fitsOutput);
		assertEquals("FitsOutput version not the same as Fits version", Fits.VERSION, fitsOutput.getFitsVersion());
	}
		
}

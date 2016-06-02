package edu.harvard.hul.ois.fits.tests;

import java.io.File;

/**
 * This base class is for initializing logging for those test classes that don't use the Fits class (which
 * has its own logging initialization) or for  those test classes that want to override the logging configuration
 * as configured in the Fits constructor. This allows for a separate logging configuration for test classes
 * other than the default logging as set up within FITS.java.
 * 
 * @see edu.harvard.hul.ois.fits.Fits#Fits(String, File)
 * @author dan179
 */
public class AbstractLoggingTest {
	
	/**
	 * Configure logging with the test logging configuration file by setting a system property.
	 * See "Default Initialization Procedure" here: https://logging.apache.org/log4j/1.2/manual.html
	 */
	static {
		File log4jProperties = new File("tests.log4j.properties");
	    System.setProperty( "log4j.configuration", log4jProperties.toURI().toString());
	}
}

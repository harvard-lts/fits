package edu.harvard.hul.ois.fits.tests;

import java.io.File;

/**
 * This base class is for initializing logging for test classes to set the logging configuration
 * in the Fits constructor by means of an environment variable. This allows for a separate logging configuration for test classes
 * other than the default logging as set up within FITS.java.
 * 
 * @see edu.harvard.hul.ois.fits.Fits#Fits(String, File)
 * @author dan179
 */
public abstract class AbstractLoggingTest {
	
	/**
	 * Configure logging with the test logging configuration file by setting a system property.
	 * See "Default Initialization Procedure" here: https://logging.apache.org/log4j/1.2/manual.html
	 */
	static {
		File log4jProperties = new File("tests.log4j.properties");
	    System.setProperty( "log4j.configuration", log4jProperties.toURI().toString());
	}
}

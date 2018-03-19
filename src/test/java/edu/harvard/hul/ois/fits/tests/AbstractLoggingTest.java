/* 
 * Copyright 2017 Harvard University Library
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

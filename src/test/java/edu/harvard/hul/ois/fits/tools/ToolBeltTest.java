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
package edu.harvard.hul.ois.fits.tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;

public class ToolBeltTest extends AbstractLoggingTest {

	/**
	 * Tests reflection code in ToolBelt for instantiating a Tool implementation
	 * which contains only (default) no-argument constructor.
	 * 
	 * @see edu.harvard.hul.ois.fits.tools.ToolBelt#createToolClassInstance(
	 *      Class<?>, Fits)
	 */
	@Test
	public void noArgumentConstructorToolTest() {
		File fitsConfigFile = new File("testfiles/properties/fits_test_no_arg_tool.xml");
		assertNotNull(fitsConfigFile);
		try {
			// Instantiating Fits will exercise the ToolBelt.
			@SuppressWarnings("unused")
			Fits fits = new Fits(null, fitsConfigFile);
		} catch (FitsConfigurationException e) {
			fail("Could not instantiate Fits or the XMLConfiguration: " + e.getMessage());
		}
	}

	/**
	 * Tests reflection code in ToolBelt for instantiating a Tool implementation
	 * which contains a 1-argument constructor with Fits as the argument.
	 * 
	 * @see edu.harvard.hul.ois.fits.tools.ToolBelt#createToolClassInstance(
	 *      Class<?>, Fits)
	 */
	@Test
	public void oneArgumentConstructorToolTest() {
		File fitsConfigFile = new File("testfiles/properties/fits_test_one_arg_tool.xml");
		assertNotNull(fitsConfigFile);
		try {
			// Instantiating Fits will exercise the ToolBelt.
			@SuppressWarnings("unused")
			Fits fits = new Fits(null, fitsConfigFile);
		} catch (FitsConfigurationException e) {
			fail("Could not instantiate Fits or the XMLConfiguration: " + e.getMessage());
		}
	}
}

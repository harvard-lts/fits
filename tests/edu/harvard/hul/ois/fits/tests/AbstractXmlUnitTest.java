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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLIdentical;

import java.io.IOException;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

import edu.harvard.hul.ois.fits.junit.IgnoreNamedElementsDifferenceListener;

/**
 * Base class containing common functionality for performing XMLUnit tests.
 * 
 * @author dan179
 */
public class AbstractXmlUnitTest extends AbstractLoggingTest {
	
	// Suffix added to input file name for actual FITS output file.
	protected static final String ACTUAL_OUTPUT_FILE_SUFFIX = "_XmlUnitActualOutput.xml";

	// Suffix added to input file name for finding expected FITS output file.
	protected static final String EXPECTED_OUTPUT_FILE_SUFFIX = "_XmlUnitExpectedOutput.xml";

	// These are the attributes that should be ignored when comparing actual FITS output with
	// the expected output file.
	private static final String[] IGNORED_XML_ELEMENTS = {
			"version",
			"created",
			"toolversion",
			"dateModified",
			"fslastmodified",
			"startDate",
			"startTime",
			"timestamp", 
			"fitsExecutionTime",
			"executionTime",
			"filepath",
			"location",
			"lastmodified"};


	@BeforeClass
	public static void abstractClassSetup() throws Exception {
		// Set up XMLUnit for all classes.
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalizeWhitespace(true);
	}

	/**
	 * Allows for the overriding of XML elements to be ignored in the XMLUnit comparison.
	 */
	protected String[] getIgnoredXmlElements() {
		return IGNORED_XML_ELEMENTS;
	}
	
	/**
	 * This method performs the actual test of actual FITS output against expected.
	 */
	protected void testActualAgainstExpected(String actualXmlStr, String expectedXmlStr, String inputFilename)
			throws SAXException, IOException {
		Diff diff = new Diff(expectedXmlStr,actualXmlStr);

		// Initialize attributes or elements to ignore for difference checking
		diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener(getIgnoredXmlElements()));

		DetailedDiff detailedDiff = new DetailedDiff(diff);

		// Display any Differences
		@SuppressWarnings("unchecked")
		List<Difference> diffs = detailedDiff.getAllDifferences();
		if (!diff.identical()) { 
			StringBuffer differenceDescription = new StringBuffer(); 
			differenceDescription.append(diffs.size()).append(" differences"); 
			
			System.out.println(differenceDescription.toString());
			for(Difference difference : diffs) {
				System.out.println(difference.toString());
			}

		}
		assertXMLIdentical("Differences in XML for file: " + inputFilename, diff, true);
	}

}

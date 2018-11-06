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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.junit.IgnoreNamedElementsDifferenceListener;

/**
 * Base class containing common functionality for performing XMLUnit tests.
 * 
 * @author dan179
 */
public class AbstractXmlUnitTest extends AbstractLoggingTest {
	
	// Suffix added to input file name for actual FITS output file used for test comparison.
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

	// To be used when accessing an external FITS Servlet deployment.
	private static CloseableHttpClient httpclient;
	// Location of external FITS Servlet instance.
	private static String servicePostURL = "http://localhost:8080/fits/examine?includeStandardOutput=false";

	private static Logger logger = null;


	@BeforeClass
	public static void abstractClassSetup() throws Exception {
		// Set up XMLUnit for all classes.
	    logger = Logger.getLogger(AbstractXmlUnitTest.class);
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
			differenceDescription.append(diffs.size()).append(" differences for input file: " + inputFilename); 
			
			System.out.println(differenceDescription.toString());
			for(Difference difference : diffs) {
				System.out.println(difference.toString());
			}

		}
		assertXMLIdentical("Differences in XML for file: " + inputFilename, diff, true);
	}
	
	/**
	 * To be called from a @BeforeClass method in a class that is testing an external FITS web application.
	 */
	protected static void beforeServiceTest() throws Exception {
        httpclient = HttpClients.createDefault();
	}

	/**
	 * To be called from a @AfterClass method in a class that is testing an external FITS web application.
	 */
	protected static void afterServiceTest() throws Exception {
        httpclient = HttpClients.createDefault();
	}

	/**
	 * To be called from a method in a class that is testing an external FITS web application.
	 */
	protected FitsOutput examine(File inputFile) throws Exception {
		
    	HttpPost httpPost = new HttpPost(servicePostURL);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("datafile", inputFile, ContentType.APPLICATION_OCTET_STREAM, inputFile.getName());
		HttpEntity reqEntity = builder.build();
		httpPost.setEntity(reqEntity);

		logger.info("executing request " + httpPost.getRequestLine());
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
		FitsOutput fitsOutput = null;
		try {
			logger.info("HTTP Response Status Line: " + response.getStatusLine());
			// Expecting a 200 Status Code
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				String reason = response.getStatusLine().getReasonPhrase();
				logger.warn("Unexpected HTTP response status code:[" + response.getStatusLine().getStatusCode() +
						"] -- Reason (if available): " + reason);
			} else {
				HttpEntity resEntity = response.getEntity();
				InputStream is = resEntity.getContent();
				BufferedReader in = new BufferedReader( new InputStreamReader( is, StandardCharsets.UTF_8.name() ) );

				String output;
				StringBuilder sb = new StringBuilder();
				while ( (output = in.readLine()) != null ) {
					sb.append( output );
					sb.append(System.getProperty("line.separator"));
				}
				logger.info(sb.toString());
				in.close();
				EntityUtils.consume(resEntity);
				fitsOutput = new FitsOutput(sb.toString());
			}
		} finally {
			response.close();
		}
		return fitsOutput;
	}

}

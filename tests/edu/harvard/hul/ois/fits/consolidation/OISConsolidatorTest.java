/* 
 * Copyright 2016 Harvard University Library
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
package edu.harvard.hul.ois.fits.consolidation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.ToolBelt;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

/**
 * This test class is to verify output created by the OISConsolidator class.
 * 
 * @author dneiman
 */
public class OISConsolidatorTest extends AbstractLoggingTest {

	private static Logger logger = Logger.getLogger(OISConsolidatorTest.class);
	
	/**
	 * This is a trivial test of passing a single ToolOutput from the Droid tool into the OISConsolidator
	 * to verify its identity output.
	 */
	@Test
	public void singleTool() throws Exception {
		String inputFilename = "Winnie-the-Pooh-protected.epub";
		File input = new File("testfiles/" + inputFilename);
		File fitsConfigFile = new File("testfiles/properties/fits_droid_only.xml");
		
		// Make sure ToolOutput for each tool not reset after examine() so it can be reused for test.
		Fits fits = new Fits(null, fitsConfigFile) {
			
			@Override
			 public FitsOutput examine( File input ) throws FitsException {
				resetToolOutputAfterExaminingInput(false);
				return super.examine(input);
			}
		};
		
    	FitsOutput fitsOut = fits.examine(input);
    	
    	// output to file and console to view output if necessary, before running assertions
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_droid_only_Output.xml");
    	XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    	fitsOut.addStandardCombinedFormat();
    	serializer.output(fitsOut.getFitsXml(), System.out);
    	
    	ToolBelt toolBelt = fits.getToolbelt();
    	assertEquals(1, toolBelt.getTools().size());
    	Tool tool = toolBelt.getTools().get(0);
    	ToolOutput toolOutput = tool.getOutput();
    	assertNotNull(toolOutput);
    	OISConsolidator consolidator = new OISConsolidator(fits);
    	List<ToolOutput> results = new ArrayList<ToolOutput>();
    	results.add(toolOutput);
    	FitsOutput fitsOutput = consolidator.processResults(results);
    	
    	List<FitsIdentity> identities = fitsOutput.getIdentities();
    	assertNotNull(identities);
    	assertEquals(1, identities.size());
    	FitsIdentity ident = identities.get(0);
    	assertEquals("ZIP Format", ident.getFormat());
    	assertEquals("application/zip", ident.getMimetype());
    	assertEquals(1, ident.getReportingTools().size());
    	assertEquals("Droid", ident.getReportingTools().get(0).getName());
    	assertEquals(1, ident.getExternalIdentifiers().size());
	}
	
	/**
	 * This tests that a less specific identity (Droid) will not appear with one that is more specific (Tika)
	 * when the tool with the MORE specific identity appears first in the fits.xml tool list.
	 */
	@Test
	public void tikaBeforeDroid() throws Exception {
		String inputFilename = "Winnie-the-Pooh-protected.epub";
		File input = new File("testfiles/" + inputFilename);
		File fitsConfigFile = new File("testfiles/properties/fits_tika_droid.xml");
		
		// Make sure ToolOutput for each tool not reset after examine() so it can be reused for test.
		Fits fits = new Fits(null, fitsConfigFile) {
			
			@Override
			 public FitsOutput examine( File input ) throws FitsException {
				resetToolOutputAfterExaminingInput(false);
				return super.examine(input);
			}
		};
		
    	FitsOutput fitsOut = fits.examine(input);
    	
    	// output to file and console to view output if necessary, before running assertions
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_tika_droid_Output.xml");
    	XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    	fitsOut.addStandardCombinedFormat();
    	serializer.output(fitsOut.getFitsXml(), System.out);
    	
    	ToolBelt toolBelt = fits.getToolbelt();
    	assertEquals(2, toolBelt.getTools().size());
    	OISConsolidator consolidator = new OISConsolidator(fits);
    	List<ToolOutput> results = new ArrayList<ToolOutput>();
    	for (Tool tool : toolBelt.getTools()) {
    		ToolOutput toolOutput = tool.getOutput();
    		assertNotNull(toolOutput);
    		results.add(toolOutput);
    	}
    	FitsOutput fitsOutput = consolidator.processResults(results);
    	
    	List<FitsIdentity> identities = fitsOutput.getIdentities();
    	assertNotNull(identities);
    	assertEquals(1, identities.size());
    	FitsIdentity ident = identities.get(0);
    	assertEquals("EPUB", ident.getFormat());
    	assertEquals("application/epub+zip", ident.getMimetype());
    	assertEquals(1, ident.getReportingTools().size());
    	assertEquals("Tika", ident.getReportingTools().get(0).getName());
    	assertEquals(0, ident.getExternalIdentifiers().size());
	}
	
	/**
	 * This tests that a less specific identity (Droid) will not appear with one that is more specific (Tika)
	 * when the tool with the LESS specific identity appears first in the fits.xml tool list.
	 */
	@Test
	public void droidBeforeTika() throws Exception {
		String inputFilename = "Winnie-the-Pooh-protected.epub";
		File input = new File("testfiles/" + inputFilename);
		File fitsConfigFile = new File("testfiles/properties/fits_droid_tika.xml");
		
		// Make sure ToolOutput for each tool not reset after examine() so it can be reused for test.
		Fits fits = new Fits(null, fitsConfigFile) {
			
			@Override
			 public FitsOutput examine( File input ) throws FitsException {
				resetToolOutputAfterExaminingInput(false);
				return super.examine(input);
			}
		};
		
    	FitsOutput fitsOut = fits.examine(input);
    	
    	// output to file and console to view output if necessary, before running assertions
    	fitsOut.saveToDisk("test-generated-output/" + inputFilename + "_droid_tika_Output.xml");
    	XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    	fitsOut.addStandardCombinedFormat();
    	serializer.output(fitsOut.getFitsXml(), System.out);
    	
    	ToolBelt toolBelt = fits.getToolbelt();
    	assertEquals(2, toolBelt.getTools().size());
    	OISConsolidator consolidator = new OISConsolidator(fits);
    	List<ToolOutput> results = new ArrayList<ToolOutput>();
    	for (Tool tool : toolBelt.getTools()) {
    		ToolOutput toolOutput = tool.getOutput();
    		assertNotNull(toolOutput);
    		results.add(toolOutput);
    	}
    	FitsOutput fitsOutput = consolidator.processResults(results);
    	
    	List<FitsIdentity> identities = fitsOutput.getIdentities();
    	assertNotNull(identities);
    	assertEquals(1, identities.size());
    	FitsIdentity ident = identities.get(0);
    	assertEquals("EPUB", ident.getFormat());
    	assertEquals("application/epub+zip", ident.getMimetype());
    	assertEquals(1, ident.getReportingTools().size());
    	assertEquals("Tika", ident.getReportingTools().get(0).getName());
    	assertEquals(0, ident.getExternalIdentifiers().size());
	}

}

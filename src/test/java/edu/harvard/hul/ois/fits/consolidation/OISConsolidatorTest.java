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

import static edu.harvard.hul.ois.fits.FitsPaths.INPUT_DIR;
import static edu.harvard.hul.ois.fits.FitsPaths.OUTPUT_DIR;
import static edu.harvard.hul.ois.fits.FitsPaths.PROPS_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.ToolBelt;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * This test class is to verify output created by the OISConsolidator class.
 *
 * @author dneiman
 */
public class OISConsolidatorTest extends AbstractLoggingTest {

    /**
     * This is a trivial test of passing a single ToolOutput from the Droid tool into the OISConsolidator
     * to verify its identity output.
     */
    @Test
    public void singleTool() throws Exception {
        String inputFilename = "Winnie-the-Pooh-protected.epub";
        File input = new File(INPUT_DIR + inputFilename);
        File fitsConfigFile = new File(PROPS_DIR + "fits_droid_only.xml");

        // Make sure ToolOutput for each tool not reset after examine() so it can be reused for test.
        Fits fits = new Fits(null, fitsConfigFile) {

            @Override
            public FitsOutput examine(File input) throws FitsException {
                resetToolOutputAfterExaminingInput(false);
                return super.examine(input);
            }
        };

        FitsOutput fitsOut = fits.examine(input);

        // output to file and console to view output if necessary, before running assertions
        fitsOut.saveToDisk(OUTPUT_DIR + inputFilename + "_droid_only_Output.xml");
        fitsOut.addStandardCombinedFormat();

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
        assertEquals("EPUB", ident.getFormat());
        assertEquals("application/epub+zip", ident.getMimetype());
        assertEquals(1, ident.getReportingTools().size());
        assertEquals("Droid", ident.getReportingTools().get(0).getName());
        assertEquals(1, ident.getExternalIdentifiers().size());
    }

    /**
     * This tests that a less specific identity (jhove) will not appear with one that is more specific (droid)
     * when the tool with the MORE specific identity appears first in the fits.xml tool list.
     */
    @Test
    public void droidBeforeJhove() throws Exception {
        String inputFilename = "image-vectorgraphic.svg";
        File input = new File(INPUT_DIR + inputFilename);
        File fitsConfigFile = new File(PROPS_DIR + "fits_droid_jhove.xml");

        // Make sure ToolOutput for each tool not reset after examine() so it can be reused for test.
        Fits fits = new Fits(null, fitsConfigFile) {

            @Override
            public FitsOutput examine(File input) throws FitsException {
                resetToolOutputAfterExaminingInput(false);
                return super.examine(input);
            }
        };

        FitsOutput fitsOut = fits.examine(input);

        // output to file and console to view output if necessary, before running assertions
        fitsOut.saveToDisk(OUTPUT_DIR + inputFilename + "_droid_tika_Output.xml");
        fitsOut.addStandardCombinedFormat();

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
        assertEquals("Scalable Vector Graphics (SVG)", ident.getFormat());
        assertEquals("image/svg+xml", ident.getMimetype());
        assertEquals(1, ident.getReportingTools().size());
        assertEquals("Droid", ident.getReportingTools().get(0).getName());
        assertEquals(1, ident.getExternalIdentifiers().size());
    }

    /**
     * This tests that a less specific identity (jhove) will not appear with one that is more specific (droid)
     * when the tool with the LESS specific identity appears first in the fits.xml tool list.
     */
    @Test
    public void jhoveBeforeDroid() throws Exception {
        String inputFilename = "image-vectorgraphic.svg";
        File input = new File(INPUT_DIR + inputFilename);
        File fitsConfigFile = new File(PROPS_DIR + "fits_jhove_droid.xml");

        // Make sure ToolOutput for each tool not reset after examine() so it can be reused for test.
        Fits fits = new Fits(null, fitsConfigFile) {

            @Override
            public FitsOutput examine(File input) throws FitsException {
                resetToolOutputAfterExaminingInput(false);
                return super.examine(input);
            }
        };

        FitsOutput fitsOut = fits.examine(input);

        // output to file and console to view output if necessary, before running assertions
        fitsOut.saveToDisk(OUTPUT_DIR + inputFilename + "_jhove_droid_Output.xml");
        fitsOut.addStandardCombinedFormat();

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
        assertEquals("Scalable Vector Graphics (SVG)", ident.getFormat());
        assertEquals("image/svg+xml", ident.getMimetype());
        assertEquals(1, ident.getReportingTools().size());
        assertEquals("Droid", ident.getReportingTools().get(0).getName());
        assertEquals(1, ident.getExternalIdentifiers().size());
    }
}

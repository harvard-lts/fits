/*
 * Copyright 2015 Harvard University Library
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
package edu.harvard.hul.ois.fits.junit;

import static edu.harvard.hul.ois.fits.FitsPaths.ACTUAL_OUTPUT_FILE_SUFFIX;
import static edu.harvard.hul.ois.fits.FitsPaths.EXPECTED_OUTPUT_FILE_SUFFIX;
import static edu.harvard.hul.ois.fits.FitsPaths.INPUT_DIR;
import static edu.harvard.hul.ois.fits.FitsPaths.OUTPUT_DIR;
import static edu.harvard.hul.ois.fits.FitsPaths.PROPS_DIR;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractXmlUnitTest;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class VideoStdSchemaXmlUnitTest extends AbstractXmlUnitTest {

    // These override the values in the parent class.
    private static final String[] OVERRIDING_IGNORED_XML_ELEMENTS = {
        "version",
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
        "lastmodified",
        "ebucore:locator"
    };

    /*
     *  Only one Fits instance is needed to run all tests.
     *  This also speeds up the tests.
     */
    private static Fits fits;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // Set up FITS for entire class.
        fits = new Fits();
    }

    @AfterClass
    public static void afterClass() {
        fits = null;
    }

    @Test
    public void testVideoXmlUnitFitsOutput_AVC() throws Exception {
        testFile("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4", fits, OutputType.DEFAULT);
    }

    @Test
    public void testVideoXmlUnitFitsOutput_DV() throws Exception {
        testFile("FITS-SAMPLE-26.mov", fits, OutputType.DEFAULT);
    }

    @Test
    public void testVideoXmlUnitStandardOutput_AVC() throws Exception {
        testFile("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4", fits, OutputType.STANDARD);
    }

    @Test
    public void testVideoXmlUnitStandardOutput_DV() throws Exception {
        testFile("FITS-SAMPLE-26.mov", fits, OutputType.STANDARD);
    }

    @Test
    public void testVideoXmlUnitCombinedOutput_DV() throws Exception {
        testFile("FITS-SAMPLE-26.mov");
    }

    /**
     * Tests that the output from FITS matches the expected output.
     */
    @Test
    public void testVideoXmlUnitOutput_MXF() throws Exception {
        testFile("freeMXF-mxf1a.mxf", fits, OutputType.DEFAULT);
    }

    @Test
    public void testVideoXmlUnitCombinedOutput() throws Exception {
        testFile("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_4_8_1_2_1_1.mov");
    }

    @Test
    public void testVideoXmlUnitFitsOutput_AVC_NO_MD5() throws Exception {
        File fitsConfigFile = new File(PROPS_DIR + "fits_no_md5_video.xml");
        Fits fits = new Fits(null, fitsConfigFile);

        // First generate the FITS output
        String inputFilename = "FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4";
        String namePart = "-no-md5";
        File input = new File(INPUT_DIR + inputFilename);
        FitsOutput fitsOut = fits.examine(input);
        fitsOut.saveToDisk(OUTPUT_DIR + inputFilename + namePart + ACTUAL_OUTPUT_FILE_SUFFIX);

        XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
        String actualXmlStr = serializer.outputString(fitsOut.getFitsXml());

        // Read in the expected XML file
        String expectedXmlStr = FileUtils.readFileToString(
                new File(OUTPUT_DIR + inputFilename + namePart + EXPECTED_OUTPUT_FILE_SUFFIX), StandardCharsets.UTF_8);

        testActualAgainstExpected(actualXmlStr, expectedXmlStr, inputFilename);
    }

    /**
     * Send in set of XML elements to ignore that overrides the default.
     */
    @Override
    protected String[] getIgnoredXmlElements() {
        return OVERRIDING_IGNORED_XML_ELEMENTS;
    }
}

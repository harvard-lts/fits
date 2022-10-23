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
package edu.harvard.hul.ois.fits.junit.service;

import static edu.harvard.hul.ois.fits.FitsPaths.PROPS_DIR;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.tests.AbstractWebAppTest;
import java.io.File;
import org.junit.Ignore;
import org.junit.Test;

/**
 * These tests compare actual FITS output with expected output on video files.
 * NOTE: This is an integration test that requires a running web application with the
 * FITS Service running.
 *
 * @author dan179
 */
@Ignore
public class VideoStdSchemaTestXmlServiceUnit extends AbstractWebAppTest {

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

    @Test
    public void testVideoXmlUnitFitsOutput_AVC() throws Exception {
        testFileInWebApp("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4", OutputType.DEFAULT);
    }

    @Test
    public void testVideoXmlUnitFitsOutput_DV() throws Exception {
        testFileInWebApp("FITS-SAMPLE-26.mov", OutputType.DEFAULT);
    }

    @Test
    public void testVideoXmlUnitStandardOutput_AVC() throws Exception {
        testFileInWebApp("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4", OutputType.STANDARD);
    }

    @Test
    public void testVideoXmlUnitStandardOutput_DV() throws Exception {
        testFileInWebApp("FITS-SAMPLE-26.mov", OutputType.STANDARD);
    }

    @Test
    public void testVideoXmlUnitCombinedOutput_AVC() throws Exception {
        testFileInWebApp("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4", OutputType.DEFAULT);
    }

    @Test
    public void testVideoXmlUnitCombinedOutput_DV() throws Exception {
        testFileInWebApp("FITS-SAMPLE-26.mov", OutputType.DEFAULT);
    }

    /**
     * Tests that the output from FITS matches the expected output.
     */
    @Test
    public void testVideoXmlUnitOutput_MXF() throws Exception {
        testFileInWebApp("freeMXF-mxf1a.mxf", OutputType.DEFAULT);
    }

    @Test
    public void testVideoXmlUnitCombinedOutput_MPEG2() throws Exception {
        testFileInWebApp("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_4_8_1_2_1_1.mov", OutputType.DEFAULT);
    }

    @Test
    public void testVideoXmlUnitFitsOutput_AVC_NO_MD5() throws Exception {
        File fitsConfigFile = new File(PROPS_DIR + "fits_no_md5_video.xml");
        Fits fits = new Fits(null, fitsConfigFile);

        // First generate the FITS output
        testFile("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4", fits, OutputType.DEFAULT);
    }

    /**
     * Send in set of XML elements to ignore that overrides the default.
     */
    @Override
    protected String[] getIgnoredXmlElements() {
        return OVERRIDING_IGNORED_XML_ELEMENTS;
    }
}

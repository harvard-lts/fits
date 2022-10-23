/*
 * Copyright 2019 Harvard University Library
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

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to process a group of disparate input files from a specific
 * input directory and direct the FITS metadata output files to another directory.
 * There is also a text that can be configured to process a single file.
 */
@Ignore // The class should be configured with @Ignore by default.
public class BulkDirectoryTest extends AbstractLoggingTest {

    /*
     *  Only one Fits instance is needed to run all tests.
     *  This also speeds up the tests.
     */
    private static Fits fits;

    private static final String FITS_CONFIG_FILE = "<some_directory>/some-fits.xml";
    private static final String FITS_INPUT_DIRECTORY = "<some_directory_for_input_files>";
    private static final String FITS_OUTPUT_DIRECTORY = "test-generated-output";

    private static Logger logger = LoggerFactory.getLogger(BulkDirectoryTest.class);

    @BeforeClass
    public static void beforeClass() throws Exception {
        // Set up FITS for entire class.

        // The following uses the standard FITS config file, fits.xml
        //		fits = new Fits();
        // Use the following two lines to use a custom config file
        File fitsConfigFile = new File(FITS_CONFIG_FILE);
        fits = new Fits(null, fitsConfigFile);
    }

    @AfterClass
    public static void afterClass() {
        fits = null;
    }

    @Test
    public void testDirectory() throws Exception {

        File inputDir = new File(FITS_INPUT_DIRECTORY);
        File[] inputFiles = inputDir.listFiles();
        logger.info("Processing this many files: " + inputFiles.length);
        for (File input : inputFiles) {
            String fileName = input.getName();
            logger.info("processsing file: " + fileName);
            FitsOutput fitsOut = fits.examine(input);
            fitsOut.addStandardCombinedFormat();
            fitsOut.saveToDisk(FITS_OUTPUT_DIRECTORY + File.separator + fileName + OUTPUT_FILE_SUFFIX);
        }
    }

    @Test
    public void testSingleFile() throws Exception {

        String fileName = "T-350_036_Archival_Side_1.adl";
        File input = new File(FITS_INPUT_DIRECTORY + File.separator + fileName);
        FitsOutput fitsOut = fits.examine(input);
        fitsOut.addStandardCombinedFormat();
        fitsOut.saveToDisk(FITS_OUTPUT_DIRECTORY + File.separator + fileName + OUTPUT_FILE_SUFFIX);
        fits.getToolbelt().printToolInfo(true);
    }
}

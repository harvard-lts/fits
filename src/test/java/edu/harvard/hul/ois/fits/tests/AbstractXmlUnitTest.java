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

import static edu.harvard.hul.ois.fits.FitsPaths.ACTUAL_OUTPUT_FILE_SUFFIX;
import static edu.harvard.hul.ois.fits.FitsPaths.EXPECTED_OUTPUT_FILE_SUFFIX;
import static edu.harvard.hul.ois.fits.FitsPaths.INPUT_DIR;
import static edu.harvard.hul.ois.fits.FitsPaths.OUTPUT_DIR;
import static edu.harvard.hul.ois.fits.FitsPaths.OUTPUT_FILE_SUFFIX;
import static edu.harvard.hul.ois.fits.FitsPaths.PROPS_DIR;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLIdentical;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsFormat;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.junit.IgnoreNamedElementsDifferenceListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.jdom2.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Base class containing common functionality for performing XMLUnit tests.
 *
 * @author dan179
 */
public class AbstractXmlUnitTest extends AbstractLoggingTest {

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
        "lastmodified",
        "audioObjectRef",
        "faceRef",
        "ID",
        "ownerRef",
        "formatRef",
        "faceRegionRef"
    };

    private static Logger logger = null;

    /*
     *  Only one Fits instance is needed to run all tests.
     *  This also speeds up the tests.
     */
    protected static Fits fits;

    /**
     * true if the expected test result xml files should be overwritten with the current results. This is useful for
     * doing a bulk expectation update.
     */
    private static boolean overwrite;

    @BeforeClass
    public static void abstractClassSetup() throws FitsConfigurationException {
        // Set up XMLUnit for all classes.
        logger = LoggerFactory.getLogger(AbstractXmlUnitTest.class);
        overwrite = Boolean.parseBoolean(System.getProperty("overwrite", "false"));
    }

    @AfterClass
    public static void abstractAfterClass() {
        fits = null;
    }

    @Before
    public void abstractSetup() throws FitsConfigurationException {
        // Same FITS instance is used for all of the tests in a class
        if (fits == null) {
            String configFile = fitsConfigFile();
            if (configFile == null) {
                fits = new Fits();
            } else {
                fits = new Fits(null, new File(PROPS_DIR + configFile));
            }
        }
    }

    /**
     * Subclasses should override this method if they want to start FITS using a custom config file. The returned
     * config file name should be relative to `testfiles/properties`.
     *
     * @return the name of the config file in testfiles/properties to use
     */
    protected String fitsConfigFile() {
        return null;
    }

    /**
     * Allows for the overriding of XML elements to be ignored in the XMLUnit comparison.
     */
    protected String[] getIgnoredXmlElements() {
        return IGNORED_XML_ELEMENTS;
    }

    /**
     * Examines the specified file (relative to the `testfiles/input` directory) and compares the output to the expected
     * output. The expected output file must exist at `testfiles/output/INPUT_NAME_XmlUnitExpectedOutput.xml`.
     * The generated output file will be at `testfiles/output/INPUT_NAME_XmlUnitActualOutput.xml`.
     *
     * @param inputFilename the name of the file to examine relative testfiles/input
     * @throws Exception
     */
    protected void testFile(String inputFilename) throws Exception {
        testFile(inputFilename, fits, OutputType.COMBINED);
    }

    /**
     * Examines the specified file (relative to the `testfiles/input` directory) and compares the output to the expected
     * output. The expected output file must exist at `testfiles/output/INPUT_NAME_XmlUnitExpectedOutput.xml`.
     * The generated output file will be at `testfiles/output/INPUT_NAME_XmlUnitActualOutput.xml`.
     *
     * @param inputFilename the name of the file to examine relative testfiles/input
     * @param fits the fits instance to use
     * @throws Exception
     */
    protected void testFile(String inputFilename, Fits fits, OutputType outputType) throws Exception {
        File input = new File(INPUT_DIR + inputFilename);
        FitsOutput fitsOut = fits.examine(input);
        writeAndValidate(fitsOut, inputFilename, outputType);
    }

    protected void writeAndValidate(FitsOutput fitsOut, String inputFilename, OutputType outputType) throws Exception {
        XMLOutputter serializer = new XMLOutputter(FitsFormat.xmlFormat());
        String actualXmlStr;
        String namePart = "";
        boolean writeStr = false;

        switch (outputType) {
            case COMBINED:
                fitsOut.addStandardCombinedFormat();
                actualXmlStr = serializer.outputString(fitsOut.getFitsXml());
                break;
            case STANDARD:
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Fits.outputStandardSchemaXml(fitsOut, out);
                actualXmlStr = out.toString();
                namePart = "-standard-only";
                writeStr = true;
                break;
            case DEFAULT:
                actualXmlStr = serializer.outputString(fitsOut.getFitsXml());
                namePart = "-default";
                break;
            default:
                throw new IllegalStateException();
        }

        String className = this.getClass().getSimpleName();
        String actualFile = OUTPUT_DIR + inputFilename + namePart + "_" + className + ACTUAL_OUTPUT_FILE_SUFFIX;

        if (writeStr) {
            FileUtils.writeStringToFile(new File(actualFile), actualXmlStr, StandardCharsets.UTF_8);
        } else {
            fitsOut.saveToDisk(actualFile);
        }

        // Read in the expected XML file
        String expectedFile = OUTPUT_DIR + inputFilename + namePart + "_" + className + ACTUAL_OUTPUT_FILE_SUFFIX;
        if (Files.notExists(Paths.get(expectedFile))) {
            expectedFile = OUTPUT_DIR + inputFilename + namePart + EXPECTED_OUTPUT_FILE_SUFFIX;
        }
        String expectedXmlStr = FileUtils.readFileToString(new File(expectedFile), StandardCharsets.UTF_8);

        if (overwrite) {
            System.out.println("Overwriting test expectations at: " + expectedFile);
            Files.copy(Paths.get(actualFile), Paths.get(expectedFile), StandardCopyOption.REPLACE_EXISTING);
        } else {
            testActualAgainstExpected(actualXmlStr, expectedXmlStr, actualFile, expectedFile);
        }
    }

    /**
     * Examines the specified file (relative to the `testfiles/input` directory) and writes the standard combined
     * output to `testfiles/output/INPUT_NAME_Output.xml`. This DOES NOT make any assertions on the output.
     *
     * @param inputFilename the name of the file to examine relative testfiles/input
     * @return the FitsOutput object
     * @throws Exception
     */
    protected FitsOutput writeOutput(String inputFilename) throws Exception {
        File input = new File(INPUT_DIR + inputFilename);
        FitsOutput fitsOut = fits.examine(input);
        fitsOut.addStandardCombinedFormat();
        fitsOut.saveToDisk(OUTPUT_DIR + inputFilename + OUTPUT_FILE_SUFFIX);
        return fitsOut;
    }

    /**
     * This method performs the actual test of actual FITS output against expected.
     */
    protected void testActualAgainstExpected(
            String actualXmlStr, String expectedXmlStr, String actualFile, String expectedFile)
            throws SAXException, IOException {
        Diff diff = new Diff(expectedXmlStr, actualXmlStr);

        // Initialize attributes or elements to ignore for difference checking
        diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener(getIgnoredXmlElements()));

        DetailedDiff detailedDiff = new DetailedDiff(diff);

        // Display any Differences
        @SuppressWarnings("unchecked")
        List<Difference> diffs = detailedDiff.getAllDifferences();
        if (!diff.identical()) {
            System.out.printf(
                    "%s differences between actual %s and expected %s%n", diffs.size(), actualFile, expectedFile);
            for (Difference difference : diffs) {
                System.out.println(difference.toString());
            }
        }
        assertXMLIdentical(
                String.format("Differences between actual %s and expected %s", actualFile, expectedFile), diff, true);
    }

    public enum OutputType {
        DEFAULT,
        STANDARD,
        COMBINED
    }
}

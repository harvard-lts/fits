package edu.harvard.hul.ois.fits;

public final class FitsPaths {

    /**
     * The directory that contains the input files for the tests
     */
    public static final String INPUT_DIR = "testfiles/input/";

    /**
     * The directory that contains the output files, including the expected output
     */
    public static final String OUTPUT_DIR = "testfiles/output/";

    /**
     * The directory that contains FITS config files
     */
    public static final String PROPS_DIR = "testfiles/properties/";

    /**
     * Suffix added to input file name for actual FITS output file used for test comparison.
     */
    public static final String ACTUAL_OUTPUT_FILE_SUFFIX = "_XmlUnitActualOutput.xml";

    /**
     * Suffix added to input file name for finding expected FITS output file.
     */
    public static final String EXPECTED_OUTPUT_FILE_SUFFIX = "_XmlUnitExpectedOutput.xml";

    /**
     * Suffix added to input file name for actual FITS output file NOT used for test comparison.
     */
    public static final String OUTPUT_FILE_SUFFIX = "_Output.xml";

    private FitsPaths() {}
}

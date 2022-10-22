//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits;

import edu.harvard.hul.ois.fits.consolidation.ToolOutputConsolidator;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.mapping.FitsXmlMapper;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.Tool.RunStatus;
import edu.harvard.hul.ois.fits.tools.ToolBelt;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for FITS.
 */
public class Fits {

    public static volatile String FITS_HOME; // during initialization this value will always have a trailing slash.
    public static String FITS_XML_DIR;
    public static String FITS_TOOLS_DIR;
    public static String VERSION = "<unknown>";
    public static final String XML_NAMESPACE = "http://hul.harvard.edu/ois/xml/ns/fits/fits_output";

    private static boolean traverseDirs;
    private static boolean
            nestDirs; // whether traversing nested directories of input files creates nest output directories - if
    // false, all output goes in same output directory
    private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private static boolean rawToolOutput = false;

    private static final String FITS_CONFIG_FILE_NAME = "fits.xml";
    private static final String VERSION_PROPERTIES_FILE = "version.properties";
    private static final int DEFAULT_MAX_THREADS = 20;

    private XMLConfiguration config;
    private FitsXmlMapper mapper;
    private boolean enableStatistics;
    private boolean consolidateFirstIdentity;
    private String externalOutputSchema;
    private String internalOutputSchema;
    private boolean validateToolOutput;
    private int maxThreads;
    private ToolOutputConsolidator consolidator;
    private ToolBelt toolbelt;
    private boolean resetToolOutput = true; // should always be true except for unit tests

    private static Logger logger;

    static {
        // set FITS_HOME from environment variable if it exists
        FITS_HOME = System.getenv("FITS_HOME");
        if (StringUtils.isEmpty(FITS_HOME)) {
            // if not set use the current directory
            FITS_HOME = "";
        }
    }

    /**
     * Default, no-arg constructor. FITS configuration file expected in default location with
     * FITS_HOME either default location or set by environment variable.
     *
     * @throws FitsConfigurationException If there is a problem configuring FITS.
     */
    public Fits() throws FitsConfigurationException {
        this(null);
    }

    /**
     * Constructor with path to alternate FITS_HOME.
     *
     * @param fits_home Full path to home directory of FITS installation.
     *        NOTE: If FITS_HOME set as environment variable this argument has no effect.
     * @throws FitsConfigurationException If there is a problem configuring FITS.
     */
    public Fits(String fits_home) throws FitsConfigurationException {
        this(fits_home, null);
    }

    /**
     * Constructor with alternate FITS_HOME and alternate FITS configuration file.
     *
     * @param fits_home Full path to home directory of FITS installation.
     *        NOTE: If FITS_HOME set as environment variable this argument has no effect.
     * @param fitsXmlConfig File containing FITS configuration.
     * @throws FitsConfigurationException If there is a problem configuring FITS.
     */
    public Fits(String fits_home, File fitsXmlConfig) throws FitsConfigurationException {

        // NOTE: a "FITS_HOME" environment variable (see initial static block of this class)
        // "wins" over a FITS home value passed into the constructor.
        if (StringUtils.isEmpty(FITS_HOME)) {
            // if env variable not set check for fits_home passed into constructor
            if (fits_home != null) {
                FITS_HOME = fits_home;
            }
        }

        setFitsVersionFromFile();

        FITS_XML_DIR = FITS_HOME + "xml" + File.separator;
        FITS_TOOLS_DIR = FITS_HOME + "tools" + File.separator;

        // Set up logging explicitly so one of the tools aggregated into FITS does not circumvent this.
        //
        // First look for a system property (the Log4j preferred way of configuration) at the Log4j expected value,
        // "log4j2.configurationFile".
        // This value can be either a file path, file protocol (e.g. - file:/path/to/log4j2.xml), or a URL
        // (http://some/server/log4j2.xml).
        // If this value either is does not, the default file that comes with FITS will be used for initialization.
        String log4jSystemProp = System.getProperty("log4j2.configurationFile");
        if (log4jSystemProp == null) {
            File log4jProperties = new File(FITS_HOME + "log4j2.xml");
            System.setProperty("log4j2.configurationFile", log4jProperties.getPath());
        }
        logger = LoggerFactory.getLogger(Fits.class);
        logger.info("Logging initialized with: " + System.getProperty("log4j2.configurationFile"));
        try {
            if (fitsXmlConfig != null) {
                config = new XMLConfiguration(fitsXmlConfig);
            } else {
                config = new XMLConfiguration(FITS_XML_DIR + FITS_CONFIG_FILE_NAME);
            }
        } catch (ConfigurationException e) {
            logger.error(
                    "Error reading {}{}: {}",
                    FITS_XML_DIR,
                    FITS_CONFIG_FILE_NAME,
                    e.getClass().getName());
            throw new FitsConfigurationException("Error reading " + FITS_XML_DIR + FITS_CONFIG_FILE_NAME, e);
        }
        try {
            mapper = new FitsXmlMapper();
        } catch (Exception e) {
            logger.error("Error creating FITS XML Mapper: {}", e.getClass().getName());
            throw new FitsConfigurationException("Error creating FITS XML Mapper", e);
        }
        // required config values
        try {
            validateToolOutput = config.getBoolean("output.validate-tool-output");
            externalOutputSchema = config.getString("output.external-output-schema");
            internalOutputSchema = config.getString("output.internal-output-schema");
            enableStatistics = config.getBoolean("output.enable-statistics");
            consolidateFirstIdentity = config.getBoolean("output.consolidate-first-identity", true);
        } catch (NoSuchElementException e) {
            logger.error("Error in configuration file: {}", e.getClass().getName());
            System.out.println("Error inconfiguration file: " + e.getMessage());
            return;
        }

        // optional config values GDM 16-Nov-2012
        try {
            maxThreads = config.getShort("process.max-threads");
        } catch (NoSuchElementException e) {
            logger.warn("'max-threads' value not set in fit.xml. Setting to default: {}", DEFAULT_MAX_THREADS);
            maxThreads = DEFAULT_MAX_THREADS;
        }
        if (maxThreads < 1) {
            // If invalid number specified, use a default.
            logger.warn(
                    "Invalid number of threads specified: {} -- Resetting to default: {}",
                    maxThreads,
                    DEFAULT_MAX_THREADS);
            maxThreads = DEFAULT_MAX_THREADS;
        }
        logger.debug("Maximum threads = " + maxThreads);

        String consolidatorClassFullyQualifiedName = config.getString("output.dataConsolidator[@class]");
        try {
            // Instantiate the Consolidator class using Reflection by passing Fits into the constructor.
            Class<?> c = Class.forName(consolidatorClassFullyQualifiedName);
            Constructor<?> ctor = c.getConstructor(Fits.class);
            consolidator = (ToolOutputConsolidator) ctor.newInstance(this);
        } catch (Exception e) {
            throw new FitsConfigurationException("Error initializing " + consolidatorClassFullyQualifiedName, e);
        }

        toolbelt = new ToolBelt(config, this);
    }

    public static void main(String[] args) throws FitsException, IOException, ParseException, XMLStreamException {

        setFitsVersionFromFile();

        Options options = new Options();
        options.addOption("i", true, "input file or directory (required)");
        options.addOption("r", false, "process directories recursively when -i is a directory ");
        options.addOption(
                "n",
                false,
                "output directories are nested when recursively processing nested directories (when -r is set and -i is a directory) (optional)");
        options.addOption(
                "o", true, "Directs the FITS output to a file or directory (if -i is a directory) rather than console");
        options.addOption("h", false, "print this message");
        options.addOption("v", false, "print version information");
        options.addOption("f", true, "alternate fits.xml configuration file location (optional)");
        options.addOption("t", false, "include all raw tool output");
        OptionGroup outputOptions = new OptionGroup();
        Option stdxml = new Option(
                "x",
                false,
                "convert FITS output to a standard metadata schema -- note: only standard schema metadata is output");
        Option combinedStd = new Option("xc", false, "output using a standard metadata schema and include FITS xml");
        outputOptions.addOption(stdxml);
        outputOptions.addOption(combinedStd);
        options.addOptionGroup(outputOptions);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        if (cmd.hasOption("h")) {
            printHelp(options);
            System.exit(0);
        }
        if (cmd.hasOption("v")) {
            System.out.println(Fits.VERSION);
            System.exit(0);
        }
        if (cmd.hasOption("r")) {
            traverseDirs = true;
        } else {
            traverseDirs = false;
        }
        if (cmd.hasOption("n")) {
            nestDirs = true;
        } else {
            nestDirs = false;
        }
        rawToolOutput = cmd.hasOption("t");

        File fitsConfigFile = null;
        try {
            if (cmd.hasOption('f')) {
                String input = cmd.getOptionValue('f');
                if (StringUtils.isEmpty(input)) {
                    throw new FitsException("When using the -f option there must be an associated value.");
                }
                fitsConfigFile = new File(input);
                if (!fitsConfigFile.isFile() || !fitsConfigFile.canRead()) {
                    throw new FitsException("The FITS configuration file: " + input + " cannot be read.");
                }
            }

            if (cmd.hasOption("i")) {
                String input = cmd.getOptionValue("i");
                File inputFile = new File(input);

                if (inputFile.isDirectory()) {
                    String outputDir = cmd.getOptionValue("o");
                    if (outputDir == null || !(new File(outputDir).isDirectory())) {
                        throw new FitsException(
                                "When FITS is run in directory processing mode the output location must be a directory.");
                    }
                    Fits fits = constructFits(fitsConfigFile);
                    fits.doDirectory(inputFile, new File(outputDir), cmd.hasOption("x"), cmd.hasOption("xc"));
                } else { // inputFile is a file so output -o must either be a file or not set at all
                    String outputFile = cmd.getOptionValue("o");
                    if (outputFile != null && (new File(outputFile).isDirectory())) {
                        throw new FitsException(
                                "When FITS is processing a single file the output location must also be a file.");
                    }
                    Fits fits = constructFits(fitsConfigFile);
                    FitsOutput result = fits.doSingleFile(inputFile);
                    fits.outputResults(result, cmd.getOptionValue("o"), cmd.hasOption("x"), cmd.hasOption("xc"), false);
                }
            } else {
                System.err.println("Invalid CLI options: -i <arg> is required.");
                printHelp(options);
                System.exit(1);
            }
        } catch (FitsException fe) {
            System.err.println(fe.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }

    private static Fits constructFits(File fitsConfigFile) throws FitsConfigurationException {
        Fits fits = null;
        if (fitsConfigFile != null) {
            fits = new Fits(null, fitsConfigFile);
        } else {
            fits = new Fits();
        }
        return fits;
    }

    /*
     * Called from either main() for stand-alone application usage or constructor
     * when used by another program, this reads the properties file containing the
     * current version of FITS.
     *
     * Precondition of this method is that the static FITS_HOME has been set via
     * an environment variable, being passed into a constructor, or is just the current directory.
     */
    private static void setFitsVersionFromFile() {

        // If fits home is not an empty string and doesn't end with a file
        // separator character, add one
        if (FITS_HOME.length() > 0 && !FITS_HOME.endsWith(File.separator)) {
            FITS_HOME = FITS_HOME + File.separator;
        }

        // get version from properties file and set in class
        String versionPropFileFullPath = "";
        if (!StringUtils.isEmpty(FITS_HOME)) {
            versionPropFileFullPath = FITS_HOME;
        }

        File versionFile = new File(versionPropFileFullPath + VERSION_PROPERTIES_FILE);
        Properties versionProps = new Properties();
        try {
            versionProps.load(new FileInputStream(versionFile));
            String version = versionProps.getProperty("build.version");
            if (version != null && !version.isEmpty()) {
                Fits.VERSION = version;
            }
        } catch (IOException e) {
            System.err.println(
                    "Problem loading [" + VERSION_PROPERTIES_FILE + "]: " + "Cannot display FITS version information.");
        }
    }

    /**
     * Recursively processes all files in the directory.
     *
     * @param intputFile
     * @param useStandardSchemas
     * @throws IOException
     * @throws XMLStreamException
     * @throws FitsException
     */
    private void doDirectory(File inputDir, File outputDir, boolean useStandardSchemas, boolean standardCombinedFormat)
            throws FitsException, XMLStreamException, IOException {
        if (inputDir.listFiles() == null) {
            return;
        }

        logger.info("Processing directory " + inputDir.getAbsolutePath());

        for (File f : inputDir.listFiles()) {

            if (f == null || !f.exists() || !f.canRead()) {
                continue;
            }

            logger.info("processing " + f.getPath());

            if (f.isDirectory() && traverseDirs) {
                // need to reset original directory after return from recursive call when nesting output
                File savedDir = outputDir;
                if (nestDirs) {
                    outputDir = new File(outputDir, f.getName());
                    if (!outputDir.exists()) {
                        outputDir.mkdir();
                    }
                }
                doDirectory(f, outputDir, useStandardSchemas, standardCombinedFormat);
                outputDir = savedDir;
            } else if (f.isFile()) {
                if (".DS_Store".equals(f.getName())) {
                    // Mac hidden directory services file, ignore
                    logger.debug("Skipping .DS_Store");
                    continue;
                }
                FitsOutput result = doSingleFile(f);
                String outputFile = outputDir.getPath() + File.separator + f.getName() + "." + FITS_CONFIG_FILE_NAME;
                File output = new File(outputFile);
                if (output.exists()) {
                    int cnt = 1;
                    while (true) {
                        outputFile = outputDir.getPath() + File.separator + f.getName() + "-" + cnt + "."
                                + FITS_CONFIG_FILE_NAME;
                        output = new File(outputFile);
                        if (!output.exists()) {
                            break;
                        }
                        cnt++;
                    }
                }
                outputResults(result, outputFile, useStandardSchemas, standardCombinedFormat, true);
            }
        }
    }

    /**
     * processes a single file and outputs to the provided output location.
     * Outputs to standard out if outputLocation is null
     *
     * @param inputFile
     * @param outputLocation
     * @param useStandardSchemas
     *          - use standard schemas if available for output type
     * @throws FitsException
     * @throws XMLStreamException
     * @throws IOException
     */
    private FitsOutput doSingleFile(File inputFile) throws FitsException, XMLStreamException, IOException {

        logger.debug("Processing file " + inputFile.getAbsolutePath());
        FitsOutput result = this.examine(inputFile);
        return result;
    }

    private void outputResults(
            FitsOutput result,
            String outputLocation,
            boolean standardSchema,
            boolean standardCombinedFormat,
            boolean dirMode)
            throws XMLStreamException, IOException, FitsException {
        OutputStream out = null;
        logger.debug("Outputting results");
        try {
            // figure out the output location
            if (outputLocation != null) {
                out = new FileOutputStream(outputLocation);
            } else if (!dirMode) {
                out = System.out;
            } else {
                throw new FitsException("The output location must be provided when running FITS in directory mode");
            }

            // if -x is set, then convert to standard metadata schema and output to -o
            if (standardSchema) {
                outputStandardSchemaXml(result, out);
            }
            // if we are using -xc output FITS xml and standard format
            else if (standardCombinedFormat) {
                outputStandardCombinedFormat(result, out);
            }
            // else output FITS XML to -o
            else {
                Document doc = result.getFitsXml();
                XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
                serializer.output(doc, out);
            }

        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void outputStandardCombinedFormat(FitsOutput result, OutputStream out)
            throws XMLStreamException, IOException, FitsException {
        // add the normal fits xml output
        result.addStandardCombinedFormat();

        // output the merged JDOM Document
        XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
        serializer.output(result.getFitsXml(), out);
    }

    public static void outputStandardSchemaXml(FitsOutput fitsOutput, OutputStream out)
            throws XMLStreamException, IOException, FitsException {
        XmlContent xml = fitsOutput.getStandardXmlContent();
        FitsMetadataElement fileNameElement = fitsOutput.getFileInfoElement("filename");
        String inputFilename = fileNameElement == null ? null : fileNameElement.getValue();

        // create an xml output factory
        Transformer transformer = null;

        // initialize transformer for pretty print xslt
        TransformerFactory tFactory = null;
        String factoryImpl = "net.sf.saxon.TransformerFactoryImpl";
        try {
            Class<?> clazz = Class.forName(factoryImpl);
            tFactory = (TransformerFactory) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
            throw new FitsToolException("Could not access or instantiate class: " + factoryImpl, e);
        }

        String prettyPrintXslt = FITS_XML_DIR + "prettyprint.xslt";
        try {
            Templates template = tFactory.newTemplates(new StreamSource(prettyPrintXslt));
            transformer = template.newTransformer();
        } catch (Exception e) {
            transformer = null;
        }

        if (xml != null && transformer != null) {

            xml.setRoot(true);
            ByteArrayOutputStream xmlOutStream = new ByteArrayOutputStream();
            OutputStream xsltOutStream = new ByteArrayOutputStream();

            try {
                // send standard xml to the output stream
                XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(xmlOutStream);
                xml.output(sw);

                // convert output stream to byte array and read back in as inputstream
                Source source = new StreamSource(new ByteArrayInputStream(xmlOutStream.toByteArray()));
                Result rstream = new StreamResult(xsltOutStream);

                // apply the xslt
                transformer.transform(source, rstream);

                // send to the providedOutpuStream
                out.write(xsltOutStream.toString().getBytes("UTF-8"));
                out.flush();

            } catch (Exception e) {
                System.err.println(
                        "error converting output to a standard schema format for input file: [" + inputFilename + "]");
            } finally {
                xmlOutStream.close();
                xsltOutStream.close();
            }

        } else {
            System.err.println("Error: output cannot be converted to a standard schema format for input file: ["
                    + inputFilename + "]");
        }
    }

    private static void printHelp(Options opts) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("fits", null, opts, null);
    }

    public FitsOutput examine(File input) throws FitsException {
        long t1 = System.currentTimeMillis();
        if (!input.exists()) {
            throw new FitsConfigurationException(input.getAbsolutePath() + " does not exist or is not readable");
        }

        List<ToolOutput> toolResults = new ArrayList<ToolOutput>();

        // run file through each tool, catching exceptions thrown by tools
        List<Throwable> caughtThrowables = new ArrayList<Throwable>();
        String path = input.getPath().toLowerCase();
        String ext = path.substring(path.lastIndexOf(".") + 1);

        ArrayList<Thread> threads = new ArrayList<Thread>();
        // GDM 16-Nov-12: Implement limit on maximum threads
        for (Tool t : toolbelt.getTools()) {
            if (t.isEnabled()) {

                // figure out of the tool should be run against the file depending on
                // the include and exclude extension lists
                RunStatus runStatus = RunStatus.SHOULDNOTRUN;
                // if the tool has an include-exts list and it has the extension in it,
                // then run
                if (t.hasIncludedExtensions()) {
                    if (t.hasIncludedExtension(ext)) {
                        runStatus = RunStatus.SHOULDRUN;
                    }
                }
                // if the tool has an exclude-exts list and it does NOT have the
                // extension in it, then run
                else if (t.hasExcludedExtensions()) {
                    if (!t.hasExcludedExtension(ext)) {
                        runStatus = RunStatus.SHOULDRUN;
                    }
                }
                // if the tool does not have an include-exts or exclude-exts list then
                // run
                else if (!t.hasIncludedExtensions() && !t.hasExcludedExtensions()) {
                    runStatus = RunStatus.SHOULDRUN;
                }

                t.setRunStatus(runStatus);

                if (runStatus == RunStatus.SHOULDRUN) {
                    // Don't exceed the maximum thread count
                    while (countActiveTools(threads) >= maxThreads) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                    }
                    // spin up new threads
                    t.setInputFile(input);
                    // GDM 16-Nov-12: Name the threads as a debugging aid
                    Thread thread = new Thread(t, t.getToolInfo().getName());
                    threads.add(thread);
                    logger.debug("Starting thread " + thread.getName());
                    thread.start();
                }
            }
        }

        // wait for them all to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("Caught exception while waiting for tools to finish running: " + e.getMessage(), e);
            }
        }

        // get all output from the tools
        for (Tool t : toolbelt.getTools()) {
            toolResults.add(t.getOutput());
            if (t.getCaughtThrowable() != null) {
                caughtThrowables.add(t.getCaughtThrowable());
            }
        }

        // consolidate the results into a single DOM
        FitsOutput result = consolidator.processResults(toolResults);
        result.setCaughtThrowables(caughtThrowables);

        long t2 = System.currentTimeMillis();
        if (enableStatistics) {
            result.createStatistics(toolbelt, t2 - t1);
        }

        if (resetToolOutput) {
            for (Tool t : toolbelt.getTools()) {
                t.resetOutput();
            }
        }

        if (result.getCaughtThrowables().size() > 0) {
            for (Throwable e : result.getCaughtThrowables()) {
                logger.error("Tool error processing file: " + input.getName() + "\n" + e.getMessage(), e);
            }
        }

        return result;
    }

    /**
     * Default is that the output of each tool is reset after gathering the results
     * in the examine() method. This method should only be used for testing purposes.
     *
     * @param resetToolOutput <code>false</code> will change default functionality of
     * 		resetting each tool's output after running examine().
     * @see edu.harvard.hul.ois.fits.tools.Tool#resetOutput()
     */
    protected void resetToolOutputAfterExaminingInput(boolean resetToolOutput) {
        this.resetToolOutput = resetToolOutput;
    }

    public ToolBelt getToolbelt() {
        return toolbelt;
    }

    public XMLConfiguration getConfig() {
        return config;
    }

    public FitsXmlMapper getFitsXmlMapper() {
        return mapper;
    }

    public String getExternalOutputSchema() {
        return externalOutputSchema;
    }

    public String getInternalOutputSchema() {
        return internalOutputSchema;
    }

    public boolean validateToolOutput() {
        return validateToolOutput;
    }

    /**
     * @return true if tool output should be consolidated to only include output from tools in the first identity block
     */
    public boolean isConsolidateFirstIdentity() {
        return consolidateFirstIdentity;
    }

    public boolean isRawToolOutput() {
        return rawToolOutput;
    }

    /* Count up all the threads that are still running */
    private int countActiveTools(List<Thread> threads) {
        int count = 0;
        for (Thread t : threads) {
            if (t.isAlive()) {
                ++count;
            }
        }
        return count;
    }
}

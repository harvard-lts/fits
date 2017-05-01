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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.hul.ois.fits.consolidation.ToolOutputConsolidator;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.mapping.FitsXmlMapper;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.Tool.RunStatus;
import edu.harvard.hul.ois.fits.tools.ToolBelt;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

/**
 * The main class for FITS.
 */
public class Fits {

  public static volatile String FITS_HOME;
  public static String FITS_XML_DIR;
  public static String FITS_TOOLS_DIR;
  public static String VERSION = "<unknown>";
  public static final String XML_NAMESPACE = "http://hul.harvard.edu/ois/xml/ns/fits/fits_output";
  
  private static boolean traverseDirs;
  private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

  private static final String FITS_CONFIG_FILE_NAME = "fits.xml";
  private static final String VERSION_PROPERTIES_FILE = "version.properties";
  
  private XMLConfiguration config;
  private FitsXmlMapper mapper;
  private boolean enableStatistics;
  private String externalOutputSchema;
  private String internalOutputSchema;
  private boolean validateToolOutput;
  private int maxThreads = 20;
  private ToolOutputConsolidator consolidator;
  private ToolBelt toolbelt;
  private boolean resetToolOutput = true; // should always be true except for unit tests
  
  private static Logger logger;

    static {
        // set FITS_HOME from environment variable if it exists
        FITS_HOME = System.getenv( "FITS_HOME" );
        if ( StringUtils.isEmpty(FITS_HOME) ) {
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
    this( null );
  }

  /**
   * Constructor with path to alternate FITS_HOME.
   *
   * @param fits_home Full path to home directory of FITS installation.
   *        NOTE: If FITS_HOME set as environment variable this argument has no effect.
   * @throws FitsConfigurationException If there is a problem configuring FITS.
   */
  public Fits( String fits_home ) throws FitsConfigurationException {
	  this( fits_home, null );
  }

  /**
   * Constructor with alternate FITS_HOME and alternate FITS configuration file.
   *
   * @param fits_home Full path to home directory of FITS installation.
   *        NOTE: If FITS_HOME set as environment variable this argument has no effect.
   * @param fitsXmlConfig File containing FITS configuration.
   * @throws FitsConfigurationException If there is a problem configuring FITS.
   */
  public Fits( String fits_home, File fitsXmlConfig ) throws FitsConfigurationException {

	  // NOTE: a "FITS_HOME" environment variable (see initial static block of this class)
	  // "wins" over a FITS home value passed into the constructor.
    if ( StringUtils.isEmpty(FITS_HOME) ) {
      // if env variable not set check for fits_home passed into constructor
      if (fits_home != null) {
        FITS_HOME = fits_home;
      }
    }

    setFitsVersionFromFile();

    FITS_XML_DIR = FITS_HOME + "xml" + File.separator;
    FITS_TOOLS_DIR = FITS_HOME + "tools" + File.separator;

    // Set up logging explicitly so one of the tools aggregated into FITS does not circumvent this.
    // This process also makes initialization a more flexible by allowing a path to a file without a scheme.
    // Log4j seems to want a valid URI with a scheme value for proper initialization.
    // If the property is just a path then convert it to a URI with a scheme and
    // set it back into the system property.
    //
    // First look for a system property (the Log4j preferred way of configuration) at the Log4j expected value, "log4j.configuration".
    // This value can be either a file path, file protocol (e.g. - file:/path/to/log4j.properties), or a URL (http://some/server/log4j.properties).
    // If this value either is does not exist or is not valid, the default file that comes with FITS will be used for initialization.
    String log4jSystemProp = System.getProperty("log4j.configuration");
    URI log4jUri = null;
    if (log4jSystemProp != null) {
        try {
            log4jUri = new URI(log4jSystemProp);
            // log4j system needs a scheme in the URI so convert to file if necessary.
            if (null == log4jUri.getScheme()) {
                File log4jProperties = new File(log4jSystemProp);
                if (log4jProperties.exists() && log4jProperties.isFile()) {
                    log4jUri = log4jProperties.toURI();
                } else {
                    // No scheme and not a file - yikes!!! Let's bail and use fall-back file.
                    log4jUri = null;
                    throw new URISyntaxException(log4jSystemProp, "Not a valid file");
                }
            }
        } catch (URISyntaxException e) {
            // fall back to FITS-supplied file
            System.err.println("Unable to load log4j.properties file: " + log4jSystemProp + " -- reason: " + e.getReason());
            System.err.println("Falling back to default log4j.properties file: " + FITS_HOME + "log4j.properties");
        }
    }
    // Only set up logging with FITS default logging configuration if
    // either the System property is null or exception was thrown creating URI.
    if (log4jUri == null) {
        File log4jProperties = new File(FITS_HOME + "log4j.properties");
        log4jUri = log4jProperties.toURI();
    }

    // Even if set, reset logging System property to ensure it's in a URI format
    // with scheme so the log4j framework can initialize.
    System.setProperty( "log4j.configuration", log4jUri.toString());

    logger = Logger.getLogger( this.getClass() );
    logger.info("Logging initialized with: " + log4jUri.toString());
    try {
      if ( fitsXmlConfig != null ) {
          config = new XMLConfiguration( fitsXmlConfig );
      } else {
          config = new XMLConfiguration( FITS_XML_DIR + FITS_CONFIG_FILE_NAME );
      }
    } catch (ConfigurationException e) {
      logger.fatal( "Error reading " + FITS_XML_DIR + FITS_CONFIG_FILE_NAME + ": " + e.getClass().getName() );
      throw new FitsConfigurationException( "Error reading " + FITS_XML_DIR + FITS_CONFIG_FILE_NAME, e );
    }
    try {
      mapper = new FitsXmlMapper();
    } catch (Exception e) {
      logger.fatal( "Error creating FITS XML Mapper: " + e.getClass().getName() );
      throw new FitsConfigurationException( "Error creating FITS XML Mapper", e );
    }
    // required config values
    try {
      validateToolOutput = config.getBoolean( "output.validate-tool-output" );
      externalOutputSchema = config.getString( "output.external-output-schema" );
      internalOutputSchema = config.getString( "output.internal-output-schema" );
      enableStatistics = config.getBoolean( "output.enable-statistics" );
    } catch (NoSuchElementException e) {
      logger.fatal( "Error in configuration file: " + e.getClass().getName() );
      System.out.println( "Error inconfiguration file: " + e.getMessage() );
      return;
    }

    // optional config values GDM 16-Nov-2012
    try {
      maxThreads = config.getShort( "process.max-threads" );
    } catch (NoSuchElementException e) {
    }
    if (maxThreads < 1) {
      // If invalid number specified, use a default.
      maxThreads = 20;
    }
    logger.debug( "Maximum threads = " + maxThreads );

    String consolidatorClassFullyQualifiedName = config.getString( "output.dataConsolidator[@class]" );
    try {
		// Instantiate the Consolidator class using Reflection by passing Fits into the constructor.
		Class<?> c = Class.forName(consolidatorClassFullyQualifiedName);
		Constructor<?> ctor = c.getConstructor(Fits.class);
		consolidator = (ToolOutputConsolidator)ctor.newInstance(this);
    } catch (Exception e) {
      throw new FitsConfigurationException( "Error initializing " + consolidatorClassFullyQualifiedName, e );
    }

    toolbelt = new ToolBelt( config, this );

  }

  public static void main( String[] args ) throws FitsException, IOException, ParseException, XMLStreamException {

    setFitsVersionFromFile();

    Options options = new Options();
    options.addOption( "i", true, "input file or directory" );
    options.addOption( "r", false, "process directories recursively when -i is a directory " );
    options.addOption( "o", true, "output file or directory if -i is a directory" );
    options.addOption( "h", false, "print this message" );
    options.addOption( "v", false, "print version information" );
    OptionGroup outputOptions = new OptionGroup();
    Option stdxml = new Option( "x", false, "convert FITS output to a standard metadata schema" );
    Option combinedStd = new Option( "xc", false, "output using a standard metadata schema and include FITS xml" );
    outputOptions.addOption( stdxml );
    outputOptions.addOption( combinedStd );
    options.addOptionGroup( outputOptions );

    CommandLineParser parser = new GnuParser();
    CommandLine cmd = parser.parse( options, args );

    if (cmd.hasOption( "h" )) {
      printHelp( options );
      System.exit( 0 );
    }
    if (cmd.hasOption( "v" )) {
      System.out.println( Fits.VERSION );
      System.exit( 0 );
    }
    if (cmd.hasOption( "r" )) {
      traverseDirs = true;
    } else {
      traverseDirs = false;
    }

    if (cmd.hasOption( "i" )) {
      String input = cmd.getOptionValue( "i" );
      File inputFile = new File( input );

      if (inputFile.isDirectory()) {
        String outputDir = cmd.getOptionValue( "o" );
        if (outputDir == null || !(new File( outputDir ).isDirectory())) {
          throw new FitsException(
              "When FITS is run in directory processing mode the output location must be a directory" );
        }
        Fits fits = new Fits();
        fits.doDirectory( inputFile, new File( outputDir ), cmd.hasOption( "x" ), cmd.hasOption( "xc" ) );
      } else {
        Fits fits = new Fits();
        FitsOutput result = fits.doSingleFile( inputFile );
        fits.outputResults( result, cmd.getOptionValue( "o" ), cmd.hasOption( "x" ), cmd.hasOption( "xc" ), false );
      }
    } else {
      System.err.println( "Invalid CLI options" );
      printHelp( options );
      System.exit( -1 );
    }

    System.exit( 0 );
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
      if (FITS_HOME.length() > 0 && !FITS_HOME.endsWith( File.separator )) {
          FITS_HOME = FITS_HOME + File.separator;
      }

      // get version from properties file and set in class
      String versionPropFileFullPath = "";
      if ( !StringUtils.isEmpty(FITS_HOME) ) {
          versionPropFileFullPath = FITS_HOME;
      }

      File versionFile = new File( versionPropFileFullPath + VERSION_PROPERTIES_FILE );
      Properties versionProps = new Properties();
      try {
          versionProps.load(new FileInputStream(versionFile));
          String version = versionProps.getProperty("build.version");
          if (version != null && !version.isEmpty()) {
              Fits.VERSION = version;
          }
      } catch (IOException e) {
          System.err.println("Problem loading [" + VERSION_PROPERTIES_FILE + "]: " + "Cannot display FITS version information.");
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
	private void doDirectory(File inputDir, File outputDir,boolean useStandardSchemas, boolean standardCombinedFormat) throws FitsException, XMLStreamException, IOException {
		if(inputDir.listFiles() == null) {
			return;
		}

		logger.info("Processing directory " + inputDir.getAbsolutePath());

		for (File f : inputDir.listFiles()) {

			if(f == null || !f.exists() || !f.canRead()) {
				continue;
			}

			logger.info("processing " + f.getPath());

			if (f.isDirectory() && traverseDirs) {
				doDirectory(f, outputDir, useStandardSchemas, standardCombinedFormat);
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
						outputFile = outputDir.getPath() + File.separator + f.getName() + "-" + cnt + "." + FITS_CONFIG_FILE_NAME;
						output = new File(outputFile);
						if (!output.exists()) {
							break;
						}
						cnt++;
					}
				}
				outputResults(result, outputFile, useStandardSchemas,
						standardCombinedFormat, true);
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
  private FitsOutput doSingleFile( File inputFile ) throws FitsException, XMLStreamException, IOException {

    logger.debug( "Processing file " + inputFile.getAbsolutePath() );
    FitsOutput result = this.examine( inputFile );
    if (result.getCaughtExceptions().size() > 0) {
      for (Exception e : result.getCaughtExceptions()) {
        logger.error( "Warning: " + e.getMessage(), e );
      }
    }
    return result;
  }

  private void outputResults( FitsOutput result, String outputLocation, boolean standardSchema,
      boolean standardCombinedFormat, boolean dirMode ) throws XMLStreamException, IOException, FitsException {
    OutputStream out = null;
    logger.debug( "Outputting results" );
    try {
      // figure out the output location
      if (outputLocation != null) {
        out = new FileOutputStream( outputLocation );
      } else if (!dirMode) {
        out = System.out;
      } else {
        throw new FitsException( "The output location must be provided when running FITS in directory mode" );
      }

      // if -x is set, then convert to standard metadata schema and output to -o
      if (standardSchema) {
        outputStandardSchemaXml( result, out );
      }
      // if we are using -xc output FITS xml and standard format
      else if (standardCombinedFormat) {
        outputStandardCombinedFormat( result, out );
      }
      // else output FITS XML to -o
      else {
        Document doc = result.getFitsXml();
        XMLOutputter serializer = new XMLOutputter( Format.getPrettyFormat() );
        serializer.output( doc, out );
      }

    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

  public static void outputStandardCombinedFormat( FitsOutput result, OutputStream out ) throws XMLStreamException,
      IOException, FitsException {
    // add the normal fits xml output
    result.addStandardCombinedFormat();

    // output the merged JDOM Document
    XMLOutputter serializer = new XMLOutputter( Format.getPrettyFormat() );
    serializer.output( result.getFitsXml(), out );

  }

  public static void outputStandardSchemaXml( FitsOutput fitsOutput, OutputStream out ) throws XMLStreamException,
      IOException {
    XmlContent xml = fitsOutput.getStandardXmlContent();
	FitsMetadataElement fileNameElement = fitsOutput.getMetadataElement("filename");
	String inputFilename = fileNameElement == null ? null : fileNameElement.getValue();

    // create an xml output factory
    Transformer transformer = null;

    // initialize transformer for pretty print xslt
    TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
    String prettyPrintXslt = FITS_XML_DIR + "prettyprint.xslt";
    try {
      Templates template = tFactory.newTemplates( new StreamSource( prettyPrintXslt ) );
      transformer = template.newTransformer();
    } catch (Exception e) {
      transformer = null;
    }

    if (xml != null && transformer != null) {

      xml.setRoot( true );
      ByteArrayOutputStream xmlOutStream = new ByteArrayOutputStream();
      OutputStream xsltOutStream = new ByteArrayOutputStream();

      try {
        // send standard xml to the output stream
        XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter( xmlOutStream );
        xml.output( sw );

        // convert output stream to byte array and read back in as inputstream
        Source source = new StreamSource( new ByteArrayInputStream( xmlOutStream.toByteArray() ) );
        Result rstream = new StreamResult( xsltOutStream );

        // apply the xslt
        transformer.transform( source, rstream );

        // send to the providedOutpuStream
        out.write( xsltOutStream.toString().getBytes( "UTF-8" ) );
        out.flush();

      } catch (Exception e) {
        System.err.println( "error converting output to a standard schema format for input file: [" + inputFilename + "]");
      } finally {
        xmlOutStream.close();
        xsltOutStream.close();
      }

    } else {
      System.err.println( "Error: output cannot be converted to a standard schema format for input file: [" + inputFilename + "]");
    }
  }

  private static void printHelp( Options opts ) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "fits", opts );
  }

  public FitsOutput examine( File input ) throws FitsException {
    long t1 = System.currentTimeMillis();
    if (!input.exists()) {
      throw new FitsConfigurationException( input + " does not exist or is not readable" );
    }

    List<ToolOutput> toolResults = new ArrayList<ToolOutput>();

    // run file through each tool, catching exceptions thrown by tools
    List<Exception> caughtExceptions = new ArrayList<Exception>();
    String path = input.getPath().toLowerCase();
    String ext = path.substring( path.lastIndexOf( "." ) + 1 );

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
          if (t.hasIncludedExtension( ext )) {
            runStatus = RunStatus.SHOULDRUN;
          }
        }
        // if the tool has an exclude-exts list and it does NOT have the
        // extension in it, then run
        else if (t.hasExcludedExtensions()) {
          if (!t.hasExcludedExtension( ext )) {
            runStatus = RunStatus.SHOULDRUN;
          }
        }
        // if the tool does not have an include-exts or exclude-exts list then
        // run
        else if (!t.hasIncludedExtensions() && !t.hasExcludedExtensions()) {
          runStatus = RunStatus.SHOULDRUN;
        }

        t.setRunStatus( runStatus );

        if (runStatus == RunStatus.SHOULDRUN) {
          // Don't exceed the maximum thread count
          while (countActiveTools( threads ) >= maxThreads) {
            try {
              Thread.sleep( 200 );
            } catch (InterruptedException e) {
            }
          }
          // spin up new threads
          t.setInputFile( input );
          // GDM 16-Nov-12: Name the threads as a debugging aid
          Thread thread = new Thread( t, t.getToolInfo().getName() );
          threads.add( thread );
          logger.debug( "Starting thread " + thread.getName() );
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
      toolResults.add( t.getOutput() );
    }

    // consolidate the results into a single DOM
    FitsOutput result = consolidator.processResults( toolResults );
    result.setCaughtExceptions( caughtExceptions );

    long t2 = System.currentTimeMillis();
    if (enableStatistics) {
      result.createStatistics( toolbelt, ext, t2 - t1 );
    }

    if (resetToolOutput) {
    	for (Tool t : toolbelt.getTools()) {
    		t.resetOutput();
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
  
  /* Count up all the threads that are still running */
  private int countActiveTools( List<Thread> threads ) {
    int count = 0;
    for (Thread t : threads) {
      if (t.isAlive()) {
        ++count;
      }
    }
    return count;
  }
}

/* 
 * Copyright 2009 Harvard University Library
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
package edu.harvard.hul.ois.fits;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

  private static Logger logger;

  public static volatile String FITS_HOME;
  public static String FITS_XML;
  public static String FITS_TOOLS;
  public static XMLConfiguration config;
  public static FitsXmlMapper mapper;
  public static boolean validateToolOutput;
  public static boolean enableStatistics;
  public static String externalOutputSchema;
  public static String internalOutputSchema;
  public static int maxThreads = 20; // GDM 16-Nov-2012
  public static final String XML_NAMESPACE = "http://hul.harvard.edu/ois/xml/ns/fits/fits_output";

  public static String VERSION = "0.8.4";

  private ToolOutputConsolidator consolidator;
  private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
  private ToolBelt toolbelt;

  private static boolean traverseDirs;

  public Fits() throws FitsException {
    this( null );
  }

  public Fits( String fits_home ) throws FitsConfigurationException {

    // Set BB_HOME dir with environment variable
    FITS_HOME = System.getenv( "FITS_HOME" );
    if (FITS_HOME == null) {
      // if env variable not set check for fits_home passed into constructor
      if (fits_home != null) {
        FITS_HOME = fits_home;
      } else {
        // if fits_home is still not set use the current directory
        FITS_HOME = "";
      }
    }

    // If fits home is not an empty string and doesn't send with a file
    // separator character, add one
    if (FITS_HOME.length() > 0 && !FITS_HOME.endsWith( File.separator )) {
      FITS_HOME = FITS_HOME + File.separator;
    }

    FITS_XML = FITS_HOME + "xml" + File.separator;
    FITS_TOOLS = FITS_HOME + "tools" + File.separator;

    // Set up logging.
    // Now using an explicit properties file, because otherwoise DROID will
    // hijack it, and it's cleaner this way anyway.
    
    //(SM 1/2/14 -- Note that this statement probably isn't doing what the author intended.
    //  If log4j.debug=true is set then it shows that this doesn't actually find the specified
    //  log4j.properties file.  Leaving as is for now since overall logging works as intended.
    //  also note that any logging statements in this class probably do not work.
    System.setProperty( "log4j.configuration", FITS_TOOLS + "log4j.properties" );

    logger = Logger.getLogger( this.getClass() );
    try {
      config = new XMLConfiguration( FITS_XML + "fits.xml" );
    } catch (ConfigurationException e) {
      logger.fatal( "Error reading " + FITS_XML + "fits.xml: " + e.getClass().getName() );
      throw new FitsConfigurationException( "Error reading " + FITS_XML + "fits.xml", e );
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

    String consolidatorClass = config.getString( "output.dataConsolidator[@class]" );
    try {
      Class<?> c = Class.forName( consolidatorClass );
      consolidator = (ToolOutputConsolidator) c.newInstance();
    } catch (Exception e) {
      throw new FitsConfigurationException( "Error initializing " + consolidatorClass, e );
    }

    toolbelt = new ToolBelt( FITS_XML + "fits.xml" );

  }

  public static void main( String[] args ) throws FitsException, IOException, ParseException, XMLStreamException {    

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
				String outputFile = outputDir.getPath() + File.separator + f.getName() + ".fits.xml";
				File output = new File(outputFile);
				if (output.exists()) {
					int cnt = 1;
					while (true) {
						outputFile = outputDir.getPath() + File.separator + f.getName() + "-" + cnt + ".fits.xml";
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
        System.err.println( "Warning: " + e.getMessage() );
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

    // create an xml output factory
    Transformer transformer = null;

    // initialize transformer for pretty print xslt
    TransformerFactory tFactory = TransformerFactory.newInstance();
    String prettyPrintXslt = FITS_XML + "prettyprint.xslt";
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
        System.err.println( "error converting output to a standard schema format" );
      } finally {
        xmlOutStream.close();
        xsltOutStream.close();
      }

    } else {
      System.err.println( "Error: output cannot be converted to a standard schema format for this file" );
    }
  }

  private static void printHelp( Options opts ) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "fits", opts );
  }

  /*
   * ORIGINAL EXAMINE METHOD WITHOUT THREADS
   * 
   * public FitsOutput examineOriginal(File input) throws FitsException {
   * if(!input.exists()) { throw new
   * FitsConfigurationException(input+" does not exist or is not readable"); }
   * 
   * List<ToolOutput> toolResults = new ArrayList<ToolOutput>();
   * 
   * //run file through each tool, catching exceptions thrown by tools
   * List<Exception> caughtExceptions = new ArrayList<Exception>(); String path
   * = input.getPath().toLowerCase(); String ext =
   * path.substring(path.lastIndexOf(".")+1); for(Tool t : toolbelt.getTools())
   * { if(t.isEnabled()) { if(!t.hasExcludedExtension(ext)) { try { ToolOutput
   * tOutput = t.extractInfo(input); toolResults.add(tOutput); } catch(Exception
   * e) { caughtExceptions.add(e); } } } }
   * 
   * 
   * // consolidate the results into a single DOM FitsOutput result =
   * consolidator.processResults(toolResults);
   * result.setCaughtExceptions(caughtExceptions);
   * 
   * for(Tool t: toolbelt.getTools()) { t.resetOutput(); }
   * 
   * return result; }
   */

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
        e.printStackTrace();
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

    for (Tool t : toolbelt.getTools()) {
      t.resetOutput();
    }

    return result;
  }

  public ToolBelt getToolbelt() {
    return toolbelt;
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

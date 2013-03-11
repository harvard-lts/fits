package uk.ac.ox.bodleian.beam.tika;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;

import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.Gson;

/**
 * TODO: Better JavaDoc for TikaWrapper.
 * <p/>
 * TODO: Tests for TikaWrapper.
 * <p/>
 * 
 * Class to wrap some of the dirty work associated with setting up Tika. Wraps a
 * static Tika instance set up with a default org.apache.tika.detect.Detector
 * and an org.apache.tika.parser.AutoDetectParser instance. The class is a
 * singleton, managed by a private default constructor and a static
 * getInstance() method. This class was adapted from a similar wrapper created
 * by Peter May at the SPRUCE event in Glasgow, April 2012.
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @author Peter May (The British Library)
 * @version 0.1 Created 5 Jul 2012:14:08:02
 */
public class TikaWrapper {
	private static final String DEFAULT_ENCODING = "UTF8";
	private static final String NULL_ARG_MSG = " argument is null.";
	private static final String NOT_FOUND_MSG = " file not found.";
	private static final String IS_DIR_MSG = " is a directory.";

	// Static parser context reference
	static ParseContext CONTEXT = new ParseContext();
	// Static detector reference
	private static Detector DETECTOR = new DefaultDetector();
	// Static parser reference
	static Parser PARSER = new AutoDetectParser(DETECTOR);
	// Static Tika reference
	private static Tika TIKA = new Tika(DETECTOR, PARSER);
	// Set the context up
	static {
		CONTEXT.set(Parser.class, PARSER);
	}
	// Singleton Tika instance
	private static TikaWrapper INSTANCE = new TikaWrapper();

	/* Private constructor to enable singleton creation */
	private TikaWrapper() {
	}

	/**
	 * Returns a singleton instance of the TikaWrapper class
	 * 
	 * @return the singleton TikaWrapper instance
	 */
	public static synchronized TikaWrapper getTika() {
		return INSTANCE;
	}

	/**
	 * @return the character encoding, currently the default UTF-8 encoding
	 */
	public static String getEncoding() {
		return DEFAULT_ENCODING;
	}

	/**
	 * Returns a MediaType object representing the mime-type of the specified
	 * java.io.File. This doesn't perform a full Tika parse, just content type
	 * detection.
	 * 
	 * @param file
	 *            the file to find the mime-type information of
	 * @return MediaType an object representing the mime-type information of the
	 *         specified file
	 * @throws IOException
	 */
	public MediaType getMediaType(File file) throws IOException {
		// Defensive file check
		URL fileURL = getFileURL(file);
		TikaInputStream tikaStream = TikaInputStream.get(fileURL);
		MediaType mt = getMediaTypeFromTikaStream(tikaStream); 
		try {
			tikaStream.close();
		} catch (IOException excep) {
			// Do nothing
		}
		return mt;
	}

	/**
	 * Method to get the org.apache.tika.mime.MediaType for a
	 * java.io.InputStream. This doesn't perform a full Tika parse, just content
	 * type detection.
	 * 
	 * @param stream
	 *            an input stream to the data for which the media type is
	 *            required.
	 * @return the org.apache.tika.mime.MediaType detected from the stream
	 * @throws IOException
	 *             when there'san exception reading from the stream
	 */
	public MediaType getMediaType(InputStream stream) throws IOException {
		// Defensive argument check
		if (stream == null)
			throw new IllegalArgumentException("stream " + NULL_ARG_MSG);
		TikaInputStream tikaStream = TikaInputStream.get(stream);
		return getMediaTypeFromTikaStream(tikaStream);
	}

	/**
	 * Method to perform a full Tika parse on an input stream, populating the
	 * passed Metadata object, and returning a Reader to the full text content.
	 * 
	 * @param stream
	 *            an input stream to Tika parse
	 * @param metadata
	 *            a metadata object for the Tika metadata
	 * @return a java.io.Reader for the text content
	 * @throws IOException
	 *             if there's an error reading the stream
	 * @throws TikaException
	 *             if there's a Tika error parsing the stream data
	 */
	public Reader parse(InputStream stream, Metadata metadata)
			throws IOException, TikaException {
		// Defensive arg checks
		if (stream == null)
			throw new IllegalArgumentException("stream " + NULL_ARG_MSG);
		if (metadata == null)
			throw new IllegalArgumentException("metadata " + NULL_ARG_MSG);
		return TIKA.parse(stream, metadata);
	}

	/**
	 * Parses the specified file using Tika and returns the populated Tika
	 * Metadata object.
	 * 
	 * @param file
	 *            the file to parse
	 * @param output
	 *            a stream to output the metadata to
	 * @return String a String containing metadata information
	 * @throws IOException
	 *             when there's an issue reading the file
	 * @throws TikaException
	 *             when there's a problem tika parsing the file
	 */
	public Metadata parse(File file, OutputStream output) throws IOException,
			TikaException {
		// Get URL from the file for tika stream, built in defensive checks
		URL fileUrl = getFileURL(file);
		if (output == null)
			throw new IllegalArgumentException("output " + NULL_ARG_MSG);
		// New Tika metadata object for population
		Metadata metadata = new Metadata();
		TikaInputStream tis = null;
		tis = TikaInputStream.get(fileUrl, metadata);
		this.JSON.process(tis, output, metadata, DEFAULT_ENCODING);
		tis.close();
		output.flush();
		output.close();
		return metadata;
	}

	/**
	 * @param stream
	 *            an input stream to parse
	 * @param output
	 *            an output stream for metadata
	 * @return the metadata parsed
	 * @throws TikaException
	 */
	public Metadata parse(InputStream stream, OutputStream output)
			throws TikaException {
		if (stream == null)
			throw new IllegalArgumentException("stream " + NULL_ARG_MSG);
		if (output == null)
			throw new IllegalArgumentException("output " + NULL_ARG_MSG);
		Metadata metadata = new Metadata();
		TikaInputStream tis = TikaInputStream.get(stream);
		this.JSON.process(tis, output, metadata, DEFAULT_ENCODING);
		try {
			stream.close();
			output.flush();
		} catch (IOException excep) {
			// Do nothing, it just won't close / flush
		}
		return metadata;
	}

	// Helper to retrieve the MediaType from a TikaInputStream
	private static MediaType getMediaTypeFromTikaStream(TikaInputStream tis)
			throws IOException {
		// Create a metdata object required for detect method
		Metadata metadata = new Metadata();
		// return the detection result
		MediaType mt = ((AutoDetectParser) PARSER).getDetector().detect(tis,
				metadata);
		tis.close();
		return mt;
	}

	// Helper method that tests file argument to make sure it is an existing
	// file
	private static final URL getFileURL(File file) throws FileNotFoundException {
		URL retVal = null;
		// Defensive argument checks, not null, file exists and is not a
		// directory
		if (file == null)
			throw new IllegalArgumentException("file " + NULL_ARG_MSG);
		if (!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath()
					+ NOT_FOUND_MSG);
		if (file.isDirectory())
			throw new IllegalArgumentException(file.getAbsolutePath()
					+ IS_DIR_MSG);
		try {
			retVal = file.toURI().toURL();
		} catch (MalformedURLException excep) {
			throw new IllegalArgumentException("File arg "
					+ file.getAbsolutePath() + " cannot be converted to URL.",
					excep);
		}
		return retVal;
	}

	private class OutputType {

		public void process(InputStream input, OutputStream output,
				Metadata metadata, String encoding) throws TikaException {
			Parser p = PARSER;
			ContentHandler handler = null;
			try {
				handler = getContentHandler(output, metadata, encoding);
			} catch (Exception excep) {
				// TODO Auto-generated catch block
				excep.printStackTrace();
			}
			try {
				p.parse(input, handler, metadata, CONTEXT);
			} catch (IOException excep) {
				// TODO Auto-generated catch block
				excep.printStackTrace();
			} catch (SAXException excep) {
				// TODO Auto-generated catch block
				excep.printStackTrace();
			}
			// fix for TIKA-596: if a parser doesn't generate
			// XHTML output, the lack of an output document prevents
			// metadata from being output: this fixes that
			if (handler instanceof NoDocumentMetHandler) {
				NoDocumentMetHandler metHandler = (NoDocumentMetHandler) handler;
				if (!metHandler.metOutput()) {
					metHandler.endDocument();
				}
			}
		}

		protected ContentHandler getContentHandler(OutputStream output,
				Metadata metadata, String encoding) throws Exception {
			throw new UnsupportedOperationException();
		}

	}

	private final OutputType JSON = new OutputType() {
		@Override
		protected ContentHandler getContentHandler(OutputStream output,
				Metadata metadata, String encoding) throws Exception {
			final PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					output, encoding));
			return new NoDocumentJSONMetHandler(metadata, writer);
		}
	};
	
	private final OutputType XML = new OutputType() {
		@Override
		protected ContentHandler getContentHandler(OutputStream output,
				Metadata metadata, String encoding) throws Exception {
			final PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					output, encoding));
			return new NoDocumentXMLMetHandler(metadata, writer);
		}
	};

	private class NoDocumentMetHandler extends DefaultHandler {

		protected final Metadata metadata;

		protected PrintWriter writer;

		private boolean metOutput;

		public NoDocumentMetHandler(Metadata metadata, PrintWriter writer) {
			this.metadata = metadata;
			this.writer = writer;
			this.metOutput = false;
		}

		@Override
		public void endDocument() {
			String[] names = this.metadata.names();
			Arrays.sort(names);
			outputMetadata(names);
			this.writer.flush();
			this.metOutput = true;
		}

		public void outputMetadata(String[] names) {
			for (String name : names) {
				this.writer.println(name + ": " + this.metadata.get(name));
			}
		}

		public boolean metOutput() {
			return this.metOutput;
		}

	}

	private class NoDocumentJSONMetHandler extends NoDocumentMetHandler {
		private NumberFormat formatter;
		private Gson gson;

		public NoDocumentJSONMetHandler(Metadata metadata, PrintWriter writer) {
			super(metadata, writer);

			this.formatter = NumberFormat.getInstance();
			this.gson = new Gson();
		}

		@Override
		public void outputMetadata(String[] names) {
			this.writer.print("{ ");
			boolean first = true;
			for (String name : names) {
				if (!first) {
					this.writer.println(", ");
				} else {
					first = false;
				}
				this.gson.toJson(name, this.writer);
				this.writer.print(":");
				outputValues(this.metadata.getValues(name));
			}
			this.writer.print(" }");
		}

		public void outputValues(String[] values) {
			if (values.length > 1) {
				this.writer.print("[");
			}
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				if (i > 0) {
					this.writer.print(", ");
				}

				if (value == null || value.length() == 0) {
					this.writer.print("null");
				} else {
					// Is it a number?
					ParsePosition pos = new ParsePosition(0);
					this.formatter.parse(value, pos);
					if (value.length() == pos.getIndex()) {
						// It's a number. Remove leading zeros and output
						value = value.replaceFirst("^0+(\\d)", "$1");
						this.writer.print(value);
					} else {
						// Not a number, escape it
						this.gson.toJson(value, this.writer);
					}
				}
			}
			if (values.length > 1) {
				this.writer.print("]");
			}
		}
	}

	/**
	 * Uses GSON to do the JXML escaping, but does the general JSON glueing
	 * ourselves.
	 */
	private class NoDocumentXMLMetHandler extends NoDocumentMetHandler {
		private NumberFormat formatter;

		public NoDocumentXMLMetHandler(Metadata metadata, PrintWriter writer) {
			super(metadata, writer);

			this.formatter = NumberFormat.getInstance();
		}

		@Override
		public void outputMetadata(String[] names) {
			this.writer.print("<metadata>");
			for (String name : names) {
				this.writer.print("<field name=\"");
				this.writer.print(name);
				this.writer.print("\">");
				outputValues(this.metadata.getValues(name));
				this.writer.print("</field>");
			}
			this.writer.print("</metadata>");
		}

		public void outputValues(String[] values) {
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				this.writer.print("<value>");
				if (value == null || value.length() == 0) {
					value = "null";
				} else {
					// Is it a number?
					ParsePosition pos = new ParsePosition(0);
					this.formatter.parse(value, pos);
					if (value.length() == pos.getIndex()) {
						// It's a number. Remove leading zeros and output
						value = value.replaceFirst("^0+(\\d)", "$1");
					}
				}
				this.writer.print(value + "</value>");
			}
		}
	}
}
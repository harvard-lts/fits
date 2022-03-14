//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.tika;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import edu.harvard.hul.ois.fits.util.DateTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.metadata.OfficeOpenXMLCore;
import org.apache.tika.metadata.OfficeOpenXMLExtended;
import org.apache.tika.metadata.PDF;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMP;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.harvard.hul.ois.fits.DocumentTypes;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataValues;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.XmlUtils;
import org.xml.sax.helpers.DefaultHandler;

public class TikaTool extends ToolBase {

    /** Constants for all Tika property names, so that they're all gathered in
     *  one place and we don't have to fix inline names. */
    private final static String P_AUTHOR = "author";
    private final static String P_BITS = "bits";
    private final static String P_CHANNELS = "channels";
    private final static String P_COMPRESSION_COMPRESSION_TYPE_NAME = "Compression CompressionTypeName";
    private final static String P_COMPRESSION_TYPE = "Compression Type";
    private final static String P_DIMENSION_IMAGE_ORIENTATION ="Dimension ImageOrientation";
    private final static String P_ENCODING = "encoding";
    private final static String P_GENERATOR = "generator";
    private final static String P_HEIGHT = "height";
    private final static String P_IMAGE_HEIGHT = "Image Height";
    private final static String P_IMAGE_WIDTH = "Image Width";
    private final static String P_PDFX_VERSION = "GTS_PDFXVersion";
    private final static String P_RESOLUTION_UNIT = "Resolution Unit";
    private final static String P_SAMPLE_RATE = "samplerate";
    private final static String P_TIFF_IMAGE_LENGTH = "tiff:ImageLength";
    private final static String P_TIFF_IMAGE_WIDTH = "tiff:ImageWidth";
    private final static String P_TIFF_RESOLUTION_UNIT = "tiff:ResolutionUnit";
    private final static String P_TIFF_SAMPLES_PER_PIXEL = "tiff:SamplesPerPixel";
    private final static String P_TIFF_X_RESOLUTION = "tiff:XResolution";
    private final static String P_TIFF_Y_RESOLUTION = "tiff:YResolution";
    private final static String P_WIDTH = "width";
    private final static String P_X_RESOLUTION = "X Resolution";
    private final static String P_Y_RESOLUTION = "Y Resolution";
    private final static String P_XMP_AUDIO_CHANNEL_TYPE = "xmpDM:audioChannelType";
    private final static String P_XMP_AUDIO_COMPRESSOR = "xmpDM:audioCompressor";
    private final static String P_XMP_AUDIO_SAMPLE_RATE = "xmpDM:audioSampleRate";
    private final static String P_XMP_AUDIO_SAMPLE_TYPE = "xmpDM:audioSampleType";
    private final static String P_XMP_PIXEL_ASPECT_RATIO = "xmpDM:videoPixelAspectRatio";
    private final static String P_XMP_VIDEO_COLOR_SPACE = "xmpDM:videoColorSpace";
    private final static String P_XMP_VIDEO_FRAME_RATE = "xmpDM:videoFrameRate";
    private final static String P_XMP_VIDEO_PIXEL_DEPTH = "xmpDM:videoPixelDepth";
    private final static String P_XMP_NPAGES = "xmpTPg:NPages";


    /** Enumeration of Tika properties. */
    private enum TikaProperty {
        AUTHOR,
        BITS,
        //BITS_PER_SAMPLE,
        CATEGORY,
        CHANNELS,
        CHARACTER_COUNT,
        COMPRESSION_COMPRESSION_TYPE_NAME,
        COMPRESSION_TYPE,
        CONTENT_ENCODING,
        DESCRIPTION,
        DIMENSION_IMAGE_ORIENTATION,
        ENCODING,
        IDENTIFIER,
        IMAGE_COUNT,
        IMAGE_HEIGHT,
        IMAGE_WIDTH,
        LANGUAGE,
        LINE_COUNT,
        OBJECT_COUNT,
        PAGE_COUNT,
        PARAGRAPH_COUNT,
        RESOLUTION_UNIT,
        RIGHTS,
        SAMPLE_RATE,
        SECURITY,
        TABLE_COUNT,
        TIFF_SAMPLES_PER_PIXEL,
        TITLE,
        WORD_COUNT,
        X_RESOLUTION,
        Y_RESOLUTION,
        XMP_AUDIO_CHANNEL_TYPE,
        XMP_AUDIO_COMPRESSOR,
        XMP_AUDIO_SAMPLE_TYPE,
        XMP_PIXEL_ASPECT_RATIO,
        XMP_VIDEO_COLOR_SPACE,
        XMP_VIDEO_FRAME_RATE,
        XMP_VIDEO_PIXEL_DEPTH
    }

    /** Map of Tika properties to TikaProperty
     */
    private final static Map<String, TikaProperty> propertyNameMap =
            new HashMap<String, TikaProperty>();
    static {
        propertyNameMap.put (TikaCoreProperties.CREATOR.getName(), TikaProperty.AUTHOR);
        propertyNameMap.put (P_AUTHOR, TikaProperty.AUTHOR);
        propertyNameMap.put (Office.AUTHOR.getName(), TikaProperty.AUTHOR);
        propertyNameMap.put (P_BITS, TikaProperty.BITS);
        propertyNameMap.put (OfficeOpenXMLCore.CATEGORY.getName(), TikaProperty.CATEGORY);
        propertyNameMap.put (P_CHANNELS, TikaProperty.CHANNELS);
        propertyNameMap.put (Office.CHARACTER_COUNT.getName(), TikaProperty.CHARACTER_COUNT);
        propertyNameMap.put (P_COMPRESSION_COMPRESSION_TYPE_NAME, TikaProperty.COMPRESSION_COMPRESSION_TYPE_NAME);
        propertyNameMap.put (P_COMPRESSION_TYPE, TikaProperty.COMPRESSION_TYPE);
        propertyNameMap.put (HttpHeaders.CONTENT_ENCODING, TikaProperty.CONTENT_ENCODING);
        propertyNameMap.put (TikaCoreProperties.DESCRIPTION.getName(), TikaProperty.DESCRIPTION);
        propertyNameMap.put (P_DIMENSION_IMAGE_ORIENTATION, TikaProperty.DIMENSION_IMAGE_ORIENTATION);
        propertyNameMap.put (P_ENCODING, TikaProperty.ENCODING);
        propertyNameMap.put (TikaCoreProperties.IDENTIFIER.getName(), TikaProperty.IDENTIFIER);
        propertyNameMap.put (Office.IMAGE_COUNT.getName(), TikaProperty.IMAGE_COUNT);
        propertyNameMap.put (P_IMAGE_HEIGHT, TikaProperty.IMAGE_HEIGHT);
        propertyNameMap.put (P_TIFF_IMAGE_LENGTH, TikaProperty.IMAGE_HEIGHT);
        propertyNameMap.put (P_HEIGHT, TikaProperty.IMAGE_HEIGHT);
        propertyNameMap.put (P_IMAGE_WIDTH, TikaProperty.IMAGE_WIDTH);
        propertyNameMap.put (P_TIFF_IMAGE_WIDTH, TikaProperty.IMAGE_WIDTH);
        propertyNameMap.put (P_WIDTH, TikaProperty.IMAGE_WIDTH);
        propertyNameMap.put (TikaCoreProperties.LANGUAGE.getName(), TikaProperty.LANGUAGE);
        propertyNameMap.put (Office.LINE_COUNT.getName(), TikaProperty.LINE_COUNT);
        propertyNameMap.put (Office.OBJECT_COUNT.getName(), TikaProperty.OBJECT_COUNT);
        propertyNameMap.put (Office.PAGE_COUNT.getName(), TikaProperty.PAGE_COUNT);
        propertyNameMap.put (P_XMP_NPAGES, TikaProperty.PAGE_COUNT);
        propertyNameMap.put (Office.PARAGRAPH_COUNT.getName(), TikaProperty.PARAGRAPH_COUNT);
        propertyNameMap.put (P_RESOLUTION_UNIT, TikaProperty.RESOLUTION_UNIT);
        propertyNameMap.put (P_TIFF_RESOLUTION_UNIT, TikaProperty.RESOLUTION_UNIT);
        propertyNameMap.put (TikaCoreProperties.RIGHTS.getName(), TikaProperty.RIGHTS);
        propertyNameMap.put (P_SAMPLE_RATE, TikaProperty.SAMPLE_RATE);
        propertyNameMap.put (P_XMP_AUDIO_SAMPLE_RATE, TikaProperty.SAMPLE_RATE);
        propertyNameMap.put (OfficeOpenXMLExtended.DOC_SECURITY.getName(), TikaProperty.SECURITY);
        propertyNameMap.put (Office.TABLE_COUNT.getName(), TikaProperty.TABLE_COUNT);
        propertyNameMap.put (P_TIFF_SAMPLES_PER_PIXEL, TikaProperty.TIFF_SAMPLES_PER_PIXEL);
        propertyNameMap.put (TikaCoreProperties.TITLE.getName(), TikaProperty.TITLE);
        propertyNameMap.put (Office.WORD_COUNT.getName(), TikaProperty.WORD_COUNT);
        propertyNameMap.put (P_X_RESOLUTION, TikaProperty.X_RESOLUTION);
        propertyNameMap.put (P_TIFF_X_RESOLUTION, TikaProperty.X_RESOLUTION);
        propertyNameMap.put (P_Y_RESOLUTION, TikaProperty.Y_RESOLUTION);
        propertyNameMap.put (P_TIFF_Y_RESOLUTION, TikaProperty.Y_RESOLUTION);
        propertyNameMap.put (P_XMP_AUDIO_CHANNEL_TYPE, TikaProperty.XMP_AUDIO_CHANNEL_TYPE);
        propertyNameMap.put (P_XMP_AUDIO_COMPRESSOR, TikaProperty.XMP_AUDIO_COMPRESSOR);
        propertyNameMap.put (P_XMP_AUDIO_SAMPLE_TYPE, TikaProperty.XMP_AUDIO_SAMPLE_TYPE);
        propertyNameMap.put (P_XMP_PIXEL_ASPECT_RATIO, TikaProperty.XMP_PIXEL_ASPECT_RATIO);
        propertyNameMap.put (P_XMP_VIDEO_COLOR_SPACE, TikaProperty.XMP_VIDEO_COLOR_SPACE);
        propertyNameMap.put (P_XMP_VIDEO_FRAME_RATE, TikaProperty.XMP_VIDEO_FRAME_RATE);
        propertyNameMap.put (P_XMP_VIDEO_PIXEL_DEPTH, TikaProperty.XMP_VIDEO_PIXEL_DEPTH);
    }

    /** Map of Tika compression types to FITS compression types */
    private final static Map<String, String> compressionTypeMap = new HashMap<String, String>();
    static {
        compressionTypeMap.put("lzw", FitsMetadataValues.CMPR_LZW);
        compressionTypeMap.put("JPEG", FitsMetadataValues.CMPR_JPEG);
        compressionTypeMap.put("Baseline", FitsMetadataValues.CMPR_JPEG);
        compressionTypeMap.put("Progressive, Huffman", FitsMetadataValues.CMPR_JPEG);
        compressionTypeMap.put("deflate", FitsMetadataValues.CMPR_DEFLATE);
    }

    private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);
    private final static String TOOL_NAME = "Tika";
    private final static String TOOL_VERSION = "2.3.0";  // Hard-coded version till we can do better

    private final static MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
    private final Parser tikaParser;

    private static final Logger logger = LoggerFactory.getLogger(TikaTool.class);
    private boolean enabled = true;
    private Fits fits;

    public TikaTool(Fits fits) throws FitsToolException {
        super();
        try {
            TikaConfig config = new TikaConfig(Fits.FITS_XML_DIR + "tika" + File.separator + "tika-config.xml");
            tikaParser = new AutoDetectParser(config);
        } catch (Exception e) {
            throw new FitsToolException("Problem loading tika-config.xml", e);
        }
        this.fits = fits;
        logger.debug ("Initializing TikaTool");
        info = new ToolInfo(TOOL_NAME, TOOL_VERSION,"");
    }

    public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("TikaTool.extractInfo starting on " + file.getName());
    	long startTime = System.currentTimeMillis();
        Metadata metadata = new Metadata();

        InputStream instrm = null;
        try {
            instrm = TikaInputStream.get(file.toPath(), metadata);
        } catch (FileNotFoundException e) {
            logger.debug(("FileNotFoundException with Tika on file " + file.getAbsolutePath()));
            throw new FitsToolException ("Can't open file with Tika", e);
        } catch (IOException e) {
            logger.debug (e.getClass().getName() + " in Tika: " + e.getMessage());
            throw new FitsToolException ("IOException in Tika", e);
        }

        try {
            ParseContext context = new ParseContext();
            context.set(Parser.class, tikaParser);
            tikaParser.parse(instrm, new DefaultHandler(), metadata, context);
        } catch (EncryptedDocumentException e) {
            logger.debug("Tika cannot parse file " + file.getAbsolutePath() + " because it is encrypted");
        } catch (TikaException e) {
            logger.debug("Tika encountered an issue parsing file", e);
        } catch (Exception e) {
            logger.debug(e.getClass().getName() + " in Tika: " + e.getMessage(), e);
            throw new FitsToolException("Tika failed", e);
        } finally {
            try {
                instrm.close();
            } catch (Exception e) {
                logger.debug("Failed to close input stream for file " + file.getAbsolutePath());
            }
        }

        // Now we start constructing the tool output JDOM document
        Document toolData = buildToolData (metadata);
        // Now construct the raw data JDOM document
        Document rawData = buildRawData (metadata);
        ToolOutput output = new ToolOutput (this, toolData, rawData, fits);
        duration = System.currentTimeMillis()-startTime;
        runStatus = RunStatus.SUCCESSFUL;
        logger.debug ("Tika.extractInfo finished on " + file.getName());
        return output;
    }

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}

	/* Create the tool data from the Metadata object */
	private Document buildToolData (Metadata metadata) throws FitsToolException {
        //String mimeType =  DocumentTypes.normalizeMimeType(metadata.get (P_CONTENT_TYPE));
        String mimeType = FitsMetadataValues.getInstance().normalizeMimeType(metadata.get(Metadata.CONTENT_TYPE));

        Element fitsElem = new Element ("fits", fitsNS);
        Document toolDoc = new Document (fitsElem);
        Element idElem = new Element ("identification", fitsNS);
        fitsElem.addContent(idElem);
        Element identityElem = new Element ("identity", fitsNS);
        // Format and mime type info.

        String formatType = getFormatType(mimeType);
        // special case -- possibly override for PDF subtypes
        String pdfVersion = metadata.get(PDF.PDF_VERSION);
        String pdfaVersion = metadata.get(PDF.PDFA_VERSION);
        String pdfxVersion = metadata.get(P_PDFX_VERSION);
        if (pdfaVersion != null) {
        	formatType = "PDF/A";
        } else if (pdfxVersion != null) {
        	formatType = "PDF/X";
        }
        String version = null;
        if (pdfaVersion != null) {
        	version = pdfaVersion;
        } else if (pdfxVersion != null) {
        	version = pdfxVersion;
        } else if (pdfVersion != null) {
        	version = pdfVersion;
        }
        if (version != null) {
        	Element versionElem = new Element("version", fitsNS);
        	versionElem.addContent(version);
        	identityElem.addContent(versionElem);
        }

        Attribute attr = new Attribute ("format", formatType);
        identityElem.setAttribute (attr);
        attr = new Attribute ("mimetype", mimeType);
        identityElem.setAttribute (attr);
        idElem.addContent (identityElem);
        Element fileInfoElem = buildFileInfoElement (metadata);
        fitsElem.addContent (fileInfoElem);

        Element metadataElem = buildMetadataElement (metadata, mimeType);
        fitsElem.addContent (metadataElem);

        return toolDoc;
	}

	/* Create a dummy raw data object */
	private Document buildRawData (Metadata metadata) throws FitsToolException {
	    String xml = MetadataFormatter.toXML(metadata);
	    xml = XmlUtils.cleanXmlNulls(xml);
	    StringReader srdr = new StringReader (xml);

	    try {
	        Document rawDoc = saxBuilder.build (srdr);
	        return rawDoc;
	    }
	    catch (Exception e) {
	        throw new FitsToolException ("Exception reading metadata", e);
	    }
	}

    /* Build the file information.
     * Tika can deliver the same property with different names, sometimes for
     * the same file.
     */
    private Element buildFileInfoElement (Metadata metadata) {
        String lastModified = metadata.get(TikaCoreProperties.MODIFIED);
        String created = metadata.get(TikaCoreProperties.CREATED);
        String contentLength = metadata.get(Metadata.CONTENT_LENGTH);
        String producer = metadata.get(PDF.PRODUCER);
        String creatorTool = metadata.get(XMP.CREATOR_TOOL);
        String generator = metadata.get(P_GENERATOR);
        String applicationName = metadata.get(OfficeOpenXMLExtended.APPLICATION);
        String applicationVersion = metadata.get(OfficeOpenXMLExtended.APP_VERSION);

        // look for creating application
        String appName = "";
        if(producer != null && creatorTool != null) {
        	appName = producer + "/" + creatorTool;
        }
        else if(producer != null) {
        	appName = producer;
        }
        else if(creatorTool != null) {
        	appName = creatorTool;
        }
        else if (generator !=null) {
        	appName = generator;
        }
        if (applicationName != null) {
        	appName = applicationName;
        }


        // Put together the fileinfo element
        Element fileInfoElem = new Element ("fileinfo", fitsNS);
        if (lastModified != null) {
            Element lastModElem = new Element (FitsMetadataValues.LAST_MODIFIED, fitsNS);
            lastModElem.addContent (DateTimeUtil.standardize(lastModified));
            fileInfoElem.addContent (lastModElem);
        }

        if (created != null) {
            Element createdElem = new Element (FitsMetadataValues.CREATED, fitsNS);
            createdElem.addContent(DateTimeUtil.standardize(created));
            fileInfoElem.addContent(createdElem);
        }

        if (appName != null) {
            Element appNameElem = new Element (FitsMetadataValues.CREATING_APPLICATION_NAME, fitsNS);
            appNameElem.addContent (appName);
            fileInfoElem.addContent (appNameElem);
        }

        if (applicationVersion != null) {
            Element appVersionElem = new Element (FitsMetadataValues.CREATING_APPLICATION_VERSION, fitsNS);
            appVersionElem.addContent (applicationVersion);
            fileInfoElem.addContent (appVersionElem);
        }

        if (contentLength != null) {
            Element sizeElem = new Element (FitsMetadataValues.SIZE, fitsNS);
            sizeElem.addContent (contentLength);
            fileInfoElem.addContent (sizeElem);
        }
        return fileInfoElem;

	}

	private String getFormatType(String mime) throws FitsToolException {
	    String format = "";
	    try {
	        MimeType mimeType = mimeTypes.forName(mime);
	        format = mimeType.getDescription();

	        //try mapping the format to the standard form
	        if(format != null) {
	        	String stdFormat = FitsMetadataValues.getInstance().normalizeFormat(format);
	        	if(stdFormat != null) {
	        		format = stdFormat;
	        	}
	        }

	        // check if we need to get the format name based on the mime type
	        String stdText = FitsMetadataValues.getInstance().getFormatForMime(mime);
	        if (stdText != null) {
	            format = stdText;
	        }

	        // it may be necessary to get format name based on tool element rather than mime type



	        return format;
	    } catch (MimeTypeException e) {
	        throw new FitsToolException("Tika error looking up mime type");
	    }
	}

    private Element buildMetadataElement (Metadata metadata, String mimeType) {
        DocumentTypes.Doctype doctype = DocumentTypes.mimeToDoctype(mimeType);
        Element metadataElem = new Element ("metadata", fitsNS);
        switch (doctype) {
            case AUDIO:
                Element audioElem = buildAudioElement (metadata);
                metadataElem.addContent (audioElem);
                break;
            case IMAGE:
                Element imageElem = buildImageElement (metadata);
                metadataElem.addContent (imageElem);
                break;
            case DOCUMENT:
                Element docElem = buildDocElement (metadata, "application/pdf".equals(mimeType));
                metadataElem.addContent (docElem);
                break;
            case TEXT:
                Element textElem = buildTextElement (metadata);
                metadataElem.addContent (textElem);
                break;
            case VIDEO:
                Element videoElem = buildVideoElement (metadata);
                metadataElem.addContent (videoElem);
                break;
            default:
                break;
        }
        return metadataElem;
    }

    /* Return an element for an audio file */
    private Element buildAudioElement(Metadata metadata) {
        String[] metadataNames = metadata.names();
        Element elem = new Element (FitsMetadataValues.AUDIO, fitsNS);
        boolean titleReported = false;
        boolean authorReported = false;
        for (String name : metadataNames) {
            TikaProperty prop = propertyNameMap.get(name);
            if (prop == null) {
                continue;
            }
            String value = metadata.get(name);

            switch (prop) {

                case BITS:
                    addSimpleElement (elem, FitsMetadataValues.BIT_DEPTH, value);
                    break;

                case TITLE:
                    if (!titleReported) {
                        addSimpleElement (elem, FitsMetadataValues.TITLE, value);
                        titleReported = true;
                    }
                    break;

                case AUTHOR:
                    if (!authorReported) {
                        addSimpleElement (elem, FitsMetadataValues.AUTHOR, value);
                        authorReported = true;
                    }
                    break;

                case CHANNELS:
                    addSimpleElement (elem, FitsMetadataValues.CHANNELS, value);
                    break;

                case COMPRESSION_TYPE:
                    addSimpleElement (elem, FitsMetadataValues.COMPRESSION_SCHEME, value);
                    break;

//			Tika is not outputting the correct bits per sample
//           case DATA_BITS_PER_SAMPLE:
//               addSimpleElement (elem, FitsMetadataValues.BIT_DEPTH, value);
//               break;

                case ENCODING:
                    addSimpleElement (elem, FitsMetadataValues.AUDIO_DATA_ENCODING, value);
                    break;

                case SAMPLE_RATE:
                    addSimpleElement (elem, FitsMetadataValues.SAMPLE_RATE, value);
            }
        }
        return elem;
    }

	/* Return an element for an image file */
	private Element buildImageElement(Metadata metadata) {
        String[] metadataNames = metadata.names();
	    Element elem = new Element (FitsMetadataValues.IMAGE, fitsNS);
	    boolean bpsReported = false;
	    boolean widthReported = false;
	    boolean heightReported = false;
	    boolean xresReported = false;
        boolean yresReported = false;
        boolean resUnitReported = false;
	    for (String name : metadataNames) {
	        TikaProperty prop = propertyNameMap.get(name);
	        if (prop == null) {
	            // a property we don't know about?
	            continue;
	        }
	        String value = metadata.get(name);

            int idx;
            switch (prop) {
                case DIMENSION_IMAGE_ORIENTATION:
                    if ("Normal".equals (value)) {
                        addSimpleElement (elem, FitsMetadataValues.ORIENTATION, "normal*");
                    }
                    break;
                case IMAGE_WIDTH:
                    if (!widthReported) {
                        idx = value.indexOf (" pixels");
                        if (idx > 0) {
                            value = value.substring (0, idx);
                        }
                        addSimpleElement (elem, FitsMetadataValues.IMAGE_WIDTH, value);
                        widthReported = true;
                    }
                    break;

                case IMAGE_HEIGHT:
                    if (!heightReported) {
                        idx = value.indexOf (" pixels");
                        if (idx > 0) {
                            value = value.substring (0, idx);
                        }
                        addSimpleElement (elem, FitsMetadataValues.IMAGE_HEIGHT, value);
                        heightReported = true;
                    }
                    break;

                case TIFF_SAMPLES_PER_PIXEL:
                    addSimpleElement (elem, FitsMetadataValues.SAMPLES_PER_PIXEL, value);
                    break;

                case COMPRESSION_TYPE:
                case COMPRESSION_COMPRESSION_TYPE_NAME:
                    String stdValue = compressionTypeMap.get(value);
                    if (stdValue != null) {
                        value = stdValue;
                    }
                    addSimpleElement (elem, FitsMetadataValues.COMPRESSION_SCHEME, value);
                    break;

// Tika is not outputting the correct bits per sample
//	        case TIFF_BITS_PER_SAMPLE:
//	        case DATA_BITS_PER_SAMPLE:
//	            // We may get the same data in more than one property
//	            if (!bpsReported) {
//	                addSimpleElement (elem, FitsMetadataValues.BITS_PER_SAMPLE, value);
//	                bpsReported = true;
//	            }
//	            break;

                case RESOLUTION_UNIT:
                    if (!resUnitReported) {
                        if(value.equals("Inch")) {
                            value = "In.";
                        }
                        addSimpleElement (elem, FitsMetadataValues.SAMPLING_FREQUENCY_UNIT, value);
                        resUnitReported = true;
                    }
                    break;

                case X_RESOLUTION:
                    if (!xresReported) {
                        int ix = value.indexOf (" dot");
                        if (ix > 0) {
                            value = value.substring (0, ix);
                        }
                        addSimpleElement (elem, FitsMetadataValues.X_SAMPLING_FREQUENCY, value);
                        xresReported = true;
                    }
                    break;

                case Y_RESOLUTION:
                    if (!yresReported) {
                        int ix = value.indexOf (" dot");
                        if (ix > 0) {
                            value = value.substring (0, ix);
                        }
                        addSimpleElement (elem, FitsMetadataValues.Y_SAMPLING_FREQUENCY, value);
                        yresReported = true;
                    }
                    break;

            }
        }
        return elem;
    }

    /* Return an element for an document file */
    private Element buildDocElement(Metadata metadata, boolean isPdf) {
        String[] metadataNames = metadata.names();
        Element elem = new Element (FitsMetadataValues.DOCUMENT, fitsNS);

        String subject = metadata.get(TikaCoreProperties.SUBJECT.getName());
        if (subject != null) {
            addSimpleElement (elem, FitsMetadataValues.SUBJECT, subject);
        }

        boolean titleReported = false;
        boolean authorReported = false;
        boolean pageCountReported = false;
        boolean rightsReported = false;
        boolean wordCountReported = false;
        boolean descriptionReported = false;
        boolean identifierReported = false;
        for (String name : metadataNames) {
            TikaProperty prop = propertyNameMap.get(name);
            if (prop == null) {
                // a property we don't know about?
                continue;
            }
            String value = metadata.get(name);

            switch (prop) {
                case TITLE:
                    if (!titleReported) {
                        addSimpleElement (elem, FitsMetadataValues.TITLE, value);
                        titleReported = true;
                    }
                    break;

                case AUTHOR:
                    if (!authorReported) {
                        // Take Author if only one value from metadata (check for multiple values)
                        // otherwise will skip and let another tool deal with this.
                        String[] values = metadata.getValues(name);
                        if (values != null && values.length == 1) {
                            addSimpleElement (elem, FitsMetadataValues.AUTHOR, value);
                        }
                        authorReported = true;
                    }
                    break;

                case PAGE_COUNT:
                    if (!pageCountReported) {
                        addSimpleElement (elem, FitsMetadataValues.PAGE_COUNT, value);
                        pageCountReported = true;
                    }
                    break;

                case CATEGORY:
                    addSimpleElement (elem, FitsMetadataValues.CATEGORY, value);
                    break;

                case WORD_COUNT:
                    if (!wordCountReported) {
                        addSimpleElement (elem, FitsMetadataValues.WORD_COUNT, value);
                        wordCountReported = true;
                    }
                    break;

                case CHARACTER_COUNT:
                    addSimpleElement (elem, FitsMetadataValues.CHARACTER_COUNT, value);
                    break;

                case LINE_COUNT:
                    addSimpleElement (elem, FitsMetadataValues.LINE_COUNT, value);
                    break;

                case PARAGRAPH_COUNT:
                    addSimpleElement (elem, FitsMetadataValues.PARAGRAPH_COUNT, value);
                    break;

                case LANGUAGE:
                    addSimpleElement(elem, FitsMetadataValues.LANGUAGE, value);
                    break;

                case RIGHTS:
                    if (!rightsReported) {
                        value = "yes";
                        addSimpleElement (elem, FitsMetadataValues.IS_RIGHTS_MANAGED, value);
                        rightsReported = true;
                    }
                    break;

                case SECURITY:
                    if (!StringUtils.isEmpty(value)) {
                        value = "yes";
                        addSimpleElement(elem, FitsMetadataValues.IS_PROTECTED, value);
                    }
                    break;

                case IMAGE_COUNT:
                    if (!StringUtils.isEmpty(value)) {
                        addSimpleElement(elem, FitsMetadataValues.IMAGE_COUNT, value);
                    }
                    break;

                case TABLE_COUNT:
                    if (!StringUtils.isEmpty(value)) {
                        addSimpleElement(elem, FitsMetadataValues.TABLE_COUNT, value);
                    }
                    break;

                case OBJECT_COUNT:
                    if (!StringUtils.isEmpty(value) && !"0".equals(value)) {
                        value = "yes";
                        addSimpleElement(elem, FitsMetadataValues.HAS_EMBEDDED_RESOURCES, value);
                    }

                case DESCRIPTION:
                    if (!descriptionReported) {
                        addSimpleElement(elem, FitsMetadataValues.DESCRIPTION, value);
                        descriptionReported = true;
                    }
                    break;

                case IDENTIFIER:
                    if (!identifierReported) {
                        addSimpleElement(elem, FitsMetadataValues.IDENTIFIER, value);
                        identifierReported = true;
                    }
                    break;
            }
        }

        return elem;
    }

    /* Return an element for a text file */
    private Element buildTextElement(Metadata metadata) {
        String[] metadataNames = metadata.names();
        Element elem = new Element (FitsMetadataValues.TEXT, fitsNS);
        for (String name : metadataNames) {
            TikaProperty prop = propertyNameMap.get(name);
            if (prop == null) {
                // a property we don't know about
                continue;
            }
            String value = metadata.get(name);

            switch (prop) {
                case TITLE:
                    addSimpleElement (elem, FitsMetadataValues.TITLE, value);
                    break;

                case CONTENT_ENCODING:
                    addSimpleElement (elem, FitsMetadataValues.CHARSET, value);
                    break;

                case WORD_COUNT:
                    addSimpleElement (elem, FitsMetadataValues.WORD_COUNT, value);
                    break;
            }
        }
        return elem;
    }

    /* Return an element for an video file */
    private Element buildVideoElement(Metadata metadata) {
        String[] metadataNames = metadata.names();
        Element elem = new Element (FitsMetadataValues.VIDEO, fitsNS);
        boolean heightReported = false;
        boolean compressionTypeReported = false;

        for (String name : metadataNames) {
            TikaProperty prop = propertyNameMap.get(name);
            if (prop == null) {
                continue;
            }
            String value = metadata.get(name);

            switch (prop) {

                case TITLE:
                    addSimpleElement (elem, FitsMetadataValues.TITLE, value);
                    break;

                case AUTHOR:
                    addSimpleElement (elem, FitsMetadataValues.AUTHOR, value);
                    break;

                case XMP_AUDIO_CHANNEL_TYPE:
                    addSimpleElement (elem, FitsMetadataValues.AUDIO_CHANNEL_TYPE, value);
                    break;

                case SAMPLE_RATE:
                    addSimpleElement (elem, FitsMetadataValues.AUDIO_SAMPLE_RATE, value);
                    break;

                case XMP_AUDIO_SAMPLE_TYPE:
                    addSimpleElement (elem, FitsMetadataValues.AUDIO_SAMPLE_TYPE, value);
                    break;

                case XMP_AUDIO_COMPRESSOR:
                case COMPRESSION_TYPE:
                    if (!compressionTypeReported) {
                        addSimpleElement (elem, FitsMetadataValues.COMPRESSION_SCHEME, value);
                        compressionTypeReported = true;
                    }
                    break;

                case XMP_VIDEO_FRAME_RATE:
                    addSimpleElement(elem, FitsMetadataValues.FRAME_RATE, value);
                    break;

                case XMP_PIXEL_ASPECT_RATIO:
                    addSimpleElement(elem, FitsMetadataValues.PIXEL_ASPECT_RATIO, value);
                    break;

                case XMP_VIDEO_PIXEL_DEPTH:
                    addSimpleElement (elem, FitsMetadataValues.BIT_DEPTH, value);
                    break;

                case XMP_VIDEO_COLOR_SPACE:
                    addSimpleElement (elem, FitsMetadataValues.COLOR_SPACE, value);
                    break;

                case IMAGE_HEIGHT:
                    if (!heightReported) {
                        int idx = value.indexOf (" pixels");
                        if (idx > 0) {
                            value = value.substring (0, idx);
                        }
                        addSimpleElement (elem, FitsMetadataValues.IMAGE_HEIGHT, value);
                        heightReported = true;
                    }
                    break;
            }
        }
        return elem;
    }



    private void addSimpleElement (Element parent, String tag, String value ) {
        Element newElem = new Element (tag, fitsNS);
        newElem.addContent (value);
        parent.addContent (newElem);
    }

}

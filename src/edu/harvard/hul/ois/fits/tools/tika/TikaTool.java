package edu.harvard.hul.ois.fits.tools.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import edu.harvard.hul.ois.fits.DocumentTypes;
import edu.harvard.hul.ois.fits.FitsMetadataValues;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.utils.XmlUtils;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class TikaTool extends ToolBase {

    /** Constants for all Tika property names, so that they're all gathered in
     *  one place and we don't have to fix inline names. */
    private final static String P_APPLICATION_NAME = "Application-Name";
    private final static String P_AUTHOR = "Author";
    private final static String P_BITS = "bits";
    private final static String P_CHANNELS = "channels";
    private final static String P_CHRM = "cHRM";
    private final static String P_CHROMA_BLACK_IS_ZERO = "Chroma BlackIsZero";
    private final static String P_CHROMA_COLOR_SPACE_TYPE = "Chroma ColorSpaceType";
    private final static String P_CHROMA_NUM_CHANNELS = "Chroma NumChannels";
    private final static String P_COMPRESSION_COMPRESSION_TYPE_NAME = "Compression CompressionTypeName";
    private final static String P_COMPRESSION_LOSSLESS = "Compression Lossless";
    private final static String P_COMPRESSION_NUM_PROGRESSIVE_SCANS = "Compression NumProgressiveScans";
    private final static String P_COMPRESSION_TYPE = "Compression Type";
    private final static String P_CONTENT_ENCODING = "Content-Encoding";
    private final static String P_CONTENT_LENGTH = "Content-Length";
    private final static String P_CONTENT_TYPE = "Content-Type";
    private final static String P_CONTRIBUTOR = "contributor";
    private final static String P_CREATED = "created";
    private final static String P_CREATION_DATE = "Creation-Date";
    private final static String P_CREATOR = "creator";
    private final static String P_CREATOR_TOOL = "xmp:CreatorTool";
    private final static String P_DATA_BITS_PER_SAMPLE = "Data BitsPerSample";
    private final static String P_DATA_PLANAR_CONFIGURATION = "Data PlanarConfiguration";
    private final static String P_DATA_SAMPLE_FORMAT = "Data SampleFormat";
    private final static String P_DATE = "date";
    private final static String P_DC_CONTRIBUTOR = "dc:contributor";
    private final static String P_DC_CREATED = "dcterms:created";
    private final static String P_DC_CREATOR = "dc:creator";
    private final static String P_DC_IDENTIFIER = "dc:identifier";
    private final static String P_DC_LANGUAGE = "dc:language";
    private final static String P_DC_MODIFIED = "dcterms:modified";
    private final static String P_DC_PUBLISHER = "dc:publisher";
    private final static String P_DC_RIGHTS = "dc:rights";
    private final static String P_DC_SUBJECT = "dc:subject";
    private final static String P_DC_TITLE = "dc:title";
    private final static String P_DIMENSION_HORIZONTAL_PIXEL_SIZE = "Dimension HorizontalPixelSize";
    private final static String P_DIMENSION_IMAGE_ORIENTATION ="Dimension ImageOrientation";
    private final static String P_DIMENSION_PIXEL_ASPECT_RATIO ="Dimension PixelAspectRatio";
    private final static String P_DIMENSION_VERTICAL_PIXEL_SIZE ="Dimension VerticalPixelSize";
    private final static String P_EDIT_TIME = "Edit-Time";
    private final static String P_EDITING_CYCLES = "editing-cycles";
    private final static String P_ENCODING = "encoding";
    private final static String P_GENERATOR = "generator";
    private final static String P_HEIGHT = "height";
    private final static String P_ICCP = "iCCP";
    private final static String P_IDENTIFIER = "identifier";
    private final static String P_IHDR = "IHDR";
    private final static String P_IMAGE_HEIGHT = "Image Height";
    private final static String P_IMAGE_WIDTH = "Image Width";
    private final static String P_KEYWORDS = "Keywords";
    private final static String P_LANGUAGE = "language";
    private final static String P_LAST_MODIFIED = "Last-Modified";
    private final static String P_LAST_SAVE_DATE = "Last-Save-Date";
    private final static String P_META_AUTHOR = "meta:author";
    private final static String P_META_CREATION_DATE = "meta:creation-date";
    private final static String P_META_INITIAL_AUTHOR = "meta:initial-author";
    private final static String P_META_KEYWORD = "meta:keyword";
    private final static String P_META_SAVE_DATE = "meta:save-date";
    private final static String P_META_OBJECT_COUNT = "meta:object-count";
    private final static String P_META_TABLE_COUNT = "meta:table-count";
    private final static String P_MODIFIED = "modified";
    private final static String P_NBOBJECT = "nbObject";
    private final static String P_NBTAB = "nbTab";
    private final static String P_OBJECT_COUNT = "Object-Count";
    private final static String P_PHYS = "pHYs";
    private final static String P_PRODUCER = "producer";
    private final static String P_PUBLISHER = "publisher";
    private final static String P_RESOURCE_NAME = "resourceName";
    private final static String P_SAMPLE_RATE = "samplerate";
    private final static String P_SUBJECT = "subject";
    private final static String P_TABLE_COUNT = "Table-Count";
    private final static String P_TIFF_BITS_PER_SAMPLE = "tiff:BitsPerSample";
    private final static String P_TIFF_IMAGE_LENGTH = "tiff:ImageLength";
    private final static String P_TIFF_IMAGE_WIDTH = "tiff:ImageWidth";
    private final static String P_TIFF_RESOLUTION_UNIT = "tiff:ResolutionUnit";
    private final static String P_TITLE = "title";
    private final static String P_TRANSPARENCY_ALPHA = "Transparency Alpha";
    private final static String P_WIDTH = "width";
    private final static String P_VERSION = "version";
    private final static String P_WORD_COUNT = "Word-Count";
    private final static String P_XMP_ARTIST = "xmpDM:artist";
    private final static String P_XMP_AUDIO_CHANNEL_TYPE = "xmpDM:audioChannelType";
    private final static String P_XMP_AUDIO_COMPRESSOR = "xmpDM:audioCompressor";
    private final static String P_XMP_AUDIO_SAMPLE_RATE = "xmpDM:audioSampleRate";
    private final static String P_XMP_AUDIO_SAMPLE_TYPE = "xmpDM:audioSampleType";
    private final static String P_XMP_GENRE = "xmpDM:genre";
    private final static String P_XMP_NPAGES = "xmpTPg:NPages";

    
    /** Enumeration of Tika properties. */
    private enum TikaProperty {
        APPLICATION_NAME,
        AUTHOR,
        BITS,
        //BITS_PER_SAMPLE,
        CHANNELS,
        CHRM,
        CHROMA_BLACK_IS_ZERO,
        CHROMA_COLOR_SPACE_TYPE,
        CHROMA_NUM_CHANNELS,
        COMPRESSION_NUM_PROGRESSIVE_SCANS,
        COMPRESSION_COMPRESSION_TYPE_NAME,
        COMPRESSION_LOSSLESS,
        COMPRESSION_TYPE,
        CONTENT_ENCODING,
        CONTENT_LENGTH,
        CONTENT_TYPE,
        CONTRIBUTOR,
        CREATED,
        CREATION_DATE,
        CREATOR,
        CREATOR_TOOL,
        DATA_BITS_PER_SAMPLE,
        DATA_PLANAR_CONFIGURATION,
        DATA_SAMPLE_FORMAT,
        DATE,
        DC_CONTRIBUTOR,
        DC_CREATED,
        DC_CREATOR,
        DC_IDENTIFIER,
        DC_LANGUAGE,
        DC_MODIFIED,
        DC_PUBLISHER,
        DC_RIGHTS,
        DC_SUBJECT,
        DC_TITLE,
        DIMENSION_HORIZONTAL_PIXEL_SIZE,
        DIMENSION_IMAGE_ORIENTATION,
        DIMENSION_PIXEL_ASPECT_RATIO,
        DIMENSION_VERTICAL_PIXEL_SIZE,
        EDIT_TIME,
        EDITING_CYCLES,
        ENCODING,
        GENERATOR,
        HEIGHT,
        ICCP,
        IDENTIFIER,
        IHDR,
        IMAGE_HEIGHT,
        IMAGE_WIDTH,
        KEYWORDS,
        LANGUAGE,
        LAST_MODIFIED,
        LAST_SAVE_DATE,
        META_AUTHOR,
        META_CREATION_DATE,
        META_INITIAL_AUTHOR,
        META_KEYWORD,
        META_SAVE_DATE,
        META_OBJECT_COUNT,
        META_TABLE_COUNT,
        MODIFIED,
        N_PAGES,
        NBOBJECT,
        NBTAB,
        OBJECT_COUNT,
        PHYS,
        PRODUCER,
        PUBLISHER,
        RESOURCE_NAME,
        SAMPLE_RATE,
        SUBJECT,
        TABLE_COUNT,
        TIFF_BITS_PER_SAMPLE,
        TIFF_IMAGE_LENGTH,
        TIFF_IMAGE_WIDTH,
        TIFF_RESOLUTION_UNIT,
        TITLE,
        TRANSPARENCY_ALPHA,
        VERSION,
        WIDTH,
        WORD_COUNT,
        XMP_ARTIST,
        XMP_AUDIO_CHANNEL_TYPE,
        XMP_AUDIO_COMPRESSOR,
        XMP_AUDIO_SAMPLE_RATE,
        XMP_AUDIO_SAMPLE_TYPE,
        XMP_GENRE,
        XMP_NPAGES
    }
    
    /** Map of Tika properties to TikaProperty
     */
    private final static Map<String, TikaProperty> propertyNameMap = 
            new HashMap<String, TikaProperty>();
    static {
        propertyNameMap.put (P_APPLICATION_NAME, TikaProperty.APPLICATION_NAME);
        propertyNameMap.put (P_AUTHOR, TikaProperty.AUTHOR);
        propertyNameMap.put (P_BITS, TikaProperty.BITS);
        propertyNameMap.put (P_CHANNELS, TikaProperty.CHANNELS);
        propertyNameMap.put (P_CHRM, TikaProperty.CHRM);
        propertyNameMap.put (P_CHROMA_BLACK_IS_ZERO, TikaProperty.CHROMA_BLACK_IS_ZERO);
        propertyNameMap.put (P_CHROMA_COLOR_SPACE_TYPE, TikaProperty.CHROMA_COLOR_SPACE_TYPE);
        propertyNameMap.put (P_CHROMA_NUM_CHANNELS, TikaProperty.CHROMA_NUM_CHANNELS);
        propertyNameMap.put (P_COMPRESSION_NUM_PROGRESSIVE_SCANS, TikaProperty.COMPRESSION_NUM_PROGRESSIVE_SCANS);
        propertyNameMap.put (P_COMPRESSION_COMPRESSION_TYPE_NAME, TikaProperty.COMPRESSION_COMPRESSION_TYPE_NAME);
        propertyNameMap.put (P_COMPRESSION_LOSSLESS, TikaProperty.COMPRESSION_LOSSLESS);
        propertyNameMap.put (P_COMPRESSION_TYPE, TikaProperty.COMPRESSION_TYPE);
        propertyNameMap.put (P_CONTENT_ENCODING, TikaProperty.CONTENT_ENCODING);
        propertyNameMap.put (P_CONTENT_LENGTH, TikaProperty.CONTENT_LENGTH);
        propertyNameMap.put (P_CONTENT_TYPE, TikaProperty.CONTENT_TYPE);
        propertyNameMap.put (P_CONTRIBUTOR, TikaProperty.CONTRIBUTOR);
        propertyNameMap.put (P_CREATED, TikaProperty.CREATED);
        propertyNameMap.put (P_CREATION_DATE, TikaProperty.CREATION_DATE);
        propertyNameMap.put (P_CREATOR, TikaProperty.CREATOR);
        propertyNameMap.put (P_CREATOR_TOOL, TikaProperty.CREATOR_TOOL);
        propertyNameMap.put (P_DATA_BITS_PER_SAMPLE, TikaProperty.DATA_BITS_PER_SAMPLE);
        propertyNameMap.put (P_DATA_PLANAR_CONFIGURATION, TikaProperty.DATA_PLANAR_CONFIGURATION);
        propertyNameMap.put (P_DATA_SAMPLE_FORMAT, TikaProperty.DATA_SAMPLE_FORMAT);
        propertyNameMap.put (P_DATE, TikaProperty.DATE);
        propertyNameMap.put (P_DC_CONTRIBUTOR, TikaProperty.DC_CONTRIBUTOR);
        propertyNameMap.put (P_DC_CREATED, TikaProperty.DC_CREATED);
        propertyNameMap.put (P_DC_CREATOR, TikaProperty.DC_CREATOR);
        propertyNameMap.put (P_DC_IDENTIFIER, TikaProperty.DC_IDENTIFIER);
        propertyNameMap.put (P_DC_LANGUAGE, TikaProperty.DC_LANGUAGE);
        propertyNameMap.put (P_DC_MODIFIED, TikaProperty.DC_MODIFIED);
        propertyNameMap.put (P_DC_SUBJECT, TikaProperty.DC_SUBJECT);
        propertyNameMap.put (P_DC_TITLE, TikaProperty.DC_TITLE);
        propertyNameMap.put (P_DIMENSION_HORIZONTAL_PIXEL_SIZE, TikaProperty.DIMENSION_HORIZONTAL_PIXEL_SIZE);
        propertyNameMap.put (P_DIMENSION_IMAGE_ORIENTATION, TikaProperty.DIMENSION_IMAGE_ORIENTATION);
        propertyNameMap.put (P_DIMENSION_PIXEL_ASPECT_RATIO, TikaProperty.DIMENSION_PIXEL_ASPECT_RATIO);
        propertyNameMap.put (P_DIMENSION_VERTICAL_PIXEL_SIZE, TikaProperty.DIMENSION_VERTICAL_PIXEL_SIZE);
        propertyNameMap.put (P_EDIT_TIME, TikaProperty.EDIT_TIME);
        propertyNameMap.put (P_EDITING_CYCLES, TikaProperty.EDITING_CYCLES);
        propertyNameMap.put (P_ENCODING, TikaProperty.ENCODING);
        propertyNameMap.put (P_GENERATOR, TikaProperty.GENERATOR);
        propertyNameMap.put (P_HEIGHT, TikaProperty.HEIGHT);
        propertyNameMap.put (P_ICCP, TikaProperty.ICCP);
        propertyNameMap.put (P_IDENTIFIER, TikaProperty.IDENTIFIER);
        propertyNameMap.put (P_IHDR, TikaProperty.IHDR);
        propertyNameMap.put (P_IMAGE_HEIGHT, TikaProperty.IMAGE_HEIGHT);
        propertyNameMap.put (P_IMAGE_WIDTH, TikaProperty.IMAGE_WIDTH);
        propertyNameMap.put (P_KEYWORDS, TikaProperty.KEYWORDS);
        propertyNameMap.put (P_LANGUAGE, TikaProperty.LANGUAGE);
        propertyNameMap.put (P_LAST_MODIFIED, TikaProperty.LAST_MODIFIED);
        propertyNameMap.put (P_LAST_SAVE_DATE, TikaProperty.LAST_SAVE_DATE);
        propertyNameMap.put (P_META_AUTHOR, TikaProperty.META_AUTHOR);
        propertyNameMap.put (P_META_CREATION_DATE, TikaProperty.META_CREATION_DATE);
        propertyNameMap.put (P_META_INITIAL_AUTHOR, TikaProperty.META_INITIAL_AUTHOR);
        propertyNameMap.put (P_META_KEYWORD, TikaProperty.META_KEYWORD);
        propertyNameMap.put (P_META_OBJECT_COUNT, TikaProperty.META_OBJECT_COUNT);
        propertyNameMap.put (P_META_TABLE_COUNT, TikaProperty.META_TABLE_COUNT);
        propertyNameMap.put (P_META_SAVE_DATE, TikaProperty.META_SAVE_DATE);
        propertyNameMap.put (P_MODIFIED, TikaProperty.MODIFIED);
        propertyNameMap.put (P_NBOBJECT, TikaProperty.NBOBJECT);
        propertyNameMap.put (P_NBTAB, TikaProperty.NBTAB);
        propertyNameMap.put (P_OBJECT_COUNT, TikaProperty.OBJECT_COUNT);
        propertyNameMap.put (P_PHYS, TikaProperty.PHYS);
        propertyNameMap.put (P_PRODUCER, TikaProperty.PRODUCER);
        propertyNameMap.put (P_PUBLISHER, TikaProperty.PUBLISHER);
        propertyNameMap.put (P_RESOURCE_NAME, TikaProperty.RESOURCE_NAME);
        propertyNameMap.put (P_SAMPLE_RATE, TikaProperty.SAMPLE_RATE);
        propertyNameMap.put (P_SUBJECT, TikaProperty.SUBJECT);
        propertyNameMap.put (P_TABLE_COUNT, TikaProperty.TABLE_COUNT);
        propertyNameMap.put (P_TIFF_BITS_PER_SAMPLE, TikaProperty.TIFF_BITS_PER_SAMPLE);
        propertyNameMap.put (P_TIFF_IMAGE_LENGTH, TikaProperty.TIFF_IMAGE_LENGTH);
        propertyNameMap.put (P_TIFF_IMAGE_WIDTH, TikaProperty.TIFF_IMAGE_WIDTH);
        propertyNameMap.put (P_TIFF_RESOLUTION_UNIT, TikaProperty.TIFF_RESOLUTION_UNIT);
        propertyNameMap.put (P_TITLE, TikaProperty.TITLE);
        propertyNameMap.put (P_TRANSPARENCY_ALPHA, TikaProperty.TRANSPARENCY_ALPHA);
        propertyNameMap.put (P_VERSION, TikaProperty.VERSION);
        propertyNameMap.put (P_WIDTH, TikaProperty.WIDTH);
        propertyNameMap.put (P_WORD_COUNT, TikaProperty.WORD_COUNT);
        propertyNameMap.put (P_XMP_ARTIST, TikaProperty.XMP_ARTIST);
        propertyNameMap.put (P_XMP_AUDIO_CHANNEL_TYPE, TikaProperty.XMP_AUDIO_CHANNEL_TYPE);
        propertyNameMap.put (P_XMP_AUDIO_COMPRESSOR, TikaProperty.XMP_AUDIO_COMPRESSOR);
        propertyNameMap.put (P_XMP_AUDIO_SAMPLE_RATE, TikaProperty.XMP_AUDIO_SAMPLE_RATE);
        propertyNameMap.put (P_XMP_AUDIO_SAMPLE_TYPE, TikaProperty.XMP_AUDIO_SAMPLE_TYPE);
        propertyNameMap.put (P_XMP_GENRE, TikaProperty.XMP_GENRE);
        propertyNameMap.put (P_XMP_NPAGES, TikaProperty.XMP_NPAGES);
    }
    

    private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);
    private final static String TOOL_NAME = "Tika";
    private final static String TOOL_VERSION = "1.3";  // Hard-coded version till we can do better
    
    // TODO could get the version by doing an exec of tika --version ...
    // but then I have to know the path of the jar.
    private final static MediaTypeRegistry typeRegistry = MediaTypeRegistry.getDefaultRegistry();
    private final static MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
    private Tika tika = new Tika ();
    
    private boolean enabled = true;

    public TikaTool() throws FitsToolException {
        info = new ToolInfo(TOOL_NAME, TOOL_VERSION,"");
    }

    public ToolOutput extractInfo(File file) throws FitsToolException {
    	long startTime = System.currentTimeMillis();
        Metadata metadata = new Metadata(); // = new Metadata();
        FileInputStream instrm = null;
        try {
            instrm = new FileInputStream (file);
        }
        catch (FileNotFoundException e) {
            throw new FitsToolException ("Can't open file with Tika", e);
        }
        try {
            tika.parse (instrm, metadata);
        } catch (IOException e) {
            throw new FitsToolException ("IOException in Tika", e);
        }
        
        // Now we start constructing the tool output JDOM document
        Document toolData = buildToolData (metadata);
        // Now construct the raw data JDOM document
        Document rawData = buildRawData (metadata);
        ToolOutput output = new ToolOutput (this, toolData, rawData);
        duration = System.currentTimeMillis()-startTime;
        runStatus = RunStatus.SUCCESSFUL;
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
        String mimeType = 
                DocumentTypes.normalizeMimeType(metadata.get (P_CONTENT_TYPE));


        Element fitsElem = new Element ("fits", fitsNS);
        Document toolDoc = new Document (fitsElem);
        Element idElem = new Element ("identification", fitsNS);
        fitsElem.addContent(idElem);
        Element identityElem = new Element ("identity", fitsNS);
        // Format and mime type info. 
        // TODO create real format name
        
        Attribute attr = new Attribute ("format", mimeToFileType(mimeType));
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
        String lastModified = metadata.get (P_LAST_MODIFIED);
        if (lastModified == null) {
            lastModified = metadata.get(P_MODIFIED);
        }
        if (lastModified == null) {
            lastModified = metadata.get(P_DC_MODIFIED);
        }
        if (lastModified == null) {
            lastModified = metadata.get(P_LAST_MODIFIED);
        }
        String created = metadata.get (P_CREATED);
        if (created == null) {
            created = metadata.get(P_CREATION_DATE);
        }
        if (created == null) {
            created = metadata.get(P_DC_CREATED);
        }
        String contentLength = metadata.get (P_CONTENT_LENGTH);
        String resourceName = metadata.get (P_RESOURCE_NAME);
        String appName = metadata.get (P_APPLICATION_NAME);
        if (appName == null) {
            appName = metadata.get (P_CREATOR_TOOL);
        }

        // Put together the fileinfo element
        Element fileInfoElem = new Element ("fileinfo", fitsNS);
        if (lastModified != null) {
            Element lastModElem = new Element ("lastmodified", fitsNS);
            lastModElem.addContent (lastModified);
            fileInfoElem.addContent (lastModElem);
        }
        
        if (appName != null) {
            Element appNameElem = new Element ("creatingApplicationName", fitsNS);
            appNameElem.addContent (appName);
            fileInfoElem.addContent (appNameElem);
        }
        
        if (contentLength != null) {
            Element sizeElem = new Element ("size", fitsNS);
            sizeElem.addContent (sizeElem);
            fileInfoElem.addContent (sizeElem);
        }
        return fileInfoElem;
	    
	}
	
	private String mimeToFileType (String mime) throws FitsToolException {
	    String format = "";
	    try {
	        MimeType mimeType = mimeTypes.forName(mime);
	        format = mimeType.getDescription();
	        
	        // convert to FITS standard file types if necessary
	        String stdText = FitsMetadataValues.mimeToDescMap.get(mime);
	        if (stdText != null) {
	            format = stdText;
	        }
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
           Element docElem = buildDocElement (metadata);
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
           case DC_TITLE:
               if (!titleReported) {
                   addSimpleElement (elem, FitsMetadataValues.TITLE, value);
                   titleReported = true;
               }
               break;
           
           case AUTHOR:
           case META_AUTHOR:
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
               
           case DATA_BITS_PER_SAMPLE:
               addSimpleElement (elem, FitsMetadataValues.BIT_DEPTH, value);
               break;
               
           case ENCODING:
               addSimpleElement (elem, FitsMetadataValues.AUDIO_ENCODING, value);
               break;

           case SAMPLE_RATE:
           case XMP_AUDIO_SAMPLE_RATE:
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
	        case TIFF_IMAGE_WIDTH:
	        case WIDTH:
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
	        case TIFF_IMAGE_LENGTH:
	        case HEIGHT:
	            if (!heightReported) {
    	            idx = value.indexOf (" pixels");
    	            if (idx > 0) {
    	                value = value.substring (0, idx);
    	            }
    	            addSimpleElement (elem, FitsMetadataValues.IMAGE_HEIGHT, value);
    	            heightReported = true;
	            }
	            break;

	        case COMPRESSION_TYPE:
	            addSimpleElement (elem, FitsMetadataValues.COMPRESSION_SCHEME, value);
	            break;
	            
	        case TIFF_BITS_PER_SAMPLE:
	        case DATA_BITS_PER_SAMPLE:
	            // We may get the same data in more than one property
	            if (!bpsReported) {
	                addSimpleElement (elem, FitsMetadataValues.BITS_PER_SAMPLE, value);
	                bpsReported = true;
	            }
	            break;
	        
	        case TIFF_RESOLUTION_UNIT:
	            // TODO we have to use this with XResolution and YResolution to get the image resolution.
	            break;
            }
	    }
	    return elem;
	}
	
   /* Return an element for an document file */
    private Element buildDocElement(Metadata metadata) {
        String[] metadataNames = metadata.names();
        Element elem = new Element (FitsMetadataValues.DOCUMENT, fitsNS);
        boolean titleReported = false;
        for (String name : metadataNames) {
            TikaProperty prop = propertyNameMap.get(name);
            if (prop == null) {
                // a property we don't know about?
                continue;
            }
            String value = metadata.get(name);
            
            switch (prop) {
            case TITLE:
            case DC_TITLE:
                if (!titleReported) {
                    addSimpleElement (elem, FitsMetadataValues.TITLE, value);
                    titleReported = true;
                }
                break;
            
            case AUTHOR:
                addSimpleElement (elem, FitsMetadataValues.AUTHOR, value);
                break;
                
            case LAST_MODIFIED:
                // TODO do something here
                break;
                
            case SUBJECT:
                addSimpleElement (elem, FitsMetadataValues.SUBJECT, value);
                break;
            
            case N_PAGES:
                addSimpleElement (elem, FitsMetadataValues.PAGE_COUNT, value);
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
                
            case COMPRESSION_TYPE:
                addSimpleElement (elem, FitsMetadataValues.COMPRESSION_SCHEME, value);
                break;
                
//            case BITS_PER_SAMPLE:
//                addSimpleElement (elem, FitsMetadataValues.BITS_PER_SAMPLE, value);
//                break;
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

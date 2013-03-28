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
    private final static String P_TIFF_BITS_PER_SAMPLE = "tiff:BitsPerSample";
    private final static String P_COMPRESSION_TYPE = "Compression Type";
    private final static String P_CONTENT_LENGTH = "Content-Length";
    private final static String P_CONTENT_TYPE = "Content-Type";
    private final static String P_CREATED = "created";
    private final static String P_CREATION_DATE = "Creation-Date";
    private final static String P_CREATOR_TOOL = "xmp:CreatorTool";
    private final static String P_DATE = "date";
    private final static String P_DC_CREATED = "dcterms:created";
    private final static String P_DC_MODIFIED = "dcterms:modified";
    private final static String P_DC_TITLE = "dc:title";
    private final static String P_IMAGE_HEIGHT = "Image Height";
    private final static String P_IMAGE_WIDTH = "Image Width";
    private final static String P_LAST_MODIFIED = "Last-Modified";
    private final static String P_LAST_SAVE_DATE = "Last-Save-Date";
    private final static String P_MODIFIED = "modified";
    private final static String P_META_CREATION_DATE = "meta:creation-date";
    private final static String P_META_SAVE_DATE = "meta:save-date";
    private final static String P_NPAGES = "xmpTPg:NPages";
    private final static String P_PRODUCER = "producer";
    private final static String P_RESOURCE_NAME = "resourceName";
    private final static String P_SUBJECT = "subject";
    private final static String P_TIFF_IMAGE_LENGTH = "tiff:ImageLength";
    private final static String P_TIFF_IMAGE_WIDTH = "tiff:ImageWidth";
    private final static String P_TIFF_RESOLUTION_UNIT = "tiff:ResolutionUnit";
    private final static String P_TITLE = "title";
    private final static String P_WORD_COUNT = "Word-Count";

    
    /** Enumeration of Tika properties. */
    private enum TikaProperty {
        APPLICATION_NAME,
        AUTHOR,
        BITS_PER_SAMPLE,
        COMPRESSION_TYPE,
        CONTENT_LENGTH,
        CONTENT_TYPE,
        CREATED,
        CREATION_DATE,
        CREATOR_TOOL,
        DATE,
        DC_CREATED,
        DC_MODIFIED,
        DC_TITLE,
        IMAGE_HEIGHT,
        IMAGE_WIDTH,
        LAST_MODIFIED,
        LAST_SAVE_DATE,
        META_CREATION_DATE,
        META_SAVE_DATE,
        MODIFIED,
        N_PAGES,
        PRODUCER,
        RESOURCE_NAME,
        SUBJECT,
        TIFF_BITS_PER_SAMPLE,
        TIFF_IMAGE_LENGTH,
        TIFF_IMAGE_WIDTH,
        TIFF_RESOLUTION_UNIT,
        TITLE,
        WORD_COUNT
    }
    
    /** Map of Tika properties to TikaProperty
     */
    private final static Map<String, TikaProperty> propertyNameMap = 
            new HashMap<String, TikaProperty>();
    static {
        propertyNameMap.put (P_APPLICATION_NAME, TikaProperty.APPLICATION_NAME);
        propertyNameMap.put (P_AUTHOR, TikaProperty.AUTHOR);
        propertyNameMap.put (P_COMPRESSION_TYPE, TikaProperty.COMPRESSION_TYPE);
        propertyNameMap.put (P_CONTENT_LENGTH, TikaProperty.CONTENT_LENGTH);
        propertyNameMap.put (P_CONTENT_TYPE, TikaProperty.CONTENT_TYPE);
        propertyNameMap.put (P_CREATED, TikaProperty.CREATED);
        propertyNameMap.put (P_CREATION_DATE, TikaProperty.CREATION_DATE);
        propertyNameMap.put (P_CREATOR_TOOL, TikaProperty.CREATOR_TOOL);
        propertyNameMap.put (P_DATE, TikaProperty.DATE);
        propertyNameMap.put (P_DC_CREATED, TikaProperty.DC_CREATED);
        propertyNameMap.put (P_DC_MODIFIED, TikaProperty.DC_MODIFIED);
        propertyNameMap.put (P_DC_TITLE, TikaProperty.DC_TITLE);
        propertyNameMap.put (P_IMAGE_HEIGHT, TikaProperty.IMAGE_HEIGHT);
        propertyNameMap.put (P_IMAGE_WIDTH, TikaProperty.IMAGE_WIDTH);
        propertyNameMap.put (P_LAST_MODIFIED, TikaProperty.LAST_MODIFIED);
        propertyNameMap.put (P_LAST_SAVE_DATE, TikaProperty.LAST_SAVE_DATE);
        propertyNameMap.put (P_META_CREATION_DATE, TikaProperty.META_CREATION_DATE);
        propertyNameMap.put (P_META_SAVE_DATE, TikaProperty.META_SAVE_DATE);
        propertyNameMap.put (P_MODIFIED, TikaProperty.MODIFIED);
        propertyNameMap.put (P_NPAGES, TikaProperty.N_PAGES);
        propertyNameMap.put (P_PRODUCER, TikaProperty.PRODUCER);
        propertyNameMap.put (P_RESOURCE_NAME, TikaProperty.RESOURCE_NAME);
        propertyNameMap.put (P_SUBJECT, TikaProperty.SUBJECT);
        propertyNameMap.put (P_TIFF_BITS_PER_SAMPLE, TikaProperty.TIFF_BITS_PER_SAMPLE);
        propertyNameMap.put (P_TIFF_IMAGE_LENGTH, TikaProperty.TIFF_IMAGE_LENGTH);
        propertyNameMap.put (P_TIFF_IMAGE_WIDTH, TikaProperty.TIFF_IMAGE_WIDTH);
        propertyNameMap.put (P_TIFF_RESOLUTION_UNIT, TikaProperty.TIFF_RESOLUTION_UNIT);
        propertyNameMap.put (P_TITLE, TikaProperty.TITLE);
        propertyNameMap.put (P_WORD_COUNT, TikaProperty.WORD_COUNT);
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
        
        /* Convert the metadata map into an indexed map */
        // convert the information in metadata to FITS output.
        String [] propertyNames = metadata.names();
        // TODO DEBUG: look through these values to better understand what Tika returns.
//        for (String name : propertyNames) {
//            String value = metadata.get (name);
//            System.out.println (name + ": " + value);
//        }
        
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
        String mimeType = metadata.get (P_CONTENT_TYPE);


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
	

	private Element buildFileInfoElement (Metadata metadata) {
        String lastModified = metadata.get (P_LAST_MODIFIED);
        String contentLength = metadata.get (P_CONTENT_LENGTH);
        String resourceName = metadata.get (P_RESOURCE_NAME);
        String appName = metadata.get (P_APPLICATION_NAME);
        String creatorApp = metadata.get (P_CREATOR_TOOL);

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
        else if (creatorApp != null) {
            Element appNameElem = new Element ("creatingApplicationName", fitsNS);
            appNameElem.addContent (creatorApp);
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
	        
	        // convert to FITS standard file types
	        String stdText = FitsMetadataValues.mimeToDescMap.get(mime);
	        if (stdText != null) {
	            format = stdText;
	        }
//	        if (mime.startsWith("image/jpeg")) {
//	            format = "JPEG File Interchange Format";
//	        }
	        return format;
	    } catch (MimeTypeException e) {
	        throw new FitsToolException("Tika error looking up mime type");

	    }
	}
	

   private Element buildMetadataElement (Metadata metadata, String mimeType) {
       DocumentTypes.Doctype doctype = DocumentTypes.mimeToDoctype(mimeType);
       Element metadataElem = new Element ("metadata", fitsNS);
       switch (doctype) {
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
       default:
           break;
       }
       return metadataElem;  
   }

	/* Return an element for an image file */
	private Element buildImageElement(Metadata metadata) {
        String[] metadataNames = metadata.names();
	    Element elem = new Element (FitsMetadataValues.IMAGE, fitsNS);
	    for (String name : metadataNames) {
	        TikaProperty prop = propertyNameMap.get(name);
	        if (prop == null) {
	            // a property we don't know about?
	            continue;
	        }
	        String value = metadata.get(name);
	        
	        int idx;
	        switch (prop) {
	        case IMAGE_WIDTH:
	        case TIFF_IMAGE_WIDTH:
	            idx = value.indexOf (" pixels");
	            if (idx > 0) {
	                value = value.substring (0, idx);
	            }
                addSimpleElement (elem, FitsMetadataValues.IMAGE_WIDTH, value);
	            break;
	            
	        case IMAGE_HEIGHT:
	        case TIFF_IMAGE_LENGTH:
	            idx = value.indexOf (" pixels");
	            if (idx > 0) {
	                value = value.substring (0, idx);
	            }
	            addSimpleElement (elem, FitsMetadataValues.IMAGE_HEIGHT, value);
	            break;

	        case COMPRESSION_TYPE:
	            addSimpleElement (elem, FitsMetadataValues.COMPRESSION_SCHEME, value);
	            break;
	            
	        case BITS_PER_SAMPLE:
	        case TIFF_BITS_PER_SAMPLE:
                addSimpleElement (elem, FitsMetadataValues.BITS_PER_SAMPLE, value);
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
        for (String name : metadataNames) {
            TikaProperty prop = propertyNameMap.get(name);
            if (prop == null) {
                // a property we don't know about?
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
                
            case LAST_MODIFIED:
                // TODO do something here
                break;
                
            case SUBJECT:
                addSimpleElement (elem, FitsMetadataValues.SUBJECT, value);
                break;
                
            case CREATION_DATE:
            case META_CREATION_DATE:
                //TODO becomes what?
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
                // a property we don't know about?
                continue;
            }
            String value = metadata.get(name);
            
            switch (prop) {
            case WORD_COUNT:
                addSimpleElement (elem, FitsMetadataValues.WORD_COUNT, value);
                break;
            }
        }
//        String wc = metadata.get ("Word-Count");
//        if (wc != null) {
//            Element wcElem = new Element ("wordCount", fitsNS);
//            wcElem.addContent (wc);
//            elem.addContent (wcElem);
//        }
        return elem;
    }

    private void addSimpleElement (Element parent, String tag, String value ) {
        Element newElem = new Element (tag, fitsNS);
        newElem.addContent (value);
        parent.addContent (newElem);
    }
    
}

package edu.harvard.hul.ois.fits.tools.tika;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.utils.XmlUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
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

    enum Doctype {
        UNKNOWN,
        DOCUMENT,
        IMAGE,
        TEXT
    };
    
    private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);
    private final static String TOOL_NAME = "Tika";
    private final static String TOOL_VERSION = "1.3";  // Hard-coded version till we can do better
    
    private final static MediaTypeRegistry typeRegistry = MediaTypeRegistry.getDefaultRegistry();
    private final static MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
    private Tika tika = new Tika ();
    
    private boolean enabled = true;

    public TikaTool() throws FitsToolException {
        info = new ToolInfo(TOOL_NAME, TOOL_VERSION,"");
    }

    public ToolOutput extractInfo(File file) throws FitsToolException {
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
        String mimeType = metadata.get ("Content-Type");
        String wordCountStr = metadata.get ("Word-Count"); 

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
        String lastModified = metadata.get ("Last-Modified");
        String contentLength = metadata.get ("Content-Length");
        String resourceName = metadata.get ("resourceName");
        String appName = metadata.get ("Application-Name");
        

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
	        
	        // HACK HACK HACK convert to FITS standard file types
	        if (mime.startsWith("image/jpeg")) {
	            format = "JPEG File Interchange Format";
	        }
	        return format;
	    } catch (MimeTypeException e) {
	        throw new FitsToolException("Tika error looking up mime type");

	    }
	}
	
	/* Select a document type based on the MIME type. 
	 * This should be made more elegant. */
	private Doctype mimeToDoctype (String mime) {
	    if (mime.startsWith("image/jpeg") || 
	            mime.startsWith ("image/png")) {
	        return Doctype.IMAGE;
	    }
	    else if (mime.startsWith("application/pdf")) {
	        return Doctype.DOCUMENT;
	    }
	    else if (mime.startsWith("text/plain")) {
	        return Doctype.TEXT;
	    }
	    else {
	        return Doctype.UNKNOWN;
	    }
	}

   private Element buildMetadataElement (Metadata metadata, String mimeType) {
       Doctype doctype = mimeToDoctype(mimeType);
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
	    Element elem = new Element ("image", fitsNS);
	    String imgWidth = metadata.get ("Image Width");
	    if (imgWidth != null) {
	        Element wElem = new Element ("imageWidth", fitsNS);
	        wElem.addContent (imgWidth);
	        elem.addContent (wElem);
	    }
	    String imgHeight = metadata.get ("Image Height");
        if (imgHeight != null) {
            Element hElem = new Element ("imageHeight", fitsNS);
            hElem.addContent (imgHeight);
            elem.addContent (hElem);
        }
	    return elem;
	}
	
   /* Return an element for an document file */
    private Element buildDocElement(Metadata metadata) {
        Element elem = new Element ("document", fitsNS);
        String title = metadata.get ("title");
        if (title != null) {
            Element titleElem = new Element ("title", fitsNS);
            titleElem.addContent (title);
            elem.addContent (titleElem);
        }
        String author = metadata.get ("Author");
        if (author != null) {
            Element authElem = new Element ("author", fitsNS);
            authElem.addContent (author);
            elem.addContent (authElem);
        }
        return elem;
   // TODO stub
    }

    /* Return an element for a text file */
    private Element buildTextElement(Metadata metadata) {
        Element elem = new Element ("text", fitsNS);
        return elem;
   // TODO stub
    }

    

}

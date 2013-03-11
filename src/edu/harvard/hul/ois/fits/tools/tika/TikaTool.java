package edu.harvard.hul.ois.fits.tools.tika;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolInfo;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaTypeRegistry;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class TikaTool extends ToolBase {

    private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);
    private final static String TOOL_NAME = "Tika";
    private final static String TOOL_VERSION = "1.3";  // Hard-coded version till we can do better
    
    private final static MediaTypeRegistry typeRegistry = MediaTypeRegistry.getDefaultRegistry();
    private boolean enabled = true;

    public TikaTool() throws FitsToolException {
        info = new ToolInfo(TOOL_NAME, TOOL_VERSION,"");
    }

    public ToolOutput extractInfo(File file) throws FitsToolException {
        Tika tika = new Tika ();
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
        for (String name : propertyNames) {
            String value = metadata.get (name);
            System.out.println (name + ": " + value);
        }
        
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
	private Document buildToolData (Metadata metadata) {
        String mimeType = metadata.get ("Content-Type");
        String wordCountStr = metadata.get ("Word-Count");
        String appName = metadata.get ("Application-Name");
        String lastModified = metadata.get ("Last-Modified");
        String contentLength = metadata.get ("Content-Length");
        String resourceLength = metadata.get ("resourceName");
        
        Element fitsElem = new Element ("fits", fitsNS);
        Element idElem = new Element ("identification", fitsNS);
        fitsElem.addContent(idElem);
        Element identityElem = new Element ("identity", fitsNS);
        
        // Format and mime type info. 
        // TODO create real format name
        Attribute attr = new Attribute ("format", mimeType);
        identityElem.setAttribute (attr);
        attr = new Attribute ("mimetype", mimeType);
        identityElem.setAttribute (attr);
        idElem.addContent (identityElem);
        Document toolDoc = new Document (fitsElem);

        // Put together the fileinfo element
        Element fileInfoElem = new Element ("fileinfo", fitsNS);
        fitsElem.addContent (fileInfoElem);
        if (lastModified != null) {
            Element lastModElem = new Element ("lastmodified", fitsNS);
            lastModElem.addContent (lastModified);
            fileInfoElem.addContent (lastModElem);
        }
        return toolDoc;
	}
	
	/* Create a dummy raw data object */
	public Document buildRawData (Metadata metadata) throws FitsToolException {
	    String xml = MetadataFormatter.toXML(metadata);
	    StringReader srdr = new StringReader (xml);
	    try {
	        Document rawDoc = saxBuilder.build (srdr);
	        return rawDoc; 
	    }
	    catch (Exception e) {
	        throw new FitsToolException ("Exception reading metadata", e);
	    }
	}
	

}

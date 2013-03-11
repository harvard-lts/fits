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
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class TikaTool extends ToolBase {

    private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);
    private final static String TOOL_NAME = "Tika";
    private boolean enabled = true;

    public TikaTool() throws FitsToolException {
        info = new ToolInfo(TOOL_NAME,"1.3","");
    }

    public ToolOutput extractInfo(File file) throws FitsToolException {
//        TikaWrapper wrapper = TikaWrapper.getTika();
        Tika tika = new Tika ();
        Metadata metadata = new Metadata(); // = new Metadata();
        FileInputStream instrm = null;
        try {
            instrm = new FileInputStream (file);
        }
        catch (FileNotFoundException e) {
            throw new FitsToolException ("Can't open file", e);
        }
        try {
            tika.parse (instrm, metadata);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // TODO convert the information in metadata to FITS output.
        String [] propertyNames = metadata.names();
        // TODO look through these values to better understand what Tika returns.
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
        Element fitsElem = new Element ("fits", fitsNS);
        Element idElem = new Element ("identification", fitsNS);
        fitsElem.addContent(idElem);
        Element identityElem = new Element ("identity", fitsNS);
        Attribute attr = new Attribute ("format", "");
        identityElem.setAttribute (attr);
        attr = new Attribute ("mimetype", "image/jpeg");
        identityElem.setAttribute (attr);
        idElem.addContent (identityElem);
        Document toolDoc = new Document (fitsElem);

        // TODO put some actual content in
        return toolDoc;
	}
	
	/* Create a dummy raw data object */
	public Document buildRawData (Metadata metadata) throws FitsToolException {
	    String xml = MetadataFormatter.toXML(metadata);
	    StringReader srdr = new StringReader (xml);
	    try {
	        Document rawDoc = saxBuilder.build (srdr);
//	    Element tikaElem = new Element ("tika");
//	    Document rawDoc = new Document (tikaElem);
	        return rawDoc;     // TODO stub
	    }
	    catch (Exception e) {
	        throw new FitsToolException ("Exception reading metadata", e);
	    }
	}

}

package edu.harvard.hul.ois.fits.tools.droid;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataValues;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/** This class generates the tool output for DROID.
 * 
 *  @author <a href="http://www.garymcgath.com">Gary McGath</a>
 */
public class DroidToolOutputter {
    
    private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);

    private IdentificationResultCollection results;
    private Droid toolBase;
    
    public DroidToolOutputter (Droid toolBase, IdentificationResultCollection results) {
        this.toolBase = toolBase;
        this.results = results;
    }
    
    /** Produce a JDOM document with fits as its root element. This
     *  will contain just identification, not metadata elements.
     */
    public ToolOutput toToolOutput () throws FitsToolException {
        List<IdentificationResult> resList = results.getResults();
        Document fitsXml = createToolData ();
        Document rawOut = buildRawData (resList);
        ToolOutput output = new ToolOutput(toolBase,fitsXml,rawOut);
        return output;  
    }
    
    /** Create a base tool data document and add elements
     *  for each format. */
    private Document createToolData () {
        List<IdentificationResult> resList = results.getResults();
        Element fitsElem = new Element ("fits", fitsNS);
        Document toolDoc = new Document (fitsElem);
        Element idElem = new Element ("identification", fitsNS);
        fitsElem.addContent(idElem);
        for (IdentificationResult res : resList) {
            String filePuid = res.getPuid();
            String formatName = res.getName();
            formatName = mapFormatName(formatName);
            String mimeType = res.getMimeType();
            
            if(FitsMetadataValues.getInstance().normalizeMimeType(mimeType) != null) {
            	mimeType = FitsMetadataValues.getInstance().normalizeMimeType(mimeType); 
            }
            
            if(formatName.equals("Digital Negative (DNG)")) {
            	mimeType="image/x-adobe-dng";
            }
            
            String version = res.getVersion();
            version = mapVersion(version);
            
            Element identityElem = new Element ("identity", fitsNS);
            Attribute attr = null;
            if (formatName != null) {
                attr = new Attribute ("format", formatName);
                identityElem.setAttribute(attr);
            }
            if (mimeType != null) {
                attr = new Attribute ("mimetype", mimeType);
                identityElem.setAttribute (attr);
            }
            // Is there anything to put into the fileinfo or metadata elements?
            // Both are optional, so they can be left out if they'd be empty.
            idElem.addContent (identityElem);
            
            // If there's a version, report it
            if (version != null) {
                Element versionElem = new Element ("version", fitsNS);
                identityElem.addContent(versionElem);
                versionElem.addContent (version);
            }

            // If there's a PUID, report it as an external identifier
            if (filePuid != null) {
                Element puidElem = new Element ("externalIdentifier", fitsNS);
                identityElem.addContent (puidElem);
                puidElem.addContent (filePuid);
                attr = new Attribute ("type", "puid");
                puidElem.setAttribute (attr);
            }
        }

        return toolDoc;     // TODO stub
    }
    
    private String mapFormatName(String formatName) {
    	
    	if(formatName == null || formatName.length() == 0) {
    		return FitsMetadataValues.DEFAULT_FORMAT;
    	}
    	else if(formatName.startsWith("JPEG2000") || formatName.startsWith("JP2 (JPEG 2000")) {
    		return "JPEG 2000 JP2";
    	}
    	else if(formatName.startsWith("Exchangeable Image File Format (Compressed)")) {
    		return "Exchangeable Image File Format";
    	}    	
    	else if(formatName.contains("PDF/A")) {
    		return "PDF/A";
    	}
    	else if(formatName.contains("PDF/X")) {
    		return "PDF/X";
    	}
    	else if(formatName.contains("Portable Document Format")) {
    		return "Portable Document Format";
    	}
    	else if(formatName.startsWith("Microsoft Excel")) {
    		return "Microsoft Excel";
    	}
    	else if(FitsMetadataValues.getInstance().normalizeFormat(formatName) != null){
    		return FitsMetadataValues.getInstance().normalizeFormat(formatName);
    	}
    	else {
    		return formatName;
    	}
    }
    
    private String mapVersion(String version) {
    	
    	if(version == null || version.length() == 0) {
    		return version;
    	}
    	else if(version.equals("1987a")) {
    		return "87a";
    	}
    	else {
    		return version;
    	}
    }
    
    /**
     * Create "raw" XML. The DROID namespace is no longer meaningful. Does this have any
     * particular requirements beyond dumping as much data as might be useful?
     * 
     * @throws FitsToolException 
     * @throws SAXException 
     */
    private Document buildRawData (List<IdentificationResult> resList) throws FitsToolException {
            
            StringWriter out = new StringWriter();
            
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.write("\n");
            out.write("<results>");
            out.write("\n");
            for (IdentificationResult res : resList) {
                String filePuid = res.getPuid();
                String formatName = res.getName();
                String mimeType = res.getMimeType();
                String version = res.getVersion();
                out.write("<result>");
                out.write("\n");
                out.write("<filePuid>" + filePuid + "</filePuid>");
                out.write("\n");
                out.write("<formatName>" + formatName + "</formatName>");
                out.write("\n");
                out.write("<mimeType>" + mimeType + "</mimeType>");
                out.write("\n");
                out.write("<version>" + version + "</version>");
                out.write("</result>");
            }

            out.write("  </results>");
            out.write("\n");

            out.flush();
         
            try {
                out.close();
            } catch (IOException e) {
                throw new FitsToolException("Error closing DROID XML output stream",e);
            }
            
            Document doc = null;
            try {
                SAXBuilder saxBuilder = toolBase.getSaxBuilder();
                doc = saxBuilder.build(new StringReader(out.toString()));
            } catch (Exception e) {
                throw new FitsToolException("Error parsing DROID XML Output",e);
            } 
            return doc;
        }    
    
    /* Change any MIME types that need to be normalized. */
    private String mimeToFileType (String mime) {
        return mime;       // TODO stub
    }
}

//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.droid;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataValues;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;

/**
 * This class generates the tool output for DROID.
 *
 * @author <a href="http://www.garymcgath.com">Gary McGath</a>
 */
public class DroidToolOutputter {

    private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);
    private final static Map<Integer, String> COMPRESSION_METHOD_TO_STRING_VALUE;
    
	private static Logger logger = Logger.getLogger(DroidToolOutputter.class);

    private IdentificationResultCollection results;
    private ToolBase toolBase;
    private Fits fits;
    private ContainerAggregator aggregator; // could be null!!!
    
    static {
    	COMPRESSION_METHOD_TO_STRING_VALUE = new HashMap<>();
    	COMPRESSION_METHOD_TO_STRING_VALUE.put(ZipEntry.STORED, "stored");
    	COMPRESSION_METHOD_TO_STRING_VALUE.put(ZipEntry.DEFLATED, "deflate");
    }

    public DroidToolOutputter (ToolBase toolBase, IdentificationResultCollection results, Fits fits, ContainerAggregator aggregator) {
        this.toolBase = toolBase;
        this.results = results;
        this.fits = fits;
        this.aggregator = aggregator;
    }

    /** Produce a JDOM document with fits as its root element. This
     *  will contain just identification, not metadata elements.
     */
    public ToolOutput toToolOutput () throws FitsToolException {
        List<IdentificationResult> resList = results.getResults();
        Document fitsXml = createToolData ();
        Document rawOut = buildRawData (resList);
        ToolOutput output = new ToolOutput(toolBase,fitsXml,rawOut, fits);
        return output;
    }

    /** Create a base tool data document and add elements
     *  for each format. */
    private Document createToolData () throws FitsToolException {
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

            // maybe this block should be moved to mapFormatName() ???
            if(formatName.equals("Digital Negative (DNG)")) {
            	mimeType="image/x-adobe-dng";
            } else if (formatName.equals("Office Open XML Document")) {
            	mimeType="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
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
        
        // The only time there will be a metadata section from DROID is when
        // there is an aggregator for ZIP files and there are file entries.
        if (aggregator != null && aggregator.getTotalEntriesCount() > 0) {
        	Element metadataElem = new Element ("metadata", fitsNS);
        	fitsElem.addContent(metadataElem);
        	Element containerElem = new Element ("container", fitsNS);
    		metadataElem.addContent(containerElem);
        	
        	Element origSizeElem = new Element("originalSize", fitsNS);
    		origSizeElem.addContent( String.valueOf(aggregator.getOriginalSize()) );
    		containerElem.addContent(origSizeElem);
        	
        	Element compressionMethodElem = new Element("compressionMethod", fitsNS);
    		compressionMethodElem.addContent( COMPRESSION_METHOD_TO_STRING_VALUE.get( aggregator.getCompressionMethod() ) );
    		containerElem.addContent(compressionMethodElem);
        	
        	Element entriesElem = new Element("entries", fitsNS);
    		Attribute totalEntriesCountAttr = new Attribute("totalEntries", String.valueOf(aggregator.getTotalEntriesCount()) );
			entriesElem.setAttribute(totalEntriesCountAttr);
    		containerElem.addContent(entriesElem);
        	
        	for ( Map.Entry<String, Integer> formatEntry : aggregator.getFormatCounts().entrySet() ) {
        		Element entryElem = new Element("format", fitsNS);
        		Attribute nameAttr = new Attribute("name", formatEntry.getKey());
        		entryElem.setAttribute(nameAttr);
        		
        		Attribute numberAttr = new Attribute("number", String.valueOf(formatEntry.getValue()) );
        		entryElem.setAttribute(numberAttr);
        		
        		entriesElem.addContent(entryElem);
        	}
        }


        return toolDoc;
    }

    public static String mapFormatName(String formatName) {

    	if(formatName == null || formatName.length() == 0) {
    		return FitsMetadataValues.DEFAULT_FORMAT;
    	}
    	else if(formatName.startsWith("JPEG2000") || formatName.startsWith("JP2 (JPEG 2000")) {
    		return "JPEG 2000 JP2";
    	}
    	else if(formatName.startsWith("Exchangeable Image File Format (Compressed)")) {
    		return "JPEG EXIF";
    	}
    	else if(formatName.startsWith("Exchangeable Image File Format (Uncompressed)")) {
    		return "TIFF EXIF";
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
            
            if (aggregator != null && aggregator.getTotalEntriesCount() > 0) {
            	out.write("<container originalSize='");
            	out.write( String.valueOf(aggregator.getOriginalSize()) );
            	
            	String method = COMPRESSION_METHOD_TO_STRING_VALUE.get( aggregator.getCompressionMethod() );
            	out.write("' method='");
            	out.write(method);
            	
            	out.write("'>");
                out.write("\n");
            	
            	out.write("<entries totalEntries='");
            	out.write( String.valueOf(aggregator.getTotalEntriesCount()) );
            	out.write("'>");
                out.write("\n");

                for ( Map.Entry<String, Integer> entry : aggregator.getFormatCounts().entrySet() ) {
                	out.write("<entry formatName='");
                	out.write(entry.getKey());
                	out.write("' count='");
                	out.write( String.valueOf(entry.getValue()) );
                	out.write("'></entry>");
                	out.write("\n");
                }
            	out.write("</entries>");
            	out.write("\n");
            	
            	out.write("</container>");
            	out.write("\n");
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
}

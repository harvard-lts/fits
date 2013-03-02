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
package edu.harvard.hul.ois.fits.tools.droid;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import org.jdom.Document;
import org.xml.sax.SAXException;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.FileFormatHit;
import uk.gov.nationalarchives.droid.IdentificationFile;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

public class Droid extends ToolBase {

	private uk.gov.nationalarchives.droid.Droid droid = null;
	public final static String xslt = Fits.FITS_HOME+"xml/droid/droid_to_fits.xslt";
	private boolean enabled = true;
	
	public Droid() throws FitsToolException {

		info = new ToolInfo("Droid",AnalysisController.DROID_VERSION,null);		

		try {
			String droid_conf = Fits.FITS_TOOLS+"droid"+File.separator;
			URL droidConfig = new File(droid_conf+"DROID_config.xml").toURI().toURL();
			URL sigFile = new File(droid_conf+Fits.config.getString("droid_sigfile")).toURI().toURL();
			// The Droid(URL configFile, URL sigFileURL) constructor is broken
			//  So create droid instance and read signature file manually.
			droid = new uk.gov.nationalarchives.droid.Droid(droidConfig);
			droid.readSignatureFile(sigFile);
		} catch (Exception e) {
			throw new FitsToolException("Error initilizing DROID",e);
		}
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
		IdentificationFile idFile = droid.identify(file.getPath());
		/*List<FileIdentity> identities = new ArrayList();
		for(int i=0;i<idFile.getNumHits();i++) {
			FileFormatHit hit = idFile.getHit(i);
			FileIdentity identity = new FileIdentity(hit.getMimeType(),hit.getFileFormatName(),hit.getFileFormatVersion());
			//pronom id;
			identity.addExternalIdentifier("puid",hit.getFileFormatPUID());
			identities.add(identity);
		}	*/
		Document rawOut = createXml(idFile);
		Document fitsXml = transform(xslt, rawOut);
		
		/*
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			outputter.output(fitsXml, System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		output = new ToolOutput(this,fitsXml,rawOut);
		return output;
	}

    /**
     * Write the XML to the file, using the new schema format with elements for most of the data.
     * @throws FitsToolException 
     * @throws SAXException 
     */
    private Document createXml(IdentificationFile idFile) throws FitsToolException {
    	
    	StringWriter out = new StringWriter();
    	
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("\n");
        out.write("<FileCollection xmlns=\"" + AnalysisController.FILE_COLLECTION_NS + "\">");
        out.write("\n");
        out.write("  <DROIDVersion>" + AnalysisController.getDROIDVersion().replaceAll("&", "&amp;") + "</DROIDVersion>");
        out.write("\n");
        out.write("  <SignatureFileVersion>" + droid.getSignatureFileVersion() + "</SignatureFileVersion>");
        out.write("\n");
        out.write("  <DateCreated>" + AnalysisController.writeXMLDate(new java.util.Date()) + "</DateCreated>");
        out.write("\n");

        //create IdentificationFile element and its attributes
        out.write("  <IdentificationFile ");
        out.write("IdentQuality=\"" + idFile.getClassificationText() + "\" ");
        out.write(">");
        out.write("\n");
        out.write("    <FilePath>" + idFile.getFilePath().replaceAll("&", "&amp;") + "</FilePath>");
        out.write("\n");
        if (/*saveResults && */!"".equals(idFile.getWarning())) {
            out.write("    <Warning>" + idFile.getWarning().replaceAll("&", "&amp;") + "</Warning>");
            out.write("\n");
        }
        
        if(idFile.getNumHits() == 0) {
            out.write("    <FileFormatHit>");
            out.write("\n");        	
            out.write("    </FileFormatHit>");
            out.write("\n");
        }
        else {
	        //now create an FileFormatHit element for each hit
	        for (int hitCounter = 0; hitCounter < idFile.getNumHits(); hitCounter++) {
	            FileFormatHit formatHit = idFile.getHit(hitCounter);
	            out.write("    <FileFormatHit>");
	            out.write("\n");
	            out.write("      <Status>" + formatHit.getHitTypeVerbose() + "</Status>");
	            out.write("\n");
	            out.write("      <Name>" + formatHit.getFileFormatName().replaceAll("&", "&amp;") + "</Name>");
	            out.write("\n");
	            if (formatHit.getFileFormatVersion() != null) {
	                out.write("      <Version>" + formatHit.getFileFormatVersion().replaceAll("&", "&amp;") + "</Version>");
	                out.write("\n");
	            }
	            if (formatHit.getFileFormatPUID() != null) {
	                out.write("      <PUID>" + formatHit.getFileFormatPUID().replaceAll("&", "&amp;") + "</PUID>");
	                out.write("\n");
	            }
	            if (formatHit.getMimeType() != null) {
	                out.write("      <MimeType>" + formatHit.getMimeType().replaceAll("&", "&amp;") + "</MimeType>");
	                out.write("\n");
	            }
	            if (!"".equals(formatHit.getHitWarning())) {
	                out.write("      <IdentificationWarning>"
	                        + formatHit.getHitWarning().replaceAll("&", "&amp;") + "</IdentificationWarning>");
	                out.write("\n");
	            }
	            out.write("    </FileFormatHit>");
	            out.write("\n");
	        }//end file hit FOR        
        }

        //close IdentificationFile element
        out.write("  </IdentificationFile>");
        out.write("\n");

        //close FileCollection element
        out.write("</FileCollection>");
        out.write("\n");

        out.flush();
     
		try {
			out.close();
		} catch (IOException e) {
			throw new FitsToolException("Error closing DROID XML output stream",e);
		}
		
        Document doc = null;
		try {
			doc = saxBuilder.build(new StringReader(out.toString()));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing DROID XML Output",e);
		} 
        return doc;
    }

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}
    
}

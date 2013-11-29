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
package edu.harvard.hul.ois.fits.tools.ffident;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.apache.log4j.Logger;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

/** The main class of the FFIdent tool, adapted for use under FITS.
 *  Uses tools/ffident/formats.txt
 */
public class FFIdent extends ToolBase {
	
    private final static String TOOL_NAME = "ffident";
    private final static String TOOL_VERSION = "0.2";
    private final static String TOOL_DATE = "2005-10-21";
            
	private FormatIdentification identifier = null;
	public final static String xslt =Fits.FITS_XML+"/ffident/ffident_to_fits.xslt";
	private boolean enabled = true;

    private static final Logger logger = Logger.getLogger(FFIdent.class);

	public FFIdent() throws FitsToolException{

        logger.debug ("Initializing FFIdent");
		info = new ToolInfo(TOOL_NAME, TOOL_VERSION, TOOL_DATE);
		
		try {
			File config = new File(Fits.FITS_TOOLS+"ffident/formats.txt");
			identifier = new FormatIdentification(config.getPath());
		} catch (FileNotFoundException e) {
			throw new FitsToolException(Fits.FITS_TOOLS+"ffident/formats.txt could not be found",e);
		} 
	}
	
	public ToolOutput extractInfo(File file) throws FitsToolException {
	    logger.debug ("FFIdent.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		FormatDescription desc = identifier.identify(file);
		//FileIdentity identity = null;
		Document rawOut = null;
		Document fitsXml = null;
		//if (desc != null) {
			//desc.getShortName();
			//desc.getMimeTypes();
			//identity = new FileIdentity(desc.getMimeType(),desc.getShortName(),null);
			rawOut = createXml(desc);
			fitsXml = transform(xslt,rawOut);
		//}
		output = new ToolOutput(this,fitsXml,rawOut);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug ("FFIdent.extractInfo finished on " + file.getName());
		return output;
	}
	
   private Document createXml(FormatDescription desc) throws FitsToolException {    	
    	StringWriter out = new StringWriter();
    	
    	String shortname;
    	String longname;
    	String group;
    	List<String> mimetypes = new ArrayList<String>();
    	List<String> exts = new ArrayList<String>(); 
    	
    	if(desc == null) {
    		shortname = "";
    		longname = "Unknown Binary";
    		group = "";
    		mimetypes.add("application/octet-stream");   
    	}
    	else {
    		shortname = desc.getShortName();
    		longname = desc.getLongName();
    		group = desc.getGroup();
    		mimetypes = desc.getMimeTypes();
    		exts = desc.getFileExtensions();
    	}
    	
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("\n");
        out.write("<ffidentOutput>");	   
        out.write("\n");
        out.write("  <shortName>"+shortname+"</shortName>");
        out.write("\n");
        out.write("  <longName>"+longname+"</longName>");
        out.write("\n");
        out.write("  <group>"+group+"</group>");
        out.write("\n");
        out.write("  <mimetypes>");
        out.write("\n");
        for(String mime : mimetypes) {
        	out.write("    <mimetype>"+mime+"</mimetype>");
        	out.write("\n");
        }
        out.write("  </mimetypes>");
        out.write("\n");
        
        out.write("  <fileExtensions>");
        out.write("\n");
        for(String ext : exts) {
        	out.write("    <extension>"+ext+"</extension>");
        	out.write("\n");
        }
        out.write("  </fileExtensions>");
        out.write("\n");
        out.write("</ffidentOutput>");
        out.write("\n");
        out.flush();
        try {
			out.close();
		} catch (IOException e) {
		    logger.debug ("Error closing ffident XML output stream: " + e.getClass().getName());
			throw new FitsToolException("Error closing ffident XML output stream",e);
		}
		
        Document doc = null;
		try {
			doc = saxBuilder.build(new StringReader(out.toString()));
		} catch (Exception e) {
		    logger.debug("Error parsing ffident XML Output: " + e.getClass().getName());
			throw new FitsToolException("Error parsing ffident XML Output",e);
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

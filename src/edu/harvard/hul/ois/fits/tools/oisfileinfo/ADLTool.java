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
package edu.harvard.hul.ois.fits.tools.oisfileinfo;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.apache.log4j.Logger;

import com.therockquarry.aes31.adl.ADL;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

/**
 * This uses the audio file parsing library hclaps.jar provided by Dave Ackerman  
 * @author spencer
 *
 */
public class ADLTool extends ToolBase {
	
	private boolean enabled = true;
	private final static Namespace fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);
	private final static Namespace xsiNS = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
    private static final Logger logger = Logger.getLogger(ADLTool.class);
    
	public ADLTool() throws FitsToolException {
		info.setName("ADL Tool");
		info.setVersion("0.1");
		info.setDate("10/24/11");
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {	
        logger.debug("ADLTool.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		Document doc = createXml(file);
		output = new ToolOutput(this,(Document)doc.clone(),doc);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		logger.debug("ADLTool.extractInfo finishing on " + file.getName());
		return output;
	}
	
	private Document createXml(File file) throws FitsToolException {

		Element root = new Element("fits",fitsNS);
		root.setAttribute(new Attribute("schemaLocation","http://hul.harvard.edu/ois/xml/ns/fits/fits_output "+Fits.externalOutputSchema,xsiNS));
		
		if(file.getPath().toLowerCase().endsWith(".adl")) {	
			
			String adlVersion = null;
			String adlCreator = null;
			String adlCreatorVersion = null;
			
			try	{
				//creating an ADL objecte should be enough to validate it as ADL
				new ADL(file);
								
				Scanner scanner = new Scanner(new FileReader(file));
			    try {
			      //first use a Scanner to get each line
			      while ( scanner.hasNextLine() ){
			    	  String line = scanner.nextLine();
			    	  if(line.contains("(VER_ADL_VERSION)")) {
			    		  adlVersion = getLineVal(line);
			    	  }
			    	  else if(line.contains("(VER_CREATOR)")) {
			    		  adlCreator = getLineVal(line);
			    	  }
			    	  else if(line.contains("(VER_CRTR)")) {
			    		  adlCreatorVersion = getLineVal(line);
			    	  }
			      }
			    }
			    finally {
			      //ensure the underlying stream is always closed
			      scanner.close();
			    }
			}
			catch (Exception e)	{
				throw new FitsToolException("Error parsing ADL file", e);
			}
			
			Element identification = new Element("identification",fitsNS);
			Element identity = new Element("identity",fitsNS);
			identity.setAttribute("format","Audio Decision List");
			identity.setAttribute("mimetype","text/x-adl");
			Element version = new Element("version",fitsNS);
			version.addContent(adlVersion);
			identity.addContent(version);
			//add identity to identification section
			identification.addContent(identity);
			//add identification section to root
			root.addContent(identification);
			
			Element fileInfo = new Element("fileinfo",fitsNS);
			Element metadata = new Element("metadata",fitsNS);
			Element textMetadata = new Element("text",fitsNS);
			
			Element markupLanguage = new Element("markupLanguage",fitsNS);
			markupLanguage.addContent("EDML");
			textMetadata.addContent(markupLanguage);
			
			if(adlVersion!=null) {				
				Element markupLanguageVer = new Element("markupLanguageVersion",fitsNS);
				markupLanguageVer.addContent(adlVersion);
				textMetadata.addContent(markupLanguageVer);
			}
			if(adlCreator!=null) {
				Element elem = new Element("creatingApplicationName",fitsNS);
				elem.addContent(adlCreator);
				fileInfo.addContent(elem);
			}
			
			if(adlCreatorVersion!=null) {
				Element elem = new Element("creatingApplicationVersion",fitsNS);
				elem.addContent(adlCreatorVersion);
				fileInfo.addContent(elem);
			}		
			
			//add file info section to root
			root.addContent(fileInfo);
			//add to metadata section
			metadata.addContent(textMetadata);
			//add metadata section to root
			root.addContent(metadata);
		}
				
		return new Document(root);
    
	
	}
	
	private String getLineVal(String line) {
		String[] parts = line.split("\t");
		return parts[parts.length-1];
	}

	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}
	
}

/* 
 * Copyright 2014 Harvard University Library
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
package edu.harvard.hul.ois.fits.tools.mediainfo;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.XsltTransformMap;

public class MediaInfo extends ToolBase {

	private final static String TOOL_NAME = "MediaInfo";
	private boolean enabled = true;
	
	public final static String mediaInfoFitsConfig = Fits.FITS_XML+"mediainfo"+File.separator;
	public final static String genericTransform = "mediainfo_generic_to_fits.xslt";
	public final static String xsltTransformEbu = "mediainfo_ebu_to_fits.xslt";	
	
    private static final Logger logger = Logger.getLogger(MediaInfo.class);
    private static MediaInfoNativeWrapper mi = null;
	
	public MediaInfo() throws FitsException {
		
		info = new ToolInfo();
		info.setName(TOOL_NAME);

//		// Set the JNA library path based upon the OS
//		String osName = System.getProperty("os.name");		
//		if(osName != null && osName.toLowerCase().startsWith("mac")) { // Mac OS X
//			// TODO: Should we externalize the location and names of the native libs?
//			System.setProperty("jna.library.path", "./tools/mediainfo/mac");			
//		} else if(osName != null && osName.toLowerCase().startsWith("windows")) {
//			// TODO: Handle Windows 7
//		} else {
//			// TODO: Handle LINUX OS
//		}		
		
		// Set the JNA library path based upon the OS		
		OsCheck.OSType ostype=OsCheck.getOperatingSystemType();
		switch (ostype) {
		    case Windows:
		    	
				// Determine if the JVM is 64 or 32 bit
				String jvmModel =  System.getProperty("sun.arch.data.model");
				if (jvmModel.equals("32")) {
			    	System.setProperty("jna.library.path", "./tools/mediainfo/windows/32");					
				}
				else {
					System.setProperty("jna.library.path", "./tools/mediainfo/windows/64");
				}
		    	
		    	// 
		    	//boolean is64bitOS = false;
				//if (System.getProperty("os.name").contains("Windows")) {
				//    is64bitOS = (System.getenv("ProgramFiles(x86)") != null);
				//} else {
				//    is64bitOS = (System.getProperty("os.arch").indexOf("64") != -1);
				//}
				//if (!is64bitOS) {
				//	
				//}

		    	break;
		    case MacOS: 
		    	//System.out.println("Mac OS");
	    		System.setProperty("jna.library.path", "./tools/mediainfo/mac");
	    		break;		    
		    case Linux: System.out.println("LINUX OS"); break;
		    case Other: System.out.println("Other OS"); break;
		}		

		try {
		    String versionOutput = MediaInfoNativeWrapper.Option_Static("Info_Version");
		    // Strip "MediaInfoLib - v" from the version
		    // TODO: can we include Apache StringUtils in the project?
		    // versionOutput = versionOutput.removeStart("");
		    // info.setVersion(versionOutput);
		    info.setVersion(versionOutput.replace("MediaInfoLib - v",""));
		    
		    // Initialize the native library
		    mi = new MediaInfoNativeWrapper();
		    
		} catch (java.lang.UnsatisfiedLinkError e){
			throw new FitsToolException("Error loading naive library for " + TOOL_NAME);
		}
		
		transformMap = XsltTransformMap.getMap(mediaInfoFitsConfig+"mediainfo_xslt_map.xml");
	}	
	

	public ToolOutput extractInfo(File file) throws FitsToolException {
		
	   logger.debug("MediaInfo.extractInfo starting on " + file.getName());
	   long startTime = System.currentTimeMillis();		
			
		// TODO: should we initialize the library via a static block?
	    // Initialize the library
	    // MediaInfoViaJNA mi = new MediaInfoViaJNA();
	    
	    // Open the file with mediainfo native library
		try {
		    if (!(mi.Open(file.getCanonicalPath())>0)) {
		    	throw new FitsToolException("Error opening " + file.getName());   	
			}
	    } catch (Exception e) {
		    throw new FitsToolException("Error opening " + file.getName() ,e);
	    } 
	    
	    // Set the option:	    
	    // Complete details
	    // mi.Option("Complete", "1");
	    
	    // Complete = false, use a subset
	    mi.Option("Complete", "");    
	    
	    // Get MediaInfoLib Output as standard XML
	    mi.Option("Output", "XML");
	    String execOut = mi.Inform();
	    
	    
	    // Get MediaInfoLib Output as EBUCore 1.5
	    mi.Option("Output", "EBUCore_1.5");
	    String ebuOut = mi.Inform();
	    
	    mi.Close();

		// ====================================================================		    
	    // Create a Document out of the generated XML text
	    // And transform via XSLT
		// ====================================================================	
		Document rawOut = createXml(execOut); 			
		
		// TODO: HOW DO WE GET THE FORMAT ... or do we care for video?
		String format = null;
		
		String xsltTransform = null;
		if(format != null) {
			xsltTransform = (String)transformMap.get(format.toUpperCase());
		}
		
		// Hack - set to the video transform xslt file
		// TODO: Read this in from the above map
		xsltTransform = "mediainfo_video_to_fits.xslt";
			
		Document fitsXml = null;
		if(xsltTransform != null) {
			fitsXml = transform(mediaInfoFitsConfig+xsltTransform,rawOut);
		}
		else {
			//use generic transform
			fitsXml = transform(mediaInfoFitsConfig+genericTransform,rawOut);
		}
		
//		//
//		// DEBUG - write xml to file
//		//
//		try {
//			XMLOutputter xmlOutput = new XMLOutputter();
//			 
//			// display nice nice
//			xmlOutput.setFormat(Format.getPrettyFormat());			
//			xmlOutput.output(fitsXml, new FileWriter("fitsxml_so_far.xml"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// ====================================================================			
		
		// ====================================================================		
		// Insert the EBUCore node into the fitsXml doc
		// ====================================================================			
		
//		// DEBUG
//		String xmlSoFar_1 = new XMLOutputter(Format.getPrettyFormat()).outputString(fitsXml);
		
		// 1) Transform ebucore xml output from MediaInfoLib
		// 2) Get the ebucore node we wish to append to fits xml document
		// 3) Get an instance of the <standard> node from fits xml document
		// 4) Add the content of the ebucore node to the fits xml document
		
		// 1) Transform ebucore xml output from MediaInfoLib
		Document ebuCore = createXml(ebuOut);
		Document ebuXml = transform(mediaInfoFitsConfig+xsltTransformEbu,ebuCore);
		// DEBUG
		String xmlEbuTransform = new XMLOutputter(Format.getPrettyFormat()).outputString(ebuXml);		
		
		try {
			
			// 2) Get the ebucore node we wish to append to fits xml document
			XPath xpathEbuNode = XPath.newInstance("//wrapper/ebucore:format");
			
			// select the node we wish to append to current fits xml
			List <Element>ebuElementNodes = xpathEbuNode.selectNodes(ebuXml);

			//
			// Do we really need to detach it?
			//
			// Detach the ebu node and its children
			Iterator<Element> iter = ebuElementNodes.iterator();
			while(iter.hasNext()) {
				Element elem = iter.next();
				elem.detach();
			}				

			// 3) Get an instance of the <standard> node from fits xml document
			// We will append the ebucore node to this
			XPath xpathFits = XPath.newInstance("//x:fits/x:metadata/x:video/x:standard");			
			
			// NOTE: We need to add a namespace	to xpath, because JDom XPath 
			// does not support default namespaces. It requires you to add a
			// fake namespace to the XPath instance.
			xpathFits.addNamespace("x", fitsXml.getRootElement().getNamespaceURI());
			Element standardElement = (Element)xpathFits.selectSingleNode(fitsXml);

			// 4) Add the content of the ebucore node to the fits xml document
			if(standardElement != null)
				standardElement.addContent(ebuElementNodes);

		}
		catch(JDOMException e) {
			throw new FitsToolException("Error formatting ebucore xml node " + TOOL_NAME);			
		}			

//		// DEBUG
		String finalXml = new XMLOutputter(Format.getPrettyFormat()).outputString(fitsXml);		
		
		
		// ====================================================================
		
		output = new ToolOutput(this,fitsXml,rawOut);
		
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("MediaInfo.extractInfo finished on " + file.getName());
        
		return output;
	}	
				
	private Document createXml(String out) throws FitsToolException {
        Document doc = null;
		try {
			doc = saxBuilder.build(new StringReader(out));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing Mediainfo XML Output",e);
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

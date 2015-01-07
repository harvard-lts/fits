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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//import edu.harvard.hul.ois.fits.tools.utils.XsltTransformMap;

public class MediaInfo extends ToolBase {

	private final static String TOOL_NAME = "MediaInfo";
	private boolean enabled = true;
	
	public final static String mediaInfoFitsConfig = Fits.FITS_XML+"mediainfo"+File.separator;
	//public final static String genericTransform = "mediainfo_generic_to_fits.xslt";
	public final static String xsltTransform = "mediainfo_video_to_fits.xslt";
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
	    		System.setProperty("jna.library.path", "./tools/mediainfo/mac");
	    		break;		    
		    case Linux: System.out.println("LINUX OS"); break;
		    case Other: System.out.println("Other OS"); break;
		}		

		try {
		    String versionOutput = MediaInfoNativeWrapper.Option_Static("Info_Version");
		    // Strip "MediaInfoLib - v" from the version
		    info.setVersion(versionOutput.replace("MediaInfoLib - v",""));
		    
		    // Initialize the native library
		    mi = new MediaInfoNativeWrapper();
		    
		} catch (java.lang.UnsatisfiedLinkError e){
			throw new FitsToolException("Error loading native library for " + TOOL_NAME);
		}
		
		// TODO: Do we need a transform map for MediaInfo?
		// transformMap = XsltTransformMap.getMap(mediaInfoFitsConfig+"mediainfo_xslt_map.xml");
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
	    
	    // --------------------------------------------------------------------
	    // OUTPUT Options are:
	    // 		(Frpm MediaInfo_Inform.cpp)
	    //
	    // "EBUCore"
	    // "EBUCore_1.5"
	    //    
	    // "MPEG-7"
	    //
	    // "PBCore"
	    // "PBCore_1.2"
	    // "PBCore2"
	    // "PBCore_2.0"
	    //
	    // NOTE: "reVTMD is disabled due to its non-free licensing
	    //
	    // XML
	    // HTML
	    // CSV
	    //
	    // Separate details on various data types/tracks
	    //
	    //	     "General"
	    //	     "Video"
	    //	     "Audio"
	    //	     "Text"
	    //	     "Chapters"
	    //	     Image"
	    //	     "Menu"
	    // --------------------------------------------------------------------

	    // Set the option:	    
	    // Complete details
	    // mi.Option("Complete", "1");
	    //
	    //// Complete = false, use a subset
	    //mi.Option("Complete", "");		
		
	    // Get MediaInfoLib Output as standard RAW XML
	    mi.Option("Complete", "1");
	    mi.Option("Output", "XML");
	    String execOutRaw = mi.Inform();	    
	    
	    // Get MediaInfoLib Output as standard XML
	    mi.Option("Complete", "");
	    mi.Option("Output", "XML");
	    String execOut = mi.Inform();
	    
	    //// DEBUG
	    //System.out.println("\nMediaInfo output:\n" + execOut);
	    
	    // Get MediaInfoLib Output as EBUCore 1.5
	    mi.Option("Output", "EBUCore_1.5");
	    String ebuOut = mi.Inform();
	    
	    //// Get MediaInfoLib Output as PBCore
	    //mi.Option("Output", "PBCore");
	    //String pbOut = mi.Inform();
	    
	    // Samples count is returned in the Audio info
	    //mi.Option("Output", "Audio");
	    //String audioInfo = mi.Inform();
	    
	    // --------------------------------------------------------------------	    
	    // Retrieve additional information for audio/video tracks not contained
	    // in the general XML
	    // --------------------------------------------------------------------

	    
	    //
	    // WIP
	    //
	    String dateModified = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    		"File_Modified_Date", MediaInfoNativeWrapper.InfoKind.Text,
	    		MediaInfoNativeWrapper.InfoKind.Name);
	    
	    // Empty ???
	    String dateCreated = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    		//"File_Created_Date", MediaInfoNativeWrapper.InfoKind.Text,
	    		"Created_Date", MediaInfoNativeWrapper.InfoKind.Text,	    		
	    		MediaInfoNativeWrapper.InfoKind.Name);
	    
	    String dateEncoded = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    		"Encoded_Date", MediaInfoNativeWrapper.InfoKind.Text,
	    		MediaInfoNativeWrapper.InfoKind.Name);
	    
	    // Empty ???
	    String encodedLibraryVersion = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    		//"File_Encoded_Library_Version", MediaInfoNativeWrapper.InfoKind.Text,
	    		"Encoded_Library_Version", MediaInfoNativeWrapper.InfoKind.Text,	    		
	    		MediaInfoNativeWrapper.InfoKind.Name);	    
	    // -------
	    
	    Map <String, MediaInfoExtraData> videoTrackMap = new HashMap<String, MediaInfoExtraData>();	    
	    Map <String, MediaInfoExtraData> audioTrackMap = new HashMap<String, MediaInfoExtraData>();
	    
	    int numAudioTracks = mi.Count_Get(MediaInfoNativeWrapper.StreamKind.Audio);	    
	    for (int ndx = 0; ndx < numAudioTracks; ndx++) {
	    	MediaInfoExtraData data = new MediaInfoExtraData();
	    	
		    String id = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"ID", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);	    	
	    	
		    String audioDelay = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"Delay", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    if (audioDelay != null && audioDelay.length() > 0 )
		    	data.setDelay(audioDelay);
		    
		    String audioSamplesCount = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"Samples_count", MediaInfoNativeWrapper.InfoKind.Text,
		    		//"SamplesCount", MediaInfoNativeWrapper.InfoKind.Text,	    		
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    if (audioSamplesCount != null && audioSamplesCount.length() > 0 )
		    	data.setAudioSamplesCount(audioSamplesCount); 
		    
		    audioTrackMap.put(id, data);
	    }
	    
	    int numVideoTracks = mi.Count_Get(MediaInfoNativeWrapper.StreamKind.Video);    
	    for (int ndx = 0; ndx < numVideoTracks; ndx++) {
	    	MediaInfoExtraData data = new MediaInfoExtraData();
	    	
		    String id = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"ID", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);	    	
	    	
		    String videoDelay = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"Delay", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    if (videoDelay != null && videoDelay.length() > 0 )
		    	data.setDelay(videoDelay);
		    
		    String frameCount = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"FrameCount", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    if(frameCount != null && frameCount.length() > 0)
		    	data.setFrameCount(frameCount);
		    		    
		    videoTrackMap.put(id, data);
	    }	    
	    
	    
	    mi.Close();

		// ====================================================================		    
	    // Create a Document out of the generated XML text
	    // And transform via XSLT
		// ====================================================================	
		Document outputMetaDoc = createXml(execOut);
		Document rawOut = createXml(execOutRaw);		
		
		Document fitsXml = transform(mediaInfoFitsConfig+xsltTransform,outputMetaDoc);
		
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
		
		//
		// TODO:
		// Update fields not available in the MediaInfo XML output.
		// These have been retrieved above.
		//
		// Currently, these are:
		//
		//    General Section:
		//        timecodeStart
		//
		//    Audio Track:
		//        delay
		//        FrameRate
		//        numSamples
		//
		//    Video Track:
		//        frameCount
		
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
			// Detach the ebu node and its children
			for(Element elem : ebuElementNodes) {
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
				
		// ====================================================================
		// Revise the XML to include element data that was not returned either
		// via the MediaInfo XML or ebuCore
		// ====================================================================
		
		//
		// TODO: Clean up the below
		//
	
//		// DEBUG
//		//
//		//
//	    //AudioTrackMap.
//	    for(Map.Entry<String, MediaInfoExtraData> entry : audioTrackMap.entrySet()){
//	        System.out.printf("\nAudio Key : %s and AudioSampleCount: %s", entry.getKey(), entry.getValue().getAudioSamplesCount());
//	        System.out.printf("\nAudio Key : %s and Delay: %s", entry.getKey(), entry.getValue().getDelay());
//	        System.out.printf("\nAudio Key : %s and Frame Count %s", entry.getKey(), entry.getValue().getFrameCount());
//	    }		    
//	    
//	
//	    //VideoTrackMap.
//	    for(Map.Entry<String, MediaInfoExtraData> entry : videoTrackMap.entrySet()){
//	        //System.out.printf("\nVideo Key : %s and AudioSampleCount: %s", entry.getKey(), entry.getValue().getAudioSamplesCount());
//	        System.out.printf("\nVideo Key : %s and Delay: %s", entry.getKey(), entry.getValue().getDelay());
//	        System.out.printf("\nVideo Key : %s and Frame Count %s", entry.getKey(), entry.getValue().getFrameCount());
//	    }		
		
		try {		
		    XPath xpathFits = XPath.newInstance("//x:fits/x:metadata/x:video");			
		
		    // NOTE: We need to add a namespace	to xpath, because JDom XPath 
		    // does not support default namespaces. It requires you to add a
		    // fake namespace to the XPath instance.
		    xpathFits.addNamespace("x", fitsXml.getRootElement().getNamespaceURI());

		    Element videoElement = (Element)xpathFits.selectSingleNode(fitsXml);
		    
		    List <Element>elementList = videoElement.getContent();
		    for (Element element : elementList) {
		    	
		    	// We only care about the tracks
		    	if(element.getName().equals("track")) {
		    		String id = element.getAttributeValue("id");
		    		
		    		// video track data
		    		if (videoTrackMap.containsKey(id)) {
			    		MediaInfoExtraData data = (MediaInfoExtraData)videoTrackMap.get(id);
			    		if(data != null) {
			    			// TODO: Actually set the data in the xml element
			    			System.out.println ("GOT videoTrackMap match");
			    			
			    		}		    			
		    		} //  videoTrackMap.containsKey(id)

		    		// audio track data
		    		if (audioTrackMap.containsKey(id)) {
			    		MediaInfoExtraData data = audioTrackMap.get(id);
			    		if(data != null) {
			    			// TODO: Actually set the data in the xml element
			    			System.out.println ("GOT audioTrackMap match");
			    			
			    		}		    			
		    		} //  audioTrackMap.containsKey(id)
		    		
		    		
		    	} // if "track"
		    }
		    
		    
		    

		    
		}
		catch(JDOMException e) {
			throw new FitsToolException("Error formatting ebucore xml node " + TOOL_NAME);			
		}		    
		

		// ====================================================================		
		
//		// DEBUG
		String finalXml = new XMLOutputter(Format.getPrettyFormat()).outputString(fitsXml);
		System.out.println("\nFINAL XML:\n" + finalXml);
		
		output = new ToolOutput(this,fitsXml,rawOut);
		
		// DEBUG
		String fitsOutputString = new XMLOutputter(Format.getPrettyFormat()).outputString(output.getFitsXml());
		
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

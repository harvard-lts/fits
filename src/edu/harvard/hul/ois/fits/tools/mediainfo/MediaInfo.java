/* 
 * Copyright 2015 Harvard University Library
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

//import com.twmacinta.util.MD5;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.XmlUtils;
//import edu.harvard.hul.ois.fits.tools.utils.XsltTransformMap;

/**  The glue class for invoking the MediaInfo native library under FITS.
 */
public class MediaInfo extends ToolBase {

	private final static String TOOL_NAME = "MediaInfo";
	private boolean enabled = true;
	
	public final static String mediaInfoFitsConfig = Fits.FITS_XML+"mediainfo"+File.separator;
	public final static String xsltTransform = "mediainfo_video_to_fits.xslt";
	
    private static final Logger logger = Logger.getLogger(MediaInfo.class);
    private static MediaInfoNativeWrapper mi = null;
	
	public MediaInfo() throws FitsException {
		
		info = new ToolInfo();
		info.setName(TOOL_NAME);		
		
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
	
	@Override
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
	    // OUTPUT Options for the Native MediaInfo Library are:
	    // 		(From MediaInfo_Inform.cpp)
	    //
		// NOTE: Default is Text - when no options set
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
	    // Separate details are available via the API on various data types/tracks
	    //
	    //	     "General"
	    //	     "Video"
	    //	     "Audio"
	    //	     "Text"
	    //	     "Chapters"
	    //	     "Image"
	    //	     "Menu"
	    // --------------------------------------------------------------------

		// No format, so output is pure text
		// String textOutput = mi.Inform();
		// System.out.println("\nTEXT:\n" + textOutput);
		
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
	    // System.out.println("\nMediaInfo RAW output:\n" + execOutRaw + "\n\n");	    
	    
	    // Get MediaInfoLib Output as standard XML
	    mi.Option("Complete", "");
	    mi.Option("Output", "XML");
	    String execOut = mi.Inform();
	    
	    // DEBUG
	    // System.out.println("\nMediaInfo output:\n" + execOut + "\n\n");
	    
	    //// Get MediaInfoLib Output as EBUCore 1.5
	    //mi.Option("Output", "EBUCore_1.5");
	    //String ebuOut = mi.Inform();
	    
	    // Get MediaInfoLib Output as PBCore
	    //mi.Option("Output", "PBCore");
	    //String pbOut = mi.Inform();
	    
	    // Samples count is returned in the Audio info
	    //mi.Option("Output", "Audio");
	    //String audioInfo = mi.Inform();
	    
	    // --------------------------------------------------------------------	    
	    // Retrieve additional information for audio/video tracks not contained
	    // in the general XML
	    // --------------------------------------------------------------------

	    String dateModified = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    		"File_Modified_Date", MediaInfoNativeWrapper.InfoKind.Text,
	    		MediaInfoNativeWrapper.InfoKind.Name);

	    String generalBitRate = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    		"BitRate", MediaInfoNativeWrapper.InfoKind.Text, 
	    		MediaInfoNativeWrapper.InfoKind.Name);
	    
	    String timeCodeStart = mi.Get(MediaInfoNativeWrapper.StreamKind.Other, 0,
	    		"TimeCode_FirstFrame", MediaInfoNativeWrapper.InfoKind.Text,
	    		MediaInfoNativeWrapper.InfoKind.Name);
	    
	    String generalDuration = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    		"Duration", MediaInfoNativeWrapper.InfoKind.Text,
	    		MediaInfoNativeWrapper.InfoKind.Name);

	    String generalFileSize = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    		"FileSize", MediaInfoNativeWrapper.InfoKind.Text,
	    		MediaInfoNativeWrapper.InfoKind.Name);
	    
	    //
	    // TODO: bitRate_Maximum never seems to appear in MediaInfo
	    // in the general section
	    //
	    //String generalBitRateMax = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
	    //		"BitRate_Maximum", MediaInfoNativeWrapper.InfoKind.Text,
	    //		MediaInfoNativeWrapper.InfoKind.Name);    
	    
	    String generalFormatProfileFromVideo = "";

//	    // Empty ???
//	    String dateCreated = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
//	    		//"File_Created_Date", MediaInfoNativeWrapper.InfoKind.Text,
//	    		"Created_Date", MediaInfoNativeWrapper.InfoKind.Text,	    		
//	    		MediaInfoNativeWrapper.InfoKind.Name);
//	    
//	    String dateEncoded = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
//	    		"Encoded_Date", MediaInfoNativeWrapper.InfoKind.Text,
//	    		MediaInfoNativeWrapper.InfoKind.Name);
//	    
//	    // Empty ???
//	    String encodedLibraryVersion = mi.Get(MediaInfoNativeWrapper.StreamKind.General, 0,
//	    		//"File_Encoded_Library_Version", MediaInfoNativeWrapper.InfoKind.Text,
//	    		"Encoded_Library_Version", MediaInfoNativeWrapper.InfoKind.Text,	    		
//	    		MediaInfoNativeWrapper.InfoKind.Name);
//
	    
	    // Maps to hold data that are obtained via explicit MediaInfo API call.
	    // These values are either not exposed in the XML returned by the Inform()
	    // method call, or if the granularity of the value is not correct, such as
	    // bitRate not returning the value as milliseconds, but rather as Mpbs
	    Map<String, Map<String, String>> videoTrackValuesMap = 
	    	    new HashMap<String, Map<String, String>>();
	    Map<String, Map<String, String>> audioTrackValuesMap = 
	    	    new HashMap<String, Map<String, String>>();	    
	    
	    int numAudioTracks = mi.Count_Get(MediaInfoNativeWrapper.StreamKind.Audio);	    
	    for (int ndx = 0; ndx < numAudioTracks; ndx++) {
	   
		    String id = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"ID", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    
		    //
		    // In some instances, the ID will include some invalid data such as
		    
		    if(StringUtils.isEmpty(id)) {
		    	// If we only have one audio track, we can retrieve data with
		    	// the index 0
		    	if(numAudioTracks == 1)
		    		id = "0";
		    	else {
			    	// TODO: Throw error and/or log?
			    	logger.error("Error retrieving the ID of the audio track from MediaInfo: " + ndx);
			    	continue;
		    	}
		    }
	    	
		    String audioDelay = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"Delay", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap (audioTrackValuesMap, id, "delay", audioDelay);

		    String audioSamplesCount = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"SamplingCount", MediaInfoNativeWrapper.InfoKind.Text,    		
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap (audioTrackValuesMap, id, "numSamples", audioSamplesCount);
		    
		    String bitRate = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"BitRate", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(audioTrackValuesMap, id, "bitRate", bitRate);
		    
		    //
		    // TODO: bitRate_Maximum never seems to appear in MediaInfo
		    // in the video section
		    //
		    // bitRateMax and bitRateMode, are both used to update
		    // bitRate, when bitRateMode is variable (VBR)
		    //
		    String bitRateMax = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"BitRate_Maximum", MediaInfoNativeWrapper.InfoKind.Text,
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    
		    addDataToMap(audioTrackValuesMap, id, "bitRateMax", bitRateMax);
		    
		    String bitRateMode = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"BitRate_Mode", MediaInfoNativeWrapper.InfoKind.Text,
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(audioTrackValuesMap, id, "bitRateMode", bitRateMode);
		    
		    // ----------------------------------------------------------------
		    
		    String duration = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"Duration", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap (audioTrackValuesMap, id, "duration", duration);
		    
		    String trackSize = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"StreamSize", MediaInfoNativeWrapper.InfoKind.Text, 		    		
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(audioTrackValuesMap, id, "trackSize", trackSize);
		    
		    String samplingRate = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"SamplingRate", MediaInfoNativeWrapper.InfoKind.Text, 		    		
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(audioTrackValuesMap, id, "samplingRate", samplingRate);
		    
		    String channels = mi.Get(MediaInfoNativeWrapper.StreamKind.Audio, ndx,
		    		"Channels", MediaInfoNativeWrapper.InfoKind.Text, 		    		
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(audioTrackValuesMap, id, "channels", channels);
	    }
	    
	    int numVideoTracks = mi.Count_Get(MediaInfoNativeWrapper.StreamKind.Video);    
	    for (int ndx = 0; ndx < numVideoTracks; ndx++) {

		    String id = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"ID", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    
		    if(StringUtils.isEmpty(id)) {
		    	// If we only have one video track, we can retrieve data with
		    	// the index 0
		    	if(numVideoTracks == 1)
		    		id = "0";
		    	else {
			    	// TODO: Throw error and/or log?
			    	logger.error("Error retrieving the ID of the video track from MediaInfo: " + ndx);
			    	continue;
		    	}
		    }	    	
		    
		    String duration = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"Duration", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap (videoTrackValuesMap, id, "duration", duration);		    	
	    	
		    String videoDelay = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"Delay", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap (videoTrackValuesMap, id, "delay", videoDelay);  
		    
		    String frameCount = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"FrameCount", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);	
		    addDataToMap (videoTrackValuesMap, id, "frameCount", frameCount);		    
		    
		    String bitRate = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"BitRate", MediaInfoNativeWrapper.InfoKind.Text, 
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(videoTrackValuesMap, id, "bitRate", bitRate);
		    
		    //
		    // TODO: bitRate_Maximum never seems to appear in MediaInfo
		    // in the video section
		    //
		    // bitRateMax and bitRateMode, are both used to update
		    // bitRate, when bitRateMode is variable (VBR)
		    //
		    String bitRateMax = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"BitRate_Maximum", MediaInfoNativeWrapper.InfoKind.Text,
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    
		    addDataToMap(videoTrackValuesMap, id, "bitRateMax", bitRateMax);
		    
		    String bitRateMode = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"BitRate_Mode", MediaInfoNativeWrapper.InfoKind.Text,
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(videoTrackValuesMap, id, "bitRateMode", bitRateMode);
		    
		    // ----------------------------------------------------------------
		    
		    String trackSize = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"StreamSize", MediaInfoNativeWrapper.InfoKind.Text, 		    		
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(videoTrackValuesMap, id, "trackSize", trackSize);
		    
		    //
		    // TODO: frameRate_Maximum never seems to appear in MediaInfo
		    // in the video section
		    //
		    // frameRateMax and frameRateMode, are both used to update
		    // frameRate, when frameRateMode is variable (VFR)
		    //
		    String frameRateMax = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"FrameRate_Maximum", MediaInfoNativeWrapper.InfoKind.Text,
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    
		    addDataToMap(videoTrackValuesMap, id, "frameRateMax", frameRateMax);
		    
		    String frameRateMode = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"FrameRate_Mode", MediaInfoNativeWrapper.InfoKind.Text,
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(videoTrackValuesMap, id, "frameRateMode", frameRateMode);
		    
		    String frameRate = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"FrameRate", MediaInfoNativeWrapper.InfoKind.Text, 		    		
		    		MediaInfoNativeWrapper.InfoKind.Name);
		    addDataToMap(videoTrackValuesMap, id, "frameRate", frameRate);
		    
		    // ----------------------------------------------------------------			    
		    
		    // NOTE:
		    // formatProfile goes in the FITS XML general section, but
		    // sometimes is missing from the MediaInfo general section and
		    // present in video section.
		    generalFormatProfileFromVideo = mi.Get(MediaInfoNativeWrapper.StreamKind.Video, ndx,
		    		"Format_Profile", MediaInfoNativeWrapper.InfoKind.Text, 		    		
		    		MediaInfoNativeWrapper.InfoKind.Name);
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
		// Revise the XML to include element data that was not returned either
		// via the MediaInfo XML or require revision for granularity
		// ====================================================================		
		
		try {
		    XPath xpathFits = XPath.newInstance("//x:fits/x:metadata/x:video");	
		
		    // NOTE: We need to add a namespace	to xpath, because JDom XPath 
		    // does not support default namespaces. It requires you to add a
		    // fake namespace to the XPath instance.
		    xpathFits.addNamespace("x", fitsXml.getRootElement().getNamespaceURI());

		    Element videoElement = (Element)xpathFits.selectSingleNode(fitsXml);
		    List <Element>elementList = videoElement.getContent();
		    for (Element element : elementList) {
		    	
		    	// General size
		    	if(element.getName().equals("size")) {				    
					if (!StringUtils.isEmpty(generalFileSize)) {
						element.setText(generalFileSize);
					}
		    	}    	
		    	
		    	// General Section dateModified
		    	if(element.getName().equals("dateModified")) {				    
					if (!StringUtils.isEmpty(dateModified)) {
						element.setText(dateModified);
					}		    		
		    	}
		    	// General Section timecodeStart
		    	if(element.getName().equals("timecodeStart")) {				    
					if (!StringUtils.isEmpty(timeCodeStart)) {
						element.setText(timeCodeStart);
					}		    		
		    	}
		    	// General Section bit rate
		    	if(element.getName().equals("bitRate")) {				    
					if (!StringUtils.isEmpty(generalBitRate)) {
						element.setText(generalBitRate);
					}		    		
		    	}
		    	
		    	// General Section duration
		    	if(element.getName().equals("duration")) {				    
					if (!StringUtils.isEmpty(generalDuration)) {
						element.setText(generalDuration);
					}		    		
		    	}	    	

				// General Section formatProfile - If missing, use value from 
		    	// video section, which was set above
				if(element.getName().equals("formatProfile")) {
					String formatProfileFromElement = element.getValue();
					// if value for element is missing or empty, we need to update it
					if(StringUtils.isEmpty(formatProfileFromElement)) {
    					if(generalFormatProfileFromVideo != null && generalFormatProfileFromVideo.length() > 0) {
    						element.setText(generalFormatProfileFromVideo);
    					}
					}
					
				}
				
//				// The MD5 must be present in the MediaInfo FITS XML so that
//				// Ebucore can have access to it
//		    	if(element.getName().equals("filemd5")) {
//					try {
//						String md5Hash = MD5.asHex(MD5.getHash(new File(file.getPath())));
//						element.setText(md5Hash);
//					} catch (IOException e) {
//						throw new FitsToolException("Could not calculate the MD5 for "+file.getPath(),e);
//					}					
//		    	}				
				
		    	// Tracks
		    	if(element.getName().equals("track")) {
		    		String id = element.getAttributeValue("id");
		    		
		    		// In some cases, the track ID returned by MediaInfo in the
		    		// XML used for XSLT transformation is not fully numerical, 
		    		// and does NOT match the ID returned by calling the 
		    		// MediaInfo API, so we need to strip off the additional
		    		// information contained in the ID that the XSLT transformation
		    		//
		    		// For example, if the track ID is 1666406348 (0x635357CC)
		    		// we need to split the string into 2 pieces at the space
		    		// and set the ID to the 1st value
		    		if(!XmlUtils.isNumeric(id)) {
		    			String parts[] = id.split(" ");
		    			id = parts[0];
		    			element.setAttribute("id", id);
		    		}
		    		
			    	// HACK: In some cases a track ID is not given, in those cases, use the index
		    		if (StringUtils.isEmpty(id)) {
			    		id = "0";
		    			element.setAttribute("id", id);
		    		}

		    		// video track data
		    		if (videoTrackValuesMap.containsKey(id)) {

		    			List <Element>contents = element.getContent();		    				
		    			for (Element childElement : contents) {

		    				// delay
		    				if(childElement.getName().equals("delay")) {
		    					String delay = videoTrackValuesMap.get(id).get("delay");
		    					if(delay != null && delay.length() > 0) {
		    						childElement.setText(delay);
		    					}			    					
		    				}
		    				// frameCount
		    				if(childElement.getName().equals("frameCount")) {
		    					String frameCount = videoTrackValuesMap.get(id).get("frameCount");
		    					if(frameCount != null && frameCount.length() > 0) {
		    						childElement.setText(frameCount);
		    					}			    					
		    				}

		    				// bitRate
		    				// 1) correct format
		    				// 2) set to bitRateMax, if mode is variable
		    				if(childElement.getName().equals("bitRate")) {
			    				// NOTE: If the bitRateMode is Variable (VBR), set it to the value for
			    				// BitRateMax
		    					String bitRateMode = videoTrackValuesMap.get(id).get("bitRateMode");
		    					if(!StringUtils.isEmpty(bitRateMode) && bitRateMode.toUpperCase().equals("VBR")) {
			    					String bitRateMax = videoTrackValuesMap.get(id).get("bitRateMax");
			    					if(!StringUtils.isEmpty(bitRateMax)) {
			    						childElement.setText(bitRateMax);
			    					}
			    					// NOTE: Since I have never seen bitRateMax being set, we need a backup value
			    					else {
				    					String bitRate = videoTrackValuesMap.get(id).get("bitRate");
				    					if(bitRate != null && bitRate.length() > 0) {
				    						childElement.setText(bitRate);
				    					}
			    					}
			    					
		    					}
		    					// Otherwise, just update it if we need to
		    					else {
			    					String bitRate = videoTrackValuesMap.get(id).get("bitRate");
			    					if(bitRate != null && bitRate.length() > 0) {
			    						childElement.setText(bitRate);
			    					}		    						
		    					}
			    					
		    				} // bitRate				

		    				// duration - correct format
		    				if(childElement.getName().equals("duration")) {
		    					String duration = videoTrackValuesMap.get(id).get("duration");
		    					if(duration != null && duration.length() > 0) {
		    						childElement.setText(duration);
		    					}			    					
		    				}

		    				// trackSize - correct format
		    				if(childElement.getName().equals("trackSize")) {
		    					String trackSize = videoTrackValuesMap.get(id).get("trackSize");
		    					if(trackSize != null && trackSize.length() > 0) {
		    						childElement.setText(trackSize);
		    					}			    					
		    				}
		    				
		    				// frameRate
		    				// 1) correct format
		    				// 2) set to frameRateMax, if mode is variable
		    				if(childElement.getName().equals("frameRate")) {
			    				// NOTE: If the bitRateMode is Variable (VBR), set it to the value for
			    				// BitRateMax
		    					String frameRateMode = videoTrackValuesMap.get(id).get("frameRateMode");
		    					if(!StringUtils.isEmpty(frameRateMode) && frameRateMode.toUpperCase().equals("VFR")) {
			    					String frameRateMax = videoTrackValuesMap.get(id).get("frameRateMax");
			    					if(!StringUtils.isEmpty(frameRateMax)) {
			    						childElement.setText(frameRateMax);
			    					}
			    					// NOTE: Since I have never seen frameRateMax being set, we need a backup value
			    					else {
				    					String frameRate = videoTrackValuesMap.get(id).get("frameRate");
				    					if(frameRate != null && frameRate.length() > 0) {
				    						childElement.setText(frameRate);
				    					}
			    					}
			    					
		    					}
		    					// Otherwise, just update it if we need to
		    					else {
			    					String frameRate = videoTrackValuesMap.get(id).get("frameRate");
			    					if(frameRate != null && frameRate.length() > 0) {
			    						childElement.setText(frameRate);
			    					}		    						
		    					}
			    					
		    				}		    				

		    			} // childElement

		    		} //  videoTrackMap.containsKey(id)

		    		// audio track data
		    		if (audioTrackValuesMap.containsKey(id)) {

		    			List <Element>contents = element.getContent();		    				
		    			for (Element childElement : contents) {

		    				// delay
		    				if(childElement.getName().equals("delay")) {
		    					String delay = audioTrackValuesMap.get(id).get("delay");
		    					if(delay != null && delay.length() > 0) {
		    						childElement.setText(delay);
		    					}			    					
		    				}
		    				// number of samples
		    				if(childElement.getName().equals("numSamples")) {
		    					String numSamples = audioTrackValuesMap.get(id).get("numSamples");
		    					if(numSamples!= null && numSamples.length() > 0) {
		    						childElement.setText(numSamples);
		    					}			    					
		    				}

		    				// bitRate
		    				// 1) correct format
		    				// 2) set to bitRateMax, if mode is variable
		    				if(childElement.getName().equals("bitRate")) {
			    				// NOTE: If the bitRateMode is Variable (VBR), set it to the value for
			    				// BitRateMax
		    					String bitRateMode = audioTrackValuesMap.get(id).get("bitRateMode");
		    					if(!StringUtils.isEmpty(bitRateMode) && bitRateMode.toUpperCase().equals("VBR")) {
			    					String bitRateMax = audioTrackValuesMap.get(id).get("bitRateMax");
			    					if(!StringUtils.isEmpty(bitRateMax)) {
			    						childElement.setText(bitRateMax);
			    					}
			    					// NOTE: Since I have never seen bitRateMax being set, we need a backup value
			    					else {
				    					String bitRate = audioTrackValuesMap.get(id).get("bitRate");
				    					if(bitRate != null && bitRate.length() > 0) {
				    						childElement.setText(bitRate);
				    					}
			    					}
			    					
		    					}
		    					// Otherwise, just update it if we need to
		    					else {
			    					String bitRate = audioTrackValuesMap.get(id).get("bitRate");
			    					if(bitRate != null && bitRate.length() > 0) {
			    						childElement.setText(bitRate);
			    					}		    						
		    					}
			    					
		    				} // bitRate
		    				

		    				// duration
		    				if(childElement.getName().equals("duration")) {
		    					String duration = audioTrackValuesMap.get(id).get("duration");
		    					if(duration != null && duration.length() > 0) {
		    						childElement.setText(duration);
		    					}			    					
		    				}

		    				// trackSize - correct format
		    				if(childElement.getName().equals("trackSize")) {
		    					String trackSize = audioTrackValuesMap.get(id).get("trackSize");
		    					if(trackSize != null && trackSize.length() > 0) {
		    						childElement.setText(trackSize);
		    					}			    					
		    				}
		    				
		    				// samplingRate - correct format
		    				if(childElement.getName().equals("samplingRate")) {
		    					String samplingRate = audioTrackValuesMap.get(id).get("samplingRate");
		    					if(samplingRate != null && samplingRate.length() > 0) {
		    						childElement.setText(samplingRate);
		    					}			    					
		    				}
		    				
		    				// channels - correct format
		    				if(childElement.getName().equals("channels")) {
		    					String channels = audioTrackValuesMap.get(id).get("channels");
		    					if(channels != null && channels.length() > 0) {
		    						childElement.setText(channels);
		    					}			    					
		    				}		    				

		    			} // childElement			    			
			
		    		} //  audioTrackMap.containsKey(id)
		    		
		    		
		    	} // "track"
		    	
		    }  // for (Element element : elementList) {

		    
		} catch(JDOMException e) {
			throw new FitsToolException("Error revising xml node values " + TOOL_NAME);			
		}		    
		

		// ====================================================================		
		
		// DEBUG
		String finalXml = new XMLOutputter(Format.getPrettyFormat()).outputString(fitsXml);
		System.out.println("\nFINAL XML:\n" + finalXml);
		
		output = new ToolOutput(this,fitsXml,rawOut);
		
		// DEBUG
		// String fitsOutputString = new XMLOutputter(Format.getPrettyFormat()).outputString(output.getFitsXml());
		
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("MediaInfo.extractInfo finished on " + file.getName());
        
		return output;
	}
	
	/**
	 * Helper method to add values to the map within the trackValuesMap
	 * 
	 * @param trackValuesMap  The map to be updated
	 * @param id  The key to trackValuesMap
	 * @param key The key to the map contained within trackValuesMap
	 * @param value The value to set in the map contained within trackValuesMap
	 */
	private void addDataToMap(Map<String, Map<String, String>> trackValuesMap,
			String id, String key, String value) {
	    if (value != null && value.length() > 0 ) {
	    	if(trackValuesMap.get(id) == null)
	    		trackValuesMap.put(id, new HashMap<String, String>());
		    trackValuesMap.get(id).put(key, value);		    	
	    }		
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

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean value) {
		enabled = value;		
	}
	
}

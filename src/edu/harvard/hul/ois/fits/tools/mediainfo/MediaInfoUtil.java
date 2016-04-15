/* 
 * Copyright 2016 Harvard University Library
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.utils.XmlUtils;

/**
 * This is a helper class for the MediaInfo tool class. The main purpose of 
 * this class is post process and revise XML data where necessary when either
 * the MediaInfo API does not not return a value in its default XML output, 
 * or we need to do do some normalization based upon things the XSLT process
 * doesn't or can't handle.
 */
public class MediaInfoUtil {
	
	private final static String TOOL_NAME = "MediaInfo";
	private static final Logger logger = Logger.getLogger(MediaInfoUtil.class);
	private static MediaInfoNativeWrapper mi = null;
	
	protected MediaInfoUtil(MediaInfoNativeWrapper mi) {
		MediaInfoUtil.mi = mi;
	}
	
	protected  Map<String, String> loadGeneralDataMap () {
	    Map<String, String> generalValuesDataMap = new HashMap<String, String>();

	    generalValuesDataMap.put("dateModified", getMediaInfoString(
	    		"File_Modified_Date", MediaInfoNativeWrapper.StreamKind.General));
	    
	    generalValuesDataMap.put("generalBitRate", getMediaInfoString(
	    		"BitRate", MediaInfoNativeWrapper.StreamKind.General));
	    
	    generalValuesDataMap.put("timeCodeStart", getMediaInfoString(
	    		"TimeCode_FirstFrame", MediaInfoNativeWrapper.StreamKind.Other));
	    
	    generalValuesDataMap.put("generalDuration", getMediaInfoString(
	    		"Duration", MediaInfoNativeWrapper.StreamKind.General));

	    generalValuesDataMap.put("generalFileSize", getMediaInfoString(
	    		"FileSize", MediaInfoNativeWrapper.StreamKind.General));


//	    //
//	    // TODO: bitRate_Maximum never seems to appear in MediaInfo
//	    // in the general section
//	    //
//	    //String generalBitRateMax = getMediaInfoString(
//	    //		"BitRate_Maximum", MediaInfoNativeWrapper.StreamKind.General);
//
//	    // Empty ???
//	    String dateCreated = getMediaInfoString(
//	    		"File_Created_Date", MediaInfoNativeWrapper.StreamKind.General);
//				//"Created_Date", MediaInfoNativeWrapper.StreamKind.General);
//	    
//	    String dateEncoded = getMediaInfoString(
//	    		"Encoded_Date", MediaInfoNativeWrapper.StreamKind.General); 
//	    
//	    // Empty ???
//	    String encodedLibraryVersion = getMediaInfoString(
//	    		//"File_Encoded_Library_Version", MediaInfoNativeWrapper.StreamKind.General);
//				"Encoded_Library_Version",MediaInfoNativeWrapper.StreamKind.General);

	    return generalValuesDataMap;
	}
		
	/**
	 * Helper method to add values to the map within the trackValuesMap
	 * 
	 * @param trackValuesMap  The map to be updated
	 * @param id  The key to trackValuesMap
	 * @param key The key to the map contained within trackValuesMap
	 * @param value The value to set in the map contained within trackValuesMap
	 */
	protected void addDataToMap(Map<String, Map<String, String>> trackValuesMap,
			String id, String key, String value) {
	    if (!StringUtils.isEmpty(value)) {
	    	if(trackValuesMap.get(id) == null)
	    		trackValuesMap.put(id, new HashMap<String, String>());
		    trackValuesMap.get(id).put(key, value);		    	
	    }		
	}

	protected Map<String, Map<String, String>> loadVideoDataMap (Map<String, String> generalValuesDataMap) {
		
	    Map<String, Map<String, String>> videoTrackValuesMap = 
	    	    new HashMap<String, Map<String, String>>();
		
	    int numVideoTracks = mi.Count_Get(MediaInfoNativeWrapper.StreamKind.Video);    
	    for (int ndx = 0; ndx < numVideoTracks; ndx++) {
		    
		    String id = getMediaInfoString(ndx, "ID", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    
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
		    
		    String duration = getMediaInfoString(ndx, "Duration", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "duration", duration);		    	
	    	
		    String videoDelay = getMediaInfoString(ndx, "Delay", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "delay", videoDelay);  
		    
		    String frameCount = getMediaInfoString(ndx, "FrameCount", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "frameCount", frameCount);		    
		    
		    String bitRate = getMediaInfoString(ndx, "BitRate", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap(videoTrackValuesMap, id, "bitRate", bitRate);
		    
		    //
		    // TODO: bitRate_Maximum never seems to appear in MediaInfo
		    // in the video section
		    //
		    // bitRateMax and bitRateMode, are both used to update
		    // bitRate, when bitRateMode is variable (VBR)
		    //
		    String bitRateMax = getMediaInfoString(ndx, "BitRate_Maximum", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap(videoTrackValuesMap, id, "bitRateMax", bitRateMax);
		    
		    String bitRateMode = getMediaInfoString(ndx, "BitRate_Mode", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap(videoTrackValuesMap, id, "bitRateMode", bitRateMode);
		    
		    // ----------------------------------------------------------------
		    
		    String trackSize = getMediaInfoString(ndx, "StreamSize", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap(videoTrackValuesMap, id, "trackSize", trackSize);
		    
		    //
		    // TODO: frameRate_Maximum never seems to appear in MediaInfo
		    // in the video section
		    //
		    // frameRateMax and frameRateMode, are both used to update
		    // frameRate, when frameRateMode is variable (VFR)
		    //
		    String frameRateMax = getMediaInfoString(ndx, "FrameRate_Maximum", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap(videoTrackValuesMap, id, "frameRateMax", frameRateMax);
		    
		    String frameRateMode = getMediaInfoString(ndx, "FrameRate_Mode", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap(videoTrackValuesMap, id, "frameRateMode", frameRateMode);
		    
		    String frameRate = getMediaInfoString(ndx, "FrameRate", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap(videoTrackValuesMap, id, "frameRate", frameRate);
		    
		    // ----------------------------------------------------------------			    
		    String scanningOrder = getMediaInfoString(ndx, "ScanOrder", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "scanningOrder", scanningOrder);
		    
		    // Additional Codec stuff:
		    String codecId = getMediaInfoString(ndx, "CodecID", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "codecId", codecId);
		    
		    String codecCC = getMediaInfoString(ndx, "Codec/CC", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "codecCC", codecCC);
		    
		    String codecName = getMediaInfoString(ndx, "Codec", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "codecName", codecName);	
		    
		    String codecVersion = getMediaInfoString(ndx, "Codec_Profile", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "codecVersion", codecVersion);
		    
		    String codecFamily = getMediaInfoString(ndx, "Codec/Family", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "codecFamily", codecFamily);
		    
		    String codecInfo = getMediaInfoString(ndx, "Codec/Info", 
		    		MediaInfoNativeWrapper.StreamKind.Video);
		    addDataToMap (videoTrackValuesMap, id, "codecInfo", codecInfo);	    
		    
		    // NOTE:
		    // formatProfile goes in the FITS XML general section, but
		    // sometimes is missing from the MediaInfo general section and
		    // present in video section.
		    generalValuesDataMap.put("generalFormatProfileFromVideo",
		    		getMediaInfoString(ndx, "Format_Profile", 
		    		MediaInfoNativeWrapper.StreamKind.Video));
	    }
		return videoTrackValuesMap;
	}	

	protected Map<String, Map<String, String>> loadAudioDataMap () {

		Map<String, Map<String, String>> audioTrackValuesMap = 
	    	    new HashMap<String, Map<String, String>>();
		
	    int numAudioTracks = mi.Count_Get(MediaInfoNativeWrapper.StreamKind.Audio);	    
	    for (int ndx = 0; ndx < numAudioTracks; ndx++) {
	   
		    String id = getMediaInfoString(ndx, "ID", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);

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
	    	
		    String audioDelay = getMediaInfoString(ndx, "Delay", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap (audioTrackValuesMap, id, "delay", audioDelay);

		    String audioSamplesCount = getMediaInfoString(ndx, "SamplingCount", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap (audioTrackValuesMap, id, "numSamples", audioSamplesCount);
		    
		    String bitRate = getMediaInfoString(ndx, "BitRate", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap(audioTrackValuesMap, id, "bitRate", bitRate);
		    
		    //
		    // TODO: bitRate_Maximum never seems to appear in MediaInfo
		    // in the video section
		    //
		    // bitRateMax and bitRateMode, are both used to update
		    // bitRate, when bitRateMode is variable (VBR)
		    //
		    String bitRateMax = getMediaInfoString(ndx, "BitRate_Maximum", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap(audioTrackValuesMap, id, "bitRateMax", bitRateMax);
		    
		    String bitRateMode = getMediaInfoString(ndx, "BitRate_Mode", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap(audioTrackValuesMap, id, "bitRateMode", bitRateMode);
		    
		    // ----------------------------------------------------------------
		    
		    String duration = getMediaInfoString(ndx, "Duration", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);;
		    addDataToMap (audioTrackValuesMap, id, "duration", duration);
		    
		    String trackSize = getMediaInfoString(ndx, "StreamSize", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap(audioTrackValuesMap, id, "trackSize", trackSize);
		    
		    String samplingRate = getMediaInfoString(ndx, "SamplingRate", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap(audioTrackValuesMap, id, "samplingRate", samplingRate);
		    
		    String channels = getMediaInfoString(ndx, "Channels", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap(audioTrackValuesMap, id, "channels", channels);
		    
		    // Additional Codec stuff:
		    String codecId = getMediaInfoString(ndx, "CodecID", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap(audioTrackValuesMap, id, "codecId", codecId);
		    
		    String codecFamily = getMediaInfoString(ndx, "Codec/Family", 
		    		MediaInfoNativeWrapper.StreamKind.Audio);
		    addDataToMap(audioTrackValuesMap, id, "codecFamily", codecFamily);
		    
		    //String codecInfo = getMediaInfoString(ndx, "Codec/Info", 
		    //		MediaInfoNativeWrapper.StreamKind.Audio);
		    //addDataToMap(audioTrackValuesMap, id, "codecInfo", codecInfo);		    
	    }
	    
	    return audioTrackValuesMap;
		
	}
	
	/**
	 * Revises the XML to include element data that was not returned either via
	 * the MediaInfo XML or require revision for granularity using various data 
	 * maps
	 * 
	 * @param fitsXml
	 * @param videoTrackValuesMap
	 * @param audioTrackValuesMap
	 * @param generalValuesDataMap
	 * @throws FitsToolException
	 */
	protected void reviseXmlData (Document fitsXml, Map<String, Map<String, String>> videoTrackValuesMap,
			Map<String, Map<String, String>> audioTrackValuesMap,  Map<String, String> generalValuesDataMap ) 
					throws FitsToolException {	
		
		try {			
			reviseIdentification(fitsXml);

		    XPath xpathFits = XPath.newInstance("//x:fits/x:metadata/x:video");	
		
		    // NOTE: We need to add a namespace	to xpath, because JDom XPath 
		    // does not support default namespaces. It requires you to add a
		    // fake namespace to the XPath instance.
		    xpathFits.addNamespace("x", fitsXml.getRootElement().getNamespaceURI());

		    Element videoElement = (Element)xpathFits.selectSingleNode(fitsXml);
		    List <Element>elementList = videoElement.getContent();
		    for (Element element : elementList) {
		    	
		    	// First revise the general data right off the video element
		    	reviseGeneralSection(element,  generalValuesDataMap);
				
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
		    		// or even 189 (0xBD)-128 (0x80)
		    		// we need to remove all of the text between, and including
		    		// the parenthesis to match what is returned by MediaInfo
		    		// and set the ID to the 1st value
		    		if(!XmlUtils.isNumeric(id)) {
		    			id = convertIdToMediaInfoFormat(id);
		    			element.setAttribute("id", id);
		    		}
		    		
			    	// HACK: In some cases a track ID is not given, in those cases, use the index
		    		if (StringUtils.isEmpty(id)) {
			    		id = "0";
		    			element.setAttribute("id", id);
		    		}

		    		// video track data
		    		if (videoTrackValuesMap.containsKey(id)) {		    			
		    			reviseVideoSection(element, id, videoTrackValuesMap);
		    		}
		    		// audio track data
		    		if (audioTrackValuesMap.containsKey(id)) {
		    			reviseAudioSection(element, id, audioTrackValuesMap);
		    		}	    		
		    		
		    	} // "track"
		    	
		    }  // for (Element element : elementList) {

		    
		} catch(JDOMException e) {
			throw new FitsToolException("Error revising xml node values " + TOOL_NAME);			
		}		    

	}
	
	private String convertIdToMediaInfoFormat(String id) {
		return id.replaceAll("\\s*\\([^\\)]*\\)\\s*", "");
	}	
	
	/**
	 * Helper method to revise text in the identification element.
	 * @param fitsXml
	 * @throws JDOMException
	 */
	private void reviseIdentification (Document fitsXml ) 
					throws JDOMException {
		
		//
		// Get the format and formatProfile from the video track element
		//
		String videoFormat = "";
		String videoFormatProfile = "";
	    XPath xpathFits = XPath.newInstance("//x:fits/x:metadata/x:video");	
		
	    // NOTE: We need to add a namespace	to xpath, because JDom XPath 
	    // does not support default namespaces. It requires you to add a
	    // fake namespace to the XPath instance.
	    xpathFits.addNamespace("x", fitsXml.getRootElement().getNamespaceURI());

	    Element videoElement = (Element)xpathFits.selectSingleNode(fitsXml);
	    List <Element>elementList = videoElement.getContent();
	    for (Element element : elementList) {
	    	if(element.getName().equals("format")) {
	    		videoFormat = element.getText();
	    	}
	    	else if(element.getName().equals("formatProfile")) {
	    		videoFormatProfile = element.getText();
	    	}
	    }		
		
		//
		// Normalize the format and mimetype if necessary.
		// NOTE: If the following is true, then we need to update the 
		// "identity" element to be "quicktime" for the format attribute 
		// and "video/quicktime" for the mimetype attribute
		//
	    XPath xpathFitsIdentity = XPath.newInstance("//x:fits/x:identification");
	    
	    // NOTE: We need to add a namespace	to xpath, because JDom XPath 
	    // does not support default namespaces. It requires you to add a
	    // fake namespace to the XPath instance.
	    xpathFitsIdentity.addNamespace("x", fitsXml.getRootElement().getNamespaceURI());

	    Element identityElement = (Element)xpathFitsIdentity.selectSingleNode(fitsXml);
	    List <Element>elementIdentityList = identityElement.getContent();
	    for (Element elementIdentity : elementIdentityList) {
	    	
	    	Attribute formatAttrib = elementIdentity.getAttribute("format"); 	
	    	Attribute mimeAttrib = elementIdentity.getAttribute("mimetype");
	    	
	    	// Only Reset the format and mimetype if they are the format and
	    	// formatProfile from the video section are the required types
	    	if(formatAttrib != null && mimeAttrib != null) {
		    	if(videoFormat.toUpperCase().contains("MPEG-4") && videoFormatProfile.toUpperCase().equals("QUICKTIME")){	    		
		    		formatAttrib.setValue("Quicktime");
		    		mimeAttrib.setValue("video/quicktime");
		    	}
	    		break;
	    	}
	    	

	    	
	    }		
		
	}
	
	/**
	 * Helper method to revise text in the element passed in. If the element
	 * is found to have an associated value in the map, the current text of 
	 * the element is revised.
	 * 
	 * @param element jdom element to be revised
	 * @param generalValuesDataMap map holding video elements to revise
	 */
	private void reviseGeneralSection(Element element, 
			Map<String, String> generalValuesDataMap) {
		
    	// General size
    	if(element.getName().equals("size")) {
    		String generalFileSize = generalValuesDataMap.get("generalFileSize");
			if (!StringUtils.isEmpty(generalFileSize)) {
				element.setText(generalFileSize);
			}
    	}    	
    	
    	// General Section dateModified
    	else if(element.getName().equals("dateModified")) {
    		String dateModified = generalValuesDataMap.get("dateModified");
			if (!StringUtils.isEmpty(dateModified)) {
				element.setText(dateModified);
			}		    		
    	}
    	// General Section timecodeStart
    	else if(element.getName().equals("timecodeStart")) {
    		String timeCodeStart = generalValuesDataMap.get("timeCodeStart");
			if (!StringUtils.isEmpty(timeCodeStart)) {
				element.setText(timeCodeStart);
			}		    		
    	}
    	// General Section bit rate
    	else if(element.getName().equals("bitRate")) {
    		String generalBitRate = generalValuesDataMap.get("generalBitRate");
			if (!StringUtils.isEmpty(generalBitRate)) {
				element.setText(generalBitRate);
			}		    		
    	}
    	
    	// General Section duration
    	else if(element.getName().equals("duration")) {
    		String generalDuration = generalValuesDataMap.get("generalDuration");
			if (!StringUtils.isEmpty(generalDuration)) {
				element.setText(generalDuration);
			}		    		
    	}
    	
    	// For now, the MD5 will be put in Ebucore manually AFTER FITS generates it.
    	//// General Section  MD5
    	//else if(element.getName().equals("filemd5")) {
    	//	String generalMd5 = generalValuesDataMap.get("md5Hash");
		//	if (!StringUtils.isEmpty(generalMd5)) {
		//		element.setText(generalMd5);
		//	}		    		
    	//}
 
		// General Section formatProfile - If missing, use value from 
    	// video section, which was set above
    	else if(element.getName().equals("formatProfile")) {
			String formatProfileFromElement = element.getValue();
			// if value for element is missing or empty, we need to update it
			if(StringUtils.isEmpty(formatProfileFromElement)) {
				String generalFormatProfileFromVideo = generalValuesDataMap.get("formatProfileFromElement");
				if(generalFormatProfileFromVideo != null && generalFormatProfileFromVideo.length() > 0) {
					element.setText(generalFormatProfileFromVideo);
				}
			}
			
		}
		
	}
	
	/**
	 * Wrapper to the MediaInfo Get method to retrieve a MediaInfo String from
	 * stream number 0, of info type text, using a named field.
	 * 
	 * @param fieldName MediaInfo Field
	 * @param streamType MediaInfoNativeWrapper.InfoKind
	 * @return The data as a String
	 */
	private String getMediaInfoString(String fieldName, 
			MediaInfoNativeWrapper.StreamKind streamType) {
		return getMediaInfoString(0, fieldName, streamType);
	}
	
	/**
	 * Wrapper to the MediaInfo Get method to retrieve a MediaInfo String from
	 * the stream number passed in, of info type text, using a named field.
	 * 
	 * @param streamNumber MediaInfo stream number
	 * @param fieldName MediaInfo Field
	 * @param streamType MediaInfoNativeWrapper.InfoKind
	 * @return The data as a String
	 */
	private String getMediaInfoString(int streamNumber, String fieldName, 
			MediaInfoNativeWrapper.StreamKind streamType) {
		return mi.Get(streamType, streamNumber, fieldName, 
				MediaInfoNativeWrapper.InfoKind.Text, 
	    		MediaInfoNativeWrapper.InfoKind.Name);
	}
	
	private String convertToNormalizedData(String originalString, String labelToPreserve) {
		return StringUtils.deleteWhitespace(originalString.replace(labelToPreserve, "")) + labelToPreserve;

	}	
	
	/**
	 * Helper method to revise text in the video track element passed in. The 
	 * id is used as a key to a map of values. If the element's name is found 
	 * in the map, the value in the map is used to replace the current text of 
	 * the element.
	 * 
	 * @param element jdom element to be revised
	 * @param id track key to the map value
	 * @param videoTrackValuesMap map holding video elements to revise
	 */
    private void reviseVideoSection(Element element, String id, 
    		Map<String, Map<String, String>> videoTrackValuesMap) {
    	
    	// Remove any empty elements
    	// Right now it is only scanning order
    	List<Element> elementsToRemove = new ArrayList<Element>();

    	List <Element>contents = element.getContent();		    				
    	for (Element childElement : contents) {
    		String name = childElement.getName();
    		
       		// If the element name is one which has to be normalized, do so.
    		ElementsToNormalize elementToNomalize = ElementsToNormalize.lookup(name);
    		if(elementToNomalize != null) {
    			String value = childElement.getValue();
    			if(!StringUtils.isEmpty(value)) {
        			value = convertToNormalizedData(value, elementToNomalize.getUnits());    				
    				// If we got here, then we need to update the element's text
    				if(!StringUtils.isEmpty(value))
    					childElement.setText(value);     				
    			}
    			continue;
    		}
    		// End normalize
    		
    		
    		// If the "name" is not contained in the enum, continue
    		VideoMethods videoValueEnum = VideoMethods.lookup(name);
    		if(videoValueEnum == null) 
    			continue;

    		String value = videoTrackValuesMap.get(id).get(videoValueEnum.getName());
    		switch (videoValueEnum) {
    		// bitRate
    		// 1) correct format
    		// 2) set to bitRateMax, if mode is variable
    		case bitRate:
    			// NOTE: If the bitRateMode is Variable (VBR), set it to the value for
    			// BitRateMax
    			String bitRateMode = videoTrackValuesMap.get(id).get("bitRateMode");
    			if(!StringUtils.isEmpty(bitRateMode) && bitRateMode.toUpperCase().equals("VBR")) {
    				String bitRateMax = videoTrackValuesMap.get(id).get("bitRateMax");
    				if(!StringUtils.isEmpty(bitRateMax)) {
    					childElement.setText(bitRateMax);
    					value = bitRateMax;
    				}
    			}
    			break;
    		case frameRate:
    			// NOTE: If the bitRateMode is Variable (VBR), set it to the value for
    			// BitRateMax
    			String frameRateMode = videoTrackValuesMap.get(id).get("frameRateMode");
    			if(!StringUtils.isEmpty(frameRateMode) && frameRateMode.toUpperCase().equals("VFR")) {
    				String frameRateMax = videoTrackValuesMap.get(id).get("frameRateMax");
    				if(!StringUtils.isEmpty(frameRateMax)) {
    					childElement.setText(frameRateMax);
    					value = frameRateMax;
    				}
    			}
    			break;
    			
    		case scanningOrder:
    			// TODO: Fix the below ...
    			// It was noticed that might display BFF (Bottom Filed First),
    			// instead of TFF (Top Field First) for interlaced videos
    			// See:
    			// https://mediaarea.net/en-us/MediaInfo/Support/FAQ
    			// Section:
    			// MediaInfo states video is Bottom Filed First when it is actually Top Field First
        		if(!StringUtils.isEmpty(value))
        			childElement.setText(value);
        		else {	// Remove the child element if it is empty
        			elementsToRemove.add(childElement);
        		}
    			break;
    		default:
    			break;

    		}	// switch

    		// If we got here, then we need to update the element's text
    		if(!StringUtils.isEmpty(value))
    			childElement.setText(value);

    	} // childElement
    	
    	// Remove all elements marked as empty
    	for (Element elementToRemove : elementsToRemove) {
    		elementToRemove.getParent().removeContent(elementToRemove);
    	}
    }
    
	/**
	 * Helper method to revise text in the audio track element passed in. The 
	 * id is used as a key to a map of values. If the element's name is found 
	 * in the map, the value in the map is used to replace the current text of 
	 * the element.
	 * 
	 * @param element jdom element to be revised
	 * @param id track key to the map value
	 * @param  audioTrackValuesMap map holding audio elements to revise
	 */
    private void reviseAudioSection(Element element, String id, 
    		Map<String, Map<String, String>> audioTrackValuesMap) {

    	List <Element>contents = element.getContent();		    				
    	for (Element childElement : contents) {
    		String name = childElement.getName();
    		
    		// If the "name" is not contained in the enum, continue
    		AudioMethods audioValueEnum = AudioMethods.lookup(name);
    		if(audioValueEnum == null) 
    			continue;

    		String value = audioTrackValuesMap.get(id).get(audioValueEnum.getName());
    		switch (audioValueEnum) {
    		// bitRate
    		// 1) correct format
    		// 2) set to bitRateMax, if mode is variable
    		case bitRate:
    			// NOTE: If the bitRateMode is Variable (VBR), set it to the value for
    			// BitRateMax
    			String bitRateMode = audioTrackValuesMap.get(id).get("bitRateMode");
    			if(!StringUtils.isEmpty(bitRateMode) && bitRateMode.toUpperCase().equals("VBR")) {
    				String bitRateMax = audioTrackValuesMap.get(id).get("bitRateMax");
    				if(!StringUtils.isEmpty(bitRateMax)) {
    					childElement.setText(bitRateMax);
    					value = bitRateMax;
    				}
    			}
    			break;
    		default:
    			break;

    		}	// switch

    		// If we got here, then we need to update the element's text
    		if(!StringUtils.isEmpty(value))
    			childElement.setText(value);

    	} // childElement
    }
    
	protected void removeEmptyElements (Document fitsXml) throws FitsToolException {
		
    	// Remove any empty elements
    	// Right now it is only scanning order
    	List<Element> elementsToRemove = new ArrayList<Element>();
		
		try {
			
		    XPath xpathFits = XPath.newInstance("//x:fits/x:metadata/x:video");	
			
		    // NOTE: We need to add a namespace	to xpath, because JDom XPath 
		    // does not support default namespaces. It requires you to add a
		    // fake namespace to the XPath instance.
		    xpathFits.addNamespace("x", fitsXml.getRootElement().getNamespaceURI());

		    Element videoElement = (Element)xpathFits.selectSingleNode(fitsXml);
		    List <Element>elementList = videoElement.getContent();
		    for (Element element : elementList) {	
				
		    	// Tracks
		    	if(element.getName().equals("track")) {
		    		
		        	List <Element>contents = element.getContent();		    				
		        	for (Element childElement : contents) {
		        		String name = childElement.getName();
		        		
		        		String value = childElement.getText();
		        		if(StringUtils.isEmpty(value)) {
		        			elementsToRemove.add(childElement);
		        		}
		        	}
		    		
		    	}
		    }

		} catch(JDOMException e) {
			throw new FitsToolException("Error revising xml node values " + TOOL_NAME);			
		}
		
    	// Remove all elements marked as empty
    	for (Element elementToRemove : elementsToRemove) {
    		elementToRemove.getParent().removeContent(elementToRemove);
    	}

	}
}

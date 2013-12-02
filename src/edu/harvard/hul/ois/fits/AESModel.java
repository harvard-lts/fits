/**********************************************************************
 * Copyright (c) 2009 by the President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Contact information
 *
 * Office for Information Systems
 * Harvard University Library
 * Harvard University
 * Cambridge, MA  02138
 * (617)495-3724
 * hulois@hulmail.harvard.edu
 **********************************************************************/

package edu.harvard.hul.ois.fits;

import java.util.UUID;

import edu.harvard.hul.ois.ots.schemas.AES.AudioObject;
import edu.harvard.hul.ois.ots.schemas.AES.BitrateReduction;
import edu.harvard.hul.ois.ots.schemas.AES.ChannelAssignment;
import edu.harvard.hul.ois.ots.schemas.AES.EditUnitNumber;
import edu.harvard.hul.ois.ots.schemas.AES.Face;
import edu.harvard.hul.ois.ots.schemas.AES.FaceRegion;
import edu.harvard.hul.ois.ots.schemas.AES.Format;
import edu.harvard.hul.ois.ots.schemas.AES.FormatList;
import edu.harvard.hul.ois.ots.schemas.AES.FormatRegion;
import edu.harvard.hul.ois.ots.schemas.AES.GenericFormatRegion;
import edu.harvard.hul.ois.ots.schemas.AES.Identifier;
import edu.harvard.hul.ois.ots.schemas.AES.Stream;
import edu.harvard.hul.ois.ots.schemas.AES.TimeRange;
import edu.harvard.hul.ois.ots.schemas.AES.FormatRegion.regionTypeEnum;
import edu.harvard.hul.ois.ots.schemas.AES.Use;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;

public class AESModel {

    protected AudioObject aes;
    protected Face face;
    protected FaceRegion region;
    protected FormatRegion formatRegion;
    protected GenericFormatRegion genericFormatRegion;
    protected FormatList formatList;
    protected TimeRange timeline;
    protected TimeRange timeRange;
    protected BitrateReduction brr;
    
    protected final String audioObjectID = "AUDIO_OBJECT_"+UUID.randomUUID().toString();
    protected final String faceID = "FACE_"+UUID.randomUUID().toString();
    protected final String regionID = "REGION_"+UUID.randomUUID().toString();
    protected final String formatRegionID = "FORMAT_REGION_"+UUID.randomUUID().toString();
	

    protected AESModel () throws XmlContentException {
    	
    	//set up base AES object structure    	
        aes = new AudioObject ();
        aes.setSchemaVersion("1.0.0");
        aes.setID(audioObjectID);
        aes.setDisposition("");
        Identifier ident = new Identifier("","primaryIdentifier");
        ident.setIdentifierType("FILE_NAME");
        aes.setPrimaryIdentifier(ident);
            	
    	face = new Face();
    	face.setLabel("face 1");
    	face.setDirection("NONE");
    	face.setID(faceID);
    	face.setAudioObjectRef(audioObjectID);
    	aes.addFace(face);
    	
    	timeline = new TimeRange("timeline");
    	EditUnitNumber startTime = new EditUnitNumber(0,"startTime");
    	startTime.setEditRate(1);
    	timeline.setStartTime(startTime);
    	face.setTimeline(timeline);
    	
    	region = new FaceRegion();
    	region.setID(regionID);
    	region.setFaceRef(faceID);
    	region.setLabel("region 1");
    	timeRange = new TimeRange("timeRange");
    	timeRange.setStartTime(startTime);
    	region.setTimeRange(timeRange);
    	region.setFormatRef(formatRegionID);
    	face.addRegion(region);
    	
    	formatList = new FormatList();
    	
    	formatRegion = new FormatRegion(regionTypeEnum.GENERIC);
    	genericFormatRegion = (GenericFormatRegion) formatRegion.getContent();
    	genericFormatRegion.setID(formatRegionID);
    	genericFormatRegion.setOwnerRef(regionID);
    	genericFormatRegion.setLabel("format region 1");
   
    	formatList.addFormatRegion(formatRegion);
    	aes.setFormatList(formatList);
    
    }
    
    private void initBitRateReduction() throws XmlContentException {
    	brr = new BitrateReduction();
    	brr.setCodecName("");
    	brr.setCodecNameVersion("");
    	brr.setCodecCreatorApplication("");
    	brr.setCodecCreatorApplicationVersion("");
    	brr.setCodecQuality("LOSSY");
    	brr.setDataRate("");
    	brr.setDataRateMode("FIXED");
    	genericFormatRegion.addBitrateReduction(brr);
    }
    
    protected void setBitRate(String rate) throws XmlContentException {
    	if(brr == null) {
    		initBitRateReduction();
    	}
    	brr.setDataRate(rate);
    }
    protected void setCodec(String codec) throws XmlContentException {
    	if(brr == null) {
    		initBitRateReduction();
    	}
    	brr.setCodecName(codec);
    }
    protected void setCodecVersion(String codecVersion) throws XmlContentException {
    	if(brr == null) {
    		initBitRateReduction();
    	}
    	brr.setCodecNameVersion(codecVersion);
    }
    protected void setCodecCreatorApplication(String codecCreatorApp) throws XmlContentException {
    	if(brr == null) {
    		initBitRateReduction();
    	}
    	brr.setCodecCreatorApplication(codecCreatorApp);
    }
    protected void setCodecCreatorApplicationVersion(String codecCreatorAppVersion) throws XmlContentException {
    	if(brr == null) {
    		initBitRateReduction();
    	}
    	brr.setCodecCreatorApplicationVersion(codecCreatorAppVersion);
    }
    
    /**
     * sets the timeline and timeRage startTime
     * @param time
     * @param editRate
     * @throws XmlContentException
     */
    protected void setStartTime(String time, int editRate) throws XmlContentException {
    	EditUnitNumber startTime = getEditUnitNumber(time,editRate,0,"startTime");
    	//set timeline duration
    	timeline.setStartTime(startTime);
     	//set timeRange startTime
    	timeRange.setStartTime(startTime);
    }
    
    /**
     * Sets the timeline and timeRange duration
     * @param time
     * @param editRate
     * @param numSamples
     * @throws XmlContentException
     */
    protected void setDuration(String time, int editRate, long numSamples) throws XmlContentException {
    	EditUnitNumber duration = getEditUnitNumber(time,editRate,numSamples,"duration");
    	//set timeline duration
    	timeline.setDuration(duration);
    	//set timeRange duration
    	timeRange.setDuration(duration);    	
    }
    
    private EditUnitNumber getEditUnitNumber(String time, int editRate, long numSamples, String elementName) {
    	EditUnitNumber eun = null;
    	if(editRate != 0 && numSamples != 0) {
        	eun = new EditUnitNumber((int)numSamples,elementName);
        	eun.setEditRate(editRate);
    	}
    	else {
	    	double timeval = timeUnitToAddress(time);
	    	//check if time is a whole number
	    	if(Math.floor(timeval) == timeval) {
	    		//whole number so use seconds, use timeval as is
	    		editRate = 1;
	    	}
	    	else {
	    		//convert timevalto milliseconds
	    		timeval = timeval * 1000;
	    		editRate = 1000;
	    	}
	    	eun = new EditUnitNumber((int)timeval,elementName);
	    	eun.setEditRate(editRate);
    	}
    	return eun;
    }
    
    private double timeUnitToAddress(String time) {
    	String[] parts = time.split(":");
    	
    	double seconds = 0;
    	
    	// hours:minutes:seconds:milliseconds
    	if(parts.length >= 3) {
	    	//hours
	    	seconds = (Long.valueOf(parts[0]) * 60 * 60);
	    	//minutes
	    	seconds += Long.valueOf(parts[1]) * 60;
	    	//seconds
	    	seconds += Long.valueOf(parts[2]);
	    	//milliseconds
	    	if(parts.length == 4)
	    		seconds += Long.valueOf(parts[3]) / 1000.000;
    	}
    	// minutes:seconds
    	else if (parts.length == 2) {
	    	//minutes
	    	seconds += Long.valueOf(parts[0]) * 60;
	    	//seconds
	    	seconds += Long.valueOf(parts[1]);
    	}
    	return seconds;
    }
    
    /**
     * sets bps
     * @throws XmlContentException 
     */
    protected void setBitDepth(int bitDepth) throws XmlContentException {
    	genericFormatRegion.setBitDepth(bitDepth);
    }
    
    protected void setWordSize(int wordsize) throws XmlContentException {
    	genericFormatRegion.setWordSize(wordsize);
    }
    
    protected void setNumChannels(int num) throws XmlContentException {
    	region.setNumChannels(num);
		if (num == 1) {
			genericFormatRegion.setSoundField("MONO");
		}
		else if (num == 2) {
			genericFormatRegion.setSoundField("STEREO");
		}
		else if (num != 0) {
			genericFormatRegion.setSoundField("SURROUND");
		}
    }
    
    
    protected void setDummyUseType() throws XmlContentException {
    	if(aes.getUses().size() == 0) {
    		Use use = new Use();
	    	aes.addUse(use);
	    	use.setUseType("OTHER");
	    	use.setOtherType("unknown");
    	}
    }
        
    protected void addStream(int channelNum, double leftRightPos, double frontRearPos) throws XmlContentException {
    	Stream stream = new Stream();
    	stream.setID("STREAM_"+UUID.randomUUID().toString());
    	stream.setFaceRegionRef(regionID);
    	stream.setLabel("stream "+channelNum);
    	ChannelAssignment channelAssignment = new ChannelAssignment();
    	channelAssignment.setChannelNum(channelNum);
    	channelAssignment.setLeftRightPosition(leftRightPos);
    	channelAssignment.setFrontRearPosition(frontRearPos);
    	stream.setChannelAssignment(channelAssignment);
    	region.addStream(stream);
    }
    
    protected void setFormat(String format, String version) throws XmlContentException {
    	Format formatElem = new Format(format);
    	if(version != null && version.length() > 0) {
    		formatElem.setAttribute("specificationVersion", version);
    	}
    	aes.setFormat(formatElem);
    }
    
    protected void setAudioDataEncoding(String encoding) throws XmlContentException {
    	aes.setAudioDataEncoding(encoding);
    }
    
    protected void setAudioDataBlockSize(int adbs) throws XmlContentException {
    	aes.setAudioDataBlockSize(adbs);
    }
}

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
package edu.harvard.hul.ois.fits;

//import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.Namespace;

import edu.harvard.hul.ois.ots.schemas.Ebucore.AspectRatio;
import edu.harvard.hul.ois.ots.schemas.Ebucore.AudioEncoding;
import edu.harvard.hul.ois.ots.schemas.Ebucore.AudioFormat;
//import edu.harvard.hul.ois.ots.schemas.Ebucore.AudioFormatExtended;
import edu.harvard.hul.ois.ots.schemas.Ebucore.AudioTrack;
import edu.harvard.hul.ois.ots.schemas.Ebucore.AudioTrackConfiguration;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Codec;
import edu.harvard.hul.ois.ots.schemas.Ebucore.CodecIdentifier;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Comment;
import edu.harvard.hul.ois.ots.schemas.Ebucore.ContainerFormat;
import edu.harvard.hul.ois.ots.schemas.Ebucore.CoreMetadata;
import edu.harvard.hul.ois.ots.schemas.Ebucore.DateTime;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Duration;
import edu.harvard.hul.ois.ots.schemas.Ebucore.EditUnitNumber;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Format;
import edu.harvard.hul.ois.ots.schemas.Ebucore.EbuCoreMain;
import edu.harvard.hul.ois.ots.schemas.Ebucore.FrameRate;
import edu.harvard.hul.ois.ots.schemas.Ebucore.HeightIdentifier;
import edu.harvard.hul.ois.ots.schemas.Ebucore.MimeType;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Start;
import edu.harvard.hul.ois.ots.schemas.Ebucore.TechnicalAttributeInteger;
import edu.harvard.hul.ois.ots.schemas.Ebucore.TechnicalAttributeString;
import edu.harvard.hul.ois.ots.schemas.Ebucore.VideoEncoding;
import edu.harvard.hul.ois.ots.schemas.Ebucore.VideoFormat;
import edu.harvard.hul.ois.ots.schemas.Ebucore.VideoTrack;
import edu.harvard.hul.ois.ots.schemas.Ebucore.WidthIdentifier;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;

public class EbuCoreModel {
	
	private final static String UTC_TEXT = "UTC ";
	
    protected EbuCoreMain ebucoreMain;
    protected Format format;
    
    // TODO: Use audioFmtExt?
    // protected AudioFormatExtended audioFmtExt;
    protected ContainerFormat containerFormat;
    protected Duration duration;
    
    //protected final String ebucoreID = "EBUCORE_"+UUID.randomUUID().toString();   
    
    protected EbuCoreModel () throws XmlContentException {
    	
    	//set up base ebucore main object structure    	
    	ebucoreMain = new EbuCoreMain ();
    	
    	//ebucoreMain.setSchemaVersion("1.0.0");
    	//ebucoreMain.setID(ebucoreID);
        //ebucoreMain.setDisposition("");
    	
        //Identifier ident = new Identifier("","primaryIdentifier");
        //ident.setIdentifierType("FILE_NAME");
        //video.setPrimaryIdentifier(ident);

        containerFormat = new ContainerFormat();
        duration = new Duration();
		
		format = new Format("format");
		format.setContainerFormat(containerFormat);
		format.setDuration(duration);
		
		// TODO: Do we need this
		// format.setAudioFormatExtended(audioFmtExt);
		
        CoreMetadata cm = new CoreMetadata();
        cm.setFormat(format);

        ebucoreMain.setCoreMetadata(cm);
    }
	
    protected void createVideoFormatElement(Element elem, Namespace ns) 
    		throws XmlContentException {

    	VideoFormat vfmt = new VideoFormat("videoFormat");

    	String id = elem.getAttribute("id").getValue();
    	VideoTrack vt = new VideoTrack();
    	vt.setTrackId(id);         			
    	vfmt.setVideoTrack(vt); 

    	for (VideoFormatElements videoElem : VideoFormatElements.values()) {

    		String fitsName = videoElem.getName ();
    		Element dataElement = elem.getChild (fitsName,ns);
    		if (dataElement == null)
    			continue;
    		String dataValue = dataElement.getText().trim();
    		if (StringUtils.isEmpty(dataValue))
    			continue;
    		switch (videoElem) {	

    		case width:
    			dataValue = StringUtils.deleteWhitespace(
    					dataElement.getText().replace(" pixels", ""));
    			WidthIdentifier width = new WidthIdentifier(
    					dataValue,
    					videoElem.getName());                                
    			width.setUnit("pixel");
    			vfmt.setWidthIdentifier(width);
    			break;
    		case height:
        		dataValue = StringUtils.deleteWhitespace(
        				dataElement.getText().replace(" pixels", ""));
        		HeightIdentifier height = new HeightIdentifier(
        				dataValue,
        				videoElem.getName());                             
        		height.setUnit("pixel");
        		vfmt.setHeightIdentifier(height);   			
    			break;
    		case frameRate:
        		EbuCoreFrameRateRatio frRatio = 
					new EbuCoreFrameRateRatio(dataValue);

        		FrameRate frameRate = new FrameRate(frRatio.getValue(),
        				videoElem.getName());
        		frameRate.setFactorNumerator(frRatio.getNumerator());
        		frameRate.setFactorDenominator(frRatio.getDenominator());
        		vfmt.setFrameRate(frameRate);
    			break;
    		case bitRate:
        		vfmt.setBitRate(Integer.parseInt(dataValue));
        		break;
    		case bitRateMax:
    			vfmt.setBitRateMax(Integer.parseInt(dataValue));
    			break;
    		case bitRateMode:
    			vfmt.setBitRateMode(dataValue.toLowerCase());
    			break;
    		case scanningFormat:
        		vfmt.setScanningFormat(dataValue.toLowerCase());
    			break;
    		case videoDataEncoding:
        		VideoEncoding ve = new VideoEncoding();
        		ve.setTypeLabel(dataValue);
        		vfmt.setVideoEncoding(ve); 
        		break;
    		case aspectRatio:
        		String[] splitValues = dataValue.split(":");
        		// TODO: throw exception if there are not 2 pieces
        		if (splitValues != null && splitValues.length == 2) {
        			AspectRatio ar = new AspectRatio(videoElem.getName());

        			// Normalize the ratio
        			EbuCoreNormalizedRatio ratio = new EbuCoreNormalizedRatio(
        					splitValues[0], splitValues[1]);

        			ar.setFactorNumerator(ratio.getNormalizedNumerator());
        			ar.setFactorDenominator(ratio.getNormalizedDenominator());
        			ar.setTypeLabel("display");
        			vfmt.setAspectRatio(ar);
        		}
    			break;
    			
    		// Technical Attribute Strings
    		case colorspace: 			
        		TechnicalAttributeString tas =
    				new TechnicalAttributeString(dataValue);   		
        		tas.setTypeLabel(videoElem.getName());
        		vfmt.addTechnicalAttributeString(tas);
        		break;
			
    		case chromaSubsampling:
    		case frameRateMode:
    		case byteOrder:
    		case delay:
    		case compression:
        		TechnicalAttributeString tasLc =
    				new TechnicalAttributeString(dataValue.toLowerCase());        		
        		tasLc.setTypeLabel(videoElem.getName());
        		vfmt.addTechnicalAttributeString(tasLc);
    			break;
    			
        	// Technical Attribute Integers	
    		case streamSize:
    		case frameCount:
    		case bitDepth:
    		case duration:
	    		// For bitDepth, the string might have "bits" at the tail, so 
	    		// we need to remove it
    			if (videoElem.getName().equals("bitDepth")) {
    	    		String[] parts = dataValue.
    	    				split(" ");
    	    		dataValue = parts[0];
    			}
        		TechnicalAttributeInteger tai = 
        				new TechnicalAttributeInteger(Integer.
						parseInt(dataValue));
        		tai.setTypeLabel(videoElem.getName());
        		vfmt.addTechnicalAttributeInteger(tai);
    			break;	
    			
    		default:
    			break;
    		}
    	}
    	

    	// TODO: break this out to enum code
    	// Codec element
   		Element dataElement = elem.getChild ("codecId",ns);
        if(dataElement != null) {
        	String dataValue = dataElement.getText().trim();
        	CodecIdentifier ci = new CodecIdentifier("codecIdentifier");
        	ci.setIdentifier(dataValue);
        	
        	Codec codec = new Codec("codec");
           	codec.setCodecIdentifier(ci);
           	
           	Element codecElement = elem.getChild ("codecInfo",ns);
        	if(codecElement != null) {
        		dataValue = codecElement.getText().trim();
        		codec.setInfo(dataValue);
        	}           	
        	codecElement = elem.getChild ("codecFamily",ns);
        	if(codecElement != null) {
        		dataValue = codecElement.getText().trim();
        		codec.setFamily(dataValue);
        	}
        	vfmt.setCodec(codec);  
        }

        // Add the audio format object to the list
        this.format.addVideoFormat(vfmt);
    }
    
    protected void createAudioFormatElement(Element elem, Namespace ns) 
    		throws XmlContentException {

    	AudioFormat afmt = new AudioFormat("audioFormat");

    	Element encodingDataElement = elem.getChild ("audioDataEncoding",ns);
    	if (encodingDataElement != null) {
    		String dataValue = encodingDataElement.getText().trim();                   			
    		AudioEncoding ae = new AudioEncoding();
    		ae.setTypeLabel(dataValue);
    		// Type Link NOT in AVPreserve example, so don't expose
    		// ae.setTypeLink("http://www.ebu.ch/metadata/cs/ebu_AudioCompressionCodeCS.xml#11");
    		afmt.setAudioEncoding(ae);

    		//afmt.setAudioFormatName(dataValue);
    	}

    	String id = elem.getAttribute("id").getValue();                  			
    	AudioTrack at = new AudioTrack();
    	at.setTrackId(id);                			
    	afmt.setAudioTrack(at);

    	for (AudioFormatElements audioElem : AudioFormatElements.values()) {

    		// NOTE: use the .name(), not .getName(), so we can use the correct
    		// value to set the typeLabel
    		String fitsName = audioElem.name();
    		Element dataElement = elem.getChild (fitsName,ns);
    		if (dataElement == null)
    			continue;
    		String dataValue = dataElement.getText().trim();
    		if (StringUtils.isEmpty(dataValue))
    			continue;
    		    		
    		switch (audioElem) {
    		
    		case soundField:                 			
	    		AudioTrackConfiguration atc = new AudioTrackConfiguration();
	    		atc.setTypeLabel(dataValue);
	    		afmt.setAudioTrackConfiguration(atc);
    			break;	
    		case bitRate:
    	    	afmt.setBitRate(Integer.parseInt(dataValue));
    	    	break;
    		case bitRateMode:
   	    		afmt.setBitRateMode(dataValue.toLowerCase());
   	    		break;
    		case samplingRate:
	    		afmt.setSamplingRate(Integer.parseInt(dataValue));
	    		break;
    		case sampleSize:
	    		afmt.setSampleSize(dataValue);
	    		break;
    		case channels:
  	    		afmt.setChannels(Integer.parseInt(dataValue));
	    		break;
  			
    		// Technical Attribute Strings
    		case byteOrder:
    		case delay:
    		case compression:
 	    		TechnicalAttributeString tas =
				new TechnicalAttributeString(dataValue.toLowerCase());
 	    		tas.setTypeLabel(audioElem.getEbucoreName());
 	    		afmt.addTechnicalAttributeString(tas);
 	    		break;
 	    		
 	    	// Technical Attribute Integers	
    		case trackSize:
    		case numSamples:
    		case duration:
	    		TechnicalAttributeInteger tai = 
					new TechnicalAttributeInteger(Integer.
					parseInt(dataValue));
	    			tai.setTypeLabel(audioElem.getEbucoreName());
	    		afmt.addTechnicalAttributeInteger(tai);
    			break;
    			
    		default:
    			break;
    		}
    	}

    	// TODO: break this out to enum code
    	// Codec element
   		Element dataElement = elem.getChild ("codecId",ns);
        if(dataElement != null) {
        	String dataValue = dataElement.getText().trim();
        	CodecIdentifier ci = new CodecIdentifier("codecIdentifier");
        	ci.setIdentifier(dataValue);
        	
        	Codec codec = new Codec("codec");
           	codec.setCodecIdentifier(ci);
           	
           	Element codecElement = elem.getChild ("codecInfo",ns);
        	if(codecElement != null) {
        		dataValue = codecElement.getText().trim();
        		codec.setInfo(dataValue);
        	}           	
        	//codecElement = elem.getChild ("codecFamily",ns);
        	//if(codecElement != null) {
        	//	dataValue = codecElement.getText().trim();
        	//	codec.setFamily(dataValue);
        	//}
        	afmt.setCodec(codec);  
        }    	

        // Add the audio format object to the list
        this.format.addAudioFormat(afmt);
    }    
    
    protected void createFormatElement(String fitsName, Element elem) 
    		throws XmlContentException {

    	String dataValue = elem.getText().trim();
    	if (StringUtils.isEmpty(dataValue)) {
    		// TODO: Log and throw Error
    		return;
    	}

    	FormatElements formatElem = FormatElements.lookup(fitsName);
    	if (formatElem == null) {
    		// TODO: Log and throw Error
    		return;
    	}

    	switch (formatElem) {
    	// Note: size may be filtered off by the consolidator
    	case size:
    		this.format.setFileSize(dataValue);
    		break;
    	// Note: filename may be filtered off by the consolidator
    	case filename:
    		this.format.setFileName(dataValue);
    		break;
    	case mimeType:
    		MimeType mimeType  = new MimeType();
    		mimeType.setTypeLabel(dataValue);
    		this.format.setMimeType(mimeType);
    		break;
    	case location:
    		this.format.setLocator(dataValue);
    		break;
    	case bitRate:
    		TechnicalAttributeString tas =
    			new TechnicalAttributeString(dataValue);
    		tas.setTypeLabel("overallBitRate");
    		this.format.setTechnicalAttributeString(tas);
    		break;
    	case dateCreated:
    	case dateModified:
    		String parts[] = dataValue.replace(UTC_TEXT, "").split(" ");
    		DateTime dateTime = new DateTime(formatElem.getEbucoreName());
    		dateTime.setStartDate(parts[0]);
    		dateTime.setStartTime(parts[1]+"Z");                		
    		this.format.setDateCreated(dateTime);
    		break;
    	case formatProfile:
    	case format:
    		Comment comment = new Comment(dataValue);
    		this.containerFormat.addComment(comment);
    		break;
    	case duration:
    		Integer intValue = new Integer(dataValue);
    		EditUnitNumber eun = new EditUnitNumber(intValue, "editUnitNumber");
    		eun.setEditRate(1000);
    		eun.setFactorNumerator(1);
    		eun.setFactorDenominator(1); 
    		this.duration.setDuration(eun);
    		break;

    	default:
    		break;
    	}
    	
    }

    protected void createStart(String timecode, String framerate)
    		throws XmlContentException {
    	
    	if(timecode.equals("NOT_SET") || framerate.equals("NOT_SET"))
    		return;
    	
        int hours = Integer.parseInt(timecode.substring(0, 2));
        int minutes = Integer.parseInt(timecode.substring(3, 5));
        int seconds = Integer.parseInt(timecode.substring(6, 8));
        int framesFromTc = Integer.parseInt(timecode.substring(9, 11 ));
        
        EbuCoreFrameRateRatio ratio = new EbuCoreFrameRateRatio(framerate);
        int fps = new Integer (ratio.getValue());
        
        // hours
        int frames = (int)(( double)hours * 3600 * fps);
        
        // minutes
        frames += (double) minutes * 60 * fps;
        
        // seconds
        double tmpFrame = (double)seconds * fps;
        // Not necessary, as normalization is already done in the
        // EbuCoreFrameRateRatio class
        //if (tmpFrame > Math.floor(tmpFrame)) 
        //	tmpFrame = Math.floor(tmpFrame) + 1;
        frames += tmpFrame;
        
        // frames
        frames += framesFromTc;

		EditUnitNumber eun = new EditUnitNumber(new Integer(frames), "editUnitNumber");
		eun.setEditRate(fps);
		eun.setFactorNumerator(new Integer(ratio.getNumerator()));
		eun.setFactorDenominator(new Integer(ratio.getDenominator()));

		Start start = new Start("start");
		start.setTimecode(eun);
		this.format.setStart(start);
    
    }

//    public static void main(String[] args) throws XmlContentException {
//    	
//    	String timecode = "00:00:01:00";
//    	String framerate = "29.97";
//    	
//    	EbuCoreModel test = new EbuCoreModel();
//    	
//    	test.createStart(timecode, framerate);
//    }

}

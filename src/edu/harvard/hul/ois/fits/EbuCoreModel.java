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
import edu.harvard.hul.ois.ots.schemas.Ebucore.Comment;
import edu.harvard.hul.ois.ots.schemas.Ebucore.ContainerFormat;
import edu.harvard.hul.ois.ots.schemas.Ebucore.CoreMetadata;
import edu.harvard.hul.ois.ots.schemas.Ebucore.DateTime;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Duration;
import edu.harvard.hul.ois.ots.schemas.Ebucore.DurationInner;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Format;
import edu.harvard.hul.ois.ots.schemas.Ebucore.EbuCoreMain;
import edu.harvard.hul.ois.ots.schemas.Ebucore.FrameRate;
import edu.harvard.hul.ois.ots.schemas.Ebucore.HeightIdentifier;
import edu.harvard.hul.ois.ots.schemas.Ebucore.MimeType;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Start;
import edu.harvard.hul.ois.ots.schemas.Ebucore.TechnicalAttributeInteger;
import edu.harvard.hul.ois.ots.schemas.Ebucore.TechnicalAttributeString;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Timecode;
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
		
        CoreMetadata cm = new CoreMetadata("coreMetadata");
        cm.setFormat(format);

        ebucoreMain.setFormat(cm);
    }
    

	
    protected void createVideoFormatElement(Element elem, Namespace ns) 
    		throws XmlContentException {

    	VideoFormat vfmt = new VideoFormat("videoFormat");

    	String id = elem.getAttribute("id").getValue();
    	VideoTrack vt = new VideoTrack();
    	vt.setTrackId(id);         			
    	vfmt.setVideoTrack(vt);                   			

    	Element dataElement = elem.getChild ("width",ns);
    	if(dataElement != null) {
    		String dataValue = dataElement.getText().replace(" pixels", "");
    		WidthIdentifier width = new WidthIdentifier(
    				StringUtils.deleteWhitespace(dataValue),
    				"width");                                
    		width.setUnit("pixel");
    		vfmt.setWidthIdentifier(width);
    	}                   			

    	dataElement = elem.getChild ("height",ns);
    	if(dataElement != null) {
    		String dataValue = dataElement.getText().replace(" pixels", "");
    		HeightIdentifier height = new HeightIdentifier(
    				StringUtils.deleteWhitespace(dataValue),
    				"height");                             
    		height.setUnit("pixel");
    		vfmt.setHeightIdentifier(height);                      
    	}

    	//
    	// Frame rate is calculated as follows:
    	//
    	// If FrameRate from MediaInfo is a whole number,
    	// then the following is true:
    	//
    	//   frameRate = the value from MediaInfo
    	//   numerator = 1
    	//   denominator = 1
    	//
    	// Otherwise the following is true:
    	//   frameRate = the value from MediaInfo rounded
    	//   numerator = 1000
    	//   denominator = 1001
    	//
    	dataElement = elem.getChild ("frameRate",ns);
    	if(dataElement != null) {
    		String dataValue = dataElement.getText().trim();

    		EbuCoreFrameRateRatio frRatio = 
    				new EbuCoreFrameRateRatio(dataValue);

    		FrameRate frameRate = new FrameRate(frRatio.getValue(),
    				"frameRate");
    		frameRate.setFactorNumerator(frRatio.getNumerator());
    		frameRate.setFactorDenominator(frRatio.getDenominator());
    		vfmt.setFrameRate(frameRate);                       
    	}                            

    	dataElement = elem.getChild ("bitRate",ns);
    	// bitRate might be missing
    	if(dataElement != null && StringUtils.isNotEmpty(dataElement.getValue())) {
    		vfmt.setBitRate(Integer.parseInt(
    				dataElement.getValue().trim()));
    	}

    	dataElement = elem.getChild ("bitRateMax",ns);
    	if(dataElement != null && dataElement.getValue() != null && dataElement.getValue().length() > 0) {
    		vfmt.setBitRateMax(Integer.parseInt(
    				dataElement.getValue().trim()));                           
    	}

    	dataElement = elem.getChild ("bitRateMode",ns);
    	if(dataElement != null) {
    		vfmt.setBitRateMode(
    				dataElement.getValue().trim().toLowerCase());                           
    	}

    	dataElement = elem.getChild ("scanningFormat",ns);
    	if(dataElement != null) {
    		vfmt.setScanningFormat(
    				dataElement.getValue().trim().toLowerCase());                           
    	}

    	dataElement = elem.getChild ("videoDataEncoding",ns);
    	if(dataElement != null && dataElement.getValue() != null && dataElement.getValue().length() > 0) {
    		String dataValue = dataElement.getText().trim();
    		VideoEncoding ve = new VideoEncoding();
    		ve.setTypeLabel(dataValue);
    		vfmt.setVideoEncoding(ve);                       	
    	}

    	dataElement = elem.getChild ("aspectRatio",ns);
    	if(dataElement != null) {
    		String dataValue = dataElement.getText().trim();
    		String[] splitValues = dataValue.split(":");
    		// TODO: throw exception if there are not 2 pieces
    		if (splitValues != null && splitValues.length == 2) {
    			AspectRatio ar = new AspectRatio("aspectRatio");

    			// Normalize the ratio
    			EbuCoreNormalizedRatio ratio = new EbuCoreNormalizedRatio(
    					splitValues[0], splitValues[1]);

    			ar.setFactorNumerator(ratio.getNormalizedNumerator());
    			ar.setFactorDenominator(ratio.getNormalizedDenominator());
    			ar.setTypeLabel("display");
    			vfmt.setAspectRatio(ar);
    		}

    	}

    	//
    	// TechnicalAttributeString
    	// TODO: Use an enum to reduce code and test errors
    	//
    	dataElement = elem.getChild ("chromaSubsampling",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("chromaSubsampling");
    		vfmt.addTechnicalAttributeString(tas);
    	}

    	dataElement = elem.getChild ("colorspace",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("colorspace");
    		vfmt.addTechnicalAttributeString(tas);
    	}

    	dataElement = elem.getChild ("frameRateMode",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("frameRateMode");
    		vfmt.addTechnicalAttributeString(tas);
    	}

    	dataElement = elem.getChild ("byteOrder",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("byteOrder");
    		vfmt.addTechnicalAttributeString(tas);
    	} 

    	dataElement = elem.getChild ("delay",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("delay");
    		vfmt.addTechnicalAttributeString(tas);
    	}             

    	dataElement = elem.getChild ("compression",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("compression");
    		vfmt.addTechnicalAttributeString(tas);
    	}

    	//
    	// TechnicalAttributeInteger
    	// TODO: Use an enum to reduce code and test errors
    	//
    	dataElement = elem.getChild ("streamSize",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeInteger tai = 
    				new TechnicalAttributeInteger(Integer.
    						parseInt(dataElement.getValue().trim()), 
    						"technicalAttributeInteger");
    		tai.setTypeLabel("streamSize");
    		vfmt.addTechnicalAttributeInteger(tai);
    	}
    	dataElement = elem.getChild ("frameCount",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeInteger tai = 
    				new TechnicalAttributeInteger(Integer.
    						parseInt(dataElement.getValue().trim()), 
    						"technicalAttributeInteger");
    		tai.setTypeLabel("frameCount");
    		vfmt.addTechnicalAttributeInteger(tai);
    	}
    	dataElement = elem.getChild ("bitDepth",ns) ;
    	if(dataElement != null) {
    		// the string might have bits, at the end, so 
    		// we need to remove it
    		String[] parts = dataElement.getValue().trim().
    				split(" ");
    		TechnicalAttributeInteger tai = 
    				new TechnicalAttributeInteger(Integer.
    						parseInt(parts[0]), 
    						"technicalAttributeInteger");
    		tai.setTypeLabel("bitDepth");
    		vfmt.addTechnicalAttributeInteger(tai);
    	}
    	dataElement = elem.getChild ("duration",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeInteger tai = 
    				new TechnicalAttributeInteger(Integer.
    						parseInt(dataElement.getValue().trim()), 
    						"technicalAttributeInteger");
    		tai.setTypeLabel("duration");
    		vfmt.addTechnicalAttributeInteger(tai);
    	}

        // Add the audio format object to the list
        this.format.addVideoFormat(vfmt);
    }
    
    protected void createAudioFormatElement(Element elem, Namespace ns) 
    		throws XmlContentException {

    	AudioFormat afmt = new AudioFormat("audioFormat");

    	Element dataElement = elem.getChild ("audioDataEncoding",ns);
    	if (dataElement != null) {
    		String dataValue = dataElement.getText().trim();                   			
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

    	dataElement = elem.getChild ("soundField",ns);
    	if (dataElement != null) {
    		String dataValue = dataElement.getText().trim();                   			
    		AudioTrackConfiguration atc = new AudioTrackConfiguration();
    		atc.setTypeLabel(dataValue);

    		afmt.setAudioTrackConfiguration(atc);
    	}

    	dataElement = elem.getChild ("bitRate",ns);
    	// bitRate might be missing
    	if(dataElement != null && dataElement.getValue() != null && dataElement.getValue().length() > 0) {
    		afmt.setBitRate(
    				Integer.parseInt(dataElement.getValue().trim()));                               
    	}

    	dataElement = elem.getChild ("bitRateMode",ns);
    	if(dataElement != null && !StringUtils.isEmpty(dataElement.getValue().trim())) {
    		afmt.setBitRateMode(
    				dataElement.getValue().trim().toLowerCase());
    	}                            

    	dataElement = elem.getChild ("samplingRate",ns);
    	if(dataElement != null) {
    		afmt.setSamplingRate(
    				Integer.parseInt(dataElement.getValue().trim()));
    	}                            

    	dataElement = elem.getChild ("sampleSize",ns);
    	if(dataElement != null) {
    		afmt.setSampleSize(dataElement.getValue().trim());                         
    	}                           

    	dataElement = elem.getChild ("channels",ns);
    	if(dataElement != null) {
    		afmt.setChannels(
    				Integer.parseInt(dataElement.getValue().trim()));                          
    	}

    	//
    	// TechnicalAttributeString
    	// TODO: Use an enum to reduce code and test errors
    	//
    	dataElement = elem.getChild ("byteOrder",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("byteOrder");
    		afmt.addTechnicalAttributeString(tas);
    	} 

    	dataElement = elem.getChild ("delay",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("delay");
    		afmt.addTechnicalAttributeString(tas);
    	}             

    	dataElement = elem.getChild ("compression",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeString tas = 
    				new TechnicalAttributeString(dataElement.getValue().trim(), "technicalAttributeString");
    		tas.setTypeLabel("compression");
    		afmt.addTechnicalAttributeString(tas);
    	}

    	//
    	// TechnicalAttributeInteger
    	// TODO: Use an enum to reduce code and test errors
    	//
    	dataElement = elem.getChild ("trackSize",ns) ;
    	if(dataElement != null && !StringUtils.isEmpty(dataElement.getValue().trim())) {
    		TechnicalAttributeInteger tai = 
    				new TechnicalAttributeInteger(Integer.
    						parseInt(dataElement.getValue().trim()), 
    						"technicalAttributeInteger");
    		tai.setTypeLabel("streamSize");
    		afmt.addTechnicalAttributeInteger(tai);
    	}
    	dataElement = elem.getChild ("numSamples",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeInteger tai = 
    				new TechnicalAttributeInteger(Integer.
    						parseInt(dataElement.getValue().trim()), 
    						"technicalAttributeInteger");
    		tai.setTypeLabel("sampleCount");
    		afmt.addTechnicalAttributeInteger(tai);
    	}
    	dataElement = elem.getChild ("duration",ns) ;
    	if(dataElement != null) {
    		TechnicalAttributeInteger tai = 
    				new TechnicalAttributeInteger(Integer.
    						parseInt(dataElement.getValue().trim()), 
    						"technicalAttributeInteger");
    		tai.setTypeLabel("duration");
    		afmt.addTechnicalAttributeInteger(tai);
    	}                            

        // Add the audio format object to the list
        this.format.addAudioFormat(afmt);
    }
    
    protected void createFormatElement(String fitsName, Element elem, Namespace ns) 
    		throws XmlContentException {

        if (fitsName.equals("size")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value))
        		this.format.setFileSize(value);
        }
        else if (fitsName.equals("filename")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value))
        		this.format.setFileName(value);
        }
        else if (fitsName.equals("mimeType")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value)) {                			
        		MimeType mimeType  = new MimeType();
        		mimeType.setTypeLabel(value);
        		this.format.setMimeType(mimeType);
        	}
        }                
        else if (fitsName.equals("location")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value))
        		this.format.setLocator(value);
        }
        else if (fitsName.equals("bitRate")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value)) {
        		TechnicalAttributeString tas = 
        				new TechnicalAttributeString(value, "technicalAttributeString");
        		tas.setTypeLabel("overallBitRate");
        		this.format.setTechnicalAttributeString(tas);
        	}
        }                
        else if (fitsName.equals("dateCreated")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value)) {
        		String parts[] = value.replace(UTC_TEXT, "").split(" ");
        		DateTime dateTime = new DateTime("dateCreated");
        		dateTime.setStartDate(parts[0]);
        		dateTime.setStartTime(parts[1]+"Z");                		
        		this.format.setDateCreated(dateTime);
        	}
        }
        else if (fitsName.equals("dateModified")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value)) {
        		String parts[] = value.replace(UTC_TEXT, "").split(" ");
        		DateTime dateTime = new DateTime("dateModified");
        		dateTime.setStartDate(parts[0]);
        		dateTime.setStartTime(parts[1]+"Z");                		
        		this.format.setDateModified(dateTime);
        	}
        }
        else if (fitsName.equals("formatProfile")) {               	
           	String value = elem.getText().trim();
           	if (!StringUtils.isEmpty(value)) {
        		Comment comment = new Comment(value);
        		this.containerFormat.addComment(comment);              		
        	}
        }
        else if (fitsName.equals("format")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value)) {
        		Comment comment = new Comment(value);
        		this.containerFormat.addComment(comment);
        	}
        }
        
        // Set the start/timecodeStart element
        else if (fitsName.equals("timecodeStart")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value)) {
        		Start start = new Start("start");
        		Timecode timecode = new Timecode(value);
        		start.setTimecode(timecode);
        		this.format.setStart(start);                		
        	}
        }
        
        // Create Duration::Duration                
        else if (fitsName.equals("duration")) {
        	String value = elem.getText().trim();
        	if (!StringUtils.isEmpty(value)) {
                DurationInner di = new DurationInner(value);
                this.duration.setDuration(di);
        	}
        }
    }    

}

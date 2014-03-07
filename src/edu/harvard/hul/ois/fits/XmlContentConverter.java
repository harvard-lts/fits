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


import java.io.File;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.ots.schemas.XmlContent.Rational;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlDateFormat;
import edu.harvard.hul.ois.ots.schemas.MIX.Compression;
import edu.harvard.hul.ois.ots.schemas.MIX.YCbCrSubSampling;

/** This class handles conversion between FITS metadata and XmlContent
 *  implementations of metadata schemas.
 *  ots-schemas.jar (or the OTS-Schemas project in Eclipse) has to be on the build path
 *  for this to compile. */
public class XmlContentConverter {
	
	Logger logger = Logger.getLogger( this.getClass() );
	
	private final static Namespace ns = Namespace.getNamespace(Fits.XML_NAMESPACE);
    
    /** Converts an image element to a MIX object
     * 
     *  @param  fitsImage   an image element in the FITS schema
     */
    public XmlContent toMix (Element fitsImage, Element fileinfo) {
        MixModel mm = new MixModel ();
        
        for (ImageElement fitsElem : ImageElement.values()) {
        	try {
                String fitsName = fitsElem.getName ();
                Element dataElement = fitsImage.getChild (fitsName,ns);
                if (dataElement == null)
                    continue;
                String dataValue = dataElement.getText().trim();
                if (dataElement != null) {
                    // Parse the numerically various ways, just once
                    Integer intValue = null;
                    try {
                        intValue = Integer.parseInt (dataValue);
                    }
                    catch (NumberFormatException e) {}
                    Double dblValue = null;
                    try {
                        dblValue = Double.parseDouble (dataValue);
                    }
                    catch (NumberFormatException e) {}
                    Rational ratValue = null; 
                    if (intValue != null) {
                        ratValue = new Rational (intValue, 1);
                    }
                    else if (dblValue != null) {
                        ratValue = new Rational ((int) (dblValue * 100 + 0.5), 100);
                    }
                    else if(dataValue.contains("/")) {
                    	try {
	                    	int num = Integer.parseInt(dataValue.substring(0,dataValue.indexOf("/")));
	                    	int den = Integer.parseInt(dataValue.substring(dataValue.indexOf("/")+1));
	                    	ratValue = new Rational(num,den);
                    	}
                    	catch (NumberFormatException e) {}
                    }
                    
                    // This is a very long switch, but I don't think much would be gained
                    // by breaking out each case into a separate processing function.
                    switch (fitsElem) {
                    case byteOrder:
                        mm.bdoi.setByteOrder(dataValue);
                        break;
                    case compressionScheme:
                        Compression cmp = new Compression ();
                        mm.bdoi.setCompression (cmp);
                        cmp.setCompressionScheme (dataValue);
                        break;
                    case imageWidth:
                        mm.bic.setImageWidth(Integer.parseInt (dataValue));
                        break;
                    case imageHeight:
                        mm.bic.setImageHeight(Integer.parseInt (dataValue));
                        break;
                    case colorSpace:
                        mm.phi.setColorSpace (dataValue);
                        break;
                    case referenceBlackWhite:
                        // referenceBlackWhite depends on colorSpace
                        Element cspc = fitsImage.getChild ("colorSpace",ns);
                        String cspcStr = "";
                        if (cspc != null)
                            cspcStr = cspc.getText().trim();
                        mm.populateReferenceBlackWhite (dataValue, cspcStr);
                        break;
                    case iccProfileName:
                        mm.attachIccp ();
                        mm.iccp.setIccProfileName(dataValue);
                        mm.attachIccp ();
                        break;
                    case iccProfileVersion:
                        mm.iccp.setIccProfileVersion(dataValue);
                        mm.attachIccp ();
                        break;
                    case YCbCrSubSampling:
                        YCbCrSubSampling ycbcrss = new YCbCrSubSampling();
                        // Tokenize the value..
                        mm.populateYCbCrSS (ycbcrss, dataValue);
                        break;
                    case YCbCrCoefficients:
                        mm.populateYCbCrCoefficients(dataValue);
                        break;
                    case tileWidth:
                    case tileHeight:
                        mm.populateJPEG2000 ();
                        try {
                            if (intValue != null) {
                                if (fitsElem == ImageElement.tileWidth)
                                    mm.tiles.setTileWidth(intValue);
                                else
                                    mm.tiles.setTileHeight(intValue);
                            }
                        }
                        catch (NumberFormatException e) {}
                        break;
                    case qualityLayers:
                    case resolutionLevels:
                        mm.populateJPEG2000();
                        try {
                            if (intValue != null) {
                                if (fitsElem == ImageElement.qualityLayers)
                                    mm.eo.setQualityLayers(intValue);
                                else 
                                    mm.eo.setResolutionLevels(intValue);
                            }
                        }
                        catch (NumberFormatException e) {}
                        break;
                    case orientation:
                        mm.icm.setOrientation(dataValue);
                        break;
                    case samplingFrequencyUnit:
                        mm.sm.setSamplingFrequencyUnit(dataValue);
                        break;
                    case xSamplingFrequency:
                        if (ratValue != null)
                            mm.sm.setXSamplingFrequency(ratValue);
                        break;
                    case ySamplingFrequency:
                        if (ratValue != null)
                            mm.sm.setYSamplingFrequency(ratValue);
                        break;
                    case bitsPerSample:
                        if (dataValue != null)
                            mm.setBitsPerSample (dataValue);
                        break;
                    case samplesPerPixel:
                        if (intValue != null)
                            mm.ice.setSamplesPerPixel (intValue);
                        break;
                    case extraSamples:
                        //Can there be more than one of these? Assume only one.
                        mm.ice.addExtraSamples (dataValue);
                        break;
                    case colorMap:
                        //Assume we're getting the colormap reference.
                        mm.attachColorMap ();
                        mm.cm.setColormapReference(dataValue);
                        break;
                    case grayResponseCurve:
                        // If FITS gives us anything it will be just one number, not the whole curve. 
                        // We're best off ignoring it rather than putting defective
                        // data into MIX.
                        break;
                    case grayResponseUnit:
                        mm.ice.setGrayResponseUnit(dataValue);
                        break;
                    case whitePointXValue:
                        //Can there be more than one of these?
                        if (ratValue != null) {
                            mm.populateWhitePoint();
                            mm.wp.setWhitePointXValue(ratValue);
                        }
                        break;
                    case whitePointYValue:
                        //Can there be more than one of these?
                        if (ratValue != null) {
                            mm.populateWhitePoint();
                            mm.wp.setWhitePointYValue(ratValue);
                        }
                        break;
                    case primaryChromaticitiesRedX:
                    case primaryChromaticitiesRedY:
                    case primaryChromaticitiesBlueX:
                    case primaryChromaticitiesBlueY:
                    case primaryChromaticitiesGreenX:
                    case primaryChromaticitiesGreenY:
                        mm.populatePrimaryChromaticities();
                        if (ratValue != null) {
                            if (fitsElem == ImageElement.primaryChromaticitiesRedX)
                                mm.pc.setPrimaryChromaticitiesRedX(ratValue);
                            else if (fitsElem == ImageElement.primaryChromaticitiesRedY)
                                mm.pc.setPrimaryChromaticitiesRedY(ratValue);
                            if (fitsElem == ImageElement.primaryChromaticitiesGreenX)
                                mm.pc.setPrimaryChromaticitiesGreenX(ratValue);
                            else if (fitsElem == ImageElement.primaryChromaticitiesGreenY)
                                mm.pc.setPrimaryChromaticitiesGreenY(ratValue);
                            if (fitsElem == ImageElement.primaryChromaticitiesBlueX)
                                mm.pc.setPrimaryChromaticitiesBlueX(ratValue);
                            else if (fitsElem == ImageElement.primaryChromaticitiesBlueY)
                                mm.pc.setPrimaryChromaticitiesBlueY(ratValue);
                        }
                        break;
                    case imageProducer:
                        mm.gci.addImageProducer (dataValue);
                        break;
                    case captureDevice:
                        mm.gci.setCaptureDevice(dataValue);
                        break;
                    case scannerManufacturer:
                        mm.sc.setScannerManufacturer(dataValue);
                        mm.attachScannerCapture();
                        break;
                    case scannerModelName:
                    case scannerModelNumber:
                    case scannerModelSerialNo:
                        if (fitsElem == ImageElement.scannerModelName)
                            mm.scanm.setScannerModelName(dataValue);
                        else if (fitsElem == ImageElement.scannerModelNumber)
                            mm.scanm.setScannerModelNumber(dataValue);
                        else if (fitsElem == ImageElement.scannerModelSerialNo)
                            mm.scanm.setScannerModelSerialNo(dataValue);
                        mm.attachScannerModel ();
                        break;
                    case scanningSoftwareName:
                    case scanningSoftwareVersionNo:
                        if (fitsElem == ImageElement.scanningSoftwareName) 
                            mm.sss.setScanningSoftwareName(dataValue);
                        else
                            mm.sss.setScanningSoftwareVersionNo (dataValue);
                        mm.attachScanningSystemSoftware();
                        break;
                    
                     
                    case digitalCameraManufacturer:
                    	mm.dcc.setDigitalCameraManufacturer(dataValue);
                    	break;
                    case digitalCameraModelName:
                    case digitalCameraModelNumber:
                    case digitalCameraModelSerialNo:
                        if (fitsElem == ImageElement.digitalCameraModelName)
                            mm.dcc.getDigitalCameraModel().setDigitalCameraModelName(dataValue);
                        else if (fitsElem == ImageElement.digitalCameraModelNumber)
                        	mm.dcc.getDigitalCameraModel().setDigitalCameraModelNumber(dataValue);
                        else if (fitsElem == ImageElement.digitalCameraModelSerialNo)
                        	mm.dcc.getDigitalCameraModel().setDigitalCameraModelSerialNo(dataValue);
                        break;

                    case fNumber:
                        if (dblValue != null) {
                            mm.id.setFNumber(dblValue);
                            mm.attachImageData();
                        }
                        break;
                    case exposureTime:
                        if (dblValue != null) {
                            mm.id.setExposureTime (dblValue);
                            mm.attachImageData();
                        }
                        break;
                    case exposureProgram:
                        mm.id.setExposureProgram (dataValue);
                        mm.attachImageData();
                        break;
                    case spectralSensitivity:
                        mm.id.addSpectralSensitivity (dataValue);
                        mm.attachImageData ();
                        break;
                    case isoSpeedRating:
                        if (intValue != null) {
                            mm.id.setIsoSpeedRatings(intValue);
                            mm.attachImageData ();
                        }
                        break;
                    case oECF:
                        if (ratValue != null) {
                            mm.id.setOECF(ratValue);
                            mm.attachImageData ();
                        }
                        break;
                    case exifVersion:
                        mm.id.setExifVersion(dataValue);
                        mm.attachImageData ();
                        break;
                    case shutterSpeedValue:
                        if (ratValue != null) {
                            mm.id.setShutterSpeedValue(ratValue);
                            mm.attachImageData ();
                        }
                        break;
                    case apertureValue:
                        if (ratValue != null) {
                            mm.id.setApertureValue(ratValue);
                            mm.attachImageData ();
                        }
                        break;
                    case brightnessValue:
                        if (ratValue != null) {
                            mm.id.setBrightnessValue(ratValue);
                            mm.attachImageData ();
                        }
                        break;
                    case exposureBiasValue:
                        if (ratValue != null) {
                            mm.id.setExposureBiasValue(ratValue);
                            mm.attachImageData ();
                        }
                        break;
                    case maxApertureValue:
                        if (ratValue != null) {
                            mm.id.setMaxApertureValue(ratValue);
                            mm.attachImageData ();
                        }
                        break;
                    case subjectDistance:
                        // I think we use only the nominal distance, not the min and max
                        if (dblValue != null) {
                            mm.sd.setDistance(dblValue);
                            mm.attachSubjectDistance ();
                        }
                        break;
                    case meteringMode:
                        mm.id.setMeteringMode(dataValue);
                        mm.attachImageData ();
                        break;
                    case lightSource:
                        mm.id.setLightSource(dataValue);
                        mm.attachImageData ();
                        break;
                    case flash:
                        mm.id.setFlash (dataValue);
                        mm.attachImageData ();
                        break;
                    case focalLength:
                        mm.id.setFocalLength (Double.parseDouble (dataValue));
                        mm.attachImageData ();
                        break;
                    case flashEnergy:
                        if (ratValue != null) {
                            mm.id.setFlashEnergy(ratValue);
                            mm.attachImageData ();
                        }
                        break;
                    case exposureIndex:
                        mm.id.setExposureIndex (Double.parseDouble (dataValue));
                        mm.attachImageData ();
                        break;
                    case sensingMethod:
                        mm.id.setSensingMethod (dataValue);
                        mm.attachImageData ();
                        break;
                    case cfaPattern:
                        if (intValue != null) {
                            mm.id.setCfaPattern(intValue);
                            mm.attachImageData ();
                        }
                        break;
                    case cfaPattern2:
                        //This is generated by Exiftool and has no counterpart
                        //in MIX. No one knows what it means. Ignore it.
                        break;
                    case gpsVersionID:
                        mm.gps.setGpsVersionID(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsLatitudeRef:
                        mm.gps.setGpsLatitudeRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsLatitude:
                        mm.populateGPSLatitude (dataValue);
                        break;
                    case gpsLongitudeRef:
                        mm.gps.setGpsLongitudeRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsLongitude:
                        mm.populateGPSLongitude (dataValue);
                        break;
                    case gpsAltitudeRef:
                        mm.gps.setGpsAltitudeRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsAltitude:
                        if (ratValue != null) {
                            mm.gps.setGpsAltitude(ratValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsTimeStamp:
                        mm.gps.setGpsTimeStamp(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsSatellites:
                        mm.gps.setGpsSatellites(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsStatus:
                        mm.gps.setGpsStatus(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsMeasureMode:
                        mm.gps.setGpsMeasureMode(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDOP:
                        if (ratValue != null) {
                            mm.gps.setGpsDOP (ratValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsSpeedRef:
                        mm.gps.setGpsSpeedRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsSpeed:
                        if (ratValue != null) {
                            mm.gps.setGpsSpeed (ratValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsTrackRef:
                        mm.gps.setGpsTrackRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsTrack:
                        if (ratValue != null) {
                            mm.gps.setGpsTrack (ratValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsImgDirectionRef:
                        mm.gps.setGpsImgDirectionRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsImgDirection:
                        if (ratValue != null) {
                            mm.gps.setGpsImgDirection (ratValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsMapDatum:
                        mm.gps.setGpsMapDatum(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDestLatitudeRef:
                        mm.gps.setGpsDestLatitudeRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDestLatitude:
                        mm.populateGPSDestLatitude (dataValue);
                        break;
                    case gpsDestLongitudeRef:
                        mm.gps.setGpsDestLongitudeRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDestLongitude:
                        mm.populateGPSDestLongitude (dataValue);
                        break;
                    case gpsDestBearingRef:
                        mm.gps.setGpsDestBearingRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDestBearing:
                        if (ratValue != null) {
                            mm.gps.setGpsDestBearing (ratValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsDestDistanceRef:
                        mm.gps.setGpsDestDistanceRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDestDistance:
                        if (ratValue != null) {
                            mm.gps.setGpsDestDistance (ratValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsProcessingMethod:
                        mm.gps.setGpsProcessingMethod(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsAreaInformation:
                        mm.gps.setGpsAreaInformation(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDateStamp:
                        mm.gps.setGpsDateStamp(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDifferential:
                        mm.gps.setGpsDifferential(dataValue);
                        mm.attachGPSData();
                        break;

                    }
                }
            }
            catch (XmlContentException e) {
            	logger.error("Invalid MIX content: " + e.getMessage ());
            }
        }//end of for loop
            
            
       try {
            if(fileinfo != null) {
            	Element created = fileinfo.getChild (ImageElement.created.toString(),ns);
            	if(created != null) {
            		String date = null;
            		try {
            			date = XmlDateFormat.exifDateTimeToXml(created.getText().trim());
            		}
            		catch(ParseException e) {
            			System.out.println ("Warning: " + e.getMessage ());
            			//e.printStackTrace();
            		}
            		if(date != null) {
            			mm.icm.getGeneralCaptureInformation().setDateTimeCreated(date);
            		}
            	}
            }
       }
       catch (XmlContentException e) {
    	   logger.error("Invalid MIX content: " + e.getMessage ());
       }
            
        return mm.mix;
    }

    /** Converts a document element to a DocumentMD object 
     *  @param  fitsDoc   a document element in the FITS schema
     */
    public XmlContent toDocumentMD (Element fitsDoc) {
        DocumentMDModel dm = new DocumentMDModel ();
            for (DocumentMDElement fitsElem : DocumentMDElement.values()) {
                String fitsName = fitsElem.getName ();
                Element dataElement = fitsDoc.getChild (fitsName,ns);
                if (dataElement == null)
                    continue;
                String dataValue = dataElement.getText().trim();
                Integer intValue = null;
                try {
                    intValue = Integer.parseInt (dataValue);
                }
                catch (NumberFormatException e) {}
            
                switch (fitsElem) {
                case pageCount:

                 if(intValue != null)
                 dm.docMD.setPageCount (intValue);
                    break;
                case wordCount:
                 if(intValue != null)
                 dm.docMD.setWordCount(intValue);
                    break;
                case characterCount:
                 if(intValue != null)
                 dm.docMD.setCharacterCount(intValue);
                    break;
                case lineCount:
                 if(intValue != null)
                 dm.docMD.setLineCount(intValue);
                    break;
                case graphicsCount:
                 if(intValue != null)
                 dm.docMD.setGraphicsCount(intValue);
                    break;
                case tableCount:
                 if(intValue != null)
                 dm.docMD.setTableCount(intValue);
                    break;
                case language:
                 if(dataValue != null)
                 dm.docMD.addLanguage(dataValue);
                    break;
                case font:
                    // Currently not provided by FITS
                    break;
                case isTagged:
                case hasLayers:
                case hasTransparency:
                case hasOutline:
                case hasThumbnails:
                case hasAttachments:
                case isRightsManaged:
                case hasForms:
                case isProtected:
                case hasAnnotations:
                case hasDigitalSignature:
                 if(dataElement != null) {
                	 dm.addFeature(dataElement);
                 }
                  break;
                }
            }
        return dm.docMD;
    }
    
    /** Converts a text element to a TextMD object 
     *  @param  fitsText   a text element in the FITS schema
     */
    public XmlContent toTextMD (Element fitsText) {
        TextMDModel tm = new TextMDModel ();
        for (TextMDElement fitsElem : TextMDElement.values()) {
            try {
                String fitsName = fitsElem.getName ();
                Element dataElement = fitsText.getChild (fitsName,ns);
                if (dataElement == null)
                    continue;
                String dataValue = dataElement.getText().trim();
                switch (fitsElem) {
                case linebreak:
                    tm.attachCharacterInfo();
                    tm.ci.setLinebreak(dataValue);
                    break;
                case charset:
                    tm.attachCharacterInfo();
                    tm.ci.setCharset(dataValue.toUpperCase());
                    break;
                case markupBasis:
                    tm.attachMarkupBasis (); 
                    tm.mb.setValue(dataValue);
                    break;
                case markupBasisVersion:
                    tm.attachMarkupBasis (); 
                    tm.mb.setVersion(dataValue);
                    break;
                case markupLanguage:
                    tm.attachMarkupLanguage (); 
                    tm.ml.setValue(dataValue);
                    break;
                case markupLanguageVersion:
                    tm.attachMarkupLanguage (); 
                    tm.ml.setVersion(dataValue);
                    break;
                }
            }
	        catch (XmlContentException e) {
	        	logger.error("Invalid content: " + e.getMessage ());
	        }
        }//end for
            
        return tm.textMD;
    }
    
    /**
     * Converts a audio element into a AudioObject AES object
     * @param fitsAudio		an audio element in the FITS schema
     */
    public XmlContent toAES (FitsOutput fitsOutput,Element fitsAudio) {
        AESModel aesModel = null;
        
    	try {
			aesModel = new AESModel ();
		} catch (XmlContentException e2) {
			logger.error("Invalid content: " + e2.getMessage ());
		}
    	
    	String filename = fitsOutput.getMetadataElement("filename").getValue();
    	
    	
    	FitsIdentity fitsIdent = fitsOutput.getIdentities().get(0);
    	String version = null;
    	if(fitsIdent.getFormatVersions().size() > 0) {
    		version = fitsIdent.getFormatVersions().get(0).getValue();
    	}
    	
    	try {
			aesModel.setFormat(fitsIdent.getFormat(),version);
		} catch (XmlContentException e1) {
			logger.error("Invalid content: " + e1.getMessage ());
		}
    	
    	aesModel.aes.getPrimaryIdentifier().setText(new File(filename).getName());
    	
    	int sampleRate = 0;
    	int channelCnt = 0;
    	long numSamples = 0;
    	String duration = "0";
    	String timeStampStart = "0";
    	
        for (AudioElement fitsElem : AudioElement.values()) {
            try {
                String fitsName = fitsElem.getName ();
                Element dataElement = fitsAudio.getChild (fitsName,ns);
                if (dataElement == null)
                    continue;
                String dataValue = dataElement.getText().trim();                
                switch (fitsElem) {
                case duration:
                	duration = dataValue;
                    break;
                case bitDepth:
                	aesModel.setBitDepth(Integer.parseInt(dataValue));
                    break;
                case sampleRate:
            		if(dataValue.contains(".")) {
            			String[] sampleParts = dataValue.split("\\.");
            			if(sampleParts[sampleParts.length-1].equals("0")) {
            				sampleRate = Integer.parseInt(sampleParts[0]);
            			}
            		}
            		else {
            			sampleRate = Integer.parseInt(dataValue);
            		}
                    aesModel.genericFormatRegion.setSampleRate(Double.parseDouble(dataValue));
                    break;
                case channels:
                	channelCnt = Integer.parseInt(dataValue);
                	//add streams and channel cnt
                	aesModel.setNumChannels(channelCnt);
                	for(int i=0;i<channelCnt;i++) {
                		aesModel.addStream(i,0.0,0.0);
                	}
                    break;
                case offset:
                    aesModel.aes.setFirstSampleOffset(Integer.parseInt(dataValue));
                    break;
                case timeStampStart:
                    timeStampStart = dataValue;
                    break;
                case byteOrder:
                    aesModel.aes.setByteOrder(dataValue);
                    break;
                case bitRate:
                    aesModel.setBitRate(dataValue);
                    break;
                case numSamples:
                    numSamples = Long.valueOf(dataValue);
                    break;
                case wordSize:
                	aesModel.setWordSize(Integer.parseInt(dataValue));
                	break;
                case audioDataEncoding:
                	aesModel.setAudioDataEncoding(dataValue);
                	break;
                case blockAlign:
                	aesModel.setAudioDataBlockSize(Integer.parseInt(dataValue));
                	break;
                case codecName:
                	aesModel.setCodec(dataValue);
                	break;
                case codecNameVersion:
                	aesModel.setCodecVersion(dataValue);
                	break;
                case codecCreatorApplication:
                	aesModel.setCodecCreatorApplication(dataValue);
                	break;
                case codecCreatorApplicationVersion: 
                	aesModel.setCodecCreatorApplicationVersion(dataValue);
                	break;
                }
                
            }
            catch (XmlContentException e) {
                logger.error("Invalid content: " + e.getMessage ());
            }
        }//end for

    	//set other requires values
    	aesModel.aes.setAnalogDigitalFlag("FILE_DIGITAL");
    	try {
			aesModel.setDummyUseType();
        	aesModel.setDuration(duration,sampleRate,numSamples);
        	aesModel.setStartTime(timeStampStart,sampleRate);
		} catch (XmlContentException e) {
			logger.error("Invalid content: " + e.getMessage ());
		}
    	
        return aesModel.aes;
    }
    
    /* an enumeration for mapping symbols to FITS audio element names */
    public enum AudioElement {
    	bitsPerSample ("bitsPerSample"),
    	duration ("duration"),
    	bitDepth ("bitDepth"),
    	sampleRate ("sampleRate"),
    	channels ("channels"),
    	dataFormatType ("dataFormatType"),
    	offset ("offset"),
    	timeStampStart ("timeStampStart"),
    	byteOrder ("byteOrder"),
    	bitRate ("bitRate"),
    	numSamples ("numSamples"),
    	wordSize ("wordSize"),
    	audioDataEncoding ("audioDataEncoding"),
    	blockAlign ("blockAlign"),
    	codecName ("codecName"),
        codecNameVersion ("codecNameVersion"),
        codecCreatorApplication ("codecCreatorApplication"),
        codecCreatorApplicationVersion ("codecCreatorApplicationVersion");
    	
    	private String name;
        
    	AudioElement(String name) {
            this.name = name;
        }
        
        public String getName () {
            return name;
        }
    }
    
    /* An enumeration for mapping symbols to FITS image element names. */
    public enum ImageElement {
        byteOrder ("byteOrder"),
        compressionScheme ("compressionScheme"),
        imageWidth ("imageWidth"),
        imageHeight ("imageHeight"),
        colorSpace ("colorSpace"),
        referenceBlackWhite ("referenceBlackWhite"),
        iccProfileName ("iccProfileName"),
        iccProfileVersion ("iccProfileVersion"),
        YCbCrSubSampling ("YCbCrSubSampling"),
        YCbCrCoefficients ("YCbCrCoefficients"),
        tileWidth ("tileWidth"),
        tileHeight ("tileHeight"),
        qualityLayers ("qualityLayers"),
        resolutionLevels ("resolutionLevels"),
        orientation ("orientation"),
        samplingFrequencyUnit ("samplingFrequencyUnit"),
        xSamplingFrequency ("xSamplingFrequency"),
        ySamplingFrequency ("ySamplingFrequency"),
        bitsPerSample ("bitsPerSample"),
        samplesPerPixel ("samplesPerPixel"),
        extraSamples ("extraSamples"),
        colorMap ("colorMap"),
        grayResponseCurve ("grayResponseCurve"),
        grayResponseUnit ("grayResponseUnit"),
        whitePointXValue ("whitePointXValue"),
        whitePointYValue ("whitePointYValue"),
        primaryChromaticitiesRedX ("primaryChromaticitiesRedX"),
        primaryChromaticitiesRedY ("primaryChromaticitiesRedY"),
        primaryChromaticitiesGreenX ("primaryChromaticitiesGreenX"),
        primaryChromaticitiesGreenY ("primaryChromaticitiesGreenY"),
        primaryChromaticitiesBlueX ("primaryChromaticitiesBlueX"),
        primaryChromaticitiesBlueY ("primaryChromaticitiesBlueY"),
        imageProducer ("imageProducer"),
        captureDevice ("captureDevice"),
        scannerManufacturer("scannerManufacturer"),
        scannerModelName ("scannerModelName"),
        scannerModelNumber ("scannerModelNumber"),
        scannerModelSerialNo ("scannerModelSerialNo"),
        scanningSoftwareName ("scanningSoftwareName"),
        scanningSoftwareVersionNo ("scanningSoftwareVersionNo"),
        fNumber ("fNumber"),
        exposureTime ("exposureTime"),
        exposureProgram ("exposureProgram"),
        spectralSensitivity ("spectralSensitivity"),
        isoSpeedRating ("isoSpeedRating"),
        oECF ("oECF"),
        exifVersion ("exifVersion"),
        shutterSpeedValue ("shutterSpeedValue"),
        apertureValue ("apertureValue"),
        brightnessValue ("brightnessValue"),
        exposureBiasValue ("exposureBiasValue"),
        maxApertureValue ("maxApertureValue"),
        subjectDistance ("subjectDistance"),
        meteringMode ("meteringMode"),
        lightSource ("lightSource"),
        flash ("flash"),
        focalLength ("focalLength"),
        flashEnergy ("flashEnergy"),
        exposureIndex ("exposureIndex"),
        sensingMethod ("sensingMethod"),
        cfaPattern ("cfaPattern"),
        cfaPattern2 ("cfaPattern2"),
        gpsVersionID ("gpsVersionID"),
        gpsLatitudeRef ("gpsLatitudeRef"),
        gpsLatitude ("gpsLatitude"),
        gpsLongitudeRef ("gpsLongitudeRef"),
        gpsLongitude ("gpsLongitude"),
        gpsAltitudeRef ("gpsAltitudeRef"),
        gpsAltitude ("gpsAltitude"),
        gpsTimeStamp ("gpsTimeStamp"),
        gpsSatellites ("gpsSatellites"),
        gpsStatus ("gpsStatus"),
        gpsMeasureMode ("gpsMeasureMode"),
        gpsDOP ("gpsDOP"),
        gpsSpeedRef ("gpsSpeedRef"),
        gpsSpeed ("gpsSpeed"),
        gpsTrackRef ("gpsTrackRef"),
        gpsTrack ("gpsTrack"),
        gpsImgDirectionRef ("gpsImgDirectionRef"),
        gpsImgDirection ("gpsImgDirection"),
        gpsMapDatum ("gpsMapDatum"),
        gpsDestLatitudeRef ("gpsDestLatitudeRef"),
        gpsDestLatitude ("gpsDestLatitude"),
        gpsDestLongitudeRef ("gpsDestLongitudeRef"),
        gpsDestLongitude ("gpsDestLongitude"),
        gpsDestBearingRef ("gpsDestBearingRef"),
        gpsDestBearing ("gpsDestBearing"),
        gpsDestDistanceRef ("gpsDestDistanceRef"),
        gpsDestDistance ("gpsDestDistance"),
        gpsProcessingMethod ("gpsProcessingMethod"),
        gpsAreaInformation ("gpsAreaInformation"),
        gpsDateStamp ("gpsDateStamp"),
        gpsDifferential ("gpsDifferential"),
        digitalCameraModelName("digitalCameraModelName"),
        digitalCameraModelNumber("digitalCameraModelNumber"),
        digitalCameraModelSerialNo("digitalCameraModelSerialNo"),
        digitalCameraManufacturer("digitalCameraManufacturer"),
        created("created");
        
        private String name;
        
        ImageElement(String name) {
            this.name = name;
        }
        
        public String getName () {
            return name;
        }
    }
    
    
    /* An enumeration for mapping symbols to FITS text metadata element names. */
    public enum TextMDElement {
        linebreak ("linebreak"),
        charset ("charset"),
        markupBasis ("markupBasis"),
        markupBasisVersion ("markupBasisVersion"),
        markupLanguage ("markupLanguage"),
        markupLanguageVersion ("markupLanguageVersion");

        private String name;

        TextMDElement (String name) {
            this.name = name;
        }

        public String getName () {
            return name;
        }
    }


    /* An enumeration for mapping symbols to FITS Document metadata element names. */
    public enum DocumentMDElement {
        pageCount ("pageCount"),
        wordCount ("wordCount"),
        characterCount ("characterCount"),
        paragraphCount ("paragraphCount"),
        lineCount ("lineCount"),
        graphicsCount ("graphicsCount"),
        tableCount ("tableCount"),
        language("language"),
        font("font"),
        isTagged("isTagged"),
        hasLayers ("hasLayers"),
        hasTransparency("hasTransparency"),
        hasOutline("hasOutline"),
        hasThumbnails("hasThumbnails"),
        hasAttachments("hasAttachments"),
        isRightsManaged("isRightsManaged"),
        hasForms("hasForms"),
        isProtected("isProtected"),
        hasAnnotations("hasAnnotations"),
        hasDigitalSignature("hasDigitalSignature");
        

        private String name;

        DocumentMDElement (String name) {
            this.name = name;
        }

        public String getName () {
            return name;
        }
    }
}

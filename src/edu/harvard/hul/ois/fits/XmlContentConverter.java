//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits;


import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;

import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.ots.schemas.DocumentMD.Font;
import edu.harvard.hul.ois.ots.schemas.MIX.Compression;
import edu.harvard.hul.ois.ots.schemas.MIX.YCbCrSubSampling;
import edu.harvard.hul.ois.ots.schemas.XmlContent.Rational;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlDateFormat;

/** This class handles conversion between FITS metadata and XmlContent
 *  implementations of metadata schemas.
 *  ots-schemas.jar (or the OTS-Schemas project in Eclipse) has to be on the build path
 *  for this to compile. */
public class XmlContentConverter {

	private static List<String> docMdNames;

	private static final Logger logger = Logger.getLogger(XmlContentConverter.class);

	private static final Namespace ns = Namespace.getNamespace(Fits.XML_NAMESPACE);

	static {
    	// collect names of DocumentMD enums
		docMdNames = new ArrayList<String>(DocumentMDElement.values().length);
		for (DocumentMDElement elem : DocumentMDElement.values()) {
			docMdNames.add(elem.getName());
		}
	}

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

                    Integer intValue = null;
                    Double dblValue = null;
                    Rational rationalValue = null;

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
                    	intValue = parseInt(dataValue, fitsElem.toString());
                    	if (intValue != null) {
                    		mm.bic.setImageWidth(intValue);
                    	}
                        break;
                    case imageHeight:
                    	intValue = parseInt(dataValue, fitsElem.toString());
                    	if (intValue != null) {
                    		mm.bic.setImageHeight(intValue);
                    	}
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
                    	intValue = parseInt(dataValue, fitsElem.toString());
                        if (intValue != null) {
                            if (fitsElem == ImageElement.tileWidth)
                                mm.tiles.setTileWidth(intValue);
                            else
                                mm.tiles.setTileHeight(intValue);
                        }
                        break;
                    case qualityLayers:
                    case resolutionLevels:
                        mm.populateJPEG2000();
                    	intValue = parseInt(dataValue, fitsElem.toString());
                        if (intValue != null) {
                            if (fitsElem == ImageElement.qualityLayers)
                                mm.eo.setQualityLayers(intValue);
                            else
                                mm.eo.setResolutionLevels(intValue);
                        }
                        break;
                    case orientation:
                        mm.icm.setOrientation(dataValue);
                        break;
                    case samplingFrequencyUnit:
                        mm.sm.setSamplingFrequencyUnit(dataValue);
                        break;
                    case xSamplingFrequency:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null)
                            mm.sm.setXSamplingFrequency(rationalValue);
                        break;
                    case ySamplingFrequency:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null)
                            mm.sm.setYSamplingFrequency(rationalValue);
                        break;
                    case bitsPerSample:
                        if (dataValue != null)
                            mm.setBitsPerSample (dataValue);
                        break;
                    case samplesPerPixel:
                    	intValue = parseInt(dataValue, fitsElem.toString());
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
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.populateWhitePoint();
                            mm.wp.setWhitePointXValue(rationalValue);
                        }
                        break;
                    case whitePointYValue:
                        //Can there be more than one of these?
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.populateWhitePoint();
                            mm.wp.setWhitePointYValue(rationalValue);
                        }
                        break;
                    case primaryChromaticitiesRedX:
                    case primaryChromaticitiesRedY:
                    case primaryChromaticitiesBlueX:
                    case primaryChromaticitiesBlueY:
                    case primaryChromaticitiesGreenX:
                    case primaryChromaticitiesGreenY:
                        mm.populatePrimaryChromaticities();
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            if (fitsElem == ImageElement.primaryChromaticitiesRedX)
                                mm.pc.setPrimaryChromaticitiesRedX(rationalValue);
                            else if (fitsElem == ImageElement.primaryChromaticitiesRedY)
                                mm.pc.setPrimaryChromaticitiesRedY(rationalValue);
                            if (fitsElem == ImageElement.primaryChromaticitiesGreenX)
                                mm.pc.setPrimaryChromaticitiesGreenX(rationalValue);
                            else if (fitsElem == ImageElement.primaryChromaticitiesGreenY)
                                mm.pc.setPrimaryChromaticitiesGreenY(rationalValue);
                            if (fitsElem == ImageElement.primaryChromaticitiesBlueX)
                                mm.pc.setPrimaryChromaticitiesBlueX(rationalValue);
                            else if (fitsElem == ImageElement.primaryChromaticitiesBlueY)
                                mm.pc.setPrimaryChromaticitiesBlueY(rationalValue);
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
                    	dblValue = parseDouble(dataValue, fitsElem.toString());
                        if (dblValue != null) {
                            mm.id.setFNumber(dblValue);
                            mm.attachImageData();
                        }
                        break;
                    case exposureTime:
                    	dblValue = parseDouble(dataValue, fitsElem.toString());
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
                    	intValue = parseInt(dataValue, fitsElem.toString());
                        if (intValue != null) {
                            mm.id.setIsoSpeedRatings(intValue);
                            mm.attachImageData ();
                        }
                        break;
                    case oECF:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.id.setOECF(rationalValue);
                            mm.attachImageData ();
                        }
                        break;
                    case exifVersion:
                        mm.id.setExifVersion(dataValue);
                        mm.attachImageData ();
                        break;
                    case shutterSpeedValue:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.id.setShutterSpeedValue(rationalValue);
                            mm.attachImageData ();
                        }
                        break;
                    case apertureValue:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.id.setApertureValue(rationalValue);
                            mm.attachImageData ();
                        }
                        break;
                    case brightnessValue:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.id.setBrightnessValue(rationalValue);
                            mm.attachImageData ();
                        }
                        break;
                    case exposureBiasValue:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.id.setExposureBiasValue(rationalValue);
                            mm.attachImageData ();
                        }
                        break;
                    case maxApertureValue:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.id.setMaxApertureValue(rationalValue);
                            mm.attachImageData ();
                        }
                        break;
                    case subjectDistance:
                        // I think we use only the nominal distance, not the min and max
                    	dblValue = parseDouble(dataValue, fitsElem.toString());
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
                    	dblValue = parseDouble(dataValue, fitsElem.toString());
                    	if (dblValue != null) {
                    		mm.id.setFocalLength (dblValue);
                    		mm.attachImageData ();
                    	}
                        break;
                    case flashEnergy:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.id.setFlashEnergy(rationalValue);
                            mm.attachImageData ();
                        }
                        break;
                    case exposureIndex:
                    	// only a positive non-zero value will validate against MIX schema
                    	dblValue = parseDouble(dataValue, fitsElem.toString());
                    	if (dblValue != null && dblValue > 0.0) {
                    		mm.id.setExposureIndex (dblValue);
                    		mm.attachImageData ();
                    	}
                        break;
                    case sensingMethod:
                        mm.id.setSensingMethod (dataValue);
                        mm.attachImageData ();
                        break;
                    case cfaPattern:
                    	intValue = parseInt(dataValue, fitsElem.toString());
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
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.gps.setGpsAltitude(rationalValue);
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
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.gps.setGpsDOP (rationalValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsSpeedRef:
                        mm.gps.setGpsSpeedRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsSpeed:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.gps.setGpsSpeed (rationalValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsTrackRef:
                        mm.gps.setGpsTrackRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsTrack:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.gps.setGpsTrack (rationalValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsImgDirectionRef:
                        mm.gps.setGpsImgDirectionRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsImgDirection:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.gps.setGpsImgDirection (rationalValue);
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
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.gps.setGpsDestBearing (rationalValue);
                            mm.attachGPSData();
                        }
                        break;
                    case gpsDestDistanceRef:
                        mm.gps.setGpsDestDistanceRef(dataValue);
                        mm.attachGPSData();
                        break;
                    case gpsDestDistance:
                    	rationalValue = parseRational(dataValue, fitsElem.toString());
                        if (rationalValue != null) {
                            mm.gps.setGpsDestDistance (rationalValue);
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
            	logger.warn("Invalid MIX content for element [" + fitsElem + "]: " + e.getMessage ());
            }
        }//end of for loop

        if(fileinfo != null) {
            Element created = fileinfo.getChild (ImageElement.created.toString(),ns);
            if(created != null) {
                String date = null;
                try {
                    date = XmlDateFormat.exifDateTimeToXml(created.getText().trim());
                    if(date != null) {
                        mm.icm.getGeneralCaptureInformation().setDateTimeCreated(date);
                    }
                }
                catch (ParseException e) {
                    logger.warn("Warning - unable to parse date: " + e.getMessage ());
                }
                catch (XmlContentException e) {
                    logger.warn("Invalid MIX content for data element [" + date + "]: " + e.getMessage ());
                }
            }
        }

        return mm.mix;
    }

    /**
     * Converts a document element to a DocumentMD object
     * @param  fitsDoc   a document element in the FITS schema
     */
    public XmlContent toDocumentMD(Element fitsDoc) {
        DocumentMDModel dm = new DocumentMDModel();
        @SuppressWarnings("unchecked")
		List<Element> dataElements = fitsDoc.getChildren();
        for (Element dataElement : dataElements) {
        	// If element name is contained in enum then we're interested in it.
        	DocumentMDElement fitsElem = null;
        	if (docMdNames.contains(dataElement.getName())) {
        		// If name of element is contained in list of enum names then we can safely use valueOf()
        		// rather than having to trap potential exception whenever the dataElement name does not convert to an enum.
        		fitsElem = DocumentMDElement.valueOf(dataElement.getName());
        	} else {
        		continue;
        	}
            String dataValue = dataElement.getText().trim();
            Integer intValue = null;  // it's sometimes necessary to convert to Integer
            switch (fitsElem) {
                case pageCount:
                    intValue = parseInt(dataValue, fitsElem.toString());
                    if(intValue != null) {
                        dm.docMD.setPageCount(intValue);
                    }
                    break;
                case wordCount:
                    intValue = parseInt(dataValue, fitsElem.toString());
                    if(intValue != null) {
                    	dm.docMD.setWordCount(intValue);
                    }
                    break;
                case characterCount:
                    intValue = parseInt(dataValue, fitsElem.toString());
                    if(intValue != null) {
                        dm.docMD.setCharacterCount(intValue);
                    }
                    break;
                case paragraphCount:
                    intValue = parseInt(dataValue, fitsElem.toString());
                    if(intValue != null) {
                        dm.docMD.setParagraphCount(intValue);
                    }
                    break;
                case lineCount:
                    intValue = parseInt(dataValue, fitsElem.toString());
                    if(intValue != null) {
                        dm.docMD.setLineCount(intValue);
                    }
                    break;
                case graphicsCount:
                    intValue = parseInt(dataValue, fitsElem.toString());
                    if(intValue != null) {
                        dm.docMD.setGraphicsCount(intValue);
                    }
                    break;
                case tableCount:
                    intValue = parseInt(dataValue, fitsElem.toString());
                    if(intValue != null) {
                        dm.docMD.setTableCount(intValue);
                    }
                    break;
                case language:
                    if(dataValue != null) {
                        dm.docMD.addLanguage(dataValue);
                    }
                    break;
                case font:
                    // Need to look for sub-elements
                	Element fontName = dataElement.getChild(DocumentMDElement.fontName.getName(), ns);
                	Element fontIsEmbedded = dataElement.getChild(DocumentMDElement.fontIsEmbedded.getName(), ns);
                	if (fontName != null && !fontName.getText().isEmpty()) {
                		Font font = new Font();
                		font.setName(fontName.getText());
                		if (fontIsEmbedded != null) {
                			boolean isEmbedded = !fontIsEmbedded.getText().isEmpty() && "true".equals(fontIsEmbedded.getText());
                			font.setEmbedded(isEmbedded);
                		}
                		dm.docMD.addFont(font);
                	}
                    break;
                case isTagged:
                case hasOutline:
                case hasThumbnails:
                case hasLayers:
                case hasForms:
                case hasAnnotations:
                case hasAttachments:
                case useTransparency:
                case hasHyperlinks:
                case hasEmbeddedResources:
                    if(dataElement != null) {
                        dm.addFeature(dataElement);
                    }
                    break;
                default:
                	logger.warn("No case entry for : " + fitsElem.getName());
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
            	logger.warn("Invalid TextMD content for element [" + fitsElem + "]: " + e.getMessage ());
	        }
        }//end for

        return tm.textMD;
    }

    /**
     * Converts a audio element into a AudioObject AES object
     * @param fitsAudio	an audio element in the FITS schema
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
                Integer intValue = null;
                switch (fitsElem) {
                case duration:
                	duration = dataValue;
                    break;
                case bitDepth:
                	intValue = parseInt(dataValue, fitsElem.toString());
                	if (intValue != null) {
                		aesModel.setBitDepth(intValue);
                	}
                    break;
                case sampleRate:
            		if(dataValue.contains(".")) {
            			String[] sampleParts = dataValue.split("\\.");
            			if(sampleParts[sampleParts.length-1].equals("0")) {
            				sampleRate = Integer.parseInt(sampleParts[0]);
            			}
            		}
            		else {
            			intValue = parseInt(dataValue, fitsElem.toString());
                    	if (intValue != null) {
                    		sampleRate = intValue;
                    	}
            		}
            		Double doubleValue = parseDouble(dataValue, fitsElem.toString());
            		if (doubleValue != null) {
            			aesModel.genericFormatRegion.setSampleRate(doubleValue);
            		}
                    break;
                case channels:
                	intValue = parseInt(dataValue, fitsElem.toString());
                	if (intValue != null) {
                		channelCnt = intValue;
                		//add streams and channel cnt
                		aesModel.setNumChannels(channelCnt);
                		for(int i=0;i<channelCnt;i++) {
                			aesModel.addStream(i,0.0,0.0);
                		}
                	}
                    break;
                case offset:
                	intValue = parseInt(dataValue, fitsElem.toString());
                	if (intValue != null) {
                		aesModel.aes.setFirstSampleOffset(intValue);
                	}
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
                	Long longValue = parseLong(dataValue, fitsElem.toString());
                	if (longValue != null) {
                		numSamples = longValue;
                	}
                    break;
                case wordSize:
                	intValue = parseInt(dataValue, fitsElem.toString());
                	if (intValue != null) {
                		aesModel.setWordSize(intValue);
                	}
                	break;
                case audioDataEncoding:
                	aesModel.setAudioDataEncoding(dataValue);
                	break;
                case blockAlign:
                	intValue = parseInt(dataValue, fitsElem.toString());
                	if (intValue != null) {
                		aesModel.setAudioDataBlockSize(intValue);
                	}
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
            	logger.warn("Invalid AES content for element [" + fitsElem + "]: " + e.getMessage ());
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

    /**
     * Converts a video element into a VideoObject ??? object
     * @param fitsVideo		a video element in the FITS schema
     */
    public XmlContent toEbuCoreVideo (FitsOutput fitsOutput,Element fitsVideo) {
    	
    	//
    	// NOTE:
    	// The FitsOutput INPUT can be one of 3 types:
    	// 1) "FITS"
    	// 2) "Combined" (both FITS and Standard)
    	// 3) "Standard" (Ebucore)
    	// ... and either contain spaces in the XML, or not
    	//
    	// The XmlContent object returned output will be as follows for each
    	// input type:
    	// 1) FITS or Combined = Standard Ebucore
    	// 2) Standard (Ebucore) = null XmlContent Object, as the fitsOutput
    	// does not contain elements or data which can be parsed into Ebucore.
    	// In other words, there is no data present which can be used to 
    	// transform the data to Ebucore because it is ALREADY Ebucore.
    	//
    	// TODO: - Maybe return an Ebucore-based FitsOutput based upon the
    	// Ebucore data contained in the Standard Element for FitsOutput input
    	// objects of type "Standard".

    	EbuCoreModel ebucoreModel = null;
    	String fitsName = null;

    	String framerate = "NOT_SET";
    	String timecode = "NOT_SET";

    	try {
    		ebucoreModel = new EbuCoreModel();
    		List<Object> videoElemList = fitsVideo.getContent();    		

    		String mimeType = null;

    		// Walk through all of the elements and process them
    		// skipping any Text Objects
    		for(Object obj : videoElemList) {
    			try {
	    			// Skip the text object.
	    			// This is most likely due to spaces preceding the real element
	    			if(obj instanceof Text) {
	    				continue;
	    			}
	    			
	    			Element elem = (Element)obj;
	    			fitsName = elem.getName ();
	    			
	    			// If we have a standard element, then we are done.
	    			// We only wish to process the basic FITS output
	    			if(fitsName.equals ("standard")) {
	    				break;
	    			}
	    			
	    	    	// Ebucore can only be generated from MediaInfo output
	    	    	if(!isMediaInfoTool(fitsName, elem))
	    	    		return null;
	
	    	    	// Set mime type - MXF codec generation needs it
	    	    	if(fitsName.equals("mimeType")) {
	    	    		mimeType = elem.getValue();
	    	    	}
	
	    			// Process the tracks
	    			if (fitsName.equals("track")) {
	    				Attribute typeAttr = elem.getAttribute("type");
	    				if(typeAttr != null) {
	    					String type = typeAttr.getValue();
	
	    					if(type.toLowerCase().equals("video")) {
	
	    						// Set the framerate
	    				   		Element dataElement = elem.getChild ("frameRate",ns);
	    			    		if (dataElement != null) {
	    			    			String dataValue = dataElement.getText().trim();
	    		   					if (!StringUtils.isEmpty(dataValue)) {
	            			    		framerate = dataValue;
	    		   					}
	
	    			    		}
	
	    						ebucoreModel.createVideoFormatElement(elem, ns, mimeType);
	
	    					} // video format
	
	    					// Audio Format
	    					else if (type.toLowerCase().equals("audio")) {
	    						ebucoreModel.createAudioFormatElement(elem, ns);
	    					}  // audio format
	    				}
	    			}  // track
	    			else {
	
	    				// Set the timecode
	    				if(fitsName.equals("timecodeStart")) {
	    					String dataValue = elem.getText().trim();
	    					if (!StringUtils.isEmpty(dataValue)) {
	    						timecode = dataValue;
	    					}
	    				}
	
	    				// Process Elements directly off the root of the Format Element
	    				ebucoreModel.createFormatElement(fitsName, elem);
	    			}

    			} catch (XmlContentException e) {
    				logger.warn("Invalid EbuCore content for element [" + fitsName + "]: " + e.getMessage ());
    			}
    		}  // for(Element elem : trackList)

			// Process Elements directly off the root of the Format Element
			ebucoreModel.
			createStart(timecode, framerate);

    	} catch (XmlContentException e) {
    		// If we get here then construction of EbuCoreModel failed so
    		// return null here to avoid NPE when dereferencing ebucoreMain below.
    		logger.error("Could not create EbuCoreModel: " + e.getMessage(), e);
    		return null;
    	}

    	return ebucoreModel.ebucoreMain;
    } // toEbuCoreVideo

    boolean isMediaInfoTool(String fitsName, Element elem) {

    	// Ebucore can only be generated from MediaInfo output
		Attribute toolNameAttr = elem.getAttribute("toolname");
    	String toolName = toolNameAttr.getValue();
    	if(toolName == null)  // just in case
    		return false;
    	if(!toolName.equalsIgnoreCase("mediainfo"))
    		return false;

    	return true;
    }

    /* an enumeration for mapping symbols to FITS audio element names */
    public enum AudioElement {
       	//samplingRate ("samplingRate"),
       	//sampleSize ("sampleSize"),
    	//bitRate ("bitRate"),
    	//bitRateMode ("bitRateMode"),
    	//channels ("channels");

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

    	private AudioElement(String name) {
            this.name = name;
        }

        public String getName() {
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

        private ImageElement(String name) {
            this.name = name;
        }

        public String getName() {
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

        private TextMDElement(String name) {
            this.name = name;
        }

        public String getName() {
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
        fontName("fontName"), // should only be sub-element of 'font'
        fontIsEmbedded("fontIsEmbedded"), // should only be sub-element of 'font'
        isTagged("isTagged"),
        hasLayers ("hasLayers"),
        useTransparency("useTransparency"),
        hasOutline("hasOutline"),
        hasThumbnails("hasThumbnails"),
        hasAttachments("hasAttachments"),
        hasForms("hasForms"),
        hasAnnotations("hasAnnotations"),
        hasHyperlinks("hasHyperlinks"),
        hasEmbeddedResources("hasEmbeddedResources");

        private String name;

        private DocumentMDElement(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /* an enumeration for mapping symbols to FITS audio element names */
    public enum AudioFormatElement {
       	samplingRate ("samplingRate"),
       	sampleSize ("sampleSize"),
    	bitRate ("bitRate"),
    	bitRateMode ("bitRateMode"),
    	channels ("channels");

    	private String name;

    	private AudioFormatElement(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /*
     * Parse a String value to Integer
     */
    private Integer parseInt(String valueToParse, String fitsElem) {
    	Integer intValue = null;
    	try {
    		intValue = Integer.parseInt(valueToParse);
    	}
    	catch (NumberFormatException | NullPointerException e) {
        	logger.info("Could not parse element [" + fitsElem + "] to Integer: " + valueToParse + " -- ignoring value. Exception message: " + e.getMessage());
    	}
    	return intValue;
    }

    /*
     * Parse a String value to Double
     */
    private Double parseDouble(String valueToParse, String fitsElem) {
    	Double doubleValue = null;
    	try {
    		doubleValue = Double.parseDouble(valueToParse);
    	}
    	catch (NumberFormatException | NullPointerException e) {
        	logger.info("Could not parse element [" + fitsElem + "] Double: " + valueToParse + " -- ignoring value.. Exception message: " + e.getMessage());
    	}
    	return doubleValue;
    }

    /*
     * Parse a String value to Long
     */
    private Long parseLong(String valueToParse, String fitsElem) {
    	Long longValue = null;
    	try {
    		longValue = Long.parseLong(valueToParse);
    	}
    	catch (NumberFormatException | NullPointerException e) {
        	logger.info("Could not parse element [" + fitsElem + "] to Long: " + valueToParse + " -- ignoring value.. Exception message: " + e.getMessage());
    	}
    	return longValue;
    }
    
    /*
     * First convert String input to either Integer or Double then, subsequently, to an OTS Rational value.
     * The input could be fractional, separated by a '/' character which would then get converted to a Rational.
     */
    private Rational parseRational(String valueToParse, String fitsElem) {
    	Rational rationalValue = null;
    	Integer intValue = null;
    	Double dblValue = null;
    	
    	try {
    		intValue = Integer.parseInt(valueToParse);
    	}
    	catch(NumberFormatException | NullPointerException e) {} 
    	
        if (intValue != null) {
            rationalValue = new Rational (intValue, 1);
        }
        else {
        	try {
        		dblValue = Double.parseDouble(valueToParse);
        	}
        	catch(NumberFormatException | NullPointerException e) {}
        	
        	if (dblValue != null) {
        		rationalValue = new Rational ((int) (dblValue * 100 + 0.5), 100);
        	}
        	else if(valueToParse.contains("/")) {
        		try {
        			int num = Integer.parseInt(valueToParse.substring(0,valueToParse.indexOf("/")));
        			int den = Integer.parseInt(valueToParse.substring(valueToParse.indexOf("/")+1));
        			rationalValue = new Rational(num,den);
        		}
        		catch (NumberFormatException | NullPointerException e) {}
        	}
        }
    	
        if (rationalValue == null) {
        	logger.info("Could not parse element [" + fitsElem + "] to Rational -- ignoring valueToParse: [" + valueToParse + "]");
        }
    	return rationalValue;
    }
}

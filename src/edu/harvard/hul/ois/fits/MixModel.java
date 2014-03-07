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

import java.util.StringTokenizer;

import edu.harvard.hul.ois.ots.schemas.MIX.BasicDigitalObjectInformation;
import edu.harvard.hul.ois.ots.schemas.MIX.BasicImageCharacteristics;
import edu.harvard.hul.ois.ots.schemas.MIX.BasicImageInformation;
import edu.harvard.hul.ois.ots.schemas.MIX.BitsPerSample;
import edu.harvard.hul.ois.ots.schemas.MIX.CameraCaptureSettings;
import edu.harvard.hul.ois.ots.schemas.MIX.ColorProfile;
import edu.harvard.hul.ois.ots.schemas.MIX.Colormap;
import edu.harvard.hul.ois.ots.schemas.MIX.Component;
import edu.harvard.hul.ois.ots.schemas.MIX.DigitalCameraCapture;
import edu.harvard.hul.ois.ots.schemas.MIX.DigitalCameraModel;
import edu.harvard.hul.ois.ots.schemas.MIX.EncodingOptions;
import edu.harvard.hul.ois.ots.schemas.MIX.GPSData;
import edu.harvard.hul.ois.ots.schemas.MIX.GPSDestLatitude;
import edu.harvard.hul.ois.ots.schemas.MIX.GPSDestLongitude;
import edu.harvard.hul.ois.ots.schemas.MIX.GPSLatitude;
import edu.harvard.hul.ois.ots.schemas.MIX.GPSLongitude;
import edu.harvard.hul.ois.ots.schemas.MIX.GeneralCaptureInformation;
import edu.harvard.hul.ois.ots.schemas.MIX.IccProfile;
import edu.harvard.hul.ois.ots.schemas.MIX.ImageAssessmentMetadata;
import edu.harvard.hul.ois.ots.schemas.MIX.ImageCaptureMetadata;
import edu.harvard.hul.ois.ots.schemas.MIX.ImageColorEncoding;
import edu.harvard.hul.ois.ots.schemas.MIX.ImageData;
import edu.harvard.hul.ois.ots.schemas.MIX.JPEG2000;
import edu.harvard.hul.ois.ots.schemas.MIX.Mix;
import edu.harvard.hul.ois.ots.schemas.MIX.PhotometricInterpretation;
import edu.harvard.hul.ois.ots.schemas.MIX.PrimaryChromaticities;
import edu.harvard.hul.ois.ots.schemas.MIX.ReferenceBlackWhite;
import edu.harvard.hul.ois.ots.schemas.MIX.ScannerCapture;
import edu.harvard.hul.ois.ots.schemas.MIX.ScannerModel;
import edu.harvard.hul.ois.ots.schemas.MIX.ScanningSystemSoftware;
import edu.harvard.hul.ois.ots.schemas.MIX.SpatialMetrics;
import edu.harvard.hul.ois.ots.schemas.MIX.SpecialFormatCharacteristics;
import edu.harvard.hul.ois.ots.schemas.MIX.SubjectDistance;
import edu.harvard.hul.ois.ots.schemas.MIX.Tiles;
import edu.harvard.hul.ois.ots.schemas.MIX.WhitePoint;
import edu.harvard.hul.ois.ots.schemas.MIX.YCbCr;
import edu.harvard.hul.ois.ots.schemas.MIX.YCbCrCoefficients;
import edu.harvard.hul.ois.ots.schemas.MIX.YCbCrSubSampling;
import edu.harvard.hul.ois.ots.schemas.MIX.GPSItudeElement;
import edu.harvard.hul.ois.ots.schemas.XmlContent.Rational;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;

/** This class represents a Mix object, with various convenient functions for use by
 *  XmlContentConverter in building up the structure. It's a captive class with no
 *  public interface, so data fields are directly accessible.
 */
public class MixModel {

    protected Mix mix;
    protected BasicDigitalObjectInformation bdoi;
    protected BasicImageInformation bii;
    protected BasicImageCharacteristics bic;
    protected PhotometricInterpretation phi;
    protected ImageCaptureMetadata icm;
    protected ImageAssessmentMetadata iam;
    protected SpatialMetrics sm;
    protected ImageColorEncoding ice;
    protected GeneralCaptureInformation gci;
    protected ColorProfile cp;
    protected YCbCr ycbcr;
    protected SpecialFormatCharacteristics sfc;
    protected JPEG2000 jp2000;
    protected EncodingOptions eo;
    protected Tiles tiles;
    protected WhitePoint wp;
    protected PrimaryChromaticities pc;
    protected ScannerCapture sc;
    protected ScannerModel scanm;
    protected ScanningSystemSoftware sss;
    protected DigitalCameraCapture dcc;
    protected DigitalCameraModel dcm;
    protected CameraCaptureSettings ccs;
    protected IccProfile iccp;
    protected ImageData id;
    protected SubjectDistance sd;
    protected GPSData gps;
    protected Colormap cm;
    
    
    
    protected MixModel () {
        mix = new Mix ();
        try {
             // Add the top-level elements to the mix element, to make life simpler.
            bdoi = new BasicDigitalObjectInformation();
            mix.setBasicDigitalObjectInformation(bdoi);
            bii = new BasicImageInformation ();
            mix.setBasicImageInformation (bii);
            bic = new BasicImageCharacteristics ();
            bii.setBasicImageCharacteristics (bic);
            phi = new PhotometricInterpretation ();
            bic.setPhotometricInterpetation(phi);
            icm = new ImageCaptureMetadata ();
            mix.setImageCaptureMetadata(icm);
            iam = new ImageAssessmentMetadata();
            mix.setImageAssessmentMetadata(iam);
            sm = new SpatialMetrics ();
            iam.setSpatialMetrics(sm);
            ice = new ImageColorEncoding ();
            iam.setImageColorEncoding(ice);
            gci = new GeneralCaptureInformation ();
            icm.setGeneralCaptureInformation(gci);
            
            // Here are some pieces which are constructed once, but added only if they're needed.
            cp = new ColorProfile ();           // Add this to phi if needed
            iccp = new IccProfile();            // Add this to cp if needed
            ycbcr = new YCbCr ();               // Add this to phi if needed
            sfc = new SpecialFormatCharacteristics();  // Add this to bii if needed
            jp2000 = new JPEG2000 ();           // Add this to sfc if needed
            eo = new EncodingOptions ();
            tiles = new Tiles ();               // Add this to eo if needed
            wp = null;                          // We leave this null so we can tell if it's been created
            pc = null;                          // We leave this null so we can tell if it's been created
            sc = new ScannerCapture ();         // Add this to icm if needed
            scanm = new ScannerModel ();        // Add this to sc if needed
            sss = new ScanningSystemSoftware ();  // add this to sc if needed
            id = new ImageData ();              // Add this to ccs if needed
            gps = new GPSData ();               // Add this to ccs if needed
            sd = new SubjectDistance ();        // Add this to id if needed
            
            // Build DigitalCameraCapture with subelements, but don't hook it to icm
            // until it's needed.
            dcc = new DigitalCameraCapture ();
            dcm = new DigitalCameraModel ();
            dcc.setDigitalCameraModel(dcm);
            ccs = new CameraCaptureSettings ();
            dcc.setCameraCaptureSettings(ccs);
            
            cm = new Colormap();
        }
        catch (XmlContentException e) {
            // Should never happen, unless the code is buggy
        }
    }
    
    /** Fill out a YCbCrSubSampling element, tokenizing the value */
    protected void populateYCbCrSS (YCbCrSubSampling ycbcrss, String data) {
        // Separation by spaces is easy material for an ordinary StringTokenizer
        StringTokenizer tok = new StringTokenizer (data);
        int hor;
        int ver;
        try {
            hor = Integer.parseInt (tok.nextToken ());
            ver = Integer.parseInt (tok.nextToken ());
        }
        catch (Exception e) {
            // Malformed data
            return;
        }
        try {
            ycbcr.setYCbCrSubSampling(ycbcrss);
            ycbcrss.setYCbCrSubsampleHoriz(hor);
            ycbcrss.setYCbCrSubsampleVert(ver);
            phi.setYCbCr(ycbcr);
        }
        catch (XmlContentException e) {
        }
    }
    
    protected void populateYCbCrCoefficients (String data) {
        StringTokenizer tok = new StringTokenizer (data);
        double red;
        double green;
        double blue;
        try {
            red = Double.parseDouble (tok.nextToken ());
            green = Double.parseDouble (tok.nextToken ());
            blue = Double.parseDouble (tok.nextToken ());
        }
        catch (Exception e) {
            // Malformed data
            return;
        }
        YCbCrCoefficients ycbcrc = new YCbCrCoefficients ();
        try {
            ycbcr.setYCbCrCoefficients(ycbcrc);
            phi.setYCbCr (ycbcr);
            ycbcrc.setLumaRed(new Rational ((int) (red * 100), 100));
            ycbcrc.setLumaGreen(new Rational ((int) (green * 100), 100));
            ycbcrc.setLumaBlue(new Rational ((int) (blue * 100), 100));
        }
        catch (XmlContentException e) {}
    }
    
    
    protected void populateJPEG2000 () {
        try {
            bii.setSpecialFormatCharacteristics(sfc);
            sfc.setJPEG2000(jp2000);
            jp2000.setEncodingOptions(eo);
            eo.setTiles(tiles);
        }
        catch (XmlContentException e) {
        }
    }
    
    protected void populateWhitePoint () {
        try {
            if (wp == null) {
                wp = new WhitePoint ();
                ice.addWhitePoint (wp);
            }
        }
        catch (XmlContentException e) {
        }
    }
    
    protected void populatePrimaryChromaticities () {
        try {
            if (pc == null) {
                pc = new PrimaryChromaticities ();
                ice.addPrimaryChromaticities(pc);
            }
        }
        catch (XmlContentException e) {
        }
    }
    
    
    //bits per sample in FITS should be in the form "8 8 8", with all values
    //  concatenated together separated by a space.
    protected void setBitsPerSample (String stringBPS) {
        try {
            BitsPerSample bps = new BitsPerSample ();
            bps.setBitsPerSampleUnit ("integer");
            String[] bpsParts = stringBPS.split(" ");
            for(int i=0;i<bpsParts.length; i++) {
            	bps.addBitsPerSampleValue(Integer.parseInt(bpsParts[i]));
            }
            ice.setBitsPerSample (bps);
        }
        catch (XmlContentException e) {
        }
    }
    
    

    /** Make the ColorProfile and ICCProfile part of the MIX */
    protected void attachIccp () {
        try {
            phi.setColorProfile(cp);
            cp.setIccProfile(iccp);
        }
        catch (XmlContentException e) {}
    }
    
    /** Make the ScannerCapture part of the MIX */
    protected void attachScannerCapture () {
        try {
            icm.setScannerCapture (sc);
        }
        catch (XmlContentException e) {}
    }
    
    protected void attachScanningSystemSoftware () {
        try {
            icm.setScannerCapture (sc);
            sc.setScanningSystemSoftware(sss);
        }
        catch (XmlContentException e) {}
    }
    
    protected void attachImageData () {
        try {
            ccs.setImageData (id);
            sc.setScanningSystemSoftware(sss);
            icm.setDigitalCameraCapture (dcc);
            // The links in between are already set up
        }
        catch (XmlContentException e) {}
    }
    
    protected void attachGPSData () {
        try {
            ccs.setGPSData (gps);
            sc.setScanningSystemSoftware(sss);
            icm.setDigitalCameraCapture (dcc);
            // The links in between are already set up
        }
        catch (XmlContentException e) {}
    }

    protected void populateGPSLongitude (String longitude) {
        attachGPSData ();
        GPSLongitude longit = new GPSLongitude ();
        parseItude (longit, longitude);
        try {
            gps.setGPSLongitude(longit);
        } 
        catch (XmlContentException e) {}
    }

    protected void populateGPSLatitude (String latitude) {
        attachGPSData ();
        GPSLatitude latit = new GPSLatitude ();
        parseItude (latit, latitude);
        try {
            gps.setGPSLatitude(latit);
        } 
        catch (XmlContentException e) {}
    }

    protected void populateGPSDestLongitude (String longitude) {
        attachGPSData ();
        GPSDestLongitude longit = new GPSDestLongitude ();
        parseItude (longit, longitude);
        try {
            gps.setGPSDestLongitude(longit);
        }
        catch (XmlContentException e) {}
    }

    protected void populateGPSDestLatitude (String latitude)  {
        attachGPSData ();
        GPSDestLatitude latit = new GPSDestLatitude ();
        parseItude (latit, latitude);
        try {
            gps.setGPSDestLatitude(latit);
        } 
        catch (XmlContentException e) {}
    }
    
    protected void populateReferenceBlackWhite (String data, String colorspace) {
        /* This consists of a series of six numbers, separated by spaces, each of which
         * is a headroom-footroom pair. The second argument tells us whether
         * the components are yCbCr or RGB. For the current cut, assume YCbCr.
         */
        StringTokenizer tok = new StringTokenizer (data);
        boolean rgb = ("RGB".equals (colorspace));
        double rbwVal[] = new double[6];
        try {
            for (int i = 0; i < 6; i++) {
                rbwVal[i] = Double.parseDouble(tok.nextToken());
            }
        }
        catch (Exception e) {
            return;
        }
        try {
            ReferenceBlackWhite rbw = new ReferenceBlackWhite ();
            phi.addReferenceBlackWhite(rbw);
            for (int i = 0; i < 6; i += 2) {
                Component comp = new Component ();
                rbw.addComponent(comp);
                String interp;
                switch (i) {
                case 0:
                    if (rgb)
                        interp = "R";
                    else
                        interp = "Y";
                    break;
                case 2:
                    if (rgb)
                        interp = "G";
                    else
                        interp = "Cb";
                    break;
                default:
                    if (rgb)
                        interp = "B";
                    else
                        interp = "Cr";
                    break;
                }
                comp.setComponentPhotometricInterpretation(interp);
                comp.setHeadroom (new Rational ((int) (rbwVal[i] * 100), 100));
                comp.setFootroom (new Rational ((int) (rbwVal[i+1] * 100), 100));
            }
        }
        catch (XmlContentException e) {}
    }

    /** Make the ScannerModel part of the MIX */
    protected void attachScannerModel () {
        try {
            icm.setScannerCapture (sc);
            sc.setScannerModel (scanm);
        }
        catch (XmlContentException e) {}
    }
    
    /** Make the Colormap part of the MIX */
    protected void attachColorMap () {
        try {
            ice.setColormap (cm);
        }
        catch (XmlContentException e) {}
    }
    
    protected void attachSubjectDistance () {
        try {
            id.setSubjectDistance (sd);
            ccs.setImageData (id);
            icm.setDigitalCameraCapture (dcc);
            // The links in between are already set up
        }
        catch (XmlContentException e) {}
    }

    /** Parse the longitude or latitude string from ExifTool. This is in
     *  a rather ugly format, like this:
     *  33 deg 24' 51.80" N
     */
    private void parseItude (GPSItudeElement elem, String itude) {
        StringBuffer itudeBuf = new StringBuffer (itude);
        // Get rid of the non-number portions 
        try {
            int pos = itudeBuf.indexOf ("deg ");
            if (pos > 0)
                itudeBuf.delete (pos, pos + 4);
            pos = itudeBuf.indexOf ("\"");
            if (pos > 0)
                itudeBuf.deleteCharAt(pos);
            pos = itudeBuf.indexOf ("'");
            if (pos > 0)
                itudeBuf.deleteCharAt(pos);

            // Now we should just have three numbers separated by spaces.
            // Fractional degrees or minutes don't make much sense given that
            // seconds exist, so treat only seconds as a double.
            StringTokenizer tok = new StringTokenizer (itudeBuf.toString());
            int deg = 0;
            int min = 0;
            double sec = 0;
            
            deg = Integer.parseInt (tok.nextToken ());
            if (tok.hasMoreElements())
                min = Integer.parseInt (tok.nextToken ());
            if (tok.hasMoreElements())
                sec = Double.parseDouble (tok.nextToken ());
            
            elem.setDegrees (new Rational (deg, 1));
            elem.setMinutes (new Rational (min, 1));
            elem.setSeconds (new Rational ((int) (sec * 100), 100));
        }
        catch (Exception e) {
        }
    }
}

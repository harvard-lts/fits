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

package edu.harvard.hul.ois.fits.junit;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.TestCase;

import edu.harvard.hul.ois.fits.XmlContentConverter;
import edu.harvard.hul.ois.ots.schemas.DocumentMD.DocumentMD;
import edu.harvard.hul.ois.ots.schemas.DocumentMD.DocumentMD.Feature;
import edu.harvard.hul.ois.ots.schemas.MIX.BasicDigitalObjectInformation;
import edu.harvard.hul.ois.ots.schemas.MIX.BasicImageCharacteristics;
import edu.harvard.hul.ois.ots.schemas.MIX.BasicImageInformation;
import edu.harvard.hul.ois.ots.schemas.MIX.CameraCaptureSettings;
import edu.harvard.hul.ois.ots.schemas.MIX.Component;
import edu.harvard.hul.ois.ots.schemas.MIX.DigitalCameraCapture;
import edu.harvard.hul.ois.ots.schemas.MIX.GPSData;
import edu.harvard.hul.ois.ots.schemas.MIX.GPSDestLatitude;
import edu.harvard.hul.ois.ots.schemas.MIX.ImageCaptureMetadata;
import edu.harvard.hul.ois.ots.schemas.MIX.Mix;
import edu.harvard.hul.ois.ots.schemas.MIX.PhotometricInterpretation;
import edu.harvard.hul.ois.ots.schemas.MIX.ReferenceBlackWhite;
import edu.harvard.hul.ois.ots.schemas.MIX.YCbCr;
import edu.harvard.hul.ois.ots.schemas.MIX.YCbCrCoefficients;
import edu.harvard.hul.ois.ots.schemas.TextMD.CharacterInfo;
import edu.harvard.hul.ois.ots.schemas.TextMD.MarkupBasis;
import edu.harvard.hul.ois.ots.schemas.TextMD.MarkupLanguage;
import edu.harvard.hul.ois.ots.schemas.TextMD.TextMD;


//import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;
//import org.jdom.JDOMException;


public class XmlContentConverterTest {

    public void testToMix () {
        // Construct a document using JDOM
        Document jdoc = new Document ();
        Element imgElem = new Element ("image");
        jdoc.addContent(imgElem);
        
        Element elem = new Element ("byteOrder");
        elem.addContent("big endian");
        imgElem.addContent (elem);
        
        elem = new Element ("gpsVersionID");
        elem.addContent ("99.5");
        imgElem.addContent (elem);
        
        elem = new Element ("gpsDestLatitude");
        elem.addContent ("33 deg 24' 51.40\" N");
        imgElem.addContent (elem);
        
        elem = new Element ("referenceBlackWhite");
        elem.addContent ("0.0 255.0 0.0 255.0 0.0 255.0");
        imgElem.addContent (elem);
        
        elem = new Element ("YCbCrCoefficients");
        elem.addContent ("10.0 20.0 30.0");
        imgElem.addContent (elem);
        
        XmlContentConverter conv = new XmlContentConverter ();
        Mix mix = (Mix) conv.toMix (imgElem,null);
        BasicDigitalObjectInformation bdoi = mix.getBasicDigitalObjectInformation();
        String byteOrder = bdoi.getByteOrder().toString ();
        assertEquals ("big endian", byteOrder);
        
        ImageCaptureMetadata icm = mix.getImageCaptureMetadata();
        DigitalCameraCapture dcc = icm.getDigitalCameraCapture();
        CameraCaptureSettings ccs = dcc.getCameraCaptureSettings();
        GPSData gps = ccs.getGPSData();
        String gpsVersionID = gps.getGpsVersionID().toString();
        assertEquals ("99.5", gpsVersionID);
        
        GPSDestLatitude dlat = gps.getGPSDestLatitude();
        double deg = dlat.getDegrees().toRational().getDouble();
        double min = dlat.getMinutes().toRational().getDouble();
        double sec = dlat.getSeconds().toRational().getDouble();
        assertTrue ((int) deg == 33);
        assertTrue ((int) min == 24);
        assertTrue ((int) sec == 51);
        
        BasicImageInformation bii = mix.getBasicImageInformation();
        BasicImageCharacteristics bic = bii.getBasicImageCharacteristics();
        PhotometricInterpretation phi = bic.getPhotometricInterpretation();
        List<ReferenceBlackWhite> rbwList = phi.getReferenceBlackWhites();
        ReferenceBlackWhite rbw = rbwList.get(0);
        List<Component> compList = rbw.getComponents();
        assertTrue (compList.size() == 3);
        Component comp = compList.get(0);
        assertEquals ("Y", comp.getComponentPhotometricInterpretation().toString ());
        double headroom = comp.getHeadroom().toRational().getDouble();
        assertTrue (headroom == 0.0);
        
        YCbCr ycbcr = phi.getYCbCr();
        YCbCrCoefficients ycbcrc = ycbcr.getYCbCrCoefficients();
        double lumaRed = ycbcrc.getLumaRed().toRational().getDouble ();
        assertTrue (lumaRed == 10.0);
        double lumaGreen = ycbcrc.getLumaGreen().toRational().getDouble ();
        assertTrue (lumaGreen == 20.0);
        double lumaBlue = ycbcrc.getLumaBlue().toRational().getDouble ();
        assertTrue (lumaBlue == 30.0);    
    }

    public void testToDocumentMD () {
        // Construct a document using JDOM
        Document jdoc = new Document ();
        Element docElem = new Element ("document");
        jdoc.addContent(docElem);

        Element elem = new Element ("isTagged");
        elem.addContent("no");
        docElem.addContent (elem);
        
        elem = new Element ("hasOutline");
        elem.addContent("no");
        docElem.addContent (elem);
        
        elem = new Element ("hasAnnotations");
        elem.addContent("no");
        docElem.addContent (elem);
        
        elem = new Element ("pageCount");
        elem.addContent("6");
        docElem.addContent (elem);
        
        elem = new Element ("isRightsManaged");
        elem.addContent("yes");
        docElem.addContent (elem);
        
        elem = new Element ("isProtected");
        elem.addContent("no");
        docElem.addContent (elem);
        
        elem = new Element( "hasForms");
        elem.addContent("yes");
        docElem.addContent (elem);

        XmlContentConverter conv = new XmlContentConverter ();
        DocumentMD dmd = (DocumentMD) conv.toDocumentMD (docElem);
        List<Feature> features = dmd.getFeatures ();
        assertTrue (features.size() == 1);
        Feature feature = features.get(0);
        assertTrue (feature == Feature.hasForms);

        assertTrue (dmd.getPageCount() == 6);
	}
        
    public void testToTextMD () {
        final String mln = "http://www.loc.gov/standards/mets/mets.xsd";
        // Construct a document using JDOM
        Document jdoc = new Document ();
        Element textElem = new Element ("text");
        jdoc.addContent(textElem);
        
        Element elem = new Element ("linebreak");
        elem.addContent ("LF");
        textElem.addContent (elem);
        
        elem = new Element ("charset");
        elem.addContent ("US-ASCII");
        textElem.addContent (elem);

        elem = new Element ("markupBasis");
        elem.addContent ("HTML");
        textElem.addContent (elem);
        
        elem = new Element ("markupBasisVersion");
        elem.addContent ("1.0");
        textElem.addContent (elem);
        
        elem = new Element ("markupLanguage");
        elem.addContent (mln);
        textElem.addContent (elem);
        
        XmlContentConverter conv = new XmlContentConverter ();
        TextMD tmd = (TextMD) conv.toTextMD (textElem);
        
        List<CharacterInfo> chinfos = tmd.getCharacterInfos();
        assertTrue (chinfos.size() == 1);
        CharacterInfo chinfo = chinfos.get(0);
        assertEquals ("US-ASCII", chinfo.getCharset());
        assertEquals ("LF", chinfo.getLinebreak ());
        
        List<MarkupBasis> mkbases = tmd.getMarkupBases();
        assertTrue (mkbases.size () == 1);
        MarkupBasis mkbas = mkbases.get(0);
        assertEquals ("HTML", mkbas.getValue());
        assertEquals ("1.0", mkbas.getVersion());
        
        List<MarkupLanguage> mklangs = tmd.getMarkupLanguages();
        assertTrue (mklangs.size() == 1);
        MarkupLanguage mklang = mklangs.get(0);
        assertEquals (mln, mklang.getValue());
    }

}

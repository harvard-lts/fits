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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Test;

import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
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

public class XmlContentConverterTest extends AbstractLoggingTest {
	
	private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);
	
	private static Logger logger = Logger.getLogger(XmlContentConverterTest.class);

	@Test
    public void testToMix () {
        // Construct a document using JDOM
        Document jdoc = new Document ();
        Element imgElem = new Element ("image", fitsNS);
        jdoc.addContent(imgElem);
        
        Element elem = new Element ("byteOrder", fitsNS);
        elem.addContent("big endian");
        imgElem.addContent (elem);
        
        elem = new Element ("gpsVersionID", fitsNS);
        elem.addContent ("99.5");
        imgElem.addContent (elem);
        
        elem = new Element ("gpsDestLatitude", fitsNS);
        elem.addContent ("33 deg 24' 51.40\" N");
        imgElem.addContent (elem);
        
        elem = new Element ("referenceBlackWhite", fitsNS);
        elem.addContent ("0.0 255.0 0.0 255.0 0.0 255.0");
        imgElem.addContent (elem);
        
        elem = new Element ("YCbCrCoefficients", fitsNS);
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
        assertEquals (33.0, deg, 0.0);
        assertEquals (24.0, min, 0.0);
        assertEquals (51.0, sec, 0.4);
        
        BasicImageInformation bii = mix.getBasicImageInformation();
        BasicImageCharacteristics bic = bii.getBasicImageCharacteristics();
        PhotometricInterpretation phi = bic.getPhotometricInterpretation();
        List<ReferenceBlackWhite> rbwList = phi.getReferenceBlackWhites();
        ReferenceBlackWhite rbw = rbwList.get(0);
        List<Component> compList = rbw.getComponents();
        assertEquals (3, compList.size());
        Component comp = compList.get(0);
        assertEquals ("Y", comp.getComponentPhotometricInterpretation().toString ());
        double headroom = comp.getHeadroom().toRational().getDouble();
        assertEquals (0.0, headroom, 0.0);
        
        YCbCr ycbcr = phi.getYCbCr();
        YCbCrCoefficients ycbcrc = ycbcr.getYCbCrCoefficients();
        double lumaRed = ycbcrc.getLumaRed().toRational().getDouble ();
        assertEquals (10.0, lumaRed, 0.0);
        double lumaGreen = ycbcrc.getLumaGreen().toRational().getDouble ();
        assertEquals (20.0, lumaGreen, 0.0);
        double lumaBlue = ycbcrc.getLumaBlue().toRational().getDouble ();
        assertEquals (30.0, lumaBlue, 0.0);    
    }

	@Test
    public void testToDocumentMD() {
        // Construct a document using JDOM
        Document jdoc = new Document();
        Element docElem = new Element("document", fitsNS);
        jdoc.addContent(docElem);

        Element elem = new Element("isTagged", fitsNS); // a feature
        elem.addContent("yes");
        docElem.addContent(elem);
        
        elem = new Element("hasOutline", fitsNS); // a feature
        elem.addContent("yes");
        docElem.addContent(elem);
        
        elem = new Element("hasAnnotations", fitsNS); // a feature
        elem.addContent("yes");
        docElem.addContent(elem);
        
        elem = new Element("pageCount", fitsNS);
        elem.addContent("6");
        docElem.addContent(elem);
        
        elem = new Element("isRightsManaged", fitsNS);
        elem.addContent("yes");
        docElem.addContent(elem);
        
        elem = new Element("isProtected", fitsNS);
        elem.addContent("no");
        docElem.addContent(elem);
        
        elem = new Element("hasForms", fitsNS); // a feature
        elem.addContent("yes");
        docElem.addContent(elem);
        
        elem = new Element("hasHyperlinks", fitsNS);
        elem.addContent("yes");
        docElem.addContent(elem);
        
        elem = new Element("hasEmbeddedResources", fitsNS);
        elem.addContent("yes");
        docElem.addContent(elem);

        // add a font
        Element fontElem1 = new Element("font", fitsNS);
        Element elemFontName = new Element("fontName", fitsNS);
        elemFontName.addContent("Helvetica");
        fontElem1.addContent(elemFontName);
        docElem.addContent(fontElem1);

        // add another font
        Element fontElem2 = new Element("font", fitsNS);
        elemFontName = new Element("fontName", fitsNS);
        elemFontName.addContent("Times");
        fontElem2.addContent(elemFontName);
        docElem.addContent(fontElem2);

        XmlContentConverter conv = new XmlContentConverter();
        DocumentMD dmd =(DocumentMD) conv.toDocumentMD(docElem);
        assertEquals(6, dmd.getPageCount());
        List<Feature> features = dmd.getFeatures();
        assertEquals(6, features.size());
        assertTrue(features.contains(Feature.hasForms));
        assertTrue(features.contains(Feature.hasOutline));
        assertTrue(features.contains(Feature.hasAnnotations));
        assertTrue(features.contains(Feature.isTagged));
//        assertTrue(features.contains(Feature.hasHyperlinks));
//        assertTrue(features.contains(Feature.hasEmbeddedResources));
        
        assertEquals(2, dmd.getFonts().size());
	}

	@Test
    public void testToTextMD () {
        final String mln = "http://www.loc.gov/standards/mets/mets.xsd";
        // Construct a document using JDOM
        Document jdoc = new Document ();
        Element textElem = new Element ("text", fitsNS);
        jdoc.addContent(textElem);
        
        Element elem = new Element ("linebreak", fitsNS);
        elem.addContent ("LF");
        textElem.addContent (elem);
        
        elem = new Element ("charset", fitsNS);
        elem.addContent ("US-ASCII");
        textElem.addContent (elem);

        elem = new Element ("markupBasis", fitsNS);
        elem.addContent ("HTML");
        textElem.addContent (elem);
        
        elem = new Element ("markupBasisVersion", fitsNS);
        elem.addContent ("1.0");
        textElem.addContent (elem);
        
        elem = new Element ("markupLanguage", fitsNS);
        elem.addContent (mln);
        textElem.addContent (elem);
        
        XmlContentConverter conv = new XmlContentConverter ();
        TextMD tmd = (TextMD) conv.toTextMD (textElem);
        
        List<CharacterInfo> chinfos = tmd.getCharacterInfos();
        assertEquals(1, chinfos.size());
        CharacterInfo chinfo = chinfos.get(0);
        assertEquals("US-ASCII", chinfo.getCharset());
        assertEquals("LF", chinfo.getLinebreak ());
        
        List<MarkupBasis> mkbases = tmd.getMarkupBases();
        assertEquals(1, mkbases.size());
        MarkupBasis mkbas = mkbases.get(0);
        assertEquals ("HTML", mkbas.getValue());
        assertEquals ("1.0", mkbas.getVersion());
        
        List<MarkupLanguage> mklangs = tmd.getMarkupLanguages();
        assertEquals(1, mklangs.size());
        MarkupLanguage mklang = mklangs.get(0);
        assertEquals(mln, mklang.getValue());
    }

}

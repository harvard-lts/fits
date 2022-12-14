/*
 * Copyright 2009 Harvard University Library
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
package edu.harvard.hul.ois.fits.junit;

import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import org.junit.Assert;
import org.junit.Test;

public class FitsOutputTest extends AbstractLoggingTest {

    @Test
    public void testFitsOutput() throws Exception {

        FitsOutput fitsOutput = new FitsOutput(fitsMetadata);
        FitsMetadataElement element = fitsOutput.getMetadataElement("fileinfo");
        element = fitsOutput.getMetadataElement("filepath");
        String filePath = element.getValue();
        Assert.assertEquals("/home/drs2_fits/tomcat8_dev/webapps/fits/upload/1474662914011/400201494.mov", filePath);
        String version = fitsOutput.getFitsVersion();
        Assert.assertEquals("1.0.2", version);
        String metaType = fitsOutput.getTechMetadataType();
        Assert.assertEquals("video", metaType);
    }

    private String fitsMetadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "  <fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.0.2\" timestamp=\"9/23/16 4:35 PM\">"
            + "    <identification>"
            + "      <identity format=\"Quicktime\" mimetype=\"video/quicktime\" toolname=\"FITS\" toolversion=\"1.0.2\">"
            + "        <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" />"
            + "        <tool toolname=\"Droid\" toolversion=\"6.1.5\" />"
            + "        <tool toolname=\"file utility\" toolversion=\"5.04\" />"
            + "        <externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">x-fmt/384</externalIdentifier>"
            + "      </identity>"
            + "    </identification>"
            + "    <fileinfo>"
            + "      <creatingApplicationName toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Apple QuickTime</creatingApplicationName>"
            + "      <filepath toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">/home/drs2_fits/tomcat8_dev/webapps/fits/upload/1474662914011/400201494.mov</filepath>"
            + "      <filename toolname=\"MediaInfo\" toolversion=\"0.7.75\">400201494.mov</filename>"
            + "      <size toolname=\"MediaInfo\" toolversion=\"0.7.75\">80856</size>"
            + "      <md5checksum toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">18b1f8ff07702e1b1b96fc8700754fa1</md5checksum>"
            + "      <fslastmodified toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">1474662914000</fslastmodified>"
            + "    </fileinfo>"
            + "    <filestatus />"
            + "    <metadata>"
            + "      <video>"
            + "        <location toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">/home/drs2_fits/tomcat8_dev/webapps/fits/upload/1474662914011/400201494.mov</location>"
            + "        <mimeType toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">video/quicktime</mimeType>"
            + "        <format toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Quicktime</format>"
            + "        <formatProfile toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">QuickTime</formatProfile>"
            + "        <duration toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">4137</duration>"
            + "        <bitRate toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">156357</bitRate>"
            + "        <dateCreated toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2015-03-13 18:48:47</dateCreated>"
            + "        <dateModified toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2016-09-23 20:35:14</dateModified>"
            + "        <track type=\"video\" id=\"2\" toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">"
            + "          <videoDataEncoding>avc1</videoDataEncoding>"
            + "          <codecId>avc1</codecId>"
            + "          <codecCC>avc1</codecCC>"
            + "          <codecVersion>Baseline@L2.1</codecVersion>"
            + "          <codecName>AVC</codecName>"
            + "          <codecFamily>H.264</codecFamily>"
            + "          <codecInfo>Advanced Video Codec</codecInfo>"
            + "          <compression>Unknown</compression>"
            + "          <byteOrder>Unknown</byteOrder>"
            + "          <bitDepth>8 bits</bitDepth>"
            + "          <bitRate>122475</bitRate>"
            + "          <duration>4137</duration>"
            + "          <trackSize>63342</trackSize>"
            + "          <width>384 pixels</width>"
            + "          <height>288 pixels</height>"
            + "          <frameRate>30.000</frameRate>"
            + "          <frameRateMode>Variable</frameRateMode>"
            + "          <frameCount>124</frameCount>"
            + "          <aspectRatio>4:3</aspectRatio>"
            + "          <scanningFormat>Progressive</scanningFormat>"
            + "          <chromaSubsampling>4:2:0</chromaSubsampling>"
            + "          <colorspace>YUV</colorspace>"
            + "          <broadcastStandard>NTSC</broadcastStandard>"
            + "        </track>"
            + "        <track type=\"audio\" id=\"1\" toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">"
            + "          <audioDataEncoding>AAC</audioDataEncoding>"
            + "          <codecId>40</codecId>"
            + "          <codecFamily>AAC</codecFamily>"
            + "          <compression>Lossy</compression>"
            + "          <bitRate>27861</bitRate>"
            + "          <bitRateMode>Constant</bitRateMode>"
            + "          <duration>4132</duration>"
            + "          <trackSize>14390</trackSize>"
            + "          <soundField>Front: C</soundField>"
            + "          <samplingRate>22050</samplingRate>"
            + "          <numSamples>91111</numSamples>"
            + "          <channels>1</channels>"
            + "        </track>"
            + "        <standard>"
            + "          <ebucore:ebuCoreMain xmlns:ebucore=\"urn:ebu:metadata-schema:ebuCore_2014\">"
            + "            <ebucore:coreMetadata>"
            + "              <ebucore:format>"
            + "                <ebucore:videoFormat>"
            + "                  <ebucore:width unit=\"pixel\">384</ebucore:width>"
            + "                  <ebucore:height unit=\"pixel\">288</ebucore:height>"
            + "                  <ebucore:frameRate factorNumerator=\"1000\" factorDenominator=\"1001\">30</ebucore:frameRate>"
            + "                  <ebucore:aspectRatio typeLabel=\"display\">"
            + "                    <ebucore:factorNumerator>4</ebucore:factorNumerator>"
            + "                    <ebucore:factorDenominator>3</ebucore:factorDenominator>"
            + "                  </ebucore:aspectRatio>"
            + "                  <ebucore:videoEncoding typeLabel=\"avc1\" />"
            + "                  <ebucore:codec>"
            + "                    <ebucore:codecIdentifier>"
            + "                      <dc:identifier xmlns:dc=\"http://purl.org/dc/elements/1.1/\">avc1</dc:identifier>"
            + "                    </ebucore:codecIdentifier>"
            + "                    <ebucore:name>AVC</ebucore:name>"
            + "                    <ebucore:version>Baseline@L2.1</ebucore:version>"
            + "                    <ebucore:family>H.264</ebucore:family>"
            + "                  </ebucore:codec>"
            + "                  <ebucore:bitRate>122475</ebucore:bitRate>"
            + "                  <ebucore:scanningFormat>progressive</ebucore:scanningFormat>"
            + "                  <ebucore:videoTrack trackId=\"2\" />"
            + "                  <ebucore:technicalAttributeString typeLabel=\"chromaSubsampling\">4:2:0</ebucore:technicalAttributeString>"
            + "                  <ebucore:technicalAttributeString typeLabel=\"colorspace\">YUV</ebucore:technicalAttributeString>"
            + "                  <ebucore:technicalAttributeString typeLabel=\"frameRateMode\">variable</ebucore:technicalAttributeString>"
            + "                  <ebucore:technicalAttributeString typeLabel=\"byteOrder\">unknown</ebucore:technicalAttributeString>"
            + "                  <ebucore:technicalAttributeString typeLabel=\"compression\">unknown</ebucore:technicalAttributeString>"
            + "                  <ebucore:technicalAttributeString typeLabel=\"broadcastStandard\">NTSC</ebucore:technicalAttributeString>"
            + "                  <ebucore:technicalAttributeLong typeLabel=\"streamSize\">63342</ebucore:technicalAttributeLong>"
            + "                  <ebucore:technicalAttributeLong typeLabel=\"frameCount\">124</ebucore:technicalAttributeLong>"
            + "                  <ebucore:technicalAttributeLong typeLabel=\"bitDepth\">8</ebucore:technicalAttributeLong>"
            + "                  <ebucore:technicalAttributeLong typeLabel=\"duration\">4137</ebucore:technicalAttributeLong>"
            + "                </ebucore:videoFormat>"
            + "                <ebucore:audioFormat>"
            + "                  <ebucore:audioEncoding typeLabel=\"AAC\" />"
            + "                  <ebucore:codec>"
            + "                    <ebucore:codecIdentifier>"
            + "                      <dc:identifier xmlns:dc=\"http://purl.org/dc/elements/1.1/\">40</dc:identifier>"
            + "                    </ebucore:codecIdentifier>"
            + "                  </ebucore:codec>"
            + "                  <ebucore:audioTrackConfiguration typeLabel=\"Front: C\" />"
            + "                  <ebucore:samplingRate>22050</ebucore:samplingRate>"
            + "                  <ebucore:bitRate>27861</ebucore:bitRate>"
            + "                  <ebucore:bitRateMode>constant</ebucore:bitRateMode>"
            + "                  <ebucore:audioTrack trackId=\"1\" />"
            + "                  <ebucore:channels>1</ebucore:channels>"
            + "                  <ebucore:technicalAttributeString typeLabel=\"compression\">lossy</ebucore:technicalAttributeString>"
            + "                  <ebucore:technicalAttributeLong typeLabel=\"streamSize\">14390</ebucore:technicalAttributeLong>"
            + "                  <ebucore:technicalAttributeLong typeLabel=\"sampleCount\">91111</ebucore:technicalAttributeLong>"
            + "                  <ebucore:technicalAttributeLong typeLabel=\"duration\">4132</ebucore:technicalAttributeLong>"
            + "                </ebucore:audioFormat>"
            + "                <ebucore:audioFormatExtended>"
            + "                  <ebucore:audioChannelFormat>"
            + "                    <ebucore:audioBlockFormat>"
            + "                      <ebucore:position coordinate=\"x\">0</ebucore:position>"
            + "                      <ebucore:position coordinate=\"y\">0</ebucore:position>"
            + "                    </ebucore:audioBlockFormat>"
            + "                  </ebucore:audioChannelFormat>"
            + "                </ebucore:audioFormatExtended>"
            + "                <ebucore:containerFormat>"
            + "                  <ebucore:comment typeLabel=\"format\">Quicktime</ebucore:comment>"
            + "                  <ebucore:comment typeLabel=\"formatProfile\">QuickTime</ebucore:comment>"
            + "                </ebucore:containerFormat>"
            + "                <ebucore:duration>"
            + "                  <ebucore:editUnitNumber editRate=\"1000\" factorNumerator=\"1\" factorDenominator=\"1\">4137</ebucore:editUnitNumber>"
            + "                </ebucore:duration>"
            + "                <ebucore:mimeType typeLabel=\"video/quicktime\" />"
            + "                <ebucore:locator>/home/drs2_fits/tomcat8_dev/webapps/fits/upload/1474662914011/400201494.mov</ebucore:locator>"
            + "                <ebucore:technicalAttributeString typeLabel=\"overallBitRate\">156357</ebucore:technicalAttributeString>"
            + "                <ebucore:dateModified startTime=\"20:35:14Z\" startDate=\"2016-09-23\" />"
            + "              </ebucore:format>"
            + "            </ebucore:coreMetadata>"
            + "          </ebucore:ebuCoreMain>"
            + "        </standard>"
            + "      </video>"
            + "    </metadata>"
            + "    <statistics fitsExecutionTime=\"123\">"
            + "      <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" executionTime=\"109\" />"
            + "      <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"did not run\" />"
            + "      <tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />"
            + "      <tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />"
            + "      <tool toolname=\"Droid\" toolversion=\"6.1.5\" executionTime=\"14\" />"
            + "      <tool toolname=\"Jhove\" toolversion=\"1.11\" status=\"did not run\" />"
            + "      <tool toolname=\"file utility\" toolversion=\"5.04\" executionTime=\"98\" />"
            + "      <tool toolname=\"Exiftool\" toolversion=\"10.00\" status=\"did not run\" />"
            + "      <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />"
            + "      <tool toolname=\"OIS File Information\" toolversion=\"0.2\" executionTime=\"3\" />"
            + "      <tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />"
            + "      <tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"14\" />"
            + "      <tool toolname=\"Tika\" toolversion=\"1.10\" status=\"did not run\" />"
            + "    </statistics>"
            + "  </fits>";
}

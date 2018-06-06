package edu.harvard.hul.ois.fits.junit;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

public class FitsOutputXmlTest_2  extends AbstractLoggingTest{	
	
	String wavOutput_combined_spaces = 
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.0.1\" timestamp=\"7/18/16 8:57 AM\">" +
	"  <identification>" +
	"    <identity format=\"Waveform Audio\" mimetype=\"audio/x-wave\" toolname=\"FITS\" toolversion=\"1.0.1\">" +
	"      <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" />" +
	"      <tool toolname=\"Droid\" toolversion=\"6.1.5\" />" +
	"      <tool toolname=\"Jhove\" toolversion=\"1.11\" />" +
	"      <tool toolname=\"file utility\" toolversion=\"5.04\" />" +
	"      <tool toolname=\"Exiftool\" toolversion=\"10.00\" />" +
	"      <version toolname=\"Droid\" toolversion=\"6.1.5\">0 PCM Encoding</version>" +
	"      <externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">fmt/142</externalIdentifier>" +
	"      <externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">fmt/703</externalIdentifier>" +
	"    </identity>" +
	"  </identification>" +
	"  <fileinfo>" +
	"    <size toolname=\"Jhove\" toolversion=\"1.11\">898136</size>" +
	"    <filepath toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">/Users/dab980/git/fits/testfiles/test.wav</filepath>" +
	"    <filename toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">test.wav</filename>" +
	"    <md5checksum toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">6d8aca2634d70d4ceb7065d321d967ea</md5checksum>" +
	"    <fslastmodified toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">1446492288000</fslastmodified>" +
	"  </fileinfo>" +
	"  <filestatus>" +
	"    <well-formed toolname=\"Jhove\" toolversion=\"1.11\" status=\"SINGLE_RESULT\">true</well-formed>" +
	"    <valid toolname=\"Jhove\" toolversion=\"1.11\" status=\"SINGLE_RESULT\">true</valid>" +
	"  </filestatus>" +
	"  <metadata>" +
	"    <audio>" +
	"      <numSamples toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">299159</numSamples>" +
	"     <sampleRate toolname=\"OIS Audio Information\" toolversion=\"0.1\">96000</sampleRate>" +
	"      <audioDataEncoding toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"CONFLICT\">PCM</audioDataEncoding>" +
	"      <audioDataEncoding toolname=\"Jhove\" toolversion=\"1.11\" status=\"CONFLICT\">PCM audio in integer format</audioDataEncoding>" +
	"      <audioDataEncoding toolname=\"Exiftool\" toolversion=\"10.00\" status=\"CONFLICT\">Microsoft PCM</audioDataEncoding>" +
	"      <blockAlign toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">3</blockAlign>" +
	"      <time toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">458028579</time>" +
	"      <channels toolname=\"OIS Audio Information\" toolversion=\"0.1\">1</channels>" +
	"      <bitDepth toolname=\"OIS Audio Information\" toolversion=\"0.1\">24</bitDepth>" +
	"      <wordSize toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">3</wordSize>" +
	"      <offset toolname=\"OIS Audio Information\" toolversion=\"0.1\">46</offset>" +
	"      <byteOrder toolname=\"Jhove\" toolversion=\"1.11\" status=\"SINGLE_RESULT\">LITTLE_ENDIAN</byteOrder>" +
	"      <duration toolname=\"Exiftool\" toolversion=\"10.00\" status=\"SINGLE_RESULT\">3.12 s</duration>" +
	"      <standard>" +
	"        <aes:audioObject xmlns:aes=\"http://www.aes.org/audioObject\" xsi:schemaLocation=\"http://www.aes.org/audioObject http://www.aes.org/standards/schemas/aes57-2011-08-27.xsd\" ID=\"AUDIO_OBJECT_7e4b79e0-418b-4b40-a76e-ba4c9c68639c\" analogDigitalFlag=\"FILE_DIGITAL\" schemaVersion=\"1.0.0\" disposition=\"\">" +
	"          <aes:format specificationVersion=\"0 PCM Encoding\">Waveform Audio</aes:format>" +
	"          <aes:audioDataEncoding>PCM</aes:audioDataEncoding>" +
	"          <aes:byteOrder>LITTLE_ENDIAN</aes:byteOrder>" +
	"          <aes:firstSampleOffset>46</aes:firstSampleOffset>" +
	"          <aes:audioDataBlockSize>3</aes:audioDataBlockSize>" +
	"          <aes:use useType=\"OTHER\" otherType=\"unknown\" />" +
	"          <aes:primaryIdentifier identifierType=\"FILE_NAME\">test.wav</aes:primaryIdentifier>" +
	"          <aes:face audioObjectRef=\"AUDIO_OBJECT_7e4b79e0-418b-4b40-a76e-ba4c9c68639c\" direction=\"NONE\" ID=\"FACE_f9bd46f9-772f-40a2-ac0e-65378871eaa0\" label=\"face 1\">" +
	"            <aes:timeline>" +
	"              <aes:startTime editRate=\"1\">0</aes:startTime>" +
	"              <aes:duration editRate=\"96000\">299159</aes:duration>" +
	"            </aes:timeline>" +
	"            <aes:region formatRef=\"FORMAT_REGION_fd5aa0b9-9d0e-42a4-a6de-8f133b449fda\" ID=\"REGION_55033ba8-a75b-4cd7-8317-7128e4ba75fe\" label=\"region 1\" faceRef=\"FACE_f9bd46f9-772f-40a2-ac0e-65378871eaa0\">" +
	"              <aes:timeRange>" +
	"                <aes:startTime editRate=\"1\">0</aes:startTime>" +
	"                <aes:duration editRate=\"96000\">299159</aes:duration>" +
	"              </aes:timeRange>" +
	"              <aes:numChannels>1</aes:numChannels>" +
	"              <aes:stream ID=\"STREAM_b503a0c8-4109-4e52-a871-d02bc869c25d\" label=\"stream 0\" faceRegionRef=\"REGION_55033ba8-a75b-4cd7-8317-7128e4ba75fe\">" +
	"                <aes:channelAssignment frontRearPosition=\"0.0\" channelNum=\"0\" leftRightPosition=\"0.0\" />" +
	"              </aes:stream>" +
	"            </aes:region>" +
	"          </aes:face>" +
	"          <aes:formatList>" +
	"            <aes:formatRegion ID=\"FORMAT_REGION_fd5aa0b9-9d0e-42a4-a6de-8f133b449fda\" ownerRef=\"REGION_55033ba8-a75b-4cd7-8317-7128e4ba75fe\" label=\"format region 1\" xsi:type=\"aes:formatRegionType\">" +
	"              <aes:bitDepth>24</aes:bitDepth>" +
	"              <aes:sampleRate>96000.0</aes:sampleRate>" +
	"              <aes:wordSize>3</aes:wordSize>" +
	"              <aes:soundField>MONO</aes:soundField>" +
	"            </aes:formatRegion>" +
	"          </aes:formatList>" +
	"        </aes:audioObject>" +
	"      </standard>" +
	"    </audio>" +
	"  </metadata>" +
	"  <statistics fitsExecutionTime=\"853\">" +
	"    <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"did not run\" />" +
	"    <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" executionTime=\"194\" />" +
	"    <tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />" +
	"    <tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />" +
	"    <tool toolname=\"Droid\" toolversion=\"6.1.5\" executionTime=\"351\" />" +
	"    <tool toolname=\"Jhove\" toolversion=\"1.11\" executionTime=\"745\" />" +
	"    <tool toolname=\"file utility\" toolversion=\"5.04\" executionTime=\"747\" />" +
	"    <tool toolname=\"Exiftool\" toolversion=\"10.00\" executionTime=\"727\" />" +
	"    <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />" +
	"    <tool toolname=\"OIS File Information\" toolversion=\"0.2\" executionTime=\"174\" />" +
	"    <tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />" +
	"    <tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"559\" />" +
	"    <tool toolname=\"Tika\" toolversion=\"1.10\" executionTime=\"334\" />" +
	"  </statistics>" +
	"</fits>";
	
	String wavOutput_standard_spaces = 
			
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"	     <aes:audioObject xmlns:aes=\"http://www.aes.org/audioObject\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.aes.org/audioObject http://www.aes.org/standards/schemas/aes57-2011-08-27.xsd\" ID=\"AUDIO_OBJECT_dc675cdf-49fb-4a2b-8e17-54118338b959\" analogDigitalFlag=\"FILE_DIGITAL\" schemaVersion=\"1.0.0\" disposition=\"\">" +
	"          <aes:format specificationVersion=\"0 PCM Encoding\">Waveform Audio</aes:format>" +
	"          <aes:audioDataEncoding>PCM</aes:audioDataEncoding>" +
	"          <aes:byteOrder>LITTLE_ENDIAN</aes:byteOrder>" +
	"          <aes:firstSampleOffset>46</aes:firstSampleOffset>" +
	"          <aes:audioDataBlockSize>3</aes:audioDataBlockSize>" +
	"          <aes:use useType=\"OTHER\" otherType=\"unknown\" />" +
	"          <aes:primaryIdentifier identifierType=\"FILE_NAME\">test.wav</aes:primaryIdentifier>" +
	"          <aes:face audioObjectRef=\"AUDIO_OBJECT_7e4b79e0-418b-4b40-a76e-ba4c9c68639c\" direction=\"NONE\" ID=\"FACE_f9bd46f9-772f-40a2-ac0e-65378871eaa0\" label=\"face 1\">" +
	"            <aes:timeline>" +
	"              <aes:startTime editRate=\"1\">0</aes:startTime>" +
	"              <aes:duration editRate=\"96000\">299159</aes:duration>" +
	"            </aes:timeline>" +
	"            <aes:region formatRef=\"FORMAT_REGION_fd5aa0b9-9d0e-42a4-a6de-8f133b449fda\" ID=\"REGION_55033ba8-a75b-4cd7-8317-7128e4ba75fe\" label=\"region 1\" faceRef=\"FACE_f9bd46f9-772f-40a2-ac0e-65378871eaa0\">" +
	"              <aes:timeRange>" +
	"                <aes:startTime editRate=\"1\">0</aes:startTime>" +
	"                <aes:duration editRate=\"96000\">299159</aes:duration>" +
	"              </aes:timeRange>" +
	"              <aes:numChannels>1</aes:numChannels>" +
	"              <aes:stream ID=\"STREAM_b503a0c8-4109-4e52-a871-d02bc869c25d\" label=\"stream 0\" faceRegionRef=\"REGION_55033ba8-a75b-4cd7-8317-7128e4ba75fe\">" +
	"                <aes:channelAssignment frontRearPosition=\"0.0\" channelNum=\"0\" leftRightPosition=\"0.0\" />" +
	"              </aes:stream>" +
	"            </aes:region>" +
	"          </aes:face>" +
	"          <aes:formatList>" +
	"            <aes:formatRegion ID=\"FORMAT_REGION_fd5aa0b9-9d0e-42a4-a6de-8f133b449fda\" ownerRef=\"REGION_55033ba8-a75b-4cd7-8317-7128e4ba75fe\" label=\"format region 1\" xsi:type=\"aes:formatRegionType\">" +
	"              <aes:bitDepth>24</aes:bitDepth>" +
	"              <aes:sampleRate>96000.0</aes:sampleRate>" +
	"              <aes:wordSize>3</aes:wordSize>" +
	"              <aes:soundField>MONO</aes:soundField>" +
	"            </aes:formatRegion>" +
	"          </aes:formatList>" +
	"        </aes:audioObject>";
	
	
	String wavOutput_fits_spaces = 
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.0.1\" timestamp=\"7/18/16 8:57 AM\">" +
	"  <identification>" +
	"    <identity format=\"Waveform Audio\" mimetype=\"audio/x-wave\" toolname=\"FITS\" toolversion=\"1.0.1\">" +
	"      <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" />" +
	"      <tool toolname=\"Droid\" toolversion=\"6.1.5\" />" +
	"      <tool toolname=\"Jhove\" toolversion=\"1.11\" />" +
	"      <tool toolname=\"file utility\" toolversion=\"5.04\" />" +
	"      <tool toolname=\"Exiftool\" toolversion=\"10.00\" />" +
	"      <version toolname=\"Droid\" toolversion=\"6.1.5\">0 PCM Encoding</version>" +
	"      <externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">fmt/142</externalIdentifier>" +
	"      <externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">fmt/703</externalIdentifier>" +
	"    </identity>" +
	"  </identification>" +
	"  <fileinfo>" +
	"    <size toolname=\"Jhove\" toolversion=\"1.11\">898136</size>" +
	"    <filepath toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">/Users/dab980/git/fits/testfiles/test.wav</filepath>" +
	"    <filename toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">test.wav</filename>" +
	"    <md5checksum toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">6d8aca2634d70d4ceb7065d321d967ea</md5checksum>" +
	"    <fslastmodified toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">1446492288000</fslastmodified>" +
	"  </fileinfo>" +
	"  <filestatus>" +
	"    <well-formed toolname=\"Jhove\" toolversion=\"1.11\" status=\"SINGLE_RESULT\">true</well-formed>" +
	"    <valid toolname=\"Jhove\" toolversion=\"1.11\" status=\"SINGLE_RESULT\">true</valid>" +
	"  </filestatus>" +
	"  <metadata>" +
	"    <audio>" +
	"      <numSamples toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">299159</numSamples>" +
	"      <sampleRate toolname=\"OIS Audio Information\" toolversion=\"0.1\">96000</sampleRate>" +
	"      <audioDataEncoding toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"CONFLICT\">PCM</audioDataEncoding>" +
	"      <audioDataEncoding toolname=\"Jhove\" toolversion=\"1.11\" status=\"CONFLICT\">PCM audio in integer format</audioDataEncoding>" +
	"      <audioDataEncoding toolname=\"Exiftool\" toolversion=\"10.00\" status=\"CONFLICT\">Microsoft PCM</audioDataEncoding>" +
	"      <blockAlign toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">3</blockAlign>" +
	"      <time toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">458028579</time>" +
	"      <channels toolname=\"OIS Audio Information\" toolversion=\"0.1\">1</channels>" +
	"      <bitDepth toolname=\"OIS Audio Information\" toolversion=\"0.1\">24</bitDepth>" +
	"      <wordSize toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"SINGLE_RESULT\">3</wordSize>" +
	"      <offset toolname=\"OIS Audio Information\" toolversion=\"0.1\">46</offset>" +
	"      <byteOrder toolname=\"Jhove\" toolversion=\"1.11\" status=\"SINGLE_RESULT\">LITTLE_ENDIAN</byteOrder>" +
	"      <duration toolname=\"Exiftool\" toolversion=\"10.00\" status=\"SINGLE_RESULT\">3.12 s</duration>" +
	"    </audio>" +
	"  </metadata>" +
	"  <statistics fitsExecutionTime=\"853\">" +
	"    <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"did not run\" />" +
	"    <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" executionTime=\"194\" />" +
	"    <tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />" +
	"    <tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />" +
	"    <tool toolname=\"Droid\" toolversion=\"6.1.5\" executionTime=\"351\" />" +
	"    <tool toolname=\"Jhove\" toolversion=\"1.11\" executionTime=\"745\" />" +
	"    <tool toolname=\"file utility\" toolversion=\"5.04\" executionTime=\"747\" />" +
	"    <tool toolname=\"Exiftool\" toolversion=\"10.00\" executionTime=\"727\" />" +
	"    <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />" +
	"    <tool toolname=\"OIS File Information\" toolversion=\"0.2\" executionTime=\"174\" />" +
	"    <tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />" +
	"    <tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"559\" />" +
	"    <tool toolname=\"Tika\" toolversion=\"1.10\" executionTime=\"334\" />" +
	"  </statistics>" +
	"</fits>";
	
	//
	// Video XML
	//
	
	String videoOutput_fits_no_space = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
	"<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.0.1\" timestamp=\"7/15/16 1:46 PM\">" + 
	"<identification>" + 
	"<identity format=\"Quicktime\" mimetype=\"video/quicktime\" toolname=\"FITS\" toolversion=\"1.0.1\">" + 
	"<tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" />" + 
	"<tool toolname=\"Droid\" toolversion=\"6.1.5\" />" + 
	"<tool toolname=\"file utility\" toolversion=\"5.04\" />" + 
	"<externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">x-fmt/384</externalIdentifier>" + 
	"</identity>" + 
	"</identification>" + 
	"<fileinfo>" + 
	"<creatingApplicationName toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Apple QuickTime</creatingApplicationName>" + 
	"<filepath toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">/Users/dab980/downloads/400192692.mov</filepath>" + 
	"<filename toolname=\"MediaInfo\" toolversion=\"0.7.75\">400192692.mov</filename>" + 
	"<size toolname=\"MediaInfo\" toolversion=\"0.7.75\">161654</size>" + 
	"<md5checksum toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">f398a3311f7676282cfa7da33322d99b</md5checksum>" + 
	"<fslastmodified toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">1468604566000</fslastmodified>" + 
	"</fileinfo>" + 
	"<filestatus />" + 
	"<metadata>" + 
	"<video>" + 
	"<location toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">/Users/dab980/Downloads/400192692.mov</location>" + 
	"<mimeType toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">video/quicktime</mimeType>" + 
	"<format toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Quicktime</format>" + 
	"<formatProfile toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">QuickTime</formatProfile>" + 
	"<duration toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">1567</duration>" + 
	"<bitRate toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">825292</bitRate>" + 
	"<dateCreated toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2014-10-04 15:40:46</dateCreated>" + 
	"<dateModified toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2016-07-15 17:42:46</dateModified>" + 
	"<track type=\"video\" id=\"1\" toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">" + 
	"<videoDataEncoding>avc1</videoDataEncoding>" + 
	"<codecId>avc1</codecId>" + 
	"<codecCC>avc1</codecCC>" + 
	"<codecVersion>Baseline@L3</codecVersion>" + 
	"<codecName>AVC</codecName>" + 
	"<codecFamily>H.264</codecFamily>" + 
	"<codecInfo>Advanced Video Codec</codecInfo>" + 
	"<compression>Unknown</compression>" + 
	"<byteOrder>Unknown</byteOrder>" + 
	"<bitDepth>8 bits</bitDepth>" + 
	"<bitRate>816894</bitRate>" + 
	"<duration>1567</duration>" + 
	"<trackSize>159975</trackSize>" + 
	"<width>568 pixels</width>" + 
	"<height>320 pixels</height>" + 
	"<frameRate>30.000</frameRate>" + 
	"<frameRateMode>Constant</frameRateMode>" + 
	"<frameCount>47</frameCount>" + 
	"<aspectRatio>16:9</aspectRatio>" + 
	"<scanningFormat>Progressive</scanningFormat>" + 
	"<chromaSubsampling>4:2:0</chromaSubsampling>" + 
	"<colorspace>YUV</colorspace>" + 
	"</track>" + 
	"</video>" + 
	"</metadata>" + 
	"<statistics fitsExecutionTime=\"529\">" + 
	"<tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" executionTime=\"493\" />" + 
	"<tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"did not run\" />" + 
	"<tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />" + 
	"<tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />" + 
	"<tool toolname=\"Droid\" toolversion=\"6.1.5\" executionTime=\"180\" />" + 
	"<tool toolname=\"Jhove\" toolversion=\"1.11\" status=\"did not run\" />" + 
	"<tool toolname=\"file utility\" toolversion=\"5.04\" executionTime=\"493\" />" + 
	"<tool toolname=\"Exiftool\" toolversion=\"10.00\" status=\"did not run\" />" + 
	"<tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />" + 
	"<tool toolname=\"OIS File Information\" toolversion=\"0.2\" executionTime=\"135\" />" + 
	"<tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />" + 
	"<tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"423\" />" + 
	"<tool toolname=\"Tika\" toolversion=\"1.10\" status=\"did not run\" />" + 
	"</statistics>" + 
	"</fits>";	
		

		String videoOutput_combined_no_space = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
		"<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.0.1\" timestamp=\"7/15/16 1:46 PM\">" + 
		"<identification>" + 
		"<identity format=\"Quicktime\" mimetype=\"video/quicktime\" toolname=\"FITS\" toolversion=\"1.0.1\">" + 
		"<tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" />" + 
		"<tool toolname=\"Droid\" toolversion=\"6.1.5\" />" + 
		"<tool toolname=\"file utility\" toolversion=\"5.04\" />" + 
		"<externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">x-fmt/384</externalIdentifier>" + 
		"</identity>" + 
		"</identification>" + 
		"<fileinfo>" + 
		"<creatingApplicationName toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Apple QuickTime</creatingApplicationName>" + 
		"<filepath toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">/Users/dab980/downloads/400192692.mov</filepath>" + 
		"<filename toolname=\"MediaInfo\" toolversion=\"0.7.75\">400192692.mov</filename>" + 
		"<size toolname=\"MediaInfo\" toolversion=\"0.7.75\">161654</size>" + 
		"<md5checksum toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">f398a3311f7676282cfa7da33322d99b</md5checksum>" + 
		"<fslastmodified toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">1468604566000</fslastmodified>" + 
		"</fileinfo>" + 
		"<filestatus />" + 
		"<metadata>" + 
		"<video>" + 
		"<location toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">/Users/dab980/Downloads/400192692.mov</location>" + 
		"<mimeType toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">video/quicktime</mimeType>" + 
		"<format toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Quicktime</format>" + 
		"<formatProfile toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">QuickTime</formatProfile>" + 
		"<duration toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">1567</duration>" + 
		"<bitRate toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">825292</bitRate>" + 
		"<dateCreated toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2014-10-04 15:40:46</dateCreated>" + 
		"<dateModified toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2016-07-15 17:42:46</dateModified>" + 
		"<track type=\"video\" id=\"1\" toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">" + 
		"<videoDataEncoding>avc1</videoDataEncoding>" + 
		"<codecId>avc1</codecId>" + 
		"<codecCC>avc1</codecCC>" + 
		"<codecVersion>Baseline@L3</codecVersion>" + 
		"<codecName>AVC</codecName>" + 
		"<codecFamily>H.264</codecFamily>" + 
		"<codecInfo>Advanced Video Codec</codecInfo>" + 
		"<compression>Unknown</compression>" + 
		"<byteOrder>Unknown</byteOrder>" + 
		"<bitDepth>8 bits</bitDepth>" + 
		"<bitRate>816894</bitRate>" + 
		"<duration>1567</duration>" + 
		"<trackSize>159975</trackSize>" + 
		"<width>568 pixels</width>" + 
		"<height>320 pixels</height>" + 
		"<frameRate>30.000</frameRate>" + 
		"<frameRateMode>Constant</frameRateMode>" + 
		"<frameCount>47</frameCount>" + 
		"<aspectRatio>16:9</aspectRatio>" + 
		"<scanningFormat>Progressive</scanningFormat>" + 
		"<chromaSubsampling>4:2:0</chromaSubsampling>" + 
		"<colorspace>YUV</colorspace>" + 
		"</track>" + 
		"<standard>" + 
		"<ebucore:ebuCoreMain xmlns:ebucore=\"urn:ebu:metadata-schema:ebuCore_2014\">" + 
		"<ebucore:coreMetadata>" + 
		"<ebucore:format>" + 
		"<ebucore:videoFormat>" + 
		"<ebucore:width unit=\"pixel\">568</ebucore:width>" + 
		"<ebucore:height unit=\"pixel\">320</ebucore:height>" + 
		"<ebucore:frameRate factorDenominator=\"1001\" factorNumerator=\"1000\">30</ebucore:frameRate>" + 
		"<ebucore:aspectRatio typeLabel=\"display\">" + 
		"<ebucore:factorNumerator>16</ebucore:factorNumerator>" + 
		"<ebucore:factorDenominator>9</ebucore:factorDenominator>" + 
		"</ebucore:aspectRatio>" + 
		"<ebucore:videoEncoding typeLabel=\"avc1\" />" + 
		"<ebucore:codec>" + 
		"<ebucore:codecIdentifier>" + 
		"<dc:identifier xmlns:dc=\"http://purl.org/dc/elements/1.1/\">avc1</dc:identifier>" + 
		"</ebucore:codecIdentifier>" + 
		"<ebucore:name>AVC</ebucore:name>" + 
		"<ebucore:version>Baseline@L3</ebucore:version>" + 
		"<ebucore:family>H.264</ebucore:family>" + 
		"</ebucore:codec>" + 
		"<ebucore:bitRate>816894</ebucore:bitRate>" + 
		"<ebucore:scanningFormat>progressive</ebucore:scanningFormat>" + 
		"<ebucore:videoTrack trackId=\"1\" />" + 
		"<ebucore:technicalAttributeString typeLabel=\"chromaSubsampling\">4:2:0</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeString typeLabel=\"colorspace\">YUV</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeString typeLabel=\"frameRateMode\">constant</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeString typeLabel=\"byteOrder\">unknown</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeString typeLabel=\"compression\">unknown</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeLong typeLabel=\"streamSize\">159975</ebucore:technicalAttributeLong>" + 
		"<ebucore:technicalAttributeLong typeLabel=\"frameCount\">47</ebucore:technicalAttributeLong>" + 
		"<ebucore:technicalAttributeLong typeLabel=\"bitDepth\">8</ebucore:technicalAttributeLong>" + 
		"<ebucore:technicalAttributeLong typeLabel=\"duration\">1567</ebucore:technicalAttributeLong>" + 
		"</ebucore:videoFormat>" + 
		"<ebucore:audioFormatExtended />" + 
		"<ebucore:containerFormat>" + 
		"<ebucore:comment typeLabel=\"format\">Quicktime</ebucore:comment>" + 
		"<ebucore:comment typeLabel=\"formatProfile\">QuickTime</ebucore:comment>" + 
		"</ebucore:containerFormat>" + 
		"<ebucore:duration>" + 
		"<ebucore:editUnitNumber factorDenominator=\"1\" factorNumerator=\"1\" editRate=\"1000\">1567</ebucore:editUnitNumber>" + 
		"</ebucore:duration>" + 
		"<ebucore:mimeType typeLabel=\"video/quicktime\" />" + 
		"<ebucore:locator>/Users/dab980/Downloads/400192692.mov</ebucore:locator>" + 
		"<ebucore:technicalAttributeString typeLabel=\"overallBitRate\">825292</ebucore:technicalAttributeString>" + 
		"<ebucore:dateModified startTime=\"17:42:46Z\" startDate=\"2016-07-15\" />" + 
		"</ebucore:format>" + 
		"</ebucore:coreMetadata>" + 
		"</ebucore:ebuCoreMain>" + 
		"</standard>" + 
		"</video>" + 
		"</metadata>" + 
		"<statistics fitsExecutionTime=\"529\">" + 
		"<tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" executionTime=\"493\" />" + 
		"<tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"did not run\" />" + 
		"<tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />" + 
		"<tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />" + 
		"<tool toolname=\"Droid\" toolversion=\"6.1.5\" executionTime=\"180\" />" + 
		"<tool toolname=\"Jhove\" toolversion=\"1.11\" status=\"did not run\" />" + 
		"<tool toolname=\"file utility\" toolversion=\"5.04\" executionTime=\"493\" />" + 
		"<tool toolname=\"Exiftool\" toolversion=\"10.00\" status=\"did not run\" />" + 
		"<tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />" + 
		"<tool toolname=\"OIS File Information\" toolversion=\"0.2\" executionTime=\"135\" />" + 
		"<tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />" + 
		"<tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"423\" />" + 
		"<tool toolname=\"Tika\" toolversion=\"1.10\" status=\"did not run\" />" + 
		"</statistics>" + 
		"</fits>";
		
		
		
		String videoOutput_Standard_no_spaces =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
		"<ebucore:ebuCoreMain xmlns:ebucore=\"urn:ebu:metadata-schema:ebuCore_2014\">" + 
		"<ebucore:coreMetadata>" + 
		"<ebucore:format>" + 
		"<ebucore:videoFormat>" + 
		"<ebucore:width unit=\"pixel\">568</ebucore:width>" + 
		"<ebucore:height unit=\"pixel\">320</ebucore:height>" + 
		"<ebucore:frameRate factorDenominator=\"1001\" factorNumerator=\"1000\">30</ebucore:frameRate>" + 
		"<ebucore:aspectRatio typeLabel=\"display\">" + 
		"<ebucore:factorNumerator>16</ebucore:factorNumerator>" + 
		"<ebucore:factorDenominator>9</ebucore:factorDenominator>" + 
		"</ebucore:aspectRatio>" + 
		"<ebucore:videoEncoding typeLabel=\"avc1\" />" + 
		"<ebucore:codec>" + 
		"<ebucore:codecIdentifier>" + 
		"<dc:identifier xmlns:dc=\"http://purl.org/dc/elements/1.1/\">avc1</dc:identifier>" + 
		"</ebucore:codecIdentifier>" + 
		"<ebucore:name>AVC</ebucore:name>" + 
		"<ebucore:version>Baseline@L3</ebucore:version>" + 
		"<ebucore:family>H.264</ebucore:family>" + 
		"</ebucore:codec>" + 
		"<ebucore:bitRate>816894</ebucore:bitRate>" + 
		"<ebucore:scanningFormat>progressive</ebucore:scanningFormat>" + 
		"<ebucore:videoTrack trackId=\"1\" />" + 
		"<ebucore:technicalAttributeString typeLabel=\"chromaSubsampling\">4:2:0</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeString typeLabel=\"colorspace\">YUV</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeString typeLabel=\"frameRateMode\">constant</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeString typeLabel=\"byteOrder\">unknown</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeString typeLabel=\"compression\">unknown</ebucore:technicalAttributeString>" + 
		"<ebucore:technicalAttributeLong typeLabel=\"streamSize\">159975</ebucore:technicalAttributeLong>" + 
		"<ebucore:technicalAttributeLong typeLabel=\"frameCount\">47</ebucore:technicalAttributeLong>" + 
		"<ebucore:technicalAttributeLong typeLabel=\"bitDepth\">8</ebucore:technicalAttributeLong>" + 
		"<ebucore:technicalAttributeLong typeLabel=\"duration\">1567</ebucore:technicalAttributeLong>" + 
		"</ebucore:videoFormat>" + 
		"<ebucore:audioFormatExtended />" + 
		"<ebucore:containerFormat>" + 
		"<ebucore:comment typeLabel=\"format\">Quicktime</ebucore:comment>" + 
		"<ebucore:comment typeLabel=\"formatProfile\">QuickTime</ebucore:comment>" + 
		"</ebucore:containerFormat>" + 
		"<ebucore:duration>" + 
		"<ebucore:editUnitNumber factorDenominator=\"1\" factorNumerator=\"1\" editRate=\"1000\">1567</ebucore:editUnitNumber>" + 
		"</ebucore:duration>" + 
		"<ebucore:mimeType typeLabel=\"video/quicktime\" />" + 
		"<ebucore:locator>/Users/dab980/Downloads/400192692.mov</ebucore:locator>" + 
		"<ebucore:technicalAttributeString typeLabel=\"overallBitRate\">825292</ebucore:technicalAttributeString>" + 
		"<ebucore:dateModified startTime=\"17:42:46Z\" startDate=\"2016-07-15\" />" + 
		"</ebucore:format>" + 
		"</ebucore:coreMetadata>" + 
		"</ebucore:ebuCoreMain>";
		
		String videoOutputFits_spaces = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.0.1\" timestamp=\"7/15/16 1:46 PM\">" + 
				"  <identification>" + 
				"    <identity format=\"Quicktime\" mimetype=\"video/quicktime\" toolname=\"FITS\" toolversion=\"1.0.1\">" + 
				"      <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" />" + 
				"      <tool toolname=\"Droid\" toolversion=\"6.1.5\" />" + 
				"      <tool toolname=\"file utility\" toolversion=\"5.04\" />" + 
				"      <externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">x-fmt/384</externalIdentifier>" + 
				"    </identity>" + 
				"  </identification>" + 
				"  <fileinfo>" + 
				"    <creatingApplicationName toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Apple QuickTime</creatingApplicationName>" + 
				"    <filepath toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">/Users/dab980/downloads/400192692.mov</filepath>" + 
				"    <filename toolname=\"MediaInfo\" toolversion=\"0.7.75\">400192692.mov</filename>" + 
				"    <size toolname=\"MediaInfo\" toolversion=\"0.7.75\">161654</size>" + 
				"    <md5checksum toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">f398a3311f7676282cfa7da33322d99b</md5checksum>" + 
				"    <fslastmodified toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">1468604566000</fslastmodified>" + 
				"  </fileinfo>" + 
				"  <filestatus />" + 
				"  <metadata>" + 
				"    <video>" + 
				"      <location toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">/Users/dab980/Downloads/400192692.mov</location>" + 
				"      <mimeType toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">video/quicktime</mimeType>" + 
				"      <format toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Quicktime</format>" + 
				"      <formatProfile toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">QuickTime</formatProfile>" + 
				"      <duration toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">1567</duration>" + 
				"      <bitRate toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">825292</bitRate>" + 
				"      <dateCreated toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2014-10-04 15:40:46</dateCreated>" + 
				"      <dateModified toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2016-07-15 17:42:46</dateModified>" + 
				"      <track type=\"video\" id=\"1\" toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">" + 
				"        <videoDataEncoding>avc1</videoDataEncoding>" + 
				"        <codecId>avc1</codecId>" + 
				"        <codecCC>avc1</codecCC>" + 
				"        <codecVersion>Baseline@L3</codecVersion>" + 
				"        <codecName>AVC</codecName>" + 
				"        <codecFamily>H.264</codecFamily>" + 
				"        <codecInfo>Advanced Video Codec</codecInfo>" + 
				"        <compression>Unknown</compression>" + 
				"        <byteOrder>Unknown</byteOrder>" + 
				"        <bitDepth>8 bits</bitDepth>" + 
				"        <bitRate>816894</bitRate>" + 
				"        <duration>1567</duration>" + 
				"        <trackSize>159975</trackSize>" + 
				"        <width>568 pixels</width>" + 
				"        <height>320 pixels</height>" + 
				"        <frameRate>30.000</frameRate>" + 
				"        <frameRateMode>Constant</frameRateMode>" + 
				"        <frameCount>47</frameCount>" + 
				"        <aspectRatio>16:9</aspectRatio>" + 
				"        <scanningFormat>Progressive</scanningFormat>" + 
				"        <chromaSubsampling>4:2:0</chromaSubsampling>" + 
				"        <colorspace>YUV</colorspace>" + 
				"      </track>" + 
				"    </video>" + 
				"  </metadata>" + 
				"  <statistics fitsExecutionTime=\"529\">" + 
				"    <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" executionTime=\"493\" />" + 
				"    <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"did not run\" />" + 
				"    <tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />" + 
				"    <tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />" + 
				"    <tool toolname=\"Droid\" toolversion=\"6.1.5\" executionTime=\"180\" />" + 
				"    <tool toolname=\"Jhove\" toolversion=\"1.11\" status=\"did not run\" />" + 
				"    <tool toolname=\"file utility\" toolversion=\"5.04\" executionTime=\"493\" />" + 
				"    <tool toolname=\"Exiftool\" toolversion=\"10.00\" status=\"did not run\" />" + 
				"    <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />" + 
				"    <tool toolname=\"OIS File Information\" toolversion=\"0.2\" executionTime=\"135\" />" + 
				"    <tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />" + 
				"    <tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"423\" />" + 
				"    <tool toolname=\"Tika\" toolversion=\"1.10\" status=\"did not run\" />" + 
				"  </statistics>" + 
				"</fits>";		
		
		
		
		String videoOutputCombine_spaces = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd\" version=\"1.0.1\" timestamp=\"7/15/16 1:46 PM\">" + 
				"  <identification>" + 
				"    <identity format=\"Quicktime\" mimetype=\"video/quicktime\" toolname=\"FITS\" toolversion=\"1.0.1\">" + 
				"      <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" />" + 
				"      <tool toolname=\"Droid\" toolversion=\"6.1.5\" />" + 
				"      <tool toolname=\"file utility\" toolversion=\"5.04\" />" + 
				"      <externalIdentifier toolname=\"Droid\" toolversion=\"6.1.5\" type=\"puid\">x-fmt/384</externalIdentifier>" + 
				"    </identity>" + 
				"  </identification>" + 
				"  <fileinfo>" + 
				"    <creatingApplicationName toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Apple QuickTime</creatingApplicationName>" + 
				"    <filepath toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">/Users/dab980/downloads/400192692.mov</filepath>" + 
				"    <filename toolname=\"MediaInfo\" toolversion=\"0.7.75\">400192692.mov</filename>" + 
				"    <size toolname=\"MediaInfo\" toolversion=\"0.7.75\">161654</size>" + 
				"    <md5checksum toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">f398a3311f7676282cfa7da33322d99b</md5checksum>" + 
				"    <fslastmodified toolname=\"OIS File Information\" toolversion=\"0.2\" status=\"SINGLE_RESULT\">1468604566000</fslastmodified>" + 
				"  </fileinfo>" + 
				"  <filestatus />" + 
				"  <metadata>" + 
				"    <video>" + 
				"      <location toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">/Users/dab980/Downloads/400192692.mov</location>" + 
				"      <mimeType toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">video/quicktime</mimeType>" + 
				"      <format toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">Quicktime</format>" + 
				"      <formatProfile toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">QuickTime</formatProfile>" + 
				"      <duration toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">1567</duration>" + 
				"      <bitRate toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">825292</bitRate>" + 
				"      <dateCreated toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2014-10-04 15:40:46</dateCreated>" + 
				"      <dateModified toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">UTC 2016-07-15 17:42:46</dateModified>" + 
				"      <track type=\"video\" id=\"1\" toolname=\"MediaInfo\" toolversion=\"0.7.75\" status=\"SINGLE_RESULT\">" + 
				"        <videoDataEncoding>avc1</videoDataEncoding>" + 
				"        <codecId>avc1</codecId>" + 
				"        <codecCC>avc1</codecCC>" + 
				"        <codecVersion>Baseline@L3</codecVersion>" + 
				"        <codecName>AVC</codecName>" + 
				"        <codecFamily>H.264</codecFamily>" + 
				"        <codecInfo>Advanced Video Codec</codecInfo>" + 
				"        <compression>Unknown</compression>" + 
				"        <byteOrder>Unknown</byteOrder>" + 
				"        <bitDepth>8 bits</bitDepth>" + 
				"        <bitRate>816894</bitRate>" + 
				"        <duration>1567</duration>" + 
				"        <trackSize>159975</trackSize>" + 
				"        <width>568 pixels</width>" + 
				"        <height>320 pixels</height>" + 
				"        <frameRate>30.000</frameRate>" + 
				"        <frameRateMode>Constant</frameRateMode>" + 
				"        <frameCount>47</frameCount>" + 
				"        <aspectRatio>16:9</aspectRatio>" + 
				"        <scanningFormat>Progressive</scanningFormat>" + 
				"        <chromaSubsampling>4:2:0</chromaSubsampling>" + 
				"        <colorspace>YUV</colorspace>" + 
				"      </track>" + 
				"      <standard>" + 
				"        <ebucore:ebuCoreMain xmlns:ebucore=\"urn:ebu:metadata-schema:ebuCore_2014\">" + 
				"          <ebucore:coreMetadata>" + 
				"            <ebucore:format>" + 
				"              <ebucore:videoFormat>" + 
				"                <ebucore:width unit=\"pixel\">568</ebucore:width>" + 
				"                <ebucore:height unit=\"pixel\">320</ebucore:height>" + 
				"                <ebucore:frameRate factorDenominator=\"1001\" factorNumerator=\"1000\">30</ebucore:frameRate>" + 
				"                <ebucore:aspectRatio typeLabel=\"display\">" + 
				"                  <ebucore:factorNumerator>16</ebucore:factorNumerator>" + 
				"                  <ebucore:factorDenominator>9</ebucore:factorDenominator>" + 
				"                </ebucore:aspectRatio>" + 
				"                <ebucore:videoEncoding typeLabel=\"avc1\" />" + 
				"                <ebucore:codec>" + 
				"                  <ebucore:codecIdentifier>" + 
				"                    <dc:identifier xmlns:dc=\"http://purl.org/dc/elements/1.1/\">avc1</dc:identifier>" + 
				"                  </ebucore:codecIdentifier>" + 
				"                  <ebucore:name>AVC</ebucore:name>" + 
				"                  <ebucore:version>Baseline@L3</ebucore:version>" + 
				"                  <ebucore:family>H.264</ebucore:family>" + 
				"                </ebucore:codec>" + 
				"                <ebucore:bitRate>816894</ebucore:bitRate>" + 
				"                <ebucore:scanningFormat>progressive</ebucore:scanningFormat>" + 
				"                <ebucore:videoTrack trackId=\"1\" />" + 
				"                <ebucore:technicalAttributeString typeLabel=\"chromaSubsampling\">4:2:0</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeString typeLabel=\"colorspace\">YUV</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeString typeLabel=\"frameRateMode\">constant</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeString typeLabel=\"byteOrder\">unknown</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeString typeLabel=\"compression\">unknown</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeLong typeLabel=\"streamSize\">159975</ebucore:technicalAttributeLong>" + 
				"                <ebucore:technicalAttributeLong typeLabel=\"frameCount\">47</ebucore:technicalAttributeLong>" + 
				"                <ebucore:technicalAttributeLong typeLabel=\"bitDepth\">8</ebucore:technicalAttributeLong>" + 
				"                <ebucore:technicalAttributeLong typeLabel=\"duration\">1567</ebucore:technicalAttributeLong>" + 
				"              </ebucore:videoFormat>" + 
				"              <ebucore:audioFormatExtended />" + 
				"              <ebucore:containerFormat>" + 
				"                <ebucore:comment typeLabel=\"format\">Quicktime</ebucore:comment>" + 
				"                <ebucore:comment typeLabel=\"formatProfile\">QuickTime</ebucore:comment>" + 
				"              </ebucore:containerFormat>" + 
				"              <ebucore:duration>" + 
				"                <ebucore:editUnitNumber factorDenominator=\"1\" factorNumerator=\"1\" editRate=\"1000\">1567</ebucore:editUnitNumber>" + 
				"              </ebucore:duration>" + 
				"              <ebucore:mimeType typeLabel=\"video/quicktime\" />" + 
				"              <ebucore:locator>/Users/dab980/Downloads/400192692.mov</ebucore:locator>" + 
				"              <ebucore:technicalAttributeString typeLabel=\"overallBitRate\">825292</ebucore:technicalAttributeString>" + 
				"              <ebucore:dateModified startTime=\"17:42:46Z\" startDate=\"2016-07-15\" />" + 
				"            </ebucore:format>" + 
				"          </ebucore:coreMetadata>" + 
				"        </ebucore:ebuCoreMain>" + 
				"      </standard>" + 
				"    </video>" + 
				"  </metadata>" + 
				"  <statistics fitsExecutionTime=\"529\">" + 
				"    <tool toolname=\"MediaInfo\" toolversion=\"0.7.75\" executionTime=\"493\" />" + 
				"    <tool toolname=\"OIS Audio Information\" toolversion=\"0.1\" status=\"did not run\" />" + 
				"    <tool toolname=\"ADL Tool\" toolversion=\"0.1\" status=\"did not run\" />" + 
				"    <tool toolname=\"VTT Tool\" toolversion=\"0.1\" status=\"did not run\" />" + 
				"    <tool toolname=\"Droid\" toolversion=\"6.1.5\" executionTime=\"180\" />" + 
				"    <tool toolname=\"Jhove\" toolversion=\"1.11\" status=\"did not run\" />" + 
				"    <tool toolname=\"file utility\" toolversion=\"5.04\" executionTime=\"493\" />" + 
				"    <tool toolname=\"Exiftool\" toolversion=\"10.00\" status=\"did not run\" />" + 
				"    <tool toolname=\"NLNZ Metadata Extractor\" toolversion=\"3.6GA\" status=\"did not run\" />" + 
				"    <tool toolname=\"OIS File Information\" toolversion=\"0.2\" executionTime=\"135\" />" + 
				"    <tool toolname=\"OIS XML Metadata\" toolversion=\"0.2\" status=\"did not run\" />" + 
				"    <tool toolname=\"ffident\" toolversion=\"0.2\" executionTime=\"423\" />" + 
				"    <tool toolname=\"Tika\" toolversion=\"1.10\" status=\"did not run\" />" + 
				"  </statistics>" + 
				"</fits>";
		
		String videoOutputStandard_spaces = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"        <ebucore:ebuCoreMain xmlns:ebucore=\"urn:ebu:metadata-schema:ebuCore_2014\">" + 
				"          <ebucore:coreMetadata>" + 
				"            <ebucore:format>" + 
				"              <ebucore:videoFormat>" + 
				"                <ebucore:width unit=\"pixel\">568</ebucore:width>" + 
				"                <ebucore:height unit=\"pixel\">320</ebucore:height>" + 
				"                <ebucore:frameRate factorDenominator=\"1001\" factorNumerator=\"1000\">30</ebucore:frameRate>" + 
				"                <ebucore:aspectRatio typeLabel=\"display\">" + 
				"                  <ebucore:factorNumerator>16</ebucore:factorNumerator>" + 
				"                  <ebucore:factorDenominator>9</ebucore:factorDenominator>" + 
				"                </ebucore:aspectRatio>" + 
				"                <ebucore:videoEncoding typeLabel=\"avc1\" />" + 
				"                <ebucore:codec>" + 
				"                  <ebucore:codecIdentifier>" + 
				"                    <dc:identifier xmlns:dc=\"http://purl.org/dc/elements/1.1/\">avc1</dc:identifier>" + 
				"                  </ebucore:codecIdentifier>" + 
				"                  <ebucore:name>AVC</ebucore:name>" + 
				"                  <ebucore:version>Baseline@L3</ebucore:version>" + 
				"                  <ebucore:family>H.264</ebucore:family>" + 
				"                </ebucore:codec>" + 
				"                <ebucore:bitRate>816894</ebucore:bitRate>" + 
				"                <ebucore:scanningFormat>progressive</ebucore:scanningFormat>" + 
				"                <ebucore:videoTrack trackId=\"1\" />" + 
				"                <ebucore:technicalAttributeString typeLabel=\"chromaSubsampling\">4:2:0</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeString typeLabel=\"colorspace\">YUV</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeString typeLabel=\"frameRateMode\">constant</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeString typeLabel=\"byteOrder\">unknown</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeString typeLabel=\"compression\">unknown</ebucore:technicalAttributeString>" + 
				"                <ebucore:technicalAttributeLong typeLabel=\"streamSize\">159975</ebucore:technicalAttributeLong>" + 
				"                <ebucore:technicalAttributeLong typeLabel=\"frameCount\">47</ebucore:technicalAttributeLong>" + 
				"                <ebucore:technicalAttributeLong typeLabel=\"bitDepth\">8</ebucore:technicalAttributeLong>" + 
				"                <ebucore:technicalAttributeLong typeLabel=\"duration\">1567</ebucore:technicalAttributeLong>" + 
				"              </ebucore:videoFormat>" + 
				"              <ebucore:audioFormatExtended />" + 
				"              <ebucore:containerFormat>" + 
				"                <ebucore:comment typeLabel=\"format\">Quicktime</ebucore:comment>" + 
				"                <ebucore:comment typeLabel=\"formatProfile\">QuickTime</ebucore:comment>" + 
				"              </ebucore:containerFormat>" + 
				"              <ebucore:duration>" + 
				"                <ebucore:editUnitNumber factorDenominator=\"1\" factorNumerator=\"1\" editRate=\"1000\">1567</ebucore:editUnitNumber>" + 
				"              </ebucore:duration>" + 
				"              <ebucore:mimeType typeLabel=\"video/quicktime\" />" + 
				"              <ebucore:locator>/Users/dab980/Downloads/400192692.mov</ebucore:locator>" + 
				"              <ebucore:technicalAttributeString typeLabel=\"overallBitRate\">825292</ebucore:technicalAttributeString>" + 
				"              <ebucore:dateModified startTime=\"17:42:46Z\" startDate=\"2016-07-15\" />" + 
				"            </ebucore:format>" + 
				"          </ebucore:coreMetadata>" + 
				"        </ebucore:ebuCoreMain>";

		
		
		@Test
		public void testWavCombinedOutput() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(wavOutput_combined_spaces);			
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			assertTrue("XML Content should NOT be null", xmlcontent != null);			
		}
		
		@Test
		public void testWavStandardOutput() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(wavOutput_standard_spaces);			
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			assertTrue("XML Content should be null", xmlcontent == null);			
		}		
		
		@Test
		public void testWavFitsOutput() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(wavOutput_fits_spaces);			
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			assertTrue("XML Content should NOT be null", xmlcontent != null);			
		}

		@Test
		public void testVideoFitsNoSpaces() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(videoOutput_fits_no_space);						
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			assertTrue("XML Content should NOT be null", xmlcontent != null);			
		}		
		
		@Test
		public void testVideoCombinedNoSpaces() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(videoOutput_combined_no_space);						
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			assertTrue("XML Content should NOT be null", xmlcontent != null);			
		}

		@Test
		public void testVideoStandardNoSpaces() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(videoOutput_Standard_no_spaces);						
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			assertTrue("XML Content should be null", xmlcontent == null);			
		}
		
		@Test
		public void testVideoFitsWithSpaces() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(videoOutputFits_spaces);						
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			assertTrue("XML Content should NOT be null", xmlcontent != null);			
		}		

		@Test
		public void testVideoConbinedWithSpaces() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(videoOutputCombine_spaces);						
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			assertTrue("XML Content should NOT be null", xmlcontent != null);			
		}
		
		@Test
		public void testVideoStandardWithSpaces() throws Exception {
			FitsOutput fitsOutput = new FitsOutput(videoOutputStandard_spaces);						
			XmlContent xmlcontent = fitsOutput.getStandardXmlContent();
			
			assertTrue("XML Content should be null", xmlcontent == null);
		}

}

<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exiftool_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output"
    	  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://hul.harvard.edu/ois/xml/ns/fits/fits_output xml/fits_output.xsd">
		<xsl:apply-imports/>
		
		<metadata>
			<video>
				<digitalCameraManufacturer>
				    <xsl:value-of select="exiftool/Make"/>
				</digitalCameraManufacturer>
				<digitalCameraModelName>
				    <xsl:value-of select="exiftool/Model"/>
				</digitalCameraModelName>

				<duration>
					<xsl:value-of select="exiftool/Duration"/>
				</duration>
				
				<xsl:choose>
					<xsl:when test="exiftool/AvgBitRate">
						<bitRate>
							<xsl:value-of select="exiftool/AvgBitRate"/>
						</bitRate>
					</xsl:when>
<!-- 					<xsl:when test="exiftool/VideoBitrate and exiftool/AudioBitrate">
						<bitRate>
							<xsl:value-of select="exiftool/VideoBitrate + exiftool/AudioBitrate"/>
						</bitRate>
					</xsl:when>	 -->			
					<xsl:when test="exiftool/VideoBitrate">
						<bitRate>
							<xsl:value-of select="exiftool/VideoBitrate"/>
						</bitRate>
					</xsl:when>				
					<xsl:when test="exiftool/AudioBitrate">
						<bitRate>
							<xsl:value-of select="exiftool/AudioBitrate"/>
						</bitRate>
					</xsl:when>
				</xsl:choose>
				
				<xsl:choose>
					<xsl:when test="exiftool/PixelsPerMeterX and exiftool/PixelsPerMeterY">
						<pixelAspectRatio>
							<xsl:value-of select="exiftool/PixelsPerMeterX"/><xsl:value-of select="'/'"/><xsl:value-of select="exiftool/PixelsPerMeterY"/>
						</pixelAspectRatio>
					</xsl:when>
				</xsl:choose>
				
				<maxBitRate>
					<xsl:value-of select="exiftool/MaxDataRate"/>
				</maxBitRate>
				
				<videoCompressor>
					<xsl:value-of select="exiftool/VideoCodec"/>
				</videoCompressor>
				
				<audioCompressor>
					<xsl:value-of select="exiftool/AudioCodec"/>
				</audioCompressor>
				
				<xsl:choose>
					<xsl:when test="exiftool/VideoFrameRate">
						<frameRate>
							<xsl:value-of select="exiftool/VideoFrameRate"/>
						</frameRate>
					</xsl:when>
					<xsl:when test="exiftool/FrameRate">
						<frameRate>
							<xsl:value-of select="exiftool/FrameRate"/>
						</frameRate>
					</xsl:when>
				</xsl:choose>
				
				<xsl:choose>
					<xsl:when test="exiftool/BitDepth">
						<bitDepth>
							<xsl:value-of select="exiftool/BitDepth"/>
						</bitDepth>
					</xsl:when>
					<xsl:when test="exiftool/BitsPerSample">
						<bitDepth>
							<xsl:value-of select="exiftool/BitsPerSample"/>
						</bitDepth>
					</xsl:when>
					<xsl:when test="exiftool/SampleSize">
						<bitDepth>
							<xsl:value-of select="exiftool/SampleSize"/>
						</bitDepth>
					</xsl:when>
				</xsl:choose>
					
				<xsl:choose>
					<xsl:when test="exiftool/AudioSampleBits">
						<audioBitsPerSample>
							<xsl:value-of select="exiftool/AudioSampleBits"/>
						</audioBitsPerSample>
					</xsl:when>
				</xsl:choose>
				
				<xsl:choose>
					<xsl:when test="exiftool/SampleRate">
						<sampleRate>
							<xsl:value-of select="exiftool/SampleRate"/>
						</sampleRate>
					</xsl:when>
				</xsl:choose>
				
				<xsl:choose>
					<xsl:when test="exiftool/AudioSampleRate">
						<audioSampleRate>
							<xsl:value-of select="exiftool/AudioSampleRate"/>
						</audioSampleRate>
					</xsl:when>
				</xsl:choose>
	
				<xsl:choose>
					<!-- WAV/FLAC -->
					<xsl:when test="exiftool/NumChannels">
						<channels>
							<xsl:value-of select="exiftool/NumChannels"/>
						</channels>
					</xsl:when>
					<!-- OGG -->
					<xsl:when test="exiftool/AudioChannels">
						<channels>
							<xsl:value-of select="exiftool/AudioChannels"/>
						</channels>
					</xsl:when>
					<!-- MP3 -->
					<xsl:when test="exiftool/ChannelMode">
						<channels>
							<xsl:value-of select="exiftool/ChannelMode"/>
						</channels>
					</xsl:when>
					<!-- FLAC -->
					<xsl:when test="exiftool/Channels">
						<channels>
							<xsl:value-of select="exiftool/Channels"/>
						</channels>
					</xsl:when>
				</xsl:choose>
				
				<imageWidth>
					<xsl:value-of select="exiftool/ImageWidth"/>
				</imageWidth>
				
				<imageHeight>
					<xsl:value-of select="exiftool/ImageHeight"/>
				</imageHeight>
				
				<rotation>
	                <xsl:value-of select="exiftool/Rotation"/>
                </rotation>
				
				<xsl:choose>
			  		<xsl:when test="string(exiftool/CaptureXResolution)">
		  				<xSamplingFrequency>
					  		<xsl:value-of select="exiftool/CaptureXResolution"/>
						</xSamplingFrequency>
			  		</xsl:when>
			  		<xsl:when test="string(exiftool/XResolution)">
		  				<xSamplingFrequency>
					  		<xsl:value-of select="exiftool/XResolution"/>
						</xSamplingFrequency>
			  		</xsl:when>
				</xsl:choose>
							
				<xsl:choose>
			  		<xsl:when test="string(exiftool/CaptureYResolution)">
		  				<ySamplingFrequency>
					  		<xsl:value-of select="exiftool/CaptureYResolution"/>
						</ySamplingFrequency>
			  		</xsl:when>
			  		<xsl:when test="string(exiftool/YResolution)">
		  				<ySamplingFrequency>
					  		<xsl:value-of select="exiftool/YResolution"/>
						</ySamplingFrequency>
			  		</xsl:when>
				</xsl:choose>	
				
				<xsl:choose>
					<xsl:when test="exiftool/Encoding">
						<encoding>	
							<xsl:value-of select="exiftool/Encoding"/>
						</encoding>	
					</xsl:when>
					<xsl:when test="exiftool/VideoEncoding and exiftool/AudioEncoding">
						<encoding>	
							<xsl:value-of select="concat(exiftool/VideoEncoding, ' + ', exiftool/AudioEncoding)"/>
						</encoding>	
					</xsl:when>
					<xsl:when test="exiftool/VideoEncoding">
						<encoding>	
							<xsl:value-of select="exiftool/VideoEncoding"/>
						</encoding>	
					</xsl:when>
					<xsl:when test="exiftool/AudioEncoding">
						<audioDataEncoding>	
							<xsl:value-of select="exiftool/AudioEncoding"/>
						</audioDataEncoding>	
					</xsl:when>
				</xsl:choose>
				
				<blockSizeMin>
					<xsl:value-of select="exiftool/BlockSizeMin"/>
				</blockSizeMin>
					
				<blockSizeMax>
					<xsl:value-of select="exiftool/BlockSizeMax"/>
				</blockSizeMax>
					
				<xsl:choose>
					<xsl:when test="exiftool/CompressorName">
						<creatingApplicationName>
							<xsl:value-of select="exiftool/CompressorName"/>
						</creatingApplicationName>
					</xsl:when>
					<xsl:when test="exiftool/CompressorID">
						<creatingApplicationName>
							<xsl:value-of select="exiftool/CompressorID"/>
						</creatingApplicationName>
					</xsl:when>
				</xsl:choose>		

				<xsl:choose>
					<xsl:when test="exiftool/VideoStreamType">
						<videoStreamType>
							<xsl:value-of select="exiftool/VideoStreamType"/>
						</videoStreamType>
					</xsl:when>
				</xsl:choose>

				<compressionScheme>
					<xsl:value-of select="exiftool/Compression"/>
				</compressionScheme>
				
				<shutterSpeedValue>
					<xsl:value-of select="exiftool/ShutterSpeed"/>
				</shutterSpeedValue>

				<apertureSetting>
					<xsl:value-of select="exiftool/ApertureSetting"/>
				</apertureSetting>

				<fNumber>
					<xsl:value-of select="exiftool/FNumber"/>
				</fNumber>

				<gain>
					<xsl:value-of select="exiftool/Gain"/>
				</gain>

				<exposureTime>
					<xsl:value-of select="exiftool/ExposureTime"/>
				</exposureTime>

				<exposureProgram>
					<xsl:value-of select="exiftool/ExposureProgram"/>
				</exposureProgram>

				<whiteBalance>
					<xsl:value-of select="exiftool/WhiteBalance"/>
				</whiteBalance>

				<imageStabilization>
					<xsl:value-of select="exiftool/ImageStabilization"/>
				</imageStabilization>

				<focus>
					<xsl:value-of select="exiftool/Focus"/>
				</focus>

				<!-- GPS FIELDS -->
				<gpsVersionID>
					<xsl:value-of select="exiftool/GPSVersionID"/>
				</gpsVersionID>
				<xsl:choose>
					<xsl:when test="exiftool/GPSLatitudeRef">
						<gpsLatitudeRef>
							<xsl:value-of select="exiftool/GPSLatitudeRef"/>
						</gpsLatitudeRef>
					</xsl:when>
					<xsl:when test="exiftool/GPSLatitude">
						<gpsLatitudeRef>
							<xsl:value-of select="substring-after(exiftool/GPSLatitude,'&quot; ')"/>
						</gpsLatitudeRef>
					</xsl:when>
				</xsl:choose>
				<gpsLatitude>
					<xsl:value-of select="exiftool/GPSLatitude"/>
				</gpsLatitude>

				<xsl:choose>
					<xsl:when test="exiftool/GPSLongitudeRef">
						<gpsLongitudeRef>
							<xsl:value-of select="exiftool/GPSLongitudeRef"/>
						</gpsLongitudeRef>
					</xsl:when>
					<xsl:when test="exiftool/GPSLongitude">
						<gpsLongitudeRef>
							<xsl:value-of select="substring-after(exiftool/GPSLongitude,'&quot; ')"/>
						</gpsLongitudeRef>
					</xsl:when>
				</xsl:choose>
				<gpsLongitude>
					<xsl:value-of select="exiftool/GPSLongitude"/>
				</gpsLongitude>

				<gpsAltitudeRef>
					<xsl:value-of select="exiftool/GPSAltitudeRef"/>
				</gpsAltitudeRef>
				<gpsAltitude>
					<xsl:value-of select="exiftool/GPSAltitude"/>
				</gpsAltitude>
				<gpsTimeStamp>
					<xsl:value-of select="exiftool/GPSTimeStamp"/>
				</gpsTimeStamp>
				<gpsSatellites>
					<xsl:value-of select="exiftool/GPSSatellites"/>
				</gpsSatellites>
				<gpsStatus>
					<xsl:value-of select="exiftool/GPSStatus"/>
				</gpsStatus>
				<gpsMeasureMode>
					<xsl:value-of select="exiftool/GPSMeasureMode"/>
				</gpsMeasureMode>
				<gpsDOP>
					<xsl:value-of select="exiftool/GPSDOP"/>
				</gpsDOP>
				<gpsSpeedRef>
					<xsl:value-of select="exiftool/GPSSpeedRef"/>
				</gpsSpeedRef>
				<gpsSpeed>
					<xsl:value-of select="exiftool/GPSSpeed"/>
				</gpsSpeed>
				<gpsTrackRef>
					<xsl:value-of select="exiftool/GPSTrackRef"/>
				</gpsTrackRef>
				<gpsTrack>
					<xsl:value-of select="exiftool/GPSTrack"/>
				</gpsTrack>
				<gpsImgDirectionRef>
					<xsl:value-of select="exiftool/GPSImgDirectionRef"/>
				</gpsImgDirectionRef>
				<gpsImgDirection>
					<xsl:value-of select="exiftool/GPSImgDirection"/>
				</gpsImgDirection>
				<gpsMapDatum>
					<xsl:value-of select="exiftool/GPSMapDatum"/>
				</gpsMapDatum>
				<xsl:choose>
					<xsl:when test="exiftool/GPSDestLatitudeRef">
						<gpsDestLatitudeRef>
							<xsl:value-of select="exiftool/GPSDestLatitudeRef"/>
						</gpsDestLatitudeRef>
					</xsl:when>
					<xsl:when test="exiftool/GPSDestLatitude">
						<gpsDestLatitudeRef>
							<xsl:value-of select="substring-after(exiftool/GPSDestLatitude,'&quot; ')"/>
						</gpsDestLatitudeRef>
					</xsl:when>
				</xsl:choose>
				<gpsDestLatitude>
					<xsl:value-of select="exiftool/GPSDestLatitude"/>
				</gpsDestLatitude>

				<xsl:choose>
					<xsl:when test="exiftool/GPSDestLongitudeRef">
						<gpsDestLongitudeRef>
							<xsl:value-of select="exiftool/GPSDestLongitudeRef"/>
						</gpsDestLongitudeRef>
					</xsl:when>
					<xsl:when test="exiftool/GPSDestLongitude">
						<gpsDestLongitudeRef>
							<xsl:value-of select="substring-after(exiftool/GPSDestLongitude,'&quot; ')"/>
						</gpsDestLongitudeRef>
					</xsl:when>
				</xsl:choose>
				<gpsDestLongitude>
					<xsl:value-of select="exiftool/GPSDestLongitudeRef"/>
				</gpsDestLongitude>
				<gpsDestBearingRef>
					<xsl:value-of select="exiftool/GPSDestBearingRef"/>
				</gpsDestBearingRef>
				<gpsDestBearing>
					<xsl:value-of select="exiftool/GPSDestBearing"/>
				</gpsDestBearing>
				<gpsDestDistanceRef>
					<xsl:value-of select="exiftool/GPSDestDistanceRef"/>
				</gpsDestDistanceRef>
				<gpsDestDistance>
					<xsl:value-of select="exiftool/GPSDestDistance"/>
				</gpsDestDistance>
				<gpsProcessingMethod>
					<xsl:value-of select="exiftool/GPSProcessingMethod"/>
				</gpsProcessingMethod>
				<gpsAreaInformation>
					<xsl:value-of select="exiftool/GPSAreaInformation"/>
				</gpsAreaInformation>
				<gpsDateStamp>
					<xsl:value-of select="replace(exiftool/GPSDateStamp,':','-')"/>
				</gpsDateStamp>
				<gpsDifferential>
					<xsl:value-of select="exiftool/GPSDifferential"/>
				</gpsDifferential>
			</video>			
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>


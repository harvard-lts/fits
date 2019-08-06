<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
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
				    <xsl:value-of select="exiftool/Make[1]"/>
				</digitalCameraManufacturer>
				<digitalCameraModelName>
				    <xsl:value-of select="exiftool/Model[1]"/>
				</digitalCameraModelName>

				<duration>
					<xsl:value-of select="exiftool/Duration[1]"/>
				</duration>
				
				<xsl:choose>
					<xsl:when test="exiftool/AvgBitRate">
						<bitRate>
							<xsl:value-of select="exiftool/AvgBitRate[1]"/>
						</bitRate>
					</xsl:when>
<!-- 					<xsl:when test="exiftool/VideoBitrate and exiftool/AudioBitrate">
						<bitRate>
							<xsl:value-of select="exiftool/VideoBitrate + exiftool/AudioBitrate[1]"/>
						</bitRate>
					</xsl:when>	 -->			
					<xsl:when test="exiftool/VideoBitrate">
						<bitRate>
							<xsl:value-of select="exiftool/VideoBitrate[1]"/>
						</bitRate>
					</xsl:when>				
					<xsl:when test="exiftool/AudioBitrate">
						<bitRate>
							<xsl:value-of select="exiftool/AudioBitrate[1]"/>
						</bitRate>
					</xsl:when>
				</xsl:choose>
				
				<xsl:choose>
					<xsl:when test="exiftool/PixelsPerMeterX and exiftool/PixelsPerMeterY">
						<pixelAspectRatio>
							<xsl:value-of select="exiftool/PixelsPerMeterX[1]"/><xsl:value-of select="'/'"/><xsl:value-of select="exiftool/PixelsPerMeterY[1]"/>
						</pixelAspectRatio>
					</xsl:when>
				</xsl:choose>
				
				<maxBitRate>
					<xsl:value-of select="exiftool/MaxDataRate[1]"/>
				</maxBitRate>
				
				<videoCompressor>
					<xsl:value-of select="exiftool/VideoCodec[1]"/>
				</videoCompressor>
				
				<audioCompressor>
					<xsl:value-of select="exiftool/AudioCodec[1]"/>
				</audioCompressor>
				
				<xsl:choose>
					<xsl:when test="exiftool/VideoFrameRate">
						<frameRate>
							<xsl:value-of select="exiftool/VideoFrameRate[1]"/>
						</frameRate>
					</xsl:when>
					<xsl:when test="exiftool/FrameRate">
						<frameRate>
							<xsl:value-of select="exiftool/FrameRate[1]"/>
						</frameRate>
					</xsl:when>
				</xsl:choose>
				
				<xsl:choose>
					<xsl:when test="exiftool/BitDepth">
						<bitDepth>
							<xsl:value-of select="exiftool/BitDepth[1]"/>
						</bitDepth>
					</xsl:when>
					<xsl:when test="exiftool/BitsPerSample">
						<bitDepth>
							<xsl:value-of select="exiftool/BitsPerSample[1]"/>
						</bitDepth>
					</xsl:when>
					<xsl:when test="exiftool/SampleSize">
						<bitDepth>
							<xsl:value-of select="exiftool/SampleSize[1]"/>
						</bitDepth>
					</xsl:when>
				</xsl:choose>
					
				<xsl:choose>
					<xsl:when test="exiftool/AudioSampleBits">
						<audioBitsPerSample>
							<xsl:value-of select="exiftool/AudioSampleBits[1]"/>
						</audioBitsPerSample>
					</xsl:when>
				</xsl:choose>
				
				<xsl:choose>
					<xsl:when test="exiftool/SampleRate">
						<sampleRate>
							<xsl:value-of select="exiftool/SampleRate[1]"/>
						</sampleRate>
					</xsl:when>
				</xsl:choose>
				
				<xsl:choose>
					<xsl:when test="exiftool/AudioSampleRate">
						<audioSampleRate>
							<xsl:value-of select="exiftool/AudioSampleRate[1]"/>
						</audioSampleRate>
					</xsl:when>
				</xsl:choose>
	
				<xsl:choose>
					<!-- WAV/FLAC -->
					<xsl:when test="exiftool/NumChannels">
						<channels>
							<xsl:value-of select="exiftool/NumChannels[1]"/>
						</channels>
					</xsl:when>
					<!-- OGG -->
					<xsl:when test="exiftool/AudioChannels">
						<channels>
							<xsl:value-of select="exiftool/AudioChannels[1]"/>
						</channels>
					</xsl:when>
					<!-- MP3 -->
					<xsl:when test="exiftool/ChannelMode">
						<channels>
							<xsl:value-of select="exiftool/ChannelMode[1]"/>
						</channels>
					</xsl:when>
					<!-- FLAC -->
					<xsl:when test="exiftool/Channels">
						<channels>
							<xsl:value-of select="exiftool/Channels[1]"/>
						</channels>
					</xsl:when>
				</xsl:choose>
				
				<imageWidth>
					<xsl:value-of select="exiftool/ImageWidth[1]"/>
				</imageWidth>
				
				<imageHeight>
					<xsl:value-of select="exiftool/ImageHeight[1]"/>
				</imageHeight>
				
				<rotation>
	                <xsl:value-of select="exiftool/Rotation[1]"/>
                </rotation>
				
				<xsl:choose>
			  		<xsl:when test="string(exiftool/CaptureXResolution)">
		  				<xSamplingFrequency>
							<xsl:value-of select="exiftool/CaptureXResolution[1]"/>
						</xSamplingFrequency>
			  		</xsl:when>
			  		<xsl:when test="string(exiftool/XResolution)">
		  				<xSamplingFrequency>
							<xsl:value-of select="exiftool/XResolution[1]"/>
						</xSamplingFrequency>
			  		</xsl:when>
				</xsl:choose>
							
				<xsl:choose>
			  		<xsl:when test="string(exiftool/CaptureYResolution)">
		  				<ySamplingFrequency>
							<xsl:value-of select="exiftool/CaptureYResolution[1]"/>
						</ySamplingFrequency>
			  		</xsl:when>
			  		<xsl:when test="string(exiftool/YResolution)">
		  				<ySamplingFrequency>
							<xsl:value-of select="exiftool/YResolution[1]"/>
						</ySamplingFrequency>
			  		</xsl:when>
				</xsl:choose>	
				
				<xsl:choose>
					<xsl:when test="exiftool/Encoding">
						<encoding>	
							<xsl:value-of select="exiftool/Encoding[1]"/>
						</encoding>	
					</xsl:when>
					<xsl:when test="exiftool/VideoEncoding and exiftool/AudioEncoding">
						<encoding>	
							<xsl:value-of select="concat(exiftool/VideoEncoding[1], ' + ', exiftool/AudioEncoding[1])"/>
						</encoding>	
					</xsl:when>
					<xsl:when test="exiftool/VideoEncoding">
						<encoding>	
							<xsl:value-of select="exiftool/VideoEncoding[1]"/>
						</encoding>	
					</xsl:when>
					<xsl:when test="exiftool/AudioEncoding">
						<audioDataEncoding>	
							<xsl:value-of select="exiftool/AudioEncoding[1]"/>
						</audioDataEncoding>	
					</xsl:when>
				</xsl:choose>
				
				<blockSizeMin>
					<xsl:value-of select="exiftool/BlockSizeMin[1]"/>
				</blockSizeMin>
					
				<blockSizeMax>
					<xsl:value-of select="exiftool/BlockSizeMax[1]"/>
				</blockSizeMax>

				<xsl:choose>
					<xsl:when test="exiftool/VideoStreamType">
						<videoStreamType>
							<xsl:value-of select="exiftool/VideoStreamType[1]"/>
						</videoStreamType>
					</xsl:when>
				</xsl:choose>

				<compressionScheme>
					<xsl:value-of select="exiftool/Compression[1]"/>
				</compressionScheme>
				
				<shutterSpeedValue>
					<xsl:value-of select="exiftool/ShutterSpeed[1]"/>
				</shutterSpeedValue>

				<apertureSetting>
					<xsl:value-of select="exiftool/ApertureSetting[1]"/>
				</apertureSetting>

				<fNumber>
					<xsl:value-of select="exiftool/FNumber[1]"/>
				</fNumber>

				<gain>
					<xsl:value-of select="exiftool/Gain[1]"/>
				</gain>

				<exposureTime>
					<xsl:value-of select="exiftool/ExposureTime[1]"/>
				</exposureTime>

				<exposureProgram>
					<xsl:value-of select="exiftool/ExposureProgram[1]"/>
				</exposureProgram>

				<whiteBalance>
					<xsl:value-of select="exiftool/WhiteBalance[1]"/>
				</whiteBalance>

				<imageStabilization>
					<xsl:value-of select="exiftool/ImageStabilization[1]"/>
				</imageStabilization>

				<focus>
					<xsl:value-of select="exiftool/Focus[1]"/>
				</focus>

				<!-- GPS FIELDS -->
				<gpsVersionID>
					<xsl:value-of select="exiftool/GPSVersionID[1]"/>
				</gpsVersionID>
				<xsl:choose>
					<xsl:when test="exiftool/GPSLatitudeRef">
						<gpsLatitudeRef>
							<xsl:value-of select="exiftool/GPSLatitudeRef[1]"/>
						</gpsLatitudeRef>
					</xsl:when>
					<xsl:when test="exiftool/GPSLatitude">
						<gpsLatitudeRef>
							<xsl:value-of select="substring-after(exiftool/GPSLatitude[1],'&quot; ')"/>
						</gpsLatitudeRef>
					</xsl:when>
				</xsl:choose>
				<gpsLatitude>
					<xsl:value-of select="exiftool/GPSLatitude[1]"/>
				</gpsLatitude>

				<xsl:choose>
					<xsl:when test="exiftool/GPSLongitudeRef">
						<gpsLongitudeRef>
							<xsl:value-of select="exiftool/GPSLongitudeRef[1]"/>
						</gpsLongitudeRef>
					</xsl:when>
					<xsl:when test="exiftool/GPSLongitude">
						<gpsLongitudeRef>
							<xsl:value-of select="substring-after(exiftool/GPSLongitude[1],'&quot; ')"/>
						</gpsLongitudeRef>
					</xsl:when>
				</xsl:choose>
				<gpsLongitude>
					<xsl:value-of select="exiftool/GPSLongitude[1]"/>
				</gpsLongitude>

				<gpsAltitudeRef>
					<xsl:value-of select="exiftool/GPSAltitudeRef[1]"/>
				</gpsAltitudeRef>
				<gpsAltitude>
					<xsl:value-of select="exiftool/GPSAltitude[1]"/>
				</gpsAltitude>
				<gpsTimeStamp>
					<xsl:value-of select="exiftool/GPSTimeStamp[1]"/>
				</gpsTimeStamp>
				<gpsSatellites>
					<xsl:value-of select="exiftool/GPSSatellites[1]"/>
				</gpsSatellites>
				<gpsStatus>
					<xsl:value-of select="exiftool/GPSStatus[1]"/>
				</gpsStatus>
				<gpsMeasureMode>
					<xsl:value-of select="exiftool/GPSMeasureMode[1]"/>
				</gpsMeasureMode>
				<gpsDOP>
					<xsl:value-of select="exiftool/GPSDOP[1]"/>
				</gpsDOP>
				<gpsSpeedRef>
					<xsl:value-of select="exiftool/GPSSpeedRef[1]"/>
				</gpsSpeedRef>
				<gpsSpeed>
					<xsl:value-of select="exiftool/GPSSpeed[1]"/>
				</gpsSpeed>
				<gpsTrackRef>
					<xsl:value-of select="exiftool/GPSTrackRef[1]"/>
				</gpsTrackRef>
				<gpsTrack>
					<xsl:value-of select="exiftool/GPSTrack[1]"/>
				</gpsTrack>
				<gpsImgDirectionRef>
					<xsl:value-of select="exiftool/GPSImgDirectionRef[1]"/>
				</gpsImgDirectionRef>
				<gpsImgDirection>
					<xsl:value-of select="exiftool/GPSImgDirection[1]"/>
				</gpsImgDirection>
				<gpsMapDatum>
					<xsl:value-of select="exiftool/GPSMapDatum[1]"/>
				</gpsMapDatum>
				<xsl:choose>
					<xsl:when test="exiftool/GPSDestLatitudeRef[1]">
						<gpsDestLatitudeRef>
							<xsl:value-of select="exiftool/GPSDestLatitudeRef[1]"/>
						</gpsDestLatitudeRef>
					</xsl:when>
					<xsl:when test="exiftool/GPSDestLatitude">
						<gpsDestLatitudeRef>
							<xsl:value-of select="substring-after(exiftool/GPSDestLatitude[1],'&quot; ')"/>
						</gpsDestLatitudeRef>
					</xsl:when>
				</xsl:choose>
				<gpsDestLatitude>
					<xsl:value-of select="exiftool/GPSDestLatitude[1]"/>
				</gpsDestLatitude>

				<xsl:choose>
					<xsl:when test="exiftool/GPSDestLongitudeRef">
						<gpsDestLongitudeRef>
							<xsl:value-of select="exiftool/GPSDestLongitudeRef[1]"/>
						</gpsDestLongitudeRef>
					</xsl:when>
					<xsl:when test="exiftool/GPSDestLongitude">
						<gpsDestLongitudeRef>
							<xsl:value-of select="substring-after(exiftool/GPSDestLongitude[1],'&quot; ')"/>
						</gpsDestLongitudeRef>
					</xsl:when>
				</xsl:choose>
				<gpsDestLongitude>
					<xsl:value-of select="exiftool/GPSDestLongitudeRef[1]"/>
				</gpsDestLongitude>
				<gpsDestBearingRef>
					<xsl:value-of select="exiftool/GPSDestBearingRef[1]"/>
				</gpsDestBearingRef>
				<gpsDestBearing>
					<xsl:value-of select="exiftool/GPSDestBearing[1]"/>
				</gpsDestBearing>
				<gpsDestDistanceRef>
					<xsl:value-of select="exiftool/GPSDestDistanceRef[1]"/>
				</gpsDestDistanceRef>
				<gpsDestDistance>
					<xsl:value-of select="exiftool/GPSDestDistance[1]"/>
				</gpsDestDistance>
				<gpsProcessingMethod>
					<xsl:value-of select="exiftool/GPSProcessingMethod[1]"/>
				</gpsProcessingMethod>
				<gpsAreaInformation>
					<xsl:value-of select="exiftool/GPSAreaInformation[1]"/>
				</gpsAreaInformation>
				<gpsDateStamp>
					<xsl:value-of select="replace(exiftool/GPSDateStamp[1],':','-')"/>
				</gpsDateStamp>
				<gpsDifferential>
					<xsl:value-of select="exiftool/GPSDifferential[1]"/>
				</gpsDifferential>
				<ProjectionType>
					<xsl:value-of select="exiftool/ProjectionType[1]"/>
				</ProjectionType>
			</video>			
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>


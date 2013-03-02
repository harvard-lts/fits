<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exiftool_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">   

		<xsl:apply-imports/>
		
		<metadata>
		<image>
		<!-- 
			<exifByteOrder>
				<xsl:value-of select="exiftool/ExifByteOrder"/>
			</exifByteOrder>
		 -->
			<compressionScheme>		
				<xsl:choose>
					<xsl:when test="exiftool/Compression='CCIRLEW'">
						<xsl:value-of select="string('CCITTRLEW')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="exiftool/Compression"/>		
					</xsl:otherwise>
				</xsl:choose>
			</compressionScheme>
			<imageWidth>
				<xsl:value-of select="exiftool/ImageWidth"/>
			</imageWidth>
			<imageHeight>
				<xsl:value-of select="exiftool/ImageHeight"/>
			</imageHeight>
			<colorSpace>
				<xsl:choose>
					<!-- Ignore colorspace output for JPEG images -->
					<xsl:when test="exiftool/FileType='JP2'">
						<!--  if there is no icc profile description, use the colorspace -->
						<xsl:if test="string-length(exiftool/ProfileDescription) = 0">
							<xsl:choose>
								<xsl:when test="exiftool/ColorSpace">
									<xsl:value-of select="exiftool/ColorSpace"/>
								</xsl:when>
								<xsl:when test="exiftool/Colorspace">
									<xsl:value-of select="exiftool/Colorspace"/>
								</xsl:when>
							</xsl:choose>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="exiftool/PhotometricInterpretation"/>	
					</xsl:otherwise>
				</xsl:choose>
			</colorSpace>
			<referenceBlackWhite>
				<xsl:value-of select="exiftool/ReferenceBlackWhite"/>
			</referenceBlackWhite>
			<iccProfileName>
				<xsl:value-of select="exiftool/ProfileDescription"/>
			</iccProfileName>
			<iccProfileVersion>
				<xsl:value-of select="exiftool/ProfileVersion"/>
			</iccProfileVersion>
			<YCbCrSubSampling>
				<xsl:variable name="ycbcrPT1" select="substring-after(exiftool/YCbCrSubSampling,'(')"/> 			
				<xsl:value-of select="substring-before($ycbcrPT1,')')"/>
			</YCbCrSubSampling>
			<YCbCrCoefficients>
				<xsl:value-of select="exiftool/YCbCrCoefficients"/>
			</YCbCrCoefficients>
			<YCbCrPositioning>
				<xsl:value-of select="exiftool/YCbCrPositioning"/>
			</YCbCrPositioning>
			
			<!--  orientation -->
			<orientation>
				<xsl:variable name="orientation" select="exiftool/Orientation"/>
				<xsl:choose>
					<xsl:when test="$orientation='Horizontal (normal)'">
						<xsl:value-of select="string('normal*')"/>
					</xsl:when>
					<xsl:when test="$orientation='Mirror horizontal'">
						<xsl:value-of select="string('normal, image flipped')"/>
					</xsl:when>
					<xsl:when test="$orientation='Rotate 180'">
						<xsl:value-of select="string('normal, rotated 180°')"/>
					</xsl:when>
					<xsl:when test="$orientation='Mirror vertical'">
						<xsl:value-of select="string('normal, image flipped, rotated 180°')"/>
					</xsl:when>
					<xsl:when test="$orientation='Mirror horizontal and rotate 270 CW'">
						<xsl:value-of select="string('normal, image flipped, rotated cw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation='Rotate 90 CW'">
						<xsl:value-of select="string('normal, rotated ccw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation='Mirror horizontal and rotate 90 CW'">
						<xsl:value-of select="string('normal, image flipped, rotated ccw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation='Rotate 270 CW'">
						<xsl:value-of select="string('normal, rotated cw 90°')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$orientation"/>
					</xsl:otherwise>
				</xsl:choose>
			</orientation>
			
			
			<xsl:choose>
		  		<xsl:when test="string(exiftool/CaptureXResolutionUnit)">
	  				<samplingFrequencyUnit>
				  		<xsl:value-of select="exiftool/CaptureXResolutionUnit"/>
					</samplingFrequencyUnit>
		  		</xsl:when>
		  		<xsl:when test="string(exiftool/CaptureYResolutionUnit)">
	  				<samplingFrequencyUnit>
				  		<xsl:value-of select="exiftool/CaptureYResolutionUnit"/>
					</samplingFrequencyUnit>
		  		</xsl:when>
		  		<xsl:when test="string(exiftool/ResolutionUnit)">
	  				<samplingFrequencyUnit>
				  		<xsl:value-of select="exiftool/ResolutionUnit"/>
					</samplingFrequencyUnit>
		  		</xsl:when>
			</xsl:choose>
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
			<!--  bits per sample -->
		  	<xsl:choose>
		  	<xsl:when test="exiftool/ColorComponents">
	          <bitsPerSample>        
		          <xsl:call-template name="precisioncomponents">
		      	  	<xsl:with-param name="components" select="exiftool/ColorComponents"/>
		      	  	<xsl:with-param name="precision"  select="exiftool/BitsPerSample"/>
		      	  	<xsl:with-param name="bps"/>
		   		  </xsl:call-template>
	          </bitsPerSample>
          	</xsl:when>
          	<xsl:otherwise>
          		<bitsPerSample>
          			<xsl:value-of select="translate(exiftool/BitsPerSample,',','')"/>
          		</bitsPerSample>
          	</xsl:otherwise>
         	</xsl:choose>	
			<samplesPerPixel>
				<xsl:value-of select="exiftool/SamplesPerPixel"/>
			</samplesPerPixel>
			<extraSamples>
				<xsl:value-of select="exiftool/ExtraSamples"/>
			</extraSamples>
			<colorMap>
				<xsl:value-of select="exiftool/ColorMap"/>
			</colorMap>
			
			<!-- GrayResponseCurve is repeatable -->
			<!-- 
			<xsl:for-each select="exiftool/GrayResponseCurve">
				<grayResponseCurve>
			  		<xsl:value-of select="."/>
				</grayResponseCurve>
			</xsl:for-each>
			 -->
			 
			<grayResponseUnit>
				<xsl:value-of select="exiftool/GrayResponseUnit"/>
			</grayResponseUnit>
			<whitePointXValue>
				<xsl:value-of select='substring-before(exiftool/WhitePoint," ")'/>
			</whitePointXValue>
			<whitePointYValue>
				<xsl:value-of select='substring-after(exiftool/WhitePoint," ")'/>
			</whitePointYValue>
			
			<!-- parse through PrimaryChromaticities for the 6 pieces -->
			<xsl:variable name="pc" select="exiftool/PrimaryChromaticities"/>
			<primaryChromaticitiesRedX>
				<xsl:value-of select='substring-before($pc,", ")'/>
			</primaryChromaticitiesRedX>
			
			<xsl:variable name="afterRedX" select='substring-after($pc,", ")'/>		
			<primaryChromaticitiesRedY>
				<xsl:value-of select='substring-before($afterRedX,", ")'/>
			</primaryChromaticitiesRedY>
			
			<xsl:variable name="afterRedY" select='substring-after($afterRedX,", ")'/>
			<primaryChromaticitiesGreenX>
				<xsl:value-of select='substring-before($afterRedY,", ")'/>
			</primaryChromaticitiesGreenX>
			
			<xsl:variable name="afterGreenX" select='substring-after($afterRedY,", ")'/>
			<primaryChromaticitiesGreenY>
				<xsl:value-of select='substring-before($afterGreenX,", ")'/>
			</primaryChromaticitiesGreenY>
			
			<xsl:variable name="afterGreenY" select='substring-after($afterGreenX,", ")'/>
			<primaryChromaticitiesBlueX>
				<xsl:value-of select='substring-before($afterGreenY,", ")'/>
			</primaryChromaticitiesBlueX>
						
			<xsl:variable name="afterBlueX" select='substring-after($afterGreenY,", ")'/>
			<primaryChromaticitiesBlueY>
				<xsl:value-of select='$afterBlueX'/>
			</primaryChromaticitiesBlueY>
			
			<xsl:variable name="source" select="exiftool/FileSource"/>
			<captureDevice>
				<xsl:value-of select="$source"/>
			</captureDevice>
			
			<!--  make and model are ambiguous for identifying 
			      a digital camera or scanner source
			      so only use them as digital camera values
			      if the file source or shutter speed is specified,
			      else assume the device is a scanner -->
			<xsl:choose>
				<xsl:when test="exiftool/FileSource='Digital Camera'">
					<digitalCameraManufacturer>
					    <xsl:value-of select="exiftool/Make"/>
					</digitalCameraManufacturer>
					<digitalCameraModelName>
					    <xsl:value-of select="exiftool/Model"/>
					</digitalCameraModelName>
				</xsl:when>
				<xsl:when test="exiftool/ShutterSpeedValue">
					<digitalCameraManufacturer>
					    <xsl:value-of select="exiftool/Make"/>
					</digitalCameraManufacturer>
					<digitalCameraModelName>
					    <xsl:value-of select="exiftool/Model"/>
					</digitalCameraModelName>
				</xsl:when>		
				<xsl:otherwise>
					<scannerManufacturer>
						<xsl:value-of select="exiftool/Make"/>
					</scannerManufacturer>
					<scannerModelName>
						<xsl:value-of select="exiftool/Model"/>
					</scannerModelName>
				</xsl:otherwise>
			</xsl:choose>
			
			<scanningSoftwareName>
				<xsl:value-of select="exiftool/Software"/>
			</scanningSoftwareName>
			<digitalCameraSerialNo>
				<xsl:value-of select="exiftool/CameraSerialNumber"/>
			</digitalCameraSerialNo>
			<fNumber>
				<xsl:value-of select="exiftool/FNumber"/>
			</fNumber>
			<exposureTime>
				<xsl:value-of select="exiftool/ExposureTime"/>
			</exposureTime>
			<exposureProgram>
				<xsl:value-of select="exiftool/ExposureProgram"/>
			</exposureProgram>
			<spectralSensitivity>
				<xsl:value-of select="exiftool/SpectralSensitivity"/>
			</spectralSensitivity>
			<isoSpeedRating>
				<xsl:value-of select="exiftool/ISO"/>
			</isoSpeedRating>			
			<oECF>
				<xsl:value-of select="exiftool/Opto-ElectricConvFactor"/>
			</oECF>
			<exifVersion>
				<xsl:value-of select="exiftool/ExifVersion"/>
			</exifVersion>
			<shutterSpeedValue>
				<xsl:value-of select="exiftool/ShutterSpeedValue"/>
			</shutterSpeedValue>
			<apertureValue>
				<xsl:value-of select="exiftool/ApertureValue"/>
			</apertureValue>
			<brightnessValue>
				<xsl:value-of select="exiftool/BrightnessValue"/>
			</brightnessValue>
			<exposureBiasValue>
				<xsl:value-of select="exiftool/ExposureCompensation"/>
			</exposureBiasValue>
			<maxApertureValue>
				<xsl:value-of select="exiftool/MaxApertureValue"/>
			</maxApertureValue>
			<subjectDistance>
				<xsl:value-of select="exiftool/SubjectDistance"/>
			</subjectDistance>
			<meteringMode>
				<xsl:value-of select="exiftool/MeteringMode"/>
			</meteringMode>
			<lightSource>
				<xsl:value-of select="exiftool/LightSource"/>
			</lightSource>
			<flash>
				<xsl:value-of select="exiftool/Flash"/>
			</flash>
			<focalLength>
				<xsl:value-of select="replace(exiftool/FocalLength,' mm','')"/>				
			</focalLength>
			<flashEnergy>
				<xsl:value-of select="exiftool/flashEnergy"/>			
			</flashEnergy>
			<exposureIndex>
				<xsl:value-of select="exiftool/ExposureIndex"/>
			</exposureIndex>
			<sensingMethod>
				<xsl:value-of select="exiftool/SensingMethod"/>
			</sensingMethod>
			<cfaPattern>
				<xsl:value-of select="exiftool/CFAPattern"/>
			</cfaPattern>
			<cfaPattern2>
				<xsl:value-of select="exiftool/CFAPattern2"/>
			</cfaPattern2>
						
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
		</image>				
		</metadata>
	</fits>	

</xsl:template>



<!-- FUNCTIONS -->
<xsl:template name="precisioncomponents" >
 	<xsl:param name="components" select="1"/>
    <xsl:param name="precision" select="2"/>
    <xsl:param name="bps" select="3"/>
    <xsl:choose>
    	<xsl:when test="$components > 0">
	        <xsl:call-template name="precisioncomponents">
	          <xsl:with-param name="components" select="$components - 1"/>
	          <xsl:with-param name="precision" select="$precision"/>
	          <xsl:with-param name="bps" select='concat($bps,$precision," ")'/>
	        </xsl:call-template>
	    </xsl:when>
	    <xsl:when test="$components = 0">
	    	<!-- remove trailing space -->
    		<xsl:value-of select='substring($bps,1,string-length($bps) - 1)'/>
      	</xsl:when>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
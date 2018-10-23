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
		 	<xsl:if test="exiftool/Compression">
				<compressionScheme>
					<xsl:variable name="compression">
						<xsl:value-of select="exiftool/Compression"/>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="contains($compression, 'CCIRLEW')">
							<xsl:value-of select="string('CCITTRLEW')"/>
						</xsl:when>
						<xsl:when test="contains($compression, 'JPEG 2000')">
							<xsl:value-of select="string('JPEG 2000')"/>
						</xsl:when>
						<xsl:when test="contains($compression, 'JPEG')">
							<xsl:value-of select="string('JPEG')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="exiftool/Compression[1]"/>		
						</xsl:otherwise>
					</xsl:choose>
				</compressionScheme>
		 	</xsl:if>
			<imageWidth>
				<xsl:choose>
					<xsl:when test="exiftool/ExifImageWidth">
						<xsl:value-of select="exiftool/ExifImageWidth[last()]"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="exiftool/ImageWidth[last()]"/>	
					</xsl:otherwise>
				</xsl:choose>
			</imageWidth>
			<imageHeight>
				<xsl:choose>
					<xsl:when test="exiftool/ExifImageHeight">
						<xsl:value-of select="exiftool/ExifImageHeight[last()]"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="exiftool/ImageHeight[last()]"/>	
					</xsl:otherwise>
				</xsl:choose>
			</imageHeight>
			<colorSpace>
				<xsl:choose>
					<!-- Ignore colorspace output for JPEG images -->
					<xsl:when test="exiftool/FileType[1]='JP2' or exiftool/FileType[1]='JPX'">
						<!--  if there is no icc profile description, use the colorspace -->
						<xsl:if test="string-length(exiftool/ProfileDescription[1]) = 0">
							<xsl:choose>
								<xsl:when test="exiftool/ColorSpace">
									<xsl:value-of select="exiftool/ColorSpace[1]"/>
								</xsl:when>
							</xsl:choose>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="exiftool/PhotometricInterpretation[1]"/>	
					</xsl:otherwise>
				</xsl:choose>
			</colorSpace>
			<referenceBlackWhite>
				<xsl:value-of select="exiftool/ReferenceBlackWhite[1]"/>
			</referenceBlackWhite>
			<iccProfileName>
				<xsl:value-of select="exiftool/ProfileDescription[1]"/>
			</iccProfileName>
			<iccProfileVersion>
				<xsl:value-of select="exiftool/ProfileVersion[1]"/>
			</iccProfileVersion>
			<YCbCrSubSampling>
				<xsl:variable name="ycbcrPT1" select="substring-after(exiftool/YCbCrSubSampling[1],'(')"/>
				<xsl:value-of select="substring-before($ycbcrPT1,')')"/>
			</YCbCrSubSampling>
			<YCbCrCoefficients>
				<xsl:value-of select="exiftool/YCbCrCoefficients[1]"/>
			</YCbCrCoefficients>
			<YCbCrPositioning>
				<xsl:value-of select="exiftool/YCbCrPositioning[1]"/>
			</YCbCrPositioning>
			
			<!--  orientation -->
			<orientation>
				<xsl:variable name="orientation" select="exiftool/Orientation[1]"/>
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
<!-- 					<xsl:otherwise>
						<xsl:value-of select="$orientation"/>
					</xsl:otherwise> -->
				</xsl:choose>
			</orientation>
			
			
			<xsl:choose>
		  		<xsl:when test="string(exiftool/CaptureXResolutionUnit[1])">
	  				<samplingFrequencyUnit>
				  		<xsl:value-of select="exiftool/CaptureXResolutionUnit[1]"/>
					</samplingFrequencyUnit>
		  		</xsl:when>
		  		<xsl:when test="string(exiftool/CaptureYResolutionUnit[1])">
	  				<samplingFrequencyUnit>
				  		<xsl:value-of select="exiftool/CaptureYResolutionUnit[1]"/>
					</samplingFrequencyUnit>
		  		</xsl:when>
		  		<xsl:when test="string(exiftool/ResolutionUnit[1])">
	  				<samplingFrequencyUnit>
				  	  <xsl:choose>
               		      <xsl:when test="exiftool/ResolutionUnit[1] = 'inches'">
		                    <xsl:value-of select="string('in.')"/>
       		              </xsl:when>
        	              <xsl:otherwise>
		                    <xsl:value-of select="exiftool/ResolutionUnit[1]"/>
		                  </xsl:otherwise>
		              </xsl:choose> 
					</samplingFrequencyUnit>
		  		</xsl:when>
			</xsl:choose>
			<xsl:choose>
		  		<xsl:when test="string(exiftool/CaptureXResolution[1])">
	  				<xSamplingFrequency>
				  		<xsl:value-of select="exiftool/CaptureXResolution[1]"/>
					</xSamplingFrequency>
		  		</xsl:when>
		  		<xsl:when test="string(exiftool/XResolution[1])">
	  				<xSamplingFrequency>
				  		<xsl:value-of select="exiftool/XResolution[1]"/>
					</xSamplingFrequency>
		  		</xsl:when>
			</xsl:choose>			
			<xsl:choose>
		  		<xsl:when test="string(exiftool/CaptureYResolution[1])">
	  				<ySamplingFrequency>
				  		<xsl:value-of select="exiftool/CaptureYResolution[1]"/>
					</ySamplingFrequency>
		  		</xsl:when>
		  		<xsl:when test="string(exiftool/YResolution[1])">
	  				<ySamplingFrequency>
				  		<xsl:value-of select="exiftool/YResolution[1]"/>
					</ySamplingFrequency>
		  		</xsl:when>
			</xsl:choose>	
			<!--  bits per sample -->
		  	<xsl:choose>
			  	<xsl:when test="exiftool/ColorComponents">
		          <bitsPerSample>        
			          <xsl:call-template name="precisioncomponents">
			      	  	<xsl:with-param name="components" select="exiftool/ColorComponents"/>
			      	  	<xsl:with-param name="precision"  select="exiftool/BitsPerSample[last()]"/>
			      	  	<xsl:with-param name="bps"/>
			   		  </xsl:call-template>
		          </bitsPerSample>
	          	</xsl:when>
	          	<xsl:otherwise>
	          		<bitsPerSample>
	          			<xsl:value-of select="translate(exiftool/BitsPerSample[1],',','')"/>
	          		</bitsPerSample>
	          	</xsl:otherwise>
         	</xsl:choose>	
			<samplesPerPixel>
				<xsl:value-of select="exiftool/SamplesPerPixel[1]"/>
			</samplesPerPixel>
			<extraSamples>
				<xsl:value-of select="exiftool/ExtraSamples[1]"/>
			</extraSamples>
			<colorMap>
				<xsl:value-of select="exiftool/ColorMap[1]"/>
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
				<xsl:value-of select="exiftool/GrayResponseUnit[1]"/>
			</grayResponseUnit>
			<whitePointXValue>
				<xsl:value-of select='substring-before(exiftool/WhitePoint[1]," ")'/>
			</whitePointXValue>
			<whitePointYValue>
				<xsl:value-of select='substring-after(exiftool/WhitePoint[1]," ")'/>
			</whitePointYValue>
			
			<!-- parse through PrimaryChromaticities for the 6 pieces -->
			<xsl:variable name="pc" select="exiftool/PrimaryChromaticities[1]"/>
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
			
			<xsl:variable name="source" select="exiftool/FileSource[1]"/>
			<captureDevice>
				<xsl:choose>
					<xsl:when test="exiftool/FileSource='Digital Camera'">
						<xsl:value-of select="'digital still camera'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$source"/>
					</xsl:otherwise>
				</xsl:choose>
			</captureDevice>
			
			<!--  make and model are ambiguous for identifying 
			      a digital camera or scanner source
			      so only use them as digital camera values
			      if the file source or shutter speed is specified,
			      else assume the device is a scanner -->
			<xsl:choose>
				<xsl:when test="exiftool/FileSource[1]='Digital Camera'">
					<digitalCameraManufacturer>
					    <xsl:value-of select="exiftool/Make[1]"/>
					</digitalCameraManufacturer>
					<digitalCameraModelName>
					    <xsl:value-of select="exiftool/Model[1]"/>
					</digitalCameraModelName>
				</xsl:when>
				<xsl:when test="exiftool/ShutterSpeedValue[1]">
					<digitalCameraManufacturer>
					    <xsl:value-of select="exiftool/Make[1]"/>
					</digitalCameraManufacturer>
					<digitalCameraModelName>
					    <xsl:value-of select="exiftool/Model[1]"/>
					</digitalCameraModelName>
				</xsl:when>		
				<xsl:otherwise>
					<scannerManufacturer>
						<xsl:value-of select="exiftool/Make[1]"/>
					</scannerManufacturer>
					<scannerModelName>
						<xsl:value-of select="exiftool/Model[1]"/>
					</scannerModelName>
				</xsl:otherwise>
			</xsl:choose>
			
			<scanningSoftwareName>
				<xsl:value-of select="exiftool/Software[1]"/>
			</scanningSoftwareName>
			<digitalCameraSerialNo>
				<xsl:value-of select="exiftool/CameraSerialNumber[1]"/>
			</digitalCameraSerialNo>
			<fNumber>
				<xsl:value-of select="exiftool/FNumber[1]"/>
			</fNumber>
			<exposureTime>
                <xsl:variable name="exposure" select="exiftool/ExposureTime[1]" />
                <xsl:choose>
                    <!-- convert fraction to decimal -->
	                <xsl:when test="contains($exposure, '/')">
	                    <xsl:variable name="numerator" select="number(substring-before($exposure, '/'))"  />
	                    <xsl:variable name="denominator" select="number(substring-after($exposure, '/'))"  />
	                    <xsl:value-of select="format-number($numerator div $denominator, '##0.####')"/>
	                </xsl:when>
	                <xsl:otherwise>
	                    <xsl:value-of select="exiftool/ExposureTime[1]"/>
	                </xsl:otherwise>
                </xsl:choose>
			</exposureTime>
			<exposureProgram>
				<xsl:variable name="exposureProgram" select="exiftool/ExposureProgram[1]"/>
				<xsl:choose>
					<xsl:when test="$exposureProgram='Not Defined'">
						<xsl:value-of select="string('Not defined')"/>
					</xsl:when>
					<xsl:when test="$exposureProgram='Manual'">
						<xsl:value-of select="string('Manual')"/>
					</xsl:when>
					<xsl:when test="$exposureProgram='Program AE'">
						<xsl:value-of select="string('Normal program')"/>
					</xsl:when>
					<xsl:when test="$exposureProgram='Aperture-priority AE'">
						<xsl:value-of select="string('Aperture priority')"/>
					</xsl:when>
					<xsl:when test="$exposureProgram='Shutter speed priority AE'">
						<xsl:value-of select="string('Shutter priority')"/>
					</xsl:when>
					<xsl:when test="$exposureProgram='Creative (Slow speed)'">
						<xsl:value-of select="string('Creative program (biased toward depth of field)')"/>
					</xsl:when>
					<xsl:when test="$exposureProgram='Action (High speed)'">
						<xsl:value-of select="string('Action program (biased toward fast shutter speed)')"/>
					</xsl:when>
					<xsl:when test="$exposureProgram='Portrait'">
						<xsl:value-of select="string('Portrait mode (for closeup photos with the background out of focus)')"/>
					</xsl:when>
					<xsl:when test="$exposureProgram='Landscape'">
						<xsl:value-of select="string('Landscape mode (for landscape photos with the background in focus)')"/>
					</xsl:when>
				</xsl:choose>
			</exposureProgram>
			<spectralSensitivity>
				<xsl:value-of select="exiftool/SpectralSensitivity[1]"/>
			</spectralSensitivity>
			<isoSpeedRating>
				<xsl:value-of select="exiftool/ISO[1]"/>
			</isoSpeedRating>			
			<oECF>
				<xsl:value-of select="exiftool/Opto-ElectricConvFactor[1]"/>
			</oECF>
			<exifVersion>
				<xsl:value-of select="exiftool/ExifVersion[1]"/>
			</exifVersion>
			<shutterSpeedValue>
				<xsl:value-of select="exiftool/ShutterSpeedValue[1]"/>
			</shutterSpeedValue>
			<apertureValue>
				<xsl:value-of select="exiftool/ApertureValue[1]"/>
			</apertureValue>
			<brightnessValue>
				<xsl:value-of select="exiftool/BrightnessValue[1]"/>
			</brightnessValue>
			<exposureBiasValue>
				<xsl:value-of select="exiftool/ExposureCompensation[1]"/>
			</exposureBiasValue>
			<maxApertureValue>
				<xsl:value-of select="exiftool/MaxApertureValue[1]"/>
			</maxApertureValue>
			<subjectDistance>
				<xsl:value-of select="exiftool/SubjectDistance[1]"/>
			</subjectDistance>
			<meteringMode>
				<xsl:variable name="meteringMode" select="exiftool/MeteringMode[1]"/>
				<xsl:choose>
					<xsl:when test="$meteringMode='Center-weighted average'">
						<xsl:value-of select="string('Center weighted average')"/>
					</xsl:when>
					<xsl:when test="$meteringMode='Multi-spot'">
						<xsl:value-of select="string('Multispot')"/>
					</xsl:when>
					<xsl:when test="$meteringMode='Multi-segment'">
						<xsl:value-of select="string('Pattern')"/>
					</xsl:when>
				</xsl:choose>
			</meteringMode>
			<lightSource>
				<xsl:variable name="lightSource" select="exiftool/LightSource[1]"/>
				<xsl:choose>
					<xsl:when test="$lightSource='Unknown'">
						<xsl:value-of select="string('unknown')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Tungsten (Incandescent)'">
						<xsl:value-of select="string('Tungsten (incandescent light)')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Fine Weather'">
						<xsl:value-of select="string('Fine weather')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Cloudy'">
						<xsl:value-of select="string('Cloudy weather')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Daylight Fluorescent'">
						<xsl:value-of select="string('Daylight fluorescent (D 5700 - 7100K)')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Day White Fluorescent'">
						<xsl:value-of select="string('Day white fluorescent (N 4600 - 5400K)')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Cool White Fluorescent'">
						<xsl:value-of select="string('Cool white fluorescent (W 3900 - 4500K)')"/>
					</xsl:when>
					<xsl:when test="$lightSource='White Fluorescent'">
						<xsl:value-of select="string('White fluorescent (WW 3200 - 3700K)')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Warm White Fluorescent'"> <!-- NO MIX VALUE FOR THIS -->
						<xsl:value-of select="string('other light source')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Standard Light A'">
						<xsl:value-of select="string('Standard light A')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Standard light B'">
						<xsl:value-of select="string('Standard light B')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Standard light C'">
						<xsl:value-of select="string('Standard light C')"/>
					</xsl:when>
					<xsl:when test="$lightSource='ISO Studio Tungsten'">
						<xsl:value-of select="string('ISO studio tungsten')"/>
					</xsl:when>
					<xsl:when test="$lightSource='Other'">
						<xsl:value-of select="string('other light source')"/>
					</xsl:when>
				</xsl:choose>
			</lightSource>
			<flash>
				<xsl:variable name="flash" select="exiftool/Flash[1]"/>
		      	<xsl:choose>
					<xsl:when test="$flash='No Flash'">
						<xsl:value-of select="string('Flash did not fire')"/>
					</xsl:when>
					<xsl:when test="$flash='Fired'">
						<xsl:value-of select="string('Flash fired')"/>
					</xsl:when>
					<xsl:when test="$flash='Fired, Return not detected'">
						<xsl:value-of select="string('Strobe return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='Fired, Return detected'">
						<xsl:value-of select="string('Strobe return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='On, Did not fire'">
						<xsl:value-of select="string('Flash did not fire, compulsory flash mode')"/>
					</xsl:when>
					<xsl:when test="$flash='On, Fired'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode')"/>
					</xsl:when>
					<xsl:when test="$flash='On, Return not detected'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='On, Return detected'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='Auto, Did not fire'">
						<xsl:value-of select="string('Flash did not fire, auto mode')"/>
					</xsl:when>
					<xsl:when test="$flash='Auto, Fired'">
						<xsl:value-of select="string('Flash fired, auto mode')"/>
					</xsl:when>
					<xsl:when test="$flash='Auto, Fired, Return not detected'">
						<xsl:value-of select="string('Flash fired, auto mode, return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='Auto, Fired, Return detected'">
						<xsl:value-of select="string('Flash fired, auto mode, return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='No flash function'">
						<xsl:value-of select="string('No flash function')"/>
					</xsl:when>
					<xsl:when test="$flash='Fired, Red-eye reduction'">
						<xsl:value-of select="string('Flash fired, red-eye reduction mode')"/>
					</xsl:when>
					<xsl:when test="$flash='Fired, Red-eye reduction, Return not detected'">
						<xsl:value-of select="string('Flash fired, red-eye reduction mode, return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='Fired, Red-eye reduction, Return detected'">
						<xsl:value-of select="string('Flash fired, red-eye reduction mode, return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='On, Red-eye reduction'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode')"/>
					</xsl:when>
					<xsl:when test="$flash='On, Red-eye reduction, Return not detected'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode, return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='On, Red-eye reduction, Return detected'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode, return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='Auto, Fired, Red-eye reduction'">
						<xsl:value-of select="string('Flash fired, auto mode, red-eye reduction mode')"/>
					</xsl:when>
					<xsl:when test="$flash='Auto, Fired, Red-eye reduction, Return not detected'">
						<xsl:value-of select="string('Flash fired, auto mode, return light not detected, red-eye reduction mode')"/>
					</xsl:when>
					<xsl:when test="$flash='Auto, Fired, Red-eye reduction, Return detected'">
						<xsl:value-of select="string('Flash fired, auto mode, return light detected, red-eye reduction mode')"/>
					</xsl:when>
				</xsl:choose>
			</flash>
			<focalLength>
				<xsl:value-of select="replace(exiftool/FocalLength[1],' mm','')"/>				
			</focalLength>
			<flashEnergy>
				<xsl:value-of select="exiftool/FlashEnergy[1]"/>			
			</flashEnergy>
			<exposureIndex>
				<xsl:value-of select="exiftool/ExposureIndex[1]"/>
			</exposureIndex>
			<sensingMethod>
				<xsl:variable name="sensingMethod" select="exiftool/SensingMethod[1]"/>
				<xsl:choose>
 					<xsl:when test="$sensingMethod='Trilinear'">
						<xsl:value-of select="string('Trilinear sensor')"/>
					</xsl:when>
					<xsl:when test="$sensingMethod='Color sequential linear'">
						<xsl:value-of select="string('Color sequential linear sensor')"/>
					</xsl:when>
					<xsl:when test="$sensingMethod='One-chip color area'">
						<xsl:value-of select="string('One-chip color area sensor')"/>
					</xsl:when>
					<xsl:when test="$sensingMethod='Two-chip color area'">
						<xsl:value-of select="string('Two-chip color area sensor')"/>
					</xsl:when>
					<xsl:when test="$sensingMethod='Three-chip color area'">
						<xsl:value-of select="string('Three-chip color area sensor')"/>
					</xsl:when>
					<xsl:when test="$sensingMethod='Color sequential area'">
						<xsl:value-of select="string('Color sequential area sensor')"/>
					</xsl:when>
				</xsl:choose>
			</sensingMethod>
			<cfaPattern>
				<xsl:value-of select="exiftool/CFAPattern[1]"/>
			</cfaPattern>
			<cfaPattern2>
				<xsl:value-of select="exiftool/CFAPattern2[1]"/>
			</cfaPattern2>
						
			<!-- GPS FIELDS -->
			<gpsVersionID>
				<xsl:value-of select="exiftool/GPSVersionID[1]"/>
			</gpsVersionID>
			
			
			<gpsLatitudeRef>
				<xsl:variable name="gpsLatRef" select="exiftool/GPSLatitudeRef"/>
				<xsl:choose>
					<xsl:when test="$gpsLatRef='North'">
						<xsl:value-of select="string('N')"/>
					</xsl:when>
					<xsl:when test="$gpsLatRef='South'">
						<xsl:value-of select="string('S')"/>
					</xsl:when>
				</xsl:choose>
			</gpsLatitudeRef>
					
			<gpsLatitude>
				<xsl:value-of select="exiftool/GPSLatitude[last()]"/>
			</gpsLatitude>			
			
			<gpsLongitudeRef>
				<xsl:variable name="gpsLonRef" select="exiftool/GPSLongitudeRef"/>
				<xsl:choose>
					<xsl:when test="$gpsLonRef='East'">
						<xsl:value-of select="string('E')"/>
					</xsl:when>
					<xsl:when test="$gpsLonRef='West'">
						<xsl:value-of select="string('W')"/>
					</xsl:when>
				</xsl:choose>
			</gpsLongitudeRef>
					
			<gpsLongitude>
				<xsl:value-of select="exiftool/GPSLongitude[last()]"/>
			</gpsLongitude>	
			
			<gpsAltitudeRef>
				<xsl:variable name="gpsAltRef" select="exiftool/GPSAltitudeRef"/>
				<xsl:choose>
					<xsl:when test="$gpsAltRef='Above Sea Level'">
						<xsl:value-of select="string('Sea level')"/>
					</xsl:when>
					<xsl:when test="$gpsAltRef='Below Sea Level'">
						<xsl:value-of select="string('Sea level reference (negative value)')"/>
					</xsl:when>
				</xsl:choose>
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
				<xsl:variable name="gpsStatus" select="exiftool/GPSStatus[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsStatus='Measurement Active'">
						<xsl:value-of select="string('A')"/>
					</xsl:when>
					<xsl:when test="$gpsStatus='Measurement Void'">
						<xsl:value-of select="string('V')"/>
					</xsl:when>
				</xsl:choose>
			</gpsStatus>
			<gpsMeasureMode>
				<xsl:if test="exiftool/GPSMeasureMode[1]">
					<xsl:value-of select="lower-case(exiftool/GPSMeasureMode)"/>
				</xsl:if>
			</gpsMeasureMode>
			<gpsDOP>
				<xsl:value-of select="exiftool/GPSDOP"/>
			</gpsDOP>
			<gpsSpeedRef>
				<xsl:variable name="gpsSpeedRef" select="exiftool/GPSSpeedRef[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsSpeedRef='km/h'">
						<xsl:value-of select="string('K')"/>
					</xsl:when>
					<xsl:when test="$gpsSpeedRef='mph'">
						<xsl:value-of select="string('M')"/>
					</xsl:when>
					<xsl:when test="$gpsSpeedRef='knots'">
						<xsl:value-of select="string('N')"/>
					</xsl:when>
				</xsl:choose>
			</gpsSpeedRef>
			<gpsSpeed>
				<xsl:value-of select="exiftool/GPSSpeed[1]"/>
			</gpsSpeed>
			<gpsTrackRef>
				<xsl:variable name="gpsTrackRef" select="exiftool/GPSTrackRef[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsTrackRef='Magnetic North'">
						<xsl:value-of select="string('M')"/>
					</xsl:when>
					<xsl:when test="$gpsTrackRef='True North'">
						<xsl:value-of select="string('T')"/>
					</xsl:when>
				</xsl:choose>
			</gpsTrackRef>
			<gpsTrack>
				<xsl:value-of select="exiftool/GPSTrack"/>
			</gpsTrack>
			<gpsImgDirectionRef>
				<xsl:variable name="gpsImgDirRef" select="exiftool/GPSImgDirectionRef[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsImgDirRef='Magnetic North'">
						<xsl:value-of select="string('M')"/>
					</xsl:when>
					<xsl:when test="$gpsImgDirRef='True North'">
						<xsl:value-of select="string('T')"/>
					</xsl:when>
				</xsl:choose>
			</gpsImgDirectionRef>
			<gpsImgDirection>
				<xsl:value-of select="exiftool/GPSImgDirection[1]"/>
			</gpsImgDirection>
			<gpsMapDatum>
				<xsl:value-of select="exiftool/GPSMapDatum[1]"/>
			</gpsMapDatum>
			<gpsDestLatitudeRef>
				<xsl:variable name="gpsDestLatRef" select="exiftool/GPSDestLatitudeRef[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsDestLatRef='North'">
						<xsl:value-of select="string('N')"/>
					</xsl:when>
					<xsl:when test="gpsDestLatRef='South'">
						<xsl:value-of select="string('S')"/>
					</xsl:when>
				</xsl:choose>
			</gpsDestLatitudeRef>
			<gpsDestLatitude>
				<xsl:value-of select="exiftool/GPSDestLatitude[1]"/>
			</gpsDestLatitude>		
			<gpsDestLongitudeRef>
				<xsl:variable name="gpsDestLonRef" select="exiftool/GPSDestLongitudeRef[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsDestLonRef='East'">
						<xsl:value-of select="string('E')"/>
					</xsl:when>
					<xsl:when test="$gpsDestLonRef='West'">
						<xsl:value-of select="string('W')"/>
					</xsl:when>
				</xsl:choose>
			</gpsDestLongitudeRef>
			<gpsDestLongitude>
				<xsl:value-of select="exiftool/GPSDestLongitude[1]"/>
			</gpsDestLongitude>			
			<gpsDestBearingRef>
				<xsl:variable name="gpsLatRef" select="exiftool/GPSDestBearingRef[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsLatRef='Magnetic North'">
						<xsl:value-of select="string('M')"/>
					</xsl:when>
					<xsl:when test="$gpsLatRef='True North'">
						<xsl:value-of select="string('T')"/>
					</xsl:when>
				</xsl:choose>
			</gpsDestBearingRef>
			<gpsDestBearing>
				<xsl:value-of select="exiftool/GPSDestBearing[1]"/>
			</gpsDestBearing>
			<gpsDestDistanceRef>
				<xsl:variable name="gpsDestDistRef" select="exiftool/GPSDestDistanceRef[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsDestDistRef='Kilometers'">
						<xsl:value-of select="string('K')"/>
					</xsl:when>
					<xsl:when test="$gpsDestDistRef='Miles'">
						<xsl:value-of select="string('M')"/>
					</xsl:when>
					<xsl:when test="$gpsDestDistRef='Nautical Miles'">
						<xsl:value-of select="string('N')"/>
					</xsl:when>
				</xsl:choose>
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
				<xsl:variable name="gpsDiff" select="exiftool/GPSDifferential[1]"/>
				<xsl:choose>
					<xsl:when test="$gpsDiff='No Correction'">
						<xsl:value-of select="string('Measurement without differential correction')"/>
					</xsl:when>
					<xsl:when test="$gpsDiff='Differential Corrected'">
						<xsl:value-of select="string('Differential correction applied')"/>
					</xsl:when>
				</xsl:choose>
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
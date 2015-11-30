<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:mix="http://www.loc.gov/mix/v20">

<xsl:import href="jhove_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">  
    
		<xsl:apply-imports/>
		
		<metadata>
			<image>
			
			<!-- variable for format -->
			<xsl:variable name="format" select="repInfo/format"/>
			
			<!-- byte order -->
			<xsl:choose>
		  		<xsl:when test="//mix:byteOrder">
		  			<xsl:for-each select="//mix:byteOrder">
						<byteOrder>
					  		<xsl:value-of select="replace(.,'-',' ')"/>
						</byteOrder>
					</xsl:for-each>
		  		</xsl:when>
		  		<xsl:otherwise>
					<byteOrder>
				  		<xsl:value-of select="replace(//property[name='ByteOrder']/values/value,'-',' ')"/>
					</byteOrder>
				</xsl:otherwise>
			</xsl:choose>
		
			<!-- compression -->
			<xsl:choose>
				<xsl:when test="$format='JPEG 2000'">
					<!-- when image is a jp2 -->
					<compressionScheme>
						<xsl:variable name="jp2Compression" select="//property[name='Transformation']/values/value"/>
						<xsl:choose>
							<xsl:when test="$jp2Compression=0">
								<xsl:value-of select="string('JPEG 2000 Lossy')"/>
							</xsl:when>
							<xsl:when test="$jp2Compression=1">
								<xsl:value-of select="string('JPEG 2000 Lossless')"/>
							</xsl:when>	
						</xsl:choose>
					</compressionScheme>
				</xsl:when>
				<xsl:otherwise>
					<!-- otherwise use mix metadata -->
					<compressionScheme>
					     <xsl:value-of select="(//mix:compressionScheme)[1]"/> <!-- Take the first width found -->
					</compressionScheme> 				
				</xsl:otherwise>
			</xsl:choose>

			 <!--  width -->
			<imageWidth>
		  		<xsl:value-of select="(//mix:imageWidth)[1]"/> <!-- Take the first width found -->
			</imageWidth>
			
			<!--  height -->
			<imageHeight>
		  		<xsl:value-of select="(//mix:imageLength)[1]"/> <!-- Take the first height found -->
			</imageHeight>
		
			<!--  colorspace -->
			<xsl:choose>
				<xsl:when test="$format = 'JPEG 2000'">
					<!-- when image is a jp2 -->
					<colorSpace>
						<!-- 
						16 = sRGB
						17 = Grayscale
						18 = sYCC
						 -->
				  		<xsl:value-of select="//property[name='EnumCS']/values/value"/>
					</colorSpace>
				</xsl:when>		
				<xsl:otherwise>
					<!-- otherwise use mix metadata -->
					<colorSpace>
					    <xsl:value-of select="(//mix:colorSpace)[1]"/> <!-- Take the first height found -->
					</colorSpace>
				</xsl:otherwise>
			</xsl:choose>

			<!--  ReferenceBlackWhite -->
			<xsl:choose>
				<xsl:when test="//mix:ReferenceBlackWhite/mix:Component">
					<referenceBlackWhite>
						<xsl:for-each select="//mix:ReferenceBlackWhite/mix:Component">
								<xsl:value-of select="./mix:footroom/mix:numerator"/>
								<xsl:value-of select="string(' ')"/>
								<xsl:value-of select="./mix:headroom/mix:numerator"/>
								<xsl:value-of select="string(' ')"/>
						</xsl:for-each>
					</referenceBlackWhite>
				</xsl:when>
				<xsl:otherwise>
					<referenceBlackWhite>
						<xsl:value-of select="//mix:ReferenceBlackWhite"/>
					</referenceBlackWhite>
				</xsl:otherwise>
			</xsl:choose>

			<!--  iccProfileName -->
			<iccProfileName>
		  		<xsl:value-of select="//mix:iccProfileName"/>
			</iccProfileName>
			
			<!--  YCbCrSubSampling -->
			<YCbCrSubSampling>
		  		<xsl:value-of select="//mix:YCbCrSubSampling"/>
			</YCbCrSubSampling>
			
			<!--  YCbCrCoefficients -->
			<YCbCrCoefficients>
		  		<xsl:value-of select="//mix:YCbCrCoefficients"/>
			</YCbCrCoefficients>

			<!--  YCbCrPositioning -->
			<YCbCrPositioning>
				<xsl:variable name="ycbcrpositioning" select="//mix:yCbCrPositioning"/>
				<xsl:choose>
					<xsl:when test="$ycbcrpositioning=1">
						<xsl:value-of select="string('Centered')"/>
					</xsl:when>
					<xsl:when test="$ycbcrpositioning=2">
						<xsl:value-of select="string('Co-sited')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$ycbcrpositioning"/>
					</xsl:otherwise>
				</xsl:choose>
			</YCbCrPositioning>
			
			<!--  tileWidth (jp2) -->
			<tileWidth>
		  		<xsl:value-of select="//property[name='XTSize']/values/value"/>
			</tileWidth>

			<!--  tileHeight (jp2) -->
			<tileHeight>
		  		<xsl:value-of select="//property[name='YTSize']/values/value"/>
			</tileHeight>
			
			<!--  qualityLayers (jp2) -->
			<qualityLayers>
		  		<xsl:value-of select="//property[name='NumberOfLayers']/values/value"/>
			</qualityLayers>
			
			<!--  resolutionLevels (jp2) -->
			<resolutionLevels>
		  		<xsl:value-of select="//property[name='NumberDecompositionLevels']/values/value"/>
			</resolutionLevels>

			<!--  orientation -->
			<orientation>
				<xsl:value-of select="(//mix:orientation)[1]"/> <!-- Take the first height found -->
			</orientation>
			
			<!-- samplingFrequencyUnit -->
			<samplingFrequencyUnit>
                <xsl:value-of select="//mix:samplingFrequencyUnit"/>
			</samplingFrequencyUnit>
			
			<!--  xSamplingFrequency -->
			<xSamplingFrequency>
			    <xsl:variable name="xNumerator" select="//mix:xSamplingFrequency/mix:numerator"/>
                <xsl:variable name="xDenominator" select="//mix:xSamplingFrequency/mix:denominator"/>
		  		<xsl:value-of select="round($xNumerator div $xDenominator)"/>
			</xSamplingFrequency>
			
			<!--  ySamplingFrequency -->
			<ySamplingFrequency>
                <xsl:variable name="yNumerator" select="//mix:ySamplingFrequency/mix:numerator"/>
                <xsl:variable name="yDenominator" select="//mix:ySamplingFrequency/mix:denominator"/>
		  		<xsl:value-of select="round($yNumerator div $yDenominator)"/>
			</ySamplingFrequency>
			
			<!--  bitsPerSample -->
			<xsl:choose>
		  		<xsl:when test="//mix:bitsPerSampleValue">
				    <bitsPerSample>
						<xsl:for-each select="//mix:bitsPerSampleValue">
						  		<xsl:value-of select="."/>
                                <xsl:if test="not(position() = last())">
	    					  		<xsl:value-of select="string(' ')"/>
	   					  		</xsl:if>
						</xsl:for-each>
				    </bitsPerSample>
		  		</xsl:when>
		  		<xsl:otherwise>				
					<xsl:for-each select="//mix:BitsPerSample">
						<bitsPerSample>
					  		<xsl:value-of select="translate(.,',',' ')"/>
						</bitsPerSample>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
			
			<!--  samplesPerPixel -->
			<samplesPerPixel>
		  		<xsl:value-of select="//mix:samplesPerPixel"/>
			</samplesPerPixel>		
			
			<!--  extraSamples -->
			<extraSamples>
				<xsl:value-of select="//mix:extraSamples"/>
			</extraSamples>			
			
			<!--  colorMap -->
			<colorMap>
		  		<xsl:value-of select="//mix:colormapReference"/>
			</colorMap>
			
			<!--  grayResponseCurve -->
			<!-- 
			<grayResponseCurve>
		  		<xsl:value-of select="//mix:grayResponseCurve"/>
			</grayResponseCurve>
			-->
			
			<!-- grayResponseCurve is repeatable -->
			<!-- 
			<xsl:for-each select="//mix:grayResponseCurve">
				<grayResponseCurve>
			  		<xsl:value-of select="."/>
				</grayResponseCurve>
			</xsl:for-each>
			 -->
			 
			<!--  grayResponseUnit -->
			<grayResponseUnit>
				<xsl:value-of select="//mix:grayResponseUnit"/>
			</grayResponseUnit>
			
			<!--  whitePointXValue -->
			<whitePointXValue>
		  		<xsl:value-of select="//mix:whitePointXValue"/>
			</whitePointXValue>

			<!--  whitePointYValue -->
			<whitePointYValue>
		  		<xsl:value-of select="//mix:whitePointYValue"/>
			</whitePointYValue>
			
			<!--  primaryChromaticitiesRedX -->
			<primaryChromaticitiesRedX>
		  		<xsl:value-of select="//mix:primaryChromaticitiesRedX"/>
			</primaryChromaticitiesRedX>

			<!--  primaryChromaticitiesRedY -->
			<primaryChromaticitiesRedY>
		  		<xsl:value-of select="//mix:primaryChromaticitiesRedY"/>
			</primaryChromaticitiesRedY>
			
			<!--  primaryChromaticitiesGreenX -->
			<primaryChromaticitiesGreenX>
		  		<xsl:value-of select="//mix:primaryChromaticitiesGreenX"/>
			</primaryChromaticitiesGreenX>	

			<!--  primaryChromaticitiesGreenY -->
			<primaryChromaticitiesGreenY>
		  		<xsl:value-of select="//mix:primaryChromaticitiesGreenY"/>
			</primaryChromaticitiesGreenY>
			
			<!--  primaryChromaticitiesBlueX -->
			<primaryChromaticitiesBlueX>
		  		<xsl:value-of select="//mix:primaryChromaticitiesBlueX"/>
			</primaryChromaticitiesBlueX>	

			<!--  primaryChromaticitiesBlueY -->
			<primaryChromaticitiesBlueY>
		  		<xsl:value-of select="//mix:primaryChromaticitiesBlueY"/>
			</primaryChromaticitiesBlueY>
			
			<!--  imageProducer -->
			<imageProducer>
		  		<xsl:value-of select="//mix:imageProducer"/>
			</imageProducer>				
			
			<!--  scannerManufacturer -->
			<!--  Jhove reports digital camera models as 'scanners', so it's commented out
			<scannerManufacturer>
		  		<xsl:value-of select="//mix:ScannerManufacturer"/>
			</scannerManufacturer>	
			 -->
			<!--  scannerModelName -->
			<!--  Jhove reports digital camera models as 'scanners', so it's commented out
			<scannerModelName>
		  		<xsl:value-of select="//mix:ScannerModelName"/>
			</scannerModelName>						
			-->
			
			<!--  scannerModelNumber -->
			<scannerModelNumber>
		  		<xsl:value-of select="//mix:scannerModelNumber"/>
			</scannerModelNumber>
			
			<!--  scannerModelSerialNo -->
			<scannerModelSerialNo>
		  		<xsl:value-of select="//mix:scannerModelSerialNo"/>
			</scannerModelSerialNo>
			
			<!--  scanningSoftwareName -->
			<scanningSoftwareName>
		  		<xsl:value-of select="//mix:scanningSoftwareName"/>
			</scanningSoftwareName>
			
			<!--  scanningSoftwareVersionNo -->
			<scanningSoftwareVersionNo>
		  		<xsl:value-of select="//mix:scanningSoftwareVersionNo"/>
			</scanningSoftwareVersionNo>

			<!--  digitalCameraModelName -->
			<digitalCameraModelName>
		  		<xsl:value-of select="//mix:digitalCameraModelName"/>
			</digitalCameraModelName>
			
			<!--  fNumber -->
			<fNumber>
		  		<xsl:value-of select="//property[name='FNumber']/values/value"/>
			</fNumber>

			<!--  exposureTime -->
			<exposureTime>
		  		<xsl:value-of select="//property[name='ExposureTime']/values/value"/>
			</exposureTime>
			
			<!--  ISO -->
			<isoSpeedRating>
				<xsl:value-of select="//property[name='ISOSpeedRatings']/values/value"/>
			</isoSpeedRating>			
			
			<!--  brightnessValue -->
			<brightnessValue>
		  		<xsl:value-of select="//mix:brightnessValue"/>
			</brightnessValue>

			<!--  exposureBiasValue -->
			<exposureBiasValue>
		  		<xsl:value-of select="//property[name='ExposureBiasValue']/values/value"/>
			</exposureBiasValue>
			
			<!--  lightSource -->
			<lightSource>
				<xsl:value-of select="//property[name='LightSource']/values/value"/>
			</lightSource>

			<!--  subjectDistance -->
			<subjectDistance>
		  		<xsl:value-of select="//mix:SubjectDistance"/>
			</subjectDistance>

			<!--  meteringMode -->
			<meteringMode>
				<xsl:value-of select="//property[name='MeteringMode']/values/value"/>
			</meteringMode>
			
			<!--  flash -->
			<flash>
				<xsl:variable name="flash" select="//property[name='Flash']/values/value"/>
		      	<xsl:choose>
					<xsl:when test="$flash='did not fire'">
						<xsl:value-of select="string('Flash did not fire')"/>
					</xsl:when>
					<xsl:when test="$flash='fired'">
						<xsl:value-of select="string('Flash fired')"/>
					</xsl:when>
					<xsl:when test="$flash='strobe return light not detected'">
						<xsl:value-of select="string('Strobe return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='strobe return light detected'">
						<xsl:value-of select="string('Strobe return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='did not fire, compulsory flash mode'">
						<xsl:value-of select="string('Flash did not fire, compulsory flash mode')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, compulsory flash mode'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, compulsory flash mode, return light not detected'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, compulsory flash mode, return light detected'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='did not fire, auto mode'">
						<xsl:value-of select="string('Flash did not fire, auto mode')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, auto mode'">
						<xsl:value-of select="string('Flash fired, auto mode')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, auto mode, return light not detected'">
						<xsl:value-of select="string('Flash fired, auto mode, return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, auto mode, return light detected'">
						<xsl:value-of select="string('Flash fired, auto mode, return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='no flash function'">
						<xsl:value-of select="string('No flash function')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, red-eye reduction mode'">
						<xsl:value-of select="string('Flash fired, red-eye reduction mode')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, red-eye reduction mode, return light not detected'">
						<xsl:value-of select="string('Flash fired, red-eye reduction mode, return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, red-eye reduction mode, return light detected'">
						<xsl:value-of select="string('Flash fired, red-eye reduction mode, return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, compulsory mode'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, compulsory mode, return light not detected'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode, return light not detected')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, compulsory flash mode, return light detected'">
						<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode, return light detected')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, auto mode, red-eye reduction mode'">
						<xsl:value-of select="string('Flash fired, auto mode, red-eye reduction mode')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, auto mode, red-eye reduction mode, return light not detected'">
						<xsl:value-of select="string('Flash fired, auto mode, return light not detected, red-eye reduction mode')"/>
					</xsl:when>
					<xsl:when test="$flash='fired, auto mode, red-eye reduction mode, return light detected'">
						<xsl:value-of select="string('Flash fired, auto mode, return light detected, red-eye reduction mode')"/>
					</xsl:when>
				</xsl:choose>
			</flash>

			<!--  focalLength -->
			<focalLength>
		  		<xsl:value-of select="//mix:focalLength"/>
			</focalLength>

			<!--  flashEnergy -->
			<flashEnergy>
		  		<xsl:value-of select="//property[name='FlashEnergy']/values/value"/>
			</flashEnergy>
			
			<!--  exposureIndex -->
			<exposureIndex>
		  		<xsl:value-of select="//mix:exposureIndex"/>
			</exposureIndex>

			</image>
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
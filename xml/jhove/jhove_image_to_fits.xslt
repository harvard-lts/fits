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
			<xsl:variable name="format" select="//repInfo/format"/>
			
			<!-- byte order -->
			<byteOrder>
				<xsl:value-of select="//mix:byteOrder"/>
			</byteOrder>
		
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
							<xsl:value-of select="//mix:Compression/mix:compressionScheme"/>
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
					    <xsl:value-of select="//mix:colorSpace"/>

					</colorSpace>
				</xsl:otherwise>
			</xsl:choose>

			<!--  ReferenceBlackWhite -->
			<referenceBlackWhite>
				<xsl:for-each select="//mix:ReferenceBlackWhite/mix:Component">
					<xsl:value-of select="./mix:componentPhotometricInterpretation"/> Footroom <xsl:for-each select="mix:footroom"><xsl:call-template name="rational"/></xsl:for-each> Headroom <xsl:for-each select="mix:headroom"><xsl:call-template name="rational"/><xsl:text> </xsl:text></xsl:for-each>
				</xsl:for-each>
			</referenceBlackWhite>

			<!--  iccProfileName -->
			<iccProfileName>
		  		<xsl:value-of select="//mix:iccProfileName"/>
			</iccProfileName>
			
			<!--  YCbCrSubSampling -->
			<YCbCrSubSampling>
		  		<xsl:value-of select="//mix:YCbCrSubSampling/mix:yCbCrSubsampleHoriz"/>
		  		<xsl:value-of select="//mix:YCbCrSubSampling/mix:yCbCrSubsampleVert"/>
			</YCbCrSubSampling>
			
			<!--  YCbCrCoefficients -->
			<YCbCrCoefficients>
				<xsl:for-each select="//mix:YCbCrCoefficients/mix:lumaRed">Red <xsl:call-template name="rational"/><xsl:text> </xsl:text></xsl:for-each>
				<xsl:for-each select="//mix:YCbCrCoefficients/mix:lumaGreen">Green <xsl:call-template name="rational"/><xsl:text> </xsl:text></xsl:for-each>
				<xsl:for-each select="//mix:YCbCrCoefficients/mix:lumaBlue">Blue <xsl:call-template name="rational"/><xsl:text> </xsl:text></xsl:for-each>
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
				<xsl:value-of select="//mix:orientation"/>
			</orientation>
			
			<!-- samplingFrequencyUnit -->
			<samplingFrequencyUnit>
				<xsl:value-of select="//mix:samplingFrequencyUnit"/>
			</samplingFrequencyUnit>
			
			<!--  xSamplingFrequency -->
			<xSamplingFrequency>
				<xsl:for-each select="//mix:ySamplingFrequency"><xsl:call-template name="rational"/></xsl:for-each>
			</xSamplingFrequency>
			
			<!--  ySamplingFrequency -->
			<ySamplingFrequency>
				<xsl:for-each select="//mix:ySamplingFrequency"><xsl:call-template name="rational"/></xsl:for-each>
			</ySamplingFrequency>
			
			<!--  bitsPerSample -->
			<xsl:choose>
		  		<xsl:when test="//mix:BitsPerSample/mix:bitsPerSampleValue">
		  			<xsl:for-each select="//mix:BitsPerSample/mix:bitsPerSampleValue">
						<bitsPerSample>
					  		<xsl:value-of select="translate(.,',',' ')"/>
						</bitsPerSample>
					</xsl:for-each>
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
		  		<xsl:value-of select="//mix:WhitePoint/mix:whitePointXvalue"/>
			</whitePointXValue>

			<!--  whitePointYValue -->
			<whitePointYValue>
		  		<xsl:value-of select="//mix:WhitePoint/mix:whitePointYvalue"/>
			</whitePointYValue>
			
			<!--  primaryChromaticitiesRedX -->
			<primaryChromaticitiesRedX>
		  		<xsl:value-of select="//mix:PrimaryChromaticities/mix:primaryChromaticitiesRedX"/>
			</primaryChromaticitiesRedX>

			<!--  primaryChromaticitiesRedY -->
			<primaryChromaticitiesRedY>
		  		<xsl:value-of select="//mix:PrimaryChromaticities/mix:primaryChromaticitiesRedY"/>
			</primaryChromaticitiesRedY>
			
			<!--  primaryChromaticitiesGreenX -->
			<primaryChromaticitiesGreenX>
		  		<xsl:value-of select="//mix:PrimaryChromaticities/mix:primaryChromaticitiesGreenX"/>
			</primaryChromaticitiesGreenX>	

			<!--  primaryChromaticitiesGreenY -->
			<primaryChromaticitiesGreenY>
		  		<xsl:value-of select="//mix:PrimaryChromaticities/mix:primaryChromaticitiesGreenY"/>
			</primaryChromaticitiesGreenY>
			
			<!--  primaryChromaticitiesBlueX -->
			<primaryChromaticitiesBlueX>
		  		<xsl:value-of select="//mix:PrimaryChromaticities/mix:primaryChromaticitiesBlueX"/>
			</primaryChromaticitiesBlueX>	

			<!--  primaryChromaticitiesBlueY -->
			<primaryChromaticitiesBlueY>
		  		<xsl:value-of select="//mix:PrimaryChromaticities/mix:primaryChromaticitiesBlueY"/>
			</primaryChromaticitiesBlueY>
			
			<!--  imageProducer -->
			<imageProducer>
		  		<xsl:value-of select="//mix:imageProducer"/>
			</imageProducer>				
			
			<!--  scannerManufacturer -->
			<!--  Jhove reports digital camera models as 'scanners', so it's commented out
			<scannerManufacturer>
		  		<xsl:value-of select="//mix:scannerManufacturer"/>
			</scannerManufacturer>	
			 -->
			<!--  scannerModelName -->
			<!--  Jhove reports digital camera models as 'scanners', so it's commented out
			<scannerModelName>
		  		<xsl:value-of select="//mix:scannerModelName"/>
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
		  		<xsl:value-of select="//mix:fNumber"/>
			</fNumber>

			<!--  exposureTime -->
			<exposureTime>
		  		<xsl:value-of select="//mix:exposureTime"/>
			</exposureTime>
			
			<!--  brightnessValue -->
			<brightnessValue>
		  		<xsl:value-of select="//mix:brightnessValue"/>
			</brightnessValue>

			<!--  exposureBiasValue -->
			<exposureBiasValue>
		  		<xsl:value-of select="//mix:exposureBiasValue"/>
			</exposureBiasValue>

			<!--  subjectDistance -->
			<subjectDistance>
		  		<xsl:value-of select="//mix:SubjectDistance/mix:distance"/>
			</subjectDistance>

			<!--  meteringMode -->
			<meteringMode>
		  		<xsl:value-of select="//mix:meteringMode"/>
			</meteringMode>
			
			<!--  flash -->
			<flash>
				<xsl:value-of select="//mix:flash"/>
			</flash>

			<!--  focalLength -->
			<focalLength>
		  		<xsl:value-of select="//mix:focalLength"/>
			</focalLength>

			<!--  flashEnergy -->
			<flashEnergy>
		  		<xsl:value-of select="//mix:flashEnergy"/>
			</flashEnergy>
			
			<!--  exposureIndex -->
			<exposureIndex>
		  		<xsl:value-of select="//mix:exposureIndex"/>
			</exposureIndex>

			</image>
		</metadata>
	</fits>	

</xsl:template>

<xsl:template name="rational">
	<xsl:if test="mix:numerator">
		<xsl:choose>
			<xsl:when test="mix:denominator">
				<xsl:value-of select="mix:numerator"/><xsl:text>/</xsl:text><xsl:value-of select="mix:denominator"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="mix:numerator"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:if>
</xsl:template>
</xsl:stylesheet>
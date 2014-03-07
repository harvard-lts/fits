<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:mix="http://www.loc.gov/mix/">

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
		  		<xsl:when test="//mix:ByteOrder">
		  			<xsl:for-each select="//mix:ByteOrder">
						<byteOrder>
					  		<xsl:value-of select="replace(.,'-',' ')"/>
						</byteOrder>
					</xsl:for-each>
		  		</xsl:when>
		  		<xsl:otherwise>
					<byteOrder>
				  		<xsl:value-of select="//property[name='ByteOrder']/values/value"/>
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
							<xsl:variable name="compression" select="//mix:CompressionScheme"/>
							<xsl:choose>
								<xsl:when test="$compression=1">
									<xsl:value-of select="string('Uncompressed')"/>
								</xsl:when>							
								<xsl:when test="$compression=2">
									<xsl:value-of select="string('CCITT 1D')"/>
								</xsl:when>
								<xsl:when test="$compression=3">
									<xsl:value-of select="string('T4/Group 3 Fax')"/>
								</xsl:when>								
								<xsl:when test="$compression=4">
									<xsl:value-of select="string('T6/Group 4 Fax')"/>
								</xsl:when>
								<xsl:when test="$compression=5">
									<xsl:value-of select="string('LZW')"/>
								</xsl:when>							
								<xsl:when test="$compression=6">
									<xsl:value-of select="string('JPEG (old-style)')"/>
								</xsl:when>
								<xsl:when test="$compression=7">
									<xsl:value-of select="string('JPEG')"/>
								</xsl:when>		
								<xsl:when test="$compression=8">
									<xsl:value-of select="string('Adobe Deflate')"/>
								</xsl:when>	
								<xsl:when test="$compression=32661">
									<xsl:value-of select="string('JBIG')"/>
								</xsl:when>		
								<xsl:when test="$compression=32771">
									<xsl:value-of select="string('CCITTRLEW')"/>
								</xsl:when>	
								<xsl:when test="$compression=32773">
									<xsl:value-of select="string('PackBits')"/>
								</xsl:when>	
								<xsl:when test="$compression=32766">
									<xsl:value-of select="string('NeXT')"/>
								</xsl:when>	
								<xsl:when test="$compression=32809">
									<xsl:value-of select="string('ThunderScan')"/>
								</xsl:when>	
								<xsl:when test="$compression=32895">
									<xsl:value-of select="string('IT8CTPAD')"/>
								</xsl:when>	
								<xsl:when test="$compression=32896">
									<xsl:value-of select="string('IT8LW')"/>
								</xsl:when>	
								<xsl:when test="$compression=32897">
									<xsl:value-of select="string('IT8MP')"/>
								</xsl:when>	
								<xsl:when test="$compression=32898">
									<xsl:value-of select="string('IT8BL')"/>
								</xsl:when>	
								<xsl:when test="$compression=32908">
									<xsl:value-of select="string('PixarFilm')"/>
								</xsl:when>	
								<xsl:when test="$compression=32909">
									<xsl:value-of select="string('PixarLog')"/>
								</xsl:when>	
								<xsl:when test="$compression=32946">
									<xsl:value-of select="string('Deflate')"/>
								</xsl:when>	
								<xsl:when test="$compression=32947">
									<xsl:value-of select="string('DCS')"/>
								</xsl:when>	
								<xsl:when test="$compression=34676">
									<xsl:value-of select="string('SGILog')"/>
								</xsl:when>	
								<xsl:when test="$compression=34677">
									<xsl:value-of select="string('SGILog24')"/>
								</xsl:when>	
								<xsl:when test="$compression=34712">
									<xsl:value-of select="string('JPEG 2000')"/>
								</xsl:when>	
								<xsl:otherwise>
									<xsl:value-of select="$compression"/>
								</xsl:otherwise>
							</xsl:choose>	 
						</compressionScheme> 				
				</xsl:otherwise>
			</xsl:choose>

			 <!--  width -->
			<imageWidth>
		  		<xsl:value-of select="(//mix:ImageWidth)[1]"/> <!-- Take the first width found -->
			</imageWidth>
			
			<!--  height -->
			<imageHeight>
		  		<xsl:value-of select="(//mix:ImageLength)[1]"/> <!-- Take the first height found -->
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
						<xsl:variable name="colorspace" select="//mix:ColorSpace"/>
						<xsl:choose>
							<xsl:when test="$colorspace=0">
								<xsl:value-of select="string('WhiteIsZero')"/>
							</xsl:when>
							<xsl:when test="$colorspace=1">
								<xsl:value-of select="string('BlackIsZero')"/>
							</xsl:when>
							<xsl:when test="$colorspace=2">
								<xsl:value-of select="string('RGB')"/>
							</xsl:when>
							<xsl:when test="$colorspace=3">
								<xsl:value-of select="string('RGB Palette')"/>
							</xsl:when>
							<xsl:when test="$colorspace=4">
								<xsl:value-of select="string('Transparency Mask')"/>
							</xsl:when>
							<xsl:when test="$colorspace=5">
								<xsl:value-of select="string('CMYK')"/>
							</xsl:when>
							<xsl:when test="$colorspace=6">
								<xsl:value-of select="string('YCbCr')"/>
							</xsl:when>
							<xsl:when test="$colorspace=8">
								<xsl:value-of select="string('CIELab')"/>
							</xsl:when>
							<xsl:when test="$colorspace=9">
								<xsl:value-of select="string('ICCLab')"/>
							</xsl:when>	
							<xsl:when test="$colorspace=10">
								<xsl:value-of select="string('ITULab')"/>
							</xsl:when>		
							<xsl:when test="$colorspace=32803">
								<xsl:value-of select="string('Color Filter Array')"/>
							</xsl:when>	
							<xsl:when test="$colorspace=32844">
								<xsl:value-of select="string('Pixar LogL')"/>
							</xsl:when>	
							<xsl:when test="$colorspace=32845">
								<xsl:value-of select="string('Pixar LogLuv')"/>							
							</xsl:when>	
							<xsl:when test="$colorspace=34892">
								<xsl:value-of select="string('Linear Raw')"/>
							</xsl:when>																																																						
						</xsl:choose>
					</colorSpace>
				</xsl:otherwise>
			</xsl:choose>

			<!--  ReferenceBlackWhite -->
			<referenceBlackWhite>
		  		<xsl:value-of select="//mix:ReferenceBlackWhite"/>
			</referenceBlackWhite>

			<!--  iccProfileName -->
			<iccProfileName>
		  		<xsl:value-of select="//mix:ProfileName"/>
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
				<xsl:variable name="ycbcrpositioning" select="//mix:YCbCrPositioning"/>
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
				<xsl:variable name="orientation" select="//mix:Orientation"/>
				<xsl:choose>
					<xsl:when test="$orientation=1">
						<xsl:value-of select="string('normal*')"/>
					</xsl:when>
					<xsl:when test="$orientation=2">
						<xsl:value-of select="string('normal, image flipped')"/>
					</xsl:when>
					<xsl:when test="$orientation=3">
						<xsl:value-of select="string('normal, rotated 180°')"/>
					</xsl:when>
					<xsl:when test="$orientation=4">
						<xsl:value-of select="string('normal, image flipped, rotated 180°')"/>
					</xsl:when>
					<xsl:when test="$orientation=5">
						<xsl:value-of select="string('normal, image flipped, rotated cw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation=6">
						<xsl:value-of select="string('normal, rotated ccw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation=7">
						<xsl:value-of select="string('normal, image flipped, rotated ccw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation=8">
						<xsl:value-of select="string('normal, rotated cw 90°')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$orientation"/>
					</xsl:otherwise>
				</xsl:choose>
			</orientation>
			
			<!-- samplingFrequencyUnit -->
			<samplingFrequencyUnit>
				<xsl:variable name="sampFreqUnit" select="//mix:SamplingFrequencyUnit"/>
				<xsl:choose>
					<xsl:when test="$sampFreqUnit=1">
						<xsl:value-of select="string('no absolute unit of measurement')"/>
					</xsl:when>
					<xsl:when test="$sampFreqUnit=2">
						<xsl:value-of select="string('in.')"/>
					</xsl:when>
					<xsl:when test="$sampFreqUnit=3">
						<xsl:value-of select="string('cm')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$sampFreqUnit"/>
					</xsl:otherwise>
				</xsl:choose>
		  		
			</samplingFrequencyUnit>
			
			<!--  xSamplingFrequency -->
			<xSamplingFrequency>
		  		<xsl:value-of select="//mix:XSamplingFrequency"/>
			</xSamplingFrequency>
			
			<!--  ySamplingFrequency -->
			<ySamplingFrequency>
		  		<xsl:value-of select="//mix:YSamplingFrequency"/>
			</ySamplingFrequency>
			
			<!--  bitsPerSample -->
			<xsl:choose>
		  		<xsl:when test="//mix:BitsPerSampleValue">
		  			<xsl:for-each select="//mix:BitsPerSampleValue">
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
		  		<xsl:value-of select="//mix:SamplesPerPixel"/>
			</samplesPerPixel>		
			
			<!--  extraSamples -->
			<extraSamples>
				<xsl:variable name="extrasamples" select="//mix:extraSamples"/>
				<xsl:choose>
					<xsl:when test="extrasamples=0">
						<xsl:value-of select="string('unspecified')"/>
					</xsl:when>
					<xsl:when test="extrasamples=1">
						<xsl:value-of select="string('associated alpha')"/>
					</xsl:when>
					<xsl:when test="$extrasamples=2">
						<xsl:value-of select="string('unassociated alpha')"/>
					</xsl:when>
					<xsl:when test="$extrasamples=3">
						<xsl:value-of select="string('range or depth')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$extrasamples"/>
					</xsl:otherwise>
				</xsl:choose>
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
				<xsl:variable name="grayunit" select="//mix:grayResponseUnit"/>
				<xsl:choose>
					<xsl:when test="grayunit=0">
						<xsl:value-of select="string('')"/>
					</xsl:when>
					<xsl:when test="grayunit=1">
						<xsl:value-of select="string('tenths of a unit')"/>
					</xsl:when>
					<xsl:when test="$grayunit=2">
						<xsl:value-of select="string('hundredths of a unit')"/>
					</xsl:when>
					<xsl:when test="$grayunit=3">
						<xsl:value-of select="string('thousandths of a unit')"/>
					</xsl:when>
					<xsl:when test="$grayunit=4">
						<xsl:value-of select="string('ten-thousandths of a unit')"/>
					</xsl:when>
					<xsl:when test="$grayunit=5">
						<xsl:value-of select="string('hundred-thousandths of a unit')"/>
					</xsl:when>										
					<xsl:otherwise>
						<xsl:value-of select="$grayunit"/>
					</xsl:otherwise>
				</xsl:choose>			
			</grayResponseUnit>
			
			<!--  whitePointXValue -->
			<whitePointXValue>
		  		<xsl:value-of select="//mix:WhitePoint_Xvalue"/>
			</whitePointXValue>

			<!--  whitePointYValue -->
			<whitePointYValue>
		  		<xsl:value-of select="//mix:WhitePoint_Yvalue"/>
			</whitePointYValue>
			
			<!--  primaryChromaticitiesRedX -->
			<primaryChromaticitiesRedX>
		  		<xsl:value-of select="//mix:PrimaryChromaticities_RedX"/>
			</primaryChromaticitiesRedX>

			<!--  primaryChromaticitiesRedY -->
			<primaryChromaticitiesRedY>
		  		<xsl:value-of select="//mix:PrimaryChromaticities_RedY"/>
			</primaryChromaticitiesRedY>
			
			<!--  primaryChromaticitiesGreenX -->
			<primaryChromaticitiesGreenX>
		  		<xsl:value-of select="//mix:PrimaryChromaticities_GreenX"/>
			</primaryChromaticitiesGreenX>	

			<!--  primaryChromaticitiesGreenY -->
			<primaryChromaticitiesGreenY>
		  		<xsl:value-of select="//mix:PrimaryChromaticities_GreenY"/>
			</primaryChromaticitiesGreenY>
			
			<!--  primaryChromaticitiesBlueX -->
			<primaryChromaticitiesBlueX>
		  		<xsl:value-of select="//mix:PrimaryChromaticities_BlueX"/>
			</primaryChromaticitiesBlueX>	

			<!--  primaryChromaticitiesBlueY -->
			<primaryChromaticitiesBlueY>
		  		<xsl:value-of select="//mix:PrimaryChromaticities_BlueY"/>
			</primaryChromaticitiesBlueY>
			
			<!--  imageProducer -->
			<imageProducer>
		  		<xsl:value-of select="//mix:ImageProducer"/>
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
		  		<xsl:value-of select="//mix:ScannerModelNumber"/>
			</scannerModelNumber>
			
			<!--  scannerModelSerialNo -->
			<scannerModelSerialNo>
		  		<xsl:value-of select="//mix:ScannerModelSerialNo"/>
			</scannerModelSerialNo>
			
			<!--  scanningSoftwareName -->
			<scanningSoftwareName>
		  		<xsl:value-of select="//mix:ScanningSoftware"/>
			</scanningSoftwareName>
			
			<!--  scanningSoftwareVersionNo -->
			<scanningSoftwareVersionNo>
		  		<xsl:value-of select="//mix:ScanningSoftwareVersionNo"/>
			</scanningSoftwareVersionNo>

			<!--  digitalCameraModelName -->
			<digitalCameraModelName>
		  		<xsl:value-of select="//mix:DigitalCameraModelName"/>
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
		  		<xsl:value-of select="//mix:BrightnessValue"/>
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
				<xsl:variable name="meteringMode" select="//property[name='MeteringMode']/values/value"/>
				<xsl:choose>
					<xsl:when test="$meteringMode='average'">
						<xsl:value-of select="string('Average')"/>
					</xsl:when>
					<xsl:when test="$meteringMode='center weighted average'">
						<xsl:value-of select="string('Center weighted average')"/>
					</xsl:when>
					<xsl:when test="$meteringMode='centre weighted average'"> <!--  catch the typo -->
						<xsl:value-of select="string('Center weighted average')"/>
					</xsl:when>
					<xsl:when test="$meteringMode='spot'">
						<xsl:value-of select="string('Spot')"/>
					</xsl:when>		
					<xsl:when test="$meteringMode='multispot'">
						<xsl:value-of select="string('Multispot')"/>
					</xsl:when>		
					<xsl:when test="$meteringMode='pattern'">
						<xsl:value-of select="string('Pattern')"/>
					</xsl:when>		
					<xsl:when test="$meteringMode='partial'">
						<xsl:value-of select="string('Partial')"/>
					</xsl:when>		
				</xsl:choose>
		  		
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
		  		<xsl:value-of select="//mix:FocalLength"/>
			</focalLength>

			<!--  flashEnergy -->
			<flashEnergy>
		  		<xsl:value-of select="//property[name='FlashEnergy']/values/value"/>
			</flashEnergy>
			
			<!--  exposureIndex -->
			<exposureIndex>
		  		<xsl:value-of select="//mix:ExposureIndex"/>
			</exposureIndex>

			</image>
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
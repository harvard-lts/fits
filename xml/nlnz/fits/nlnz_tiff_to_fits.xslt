<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Tagged Image File Format')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:value-of select="TIFF/METADATA/TYPE" />
				</xsl:attribute>
			</identity>		
		</identification>
		
  		<fileinfo>
  			<!-- 
			<fslastmodified>
				<xsl:value-of select="//MODIFIED"/>
			</fslastmodified>
			<lastModified>
				<xsl:value-of select="//ELEMENT[NAME='DateTime']/VALUE"/>
			</lastModified>
			 -->
			<created>
				<xsl:choose>
					<xsl:when test="//EXIF/DATETIME/VALUE">
						<xsl:value-of select="//EXIF/DATETIME/VALUE"/>
					</xsl:when>
					<xsl:when test="//EXIF/DATETIMEDIGITIZED/VALUE">
						<xsl:value-of select="//EXIF/DATETIMEDIGITIZED/VALUE"/>
					</xsl:when>	
				</xsl:choose>	
			</created>
			<creatingApplicationName>
				<xsl:value-of select="//ELEMENT[NAME='Software']/VALUE"/>
			</creatingApplicationName>

		</fileinfo>
		
		<metadata>
		
		<image>
			<!-- compression -->
		    <compressionScheme>
				<xsl:variable name="compression" select="//ELEMENT[NAME='Compression']/VALUE"/>
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
	        
			<!-- width -->
			<imageWidth>
				<xsl:value-of select="//ELEMENT[NAME='ImageWidth']/VALUE"/>
	        </imageWidth>
			
			<!-- height -->
			<imageHeight>
				<xsl:value-of select="//ELEMENT[NAME='ImageLength']/VALUE"/>
			</imageHeight>
		   
		   <!-- colorspace -->
		   	<colorSpace>
					<xsl:variable name="colorspace" select="//ELEMENT[NAME='PhotometricInterpretation']/VALUE"/>
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
		    
		    <!-- orientation -->
			<orientation>
				<xsl:variable name="orientation" select="//ELEMENT[NAME='Orientation']/VALUE"/>
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
				    
		    <!-- resunit -->
	        <samplingFrequencyUnit>
	            <xsl:choose>
	              <xsl:when test="//ELEMENT[NAME='ResolutionUnit']/VALUE=1">
	              	<xsl:value-of select="string('no absolute unit of measurement')"/>
	              </xsl:when>
	              <xsl:when test="//ELEMENT[NAME='ResolutionUnit']/VALUE=2">
					<xsl:value-of select="string('in.')"/>
				  </xsl:when>
	              <xsl:when test="//ELEMENT[NAME='ResolutionUnit']/VALUE=3">
	              	<xsl:value-of select="string('cm')"/>
	              </xsl:when>
	              <xsl:otherwise>
	              	<xsl:value-of select="string('in.')"/>
	              </xsl:otherwise> <!-- default to 2 -->
	            </xsl:choose>
	        </samplingFrequencyUnit>

			<!-- xSamplingFrequency -->
			<xSamplingFrequency>
				<xsl:value-of select="//ELEMENT[NAME='XResolution']/VALUE"/>
		    </xSamplingFrequency>
			
			<!-- ySamplingFrequency -->
			<ySamplingFrequency>
				<xsl:value-of select="//ELEMENT[NAME='YResolution']/VALUE"/>
		    </ySamplingFrequency>
		    
			<!--  bits per sample -->
			<bitsPerSample>
				<xsl:for-each select="//ELEMENT[NAME='BitsPerSample']/VALUE">
					<xsl:value-of select="."/>
					<xsl:if test="position()!=last()">
		        		<xsl:value-of select="' '"/>
		       		</xsl:if>
				</xsl:for-each>
		   </bitsPerSample>
		   
			<!-- Samples per pixel -->
			<samplesPerPixel>
				<xsl:value-of select="//ELEMENT[NAME='SamplesPerPixel']/VALUE"/>
		    </samplesPerPixel>
		</image>
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
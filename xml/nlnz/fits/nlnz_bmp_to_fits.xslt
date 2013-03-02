<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Windows Bitmap')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:value-of select="BMP/METADATA/TYPE" />
				</xsl:attribute>
			</identity>		
		</identification>
		
		<fileinfo>
			<!-- 
			<fslastmodified>
				<xsl:value-of select="//MODIFIED"/>
			</fslastmodified>
			 -->
		</fileinfo>
		
		<metadata>
		<image>		
			<compressionScheme>
				<xsl:choose>
				<xsl:when test="BMP/BITMAPINFO/COMPRESSIONNAME">
					<xsl:value-of select="BMP/BITMAPINFO/COMPRESSIONNAME"/>
				</xsl:when>
				<xsl:when test="BMP/INFORMATION/COMPRESSION">
					<xsl:value-of select="BMP/INFORMATION/COMPRESSION"/>
				</xsl:when>
				</xsl:choose>
			</compressionScheme>
			
			<imageWidth>
				<xsl:choose>
					<xsl:when test="BMP/BITMAPINFO/WIDTH">
						<xsl:value-of select="BMP/BITMAPINFO/WIDTH"/>
					</xsl:when>
					<xsl:when test="BMP/INFORMATION/WIDTH">
						<xsl:value-of select="BMP/INFORMATION/WIDTH"/>
					</xsl:when>
				</xsl:choose>
			</imageWidth>
			<imageHeight>
				<xsl:choose>
					<xsl:when test="BMP/BITMAPINFO/HEIGHT">
						<xsl:value-of select="BMP/BITMAPINFO/HEIGHT"/>
					</xsl:when>
					<xsl:when test="BMP/INFORMATION/HEIGHT">
						<xsl:value-of select="BMP/INFORMATION/HEIGHT"/>
					</xsl:when>
				</xsl:choose>
			</imageHeight>			
			
			<samplingFrequencyUnit>
				<xsl:value-of select="BMP/BITMAPINFO/RESOLUTIONUNIT"/>
			</samplingFrequencyUnit>
			<xSamplingFrequency>
				<xsl:choose>
					<xsl:when test="BMP/INFORMATION/XRESOLUTION">
						<xsl:value-of select="BMP/INFORMATION/XRESOLUTION"/>
					</xsl:when>
					<xsl:when test="BMP/BITMAPINFO/XRESOLUTION">
						<xsl:value-of select="BMP/BITMAPINFO/XRESOLUTION"/>
					</xsl:when>
				</xsl:choose>
			</xSamplingFrequency>
			<ySamplingFrequency>
				<xsl:choose>
					<xsl:when test="BMP/INFORMATION/YRESOLUTION">
						<xsl:value-of select="BMP/INFORMATION/YRESOLUTION"/>
					</xsl:when>
					<xsl:when test="BMP/BITMAPINFO/YRESOLUTION">
						<xsl:value-of select="BMP/BITMAPINFO/YRESOLUTION"/>
					</xsl:when>
				</xsl:choose>
			</ySamplingFrequency>

          	<bitsPerSample>
				<xsl:choose>
					<xsl:when test="BMP/BITMAPINFO/BITCOUNT">
						<xsl:value-of select="BMP/BITMAPINFO/BITCOUNT"/>
					</xsl:when>
					<xsl:when test="BMP/INFORMATION/BITCOUNT">
						<xsl:value-of select="BMP/INFORMATION/BITCOUNT"/>
					</xsl:when>
				</xsl:choose>
          	</bitsPerSample>
		</image>
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
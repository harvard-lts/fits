<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exiftool_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">   
		<xsl:apply-imports/>
		
		<metadata>
		<text>
			<charset>
		  		<xsl:value-of select="exiftool/MIMEEncoding[1]"/>
			</charset>
			<linebreak>
				<xsl:value-of select="substring-after(exiftool/Newlines[1], ' ')"/>
			</linebreak>
			<lineCount>
				<xsl:value-of select="exiftool/LineCount[1]"/>
			</lineCount>
			<wordCount>
				<xsl:value-of select="exiftool/WordCount[1]"/>
			</wordCount>
			<xsl:if test="exiftool/FileType[1] != 'TXT' and exiftool/FileType[1] != 'CSV'">
				<markupBasis>
					<xsl:value-of select="exiftool/FileType[1]"/>
				</markupBasis>
			</xsl:if>
		</text>
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
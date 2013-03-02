<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exiftool_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">   
		<xsl:apply-imports/>
		
		<metadata>
		<text>
			<charset>
		  		<xsl:value-of select="substring-after(exiftool/ContentType,'charset=')"/>
			</charset>
			<markupBasis>
				<xsl:value-of select="exiftool/FileType"/>
			</markupBasis>
		</text>				
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
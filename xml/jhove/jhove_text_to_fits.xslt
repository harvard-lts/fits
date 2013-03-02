<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:mix="http://www.loc.gov/mix/">

<xsl:import href="jhove_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">   
		<xsl:apply-imports/>

		<metadata>
			<text>
				<xsl:for-each select="//property[name='LineEndings']/values/value">
					<linebreak>
						<xsl:choose>
							<xsl:when test=".='CRLF'">
								<xsl:value-of select="string('CR/LF')"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="."/>
							</xsl:otherwise>
						</xsl:choose>
					</linebreak>
				</xsl:for-each>
				<charset>
					<xsl:value-of select="substring-after(repInfo/mimeType,'=')"/>
              	</charset>
              	<markupBasisVersion>
              		<xsl:value-of select="repInfo/version"/>
              	</markupBasisVersion>
			</text>
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
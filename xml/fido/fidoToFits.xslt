<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
	<xsl:variable name="identificationStatus" select="//correctlyIdentified" />
	<xsl:choose>
		<xsl:when test="$identificationStatus='true'">
			<xsl:variable name="mimeType" select="//mimeType" />
			<xsl:variable name="puid" select="//puid" />
			<xsl:variable name="formatName" select="//formatName" />
			<xsl:variable name="signatureName" select="//signatureName" />
			<identification>
				<identity>
					<xsl:attribute name="format">
						<xsl:value-of select="$formatName"/>
					</xsl:attribute>
					<xsl:attribute name="mimetype">
						<xsl:value-of select="$mimeType"/>
					</xsl:attribute> 
					<externalIdentifier type="puid">
						<xsl:value-of select="$puid"/>
					</externalIdentifier>
				</identity>
	    		</identification>
		</xsl:when>
	</xsl:choose>
    </fits>
  </xsl:template>
</xsl:stylesheet>

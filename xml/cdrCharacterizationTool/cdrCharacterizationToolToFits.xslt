<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
  <xsl:variable name="valid" select="//valid"/>
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
	<filestatus>
		<well-formed><xsl:value-of select="$valid"/></well-formed>
		<valid><xsl:value-of select="$valid"/></valid>
	</filestatus>
	<metadata>
		<document>
		<xsl:for-each select="//item">
			<xsl:variable name="field" select="key"></xsl:variable>

			<xsl:element name="{$field}">
     				<xsl:value-of select="value"/>
			</xsl:element>
		</xsl:for-each>
		</document>
	</metadata>
    </fits>
  </xsl:template>
</xsl:stylesheet>

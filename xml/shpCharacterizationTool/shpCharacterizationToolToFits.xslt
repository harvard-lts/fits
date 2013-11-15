<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
  <xsl:variable name="valid" select="//valid"/>
  <xsl:variable name="version" select="//identificationInfo/version"/>
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
	<identification>
		<identity>
			<xsl:choose>
				<xsl:when test="$version">
					<version>
						<xsl:value-of select="$version"/>
					</version>
				</xsl:when>
			</xsl:choose>
		</identity>
	</identification>
	<filestatus>
		<well-formed><xsl:value-of select="$valid"/></well-formed>
		<valid><xsl:value-of select="$valid"/></valid>
	</filestatus>
	<metadata>
		<document>
		<xsl:for-each select="//fileInfo/*">
			<xsl:variable name="field" select="name(.)"></xsl:variable>
			<xsl:element name="{$field}">
     				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:for-each>
		<xsl:for-each select="//recordStats/*">
			<xsl:variable name="field" select="name(.)"></xsl:variable>
			<xsl:element name="{$field}">
     				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:for-each>
		</document>
	</metadata>
    </fits>
  </xsl:template>
</xsl:stylesheet>

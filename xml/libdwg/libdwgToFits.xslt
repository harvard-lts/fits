<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
 
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
    <xsl:choose>
	    <xsl:when test="//valid"> 
		     <xsl:variable name="valid" select="//valid"/>
		     <xsl:variable name="validationError" select="//validationError"/>
			<filestatus>
				<well-formed><xsl:value-of select="$valid"/></well-formed>
				<valid><xsl:value-of select="$valid"/></valid>
				<message>
					<xsl:value-of select="$validationError"/>				
				</message>
			</filestatus>
		</xsl:when>
	</xsl:choose>
	<metadata>
		<document>
		<xsl:for-each select="//meta/*/*">
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

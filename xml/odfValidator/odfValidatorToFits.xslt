<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
  <xsl:variable name="valid" select="//valid"/>
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<filestatus>
			<well-formed><xsl:value-of select="$valid"/></well-formed>
			<valid><xsl:value-of select="$valid"/></valid>
		</filestatus>
    </fits>
  </xsl:template>
</xsl:stylesheet>

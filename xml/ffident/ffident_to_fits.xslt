<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<xsl:for-each select="ffidentOutput">
				<identity>
					<!-- format and mimetype -->
					<xsl:attribute name="format">
						<xsl:value-of select="longName" />
		 			</xsl:attribute> 
					<xsl:attribute name="mimetype">
						<xsl:value-of select="mimetypes/mimetype" />
		 			</xsl:attribute> 			
				</identity>
			</xsl:for-each>
	    </identification>
    </fits>
  </xsl:template>
</xsl:stylesheet>
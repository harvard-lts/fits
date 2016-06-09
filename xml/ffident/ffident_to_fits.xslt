<?xml version="1.0" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
        
		<identification>
			<xsl:for-each select="ffidentOutput">
    
                <xsl:variable name="mime" select="mimetypes/mimetype" />
                <xsl:variable name="format" select="longName" />

				<identity>
					<!-- format and mimetype -->
					<xsl:attribute name="format">
                        <xsl:choose>
                            <xsl:when test="$format='Rich Text Format'">
                                <xsl:value-of select="string('Rich Text Format (RTF)')"/>
                            </xsl:when>
                            <xsl:when test="$format='Microsoft Word Document'">
                                <xsl:value-of select="string('Microsoft Word Binary File Format')"/>
                            </xsl:when>
                            <xsl:otherwise>
								<xsl:value-of select="$format" />
                            </xsl:otherwise>
                        </xsl:choose>
		 			</xsl:attribute> 
					<xsl:attribute name="mimetype">
					    <xsl:choose>
					        <xsl:when test="$mime='text/rtf'">
                                <xsl:value-of select="string('application/rtf')"/>
                            </xsl:when>
                            <xsl:otherwise>
        						<xsl:value-of select="$mime" />
                            </xsl:otherwise>
					    </xsl:choose>
		 			</xsl:attribute> 			
				</identity>
			</xsl:for-each>
	    </identification>
    </fits>
  </xsl:template>
</xsl:stylesheet>
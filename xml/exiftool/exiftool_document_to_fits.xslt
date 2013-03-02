<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exiftool_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">  
		<xsl:apply-imports/>
		
		<metadata>
		<document>
			<xsl:variable name="mime" select="exiftool/MIMEType"/>
			
			<pageCount>
				<xsl:if test="$mime!='application/msword'">
					<xsl:value-of select="exiftool/PageCount"/>
				</xsl:if>
			</pageCount>		
			
			<title>
				<xsl:value-of select="exiftool/Title"/>
			</title>	

			<author>
				<xsl:value-of select="exiftool/Author"/>
			</author>
			
			<isRightsManaged>
				<xsl:choose>
					<xsl:when test="exiftool/Rights or exiftool/xmpRights">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>
			</isRightsManaged>

			<isProtected>
				<xsl:choose>
					<xsl:when test="exiftool/Encryption">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>
			</isProtected>
			
		</document>				
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
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
				<xsl:choose>
					<!-- different file types will output a different element name -->
					<xsl:when test="exiftool/Pages">
						<xsl:value-of select="exiftool/Pages"/>
					</xsl:when>
					<xsl:when test="exiftool/PageCount">
						<xsl:value-of select="exiftool/PageCount"/>
					</xsl:when>
				</xsl:choose>
			</pageCount>

			<wordCount>
				<xsl:value-of select="exiftool/Words"/>
			</wordCount>
			
			<characterCount>
				<xsl:value-of select="exiftool/Characters"/>
			</characterCount>
			
			<title>
				<xsl:value-of select="exiftool/Title"/>
			</title>	

			<author>
				<xsl:value-of select="exiftool/Creator"/>
			</author>
			
			<lineCount>
				<xsl:value-of select="exiftool/Lines"/>
			</lineCount>
			
			<paragraphCount>
				<xsl:value-of select="exiftool/Paragraphs"/>
			</paragraphCount>
			
			<isRightsManaged>
				<xsl:choose>
					<xsl:when test="exiftool/Rights or exiftool/xmpRights">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
				</xsl:choose>
			</isRightsManaged>

			<xsl:if test="exiftool/Encryption">
				<isEncrypted>
	    			<xsl:value-of select="string('yes')"/>
				</isEncrypted>
			</xsl:if>
			
			<xsl:variable name="security" select="exiftool/Security"/>
			<isProtected>
				<xsl:choose>
					<xsl:when test="$security='Password protected'">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
				</xsl:choose>
			</isProtected>
			
			<!-- outputs integer as subject when PDF -->
			<xsl:if test="exiftool/MIMEType!='application/pdf'">
				<subject>
					<xsl:value-of select="exiftool/Subject"/>
				</subject>
			</xsl:if>
			
			<category>
				<xsl:value-of select="exiftool/Category"/>
			</category>
			
			<hyperlinks>
				<xsl:value-of select="exiftool/Hyperlinks"/>
			</hyperlinks>
			
		</document>				
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
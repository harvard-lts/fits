<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="jhove_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">  
	
		<xsl:apply-imports/>
		
		<metadata>
			<document>
			
			<title>
				<xsl:value-of select="//property[name='Title']/values/value"/>
			</title>
			<author>
				<xsl:value-of select="//property[name='Author']/values/value"/>
			</author>
			<language>
				<xsl:value-of select="//property[name='Language']/values/value"/>
			</language>	
			
			<pageCount>
				<xsl:variable name="pageCount" select="count(//property[name='Pages']/values/property[name='Page'])"/>
				<xsl:if test="$pageCount > 0">
					<xsl:value-of select="$pageCount"/>
				</xsl:if>
			</pageCount>
			
				
			<isTagged>
				<xsl:choose>
					<xsl:when test="//profiles[profile='Tagged PDF']">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>
			</isTagged>
			<hasOutline>
				<xsl:choose>
					<xsl:when test="//property[name='Outlines']">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>
			</hasOutline>			
			<hasAnnotations>
				<xsl:choose>
					<xsl:when test="//property[name='Annotations']">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>
			</hasAnnotations>				
			</document>
		</metadata>

	</fits>	

</xsl:template>
</xsl:stylesheet>
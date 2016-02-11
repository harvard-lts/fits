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
                    <xsl:when test="exiftool/Document-statisticPage-count">
                        <xsl:value-of select="exiftool/Document-statisticPage-count"/>
                    </xsl:when>
				</xsl:choose>
			</pageCount>

			<wordCount>
                <xsl:choose>
	                <!-- different file types will output a different element name -->
	                <xsl:when test="exiftool/Words">
						<xsl:value-of select="exiftool/Words"/>
	                </xsl:when>
                    <xsl:when test="exiftool/Document-statisticWord-count">
		                <xsl:value-of select="exiftool/Document-statisticWord-count"/>
                    </xsl:when>
                </xsl:choose>
			</wordCount>
			
			<characterCount>
                <xsl:choose>
                    <!-- different file types will output a different element name -->
                    <xsl:when test="exiftool/Characters">
						<xsl:value-of select="exiftool/Characters"/>
                    </xsl:when>
                    <xsl:when test="exiftool/Document-statisticCharacter-count">
                        <xsl:value-of select="exiftool/Document-statisticCharacter-count"/>
                    </xsl:when>
                </xsl:choose>
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
                <xsl:choose>
                    <!-- different file types will output a different element name -->
                    <xsl:when test="exiftool/Paragraphs">
                        <xsl:value-of select="exiftool/Paragraphs"/>
                    </xsl:when>
                    <xsl:when test="exiftool/Document-statisticParagraph-count">
                        <xsl:value-of select="exiftool/Document-statisticParagraph-count"/>
                    </xsl:when>
                </xsl:choose>
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
            
            <subject>
                <xsl:value-of select="exiftool/Subject"/>
            </subject>
            
            <category>
                <xsl:value-of select="exiftool/Category"/>
            </category>
            
            <hyperlinks>
                <xsl:value-of select="exiftool/Hyperlinks"/>
            </hyperlinks>
			
			<category>
				<xsl:value-of select="exiftool/Category"/>
			</category>
			
			<xsl:if test="exiftool/Document-statisticObject-count and exiftool/Document-statisticObject-count != '0'">
			    <hasEmbeddedResources>
			         <xsl:value-of select="string('yes')"/>
			    </hasEmbeddedResources>
			</xsl:if>
			
			<tableCount>
			    <xsl:value-of select="exiftool/Document-statisticTable-count" />
			</tableCount>

			<graphicsCount>
			    <xsl:value-of select="exiftool/Document-statisticImage-count" />
			</graphicsCount>
            
            <description>
                <xsl:value-of select="exiftool/Description" />
            </description>
            
            <identifier>
                <xsl:value-of select="exiftool/Identifier" />
            </identifier>
            
            <source>
                <xsl:value-of select="exiftool/Source" />
            </source>
		</document>				
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
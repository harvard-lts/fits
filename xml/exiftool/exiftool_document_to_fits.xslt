<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
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

			<isProtected>
				<xsl:choose>
					<xsl:when test="exiftool/Security and exiftool/Security='Password protected'">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:when test="exiftool/Encryption">
                        <xsl:value-of select="string('yes')"/>
					</xsl:when>
				</xsl:choose>
			</isProtected>
            
            <subject>
                <!-- sometimes for PDF files Exiftool will spit out an integer value which is not a real Subject -->
                <xsl:if test="exiftool/Subject and string-length(exiftool/Subject) > 1">
	                <xsl:value-of select="exiftool/Subject"/>
                </xsl:if>
            </subject>
            
            <category>
                <xsl:value-of select="exiftool/Category"/>
            </category>
            
            <xsl:if test="exiftool/Hyperlinks">
                <hasHyperlinks>
                    <xsl:value-of select="string('yes')"/>
                </hasHyperlinks>
            </xsl:if>
			
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
            
            <language>
                <xsl:value-of select="exiftool/Language" />
            </language>
            
            <isTagged>
                <xsl:if test="exiftool/TaggedPDF and exiftool/TaggedPDF = 'Yes'">
	                <xsl:value-of select="string('yes')"/>
                </xsl:if>
            </isTagged>
		</document>				
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
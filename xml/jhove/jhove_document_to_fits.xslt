<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="jhove_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">  
	
		<xsl:apply-imports/>
		
		<metadata>
			<document>
			
			<title>
				 <xsl:choose>
                    <xsl:when test="//property[name='Info']/values/property['Title']/values/value and //property[name='Info']/values/property['Title']/values/value != '&lt;May be encrypted&gt;'">
						<xsl:value-of select="//property[name='Info']/values/property[name='Title']/values/value"/>
					</xsl:when>
					<xsl:otherwise>
					<xsl:if test="//property[name='Title']/values/value = '&lt;May be encrypted&gt;'" >
					    <xsl:text></xsl:text>
					    </xsl:if>
					</xsl:otherwise>
			    </xsl:choose>
			</title>
			<author>
			    <xsl:choose>
					<xsl:when test="//property[name='Author']/values/value and //property[name='Author']/values/value != '&lt;May be encrypted&gt;'">
						<xsl:value-of select="//property[name='Author']/values/value"/>
					</xsl:when>
					<xsl:otherwise>
					 <xsl:if test="//property[name='Author']/values/value = '&lt;May be encrypted&gt;'" >
					    <xsl:text></xsl:text>
					    </xsl:if>
					</xsl:otherwise>
			    </xsl:choose>
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
				<xsl:if test="//profiles[profile='Tagged PDF']">
					<xsl:value-of select="string('yes')"/>
				</xsl:if>
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
			<graphicsCount>
                <xsl:variable name="graphicsCount" select="count(//property[name='Images']/values/property[name='Image'])"/>
                <xsl:if test="$graphicsCount > 0">
                    <xsl:value-of select="$graphicsCount"/>
                </xsl:if>
			</graphicsCount>

			<!-- De-duplication of fonts. First create collection of nodes of font names with tag prefixes stripped off (if they exist). These can added into PDF's. -->
			<!-- Note: It may be possible to have the same font names with different tag prefixes. -->
			<xsl:variable name="fontNames" as="element()*">
			    <xsl:for-each select="//property[name='Fonts']//property[name='FontName']/values/value[1]">
			        <xsl:element name="anElement">
			            <xsl:variable name="fontText" select="./text()" />
			            <xsl:value-of select="if (contains($fontText,'+')) then replace($fontText, '[A-Z]+\+', '') else $fontText"/>
			        </xsl:element>
			    </xsl:for-each>
			</xsl:variable>

            <!-- Iterate all unique font names in sorted order. -->
            <xsl:for-each select="distinct-values($fontNames/text())">
                <xsl:sort select="." />
       			<font>
			        <fontName>
	                    <xsl:value-of select="."/>
			        </fontName>
       			</font>
		    </xsl:for-each>
			</document>
		</metadata>

	</fits>	

</xsl:template>
</xsl:stylesheet>
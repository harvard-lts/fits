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
				 <xsl:choose>
                    <xsl:when test="//property[name='Info']/values/property['Title']/values/value and //property[name='Info']/values/property['Title']/values/value != '&lt;May be encrypted&gt;'">
						<xsl:value-of select="//property[name='Info']/values/property['Title']/values/value"/>
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
			<graphicsCount>
                <xsl:variable name="graphicsCount" select="count(//property[name='Images']/values/property[name='Image'])"/>
                <xsl:if test="$graphicsCount > 0">
                    <xsl:value-of select="$graphicsCount"/>
                </xsl:if>
			</graphicsCount>
			
			<!-- fonts -->
            <xsl:variable name="fonts" select="//property[name='Fonts']//property[name='FontName']"/>
<!-- 			<xsl:variable name="fonts" select="count(//property[name='Fonts']/values/property/values/property[name='Font'])"/> -->
<!--             <xsl:if test="$fonts"> -->
			    <xsl:for-each select="$fonts">
        			<font>
<!-- 				        <xsl:variable name="cur_font" select=".//property[name='FontName']/values/value/text()" /> -->
<!--                         <xsl:variable name="cur_font" select="./values/property[name='BaseFont']/values/value/text()" /> -->
<!--                         <xsl:value-of select="string($cur_font)"/> -->
				        <fontName>
		                    <xsl:value-of select="string(./values/value/text())"/>
				        </fontName>
				        <fontIsEmbedded>
				            <xsl:value-of select="string('false')" />
				        </fontIsEmbedded>
        			</font>
			    </xsl:for-each>
<!--             </xsl:if> -->
			</document>
		</metadata>

	</fits>	

</xsl:template>
</xsl:stylesheet>
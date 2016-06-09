<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Microsoft Word Binary File Format')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:if test="//METADATA/TYPE='application/ms-word'">
						<xsl:value-of select="string('application/msword')" />
					</xsl:if>
				</xsl:attribute>
			</identity>		
		</identification>
		
 		<fileinfo>
			<created>
				<xsl:value-of select="//CREATED"/>
			</created>

            <xsl:if test="//APPLICATION!='null'">
				<creatingApplicationName>
					<xsl:value-of select="//APPLICATION"/>
				</creatingApplicationName>
            </xsl:if>
			
		</fileinfo>
		
		<metadata>	
		<document>
		
			<pageCount>
				<xsl:value-of select="//PAGES"/>
			</pageCount>

			<wordCount>
				<xsl:value-of select="//WORDS"/>
			</wordCount>
			
			<characterCount>
				<xsl:value-of select="//CHARACTERS"/>
			</characterCount>
			
			<xsl:if test="//TITLE and //TITLE!='null'">
				<title>
					<xsl:value-of select="//TITLE"/>
				</title>
			</xsl:if>
            
            <xsl:if test="//SUBJECT and //SUBJECT!='null'">
	            <subject>
	                <xsl:value-of select="//SUBJECT"/>
	            </subject>
            </xsl:if>
			
			<author>
				<xsl:value-of select="//AUTHOR"/>
			</author>
			
			<language>
				<xsl:value-of select="//LANG"/>
			</language>
            
            <xsl:if test="//HASPICTURES and //HASPICTURES='true'">
	            <hasPictures>
	                <xsl:value-of select="string('yes')"/>
	            </hasPictures>
            </xsl:if>
            
            <xsl:if test="//ISENCRYPTED and //ISENCRYPTED='true'">
	            <isProtected>
	                <xsl:value-of select="string('yes')" />
	            </isProtected>
            </xsl:if>

		</document>	
		</metadata>
	</fits>	

</xsl:template>


</xsl:stylesheet>
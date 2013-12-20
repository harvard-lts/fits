<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Microsoft Word Document')" />
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

			<creatingApplicationName>
				<xsl:value-of select="//APPLICATION"/>
			</creatingApplicationName>
			
			<creatingApplicationVersion>
				<xsl:value-of select="//PRODUCTVERSION"/>
			</creatingApplicationVersion>
			
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
							
			<title>
				<xsl:value-of select="//TITLE"/>
			</title>
			
			<author>
				<xsl:value-of select="//AUTHOR"/>
			</author>			
			
			<language>
				<xsl:value-of select="//LANG"/>
			</language>		

		</document>	
		</metadata>
	</fits>	

</xsl:template>


</xsl:stylesheet>
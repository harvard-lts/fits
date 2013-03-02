<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:mix="http://www.loc.gov/mix/">

<xsl:import href="jhove_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">   
		<xsl:apply-imports/>

		<metadata>
			<text>
				<linebreak>
					<xsl:value-of select="//property[name='LineEndings']/values/value"/>
				</linebreak>
			  	<charset>
		  			<xsl:value-of select="//property[name='Encoding']/values/value"/>
              	</charset>
              	<markupBasis>
              		<xsl:value-of select="repInfo/format"/>
              	</markupBasis>
              	<markupBasisVersion>
              		<xsl:value-of select="repInfo/version"/>
              	</markupBasisVersion>
              	<!-- 
              	<xsl:for-each select="//property[name='Schema']/values">
              		<markupLanguage>
              			<xsl:value-of select="property[name='NamespaceURI']/values/value"/>
              		</markupLanguage>
              		<markupLanguageLocation>
              			<xsl:value-of select="property[name='SchemaLocation']/values/value"/>
              		</markupLanguageLocation>
              	</xsl:for-each>
              	 -->
			</text>
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
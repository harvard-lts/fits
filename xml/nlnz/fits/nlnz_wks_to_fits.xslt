<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Microsoft Works')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:value-of select="//METADATA/TYPE" />
				</xsl:attribute>
			</identity>		
		</identification>
		
 		<fileinfo>
			<creatingApplicationName>
				<xsl:value-of select="//PACKAGE"/>
			</creatingApplicationName>
						
		</fileinfo>
		
	</fits>	

</xsl:template>


</xsl:stylesheet>
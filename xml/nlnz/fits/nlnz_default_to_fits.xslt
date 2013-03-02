<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fits_XsltFunctions="edu.harvard.hul.ois.fits.tools.utils.XsltFunctions">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Unknown Binary')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:value-of select="string('application/octet-stream')" />			
				</xsl:attribute>
			</identity>		
		</identification>

	</fits>	

</xsl:template>


</xsl:stylesheet>
<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:mix="http://www.loc.gov/mix/">

<xsl:import href="jhove_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">  
		<xsl:apply-imports/>

		<metadata>

		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
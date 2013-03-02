<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Graphics Interchange Format')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:value-of select="GIF/METADATA/TYPE" />
				</xsl:attribute>
				<version>
	        		<xsl:value-of select="GIF/VERSION"/>
	        	</version>
			</identity>		
		</identification>
		
 		<fileinfo>
 		<!-- 
			<fslastmodified>
				<xsl:value-of select="//MODIFIED"/>
			</fslastmodified>
		 -->
		</fileinfo>
		
		<metadata>	
		<image>
			<!-- 
			<compressionScheme>
				<xsl:value-of select="IMAGE-INFO/COMPRESSED"/>
			</compressionScheme>
			 -->
	        <imageWidth>
	        	<xsl:value-of select="GIF/SCREEN-WIDTH"/>
			</imageWidth>
	        <imageHeight>
	        	<xsl:value-of select="GIF/SCREEN-HEIGHT"/>
			</imageHeight>
			
			<!-- 
	        <bitsPerSample>
	        	<xsl:value-of select="GIF/BITS-PER-PIXEL"/>
	        </bitsPerSample>
	         -->
		</image>
	
		</metadata>
	</fits>	

</xsl:template>


</xsl:stylesheet>
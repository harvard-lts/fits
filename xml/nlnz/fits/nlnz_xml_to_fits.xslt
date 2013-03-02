<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Extensible Markup Language')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:if test="XML/METADATA/TYPE='application/xml'">
		        		<xsl:value-of select="'text/xml'"/>
		       		</xsl:if>

				</xsl:attribute>
				
				<version>
	        		<xsl:value-of select="XML/INFORMATION/VERSION"/>
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
		
		<text>
								    						
			<xsl:if test="XML/INFORMATION/ENCODING!='unspecified'">									
	            <charset>
	              <xsl:value-of select="XML/INFORMATION/ENCODING"/>
	            </charset>
	        </xsl:if>
            
            <markupBasis>
            	<xsl:value-of select="substring-after(XML/METADATA/TYPE,'/')"/>
            </markupBasis>
            
            <markupBasisVersion>
            	<xsl:value-of select="XML/INFORMATION/VERSION"/>
            </markupBasisVersion>
		</text>
	
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
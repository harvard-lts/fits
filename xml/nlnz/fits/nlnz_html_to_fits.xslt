<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Hypertext Markup Language')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
		        	<xsl:value-of select="HTML/METADATA/TYPE"/>
				</xsl:attribute>
				
				<xsl:if test="not(string-length(HTML/HTML-META/VERSION) = 0)">
					<version>
		        		<xsl:value-of select="HTML/HTML-META/VERSION"/>
		        	</version>
		        </xsl:if>
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
            <charset>
              <xsl:value-of select="HTML/HTML-META/CHARSET"/>
            </charset>
            
            <markupBasis>
            	<xsl:value-of select='substring-after(HTML/METADATA/TYPE,"/")'/>
            </markupBasis>
            
           	<markupBasisVersion>
	        	<xsl:value-of select="HTML/HTML-META/VERSION"/>
	        </markupBasisVersion>
            
            <!-- 
            <http-equiv-type>
              <xsl:value-of select="HTML/HTML-META/HTTP-EQUIV-TYPE"/>
            </http-equiv-type>
            <sub-type>
            	<xsl:value-of select="HTML/HTML-META/SUB-TYPE"/>
            </sub-type>
            <strict>
            	<xsl:value-of select="HTML/HTML-META/STRICT"/>
            </strict>
            <dtd>
            	<xsl:value-of select="HTML/HTML-META/DTD"/>
            </dtd>
            <title>
            	<xsl:value-of select="HTML/HTML-META/TITLE"/>
            </title>
            <characters>
            	<xsl:value-of select="HTML/HTML-META/CHARACTERS"/>
            </characters>
            <words>
            	<xsl:value-of select="HTML/HTML-META/WORDS"/>
            </words>
            <paragraphs>
            	<xsl:value-of select="HTML/HTML-META/PARAGRAPHS"/>
            </paragraphs>
             -->
		</text>
		</metadata>
	</fits>	

</xsl:template>


</xsl:stylesheet>
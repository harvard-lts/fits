<xsl:stylesheet version="2.0"
      
   xmlns:dc="http://purl.org/dc/elements/1.1/" 
   xmlns:ebucore="urn:ebu:metadata-schema:ebuCore_2014"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xmlns:xalan="http://xml.apache.org/xalan"

   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   	
<xsl:template match="/">
    <wrapper>
      	    <xsl:copy-of select="//ebucore:ebuCoreMain/ebucore:coreMetadata/ebucore:format" />
    </wrapper>	
</xsl:template>
</xsl:stylesheet>

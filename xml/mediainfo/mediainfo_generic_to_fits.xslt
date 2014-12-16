<xsl:stylesheet version="2.0"
      
   xmlns:dc="http://purl.org/dc/elements/1.1/" 
   xmlns:ebucore="urn:ebu:metadata-schema:ebuCore_2014" 
   xmlns:xalan="http://xml.apache.org/xalan" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xsi:schemaLocation="urn:ebu:metadata-schema:ebuCore_2014 http://www.ebu.ch/metadata/schemas/EBUCore/20140318/EBU_CORE_20140318.xsd"
   
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
<!--   
<ebucore:ebuCoreMain xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:ebucore="urn:ebu:metadata-schema:ebuCore_2014" xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:ebu:metadata-schema:ebuCore_2014 http://www.ebu.ch/metadata/schemas/EBUCore/20140318/EBU_CORE_20140318.xsd" version="1.5" dateLastModified="2014-12-01" timeLastModified="20:42:34Z">
-->

<xsl:output indent="yes"/>  
   	
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">  

		<metadata>
			<video>
						
			    <format>
       				<xsl:value-of select="//ebucore:ebuCoreMain/ebucore:coreMetadata/ebucore:format/ebucore:videoFormat/@videoFormatName"/>			             			
       			</format>			
			
			</video>
		</metadata>
	</fits>				

</xsl:template>

<!--
</ebucore:ebuCoreMain>
-->

</xsl:stylesheet>

<!--
<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="mediainfo_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">   

		<xsl:apply-imports/>
		
	</fits>	

</xsl:template>


</xsl:stylesheet>
-->
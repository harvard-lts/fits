<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Portable Document Format')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:value-of select="//METADATA/TYPE" />
				</xsl:attribute>
				<version>
	        		<xsl:value-of select="//PDF-META/VERSION"/>
	        	</version>
			</identity>		
		</identification>
		
 		<fileinfo>
 			<!-- Not as precise as other tools 
			<created>
				<xsl:value-of select="//CREATION-DATE/DATE"/>
			</created>
			-->
			<creatingApplicationName>
				<xsl:if test="//PRODUCER and //CREATOR">
					<xsl:value-of select="concat(//PRODUCER,'/',//CREATOR)"/>
				</xsl:if>
			</creatingApplicationName>
			
		</fileinfo>
		
		<metadata>	
		<document>
			<title>
				<xsl:value-of select="//TITLE"/>
			</title>
			
			<author>
				<xsl:value-of select="//AUTHOR"/>
			</author>			
			
			<language>
				<xsl:value-of select="//LANGUAGE"/>
			</language>		
			
			<isTagged>
				<xsl:choose>
					<xsl:when test="//TAGGED='true'">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>
			</isTagged>
			
			<hasOutline>
				<xsl:choose>
					<xsl:when test="//HAS-OUTLINE='true'">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>
			</hasOutline>
			
			<hasForms>
				<xsl:choose>
					<xsl:when test="//HAS-FORMS='true'">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>
			</hasForms>
			
			<isProtected>
				<xsl:choose>
					<xsl:when test="//ENCRYPTED='true'">
						<xsl:value-of select="string('yes')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="string('no')"/>
					</xsl:otherwise>
				</xsl:choose>				
			</isProtected>
		</document>	
		</metadata>
	</fits>	

</xsl:template>


</xsl:stylesheet>
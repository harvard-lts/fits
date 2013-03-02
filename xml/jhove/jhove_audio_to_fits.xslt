<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:aes="http://www.aes.org/audioObject" 
xmlns:tcf="http://www.aes.org/tcf">

<xsl:import href="jhove_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">   
		<xsl:apply-imports/>

		<metadata>
			<audio>
				<!-- 
				<duration>
				    <xsl:variable name="durH"><xsl:value-of select="//aes:face/aes:timeline/tcf:duration/tcf:hours"/></xsl:variable>
				    <xsl:variable name="durM"><xsl:value-of select="//aes:face/aes:timeline/tcf:duration/tcf:minutes"/></xsl:variable>
				    <xsl:variable name="durS"><xsl:value-of select="//aes:face/aes:timeline/tcf:duration/tcf:seconds"/></xsl:variable>
				    <xsl:variable name="durF"><xsl:value-of select="//aes:face/aes:timeline/tcf:duration/tcf:frames"/></xsl:variable>
               		<xsl:value-of select="concat($durH,':',$durM,':',$durS,':',$durF)"/>
				</duration>
				-->
			    <bitDepth>
       				<xsl:value-of select="//aes:audioObject/aes:formatList/aes:formatRegion/aes:bitDepth"/>
       			</bitDepth>
       			<sampleRate>
       				<xsl:value-of select="//aes:audioObject/aes:formatList/aes:formatRegion/aes:sampleRate"/>
       			</sampleRate>
       			<channels>
       				<xsl:value-of select="//aes:audioObject/aes:face/aes:region/aes:numChannels"/>
       			</channels>
			    <audioDataEncoding>
       				<xsl:value-of select="//aes:audioObject/aes:audioDataEncoding"/>
       			</audioDataEncoding>
       			<offset>
       				<xsl:value-of select="//aes:audioObject/aes:firstSampleOffset"/>
       			</offset>
       			<!-- 
				<timeStampStart>
				    <xsl:variable name="startH"><xsl:value-of select="//aes:face/aes:timeline/tcf:startTime/tcf:hours"/></xsl:variable>
				    <xsl:variable name="startM"><xsl:value-of select="//aes:face/aes:timeline/tcf:startTime/tcf:minutes"/></xsl:variable>
				    <xsl:variable name="startS"><xsl:value-of select="//aes:face/aes:timeline/tcf:startTime/tcf:seconds"/></xsl:variable>
				    <xsl:variable name="startF"><xsl:value-of select="//aes:face/aes:timeline/tcf:startTime/tcf:frames"/></xsl:variable>
               		<xsl:value-of select="concat($startH,':',$startM,':',$startS,':',$startF)"/>
				</timeStampStart>
				-->
       			<byteOrder>
       				<xsl:value-of select="//aes:audioObject/aes:byteOrder"/>
       			</byteOrder>       			 
			</audio>
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fits_XsltFunctions="edu.harvard.hul.ois.fits.tools.utils.XsltFunctions">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('MPEG 1/2 Audio Layer 3')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
						<xsl:value-of select="MP3/METADATA/TYPE"/>			
				</xsl:attribute>
				<version>
					<!-- 
          			<xsl:value-of select="concat(string(MP3/MPEG/VERSION),string(', '),string(MP3/MPEG/LAYER-NAME))"/>
          			 -->
          			<xsl:value-of select="MP3/MPEG/VERSION"/>
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
	     <audio>
	     	<duration>
	            <xsl:value-of select="concat(MP3/MPEG/DURATION/HOURS,':',MP3/MPEG/DURATION/MINUTES,':',MP3/MPEG/DURATION/SECONDS,':',MP3/MPEG/DURATION/MS)"/>
	        </duration>
	     	
	     	<milliseconds>
				<xsl:value-of select="MP3/MPEG/DURATION/TOTAL-MILLISECONDS"/>	     	
	     	</milliseconds>
	     	
	     	<bitRate>       
	     		<xsl:choose>
					<xsl:when test="MP3/MPEG/BIT-RATE-UNIT='kbps'">
						<xsl:value-of select="concat(MP3/MPEG/BIT-RATE,'000')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="MP3/MPEG/BIT-RATE"/>
					</xsl:otherwise>
				</xsl:choose>   
			</bitRate>
			
			<sampleRate>
				<xsl:value-of select="MP3/MPEG/SAMPLE-RATE"/>
			</sampleRate>

			<channels>
				<xsl:value-of select="MP3/MPEG/CHANNELS"/>
        	</channels>

      	</audio>
		</metadata>
	</fits>	

</xsl:template>


</xsl:stylesheet>
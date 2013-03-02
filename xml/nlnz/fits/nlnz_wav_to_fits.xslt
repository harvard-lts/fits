<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fits_XsltFunctions="edu.harvard.hul.ois.fits.tools.utils.XsltFunctions">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:value-of select="string('Waveform Audio')" />
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:if test="WAV/METADATA/TYPE='audio/wav'">
						<xsl:value-of select="'audio/x-wave'"/>
					</xsl:if>						
				</xsl:attribute>
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
	            <xsl:value-of select="fits_XsltFunctions:getDuration(number(WAV/RIFF/LENGTH),number(WAV/WAVE/AVERAGEBYTESPERSEC))"/>
	        </duration>
	     	
	     	<bitDepth>
	     		<xsl:choose>     	
	     			<xsl:when test="WAV/WAVEHEADER/BITSPERSAMPLE">
		        		<xsl:value-of select="WAV/WAVEHEADER/BITSPERSAMPLE"/>
		        	</xsl:when>
		        	<xsl:when test="WAV/WAVE/BITSPERSAMPLE">
		            	<xsl:value-of select="WAV/WAVE/BITSPERSAMPLE"/>
		            </xsl:when>
				</xsl:choose>
			</bitDepth>
			
			<sampleRate>
				<xsl:choose>
					<xsl:when test="WAV/WAVEHEADER/SAMPLESPERSEC">
						<xsl:value-of select="WAV/WAVEHEADER/SAMPLESPERSEC"/>
					</xsl:when>
					<xsl:when test="WAV/WAVE/SAMPLESPERSEC">
			          	<xsl:value-of select="WAV/WAVE/SAMPLESPERSEC"/>
			        </xsl:when>    		
          		</xsl:choose>
			</sampleRate>

			<channels>
				<xsl:choose>			
		    		<xsl:when test="WAV/WAVEHEADER/CHANNELS">
	            		<xsl:value-of select="WAV/WAVEHEADER/CHANNELS"/>
	         		</xsl:when>
	         		<xsl:when test="WAV/WAVE/CHANNELS">
	            		<xsl:value-of select="WAV/WAVE/CHANNELS"/>
	            	</xsl:when>       		
        		</xsl:choose>
        	</channels>
 			<!-- 
	          <EncapsulationName>
	            <xsl:value-of select="WAV/WAVE/FORMAT"/>
	          </EncapsulationName>
 			-->

      	</audio>
		</metadata>
	</fits>	

</xsl:template>


</xsl:stylesheet>
<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exiftool_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<xsl:apply-imports/>
		
		<metadata>
		<audio>
			<duration>
				<xsl:if test='not(contains(exiftool/Duration,"(approx)"))'> <!-- ignore approximate durations -->
					<xsl:value-of select="exiftool/Duration"/>
				</xsl:if>		
			</duration>
			<xsl:choose>
				<xsl:when test="exiftool/BitsPerSample">
					<bitDepth>
						<xsl:value-of select="exiftool/BitsPerSample[1]"/>
					</bitDepth>
				</xsl:when>
				<xsl:when test="exiftool/AudioBitsPerSample">
					<bitDepth>
						<xsl:value-of select="exiftool/AudioBitsPerSample[1]"/>
					</bitDepth>
				</xsl:when>
				<xsl:when test="exiftool/SampleSize">
					<bitDepth>
						<xsl:value-of select="exiftool/SampleSize[1]"/>
					</bitDepth>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="exiftool/NumSampleFrames">				
					<numSamples>
						<xsl:value-of select="exiftool/NumSampleFrames[1]"/>
					</numSamples>
				</xsl:when>
				<xsl:when test="exiftool/TotalSamples">				
					<numSamples>
						<xsl:value-of select="exiftool/TotalSamples[1]"/>
					</numSamples>
				</xsl:when>
			</xsl:choose>
				

	
			<bitRate>
				<xsl:value-of select="exiftool/AudioBitrate[1]"/>
			</bitRate>
	
			<avgBitRate>
				<xsl:value-of select="exiftool/AvgBitRate[1]"/>
			</avgBitRate>
			
			<maxBitRate>
				<xsl:value-of select="exiftool/MaxBitRate[1]"/>
			</maxBitRate>	
			
			<maxPacketSize>
				<xsl:value-of select="exiftool/MaxPacketSize[1]"/>
			</maxPacketSize>
			<avgPacketSize>
				<xsl:value-of select="exiftool/AvgPacketSize[1]"/>
			</avgPacketSize>
			<numPackets>
				<xsl:value-of select="exiftool/NumPackets[1]"/>
			</numPackets>
			
			<software>
				<xsl:value-of select="exiftool/Software[1]"/>
			</software>
						
			<xsl:choose>
				<xsl:when test="exiftool/SampleRate">
					<sampleRate>
						<xsl:value-of select="exiftool/SampleRate[1]"/>
					</sampleRate>
				</xsl:when>
				<xsl:when test="exiftool/AudioSampleRate">
					<sampleRate>
						<xsl:value-of select="exiftool/AudioSampleRate[1]"/>
					</sampleRate>
				</xsl:when>
			</xsl:choose>

			<xsl:choose>
				<!-- WAV/FLAC -->
				<xsl:when test="exiftool/NumChannels">
					<channels>
						<xsl:value-of select="exiftool/NumChannels[1]"/>
					</channels>
				</xsl:when>
				<!-- OGG -->
				<xsl:when test="exiftool/AudioChannels">
					<channels>
						<xsl:value-of select="exiftool/AudioChannels[1]"/>
					</channels>
				</xsl:when>
				<!-- MP3 -->
				<xsl:when test="exiftool/ChannelMode">
					<channels>
					<xsl:choose>
						<xsl:when test="contains(exiftool/ChannelMode[1],'Stereo')">
							<xsl:value-of select="string(2)"/>
						</xsl:when>
						<xsl:when test="contains(exiftool/ChannelMode[1],'Dual')">
							<xsl:value-of select="string(2)"/>
						</xsl:when>
						<xsl:when test="contains(exiftool/ChannelMode[1],'Single')">
							<xsl:value-of select="string(1)"/>
						</xsl:when>
					</xsl:choose>					
					</channels>
				</xsl:when>
				<!-- FLAC -->
				<xsl:when test="exiftool/Channels">
					<channels>
						<xsl:value-of select="exiftool/Channels[1]"/>
					</channels>
				</xsl:when>
			</xsl:choose>	
			<audioDataEncoding>
				<xsl:value-of select="exiftool/Encoding[1]"/>
			</audioDataEncoding>	
			<blockSizeMin>
				<xsl:value-of select="exiftool/BlockSizeMin[1]"/>
			</blockSizeMin>	
			<blockSizeMax>
				<xsl:value-of select="exiftool/BlockSizeMax[1]"/>
			</blockSizeMax>	
		</audio>			
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
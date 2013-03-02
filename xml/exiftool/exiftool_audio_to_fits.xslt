<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
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
						<xsl:value-of select="exiftool/BitsPerSample"/>
					</bitDepth>
				</xsl:when>
				<xsl:when test="exiftool/AudioBitsPerSample">
					<bitDepth>
						<xsl:value-of select="exiftool/AudioBitsPerSample"/>
					</bitDepth>
				</xsl:when>
				<xsl:when test="exiftool/SampleSize">
					<bitDepth>
						<xsl:value-of select="exiftool/SampleSize"/>
					</bitDepth>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="exiftool/NumSampleFrames">				
					<numSamples>
						<xsl:value-of select="exiftool/NumSampleFrames"/>
					</numSamples>
				</xsl:when>
				<xsl:when test="exiftool/TotalSamples">				
					<numSamples>
						<xsl:value-of select="exiftool/TotalSamples"/>
					</numSamples>
				</xsl:when>
			</xsl:choose>
				

	
			<bitRate>
				<xsl:value-of select="exiftool/AudioBitrate"/>
			</bitRate>
	
			<avgBitRate>
				<xsl:value-of select="exiftool/AvgBitRate"/>
			</avgBitRate>
			
			<maxBitRate>
				<xsl:value-of select="exiftool/MaxBitRate"/>
			</maxBitRate>	
			
			<maxPacketSize>
				<xsl:value-of select="exiftool/MaxPacketSize"/>
			</maxPacketSize>
			<avgPacketSize>
				<xsl:value-of select="exiftool/AvgPacketSize"/>
			</avgPacketSize>
			<numPackets>
				<xsl:value-of select="exiftool/NumPackets"/>
			</numPackets>
			
			<software>
				<xsl:value-of select="exiftool/Software"/>
			</software>
						
			<xsl:choose>
				<xsl:when test="exiftool/SampleRate">
					<sampleRate>
						<xsl:value-of select="exiftool/SampleRate"/>
					</sampleRate>
				</xsl:when>
				<xsl:when test="exiftool/AudioSampleRate">
					<sampleRate>
						<xsl:value-of select="exiftool/AudioSampleRate"/>
					</sampleRate>
				</xsl:when>
			</xsl:choose>

			<xsl:choose>
				<!-- WAV/FLAC -->
				<xsl:when test="exiftool/NumChannels">
					<channels>
						<xsl:value-of select="exiftool/NumChannels"/>
					</channels>
				</xsl:when>
				<!-- OGG -->
				<xsl:when test="exiftool/AudioChannels">
					<channels>
						<xsl:value-of select="exiftool/AudioChannels"/>
					</channels>
				</xsl:when>
				<!-- MP3 -->
				<xsl:when test="exiftool/ChannelMode">
					<channels>
					<xsl:choose>
						<xsl:when test="contains(exiftool/ChannelMode,'Stereo')">
							<xsl:value-of select="string(2)"/>
						</xsl:when>
						<xsl:when test="contains(exiftool/ChannelMode,'Dual')">
							<xsl:value-of select="string(2)"/>
						</xsl:when>
						<xsl:when test="contains(exiftool/ChannelMode,'Single')">
							<xsl:value-of select="string(1)"/>
						</xsl:when>
					</xsl:choose>					
					</channels>
				</xsl:when>
				<!-- FLAC -->
				<xsl:when test="exiftool/Channels">
					<channels>
						<xsl:value-of select="exiftool/Channels"/>
					</channels>
				</xsl:when>
			</xsl:choose>	
			<audioDataEncoding>
				<xsl:value-of select="exiftool/Encoding"/>
			</audioDataEncoding>	
			<blockSizeMin>
				<xsl:value-of select="exiftool/BlockSizeMin"/>
			</blockSizeMin>	
			<blockSizeMax>
				<xsl:value-of select="exiftool/BlockSizeMax"/>
			</blockSizeMax>	
		</audio>			
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>
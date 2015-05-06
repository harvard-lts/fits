<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
<xsl:output method="xml" indent="yes"/>
<xsl:strip-space elements="*"/>

  <xsl:template match="/">

    <xsl:variable name="mime" select="ffmpeg/format/fits_mimetype"/>
    
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output"
    	  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://hul.harvard.edu/ois/xml/ns/fits/fits_output xml/fits_output.xsd">
		<identification>
    		<identity>
    		<!-- format and mime type -->
   			<xsl:attribute name="mimetype">
				<xsl:choose>
					<xsl:when test="not($mime) or $mime='' or $mime='application/unknown'">
						<xsl:value-of select="string('application/octet-stream')"/>
					</xsl:when>			
					<xsl:otherwise>
						<xsl:value-of select="$mime"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
   			<xsl:attribute name="format">    
   				<xsl:value-of select="ffmpeg/format/fits_format"/>
			</xsl:attribute> 
    		</identity>
    	</identification>
    
    	<fileinfo>
    		<filename>
				<xsl:value-of select="ffmpeg/format/filename"/>						
    		</filename>
    		<size>
    			<xsl:value-of select="ffmpeg/format/size"/>
    		</size>
    		<created>
	    	<xsl:choose>
	    		<xsl:when test="ffmpeg/format/tag.creation_time and ffmpeg/format/tag.creation_date">
					<xsl:value-of select="concat(ffmpeg/format/tag.creation_date, ' ', ffmpeg/format/tag.creation_time)"/>		
				</xsl:when>
				<xsl:when test="ffmpeg/format/tag.creation_time and ffmpeg/format/tag.date">
					<xsl:value-of select="concat(ffmpeg/format/tag.date, ' ', ffmpeg/format/tag.creation_time)"/>		
				</xsl:when>
	    		<xsl:when test="ffmpeg/format/tag.creation_time">
					<xsl:value-of select="ffmpeg/format/tag.creation_time"/>		
				</xsl:when>	    	
	    		<xsl:when test="ffmpeg/format/tag.creation_date">
					<xsl:value-of select="ffmpeg/format/tag.creation_date"/>		
				</xsl:when>
			</xsl:choose>
			</created>	
			<creatingApplicationName>
				<xsl:value-of select="ffmpeg/format/tag.creating_application"/>						
			</creatingApplicationName>	
    	</fileinfo>

		<metadata>
		
		<xsl:if test="ffmpeg/stream[codec_type='video']">
			<video>
				<xsl:if test="ffmpeg/stream[codec_type='video']/duration!='N/A'">
					<duration>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/duration"/>
					</duration>
				</xsl:if>
				<xsl:if test="ffmpeg/stream[codec_type='video']/start_time!='N/A'">
					<timeStampStart>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/start_time"/>
					</timeStampStart>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type='video'] and ffmpeg/format/bit_rate!='N/A'">
					<bitRate>
						<xsl:value-of select="ffmpeg/format/bit_rate"/>
					</bitRate>
				</xsl:if>
								
				<xsl:choose>
					<xsl:when test="ffmpeg/stream[codec_type='video']/avg_frame_rate!='0/0'">
						<frameRate>
							<xsl:value-of select="ffmpeg/stream[codec_type='video']/avg_frame_rate"/>
						</frameRate>
					</xsl:when>
					<xsl:when test="ffmpeg/stream[codec_type='video']/r_frame_rate!='0/0'">
						<frameRate>
							<xsl:value-of select="ffmpeg/stream[codec_type='video']/r_frame_rate"/>
						</frameRate>
					</xsl:when>
				</xsl:choose>
				
				<size>
    				<xsl:value-of select="ffmpeg/format/size"/>
    			</size>
				
				<xsl:if test="ffmpeg/stream[codec_type='video']/width!='N/A'">
					<imageWidth>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/width"/>
					</imageWidth>
				</xsl:if>
				<xsl:if test="ffmpeg/stream[codec_type='video']/height!='N/A'">
					<imageHeight>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/height"/>
					</imageHeight>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type='video']/sample_aspect_ratio!='N/A'">
					<sampleAspectRatio>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/sample_aspect_ratio"/>
					</sampleAspectRatio>
				</xsl:if>
				<xsl:if test="ffmpeg/stream[codec_type='video']/display_aspect_ratio!='N/A'">
					<displayAspectRatio>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/display_aspect_ratio"/>
					</displayAspectRatio>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type='video']/pix_fmt!='N/A'">
					<signalFormat>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/pix_fmt"/>
					</signalFormat>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type='video']/time_base!='N/A'">
					<timeBase>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/time_base"/>
					</timeBase>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type='video']/nb_frames!='N/A'">
					<numFrames>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/nb_frames"/>
					</numFrames>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type='video']/codec_name!='N/A'">
					<videoDataEncoding>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/codec_name"/>
					</videoDataEncoding>
				</xsl:if>
				<xsl:if test="ffmpeg/stream[codec_type='video']/codec_long_name!='N/A'">
					<codecName>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/codec_long_name"/>
					</codecName>
				</xsl:if>
				<xsl:if test="ffmpeg/stream[codec_type='video']/codec_tag_string!='N/A' and not(starts-with(ffmpeg/stream[codec_type='video']/codec_tag_string, '['))">
					<codecCreatorApplication>
						<xsl:value-of select="ffmpeg/stream[codec_type='video']/codec_tag_string"/>
					</codecCreatorApplication>
				</xsl:if>
				
				<xsl:choose>
					<xsl:when test="ffmpeg/stream[codec_type='audio']">
					<sound>
						<xsl:value-of select="string('Yes')"/>
					</sound>
					</xsl:when>
					<xsl:otherwise>
					<sound>
						<xsl:value-of select="string('No')"/>
					</sound>
					</xsl:otherwise>
				</xsl:choose>
			</video>
		</xsl:if>
		<xsl:if test="ffmpeg/stream[codec_type='audio']">
			<audio>
				<xsl:if test="ffmpeg/stream[codec_type='audio']/duration!='N/A'">
					<duration>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/duration"/>
					</duration>
				</xsl:if>
				<xsl:if test="ffmpeg/stream[codec_type='audio']/start_time!='N/A'">
					<timeStampStart>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/start_time"/>
					</timeStampStart>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type!='video'] and ffmpeg/format/bit_rate!='N/A'">
					<bitRate>
						<xsl:value-of select="ffmpeg/format/bit_rate"/>
					</bitRate>
				</xsl:if>
			
				<xsl:if test="ffmpeg/stream[codec_type='audio']/bits_per_sample!='N/A' and ffmpeg/stream[codec_type='audio']/bits_per_sample!='0'">
					<bitDepth>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/bits_per_sample"/>
					</bitDepth>
				</xsl:if>
			
				<xsl:if test="ffmpeg/stream[codec_type='audio']/nb_frames!='N/A'">
					<numSamples>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/nb_frames"/>
					</numSamples>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type='audio']/sample_rate!='N/A'">
					<sampleRate>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/sample_rate"/>
					</sampleRate>
				</xsl:if>

				<xsl:if test="ffmpeg/stream[codec_type='audio']/channels!='N/A'">
					<channels>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/channels"/>
					</channels>
				</xsl:if>
				
				<xsl:if test="ffmpeg/stream[codec_type='audio']/codec_name!='N/A'">
					<audioDataEncoding>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/codec_name"/>
					</audioDataEncoding>
				</xsl:if>	
				<xsl:if test="ffmpeg/stream[codec_type='audio']/codec_long_name!='N/A'">
					<codecName>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/codec_long_name"/>
					</codecName>
				</xsl:if>
				<xsl:if test="ffmpeg/stream[codec_type='audio']/codec_tag_string!='N/A' and not(starts-with(ffmpeg/stream[codec_type='audio']/codec_tag_string, '['))">
					<codecCreatorApplication>
						<xsl:value-of select="ffmpeg/stream[codec_type='audio']/codec_tag_string"/>
					</codecCreatorApplication>
				</xsl:if>
			</audio>
		</xsl:if>						
		</metadata>
	</fits>	

  </xsl:template>
</xsl:stylesheet>

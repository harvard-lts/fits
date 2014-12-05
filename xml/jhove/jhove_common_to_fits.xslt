<?xml version="1.0" ?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fits_XsltFunctions="edu.harvard.hul.ois.fits.tools.utils.XsltFunctions"
	xmlns:mix="http://www.loc.gov/mix/"
	xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
	<xsl:output method="xml" indent="yes" />
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">

	<xsl:variable name="mime" select="repInfo/mimeType"/>
	<xsl:variable name="exif" select="//property[name='ExifVersion']/values/value"/>	
	<identification>
		<identity>
			<xsl:attribute name="mimetype">
				<xsl:choose>
					<xsl:when test="$mime='text/plain; charset=US-ASCII'">
						<xsl:value-of select="string('text/plain')"/>
					</xsl:when>
					<xsl:when test="$mime='text/plain; charset=UTF-8'">
						<xsl:value-of select="string('text/plain')"/>
					</xsl:when>
					<xsl:when test="normalize-space(upper-case(//property[name='Brand']/values/value))='JPX'">		
						<xsl:value-of select="string('image/jpx')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$mime"/>
					</xsl:otherwise>
				</xsl:choose>
 			</xsl:attribute> 
			<!-- profile and format format attributes-->
			<xsl:attribute name="format">
				<xsl:variable name="format">
				<xsl:choose>
			  		<xsl:when test='string((repInfo/profiles/profile)[1])'>
			  			<xsl:value-of select="concat(repInfo/format, ' ', (repInfo/profiles/profile)[1])"/>
			  		</xsl:when>		
			  		<xsl:when test="not(string-length($exif) = 0)">
						<xsl:value-of select="concat(repInfo/format, ' ','EXIF')" />
					</xsl:when>  		
			  		<xsl:otherwise>
			  			<xsl:value-of select="repInfo/format"/>
					</xsl:otherwise>
				</xsl:choose>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$format='JPEG JFIF'">
						<xsl:value-of select="string('JPEG File Interchange Format')"/>
					</xsl:when>
					<xsl:when test="$format='JPEG EXIF'">
						<xsl:value-of select="string('Exchangeable Image File Format')"/>
					</xsl:when>
					<xsl:when test="$format='GIF GIF 87a'">
						<xsl:value-of select="string('Graphics Interchange Format')"/>
					</xsl:when>		
					<xsl:when test="$format='GIF GIF 89a'">
						<xsl:value-of select="string('Graphics Interchange Format')"/>
					</xsl:when>	
					<xsl:when test="$format='TIFF'">
						<xsl:value-of select="string('Tagged Image File Format')"/>
					</xsl:when>
					<xsl:when test="$format='TIFF Baseline RGB (Class R)'">
						<xsl:value-of select="string('Tagged Image File Format')"/>						
					</xsl:when>
					<xsl:when test="$format='TIFF TIFF/IT-BP/P2 (ISO 12639:1998)'">
						<xsl:value-of select="string('Tagged Image File Format')"/>						
					</xsl:when>
					<xsl:when test="$format='TIFF Baseline grayscale (Class G)'">
						<xsl:value-of select="string('Tagged Image File Format')"/>						
					</xsl:when>					
					<xsl:when test="$format='XML'">
						<xsl:value-of select="string('Extensible Markup Language')"/>		
					</xsl:when>
					<xsl:when test="$format='HTML'">
						<xsl:value-of select="string('Hypertext Markup Language')"/>		
					</xsl:when>
					<xsl:when test="$format='WAVE PCMWAVEFORMAT'">
						<xsl:value-of select="string('Waveform Audio')"/>	
					</xsl:when>
					<xsl:when test="$format='WAVE WAVEFORMATEX'">
						<xsl:value-of select="string('Waveform Audio')"/>
					</xsl:when>
					<xsl:when test="starts-with($format,'JPEG 2000')">
						<xsl:choose>			
							<xsl:when test="//profiles[profile='JP2']">
								<xsl:value-of select="string('JPEG 2000 JP2')"/>
							</xsl:when>
							<xsl:when test="//property[name='Brand']/values/value">		
								<xsl:value-of select="concat('JPEG 2000 ',normalize-space(upper-case(//property[name='Brand']/values/value)))"/>
							</xsl:when>
						</xsl:choose>
					</xsl:when>		
					<xsl:when test="starts-with($format,'TIFF DNG')">
						<xsl:value-of select="string('Digital Negative')"/>
					</xsl:when>
					<xsl:when test="starts-with($format,'PDF')">
						<xsl:choose>
							<xsl:when test="//profiles[profile='ISO PDF/X-1a']">
								<xsl:value-of select="string('PDF/X')"/>
							</xsl:when>
							<xsl:when test="//profiles[profile='ISO PDF/X-1']">
								<xsl:value-of select="string('PDF/X')"/>
							</xsl:when>
							<xsl:when test="//profiles[profile='ISO PDF/X-3']">
								<xsl:value-of select="string('PDF/X')"/>
							</xsl:when>
							<xsl:when test="//profiles[profile='ISO PDF/X-2']">
								<xsl:value-of select="string('PDF/X')"/>
							</xsl:when>
							<xsl:when test="//profiles[profile='ISO PDF/A-1, Level A']">
								<xsl:value-of select="string('PDF/A')"/>
							</xsl:when>
							<xsl:when test="//profiles[profile='ISO PDF/A-1, Level B']">
								<xsl:value-of select="string('PDF/A')"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="string('Portable Document Format')"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>												
					<xsl:when test="$format='AIFF AIFF'">
						<xsl:value-of select="string('Audio Interchange File Format')"/>		
					</xsl:when>			
					<xsl:when test="$format='ASCII'">
						<xsl:value-of select="string('Plain text')"/>		
					</xsl:when>
					<xsl:when test="$format='UTF-8'">
						<xsl:value-of select="string('Plain text')"/>		
					</xsl:when>		
					<xsl:when test="$format='bytestream'">
						<xsl:value-of select="string('Unknown Binary')"/>		
					</xsl:when>				
					<xsl:otherwise>
						<xsl:value-of select="$format"/>
					</xsl:otherwise>			
				</xsl:choose>
			</xsl:attribute>
				
			<!-- version -->
			<xsl:if test='repInfo/version'>
				<version>
					<xsl:choose>			
						<xsl:when test="//profiles[profile='ISO PDF/X-1a']">
							<xsl:value-of select="string('1a:2003')"/>
						</xsl:when>
						<xsl:when test="//profiles[profile='ISO PDF/X-1']">
							<xsl:value-of select="string('1:2001')"/>
						</xsl:when>
						<xsl:when test="//profiles[profile='ISO PDF/X-3']">
							<xsl:value-of select="string('3:2003')"/>
						</xsl:when>
						<xsl:when test="//profiles[profile='ISO PDF/X-2']">
							<xsl:value-of select="string('2:2003')"/>
						</xsl:when>
						<xsl:when test="//profiles[profile='ISO PDF/A-1, Level A']">
							<xsl:value-of select="string('1a')"/>
						</xsl:when>
						<xsl:when test="//profiles[profile='ISO PDF/A-1, Level B']">
							<xsl:value-of select="string('1b')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="repInfo/version" />
						</xsl:otherwise>
					</xsl:choose>
				</version>
			</xsl:if>
		</identity>
	</identification>
	
	<fileinfo>
		<size>
			<xsl:value-of select="repInfo/size" />
		</size>
		<!-- 
		<fslastmodified>
			<xsl:value-of select="repInfo/lastModified" />
		</fslastmodified>
		 -->
		<creatingApplicationName>
			<xsl:choose>
				<xsl:when test="$mime='image/tiff'">
					<xsl:value-of select="//mix:ScanningSoftware"/>					
				</xsl:when>
				<xsl:when test="$mime='image/jpeg'">
					<xsl:choose>
						<xsl:when test="//mix:ScannerModelName">
							<xsl:value-of select="//mix:ScannerModelName"/>	
						</xsl:when>
						<xsl:when test="//property[name='Comments']/values/value">						
						    <xsl:for-each select="//property[name='Comments']/values/value">
						    	<xsl:choose>
						    		<xsl:when test="position()=last()">
						    			<xsl:value-of select="//property[name='Comments']/values/value"/>
						    		</xsl:when>
						    		<xsl:otherwise>
						    			<xsl:value-of select="concat(//property[name='Comments']/values/value,', ')"/>
						    		</xsl:otherwise>
						    	</xsl:choose>
    						</xsl:for-each>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$mime='image/jp2'">
					<xsl:if test="//property[name='Comments']/values/value">							
						<xsl:value-of select="//property[name='Comments']/values/value"/>			
					</xsl:if>	
				</xsl:when>	
				<xsl:when test="$mime='application/pdf'">
				    <xsl:choose>
						<xsl:when test="//property[name='Producer']/values/value and //property[name='Creator']/values/value and //property[name='Producer']/values/value != '&lt;May be encrypted&gt;'">
							<xsl:value-of select="concat(//property[name='Producer']/values/value,'/',//property[name='Creator']/values/value)"/>
						</xsl:when>
						<xsl:otherwise>
						   <xsl:if test="//property[name='Producer']/values/value = '&lt;May be encrypted&gt;'" >
					    <xsl:text></xsl:text>
					    </xsl:if>
						</xsl:otherwise>
				    </xsl:choose>
				</xsl:when>				
			</xsl:choose>
		</creatingApplicationName>	
		 
	</fileinfo>
	
	<filestatus>
			<xsl:choose>
			  <xsl:when test='contains(repInfo/status,"Not well-formed")'>
				  <well-formed>false</well-formed>
				  <valid>false</valid>
			  </xsl:when>
			  <xsl:otherwise>
			  	<well-formed>true</well-formed>
				<xsl:choose>
					<xsl:when test='contains(repInfo/status,"not valid")'>
						<valid>false</valid>
					</xsl:when>
				  	<xsl:otherwise>
				  		<valid>true</valid>
				  	</xsl:otherwise>
				  	</xsl:choose>
			  </xsl:otherwise>
			</xsl:choose>
		
		
		<xsl:for-each select="repInfo/messages/message">
			<message>
				<xsl:variable name="messageText" select="."/>
				<xsl:variable name="subMessage" select="@subMessage"/>
				<xsl:variable name="severity" select="@severijty"/>
				<xsl:variable name="offset" select="@offset"/>
				<xsl:value-of select="fits_XsltFunctions:getMessageString($messageText,$subMessage,$severity,$offset)"/>				
			</message>
		</xsl:for-each>
	</filestatus>
	
	</xsl:template>
</xsl:stylesheet>
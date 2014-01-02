<?xml version="1.0" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
  
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output"> 
		
		<xsl:for-each select="fileUtilityOutput">
		
		<xsl:variable name="format" select="format" />
		<xsl:variable name="rawoutput" select="rawOutput" />
		<xsl:variable name="mime" select="mimetype" />
		
		<identification>
		
			<identity>
								
				<xsl:attribute name="mimetype">
					<xsl:variable name="mime" select="mimetype" />
					<xsl:choose>
						<xsl:when test="$mime='image/x-ms-bmp'">
							<xsl:value-of select="string('image/bmp')"/>
						</xsl:when>
						<xsl:when test="$mime='application/xml'">
							<xsl:value-of select="string('text/xml')"/>
						</xsl:when>
						<xsl:when test="$mime='audio/x-wav'">
							<xsl:value-of select="string('audio/x-wave')"/>
						</xsl:when>	
						<xsl:when test="$mime='application/ogg' and contains($format,'audio')">
							<xsl:value-of select="string('audio/ogg')"/>
						</xsl:when>		
						<!-- Open Office Formats -->
						<xsl:when test="$format='OpenDocument Text'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.text')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Text Template'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.text-template')"/>
						</xsl:when>
						<xsl:when test="$format='OpenDocument Drawing'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.graphics')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Drawing Template'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.graphics-template')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Presentation'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.presentation')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Presentation Template'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.presentation-template')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Spreadsheet'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.spreadsheet')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Spreadsheet Template'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.spreadsheet-template')"/>
						</xsl:when>			
						<xsl:when test="$format='OpenDocument Chart'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.chart')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Chart Template'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.chart-template')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Image'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.image')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Image Template'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.image-template')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Formula'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.formula')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Formula Template'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.formula-template')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument Master Document'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.text-master')"/>
						</xsl:when>	
						<xsl:when test="$format='OpenDocument HTML Document Template'">
								<xsl:value-of select="string('application/vnd.oasis.opendocument.text-web')"/>
						</xsl:when>		
						<xsl:when test="$format='ColorSync ICC Profile'">
								<xsl:value-of select="string('application/vnd.iccprofile')"/>
						</xsl:when>			
						<xsl:when test="$format='Kodak Color Management System, ICC Profile'">
								<xsl:value-of select="string('application/vnd.iccprofile')"/>
						</xsl:when>											
						<xsl:otherwise>
							<xsl:value-of select="$mime"/>
						</xsl:otherwise>
					</xsl:choose>
	 			</xsl:attribute> 
				
				<xsl:choose>
					<!--  "JPEG image data, JFIF standard 1.02" -->
					<xsl:when test="starts-with($format,'JPEG image data, JFIF standard')">
				    	<xsl:attribute name="format">
							<xsl:value-of select="string('JPEG File Interchange Format')"/>
				    	</xsl:attribute>
					  	<xsl:analyze-string select="$format" regex="(\D*?)(\d+\.*\d+)(.*)">		
						    <xsl:matching-substring>
						    	<version>
						    		<xsl:value-of select="regex-group(2)"/>	
						    	</version>
						    </xsl:matching-substring>
						</xsl:analyze-string>					
					</xsl:when>	
					<!--  "JPEG image data, EXIF standard" -->
					<xsl:when test="starts-with($format,'JPEG image data, EXIF standard')">
				    	<xsl:attribute name="format">
							<xsl:value-of select="string('Exchangeable Image File Format')"/>
				    	</xsl:attribute>
					  	<xsl:analyze-string select="$format" regex="(\D*?)(\d+\.*\d+)(.*)">		
						    <xsl:matching-substring>
						    	<version>
						    		<xsl:value-of select="regex-group(2)"/>	
						    	</version>
						    </xsl:matching-substring>
						</xsl:analyze-string>					
					</xsl:when>	
					<!--  "GIF image data, version 87a, 378 x 775" -->
					<xsl:when test="$mime='image/gif'">
					  	<xsl:analyze-string select="$format" regex="(.*),(.*\s(.*)),(.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(1)='GIF image data'">
										<xsl:value-of select="string('Graphics Interchange Format')"/>
									</xsl:if>
						    	</xsl:attribute>
						    	<version>
						    		<xsl:value-of select="regex-group(3)"/>	
						    	</version>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>			
					<!--  TIFF Image -->
					<xsl:when test="$mime='image/tiff'">
						<xsl:attribute name="format">
						  	<xsl:if test="$format='TIFF image data, big-endian' or $format='TIFF image data, little-endian'">
								<xsl:value-of select="string('Tagged Image File Format')"/>
							</xsl:if>
						</xsl:attribute>				
					</xsl:when>		
					<!-- PC bitmap, Windows 3.x format, 497 x 332 x 24 -->
					<xsl:when test="$mime='image/x-ms-bmp' or $mime='image/bmp'">
					  	<xsl:analyze-string select="$format" regex="(.*),(.*\s((\d)\.\D)\s.*),(.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(1)='PC bitmap'">
										<xsl:value-of select="string('Windows Bitmap')"/>
									</xsl:if>
						    	</xsl:attribute>
						    	<version>
						    		<xsl:value-of select="regex-group(4)"/>	
						    	</version>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>			
					<!-- PNG image data, 240 x 214, 8-bit/color RGB, non-interlaced -->
					<xsl:when test="$mime='image/png'">
					  	<xsl:analyze-string select="$format" regex="(\D*?)(,)(.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(1)='PNG image'">
										<xsl:value-of select="string('Portable Network Graphics')"/>
									</xsl:if>
						    	</xsl:attribute>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>						
					<!-- PDF document, version 1.5 -->
					<xsl:when test="$mime='application/pdf'">
					  	<xsl:analyze-string select="$format" regex="(.*), version (.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(1)='PDF document'">
										<xsl:value-of select="string('Portable Document Format')"/>
									</xsl:if>
						    	</xsl:attribute>
						    	<version>
						    		<xsl:value-of select="regex-group(2)"/>	
						    	</version>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>						
					<!--  XML -->
					<xsl:when test="$mime='application/xml'">
						<xsl:attribute name="format">
						  	<xsl:if test="$format='XML  document text'">
								<xsl:value-of select="string('Extensible Markup Language')"/>
							</xsl:if>
						</xsl:attribute>				
					</xsl:when>	
					<!--  HTML -->
					<xsl:when test="$mime='text/html'">
						<xsl:attribute name="format">
						  	<xsl:if test="$format='HTML document text'">
								<xsl:value-of select="string('Hypertext Markup Language')"/>
							</xsl:if>
						</xsl:attribute>				
					</xsl:when>	
					<!--  ASCII -->
					<xsl:when test="$format='US-ASCII'">
						<xsl:attribute name="format">
							<xsl:value-of select="string('ASCII')"/>
						</xsl:attribute>				
					</xsl:when>		
					<!--  JPEG 2000 image data -->
					<xsl:when test="$mime='image/jp2'">
						<xsl:attribute name="format">
							<xsl:value-of select="string('JPEG 2000')"/>
						</xsl:attribute>				
					</xsl:when>							
					<!-- RIFF (little-endian) data, WAVE audio, Microsoft PCM, 24 bit, mono 96000 Hz -->
					<xsl:when test="$mime='audio/x-wav'">
					  	<xsl:analyze-string select="$format" regex="(\D*?),(\D*?),(.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(2)=' WAVE audio'">
										<xsl:value-of select="string('Waveform Audio')"/>
									</xsl:if>
						    	</xsl:attribute>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>						
					<!-- Audio file with ID3 version 2.2, MP3 encoding -->
					<xsl:when test="$mime='audio/mpeg'">
					  	<xsl:analyze-string select="$format" regex="(.*),(.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(2)=' MP3 encoding'">
										<xsl:value-of select="string('MPEG 1/2 Audio Layer 3')"/>
									</xsl:if>
						    	</xsl:attribute>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>							
					<!--  AIFF -->
					<xsl:when test="$mime='audio/x-aiff'">
						<xsl:attribute name="format">
						  	<xsl:if test="$format='IFF data, AIFF audio'">
								<xsl:value-of select="string('Audio Interchange File Format')"/>
							</xsl:if>
						</xsl:attribute>				
					</xsl:when>	
					<!-- MPEG4 Audio -->
					<xsl:when test="$mime='audio/mp4'">
					  	<xsl:analyze-string select="$format" regex="(.*),(.*),(.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(2)=' MPEG v4 system'">
										<xsl:value-of select="string('MPEG-4 Audio')"/>
									</xsl:if>
						    	</xsl:attribute>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>		
					<!-- Ogg data, Vorbis audio, stereo, 44100 Hz, ~64000 bps, created by: Xiph.Org libVorbis I (1.0) -->
					<xsl:when test="$mime='application/ogg'">
					  	<xsl:analyze-string select="$format" regex="(\D*?, \D*?),(.*)\((.*)\)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(1)='Ogg data, Vorbis audio'">
										<xsl:value-of select="string('Ogg Vorbis Codec Compressed Multimedia File')"/>
									</xsl:if>
						    	</xsl:attribute>
						    	<!-- 
						    	<version>
						    		<xsl:value-of select="regex-group(3)"/>	
						    	</version>
						    	 -->
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>							
					<!-- FLAC audio bitstream data, 16 bit, stereo, 44.1 kHz, 411840 samples -->
					<xsl:when test="$mime='audio/x-flac'">
					  	<xsl:analyze-string select="$format" regex="(\D*?),(.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(1)='FLAC audio bitstream data'">
										<xsl:value-of select="string('Free Lossless Audio Codec')"/>
									</xsl:if>
						    	</xsl:attribute>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>			
					<!--  Real Media -->
					<xsl:when test="$mime='application/vnd.rn-realmedia'">
						<xsl:attribute name="format">
						  	<xsl:if test="$format='RealMedia file'">
								<xsl:value-of select="string('RealMedia')"/>
							</xsl:if>
						</xsl:attribute>				
					</xsl:when>	
					<!--  MS Word -->
					<xsl:when test="$mime='application/msword'">
						<xsl:attribute name="format">
						  	<xsl:value-of select="string('Microsoft Word Document')"/>
						</xsl:attribute>				
					</xsl:when>	
					<!-- Zip archive data, at least v2.0 to extract -->
					<xsl:when test="$mime='application/zip'">
					  	<xsl:analyze-string select="$format" regex="(.*),(.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="regex-group(1)='Zip archive data'">
										<xsl:value-of select="string('ZIP Format')"/>
									</xsl:if>
						    	</xsl:attribute>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>						
						<version>
							<xsl:choose>
							<xsl:when test="contains($rawoutput,'at least v0.9 to extract')">
								<xsl:value-of select="string('1.0')"/>
							</xsl:when>
							<xsl:when test="contains($rawoutput,'at least v1.0 to extract')">
								<xsl:value-of select="string('1.0')"/>
							</xsl:when>
							<xsl:when test="contains($rawoutput,'at least v1.1 to extract')">
								<xsl:value-of select="string('1.1')"/>
							</xsl:when>
							<xsl:when test="contains($rawoutput,'at least v2.0 to extract')">
								<xsl:value-of select="string('2.0')"/>
							</xsl:when>
							</xsl:choose>	
						</version>
					</xsl:when>
					<!--  GZIP -->
					<xsl:when test="contains($format,'gzip compressed data')">
						<xsl:attribute name="format">
							<xsl:value-of select="string('GZIP Format')"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="$format='Adobe Photoshop Image'">
						<xsl:attribute name="format">
							<xsl:value-of select="string('Adobe Photoshop (PSD)')"/>
						</xsl:attribute>
					</xsl:when>
					
					<!-- PostScript document text conforming DSC level 3.0 -->
					<xsl:when test="$mime='application/postscript'">
					  	<xsl:analyze-string select="$format" regex="(.*) level (.*)">		
						    <xsl:matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:if test="contains(regex-group(1),'PostScript document')">
										<xsl:value-of select="string('Postscript')"/>
									</xsl:if>
						    	</xsl:attribute>
						    	<version>
						    		<xsl:value-of select="regex-group(2)"/>	
						    	</version>
						    </xsl:matching-substring>
							<xsl:non-matching-substring>
						    	<xsl:attribute name="format">
						    		<xsl:value-of select="$format" />
						    	</xsl:attribute>
						    </xsl:non-matching-substring>
						</xsl:analyze-string>					
					</xsl:when>		
					<xsl:when test="$format='SVG Scalable Vector Graphics image'">
						<xsl:attribute name="format">
							<xsl:value-of select="string('Scalable Vector Graphics (SVG)')"/>
						</xsl:attribute>
					</xsl:when>		
					<xsl:when test="$format='Macromedia Flash Video'">
						<xsl:attribute name="format">
							<xsl:value-of select="string('Flash Video (FLV)')"/>
						</xsl:attribute>
					</xsl:when>		
					<xsl:when test="$format='data'">
						<xsl:attribute name="format">
							<xsl:value-of select="string('Unknown Binary')"/>
						</xsl:attribute>
					</xsl:when>		
					<xsl:when test="$format='OpenDocument Drawing'">
						<xsl:attribute name="format">
							<xsl:value-of select="string('OpenDocument Graphics')"/>
						</xsl:attribute>
					</xsl:when>		
					<xsl:when test="starts-with($format,'x86 boot sector')">
						<xsl:attribute name="format">	
							<xsl:value-of select="string('x86 boot sector')"/>
						</xsl:attribute>
					</xsl:when>			
					<xsl:when test="starts-with($format,'Adobe Photoshop Image')">
						<xsl:attribute name="format">	
							<xsl:value-of select="string('Adobe Photoshop')"/>
						</xsl:attribute>
					</xsl:when>			
					<xsl:when test="starts-with($format,'RIFF (little-endian) data, AVI')">
						<xsl:attribute name="format">	
							<xsl:value-of select="string('Audio/Video Interleaved Format')"/>
						</xsl:attribute>
					</xsl:when>			
					<xsl:when test="starts-with($format,'MPEG ADTS, AAC')">
						<xsl:attribute name="format">	
							<xsl:value-of select="string('AAC')"/>
						</xsl:attribute>
					</xsl:when>	
	  			<xsl:when test="ends-with($format,'ICC Profile')">
						<xsl:attribute name="format">	
							<xsl:value-of select="string('ICC')"/>
						</xsl:attribute>
	  			</xsl:when>		
					<xsl:otherwise>
						<xsl:attribute name="format">
							<xsl:value-of select="$format"/>
						</xsl:attribute>
					</xsl:otherwise>							
				</xsl:choose>
			
			</identity>
			
	  </identification>

	  <xsl:if test="contains($format,'gzip compressed data')">
	  	<fileinfo>
	  		<creatingos> 			
	  			<xsl:choose>
	  			<xsl:when test="contains($rawoutput,'from FAT filesystem (MS-DOS, OS/2, NT)')">
	  				<xsl:value-of select="string('FAT filesystem (MS-DOS, OS/2, NT/Win32)')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from Amiga')">
	  				<xsl:value-of select="string('Amiga')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from VMS')">
	  				<xsl:value-of select="string('VMS (or OpenVMS)')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from Unix')">
	  				<xsl:value-of select="string('Unix')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from VM/CMS')">
	  				<xsl:value-of select="string('VM/CMS')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from Atari')">
	  				<xsl:value-of select="string('Atari TOS')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from HPFS filesystem (OS/2, NT)')">
	  				<xsl:value-of select="string('HPFS filesystem (OS/2, NT)')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from MacOS')">
	  				<xsl:value-of select="string('Macintosh')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from Z-System')">
	  				<xsl:value-of select="string('Z-System')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from CP/M')">
	  				<xsl:value-of select="string('CP/M')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from TOPS/20')">
	  				<xsl:value-of select="string('TOPS-20')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from NTFS filesystem (NT)')">
	  				<xsl:value-of select="string('NTFS filesystem (NT)')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from QDOS')">
	  				<xsl:value-of select="string('QDOS')"/>
	  			</xsl:when>
	  			<xsl:when test="contains($rawoutput,'from Acorn RISCOS')">
	  				<xsl:value-of select="string('Acorn RISCOS')"/>
	  			</xsl:when>
	  			</xsl:choose>
	  		</creatingos>
	  	</fileinfo>
	</xsl:if>
	

	
		<xsl:if test="starts-with($mime,'text/')">
		<metadata>
			<text>
				<xsl:if test="//charset"> 
					<charset>
						<xsl:value-of select="//charset"/>
					</charset>
				</xsl:if>
				<xsl:if test="//linebreak">
					<xsl:for-each select="//linebreak">
						<xsl:if test="//linebreak != 'no'">
							<linebreak>
								<xsl:value-of select="."/>
							</linebreak>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</text>
		</metadata>
		</xsl:if>

	
	</xsl:for-each>
	  
    </fits>
  </xsl:template>
</xsl:stylesheet>
<?xml version="1.0" ?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
<xsl:output method="xml" indent="yes"/>
<xsl:strip-space elements="*"/>

  <xsl:template match="/">

   	<xsl:variable name="jfif" select="exiftool/JFIFVersion[1]"/>
   	<xsl:variable name="exif" select="exiftool/ExifVersion[1]"/>
   	<xsl:variable name="exifByteOrder" select="exiftool/ExifByteOrder[1]"/>			
    <xsl:variable name="mime" select="exiftool/MIMEType[1]"/>
	<identification>
    <identity>
    	<!-- format and mime type -->
   		<xsl:attribute name="mimetype">
			<xsl:choose>
				<xsl:when test="$mime='application/rdf+xml' or $mime='application/xml'">
					<xsl:value-of select="string('text/xml')"/>
				</xsl:when>
				<xsl:when test="$mime='audio/x-wav'">
					<xsl:value-of select="string('audio/x-wave')"/>
				</xsl:when>
				<xsl:when test="$mime='image/x-raw' and exiftool/FileType='DNG'">
					<xsl:value-of select="string('image/tiff')"/>
				</xsl:when>
				<xsl:when test="$mime='audio/aiff'">
					<xsl:value-of select="string('audio/x-aiff')"/>
				</xsl:when>
				<xsl:when test="$mime='application/vnd.adobe.photoshop'">
					<xsl:value-of select="string('image/vnd.adobe.photoshop')"/>
				</xsl:when>
				<xsl:when test="$mime='audio/x-ogg'">
					<xsl:value-of select="string('audio/ogg')"/>
				</xsl:when>
				<xsl:when test="$mime='audio/flac'">
					<xsl:value-of select="string('audio/x-flac')"/>
				</xsl:when>
                <xsl:when test="$mime='text/rtf'">
                    <xsl:value-of select="string('application/rtf')"/>
                </xsl:when>
                <xsl:when test="$mime='application/vnd.ms-word.document.macroEnabled'">
                    <xsl:value-of select="string('application/vnd.ms-word.document.macroEnabled.12')"/>
                </xsl:when>
				<xsl:when test="$mime='application/photoshop'">
					<xsl:value-of select="string('image/vnd.adobe.photoshop')"/>
				</xsl:when>		
                <xsl:when test="$mime='application/vnd.adobe.illustrator'">
                    <xsl:value-of select="string('application/postscript')"/>
                </xsl:when>
                <xsl:when test="$mime='application/vnd.rn-realmedia-vbr'">
                    <xsl:value-of select="string('application/vnd.rn-realmedia')"/>
                </xsl:when>
                <xsl:when test="$mime='video/x-pn-realvideo'">
                    <xsl:value-of select="string('application/vnd.rn-realmedia')"/>
                </xsl:when>
                <xsl:when test="$mime='video/x-m4v'">
                    <xsl:value-of select="string('video/mp4')"/>
                </xsl:when>
				<xsl:when test="not($mime) or $mime='' or $mime='application/unknown'">
					<xsl:value-of select="string('application/octet-stream')"/>
				</xsl:when>
				<xsl:when test="exiftool/FileType='EPUB'">
					<xsl:value-of select="'application/epub+zip'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$mime"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
   			
   		<xsl:attribute name="format">
   			<xsl:variable name="pdfxVersion">
   				<!-- this could be an array so we convert to a single string -->
   				<xsl:choose>
   					<xsl:when test="exiftool/GTS_PDFXVersion">
   						<xsl:value-of select="exiftool/GTS_PDFXVersion"/>
   					</xsl:when>
   					<xsl:otherwise>
   						<xsl:value-of select="''"/>
   					</xsl:otherwise>
   				</xsl:choose>
   			</xsl:variable>
   			<xsl:variable name="pdfaVersion">
   				<!-- this could be an array so we convert to a single string -->
   				<xsl:choose>
   					<xsl:when test="exiftool/SchemasSchema">
   						<xsl:value-of select="exiftool/SchemasSchema"/>
   					</xsl:when>
   					<xsl:otherwise>
   						<xsl:value-of select="''"/>
   					</xsl:otherwise>
   				</xsl:choose>
   			</xsl:variable>
   			<xsl:variable name="format">
	   			<xsl:choose>
					<xsl:when test="not(string-length($exif) = 0) and not(exiftool/FileType = 'JP2')">
						<xsl:value-of select="concat(exiftool/FileType,' EXIF')" />
					</xsl:when>
		   			<xsl:when test="not(string-length($jfif) = 0) and not(exiftool/FileType = 'JP2')">
						<xsl:value-of select="concat(exiftool/FileType,' JFIF')" />
					</xsl:when>
					<xsl:when test="not(string-length($exifByteOrder) = 0) and not(exiftool/FileType = 'JP2' or exiftool/FileType = 'TIFF')">
						<xsl:value-of select="concat(exiftool/FileType,' EXIF')" />
					</xsl:when>
					<xsl:when test="contains($pdfxVersion, 'PDF/X')">
	   					<xsl:value-of select="string('PDF/X')" />
	   				</xsl:when>
	   				<xsl:when test="contains($pdfaVersion, 'PDF/A')">
						<xsl:value-of select="string('PDF/A')" />
	   				</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="exiftool/FileType[1]" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$format='JPEG JFIF'">
					<xsl:value-of select="string('JPEG File Interchange Format')"/>
				</xsl:when>
				<xsl:when test="$format='PSD EXIF'">
					<xsl:value-of select="string('Adobe Photoshop')"/>
				</xsl:when>
				<xsl:when test="$format='PSD'">
					<xsl:value-of select="string('Adobe Photoshop')"/>
				</xsl:when>
				<xsl:when test="$format='PNG'">
					<xsl:value-of select="string('Portable Network Graphics')"/>
				</xsl:when>		
				<xsl:when test="$format='GIF'">
					<xsl:value-of select="string('Graphics Interchange Format')"/>
				</xsl:when>
				<xsl:when test="$format='TIFF'">
					<xsl:value-of select="string('Tagged Image File Format')"/>						
				</xsl:when>
				<xsl:when test="$format='AVI'">
					<xsl:value-of select="string('Audio/Video Interleaved Format')"/>		
				</xsl:when>
				<xsl:when test="$format='BMP'">
					<xsl:value-of select="string('Windows Bitmap')"/>		
				</xsl:when>
				<xsl:when test="$format='HTML'">
					<xsl:value-of select="string('Hypertext Markup Language')"/>		
				</xsl:when>
				<xsl:when test="$format='XML'">
					<xsl:value-of select="string('Extensible Markup Language')"/>	
				</xsl:when>
				<xsl:when test="$format='WAV'">
					<xsl:value-of select="string('Waveform Audio')"/>
				</xsl:when>
				<xsl:when test="$format='JP2'">
					<xsl:choose>
						<xsl:when test="contains(exiftool/CompatibleBrands,'jp2')">
							<xsl:value-of select="string('JPEG 2000 JP2')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="string('JPEG 2000')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>	
				<xsl:when test="$format='JPX'">
					<xsl:choose>
						<xsl:when test="contains(exiftool/CompatibleBrands,'jpx')">
							<xsl:value-of select="string('JPEG 2000 JPX')"/>
						</xsl:when>
						<xsl:when test="contains(exiftool/CompatibleBrands,'jp2')">
							<xsl:value-of select="string('JPEG 2000 JP2')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="string('JPEG 2000')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>	
		   		<xsl:when test="$format='RM'">
						<xsl:value-of select="string('RealMedia')" />
				</xsl:when>
				<xsl:when test="$format='DNG'">
					<xsl:value-of select="string('Digital Negative (DNG)')"/>
				</xsl:when>
				<xsl:when test="$format='DNG EXIF'">
					<xsl:value-of select="string('Digital Negative (DNG)')"/>
				</xsl:when>	
				<xsl:when test="$format='PDF'">
					<xsl:value-of select="string('Portable Document Format')"/>
				</xsl:when>	
				<xsl:when test="$format='DOC'">
					<xsl:value-of select="string('Microsoft Word Binary File Format')"/>
				</xsl:when>
                <xsl:when test="$format='DOCX'">
                    <xsl:value-of select="string('Office Open XML Document')"/>
                </xsl:when>
                <xsl:when test="$format='DOCM'">
                    <xsl:value-of select="string('Office Open XML Document (Macros Enabled)')"/>
                </xsl:when>
                <xsl:when test="$format='XLSX'">
                    <xsl:value-of select="string('Office Open XML Workbook')"/>
                </xsl:when>
                <xsl:when test="$format='PPTX'">
                    <xsl:value-of select="string('Office Open XML Presentation')"/>
                </xsl:when>
                <xsl:when test="$format='RTF'">
                    <xsl:value-of select="string('Rich Text Format (RTF)')"/>
                </xsl:when>
                <xsl:when test="$format='WP'">
                    <xsl:value-of select="string('Word Perfect')"/>
                </xsl:when>
                <xsl:when test="$format='WPD'">
                    <xsl:value-of select="string('Word Perfect')"/>
                </xsl:when>
				<xsl:when test="$format='GZIP'">
					<xsl:value-of select="string('GZIP Format')"/>
				</xsl:when>		
				<xsl:when test="$format='PSD'">
					<xsl:value-of select="string('Adobe Photoshop (PSD)')"/>
				</xsl:when>
				<xsl:when test="$format='AIFF'">
					<xsl:value-of select="string('Audio Interchange File Format')"/>
				</xsl:when>	
				<xsl:when test="$format='OGG'">
					<xsl:value-of select="string('Ogg Vorbis Codec Compressed Multimedia File')"/>
				</xsl:when>	
				<xsl:when test="$format='ODP'">
					<xsl:value-of select="string('OpenDocument Presentation')"/>
				</xsl:when>	
				<xsl:when test="$format='ODT'">
					<xsl:value-of select="string('OpenDocument Text')"/>
				</xsl:when>	
				<xsl:when test="$format='ODS'">
					<xsl:value-of select="string('OpenDocument Spreadsheet')"/>
				</xsl:when>	
				<xsl:when test="$format='ODF'">
					<xsl:value-of select="string('OpenDocument Formula')"/>
				</xsl:when>	
				<xsl:when test="$format='ODG'">
					<xsl:value-of select="string('OpenDocument Graphics')"/>
				</xsl:when>	
				<xsl:when test="$format='PPT'">
					<xsl:value-of select="string('Microsoft Powerpoint Presentation')"/>
				</xsl:when>	
				<xsl:when test="$format='M4A'">
					<xsl:value-of select="string('MPEG-4 Audio')"/>
				</xsl:when>		
				<xsl:when test="$format='MP3'">
					<xsl:value-of select="string('MPEG 1/2 Audio Layer 3')"/>
				</xsl:when>	
				<xsl:when test="$format='FLAC'">
					<xsl:value-of select="string('Free Lossless Audio Codec')"/>
				</xsl:when>	
				<xsl:when test="$format='MP4'">
					<xsl:value-of select="string('MPEG-4')"/>
				</xsl:when>
				<xsl:when test="$format='M4V'">
					<xsl:value-of select="string('MPEG-4')"/>
				</xsl:when>
				<xsl:when test="$format='MOV'">
					<xsl:value-of select="string('Quicktime')"/>
				</xsl:when>
				<xsl:when test="$format='SVG'">
					<xsl:value-of select="string('Scalable Vector Graphics (SVG)')"/>
				</xsl:when>
				<xsl:when test="$format='ZIP'">
					<xsl:value-of select="string('ZIP Format')"/>
				</xsl:when>		
				<xsl:when test="$format='FLV'">
					<xsl:value-of select="string('Flash Video (FLV)')"/>
				</xsl:when>	
				<xsl:when test="$format='PS'">
					<xsl:value-of select="string('Postscript')"/>
				</xsl:when>	
				<xsl:when test="$format='XLS'">
					<xsl:value-of select="string('Microsoft Excel')"/>
				</xsl:when>	
				<xsl:when test="$format='XMP'">
					<xsl:value-of select="string('Extensible Markup Language')"/>
				</xsl:when>	
				<xsl:when test="$format='XMP EXIF'">
					<xsl:value-of select="string('Extensible Markup Language')"/>
				</xsl:when>	
				<xsl:when test="$format='AI'">
					<xsl:value-of select="string('Adobe Illustrator')"/>
				</xsl:when>	
				<xsl:when test="$format='WMV'">
					<xsl:value-of select="string('Windows Media Video')"/>
				</xsl:when>
				<xsl:when test="$format='RMVB'">
					<xsl:value-of select="string('RealMedia')"/>
				</xsl:when>
				<xsl:when test="$format='MXF'">
					<xsl:value-of select="string('Material Exchange Format (MXF)')"/>
				</xsl:when>
				<xsl:when test="$format='CSV'">
					<xsl:value-of select="string('Comma-Separated Values (CSV)')"/>
				</xsl:when>
				<xsl:when test="$format='TXT'">
					<xsl:value-of select="string('Plain text')"/>
				</xsl:when>
				<xsl:when test="$format=''">
					<xsl:value-of select="string('Unknown Binary')"/>
				</xsl:when>		
				<xsl:otherwise>
					<xsl:value-of select="$format"/>
				</xsl:otherwise>			
			</xsl:choose>			
		</xsl:attribute> 
		
		<xsl:choose>
			<xsl:when test="exiftool/JFIFVersion">
				<version>
					<xsl:value-of select="exiftool/JFIFVersion[1]"/>
				</version>
			</xsl:when>
			<xsl:when test="exiftool/GIFVersion">
				<version>
					<xsl:value-of select="exiftool/GIFVersion[1]"/>
				</version>		
			</xsl:when>
			<xsl:when test="exiftool/VorbisVersion">
				<version>
					<xsl:value-of select="exiftool/VorbisVersion[1]"/>	
				</version>		
			</xsl:when>
			<xsl:when test="exiftool/Version">
				<version>
					<xsl:value-of select="exiftool/Version[1]"/>
				</version>	
			</xsl:when>
			<xsl:when test="exiftool/ZipVersion">
			<!--  NOT FORMATTED CORRECTLY: 20 instead of 2.0
				<version>
					<xsl:value-of select="exiftool/ZipVersion"/>	
				</version>
			-->		
			</xsl:when>
			<xsl:when test="exiftool/DNGVersion">
				<version>
					<xsl:value-of select="exiftool/DNGVersion[1]"/>	
				</version>		
			</xsl:when>
		</xsl:choose>	

    </identity>
    </identification>
    
    <fileinfo>
    	<xsl:if test="exiftool/ModifyDate">
	      	<lastmodified>
	   			<xsl:value-of select="exiftool/ModifyDate[1]"/>
	    	</lastmodified>
    	</xsl:if>
    	<created>
	    	<xsl:choose>
	    		<xsl:when test="exiftool/DateTime">
					<xsl:value-of select="exiftool/DateTime[1]"/>		
				</xsl:when>	    	
	    		<xsl:when test="exiftool/DateTimeDigitized">
					<xsl:value-of select="exiftool/DateTimeDigitized[1]"/>		
				</xsl:when>
	    		<xsl:when test="exiftool/CreateDate">
					<xsl:value-of select="exiftool/CreateDate[1]"/>		
				</xsl:when>
			</xsl:choose>
		</created>	
		<creatingApplicationName>
			<xsl:choose>
				<xsl:when test="$mime='image/tiff'">
					<xsl:value-of select="exiftool/Software[1]"/>						
				</xsl:when>
				<xsl:when test="$mime='image/jpeg'">
					<xsl:choose>
						<xsl:when test="exiftool/Model">
							<xsl:value-of select="exiftool/Model[1]"/>	
						</xsl:when>
						<xsl:when test="exiftool/Comment">
							<xsl:value-of select="exiftool/Comment[1]"/>
						</xsl:when>
					</xsl:choose>				
				</xsl:when>
				<xsl:when test="$mime='image/jp2'">
					<xsl:value-of select="exiftool/Model[1]"/>					
				</xsl:when>	
				<xsl:when test="$mime='application/pdf'">
					<xsl:if test="exiftool/Producer and exiftool/CreatorTool">
						<xsl:value-of select="concat(exiftool/Producer[1],'/',exiftool/CreatorTool[1])"/>
					</xsl:if>			
				</xsl:when>		
				<xsl:when test="$mime='application/msword'">
					<xsl:variable name="asdf"></xsl:variable>
					<xsl:if test="exiftool/Software">
						<xsl:for-each select="exiftool/Software">
							<xsl:if test="contains(.,'Word')"><xsl:value-of select="."/></xsl:if>
						</xsl:for-each>
					</xsl:if>
				</xsl:when>			
                <xsl:when test="$mime='application/vnd.oasis.opendocument.text'">
                    <xsl:value-of select="exiftool/Generator"/>          
                </xsl:when>         
			</xsl:choose>
		</creatingApplicationName>	
		<creatingApplicationVersion>
			<xsl:choose>
				<xsl:when test="$mime='application/msword' or
				        $mime='application/vnd.openxmlformats-officedocument.wordprocessingml.document' or
				        $mime='application/vnd.ms-word.document.macroEnabled' or
				        $mime='application/vnd.ms-word.document.macroEnabled.12'">
				    <xsl:if test="exiftool/AppVersion">
						<xsl:value-of select="exiftool/AppVersion[1]"/>
				    </xsl:if>
				</xsl:when>
			</xsl:choose>
		</creatingApplicationVersion>
		<inhibitorType>
			<xsl:choose>
				<xsl:when test="$mime='application/pdf'">
					<xsl:value-of select="exiftool/Encryption[1]"/>
				</xsl:when>		
			</xsl:choose>		
		</inhibitorType>
		<copyrightNote>
			<xsl:choose>
				<xsl:when test="$mime='application/pdf'">
					<xsl:value-of select="exiftool/Rights[1]"/>
				</xsl:when>		
			</xsl:choose>		
		</copyrightNote>
    </fileinfo>

  </xsl:template>
</xsl:stylesheet>

<xsl:stylesheet version="2.0"   
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

   xmlns:dc="http://purl.org/dc/elements/1.1/" 
   xmlns:ebucore="urn:ebu:metadata-schema:ebuCore_2014" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xmlns:xalan="http://xml.apache.org/xalan"
   >

<xsl:import href="mediainfo_common_to_fits.xslt"/>

<!-- function to get file name from full path -->	
<xsl:function name="ebucore:getFilename">
    <xsl:param name="str"/>
    <!--str e.g. document-uri(.), filename and path-->
    <xsl:param name="char"/>
    <xsl:value-of select="subsequence(reverse(tokenize($str, $char)), 1, 1)"/>
</xsl:function>

<xsl:template match="/">

	<xsl:apply-imports/>
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
    
        <xsl:for-each select="File/track">      
            <xsl:if test="@type = 'General'">
                
                <xsl:variable name="mime" select="./Format"/>                
	            <identification>
                    <identity>

                    <!-- .ogv - video/ogg -->
                    <!-- MXF JPEG2000 : video/mj2 -->
                    <!-- MKV :  video/x-matroska -->
                    
                        <!-- mime type -->
   		                <xsl:attribute name="mimetype">                   
 			                <xsl:choose>
 				                <xsl:when test="$mime='AVI'">
					                <xsl:value-of select="string('video/avi')"/>
				                </xsl:when>	
				                <xsl:when test="contains($mime, 'Matroska')">
					                <xsl:value-of select="string('video/x-matroska')"/>
				                </xsl:when>              	                
				                <xsl:when test="$mime='MPEG-4'">
					                <xsl:value-of select="string('video/mp4')"/>
				                </xsl:when>
				                <xsl:when test="$mime='MPEG Video'">
				                    <xsl:value-of select="string('video/mpeg')"/>
				                </xsl:when>
				                <xsl:when test="$mime='MXF'">
				                    <xsl:value-of select="string('video/mj2')"/>
				                </xsl:when>
				                <xsl:when test="$mime='OGG'">
				                    <xsl:value-of select="string('video/ogg')"/>
				                </xsl:when>
				                <xsl:when test="$mime='Theora'">
				                    <xsl:value-of select="string('video/ogg')"/>
				                </xsl:when>				                 				                				                
				                <xsl:otherwise>
					                <xsl:value-of select="$mime"/>
				                </xsl:otherwise>
			                </xsl:choose>
		                </xsl:attribute>
		                
		                <xsl:attribute name="format">
		                    <xsl:value-of select="./Format"/>
		                </xsl:attribute>                          
                
                    </identity>                
		        </identification>
		        		        
            </xsl:if>
        </xsl:for-each>
        
        <xsl:for-each select="File/track">      
            <xsl:if test="@type = 'General'">
                <fileinfo>
                    <creatingApplicationName>
                        <xsl:value-of select="./Writing_library"/>
                    </creatingApplicationName>                 
                </fileinfo>            
            </xsl:if>
        </xsl:for-each>                  


        <metadata>
            <video>

        <!-- Some of the video data comes from the General Track data -->
        <xsl:for-each select="/File/track">
        
            <xsl:if test="@type = 'General'">                   
       			<xsl:variable name="completefilename" select="./Complete_name"/>
       			
       			<!-- TODO: This is already reported by the fileinfo element, so --> 
       			<!-- it will be filtered out by the consolidator -->
			    <filename>
       				<xsl:value-of select="ebucore:getFilename($completefilename, '/')"/>
       			</filename>       			           

			    <location>
       				<xsl:value-of select="$completefilename"/>
       			</location>               	

                <!-- mime type -->
                <xsl:variable name="mime" select="./Format"/>
                <mimeType>                
 			        <xsl:choose>
 				        <xsl:when test="$mime='AVI'">
					        <xsl:value-of select="string('video/avi')"/>
				        </xsl:when>
				        <xsl:when test="contains($mime, 'Matroska')">
					        <xsl:value-of select="string('video/x-matroska')"/>
				        </xsl:when>				        		        
				        <xsl:when test="$mime='MPEG-4'">
				            <xsl:value-of select="string('video/mp4')"/>
				        </xsl:when>
				        <xsl:when test="$mime='MPEG Video'">
				            <xsl:value-of select="string('video/mpeg')"/>
				        </xsl:when>
				        <xsl:when test="$mime='MXF'">
				            <xsl:value-of select="string('video/mj2')"/>
				        </xsl:when>				        
				        <xsl:when test="$mime='OGG'">
				            <xsl:value-of select="string('video/ogg')"/>
				        </xsl:when>
				        <xsl:when test="$mime='Theora'">
				            <xsl:value-of select="string('video/ogg')"/>
				        </xsl:when>				        
				        <xsl:otherwise>
					         <xsl:value-of select="$mime"/>
				        </xsl:otherwise>
			        </xsl:choose>
       			</mimeType>
       			        
			    <format>
                    <xsl:value-of select="./Format"/>
                </format>       			    
		        <formatProfile>
                    <xsl:value-of select="./Format_profile"/>
       			</formatProfile>  			        
       			<duration>
       			    <xsl:value-of select="./Duration"/>
       			</duration>
 
                <!-- this is in the video track section -->
       			<timecodeStart>  			
       			    <xsl:value-of select="./Time_code_of_first_frame"/>
       			</timecodeStart>
   
       			<bitRate>
       			    <xsl:value-of select="./Overall_bit_rate"/>
       			</bitRate>		        
       			<size>
       			    <xsl:value-of select="./File_size"/>
       			</size>
       			<dateCreated>
       			    <xsl:value-of select="./Encoded_date"/>
       			</dateCreated> 
       			<dateModified>
       			    <xsl:value-of select="./Tagged_date"/>
       			</dateModified>                
		        		        
            </xsl:if>        
            <!-- End General track -->
 
 			<!-- Video Track -->           
            <xsl:if test="@type = 'Video'">
			    <track>			    
             	    <xsl:attribute name="type">video</xsl:attribute>			    		
              	    <xsl:attribute name="id">
           	          <xsl:value-of select="ID"/>
              	    </xsl:attribute>
              	                  	    
              	    <!-- Encoding is used to determine various element data -->              	    
                    <xsl:variable name="codecID" select="./Codec_ID"/>                   	    
                    
       			    <videoDataEncoding>
       				    <xsl:value-of select="$codecID"/>       			    
       			    </videoDataEncoding>

                    <!-- TODO: What are other cpmpression checks when compression is missing -->
                    <compression>      			           			    
                    <xsl:choose> 
                        <xsl:when test="./Compression_mode">
                            <xsl:value-of select="./Compression_mode"/>
                        </xsl:when>	        
				        <xsl:otherwise>
				            <xsl:choose>
				                <xsl:when test="$codecID='apch'">
				                    <xsl:text>Lossy</xsl:text>
				                </xsl:when>	
				                <xsl:otherwise>
				                    <xsl:text>Unknown</xsl:text>
				                </xsl:otherwise>
				            </xsl:choose>				                				                			            
                        </xsl:otherwise>                
                    </xsl:choose>
                    </compression>

                    <!-- TODO: Add more encoding types -->
                    <byteOrder>      			           			    
                    <xsl:choose> 
                        <xsl:when test="./Byte_order">
                            <xsl:value-of select="./Byte_order"/>
                        </xsl:when>	        
				        <xsl:otherwise>
				            <xsl:choose> 
				                <xsl:when test="$codecID='2vuy'">
				                    <xsl:text>Unknown</xsl:text>
				                </xsl:when>
                                <xsl:when test="$codecID='apch'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='avc'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='dv'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='dv5n'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>	
                                <xsl:when test="$codecID='JPEG 2000'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>				            			            			            				            				            
				                <xsl:otherwise>
				                </xsl:otherwise>
				            </xsl:choose>
                        </xsl:otherwise>                
                    </xsl:choose>
                    </byteOrder>    
           
                    <!-- TODO: Add more encoding types -->
 			        <xsl:choose>          
                        <xsl:when test="$codecID='2vuy'">
			                <bitDepth>
			                    <xsl:text>8</xsl:text>
			                </bitDepth>                				        
				        </xsl:when>
                        <xsl:when test="$codecID='apch'">
			                <bitDepth>
			                    <xsl:text>10</xsl:text>
			                </bitDepth>                				        
				        </xsl:when>				        			        		        
				        <xsl:otherwise>
			                <bitDepth>
       				            <xsl:value-of select="./Bit_depth"/>
			                </bitDepth>		                						    
				        </xsl:otherwise>			            
 		            </xsl:choose>                
			        
			        <!-- NOTE: Bit_Rate_max does NOT appear to be present -->
                    <xsl:variable name="bitRateMode" select="./Bit_rate_mode"/>
			        <bitRate>               
 			            <xsl:choose>
 				            <xsl:when test="$bitRateMode='Variable'">
					            <xsl:value-of select="./Bit_rate_max"/>
				            </xsl:when>			        
				            <xsl:otherwise>
					            <xsl:value-of select="./Bit_rate"/>
				            </xsl:otherwise>
				        </xsl:choose>
			        </bitRate>
			        
			        <bitRateMode>
       				    <xsl:value-of select="./Bit_rate_mode"/>
			        </bitRateMode>
		        
                    <duration>
       				    <xsl:value-of select="./Duration"/>                    
                    </duration>

                    <!-- TODO: Set with data gotten in Java code -->
       			    <delay>
       			        <xsl:value-of select="./Delay"/>
       			    </delay>

       			    <trackSize>
       			        <xsl:value-of select="./Stream_size"/>
       			    </trackSize>
       			    <timecodeStart>  			
       			        <xsl:value-of select="./Time_code_of_first_frame"/>
       			    </timecodeStart>       			    
			        <width>
       				    <xsl:value-of select="./Width"/>
			        </width>
			        <height>
       				    <xsl:value-of select="./Height"/>
			        </height>
			        
			        <!-- NOTE: Frame_Rate_max does NOT appear to be present -->
                    <xsl:variable name="frameRateMode" select="./Frame_rate_mode"/>
			        <frameRate>               
 			            <xsl:choose>
 				            <xsl:when test="$frameRateMode='Variable'">
					            <xsl:value-of select="./Frame_rate_max"/>
				            </xsl:when>			        
				            <xsl:otherwise>
					            <xsl:value-of select="./Frame_rate"/>
				            </xsl:otherwise>
				        </xsl:choose>
			        </frameRate>			        

       			    <frameRateMode>
       			        <xsl:value-of select="./Frame_rate_mode"/>
       			    </frameRateMode>
       			    
       			    <!-- TODO -->
       			    <frameCount>
       			    </frameCount>
       			    
       			    <aspectRatio>
       			        <xsl:value-of select="./Display_aspect_ratio"/>
       			    </aspectRatio>
       			    
       			    <!-- If Scanning Format is NOT present, use encoding to determine the value -->			    
       			    <scanningFormat>
		                <xsl:choose>
 				            <xsl:when test="./Scan_type">
                                <xsl:value-of select="./Scan_type"/>
				            </xsl:when>			        
				            <xsl:otherwise>
				                <xsl:choose> 
				                    <xsl:when test="$codecID='2vuy'">
				                        <xsl:text>Progressive</xsl:text>
				                    </xsl:when>
				                    <xsl:when test="$codecID='v210'">
				                        <xsl:text>Progressive</xsl:text>
				                    </xsl:when>				                
                                    <xsl:when test="$codecID='apch'">
					                    <xsl:text>Unknown</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecID='avc'">
					                    <xsl:text>Unknown</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecID='dv'">
					                    <xsl:text>Unknown</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecID='dv5n'">
					                    <xsl:text>Unknown</xsl:text>             				        
				                    </xsl:when>	
                                    <xsl:when test="$codecID='JPEG 2000'">
					                    <xsl:text>Unknown</xsl:text>             				        
				                    </xsl:when>				            			            			            				            				            
				                    <xsl:otherwise>
				                    </xsl:otherwise>
				                </xsl:choose>
				            </xsl:otherwise>
				        </xsl:choose>        
       			    </scanningFormat>
       			           			    
       			    <chromaSubsampling>
       			        <xsl:value-of select="./Chroma_subsampling"/>
       			    </chromaSubsampling>
       			    <colorspace>
       			        <xsl:value-of select="./Color_space"/>
       			    </colorspace>       			      			                              	                  	    
	           </track>	
            </xsl:if>
            <!-- End Video Track -->
                        
 			<!-- Audio Track -->           
            <xsl:if test="@type = 'Audio'">
			    <track>
             	    <xsl:attribute name="type">audio</xsl:attribute>			    		
              	    <xsl:attribute name="id">
           	          <xsl:value-of select="ID"/>
              	    </xsl:attribute>
              	    
              	    <audioDataEncoding>
              	        <xsl:value-of select="./Format"/>
              	    </audioDataEncoding>
              	    
                    <compression>
                        <xsl:value-of select="./Compression_mode"/>
                    </compression>
	           
			        <bitRate>
       				    <xsl:value-of select="./Bit_rate"/>
			        </bitRate>       			
       			
			        <bitRateMode>
       				    <xsl:value-of select="./Bit_rate_mode"/>
			        </bitRateMode>	
			        
			        <bitDepth>
			            <xsl:value-of select="./Bit_depth"/>
			        </bitDepth>
			        
                    <duration>
       				    <xsl:value-of select="./Duration"/>                    
                    </duration>
                    
                    <!-- TODO: Set with data gotten in Java code -->
                    <delay>
                        <xsl:value-of select="./Delay"/>
                    </delay>
                    
                    <trackSize>
                        <xsl:value-of select="./Stream_size"/>
                    </trackSize>
                    
                    <soundField>
                        <xsl:value-of select="./Channel_positions"/>
                    </soundField>
                    
                    <samplingRate>
                        <xsl:value-of select="./Sampling_rate"/>                    
                    </samplingRate>
                    
                    <!-- TODO -->
                    <!-- Samples_count doesn't seem to show in XML -->
                    <numSamples>
                        <xsl:value-of select="./Samples_count"/>                    
                    </numSamples>
                    
                    <channels>
                        <xsl:value-of select="./Channel_s_"/>
                    </channels>
                    
                    <!-- TODO -->
                    <channelInfo />

                    <byteOrder>
                        <xsl:value-of select="./Format_settings__Endianness"/>
                    </byteOrder>
	           
	           </track>	           

            </xsl:if> 
            <!-- End Audio Track -->                           
            	  
	    </xsl:for-each>	
	    
        <!-- Standard -->
        <xsl:for-each select="/File/track">      
            <xsl:if test="@type = 'Video'">
               <standard />
            </xsl:if> 
	    </xsl:for-each>
    
         </video>	    
	</metadata>

	</fits>				

</xsl:template>

</xsl:stylesheet>
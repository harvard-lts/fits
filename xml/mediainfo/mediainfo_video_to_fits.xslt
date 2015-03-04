<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

   xmlns:dc="http://purl.org/dc/elements/1.1/" 
   xmlns:ebucore="urn:ebu:metadata-schema:ebuCore_2014" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xmlns:xalan="http://xml.apache.org/xalan"
   >

<!-- TODO: Should the below functions be moved into -->
<!-- edu.harvard.hul.ois.fits.tools.utils.XsltFunctions ? -->
<xsl:import href="mediainfo_common_to_fits.xslt"/>

<!-- function to get file name from full path -->	
<xsl:function name="ebucore:getFilename">
    <xsl:param name="str"/>
    <!--str e.g. document-uri(.), filename and path-->
    <xsl:param name="char"/>
    <xsl:value-of select="subsequence(reverse(tokenize($str, $char)), 1, 1)"/>
</xsl:function>

<xsl:function name="ebucore:getFileExtension">
    <xsl:param name="file-path"/>
    <xsl:if test="contains($file-path, '.')">
        <xsl:value-of select="lower-case(tokenize($file-path, '\.')[last()])"/>
    </xsl:if>
</xsl:function>

<xsl:template match="/">

	<xsl:apply-imports/>
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
    
        <xsl:for-each select="File/track">      
            <xsl:if test="@type = 'General'">
                
                <xsl:variable name="completefilename" select="./Complete_name"/>
       			<xsl:variable name="fileExtension" select="ebucore:getFileExtension($completefilename)"/>               
	            <identification>
                    <identity>

   		                <xsl:attribute name="mimetype">                   
 			                <xsl:choose>
       			               <xsl:when test="$fileExtension = 'avi'">
       			                   <xsl:value-of select="string('video/avi')"/>
       			               </xsl:when>
       			               <xsl:when test="$fileExtension = 'dv'">
       			                   <xsl:value-of select="string('video/x-dvi')"/>
       			               </xsl:when>        			                    			       
       			               <xsl:when test="$fileExtension = 'mkv'">
       			                   <xsl:value-of select="string('video/x-matroska')"/>
       			               </xsl:when>
      			               <xsl:when test="$fileExtension = 'mj2'">
       			                   <xsl:value-of select="string('video/mj2')"/>
       			               </xsl:when>
       			               <xsl:when test="$fileExtension = 'mov'">
       			                   <!-- <xsl:value-of select="string('video/quicktime')"/> -->
       			                   <xsl:value-of select="string('video/mp4')"/>
       			               </xsl:when>			                    			               
      			               <xsl:when test="$fileExtension = 'mp4'">
       			                   <xsl:value-of select="string('video/mp4')"/>
       			               </xsl:when>
       			               <xsl:when test="$fileExtension = 'mpg'">
       			                   <xsl:value-of select="string('video/mpg')"/>
       			               </xsl:when>
       			               <xsl:when test="$fileExtension = 'ogv'">
       			                   <xsl:value-of select="string('video/ogg')"/>
       			               </xsl:when>       			               
       			               <xsl:when test="$fileExtension = 'qt'">
       			                   <xsl:value-of select="string('video/quicktime')"/>
       			               </xsl:when>       			                      			                     			             			       
		                       <xsl:otherwise>
				                   <xsl:text>TBD</xsl:text>
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
                    
                    <!-- TODO: NOT IN sample output -->
                    <creatingApplicationVersion>
                        <xsl:value-of select="string('TODO: Do we need this and if so, where do we get this')"/>
                        <!-- xsl:value-of select="./Encoded_Library_Version"/> -->
                    </creatingApplicationVersion>                
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
       			
       			<xsl:variable name="fileExtension" select="ebucore:getFileExtension($completefilename)"/>
       			<mimeType>
       			   <xsl:choose>
       			       <xsl:when test="$fileExtension = 'avi'">
       			           <xsl:value-of select="string('video/avi')"/>
       			       </xsl:when> 
       			       <xsl:when test="$fileExtension = 'dv'">
       			           <xsl:value-of select="string('video/x-dvi')"/>
       			       </xsl:when>       			           			       
       			       <xsl:when test="$fileExtension = 'mkv'">
       			           <xsl:value-of select="string('video/x-matroska')"/>
       			       </xsl:when>
      			       <xsl:when test="$fileExtension = 'mj2'">
       			           <xsl:value-of select="string('video/mj2')"/>
       			       </xsl:when>
       			       <xsl:when test="$fileExtension = 'mov'">
       			           <!-- <xsl:value-of select="string('video/quicktime')"/> -->
       			           <xsl:value-of select="string('video/mp4')"/>       			           
       			       </xsl:when>       			       
      			       <xsl:when test="$fileExtension = 'mp4'">
       			           <xsl:value-of select="string('video/mp4')"/>
       			       </xsl:when>
       			       <xsl:when test="$fileExtension = 'mpg'">
       			           <xsl:value-of select="string('video/mpg')"/>
       			       </xsl:when>
       			       <xsl:when test="$fileExtension = 'ogv'">
       			           <xsl:value-of select="string('video/ogg')"/>
       			       </xsl:when>       			       
       			       <xsl:when test="$fileExtension = 'qt'">
       			           <xsl:value-of select="string('video/quicktime')"/>
       			       </xsl:when>       			              			              			             			       
		               <xsl:otherwise>
				           <xsl:text>TBD</xsl:text>
				       </xsl:otherwise>
				   </xsl:choose>
       			</mimeType>
       			        
			    <format>
                    <xsl:value-of select="./Format"/>
                </format>

                <!-- Can be found in either MediaInfo in General section or -->
                <!-- Video section. The Java code will add it if it is      -->
                <!-- missing in the General section, but present in the     -->
                <!-- Video section of MediaInfo                             --> 
		        <formatProfile>
                    <xsl:value-of select="./Format_profile"/>
       			</formatProfile>
       			 			        
       			<duration>
       			    <xsl:value-of select="./Duration"/>
       			</duration>
 
                <!-- Time code start is on the Track/Other section -->
                <!-- It is visible via the MediaInfo API. Set in Java code -->
       			<timecodeStart />
   
                <!-- bit rate for general video section is only visible via the MediaInfo API. Set in Java code. -->
       			<bitRate />
       			
       			<!-- TODO: The default format of size causes a conflict in fileinfo -->	        
       			<size>
       			    <xsl:value-of select="./File_size"/>
       			</size>
       			
       			<!-- TODO: Which dates to use. Modified Date is visible via API -->
       			<dateCreated>
       			    <!-- <xsl:value-of select="./Creation_Date"/> -->
       			    <xsl:value-of select="./Encoded_date"/>
       			</dateCreated>
       			
       			<!--  Modified Date is only visible via the MediaInfo API. Set in Java code -->
       			<dateModified />
       			
       			<!-- The MD5 must be present in the MediaInfo FITS XML so that Ebucore can get it -->
       			<!-- MD5 is calculated in Java code, so an XML Element placeholder is created by the xslt -->
       			<filemd5 />               
		        		        
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

                    <compression>      			           			    
                    <xsl:choose> 
                        <xsl:when test="./Compression_mode">
                            <xsl:value-of select="./Compression_mode"/>
                        </xsl:when>	        
				        <xsl:otherwise>
				            <xsl:choose>
				                <xsl:when test="$codecID='2Vuy'">
				                    <xsl:text>Lossless</xsl:text>
				                </xsl:when>
				                <xsl:when test="$codecID='v210'">
				                    <xsl:text>Lossless</xsl:text>
				                </xsl:when>	                
                                <xsl:when test="$codecID='apch'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='avc1'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='dvc'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='dv5n'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>	
                                <xsl:when test="$codecID='mjp2'">
					                <xsl:text>Lossless</xsl:text>             				        
				                </xsl:when>
				                <xsl:otherwise>
				                    <xsl:text>Unknown</xsl:text>
				                </xsl:otherwise>
				            </xsl:choose>				                				                			            
                        </xsl:otherwise>                
                    </xsl:choose>
                    </compression>
                    
                    <byteOrder>      			           			    
                    <xsl:choose> 
                        <xsl:when test="./Byte_order">
                            <xsl:value-of select="./Byte_order"/>
                        </xsl:when>	        
				        <xsl:otherwise>
				            <xsl:choose> 
				                <xsl:when test="$codecID='2Vuy'">
				                    <xsl:text>Unknown</xsl:text>
				                </xsl:when>
				                <xsl:when test="$codecID='v210'">
				                    <xsl:text>Unknown</xsl:text>
				                </xsl:when>				                				                
                                <xsl:when test="$codecID='apch'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='avc1'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='dvc'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='dv5n'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>	
                                <xsl:when test="$codecID='mjp2'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>				            			            			            				            				            
				                <xsl:otherwise>
				                    <xsl:text>Unknown</xsl:text>
				                </xsl:otherwise>
				            </xsl:choose>
                        </xsl:otherwise>                
                    </xsl:choose>
                    </byteOrder>
            
                    <bitDepth>     			           			    
                    <xsl:choose> 
                        <xsl:when test="./Bit_depth">
                            <xsl:value-of select="./Bit_depth"/>
                        </xsl:when>	        
				        <xsl:otherwise>
				            <xsl:choose> 
                                <xsl:when test="$codecID='2Vuy'">
			                        <xsl:text>8 bits</xsl:text>            				        
				                </xsl:when>
				                <xsl:when test="$codecID='v210'">
			                        <xsl:text>10 bits</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='apch'">
			                        <xsl:text>10 bits</xsl:text>                				        
				                </xsl:when>				                			        
                                <xsl:when test="$codecID='avc1'">
			                        <xsl:text>8 bits</xsl:text>              				        
				                </xsl:when>
                                <xsl:when test="$codecID='dvc'">
			                        <xsl:text>8 bits</xsl:text>             				        
				                </xsl:when>		        				        			        
                                 <xsl:when test="$codecID='dv5n'">
					                <xsl:text>8 bits</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecID='mjp2'">
					                <xsl:text>8 bits</xsl:text>             				        
				                </xsl:when>				                				        		        
				                <xsl:otherwise>
       				                <xsl:text>8 bits</xsl:text>                						    
				                </xsl:otherwise>
				            </xsl:choose>
                        </xsl:otherwise>                
                    </xsl:choose>
                    </bitDepth>               

			        <!-- If the bitRateMode is Variable (VBR), we need to -->
			        <!-- set bitRate to the value for  bitRateMax. -->
			        <!-- This is handled in Java code -->
			        <bitRate>
					    <xsl:value-of select="./Bit_rate"/>
			        </bitRate>		        			        

			        <bitRateMode>
       				    <xsl:value-of select="./Bit_rate_mode"/>
			        </bitRateMode>
		        
		            <!-- duration format is revised in java code -->
                    <duration>
       				    <xsl:value-of select="./Duration"/>                    
                    </duration>

                    <!-- delay is only visible via the MediaInfo API. Set in Java code. -->
                    <delay />

                    <!-- tracksize format is revised in java code -->
       			    <trackSize>
       			        <xsl:value-of select="./Stream_size"/>
       			    </trackSize>    			    
			        <width>
       				    <xsl:value-of select="./Width"/>
			        </width>
			        <height>
       				    <xsl:value-of select="./Height"/>
			        </height>
			        
			        <!-- If the frameRateMode is Variable (VFR), we need to -->
			        <!-- set frameRate to the value for  frameRateMax. -->
			        <!-- This is handled in Java code -->
			        <frameRate>
					    <xsl:value-of select="./Frame_rate"/>
			        </frameRate>			        

       			    <frameRateMode>
       			        <xsl:value-of select="./Frame_rate_mode"/>
       			    </frameRateMode>

       			    <!-- frame count is only visible via the MediaInfo API. Set in Java code. -->
       			    <frameCount />
       			    
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
				                    <xsl:when test="$codecID='2Vuy'">
				                        <xsl:text>Progressive</xsl:text>
				                    </xsl:when>
				                    <xsl:when test="$codecID='v210'">
				                        <xsl:text>Progressive</xsl:text>
				                    </xsl:when>				                
                                    <xsl:when test="$codecID='apch'">
					                    <xsl:text>Interlaced</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecID='avc1'">
					                    <xsl:text>Progressive</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecID='dvc'">
					                    <xsl:text>Interlaced</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecID='dv5n'">
					                    <xsl:text>Interlaced</xsl:text>             				        
				                    </xsl:when>	
                                    <xsl:when test="$codecID='mjp2'">
					                    <xsl:text>Interlaced</xsl:text>             				        
				                    </xsl:when>				            			            			            				            				            
				                    <xsl:otherwise>
				                        <xsl:text>Unknown</xsl:text>
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
              	    
              	    <!-- TODO: Does Compression mode for audio ever show up? -->
                    <compression>
		                <xsl:choose>
 				            <xsl:when test="./Compression_mode">
                                <xsl:value-of select="./Compression_mode"/>
				            </xsl:when>			        
				            <xsl:otherwise>
				                <xsl:text>none</xsl:text>
				            </xsl:otherwise>
				        </xsl:choose>				            
                    </compression>
	           
			        <bitRate>
       				    <xsl:value-of select="./Bit_rate"/>
			        </bitRate>       			
       			
       			    <!-- TODO: How do we determine none, constant, or variable if not returned by MediaInfo ? -->
			        <!-- Right now, I am defaulting to constant -->
			        <bitRateMode>
       				    <!-- <xsl:value-of select="./Bit_rate_mode"/> -->
		                <xsl:choose>
 				            <xsl:when test="./Bit_rate_mode">
                                <xsl:value-of select="./Bit_rate_mode"/>
				            </xsl:when>			        
				            <xsl:otherwise>
				                <xsl:text>constant</xsl:text>
				            </xsl:otherwise>
				        </xsl:choose>        				    
       				    
       				    
			        </bitRateMode>	
			        
			        <bitDepth>
			            <xsl:value-of select="./Bit_depth"/>
			        </bitDepth>
			        
                    <duration>
       				    <xsl:value-of select="./Duration"/>                    
                    </duration>
                    
                    <!-- delay is only visible via the MediaInfo API. Set in Java code. -->
                    <delay />
                    
                    <!-- tracksize format is revised in java code -->
                    <trackSize>
                        <xsl:value-of select="./Stream_size"/>
                    </trackSize>
                    
                    <soundField>
                        <xsl:value-of select="./Channel_positions"/>
                    </soundField>
                    
                    <!-- samplingRate format is revised in java code -->
                    <samplingRate>
                        <xsl:value-of select="./Sampling_rate"/>                    
                    </samplingRate>
                    
                    <!-- number of samples is only visible via the MediaInfo API. Set in Java code. -->
                    <numSamples />
                    
                    <!-- samplingRate format is revised in java code -->
                    <channels>
                        <xsl:value-of select="./Channel_s_"/>
                    </channels>
                    
                    <!-- TODO -->
                    <!-- This is calculated in Java, based upon the soundField  -->
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

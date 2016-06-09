<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:fits_XsltFunctions="edu.harvard.hul.ois.fits.tools.utils.XsltFunctions"
   xmlns:dc="http://purl.org/dc/elements/1.1/" 
   xmlns:ebucore="urn:ebu:metadata-schema:ebuCore_2014" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xmlns:xalan="http://xml.apache.org/xalan"
   >
<xsl:import href="mediainfo_common_to_fits.xslt"/>

<xsl:template match="/">

	<xsl:apply-imports/>
	<!-- Used to convert case of the string -->
    <xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
    
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
    
        <xsl:for-each select="File/track">      
            <xsl:if test="@type = 'General'">
                
                <xsl:variable name="completefilename" select="./Complete_name"/>
       			<xsl:variable name="fileExtension" select="fits_XsltFunctions:getFileExtension($completefilename)"/>
       			<!-- Convert Format to lowercase for comparison -->
       			<xsl:variable name="format" select="./Format"/>
                <xsl:variable name="formatLC" select="translate($format, $uppercase, $smallcase)" />             
	            <identification>
                    <identity>

   		                <xsl:attribute name="mimetype">                   
       			           <xsl:choose>
      			               <xsl:when test="contains($formatLC, 'mpeg-4')">
       			                   <xsl:value-of select="string('video/quicktime')"/>
       			               </xsl:when>
       			               <xsl:when test="contains($formatLC, 'mpeg')">
       			                   <xsl:value-of select="string('video/mpg')"/>
       			               </xsl:when>
      			               <xsl:when test="$formatLC = 'quicktime'">
       			                   <xsl:value-of select="string('video/quicktime')"/>
       			               </xsl:when> 
      			               <xsl:when test="$formatLC = 'mxf'">
       			                   <xsl:value-of select="string('application/mxf')"/>
       			               </xsl:when>        			             			              			            			   
      			               <xsl:when test="$formatLC = 'mjp2'">
       			                   <xsl:value-of select="string('video/mj2')"/>
       			               </xsl:when>
      			               <xsl:when test="$formatLC = 'matroska'">
       			                   <xsl:value-of select="string('video/x-matroska')"/>
       			               </xsl:when>
       			               <xsl:when test="$formatLC = 'ogg'">
       			                   <xsl:value-of select="string('video/ogg')"/>
       			               </xsl:when>
       			               <xsl:when test="$formatLC = 'avi'">
       			                   <xsl:value-of select="string('video/avi')"/>
       			               </xsl:when>       			            			             			        
      			               <xsl:when test="$formatLC = 'dv'">
       			                   <xsl:value-of select="string('video/x-dv')"/>
       			               </xsl:when> 
      			               <xsl:when test="contains($formatLC, 'avc')">
       			                   <xsl:value-of select="string('video/quicktime')"/>
       			               </xsl:when>       			                         			              			             			       
		                       <xsl:otherwise>
				                   <xsl:text>TBD</xsl:text>
				               </xsl:otherwise>
				           </xsl:choose>
		                </xsl:attribute>
		                
		                <xsl:attribute name="format">
                           <xsl:choose>
                               <xsl:when test="$format = 'MXF'">
                                   <xsl:value-of select="string('Material Exchange Format (MXF)')"/>
                               </xsl:when>
                               <xsl:otherwise>
                                   <xsl:value-of select="$format"/>
                               </xsl:otherwise>
                           </xsl:choose>
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
                    <!--
                    <creatingApplicationVersion>
                        <xsl:value-of select="./Encoded_Library_Version"/>
                    </creatingApplicationVersion>
                    -->               
                </fileinfo>            
            </xsl:if>
        </xsl:for-each>                  


        <metadata>
            <video>

        <!-- Some of the video data comes from the General Track data -->
        <xsl:for-each select="/File/track">
        
            <xsl:if test="@type = 'General'">                   
       			<xsl:variable name="completefilename" select="./Complete_name"/>
       			
       			<!-- NOTE: This is already reported by the fileinfo element, --> 
       			<!-- so it might be filtered out by the consolidator -->
			    <filename>
       				<xsl:value-of select="fits_XsltFunctions:getFileNameFromUrl($completefilename)"/>
       			</filename>     			      			           

			    <location>
       				<xsl:value-of select="$completefilename"/>
       			</location>
       			
       			<!-- Convert Format to lowercase for comparison -->
       			<xsl:variable name="format" select="./Format"/>
                <xsl:variable name="formatLC" select="translate($format, $uppercase, $smallcase)" /> 
                
      			<mimeType>
       			   <xsl:choose>
      			       <xsl:when test="contains($formatLC, 'mpeg-4')">
       			           <xsl:value-of select="string('video/quicktime')"/>
       			       </xsl:when>
       			       <xsl:when test="contains($formatLC, 'mpeg')">
       			           <xsl:value-of select="string('video/mpg')"/>
       			       </xsl:when>
      			       <xsl:when test="$formatLC = 'quicktime'">
       			           <xsl:value-of select="string('video/quicktime')"/>
       			       </xsl:when> 
      			       <xsl:when test="$formatLC = 'mxf'">
       			           <xsl:value-of select="string('application/mxf')"/>
       			       </xsl:when>        			             			              			            			   
      			       <xsl:when test="$formatLC = 'mjp2'">
       			           <xsl:value-of select="string('video/mj2')"/>
       			       </xsl:when>
      			       <xsl:when test="$formatLC = 'matroska'">
       			           <xsl:value-of select="string('video/x-matroska')"/>
       			       </xsl:when>
       			       <xsl:when test="$formatLC = 'ogg'">
       			           <xsl:value-of select="string('video/ogg')"/>
       			       </xsl:when>
       			       <xsl:when test="$formatLC = 'avi'">
       			           <xsl:value-of select="string('video/avi')"/>
       			       </xsl:when>       			            			             			        
      			       <xsl:when test="$formatLC = 'dv'">
       			           <xsl:value-of select="string('video/x-dv')"/>
       			       </xsl:when>
      			       <xsl:when test="contains($formatLC, 'avc')">
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
	        
	            <!-- size format is revised in java code -->
       			<!-- NOTE: This is already reported by the fileinfo element, --> 
       			<!-- so it might be filtered out by the consolidator -->
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
       			
       			<!-- The MD5 is not returned by MediaInfo FITS XML, so how can Ebucore get it? -->
       			<!-- <filemd5 /> -->             
		        		        
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
                    <xsl:variable name="codecLC" select="translate($codecID, $uppercase, $smallcase)" /> 
                    
       			    <videoDataEncoding>
       				    <xsl:value-of select="$codecID"/>			    
       			    </videoDataEncoding>
                    
                    <!-- codecId is only visible via the MediaInfo API. Set in Java code. -->
                    <codecId/>
                    <!-- codecCC is only visible via the MediaInfo API. Set in Java code. -->
                    <codecCC/>
                    <!-- codecVersion is only visible via the MediaInfo API. Set in Java code. -->
                    <codecVersion/>
                    <!-- codecName is only visible via the MediaInfo API. Set in Java code. -->
                    <codecName/>                                                                               
                    <!-- codecFamily is only visible via the MediaInfo API. Set in Java code. -->
                    <codecFamily /> 
                    <!-- codecInfo is only visible via the MediaInfo API. Set in Java code. -->
                    <codecInfo />                                    	    

                    <compression>      			           			    
                    <xsl:choose> 
                        <xsl:when test="./Compression_mode">
                            <xsl:value-of select="./Compression_mode"/>
                        </xsl:when>	        
				        <xsl:otherwise>
				            <xsl:choose>
				                <xsl:when test="$codecLC='2vuy'">
				                    <xsl:text>Lossless</xsl:text>
				                </xsl:when>
				                <xsl:when test="$codecLC='v210'">
				                    <xsl:text>Lossless</xsl:text>
				                </xsl:when>	                
                                <xsl:when test="$codecLC='apch'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='apcn'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='apcs'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='apco'">
					                <xsl:text>Lossy</xsl:text>         				        
				                </xsl:when>				         
                                <xsl:when test="$codecLC='ap4h'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='ap4x'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>			                				                				                		                				                
                                <xsl:when test="$codecLC='avc1'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='r10g'">
			                        <xsl:text>Lossless</xsl:text>                				        
				                </xsl:when>					                
                                <xsl:when test="$codecLC='dvc'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='dv5n'">
					                <xsl:text>Lossy</xsl:text>             				        
				                </xsl:when>	
                                <xsl:when test="$codecLC='mjp2'">
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
				                <xsl:when test="$codecLC='2vuy'">
				                    <xsl:text>Unknown</xsl:text>
				                </xsl:when>
				                <xsl:when test="$codecLC='v210'">
				                    <xsl:text>Unknown</xsl:text>
				                </xsl:when>				                				                
                                <xsl:when test="$codecLC='apch'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='apcn'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>				                
                                <xsl:when test="$codecLC='avc1'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='r10g'">
			                        <xsl:text>Unknown</xsl:text>                				        
				                </xsl:when>					                
                                <xsl:when test="$codecLC='dvc'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='dv5n'">
					                <xsl:text>Unknown</xsl:text>             				        
				                </xsl:when>	
                                <xsl:when test="$codecLC='mjp2'">
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
                                <xsl:when test="$codecLC='2vuy'">
			                        <xsl:text>8 bits</xsl:text>            				        
				                </xsl:when>
				                <xsl:when test="$codecLC='v210'">
			                        <xsl:text>10 bits</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='r10g'">
			                        <xsl:text>10 bits</xsl:text>                				        
				                </xsl:when>
                                <xsl:when test="$codecLC='apch'">
			                        <xsl:text>10 bits</xsl:text>                				        
				                </xsl:when>				                
                                <xsl:when test="$codecLC='apcn'">
			                        <xsl:text>10 bits</xsl:text>                				        
				                </xsl:when>				                
                                <xsl:when test="$codecLC='apcs'">
			                        <xsl:text>10 bits</xsl:text>                				        
				                </xsl:when>
                                <xsl:when test="$codecLC='apco'">
			                        <xsl:text>10 bits</xsl:text>                				        
				                </xsl:when>
                               <xsl:when test="$codecLC='ap4h'">
			                        <xsl:text>12 bits</xsl:text>                				        
				                </xsl:when>	
                               <xsl:when test="$codecLC='ap4x'">
			                        <xsl:text>12 bits</xsl:text>                				        
				                </xsl:when>			                				                				                			                		                			        
                                <xsl:when test="$codecLC='avc1'">
			                        <xsl:text>8 bits</xsl:text>              				        
				                </xsl:when>
                                <xsl:when test="$codecLC='dvc'">
			                        <xsl:text>8 bits</xsl:text>             				        
				                </xsl:when>		        				        			        
                                 <xsl:when test="$codecLC='dv5n'">
					                <xsl:text>8 bits</xsl:text>             				        
				                </xsl:when>
                                <xsl:when test="$codecLC='mjp2'">
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
       			    <!-- FITS-SAMPLE-10.mov reports Interaced, spreadsheet reports Progessive -->	                     			    		    
       			    <scanningFormat>
		                <xsl:choose>
 				            <xsl:when test="./Scan_type">
                                <xsl:value-of select="./Scan_type"/>
				            </xsl:when>			        
				            <xsl:otherwise>
				                <xsl:choose> 
				                    <xsl:when test="$codecLC='2vuy'">
				                        <xsl:text>Progressive</xsl:text>
				                    </xsl:when>
				                    <xsl:when test="$codecLC='v210'">
				                        <xsl:text>Progressive</xsl:text>
				                    </xsl:when>				                
                                    <xsl:when test="$codecLC='apch'">
					                    <xsl:text>interlaced</xsl:text>             				        
				                    </xsl:when>
				                    <!-- If no scan type returned, then the element is not present -->
				                    <!--
                                    <xsl:when test="$codecLC='apcn'">
					                    <xsl:text></xsl:text>             				        
				                    </xsl:when>
				                    -->			                    
				                    <xsl:when test="$codecLC='r10g'">
					                    <xsl:text>Progressive</xsl:text>             				        
				                    </xsl:when>				                    
                                    <xsl:when test="$codecLC='avc1'">
					                    <xsl:text>Progressive</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecLC='dvc'">
					                    <xsl:text>interlaced</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecLC='dv5n'">
					                    <xsl:text>interlaced</xsl:text>             				        
				                    </xsl:when>	
                                    <xsl:when test="$codecLC='mjp2'">
					                    <xsl:text>interlaced</xsl:text>             				        
				                    </xsl:when>
                                    <xsl:when test="$codecLC='xith'">
					                    <xsl:text>Progressive</xsl:text>             				        
				                    </xsl:when>
				                    <!-- Do not use a default mapping -->
				                    <!--            			            			            				            				            
				                    <xsl:otherwise>
				                        <xsl:text>Unknown</xsl:text>
				                    </xsl:otherwise>
				                    -->
				                </xsl:choose>
				            </xsl:otherwise>
				        </xsl:choose>        
       			    </scanningFormat>
       			     
       			     <!-- scanOrderis only visible via the MediaInfo API. Set in Java code. -->      		
       			    <scanningOrder />
       			          			           			    
       			    <chromaSubsampling>
       			        <xsl:value-of select="./Chroma_subsampling"/>
       			    </chromaSubsampling>
       			    <colorspace>
       			        <xsl:value-of select="./Color_space"/>
       			    </colorspace>
       			    <broadcastStandard>
       			        <xsl:value-of select="./Standard"/>
       			    </broadcastStandard>        			          			      			                              	                  	    
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
              	    
                    <!-- codecId is only visible via the MediaInfo API. Set in Java code. -->
                    <codecId/>                     
                    <!-- codecFamily is only visible via the MediaInfo API. Set in Java code. -->
                    <codecFamily /> 
                    <!-- codecInfo is only visible via the MediaInfo API. Set in Java code. -->
                    <!-- <codecInfo />  -->

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
	           
			        <!-- If the bitRateMode is Variable (VBR), we need to -->
			        <!-- set bitRate to the value for  bitRateMax. -->
			        <!-- This is handled in Java code -->	           
			        <bitRate>
       				    <xsl:value-of select="./Bit_rate"/>
			        </bitRate>       			
       			
       			    <!-- TODO: Sometimes this is NOT returned in multi-track scenarios -->
       			    <!-- Should it be defaulted? -->
			        <bitRateMode>
                        <xsl:value-of select="./Bit_rate_mode"/>
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

                    <channels>
                        <xsl:value-of select="./Channel_s_"/>
                    </channels>

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

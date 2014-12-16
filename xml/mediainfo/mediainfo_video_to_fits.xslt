<xsl:stylesheet version="2.0"   
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

   xmlns:dc="http://purl.org/dc/elements/1.1/" 
   xmlns:ebucore="urn:ebu:metadata-schema:ebuCore_2014" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xmlns:xalan="http://xml.apache.org/xalan"
   >

<!-- 
<xsl:import href="mediainfo_common_to_fits.xslt"/>  	
-->
<xsl:template match="/">

<!--
	<xsl:apply-imports/>
-->
    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
    
        <xsl:for-each select="File/track">      
            <xsl:if test="@type = 'General'">
                
	            <identification>
                    <identity>
                    
<!-- TODO: Really generate the below -->                   
<!-- WIP START -->
    	<!-- format and mime type -->
   		<xsl:attribute name="mimetype">video/mp4</xsl:attribute>
   		<xsl:attribute name="format">MPEG-4</xsl:attribute>		
<!-- WIP END -->      
                
                    </identity>                
		        </identification>
		        		        
            </xsl:if>
        </xsl:for-each>


        <metadata>
            <video>

        <!-- Some of the video data comes from the General Track data -->
        <xsl:for-each select="/File/track">
        
            <xsl:if test="@type = 'General'">
                    
                <!-- TODO: split out the file name and location -->
		  	    <filename>
       		        <xsl:value-of select="./Complete_name"/>
       			</filename>
			    <location>
       				<xsl:value-of select="./Complete_name"/>
       			</location>                    
               	
       			<!-- Where is this ??? -->
       			<mimeType>
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
 
       			<timecodeStart>
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
                    
    			    <!-- Where is this -->
       			    <videoDataEncoding>
       				    <xsl:value-of select="./Format"/>       			    
       			    </videoDataEncoding>

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
       			
       			    <!-- Where is this -->
       			    <delay>
       			    </delay>
       			    
       			    <!-- Where is this -->
       			    <tracksize>
       			    </tracksize>
       			           			        	    
			        <width>
       				    <xsl:value-of select="./Width"/>
			        </width>
			        <height>
       				    <xsl:value-of select="./Height"/>
			        </height>
       			    <frameRate>
       			        <xsl:value-of select="./Frame_rate"/>
       			    </frameRate>
       			    <frameRateMode>
       			        <xsl:value-of select="./Frame_rate_mode"/>
       			    </frameRateMode>
       			    
       			    <frameCount>
       			    </frameCount>
       			    
       			    <aspectRatio>
       			        <xsl:value-of select="./Display_aspect_ratio"/>
       			    </aspectRatio>
       			    
       			    <scanningFormat>
       			        <xsl:value-of select="./Scan_type"/>
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
                    
                    <delay />
                    
                    <trackSize />
                    
                    <soundField>
                        <xsl:value-of select="./Channel_positions"/>
                    </soundField>
                    
                    <samplingRate>
                        <xsl:value-of select="./Sampling_rate"/>                    
                    </samplingRate>
                    
                    <numSamples />
                    
                    <channels>
                        <xsl:value-of select="./Channel_s_"/>
                    </channels>
                    
                    <channelInfo />
                    
                    <byteOrder />			                   

	           
	           </track>	           
	           	
            </xsl:if> 
                          	                           
            	  
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

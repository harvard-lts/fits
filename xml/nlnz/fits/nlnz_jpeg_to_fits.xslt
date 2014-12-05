<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<identification>
			<identity>
				<xsl:attribute name="format">
					<xsl:choose>
						<xsl:when test="/JPG/JFIF/IDENTIFIER='JFIF'">
			        		<xsl:value-of select="'JPEG File Interchange Format'"/>
			       		</xsl:when>
			       		<xsl:when test="/JPG/EXIF">
			        		<xsl:value-of select="'Exchangeable Image File Format'"/>
			       		</xsl:when>
		       		</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="mimetype">
					<xsl:value-of select="JPG/METADATA/TYPE" />
				</xsl:attribute>
				<xsl:if test="JPG/JFIF/MAJORVERSION">
					<version>
        				<xsl:value-of select="concat(JPG/JFIF/MAJORVERSION,'.',JPG/JFIF/MINORVERSION)"/>
        			</version>
        		</xsl:if>
			</identity>		
		</identification>
 
 		<fileinfo>
 			<!-- 
			<fslastmodified>
				<xsl:value-of select="//MODIFIED"/>
			</fslastmodified>
			 -->
			<created>
				<xsl:choose>
					<xsl:when test="//EXIF/DATETIME/VALUE">
						<xsl:value-of select="//EXIF/DATETIME/VALUE"/>
					</xsl:when>
					<xsl:when test="//EXIF/DATETIMEDIGITIZED/VALUE">
						<xsl:value-of select="//EXIF/DATETIMEDIGITIZED/VALUE"/>
					</xsl:when>	
				</xsl:choose>	
			</created>
			<creatingApplicationName>
				<xsl:choose>
					<xsl:when test="//EXIF/SOFTWARE/VALUE">
						<xsl:value-of select="//EXIF/SOFTWARE/VALUE"/>
					</xsl:when>
					<xsl:when test="//EXIF/MODEL/VALUE">
						<xsl:value-of select="//EXIF/MODEL/VALUE"/>
					</xsl:when>
				</xsl:choose>
			</creatingApplicationName>
		</fileinfo>
 
		<metadata>		
		<image>
			<imageWidth>
			 	<xsl:value-of select="JPG/IMAGE/IMAGEWIDTH"/>
			</imageWidth>
			<imageHeight>
			 	<xsl:value-of select="JPG/IMAGE/IMAGEHEIGHT"/>
			</imageHeight>
			
			<!-- 
			<colorSpace>
	          	IGNORE COLORSPACE OUTPUT FOR JPEG
	        </colorSpace>
	         -->
	         
	        <YCbCrSubSampling>
	        	<xsl:value-of select="JPG/EXIF/YCBCRSUBSAMPLING/VALUE"/>
	        </YCbCrSubSampling>

	        <YCbCrCoefficients>
	        	<xsl:value-of select="JPG/EXIF/YCBCRCOEFFICIENTS/VALUE"/>
	        </YCbCrCoefficients>
	        
	        <YCbCrPositioning>
	        	<xsl:value-of  select="JPG/EXIF/YCBCRPOSITIONING/VALUE"/>
	        </YCbCrPositioning>
		
          <!-- Choose the EXIF values instead of the JFIF if present -->
          <xsl:variable name="exif" select="JPG/EXIF"/>
          <xsl:choose>
            <!-- JFIFTag is the default value -->
            <xsl:when test="string-length($exif)=0">
              <xsl:variable name="units" select="JPG/JFIF/DENSITYUNITS"/>
              <samplingFrequencyUnit>
                <xsl:choose>
                  <xsl:when test="$units=0">
                    <xsl:value-of select="string('no absolute unit of measurement')"/>
                  </xsl:when>
                  <xsl:when test="$units=1">
                    <xsl:value-of select="string('in.')"/>
                  </xsl:when>
                  <xsl:when test="$units=2">
                    <xsl:value-of select="string('cm')"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="string('')"/>
                  </xsl:otherwise>
                </xsl:choose>
              </samplingFrequencyUnit>
              <xSamplingFrequency>
                <xsl:variable name="xdens" select="JPG/JFIF/XDENSITY"/>
                <xsl:choose>
                  <xsl:when test="$units=0">
                  	<xsl:call-template name="trimDecimal">
                  		<xsl:with-param name="value" select="$xdens"/>
                  	</xsl:call-template>
                  </xsl:when>
                  <xsl:when test="$units=1">
                  	<xsl:call-template name="trimDecimal">
                  		<xsl:with-param name="value" select="$xdens"/>
                  	</xsl:call-template>
                  </xsl:when>
                  <xsl:when test="$units=2">
                  	<xsl:call-template name="trimDecimal">
                  		<xsl:with-param name="value" select="$xdens"/>
                  	</xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="string('')"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xSamplingFrequency>
              <ySamplingFrequency>
                <xsl:variable name="ydens" select="JPG/JFIF/YDENSITY"/>
                <xsl:choose>
                  <xsl:when test="$units=0">
                  	<xsl:call-template name="trimDecimal">
                  		<xsl:with-param name="value" select="$ydens"/>
                  	</xsl:call-template>
                  </xsl:when>
                  <xsl:when test="$units=1">
                  	<xsl:call-template name="trimDecimal">
                  		<xsl:with-param name="value" select="$ydens"/>
                  	</xsl:call-template>
                  </xsl:when>
                  <xsl:when test="$units=2">
                  	<xsl:call-template name="trimDecimal">
                  		<xsl:with-param name="value" select="$ydens"/>
                  	</xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="string('')"/>
                  </xsl:otherwise>
                </xsl:choose>
              </ySamplingFrequency>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="units" select="JPG/EXIF/RESOLUTIONUNIT/VALUE"/>
              <samplingFrequencyUnit>
                <xsl:choose>
                  <xsl:when test="$units=1">
                    <xsl:value-of select="string('no absolute unit of measurement')"/>
                  </xsl:when>
                  <xsl:when test="$units=2">
                    <xsl:value-of select="string('in.')"/>
                  </xsl:when>
                  <xsl:when test="$units=3">
                    <xsl:value-of select="string('cm')"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="string('')"/>
                  </xsl:otherwise>
                </xsl:choose>
              </samplingFrequencyUnit>
              
              
              <xSamplingFrequency>
                  	<xsl:call-template name="trimDecimal">
                  		<xsl:with-param name="value" select="JPG/EXIF/XRESOLUTION/VALUE"/>
                  	</xsl:call-template>
              </xSamplingFrequency>
              <ySamplingFrequency>
                  	<xsl:call-template name="trimDecimal">
                  		<xsl:with-param name="value" select="JPG/EXIF/YRESOLUTION/VALUE"/>
                  	</xsl:call-template>
              </ySamplingFrequency>
            </xsl:otherwise>
          </xsl:choose>
            
	      <bitsPerSample>	         
	          <xsl:call-template name="precisioncomponents">
	      	  	<xsl:with-param name="components" select="/JPG/IMAGE/COMPONENTS"/>
	      	  	<xsl:with-param name="precision"  select="JPG/IMAGE/PRECISION"/>
	      	  	<xsl:with-param name="bps"/>
	   		  </xsl:call-template>
	      </bitsPerSample>
	      
	      <extraSamples>
	      	<xsl:value-of select="JPG/EXIF/EXTRASAMPLES/VALUE"/>
	      </extraSamples>

	      <colorMap>
	      	<xsl:value-of select="JPG/EXIF/COLORMAP/VALUE"/>
	      </colorMap>
	      
	      <grayResponseCurve>
	      	<xsl:value-of select="JPG/EXIF/GRAYRESPONSECURVE/VALUE"/>
	      </grayResponseCurve>
	      
	      <grayResponseUnit>
	      	<xsl:value-of select="JPG/EXIF/GRAYRESPONSEUNIT/VALUE"/>
	      </grayResponseUnit>
          
          <referenceBlackWhite>
			<xsl:value-of select="JPG/EXIF/REFERENCEBLACKWHITE/VALUE"/>
		  </referenceBlackWhite>
		  
			<whitePointXValue>
				<xsl:value-of select='substring-before(JPG/EXIF/WHITEPOINT/VALUE," ")'/>
			</whitePointXValue>
			<whitePointYValue>
				<xsl:value-of select='substring-after(JPG/EXIF/WHITEPOINT/VALUE," ")'/>
			</whitePointYValue>
			
			<!-- parse through PrimaryChromaticities for the 6 pieces -->
			<xsl:variable name="pc" select="JPG/EXIF/PRIMARYCHROMATICITIES/VALUE"/>
			<primaryChromaticitiesRedX>
				<xsl:value-of select='substring-before($pc," ")'/>
			</primaryChromaticitiesRedX>
			
			<xsl:variable name="afterRedX" select='substring-after($pc," ")'/>		
			<primaryChromaticitiesRedY>
				<xsl:value-of select='substring-before($afterRedX," ")'/>
			</primaryChromaticitiesRedY>
			
			<xsl:variable name="afterRedY" select='substring-after($afterRedX," ")'/>
			<primaryChromaticitiesGreenX>
				<xsl:value-of select='substring-before($afterRedY," ")'/>
			</primaryChromaticitiesGreenX>
			
			<xsl:variable name="afterGreenX" select='substring-after($afterRedY," ")'/>
			<primaryChromaticitiesGreenY>
				<xsl:value-of select='substring-before($afterGreenX," ")'/>
			</primaryChromaticitiesGreenY>
			
			<xsl:variable name="afterGreenY" select='substring-after($afterGreenX," ")'/>
			<primaryChromaticitiesBlueX>
				<xsl:value-of select='substring-before($afterGreenY," ")'/>
			</primaryChromaticitiesBlueX>
						
			<xsl:variable name="afterBlueX" select='substring-after($afterGreenY," ")'/>
			<primaryChromaticitiesBlueY>
				<xsl:value-of select='substring-before($afterBlueX," ")'/>
			</primaryChromaticitiesBlueY>
          
			<orientation>
				<xsl:variable name="orientation" select="JPG/EXIF/ORIENTATION/VALUE"/>
				<xsl:choose>
					<xsl:when test="$orientation=1">
						<xsl:value-of select="string('normal*')"/>
					</xsl:when>
					<xsl:when test="$orientation=2">
						<xsl:value-of select="string('normal, image flipped')"/>
					</xsl:when>
					<xsl:when test="$orientation=3">
						<xsl:value-of select="string('normal, rotated 180°')"/>
					</xsl:when>
					<xsl:when test="$orientation=4">
						<xsl:value-of select="string('normal, image flipped, rotated 180°')"/>
					</xsl:when>
					<xsl:when test="$orientation=5">
						<xsl:value-of select="string('normal, image flipped, rotated cw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation=6">
						<xsl:value-of select="string('normal, rotated ccw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation=7">
						<xsl:value-of select="string('normal, image flipped, rotated ccw 90°')"/>
					</xsl:when>
					<xsl:when test="$orientation=8">
						<xsl:value-of select="string('normal, rotated cw 90°')"/>
					</xsl:when>
<!-- 					<xsl:otherwise>
						<xsl:value-of select="$orientation"/>
					</xsl:otherwise> -->
				</xsl:choose>
			</orientation>
          
          <exposureTime>
          	<xsl:value-of select="JPG/EXIF/EXPOSURETIME/VALUE"/>
          </exposureTime>
          
          <fNumber>
          	<xsl:value-of select="JPG/EXIF/FNUMBER/VALUE"/>
          </fNumber>
          
          <exposureProgram>
	       	<xsl:variable name="exposureProgram" select="JPG/EXIF/EXPOSUREPROGRAM/VALUE"/>
	      	<xsl:choose>
				<xsl:when test="$exposureProgram=0">
					<xsl:value-of select="string('Not Defined')"/>
				</xsl:when>
				<xsl:when test="$exposureProgram=1">
					<xsl:value-of select="string('Manual')"/>
				</xsl:when>
				<xsl:when test="$exposureProgram=2">
					<xsl:value-of select="string('Normal program')"/>
				</xsl:when>
				<xsl:when test="$exposureProgram=3">
					<xsl:value-of select="string('Aperture priority')"/>
				</xsl:when>
				<xsl:when test="$exposureProgram=4">
					<xsl:value-of select="string('Shutter priority')"/>
				</xsl:when>
				<xsl:when test="$exposureProgram=5">
					<xsl:value-of select="string('Creative program (biased toward depth of field)')"/>
				</xsl:when>
				<xsl:when test="$exposureProgram=6">
					<xsl:value-of select="string('Action program (biased toward fast shutter speed)')"/>
				</xsl:when>
				<xsl:when test="$exposureProgram=7">
					<xsl:value-of select="string('Portrait mode (for closeup photos with the background out of focus)')"/>
				</xsl:when>
				<xsl:when test="$exposureProgram=8">
					<xsl:value-of select="string('Landscape mode (for landscape photos with the background in focus)')"/>
				</xsl:when>
			</xsl:choose>
	     </exposureProgram>
          
          <spectralSensitivity>
          	<xsl:value-of select="JPG/EXIF/SPECTRALSENSITIVITY/VALUE"/>
          </spectralSensitivity>

          <isoSpeedRating>
          	<xsl:value-of select="JPG/EXIF/ISOSPEEDRATING/VALUE"/>
          </isoSpeedRating>

          <exifVersion>
          	<xsl:value-of select="replace(JPG/EXIF/EXIFVERSION/VALUE,'\.','')"/>
          </exifVersion>

          <shutterSpeedValue>
          	<xsl:value-of select="JPG/EXIF/SHUTTERSPEEDVALUE/VALUE"/>
          </shutterSpeedValue>
          
          <apertureValue>
          	<xsl:value-of select="JPG/EXIF/APERTUREVALUE/VALUE"/>
          </apertureValue>
          
          <brightnessvalue>
          	<xsl:value-of select="JPG/EXIF/BRIGHTNESSVALUE"/>
          </brightnessvalue>
          
          <exposureBiasValue>
          	<xsl:value-of select="JPG/EXIF/EXPOSUREBIASVALUE/VALUE"/>
          </exposureBiasValue>
          
          <maxApertureValue>
          	<xsl:value-of select="JPG/EXIF/MAXAPERTUREVALUE/VALUE"/>
          </maxApertureValue>
          
          <subjectDistance>
          	<xsl:value-of select="JPG/EXIF/SUBJECTDISTANCE/VALUE"/>
          </subjectDistance>
          
          <meteringMode>
	       	<xsl:variable name="meteringMode" select="JPG/EXIF/METERINGMODE/VALUE"/>
	      	<xsl:choose>
	      	<!--  
				<xsl:when test="$meteringMode=0">
					<xsl:value-of select="string('Unknown')"/>
				</xsl:when>
			-->
				<xsl:when test="$meteringMode=1">
					<xsl:value-of select="string('Average')"/>
				</xsl:when>
				<xsl:when test="$meteringMode=2">
					<xsl:value-of select="string('Center weighted average')"/>
				</xsl:when>
				<xsl:when test="$meteringMode=3">
					<xsl:value-of select="string('Spot')"/>
				</xsl:when>
				<xsl:when test="$meteringMode=4">
					<xsl:value-of select="string('Multispot')"/>
				</xsl:when>
				<xsl:when test="$meteringMode=5">
					<xsl:value-of select="string('Pattern')"/>
				</xsl:when>
				<xsl:when test="$meteringMode=6">
					<xsl:value-of select="string('Partial')"/>
				</xsl:when>
				<!-- 
				<xsl:when test="$meteringMode=255">
					<xsl:value-of select="string('Other')"/>
				</xsl:when>
				-->
			</xsl:choose>
	     </meteringMode>    
          
          <lightSource>
          	<xsl:variable name="lightSource" select="JPG/EXIF/LIGHTSOURCE/VALUE"/>
	      	<xsl:choose>
				<xsl:when test="$lightSource=0">
					<xsl:value-of select="string('unknown')"/>
				</xsl:when>
				<xsl:when test="$lightSource=1">
					<xsl:value-of select="string('Daylight')"/>
				</xsl:when>
				<xsl:when test="$lightSource=2">
					<xsl:value-of select="string('Fluorescent')"/>
				</xsl:when>
				<xsl:when test="$lightSource=3">
					<xsl:value-of select="string('Tungsten (incandescent light)')"/>
				</xsl:when>
				<xsl:when test="$lightSource=4">
					<xsl:value-of select="string('Flash')"/>
				</xsl:when>
				<xsl:when test="$lightSource=9">
					<xsl:value-of select="string('Fine weather')"/>
				</xsl:when>
				<xsl:when test="$lightSource=10">
					<xsl:value-of select="string('Cloudy weather')"/>
				</xsl:when>
				<xsl:when test="$lightSource=11">
					<xsl:value-of select="string('Shade')"/>
				</xsl:when>
				<xsl:when test="$lightSource=12">
					<xsl:value-of select="string('Daylight fluorescent (D 5700 - 7100K)')"/>
				</xsl:when>
				<xsl:when test="$lightSource=13">
					<xsl:value-of select="string('Day white fluorescent (N 4600 - 5400K)')"/>
				</xsl:when>
				<xsl:when test="$lightSource=14">
					<xsl:value-of select="string('Cool white fluorescent (W 3900 - 4500K)')"/>
				</xsl:when>
				<xsl:when test="$lightSource=15">
					<xsl:value-of select="string('White fluorescent (WW 3200 - 3700K)')"/>
				</xsl:when>
				<xsl:when test="$lightSource=17">
					<xsl:value-of select="string('Standard Light A')"/>
				</xsl:when>
				<xsl:when test="$lightSource=18">
					<xsl:value-of select="string('Standard Light B')"/>
				</xsl:when>
				<xsl:when test="$lightSource=19">
					<xsl:value-of select="string('Standard Light C')"/>
				</xsl:when>
				<xsl:when test="$lightSource=20">
					<xsl:value-of select="string('D55')"/>
				</xsl:when>
				<xsl:when test="$lightSource=21">
					<xsl:value-of select="string('D65')"/>
				</xsl:when>
				<xsl:when test="$lightSource=22">
					<xsl:value-of select="string('D75')"/>
				</xsl:when>
				<xsl:when test="$lightSource=23">
					<xsl:value-of select="string('D50')"/>
				</xsl:when>
				<xsl:when test="$lightSource=24">
					<xsl:value-of select="string('ISO Studio Tungsten')"/>
				</xsl:when>
				<xsl:when test="$lightSource=255">
					<xsl:value-of select="string('other light source')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="string('unknown')"/>
				</xsl:otherwise>
			</xsl:choose>
          </lightSource>  

          <flash>
          	<xsl:variable name="flash" select="JPG/EXIF/FLASH/VALUE"/>
	      	<xsl:choose>
				<xsl:when test="$flash=0">
					<xsl:value-of select="string('Flash did not fire')"/>
				</xsl:when>
				<xsl:when test="$flash=1">
					<xsl:value-of select="string('Flash fired')"/>
				</xsl:when>
				<xsl:when test="$flash=5">
					<xsl:value-of select="string('Strobe return light not detected')"/>
				</xsl:when>
				<xsl:when test="$flash=7">
					<xsl:value-of select="string('Strobe return light detected')"/>
				</xsl:when>
				<xsl:when test="$flash=8">
					<xsl:value-of select="string('Flash did not fire, compulsory flash mode')"/>
				</xsl:when>
				<xsl:when test="$flash=9">
					<xsl:value-of select="string('Flash fired, compulsory flash mode')"/>
				</xsl:when>
				<xsl:when test="$flash=13">
					<xsl:value-of select="string('Flash fired, compulsory flash mode, return light not detected')"/>
				</xsl:when>
				<xsl:when test="$flash=15">
					<xsl:value-of select="string('Flash fired, compulsory flash mode, return light detected')"/>
				</xsl:when>
				<!-- 
				<xsl:when test="$flash=16">
					<xsl:value-of select="string('Flash did not fire, compulsory flash mode')"/>
				</xsl:when>
				<xsl:when test="$flash=20">
					<xsl:value-of select="string('Flash did not fire')"/>
				</xsl:when>
				 -->
				<xsl:when test="$flash=24">
					<xsl:value-of select="string('Flash did not fire, auto mode')"/>
				</xsl:when>
				<xsl:when test="$flash=25">
					<xsl:value-of select="string('Flash fired, auto mode')"/>
				</xsl:when>
				<xsl:when test="$flash=29">
					<xsl:value-of select="string('Flash fired, auto mode, return light not detected')"/>
				</xsl:when>
				<xsl:when test="$flash=31">
					<xsl:value-of select="string('Flash fired, auto mode, return light detected')"/>
				</xsl:when>
				<xsl:when test="$flash=32">
					<xsl:value-of select="string('No flash function')"/>
				</xsl:when>
				<!-- 
				<xsl:when test="$flash=48">
					<xsl:value-of select="string('Flash did not fire')"/>
				</xsl:when>
				 -->
				<xsl:when test="$flash=65">
					<xsl:value-of select="string('Flash fired, red-eye reduction mode')"/>
				</xsl:when>
				<xsl:when test="$flash=69">
					<xsl:value-of select="string('Flash fired, red-eye reduction mode, return light not detected')"/>
				</xsl:when>
				<xsl:when test="$flash=71">
					<xsl:value-of select="string('Flash fired, red-eye reduction mode, return light detected')"/>
				</xsl:when>
				<xsl:when test="$flash=73">
					<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode')"/>
				</xsl:when>
				<xsl:when test="$flash=77">
					<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode, return light not detected')"/>
				</xsl:when>
				<xsl:when test="$flash=79">
					<xsl:value-of select="string('Flash fired, compulsory flash mode, red-eye reduction mode, return light detected')"/>
				</xsl:when>
				<!-- 
				<xsl:when test="$flash=80">
					<xsl:value-of select="string('Flash did not fire')"/>
				</xsl:when>
				<xsl:when test="$flash=88">
					<xsl:value-of select="string('Flash did not fire, auto mode')"/>
				</xsl:when>
				 -->
				<xsl:when test="$flash=89">
					<xsl:value-of select="string('Flash fired, auto mode, red-eye reduction mode')"/>
				</xsl:when>
				<xsl:when test="$flash=93">
					<xsl:value-of select="string('Flash fired, auto mode, return light not detected, red-eye reduction mode')"/>
				</xsl:when>
				<xsl:when test="$flash=95">
					<xsl:value-of select="string('Flash fired, auto mode, return light detected, red-eye reduction mode')"/>
				</xsl:when>
			</xsl:choose>
          </flash>
            
          <flashEnergy>
          	<xsl:value-of select="JPG/EXIF/FLASHENERGY/VALUE"/>
          </flashEnergy>  
          
          <focalLength>
          	<xsl:value-of select="JPG/EXIF/FOCALLENGTH/VALUE"/>
          </focalLength>   
                    
          <exposureIndex>
          	<xsl:value-of select="JPG/EXIF/EXPOSUREINDEX/VALUE"/>
          </exposureIndex>
            
          <sensingMethod>
	       	<xsl:variable name="sensingMethod" select="JPG/EXIF/SENSINGMETHOD/VALUE"/>
	      	<xsl:choose>
<!-- 				<xsl:when test="$sensingMethod=1">
					<xsl:value-of select="string('Monochrome area')"/>
				</xsl:when> -->
				<xsl:when test="$sensingMethod=2">
					<xsl:value-of select="string('One-chip color area sensor')"/>
				</xsl:when>
				<xsl:when test="$sensingMethod=3">
					<xsl:value-of select="string('Two-chip color area sensor')"/>
				</xsl:when>
				<xsl:when test="$sensingMethod=4">
					<xsl:value-of select="string('Three-chip color area sensor')"/>
				</xsl:when>
				<xsl:when test="$sensingMethod=5">
					<xsl:value-of select="string('Color sequential area sensor')"/>
				</xsl:when>
<!-- 				<xsl:when test="$sensingMethod=6">
					<xsl:value-of select="string('Monochrome linear ')"/>
				</xsl:when> -->
				<xsl:when test="$sensingMethod=7">
					<xsl:value-of select="string('Trilinear sensor')"/>
				</xsl:when>
				<xsl:when test="$sensingMethod=8">
					<xsl:value-of select="string('Color sequential linear sensor')"/>
				</xsl:when>
			</xsl:choose>
	     </sensingMethod>  
    
          <cfaPattern>
          	<xsl:value-of select="JPG/EXIF/CFAPATTERN/VALUE"/>
          </cfaPattern>  
    
		</image>
	
		</metadata>
	</fits>	

</xsl:template>


<xsl:template name="precisioncomponents" >
<xsl:param name="components" select="1"/>
  <xsl:param name="precision" select="2"/>
  <xsl:param name="bps" select="3"/>
  <xsl:choose>
  	<xsl:when test="$components > 0">
     <xsl:call-template name="precisioncomponents">
       <xsl:with-param name="components" select="$components - 1"/>
       <xsl:with-param name="precision" select="$precision"/>
       <xsl:with-param name="bps" select='concat($bps,$precision," ")'/>
     </xsl:call-template>
 	</xsl:when>
 	<xsl:when test="$components = 0">
 		<!-- remove trailing space -->
   		<xsl:value-of select='substring($bps,1,string-length($bps) - 1)'/>
	</xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template name="trimDecimal">
	<xsl:param name="value"/>
	<xsl:choose>
		<xsl:when test="ends-with($value,'.0')">
			<xsl:value-of select="substring-before($value,'.0')"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$value"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
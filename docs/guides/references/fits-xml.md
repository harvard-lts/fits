### FITS XML

FITS converts the native output of each wrapped tool to a format called FITS XML which is described here. The XML schema for FITS XML is maintained by Harvard Library and located at [http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd](http://hul.harvard.edu/ois/xml/xsd/fits/fits_output.xsd).

#### identification
This section contains the file format in one or more identity blocks. If all the tools that processed the file and could identify it came up with the same format, there will only be one identity block. If there were tools that processed the file that came up with an alternative format, there will be multiple identity blocks. The tools that identified the format will be nested within the identity elements. Some examples follow. 

##### **EXAMPLE: SUCCESSFUL FORMAT IDENTIFICATION**

In this example, two tools (Jhove 1.5 and file utility 5.04) identified the format as Plain text with a MIME media type of text/plain.

```
<identification>
     <identity format="Plain text" mimetype="text/plain" toolname="FITS" toolversion="0.8.x">
          <tool toolname="Jhove" toolversion="1.5" />
          <tool toolname="file utility" toolversion="5.04" />
     </identity>
</identification>
```

##### **EXAMPLE: FORMAT CONFLICT**
In this example, there is a "format conflict". The tool Exiftool 9.13 identified the format as PCD with MIME media type image/x-photo-cd, but the tool Tika 1.3 identified the format as MPEG-1 Audio Layer 3. Notice in this case that the identification element will carry an attribute status value of CONFLICT.
 
````
<identification status="CONFLICT">

     <identity format="PCD" mimetype="image/x-photo-cd" toolname="FITS" toolversion="0.8.x">
          <tool toolname="Exiftool" toolversion="9.13" />
     </identity>
     <identity format="MPEG-1 Audio Layer 3" mimetype="audio/mpeg" toolname="FITS" toolversion="0.8.x">
          <tool toolname="Tika" toolversion="1.3" />
     </identity>

</identification>
````

#### fileinfo
This section contains basic technical metadata that isn't specific to any format:

- copyrightBasis element  
- copyrightNote element 
- created element (file creation date) 
- creatingApplicationName element (name of the software used to create the file) 
- creatingApplicationVersion element (version of the software used to create the file) 
- creatingos element (Operating system used to create the file) 
- filepath element (full filepath to the file) 
- filename element (name of the file) 
- fslastmodified element (last modified date based on file system metadata) 
- inhibitorType element (type of file inhibitor) 
- inhibitorTarget element (what is being inhibited) 
- lastmodified element (last modified date based on metadata embedded in the file) 
- md5checksum element (MD5 value for the file) 
- rightsBasis element 
- size element (size of the file in bytes) 

Each of the above elements will carry toolname and toolversion attributes to record the name of the tool that is the source of the information. In most cases there will also be a status attribute value equal to ```SINGLE_RESULT``` which means that there wasn't any conflicting information output by a tool. In some cases, for example if tools reported different file creation dates there will be a status value of ```CONFLICT```. 

#### filestatus
If any of the tools are able to validate files in this format, this section will contain validity information:

- message element (more information from tools about what was found) 
- valid element (whether or not the file was found to be valid) 
- well-formed element (whether or not the file was found to be well-formed) 

#### metadata
This section contains the format-specific technical metadata after each tool's native output has been normalized and consolidated by FITS. The elements in this section differ depending on the genre of the file format (audio, document, image, text, video). Each genre-specific section below lists the potential elements that can appear; the actual elements depend on what the tools are able to determine for the file.

<details>
<summary><strong>AUDIO ELEMENTS</strong></summary>

<div style="display:grid; grid-template-columns:repeat(3,auto);">
  <span>
		audioDataEncoding
	</span>
	<span>
		avgBitRate
	</span>
	<span>
		avgPacketSize
	</span>
	<span>
		bitDepth
	</span>
	<span>
		bitRate
	</span>
	<span>
		blockAlign
	</span>
	<span>
		blockSizeMax
	</span>
	<span>
		blockSizeMin
	</span>
	<span>
		byteOrder
	</span>
	<span>
		channels
	</span>
	<span>
		duration
	</span>
	<span>
		maxBitRate
	</span>
	<span>
		maxPacketSize
	</span>
	<span>
		numPackets
	</span>
	<span>
		numSamples
	</span>
	<span>
		offset
	</span>
	<span>
		sampleRate
	</span>
	<span>
		software
	</span>
	<span>
		soundField
	</span>
	<span>
		time
	</span>
	<span>
		wordSize
	</span>
</div>

</details>

<details>
<summary><strong>DOCUMENT ELEMENTS</strong></summary>

<div style="display:grid; grid-template-columns:repeat(3,auto);">
  <span>
		author
	</span>
	<span>
		hasAnnotations
	</span>
	<span>
		hasOutline
	</span>
	<span>
		isProtected
	</span>
	<span>
		isRightsManaged
	</span>
	<span>
		isTagged
	</span>
	<span>
		language
	</span>
	<span>
		pageCount
	</span>
	<span>
		title
	</span>
</div>

</details>

<details>
<summary><strong>IMAGE ELEMENTS</strong></summary>

<div style="display:grid; grid-template-columns:repeat(3,auto);">
  <span>
		apertureValue
	</span>
	<span>
		bitsPerSample
	</span>
	<span>
		brightnessValue
	</span>
	<span>
		byteOrder
	</span>
	<span>
		captureDevice
	</span>
	<span>
		cfaPattern
	</span>
	<span>
		cfaPattern2
	</span>
	<span>
		colorMap
	</span>
	<span>
		colorSpace
	</span>
	<span>
		compressionScheme
	</span>
	<span>
		digitalCameraManufacturer
	</span>
	<span>
		digitalCameraModelName
	</span>
	<span>
		digitalCameraSerialNo
	</span>
	<span>
		exifVersion
	</span>
	<span>
		exposureBiasValue
	</span>
	<span>
		exposureIndex
	</span>
	<span>
		exposureProgram
	</span>
	<span>
		exposureTime
	</span>
	<span>
		extraSamples
	</span>
	<span>
		flash
	</span>
	<span>
		flashEnergy
	</span>
	<span>
		fNumber
	</span>
	<span>
		focalLength
	</span>
	<span>
		gpsAltitudeRef
	</span>
	<span>
		gpsAltitude
	</span>
	<span>
		gpsAreaInformation
	</span>
	<span>
		gpsDateStamp
	</span>
	<span>
		gpsDestBearing
	</span>
	<span>
		gpsDestBearingRef
	</span>
	<span>
		gpsDestDistance
	</span>
	<span>
		gpsDestDistanceRef
	</span>
	<span>
		gpsDestLatitude
	</span>
	<span>
		gpsDestLatitudeRef
	</span>
	<span>
		gpsDestLongitude
	</span>
	<span>
		gpsDestLongitudeRef
	</span>
	<span>
		gpsDifferential
	</span>
	<span>
		gpsDOP
	</span>
	<span>
		gpsImgDirection
	</span>
	<span>
		gpsImgDirectionRef
	</span>
	<span>
		gpsLatitude
	</span>
	<span>
		gpsLatitudeRef
	</span>
	<span>
		gpsLongitude
	</span>
	<span>
		gpsLongitudeRef
	</span>
	<span>
		gpsMapDatum
	</span>
	<span>
		gpsMeasureMode
	</span>
	<span>
		gpsProcessingMethod
	</span>
	<span>
		gpsSatellites
	</span>
	<span>
		gpsSpeed
	</span>
	<span>
		gpsSpeedRef
	</span>
	<span>
		gpsStatus
	</span>
	<span>
		gpsTimeStamp
	</span>
	<span>
		gpsTrack
	</span>
	<span>
		gpsTrackRef
	</span>
	<span>
		gpsVersionID
	</span>
	<span>
		grayResponseUnit
	</span>
	<span>
		iccProfileName
	</span>
	<span>
		iccProfileVersion
	</span>
	<span>
		imageHeight
	</span>
	<span>
		imageProducer
	</span>
	<span>
		imageWidth
	</span>
	<span>
		isoSpeedRating
	</span>
	<span>
		lightSource
	</span>
	<span>
		maxApertureValue
	</span>
	<span>
		meteringMode
	</span>
	<span>
		oECF
	</span>
	<span>
		orientation
	</span>
	<span>
		primaryChromaticitiesBlueX
	</span>
	<span>
		primaryChromaticitiesBlueY
	</span>
	<span>
		primaryChromaticitiesGreenX
	</span>
	<span>
		primaryChromaticitiesGreenY
	</span>
	<span>
		primaryChromaticitiesRedX
	</span>
	<span>
		primaryChromaticitiesRedY
	</span>
	<span>
		qualityLayers
	</span>
	<span>
		referenceBlackWhite
	</span>
	<span>
		resolutionLevels
	</span>
	<span>
		samplesPerPixel
	</span>
	<span>
		samplingFrequencyUnit
	</span>
	<span>
		scannerManufacturer
	</span>
	<span>
		scannerModelName
	</span>
	<span>
		scannerModelNumber
	</span>
	<span>
		scannerModelSerialNo
	</span>
	<span>
		scanningSoftwareName
	</span>
	<span>
		scanningSoftwareVersionNo
	</span>
	<span>
		sensingMethod
	</span>
	<span>
		shutterSpeedValue
	</span>
	<span>
		spectralSensitivity
	</span>
	<span>
		subjectDistance
	</span>
	<span>
		tileHeight
	</span>
	<span>
		tileWidth
	</span>
	<span>
		whitePointXValue
	</span>
	<span>
		whitePointYValue
	</span>
	<span>
		xSamplingFrequency
	</span>
	<span>
		ySamplingFrequency
	</span>
	<span>
		YCbCrCoefficients
	</span>
	<span>
		YCbCrPositioning
	</span>
	<span>
		YCbCrSubSampling
	</span>
</div>

</details>

<details>
<summary><strong>TEXT ELEMENTS</strong></summary>

<div style="display:grid; grid-template-columns:repeat(3,auto);">
  <span>
		charset
	</span>
	<span>
		linebreak
	</span>
	<span>
		markupBasis
	</span>
	<span>
		markupBasisVersion
	</span>
	<span>
		markupLanguage
	</span>
</div>

</details>

<details>
<summary><strong>VIDEO ELEMENTS</strong></summary>

<div style="display:grid; grid-template-columns:repeat(3,auto);">
  <span>
		apertureSetting
	</span>
	<span>
		bitDepth
	</span>
	<span>
		bitRate
	</span>
	<span>
		blockSizeMax
	</span>
	<span>
		blockSizeMin
	</span>
	<span>
		channels
	</span>
	<span>
		creatingApplicationName
	</span>
	<span>
		dataFormatType
	</span>
	<span>
		digitalCameraManufacturer
	</span>
	<span>
		digitalCameraModelName
	</span>
	<span>
		duration
	</span>
	<span>
		exposureTime
	</span>
	<span>
		exposureProgram
	</span>
	<span>
		fNumber
	</span>
	<span>
		focus
	</span>
	<span>
		frameRate
	</span>
	<span>
		gain
	</span>
	<span>
		gpsAltitude
	</span>
	<span>
		gpsAltitudeRef
	</span>
	<span>
		gpsAreaInformation
	</span>
	<span>
		gpsDateStamp
	</span>
	<span>
		gpsDestBearing
	</span>
	<span>
		gpsDestBearingRef
	</span>
	<span>
		gpsDestDistance
	</span>
	<span>
		gpsDestDistanceRef
	</span>
	<span>
		gpsDestLatitude
	</span>
	<span>
		gpsDestLatitudeRef
	</span>
	<span>
		gpsDestLongitude
	</span>
	<span>
		gpsDestLongitudeRef
	</span>
	<span>
		gpsDifferential
	</span>
	<span>
		gpsDOP
	</span>
	<span>
		gpsImgDirection
	</span>
	<span>
		gpsImgDirectionRef
	</span>
	<span>
		gpsLatitude
	</span>
	<span>
		gpsLatitudeRef
	</span>
	<span>
		gpsLongitude
	</span>
	<span>
		gpsLongitudeRef
	</span>
	<span>
		gpsMapDatum
	</span>
	<span>
		gpsMeasureMode
	</span>
	<span>
		gpsProcessingMethod
	</span>
	<span>
		gpsSatellites
	</span>
	<span>
		gpsSpeed
	</span>
	<span>
		gpsSpeedRef
	</span>
	<span>
		gpsStatus
	</span>
	<span>
		gpsTimeStamp
	</span>
	<span>
		gpsTrack
	</span>
	<span>
		gpsTrackRef
	</span>
	<span>
		gpsVersionID
	</span>
	<span>
		imageHeight
	</span>
	<span>
		imageStabilization
	</span>
	<span>
		imageWidth
	</span>
	<span>
		sampleRate
	</span>
	<span>
		shutterSpeedValue
	</span>
	<span>
		videoStreamType
	</span>
	<span>
		whiteBalance
	</span>
	<span>
		xSamplingFrequency
	</span>
	<span>
		ySamplingFrequency
	</span>
</div>

</details>

#### toolOutput
When the fits.xml file is configured to also output the native tool output, this section will contain the output from each tool that ran against the file, each surrounded by tool elements like this example:

```
<toolOutput>
    <tool name="Jhove" version="1.5">
        [Jhove's native output]
    </tool>
    <tool name="file utility" version="5.04">
        [file utility's native output]
    </tool>
    <tool name="Exiftool" version="9.13">
        [ExifTool's native output]
    </tool>
    <tool name="Droid" version="6.1.3">
        [Droid's native output]
    </tool>
    <tool name="NLNZ Metadata Extractor" version="3.4GA">
        [NLNZ Metadata Extractor's native output]
    </tool>
    <tool name="OIS File Information" version="0.1">
        [OIS File Information's native output]
    </tool>
    <tool name="ffident" version="0.2">
        [ffident's native output]
    </tool>
    <tool name="Tika" version="1.3">
        [Tika's native output]
    </tool>
</toolOutput>
```

#### statistics
 In later versions of FITS this section was added to record how much time each wrapped tool spent processing the file. As shown in this example, when a tool isn't run against a file, a status attribute value of "did not run" is output:

```
<statistics fitsExecutionTime="3705">
     <tool toolname="OIS Audio Information" toolversion="0.1" status="did not run" />
     <tool toolname="ADL Tool" toolversion="0.1" status="did not run" />
     <tool toolname="Jhove" toolversion="1.5" executionTime="3703" />
     <tool toolname="file utility" toolversion="5.04" executionTime="95" />
     <tool toolname="Exiftool" toolversion="9.13" executionTime="167" />
     <tool toolname="Droid" toolversion="6.1.3" executionTime="18" />
     <tool toolname="NLNZ Metadata Extractor" toolversion="3.4GA" executionTime="11" />
     <tool toolname="OIS File Information" toolversion="0.1" executionTime="43" />
     <tool toolname="OIS XML Metadata" toolversion="0.2" status="did not run" />
     <tool toolname="ffident" toolversion="0.2" executionTime="6" />
     <tool toolname="Tika" toolversion="1.3" executionTime="20" />
</statistics>
```

#### Additional things to understand about the FITS XML schema
##### **STATUS ATTRIBUTE**
If multiple tools disagree on a format identity or other metadata values, a status attribute is added to the element with a value of ```CONFLICT```. If only a single tool reports a format identity or other metadata value, a status attribute is added to the element with a value of ```SINGLE_RESULT```. If multiple tools agree on a an identity or value, and none disagree, the status attribute is omitted. A ```PARTIAL``` value is written when the format can only be partially identified, for example a format name is identified but not a MIME media type.

##### **TOOL ORDERING PREFERENCE**
The ordering preference of the tools in xml/fits.xml determines the ordering of conflicting values. If the report-conflict configuration option is set to false then only the tool that first reported the element is displayed and the other conflicting values are discarded.

##### **RELATIONSHIP BETWEEN FORMAT IDENTIFICATION AND TECHINICAL METADATA**
All tools that agree on a format identity are consolidated into a single ```<identity>``` section.

**Technical metadata is only output (and a part of the consolidation process) for tools that were able to identify the file and that are listed in the first ```<identity>``` section. All other output is discarded**.

##### **TOOL OUTPUT NORMALIZATION**
It’s possible for tools to output conflicting data when they actually mean the same thing. For example, one tool could report the format of a PNG image as “Portable Network Graphics”, while another may report “PNG”. A tool could report a sampling frequency unit of “2”, while another may report the text string “inches”. If left alone, these would cause false positive conflicts to appear in the FITS consolidated output. These differences are converted in the XSLT that converts the native tool output into FITS XML. In general, FITS prefers text strings to numeric values (“inches” instead of “2”), and complete format names to abbreviations (“Portable Network Graphics” instead of “PNG”). If new tools or formats are being added to FITS then thorough testing should be done to ensure that any false positive conflicts are resolved.

---
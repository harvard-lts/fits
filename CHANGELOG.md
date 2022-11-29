## Unreleased (TBD)

- Java 11 or later is now required to run FITS
- Add embARC tool integration for processing DPX files.
- Add jpylyzer 2.1.0 for processing jp2 files.
- Tool upgrades
  - DROID 6.5.2
  - ExifTool 12.50
  - File 5.43
  - Jhove 1.26.1
  - MediaInfo 22.09
  - Tika 2.6.0
- Add FITS config option `consolidate-first-identity`, defaulted to `false`. This means that metadata from
all tools that identify a file is included, even if the identified format is _not_ the highest ranked
identity. This is a behavior change from previous versions. If you prefer the old behavior, set the value
to `true` in your `fits.xml`.
- Add CLI option `-t` for including raw tool output in the report.
- Add CLI option `-d` for enabling debug output.
- Fix DROID container format identification.
- exiftool raw output now only outputs XML.
- Fix bug where errors from one run were being incorrectly reported on subsequent runs.
- Improve ePub file identification.
- Improve TEI and KML file identification.
- Fix miscategorization of some tiffs as `TIFF EXIF`.
- Remove improper MIX namespace from Jhove output.
- Improve MIX generation by cycling through all available metadata elements looking for valid values.
- Add support for processing plaintext documents with exiftool.
- Do not infer a timezone for exif timestamps.
- Preserve whitespace within text elements. This ensures that significant spaces aren't trimmed from codec codes,
but could also result in unwanted whitespace in other elements.
- No longer run NZME on image files by default.
- General internal dependency upgrades.

## Version 1.5.5 (5/10/2022)

- Upgrade to ots 1.0.58

## Version 1.5.4 (5/3/2022)

- Revert the move to log4j2 and switch to reload4j instead

## Version 1.5.3 (5/2/2022)

- Move off log4j and onto log4j2 with slf4j

## Version 1.5.1 (1/3/2022)
- There are no functional changes in this release.
- A few minor updates of dependencies. More will follow in subsequent releases.
- Due to building on MacOS 11.6, there is now more output from system-level commands resulting in increased output for the test suite. Expected output in the tests have been updated to reflect this.

## Version 1.5.0 (9/10/2019)
- Format and mime type output for some video formats have now been normalized: .mov, .mp4, .meg-4, .avi, .rm, .rmvb, .wmv
- Add command line flag (-n) so that, if the input (-i) is a nested directory structure, the output (-o) will be similarly nested.
- Update Tika to v.1.22 due to security vulnerability.
- Update Exiftool to v.11.54.
- Improve JHOVE identification of TIFF EXIF files.
- Improve capture and reporting of tool errors.
- Update to latest OTS JAR file.
- Command line errors will no longer result in Java stack traces. Only a simple error message will be output.
- All command line errors will result in an exit code of 1. (As before, success will result with an exit code of 0.)
- Improve MediaInfo access to OS-specific native libraries; Remove the element ```<fits_home>.</fits_home>``` from fits.xml since it is no longer necessary for MediaInfo.
- Update fast-md5 dependency to latest version.

## Version 1.4.1 (4/10/2019)
- Update Tika to v.1.19.1 due to security vulnerability.
- Add m4a file extension to the file utility tool exclusion list to avoid a format conflict with Exiftool thus resulting in no metadata output.
- Add m4a file and test.
- Bug fixed: Identification of JP2000 files that are not well-formed did not go into generated metadata.

## Version 1.4.0 (11/7/2018)
- Added mp3 to Jhove file exclusion list in fits.xml; Add unit tests for mp3 files.
- DROID updated to version 6.4; updated signature file to V94.
    - There have been two minor modifications to this signature file. See the README.txt file within the same [tools/droid](https://github.com/harvard-lts/fits/tree/master/tools/droid) folder where the signature file is located.
- Tika updated to version 1.19.1.
    - There are now Tika outputs warnings -- which have been turned off via a new Tika configuration file -- about not processing JPEG2000 files. This is due to a missing (intentionally omitted) dependency that is not compatible with the Apache 2.0 license (for both Tika and FITS). This can be added independently. See: [JAI Image I/O](https://pdfbox.apache.org/2.0/dependencies.html#jai-image-io)
    - This new Tika configuration file, tika-config.xml, has been added to the directory xml/tika/. See [Apache Tika Configuration](https://tika.apache.org/1.18/configuring.html#Load_Error_Handling) to turn these warning messages back on.
- Exiftool updated to version 11.14.
    - This version tends to identify PDF/A documents as only PDF whereas earlier versions identified as PDF/A. However, other tools properly identify PDF/A documents. As a result, in rare cases metadata identified only by Exiftool is dropped due its  less specific file identification.
- Other bug fixes.

## Version 1.3.0 (6/7/2018)
- Allow for Java heap size to be set in the Unix (fits-env.sh) and Windows (fits.bat) shell scripts.
- The ZIP file artifact is now built using Maven instead of Ant. The output of this file is in the target directory.

## Version 1.2.1 (4/10/2018)
- Fix XSLT error on processing multi-page TIFF files. Only first value (from first page of a multi-page) of X- and Y-sampling frequency is used to calculate sampling frequency in FITS metadata.
- Add ability to point to external fits.xml config file from command line using parameter -f <path to fits.xml>
- Embedded 'file utility' tool, which accesses OS level 'file' application now provides additional data on OSX 10.12.6. This data needs to be normalized via XSLT to avoid conflicts in <identification> section of FITS metadata output.
- Fix Jhove XSLT not propertly pulling image height.
- Greatly reduced console output from unit test suite.
- Upgrade to Jhoave 1.20.0 -- results in additional metadata output.
- Add additional format output normalization to Jhove for WAVE files.
- Add additional format output normalization to File Utility for XML files.

## Version 1.2.0 (8/16/2017)
- Addition of new ContainerMD schema to FITS output. This format handle ZIP files. All metadata output is generated by the embedded DROID tool.
- DROID updated to version 6.3.
- DROID signature file update to version 90 (from version 82).
- Remove the deprecated static member from the Java class FitsOutput.
- Add additional normalization for Word Perfect formats in format_map.txt.
- Update to latest OTS JAR file.
- Update hierarchy for JPEG EXIF file format in fits_format_tree.xml.
- Add m4a file extension to DROID tool exclusion list in fits.xml since DROID misidentifies as a video file.

## Version 1.1.1 (5/30/2017)
- In the class `FitsOutput.java`, the instance method `getFitsVersion()` reflects the version of FITS that generated the FitsOutput instance, which could be a version different than the version of FITS currently being used. (The version of FITS in use is reflected by the value `Fits.VERSION`.
- A new configuration value has been added to fits.xml. The DROID tool within FITS has been modified so that it can process large files such as .mov and .mxf files much faster while providing the same metadata since metadata in those files can be retrieved at the beginning of the file. The new attribute `<droid_read_limit include-exts="mov,mxf" read-limit-kb="64" />` allows for configuring which file types to limit processing by DROID and by what amount.

## Version (1.1.0) (5/1/2017)
Due to changes in the public API in `Fits.java` and `FitsOutput.java`, and how new tools must be implemented to be included in FITS, the minor version number has been incremented.
- Some static members of `Fits.java` have been changed to be instance variables with getters.
- In `FitsOutput.java` the public static `VERSION` member is now accessed via a getter. The deprecated static `VERSION` will be removed in a future release. VERSION and the new method, getFitsVersion() currently give the version of the FITS version currently being run. In the next release the method getFitsVersion() will reflect the version of FITS that generated the FitsOutput instance, which could be a version different than the version of FITS being used.
- All new classes that implement the interface `Tool.java` can now also have a 1-argument constructor that takes `Fits` as an argument in order to access Fits members that were previously static. See the method `createToolClassInstance()` in `ToolBelt.java` to see how this is implemented with Reflection. For backward comptibility, the standard no-arg constructor still works.
- Refactor JUnit tests to bring common functionality into new base class for XMLUnit tests.

## Version 1.0.7 (3/22/2017)
-  LIBDRS-5676 FITS fails when attempting to convert a non-numeric String to an Integer or Double during conversion of FITS metadata to MIX format (and possibly to other standard output formats).
- FitsException.java and FitsToolException.java now take a Throwable rather than Exception as constructor arguments.
- Update JHOVE to version 1.16.5 (from 1.11).
- Add 'zip' file extension to the JHOVE exclusion list in fits.xml.

## Version 1.0.6 (3/2/2017)
- LIBDRS-5575 - Fix XSLT of tool output for JPEG EXIF files to provide better MIX metadata output.
- Update to most recent version of the OTS JAR file.

## Version 1.0.5 (2/1/17)
- Update to most recent version of the OTS JAR file.

## Version 1.0.4 (12/6/16)
- LIBDRS-5209 - FITS fails to generate MIX output for certain TIFFs

## Version 1.0.3 (10/18/16)
- LIBDRS-5053 - Add additional format and mimetype normalizations for MS Word documents.
- Remove source code from distribution release on http://fitstool.org as it should only be available on GitHub.
- LIBDRS-5076 - change logging message to WARN (from ERROR) when unable to convert FITS output to standard schema.

## Version 1.0.2 (10/13/16)
- FITS startup scripts resolve symlinks before loading relative paths. Pull Request [90](https://github.com/harvard-lts/fits/pull/90).
- Bug fix for File Utility detecting HTML files. Pull request [102](https://github.com/harvard-lts/fits/pull/102).
- Bug fix for File Utility handling XML files. Pull request [104](https://github.com/harvard-lts/fits/pull/104).
- LIBDRS-4878 - MIX metadata not extracted from some TIFF files.

## Version 1.0.1 (7/20/16)
- LIBDRS-4440 - Add "typeLabel" attribute on the Ebucore "containerFormat/comment" so that consumers of the Ebucore technical metadata can clearly differentiate between the format and formatProfile data created.
- LIBDRS-4571 - Improved error handling by adding input file name when problem creating standard schema output.
- LIBDRS-4677 - Bug fixes related to handling of certain types of video files via the FITS API.

## Version 1.0.0 (6/24/16)
- Update licensing at top of source files for consistency across FITS project.
- Integrate Video containing Closed Caption Roles and Relationships into FITS.
- Add .pcd file extension to JHOVE tool exclusion list in fits.xml.
- Remove font tag prefixes from JHOVE font output when processing PDF's; update corresponding tests; Bring all JHOVE XSLT up to 2.0.
- Remove unit tests using protected word processing files -- these files not handled by FITS.
- Make executable scripts have execute flag set in build.xml.
- Change compiler compliance to Java 7. Was mistakenly set at Java 8.
- Include Javadocs in fits.zip release artifact.
- Exclude video files from being processed by Tika. File extensions added to TikaTool in fits.xml.
- FITS JAR file build artifact now contains version number as part of file name.
- Create FITS output and, optionally, DocumentMD schema output for word processing formats (.doc, .docx, .epub, .odt, .pdf, .wp, .rtf, .wpd).
- All test classes now use JUnit 4 annotations.
- All test classes that use XmlUnit no longer extend XmlUnit but, instead, use utility classes.
- Some (but not yet all) test classes extend AbstractLoggingTest which loads a new Log4j configuration file, tests.log4j.properties, so that test logging can be set at different levels than the default log4j.properties file that is used by the FITS application.
- Separate logging for unit tests that extend base class AbstractLoggingTest.java which uses tests.log4j.properties to initialize logging.
- Fits.java now only initializes logging with this project's log4j.properties if the system property "log4j.configuration" hasn't already been set, possibly by an external system using FITS. This allows for not overriding the setting of the test classes as well.
- Make FITS logging via Log4j configuration file initialized externally by setting the Log4j environment variable -Dlog4j.configuration=/path/to/log4j.properties or -Dlog4j.configuration=file://path/to/log4j.properties more robust with fallback to default properties if property does not exist, is invalid, or exception is thrown.
- Many bug fixes and performance improvements.

## Version 0.10.2 (4/12/16)
- Add MXF codec creation from CodecId, as CodecCC is not present.
- Add validation of codecs in ots descriptor validation for MXF (ots).
- Remove some mimetypes from valid list in ots descriptor validation (ots).
- Normalize format name "Exchangeable Image File Format (Uncompressed)" for TIFF files to "Exchangeable Image File Format"
- Update AES31.jar
- Revised logic to properly handle video aspect ratio in ebucore.
- Add additional MXF codecs for video validation (ots).
- Revise call to instantiate AudioObject for AES generation due to revision in ots.jar for handling xsi namespace declaration during parsing.
- Add closed caption role (ots).
- Add HAS_SUPPLEMENT relationship to VideoObject (ots).

## Version 0.10.1 (3/4/16)
- fits.jar is no longer included in source control since it is the primary build artifact of this project. It will be available only in a FITS released ZIP file.
- fits.jar is no longer needed to run unit tests. All source files are compiled to the 'build' directory rather than being compiled into the source directories. This 'build'
  directory is now used to resolve classes rather than fits.jar.
- Revised code to now output a streamSize attribute for the Video tracks.
- Due to a NumberFormatException caused by using too large a numeric String to create an Integer, revised code to hold data in an Ebucore TechnicalAttributeLong.
- Remove the following from being processed by JHOVE due to slowness of precessing: avi,mov,mpg,mpeg,mkv,mp4,mpeg4,m2ts,mxf,ogv,mj2,divx,dv,m4v,m2v,ismv
- Move normalization for Quicktime and MPEG-4 to correct files
- Added VTT file support for video.
- revised the version of ots.jar to support validation of text/*.

## Version 0.10.0 (1/21/16)
- Added the latest OTS jar file with Codec changes and additional Video MIME type inclusion.
- Removed a conflict for DROID with some XML files.
- Removed some TODO and TBD defaults in Video byteOrder and compression.
- Removed default of video scanType for 'apcn'.
- Added MediaInfo video "standard" for FITS and Ebucore broadcastStandard.
- FITS-129 - Bug fix - Identity section of output does not properly remove tool identifying less specific file and mime type.

## Version 0.9.0 (12/7/15)
This release contains a major refactoring of how JAR files are handled by the tools that are Java-based.
JAR files specific to each tool are loaded within their own custom class loader.
Details for configuring a new Java-based tool are in edu.harvard.hul.ois.fits.tools.ToolBelt.java and on the [fitstool](http://fitstool.org) site.
Video - Normalized height and width values in FITS default output.
Update of the following tools used by FITS:
- DROID 6.1.5
- ExifTool 10.00
- Tika 1.10
- NLNZ Metadata Extractor 3.6GA
- JHOVE 1.11

'm2ts' files are now included in MediaInfo tool and excluded from Exiftool tool.
Revised licensing details of MediaInfo 3rd party libraries.
log4j.properties has been moved to the top level directory.

## Version 0.8.10 (10/21/15)

NOTE:
This is the first official release of Video support using the MediaInfo tool.

Out of the box, the FITS MediaInfo tool will ONLY be supporting the following 64-bit OSes:

    Windows 7 - 64-bit
    Mac OS X -  64-bit
    Red Hat 6.x - 64-bit

These come preconfigured for MediaInfo release 0.7.75.

For more information about the MediaInfo tool, please see:

    https://mediaarea.net

It is important to know that you MUST use a 64-bit JVM (JDK or JRE) to use the MediaInfo
tool as it is distributed in FITS.

The FITS MediaInfo tool will detect whether the OS is Windows/Mac OS X/LINUX and dynamically
load the associated MediaInfo libraries for any of these OSes.

If you wish to use a LINUX distribution other than Red Hat 6.x, or for more trouble-shooting details,
please see:

    http://projects.iq.harvard.edu/fits/faq

Below are the revisions done for FITS ## Version 0.8.10:

- Implement X/Y Channel Positions based upon text returned by Media Info.
- Revise Mime Type for MPEG-4 wrapped in Quicktime.
- Added "version" to some of the XMLUnit Tests as an attribute to ignore.
- Updated hclaps.jar to address the incorrect BEXT offset calculation being reported for WAV files.
- Updated fits.xml to suppress the invocation of NLNZ for WAV files.
- Revised internal handling of MediaInfo track IDs to manage those containing hex data.
- DROID now given preference over JHOVE

## Version 0.8.9 (08/12/15) - Not an Official Release

Removed old version of TIKA jar file
Updated SLF4J jar filed for DROID
Revised to use versioned OTS jar file
Minor Unit Test cleanup

## Version 0.8.8 (07/29/15) - Not an Official Release

- Added initial support for video files using the MediaInfo Tool
- Tika 1.8 update

## Version 0.8.7 (6/8/15) - Not an Official Release


- Added logging properties that were lost back into class.

## Version 0.8.6 (5/20/15)


- OTS.jar update: fixed bug for invalid MIX XML mix:LumaRed error for TIFF with JPEG compression.

## Version 0.8.5 (5/05/15)

- Introduce ability to suppress MD5 calculation by file type.

## Version 0.8.4 (1/26/15)

- Updated OTS.jar for MODS 3.5 issues.

## Version 0.8.3 (9/29/14)

- Fixed UMID parsing code issue for audio

## Version 0.8.2 (8/25/14)

- Ignores null or empty values output by tools for Title,Author,and CreationApplicationName.
- Prevents error of having identified format and MIME media-type from different tools while processing technical metadata.

## Version 0.8.1 (8/14/14)

- Uses a blank value instead of "<May be encrypted>" for Title,Author,CreationApplicationName for encrypted PDFs.
- Introduced a more specific TIFF format to the fits_format_tree.xml.
- Now includes technical metadata in the output from tools that identify a less specific, but parent format, as documented in the FITS format tree.

## Version 0.8 (1/28/14)

- Minor performance optimizations
- Nailgun server support
- Droid update to version 6
- Official support for Apache Tika
- Normalized and consolidated identification and technical metadata output differences introduced by the new and updated tools
- Improved stability when running against directories of files
- Fixed documentmd conversion bug when page count is null
- Fixed Exiftool resolution unit conversion error
- Fixed incomplete conversion from FITS output to standard metadata schema formats (MIX, TextMD, DocumentMD, AES57)
- Fixed concurrency issues when running multiple instances of FITS in a single JVM
    - DROID 6 signature file initialization
    - FITS_HOME initialization
- Better Error reporting and logging

## Version 0.7.x (--/--/--)

...skipping

## Version 0.6.2 (3/18/13)

-Initial merge and commit from openfits
-Updated Exiftool to 9.06
-Improved video support with Exiftool
-Fixed ordering of File utility command line options
-New configuration option to limit the max number of threads
-Fixed bug in how the include-ext and exclude-ext lists were being processed
-Added a new option to enable output of statistics
-Initial experimental support for Apache Tika - disabled by default.

## Version 0.6.1 (04/25/12)

- Fixed issue 27: Fits with -r overwrites output files with same filename but different path
  http://code.google.com/p/fits/issues/detail?id=27
- Added new output option -xc.  This will output converted standard output format (MIX, TextMD, DocumentMD, AES57)
  within the FITS XML <fits>/<metadata>/<xxxx>/<standard> element, where <xxxx> is either <image>, <text>, <document>, <audio>, or <video>.
- Added eml to the Jhove exclude-ext list
- Improved FitsOutput API

## Version 0.6.0 (10/25/11)

- Performance enhancement to make all tools run in parallel in individual threads
- Added the ADL tool for the identification of Audio Decision List files
- Added options to process directories of files.  When -i is set to a directory -o must also be set to a directory.
  The new -r option will process directories recursively when -i is a directory.
- added <filepath> element to FileInfo tool, updated fits_output.xsd
- Fixed bug in normalizing Exiftool format output for TIFFs. "TIFF EXIF" is not output as "Tagged Image File Format"
- Fix to use <fileinfo>/<created> value for <mix:GeneralCaptureInformation>/<mix:dateTimeCreated>  when using -x switch

## Version 0.5.0 (2/24/11)

- Improved support for audio formats
- Ability to convert audio metadata to the AES audioObject schema using the -x option
- Now ising NLNZ VERSION element instead of VERSION-NAME for MP3 files
- Fixed incorrect JP2 version reported by Exiftool
- Better identification of JP2 and JPX images (OIS bug 2876)
- Updated File Utility XSLT to improve format output (OIS bug 2897)
- Fixed DROID format output for SVG files (OIS bug 2968)
- Added ability to extract DTD references to the XmlMetadata tool from XML DOCTYPE declaration (OIS bug 2967)
- Changed file utility call to use --mime instead of -i.  File 5.03 on OS X uses -I rather than -i to print
  the mime type and mime encoding.  --mime works on Linux and OS X.
- Tweaked Jhove XSLT conversion of image height and width elements for files containing multiple images.  
  When multiple height or width values are found only the first one is used.
- Improved identification of EXIF and JFIF JPEGs.

## Version 0.4.3 (10/29/10)

- Added -e cdf parameter to Unix File Utility call
- Removed Saxon transformer factory system property in ToolBase.java
- Fixed namespace of <toolOutput> elements
- Fixed namespace issues with FileInfo and XmlMetadata tools

## Version 0.4.2 (7/6/10)

- Fixed an issue with the <identification> element status attribute. If multiple tools agree
  with the identified format and there are no other identity conflicts then the attribute
  is omitted.  The SINGLE_RESULT status attribute is used only if a single tool successfully
  identifies the file. Thanks to Swithun Crowe for the patch.
- Added -e cdf parameter to the Windows File Utility call to prevent crashes when processing
  files that produce a large amount of output.

## Version 0.4.1 (06/30/10)

- Improved handling of File Utility output for text files
- Added 'Postscript' as a more specific format of 'Plain text' to fits_format_tree.xml
- Added 'JPEG File Interchange Format' as a more specific format of 'Raw JPEG Stream' to fits_format_tree.xml

## Version 0.4 (06/28/10)

- Fixed Jhove colorspace output, mapping "Greyscale" to "Grayscale"
- Updated DROID signature file to V35, made configurable in xml/fits.xml
- Updated OTS-Schemas.jar fixing several issues with MIX output conversion
- Fixed Exiftool primary chromaticities output
- Fixed Exiftool and Jhove orientation output.
- Updated File Utility to support version 5.03
- Changed format tree <branch format="text"> to <branch format="Plain text">
- Added Hypertext Markup Language as a child branch of Plain Text in the format tree
- Standard XML output (-x) is now pretty printed

## Version 0.3.2 (02/18/10)

- Fixed identification of PDF/A and PDF/X formats
- Added Ant build files to release (issue #8)
- Added patch from beerchr1 to tweak video output and support flash video (issue #9)

## Version 0.3.1 (02/01/10)

- Fixed error when trying to convert FITS output to a standard metadata schema for
  a file that does not have sufficient output that can be converted
- Fixed bugs in FitsOutput
- Added patch from beerchr1 to add basic video support (issue #7)

## Version 0.3 (01/14/10)

- Added API support for converting output to MIX, TextMD and DocumentMD
- Added command line support (-x) for outputting MIX, TextMD or DocumentMD
- Updated NLNZ, Jhove, and Exiftool output values to be compatible with MIX
- Added API feature (issue #4) to disable individual tools
- Updated Jhove to version 1.5
- Using byteoffset=true option for Jhove TIFF module
- Fixed issue #3: using relative path instead of $FITS_HOME/path in xml/nlnz/config.xml & in XSLT when for xml/fits_output.xsd
- Fixed issue #5: FITS 0.2.6 output file showing FITS version as 0.2.5
- Fixed usage of Exiftool scanner/digital camera make and model output
- Adjusted image metadata values to be compatible with MIX

## Version 0.2.6 (8/6/09)

- Fixed bug that prevented external identifiers from being output in <identity> sections

## Version 0.2.5 (7/28/09)

- Fixed GZIP identification output
- Fixed display-tool-output option so that all tool output is displayed
  instead of just the tools that reported a successful identification
- Exiftool mime of "application/unknown" is handled as an unknown identification (ICC files)
- All tools default unknown identification output is now normalized to 'Uknown Binary' and
  'application/octet-stream'.
- Added a partial identification status when a tool finds only a mimetype or a format name.
  If no valid identities (mimetype and format) are returned by any tools, then tool output is
  scanned according to the order defined in fits.xml for a partial identity (only a valid mimetype or format).
  If a partial identity is not found then the unknown identity returned by the first tool defined
  in fits.xml is used.

## Version 0.2.4 (6/5/09)

- Updated Jhove to release version 1.3

## Version 0.2.3 (5/1/09)

- Fixed exception handling when a tool throws an error while examining a file
  All caught exceptions are collected and passed back with the FitsOutput
- Changed when tools and consolidator are initialized to improve performance
  when using the FITS API and processing multiple files.
- Fixed occasional JPEG Jhove bug by setting home and log locations in jhove.conf
- Adding additional output value mappings to NLNZ JPEG XSLT.

## Version 0.2.2 (4/29/09)

- Fixed output of empty <version> elements
- Fixed NPE in OIS XmlMetadata tool
- Fixed File Utility XSLT JPEG regex
- Fixed bug in tool output consolidator when no valid
  identities are reported

## Version 0.2.1

- Improved FitsOutput API. It now includes access to well-formed and valid fields.
- Updated Exiftool to verison 7.74
- Fixed successful identification tests for Jhove, File Utility and NLNZ ME
- Fixed schema so mimetype and format identity attributes are optional
- Tweaked File utility wrapper

## Version 0.2.0

- Modified and recompiled NLNZ metadata.jar to disable log messages sent to the console
- Various tweaks to the OISConsolidator
- Tweaks to all XSLT to resolve conflicts
- Added classes for testing
- Updated Jhove to latest version in CVS
- Added all Exiftool supported formats
  RELEASE NOTES

## Version 0.8.10 (08/21/15)

Updated DROID to v.6.1.5
Updated NLNZ tool to v.3.6GA
Create directory for any test output

## Version 0.8.9 (08/12/15)

Removed old version of TIKA jar file
Updated SLF4J jar filed for DROID
Revised to use versioned OTS jar file
Minor Unit Test cleanup

## Version 0.8.8 (07/29/15)

- Added initial support for video files using the MediaInfo Tool
- Tika 1.8 update

## Version 0.8.7 (6/8/15)


- Added logging properties that were lost back into class.

## Version 0.8.6 (5/20/15)


- OTS.jar update: fixed bug for invalid MIX XML mix:LumaRed error for TIFF with JPEG compression.

## Version 0.8.5 (5/05/15)


- Introduce ability to suppress MD5 calculation by file type.

## Version 0.8.4 (1/26/15)

- Updated OTS.jar for MODS 3.5 issues.

## Version 0.8.3 (9/29/14)

- Fixed UMID parsing code issue for audio

## Version 0.8.2 (8/25/14)

- Ignores null or empty values output by tools for Title,Author,and CreationApplicationName.
- Prevents error of having identified format and MIME media-type from different tools while processing technical metadata.

## Version 0.8.1 (8/14/14)

- Uses a blank value instead of "<May be encrypted>" for Title,Author,CreationApplicationName for encrypted PDFs.
- Introduced a more specific TIFF format to the fits_format_tree.xml.
- Now includes technical metadata in the output from tools that identify a less specific, but parent format, as documented in the FITS format tree.

## Version 0.8 (1/28/14)
---------------------
- Minor performance optimizations
- Nailgun server support
- Droid update to version 6
- Official support for Apache Tika
- Normalized and consolidated identification and technical metadata output differences introduced by the new and updated tools
- Improved stability when running against directories of files
- Fixed documentmd conversion bug when page count is null
- Fixed Exiftool resolution unit conversion error
- Fixed incomplete conversion from FITS output to standard metadata schema formats (MIX, TextMD, DocumentMD, AES57)
- Fixed concurrency issues when running multiple instances of FITS in a single JVM
    - DROID 6 signature file initialization
    - FITS_HOME initialization
- Better Error reporting and logging

## Version 0.7.x (--/--/--)
---------------------
...skipping

## Version 0.6.2 (3/18/13)
---------------------
-Initial merge and commit from openfits
-Updated Exiftool to 9.06
-Improved video support with Exiftool
-Fixed ordering of File utility command line options
-New configuration option to limit the max number of threads
-Fixed bug in how the include-ext and exclude-ext lists were being processed
-Added a new option to enable output of statistics
-Initial experimental support for Apache Tika - disabled by default.

## Version 0.6.1 (04/25/12)

- Fixed issue 27: Fits with -r overwrites output files with same filename but different path
  http://code.google.com/p/fits/issues/detail?id=27
- Added new output option -xc.  This will output converted standard output format (MIX, TextMD, DocumentMD, AES57)
  within the FITS XML <fits>/<metadata>/<xxxx>/<standard> element, where <xxxx> is either <image>, <text>, <document>, <audio>, or <video>.
- Added eml to the Jhove exclude-ext list
- Improved FitsOutput API

## Version 0.6.0 (10/25/11)

- Performance enhancement to make all tools run in parallel in individual threads
- Added the ADL tool for the identification of Audio Decision List files
- Added options to process directories of files.  When -i is set to a directory -o must also be set to a directory.
  The new -r option will process directories recursively when -i is a directory.
- added <filepath> element to FileInfo tool, updated fits_output.xsd
- Fixed bug in normalizing Exiftool format output for TIFFs. "TIFF EXIF" is not output as "Tagged Image File Format"
- Fix to use <fileinfo>/<created> value for <mix:GeneralCaptureInformation>/<mix:dateTimeCreated>  when using -x switch

## Version 0.5.0 (2/24/11)

- Improved support for audio formats
- Ability to convert audio metadata to the AES audioObject schema using the -x option
- Now ising NLNZ VERSION element instead of VERSION-NAME for MP3 files
- Fixed incorrect JP2 version reported by Exiftool
- Better identification of JP2 and JPX images (OIS bug 2876)
- Updated File Utility XSLT to improve format output (OIS bug 2897)
- Fixed DROID format output for SVG files (OIS bug 2968)
- Added ability to extract DTD references to the XmlMetadata tool from XML DOCTYPE declaration (OIS bug 2967)
- Changed file utility call to use --mime instead of -i.  File 5.03 on OS X uses -I rather than -i to print
  the mime type and mime encoding.  --mime works on Linux and OS X.
- Tweaked Jhove XSLT conversion of image height and width elements for files containing multiple images.  
  When multiple height or width values are found only the first one is used.
- Improved identification of EXIF and JFIF JPEGs.

## Version 0.4.3 (10/29/10)

- Added -e cdf parameter to Unix File Utility call
- Removed Saxon transformer factory system property in ToolBase.java
- Fixed namespace of <toolOutput> elements
- Fixed namespace issues with FileInfo and XmlMetadata tools

## Version 0.4.2 (7/6/10)

- Fixed an issue with the <identification> element status attribute. If multiple tools agree
  with the identified format and there are no other identity conflicts then the attribute
  is omitted.  The SINGLE_RESULT status attribute is used only if a single tool successfully
  identifies the file. Thanks to Swithun Crowe for the patch.
- Added -e cdf parameter to the Windows File Utility call to prevent crashes when processing
  files that produce a large amount of output.

## Version 0.4.1 (06/30/10)

- Improved handling of File Utility output for text files
- Added 'Postscript' as a more specific format of 'Plain text' to fits_format_tree.xml
- Added 'JPEG File Interchange Format' as a more specific format of 'Raw JPEG Stream' to fits_format_tree.xml

## Version 0.4 (06/28/10)

- Fixed Jhove colorspace output, mapping "Greyscale" to "Grayscale"
- Updated DROID signature file to V35, made configurable in xml/fits.xml
- Updated OTS-Schemas.jar fixing several issues with MIX output conversion
- Fixed Exiftool primary chromaticities output
- Fixed Exiftool and Jhove orientation output.
- Updated File Utility to support version 5.03
- Changed format tree <branch format="text"> to <branch format="Plain text">
- Added Hypertext Markup Language as a child branch of Plain Text in the format tree
- Standard XML output (-x) is now pretty printed

## Version 0.3.2 (02/18/10)

- Fixed identification of PDF/A and PDF/X formats
- Added Ant build files to release (issue #8)
- Added patch from beerchr1 to tweak video output and support flash video (issue #9)

## Version 0.3.1 (02/01/10)

- Fixed error when trying to convert FITS output to a standard metadata schema for
  a file that does not have sufficient output that can be converted
- Fixed bugs in FitsOutput
- Added patch from beerchr1 to add basic video support (issue #7)

## Version 0.3 (01/14/10)

- Added API support for converting output to MIX, TextMD and DocumentMD
- Added command line support (-x) for outputting MIX, TextMD or DocumentMD
- Updated NLNZ, Jhove, and Exiftool output values to be compatible with MIX
- Added API feature (issue #4) to disable individual tools
- Updated Jhove to version 1.5
- Using byteoffset=true option for Jhove TIFF module
- Fixed issue #3: using relative path instead of $FITS_HOME/path in xml/nlnz/config.xml & in XSLT when for xml/fits_output.xsd
- Fixed issue #5: FITS 0.2.6 output file showing FITS version as 0.2.5
- Fixed usage of Exiftool scanner/digital camera make and model output
- Adjusted image metadata values to be compatible with MIX

## Version 0.2.6 (8/6/09)

- Fixed bug that prevented external identifiers from being output in <identity> sections

## Version 0.2.5 (7/28/09)

- Fixed GZIP identification output
- Fixed display-tool-output option so that all tool output is displayed
  instead of just the tools that reported a successful identification
- Exiftool mime of "application/unknown" is handled as an unknown identification (ICC files)
- All tools default unknown identification output is now normalized to 'Uknown Binary' and
  'application/octet-stream'.
- Added a partial identification status when a tool finds only a mimetype or a format name.
  If no valid identities (mimetype and format) are returned by any tools, then tool output is
  scanned according to the order defined in fits.xml for a partial identity (only a valid mimetype or format).
  If a partial identity is not found then the unknown identity returned by the first tool defined
  in fits.xml is used.

## Version 0.2.4 (6/5/09)

- Updated Jhove to release version 1.3

## Version 0.2.3 (5/1/09)

- Fixed exception handling when a tool throws an error while examining a file
  All caught exceptions are collected and passed back with the FitsOutput
- Changed when tools and consolidator are initialized to improve performance
  when using the FITS API and processing multiple files.
- Fixed occasional JPEG Jhove bug by setting home and log locations in jhove.conf
- Adding additional output value mappings to NLNZ JPEG XSLT.

## Version 0.2.2 (4/29/09)

- Fixed output of empty <version> elements
- Fixed NPE in OIS XmlMetadata tool
- Fixed File Utility XSLT JPEG regex
- Fixed bug in tool output consolidator when no valid
  identities are reported

## Version 0.2.1

Improved FitsOutput API. It now includes access to well-formed and valid fields.
- Updated Exiftool to verison 7.74
- Fixed successful identification tests for Jhove, File Utility and NLNZ ME
- Fixed schema so mimetype and format identity attributes are optional
- Tweaked File utility wrapper

## Version 0.2.0

- Modified and recompiled NLNZ metadata.jar to disable log messages sent to the console
- Various tweaks to the OISConsolidator
- Tweaks to all XSLT to resolve conflicts
- Added classes for testing
- Updated Jhove to latest version in CVS
- Added all Exiftool supported formats
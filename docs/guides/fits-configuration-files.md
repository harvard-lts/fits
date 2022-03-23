### FITS configuration files

The FITS configuration files are located in the [xml directory](https://github.com/harvard-lts/fits/tree/dev/xml).

**The FITS XML output is highly affected by how FITS is configured. In particular, the order of tools near the top of the fits.xml configuration file specifies which tools FITS should prefer when they give conflicting information and if FITS should ignore tool output for particular formats. FITS comes pre-configured based on testing different tools with different formats and the default configuration should only be changed with a great deal of care and testing.**

#### [fits.xml](https://github.com/harvard-lts/fits/blob/dev/xml/fits.xml)
This is the main configuration file for FITS. The key pieces are described here:

##### tool element
{:.no_toc}

Lists all the tools that FITS should know about. The order of these elements determines the preference in favoring one tool over another, for example when there are multiple tools reporting formats or technical metadata for a file.

The following are attributes of the tool element:
- **class** (required) - specifies the fully qualified name of the Java class that implements the Tool interface
- **exclude-exts** (optional) - specifies by file extension files that the tool should not process. This is useful if you know a tool misidentifies or generates inaccurate metadata for specific types of files
- **include-exts** (optional) - indicates to FITS to use the information reported by the tool for particular file extensions
- **classpath-dirs** (optional) - for Java-based tools when there is a need to provide class isolation via a custom class loader. By convention, any tool-specific JAR files, including any 3rd-party dependencies, should be put into a ```lib/<tool-name>``` directory. Additional directories can be added for configuration files that need to be discovered via the tool’s class loader. These files might go in, for example, ```xml/<tool-name>```. This custom class loader will load classes from the bottom up (rather than the standard Java top down scheme). The value for this attribute is the name of the sub-directory containing any JAR files for this tool.

##### output element
{:.no_toc}

Contains elements that control FITS metadata output:
- **data-consolidator** - specifies the class to use for consolidating the tool output. It's possible to use custom logic to control the tool output consolidation processes by creating a class implementing the ToolOutputConsolidator interface.
- **display-tool-output** - whether or not to append the output of the native tool output for each tool to the final consolidated FITS XML output, can be set to either true or false
- **report-conflicts** - whether or not to report when there is conflicting tool information about formats or metadata, can be set to either true or false. If set to true, conflicts will be shown in the final FITS XML output. If set to false, only the output from the most preferred tool (controlled by the ordering of the tool elements) will be displayed.
- **validate-tool-outpu**t - whether or not to validate tool output, can be set to either true or false. Generally this should be set to true. Setting it to false will disable schema validation of the output from each tool.
  - NOTE: The local copy provided with FITS is used for validation during the file processing. As each tool has its output converted to the FITS format it is validated using the local schema. This can be disabled by setting ```<validate-tool-output>``` in xml/fits.xml to false. 
- **internal-output-schema** - the location of the local copy of the XML schema specifying the FITS XML output, used during FITS execution
- **external-output-schema** - the location of the remote XML schema controlling the FITS XML output, written to the output file
- **fits-xml-namespace** - the XML namespace to use in the FITS XML output
- **enable-statistics** - whether or not to output the statistics block containing performance metrics about each tool that processed the file, can be set to either true or false enable-checksum - whether or not to compute the MD5 checksum for the file, can be set to either true or false
- **checksum­-exclusions** - ­file extensions to be excluded in the checksum calculation.
  - NOTE: This configuration parameter will only be enforced if the above enable­-checksum is set to true. 


##### process/maxThreads element
{:.no_toc}

The maximum number of threads to use

##### droid_sigfile element
{:.no_toc}

The signature file to use with the Droid tool. [Get the list of all previously released signature files](https://www.nationalarchives.gov.uk/aboutapps/pronom/droid-signature-files.htm)


##### droid_read_limit element
{:.no_toc}

This allows for limiting the amount of a file (from its beginning) that is to be examined by the DROID tool (in order to increase processing speed). For example, for some types of large video and audio files, only the first 64K bytes need to be examined to extract relevant metadata. The attribute **include-exts** sets the file extension that this limiter should be applied to, and the attribute **read-limit-kb** sets the limit, in kilobytes, of how much of the beginning of the designated file types should be examined. The default behavior (when this element remains commented-out) is for DROID to examine all files in their entirety. 


#### [fits_format_tree.xml](https://github.com/harvard-lts/fits/blob/dev/xml/fits_format_tree.xml)
Certain formats are a more specific subset of a more general format. The format tree in this file specifies these relationships. Nested formats are more specific versions of the formats they are nested under. FITS uses this to know when to report format conflicts and when it should report a more specific format. 

During output consolidation the format tree is consulted, and any less specific format identities are thrown out. For example, OpenOffice text document formats are ZIP-based. Some tools identify these files as ZIP, and others as ODT. Any tools identifying the file as a ZIP would be discarded according to the rules set by the format tree. 

An example follows using a snippet of the format tree:

```
<branch format="JPEG 2000">

    <branch format="JPEG 2000 JP2" />
    <branch format="JPEG 2000 JPX" />

</branch>
```

The above snippet of the format tree should be interpreted as: JPEG 2000 JP2 and JPEG 2000 JPX are more specific forms of the JPEG 2000 format. If one FITS-wrapped tool were to report the format of a file as JPEG 2000 and another reported it as JPEG 2000 JP2, FITS would report the more specific format (JPEG 2000 JP2) and would not report that there was a format conflict (because both tools were technically correct). 


#### [fits_output.xsd](https://github.com/harvard-lts/fits/blob/dev/xml/fits_output.xsd)
**Not listed on site, but located in xml directory - should we expand on this?**


#### [fits_xml_map.xml](https://github.com/harvard-lts/fits/blob/dev/xml/fits_xml_map.xml)
This mapping file is used to normalize the values output by some of the tools that FITS wraps, for example to change Jhove's Greyscale value to Grayscale. It allows substitution of one value for another on a tool by tool, element by element basis.

For example, if a tool outputs the value "2" as the sampling frequency unit for an image, but you want to use the text string "inches" instead, you could add an entry to fits_xml_map.xml. Mappings are applied automatically when a tool creates its FITS output, prior to output consolidation. You must specify the tool name, version, and element name that you want mapped. Currently all mapping-related needs are handled in the tool's XSLT. 


#### [format_map.txt](https://github.com/harvard-lts/fits/blob/dev/xml/format_map.txt)
The file is used to normalize format names output by some of the tools that FITS wraps. 


#### [mime_map.txt](https://github.com/harvard-lts/fits/blob/dev/xml/mime_map.txt)
The file is used to normalize MIME media type values output by some of the tools that FITS wraps.


#### [mime_to_format_map.txt](https://github.com/harvard-lts/fits/blob/dev/xml/mime_to_format_map.txt)
Used to map format names to MIME media types for some of the tools that FITS wraps.


#### [prettyprint.xslt](https://github.com/harvard-lts/fits/blob/dev/xml/prettyprint.xslt)
**Not listed on site, but located in xml directory - should we expand on this?**


#### [xslt_map.xsd](https://github.com/harvard-lts/fits/blob/dev/xml/xslt_map.xsd)
**Not listed on site, but located in xml directory - should we expand on this?**

---
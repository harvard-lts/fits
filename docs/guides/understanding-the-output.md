## How to read a FITS output

### Output format
The output format of FITS is controlled by the options used when executing FITS, how FITS is configured and the genre of the format. 

The format of the output will include one or more of the following:

#### FITS XML
{:.no_toc}
- This is the default output described in detail [here](https://projects.iq.harvard.edu/fits/fits-xml) 

#### Standardized Metadata
{:.no_toc}
- This is format genre-specific technical metadata in community-standard XML schemas
- When using the command-line, use the -x parameter (to just get the output in standard metadata), or -xc (to get FITS XML in addition to standard metadata)
- The specific XML schema used is determined by the format genre - for more information see the [standard metadata schemas](https://projects.iq.harvard.edu/fits/standard-metadata-schemas)

#### Raw output
{:.no_toc}
- This is the pre-normalized output of each tool run against the file
- This is specified by the display-tool-output configuration property in the [fits.xml configuration file](https://projects.iq.harvard.edu/fits/fits-configuration-files#fits_xml_config)

### Output destination

#### Terminal
{:.no_toc}
- This is the default unless an output file is specified

#### File
{:.no_toc}
- When using the command-line, use the -o <file> parameter
- When using the Java API, use the FitsOutput.saveToDisk method

---
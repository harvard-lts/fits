## Overview of FITS processing

FITS works in different stages as shown in the image below.

<img src="../images/fits_process.jpg">

The steps are described in more detail here.

1. First the configuration files are read. This determines which tools are called and can affect the output. 
2. Each tool (JHOVE etc.) is called in parallel to process the file or directory of files (depending on the option used). Each tool's native output is converted to FITS XML. 
3. All of the FITS XML is consolidated into a single instance of FITS XML.
4. The FITS XML is converted to standard XML (e.g. MIX) (if this option was requested for example by using the -x parameter on the command line).

For a more technical description of FITS processing - see the [Developer Manual](https://github.com/harvard-lts/fits/wiki/Developer-Manual#fits-processing).

---
//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//


package edu.harvard.hul.ois.fits.tools.exiftool;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolCLIException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;
import edu.harvard.hul.ois.fits.tools.utils.XsltTransformMap;

/**
 *  The glue class for invoking Exiftool under FITS.
 */
public class Exiftool extends ToolBase {

	private boolean osIsWindows = false;
	private boolean osHasPerl = false;
	private List<String> winCommand = new ArrayList<String>(Arrays.asList(Fits.FITS_TOOLS_DIR+"exiftool/windows/exiftool.exe"));
	private List<String> unixCommand = new ArrayList<String>(Arrays.asList("perl",Fits.FITS_TOOLS_DIR+"exiftool/perl/exiftool"));
	private List<String> perlTestCommand = Arrays.asList("which", "perl");
	private final static String TOOL_NAME = "Exiftool";
	private boolean enabled = true;
    private Fits fits;

    private final static String exiftoolFitsConfig = Fits.FITS_XML_DIR+"exiftool"+File.separator;
    private final static String genericTransform = "exiftool_generic_to_fits.xslt";

    private static final Logger logger = Logger.getLogger(Exiftool.class);

	public Exiftool(Fits fits) throws FitsException {
		super();
		this.fits = fits;
        logger.debug ("Initializing Exiftool");

		String osName = System.getProperty("os.name");
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		String versionOutput = null;
		List<String> infoCommand = new ArrayList<String>();
		if (osName.startsWith("Windows")) {
			//use provided Windows exiftool.exe
			osIsWindows = true;
			infoCommand.addAll(winCommand);
			info.setNote("exiftool for windows");
			logger.debug("Exiftool will use Windows environment");
		}
		else if (testOSForPerl()){
			osHasPerl = true;
			//use OS version of perl and the provided perl version of exiftool
			infoCommand.addAll(unixCommand);
			info.setNote("exiftool for unix");
            logger.debug("Exiftool will use Unix Perl environment");
		}

		else {
		    logger.error ("Perl and Windows not supported, not running Exiftool");
			throw new FitsToolException("Exiftool cannot be used on this system");
		}
		infoCommand.add("-ver");
		versionOutput = CommandLine.exec(infoCommand,null);
		info.setVersion(versionOutput.trim());
		transformMap = XsltTransformMap.getMap(exiftoolFitsConfig+"exiftool_xslt_map.xml");
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("Exiftool.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		List<String> execCommand = new ArrayList<String>();
		//determine if the file can be used on the current platform
		if (osIsWindows) {
			//use provided Windows File Utility
			execCommand.addAll(winCommand);
			execCommand.add(file.getPath());
		}
		else if(osHasPerl) {
			//use file command in operating system
			execCommand.addAll(unixCommand);
			execCommand.add(file.getPath());
		}
		else {
			//Tool cannot be used on this file on this system
			return null;
		}
		//Output in tabbed format with tag names instead of descriptive names
		execCommand.add("-t");
		execCommand.add("-s");
		execCommand.add("-a");

		logger.debug("Launching Exiftool, command = " + execCommand);
		String execOut = CommandLine.exec(execCommand,null);
		logger.debug("Finished running Exiftool");

		String[] outParts = execOut.split("\n");
		String format = null;
		for(String s : outParts) {
			s = s.toLowerCase();
			String[] lineParts = s.split("\t");
			if(lineParts[0].equalsIgnoreCase("filetype")) {
				format = lineParts[1].trim();
				break;
			}
		}

		Document rawOut = createXml(execOut);

		/*
		Document exifDoc = null;
		try {
			exifDoc = saxBuilder.build(new StringReader(execOut));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing ffident XML Output",e);
		}

		String format = XmlUtils.getDomValue(exifDoc.getDocument(),"File:FileType");
		exifDoc.getRootElement().getChild("rdf:Description/File:FileType");
		Namespace ns = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		String test = exifDoc.getRootElement().getChildText("rdf:Description",ns);
		*/

		String xsltTransform = null;
		if(format != null) {
			xsltTransform = (String)transformMap.get(format.toUpperCase());
		}

		Document fitsXml = null;
		if(xsltTransform != null) {
			fitsXml = transform(exiftoolFitsConfig+xsltTransform,rawOut);
		}
		else {
			//use generic transform
			fitsXml = transform(exiftoolFitsConfig+genericTransform,rawOut);
		}
		output = new ToolOutput(this,fitsXml,rawOut, fits);
		//}

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("Exiftool.extractInfo finished on " + file.getName());
		return output;
	}

	public boolean testOSForPerl() throws FitsToolCLIException {
		String output = CommandLine.exec(perlTestCommand,null);
		if(output == null || output.length() == 0) {
			return false;
		}
		else {
			return true;
		}
	}

	private Document createXml(String execOut) throws FitsToolException {
    	StringWriter out = new StringWriter();

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("\n");
        out.write("<exiftool>");
        out.write("\n");

        out.write("<rawOutput>\n"+StringEscapeUtils.escapeXml(execOut));
        out.write("</rawOutput>");
        out.write("\n");

    	String[] lines = execOut.split("\n");
    	for(String line : lines) {
    		String[] parts = line.split("\t");
    		String field = parts[0].trim();
    		if(parts.length > 1 && field.length() > 0) {
    			String value = parts[1].trim();
    			out.write("<"+field+">"+StringEscapeUtils.escapeXml(value)+"</"+field+">");
    			out.write("\n");
    		}
    	}
        out.write("</exiftool>");
        out.write("\n");

        out.flush();
        try {
			out.close();
		} catch (IOException e) {
			throw new FitsToolException("Error closing Exiftool XML output stream",e);
		}
        Document doc = null;
		try {
			doc = saxBuilder.build(new StringReader(out.toString()));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing Exiftool XML Output",e);
		}
        return doc;
    }
	/*
	public boolean isIdentityKnown(FileIdentity identity) {
		//identity and mimetype must not be null or empty strings for an identity to be "known"
		if(identity == null
				|| identity.getMime() == null
				|| identity.getMime().length() == 0
				|| identity.getFormat() == null
				|| identity.getFormat().length() == 0) {
			return false;
		}
		String mime = identity.getMime();
		if(mime.equals("application/unknown")) {
			return false;
		}
		else {
			return true;
		}
	}*/

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}

}

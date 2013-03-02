/* 
 * Copyright 2009 Harvard University Library
 * 
 * This file is part of FITS (File Information Tool Set).
 * 
 * FITS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FITS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FITS.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.hul.ois.fits.tools.mediainfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class MediaInfo extends ToolBase {

	private boolean osIsWindows = false;
	private boolean osHasMediaInfo = false;
	private List<String> winCommand = new ArrayList<String>(Arrays.asList(Fits.FITS_TOOLS+"exiftool/windows/exiftool.exe")); //TODO
	private List<String> unixCommand = new ArrayList<String>(Arrays.asList("mediainfo"));
	private List<String> testCommand = Arrays.asList("which", "mediainfo");
	private final static String TOOL_NAME = "MediaInfo";
	private boolean enabled = true;
	
	public final static String mediaInfoFitsConfig = Fits.FITS_XML+"mediainfo"+File.separator;
	public final static String genericTransform = "mediainfo_generic_to_fits.xslt";
	
	public MediaInfo() throws FitsException {
		
		String osName = System.getProperty("os.name");
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		String versionOutput = null;
		List<String> infoCommand = new ArrayList<String>();
		if (osName.startsWith("Windows")) { //TODO make it work with windows
			//use provided Windows exiftool.exe 
			osIsWindows = true;
			infoCommand.addAll(winCommand);
			info.setNote("mediainfo for windows");
		}
		else if (testForMediaInfo()){
			osHasMediaInfo = true;	
			//use OS version of perl and the provided perl version of exiftool
			infoCommand.addAll(unixCommand);
			info.setNote("mediainfo for unix");
		}
		
		else {
			throw new FitsToolException("MediaInfo was not found on this system");
		}
		infoCommand.add("--Version");
		versionOutput = CommandLine.exec(infoCommand,null);	
		info.setVersion(versionOutput.trim());
		transformMap = XsltTransformMap.getMap(mediaInfoFitsConfig+"mediainfo_xslt_map.xml");
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
		List execCommand = new ArrayList();
		//determine if the file can be used on the current platform
		if (osIsWindows) {
			//use provided Windows File Utility
			execCommand.addAll(winCommand);
			execCommand.add(file.getPath());
		}
		else if(osHasMediaInfo) {
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
		
		String execOut = CommandLine.exec(execCommand,null);
		
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
			
		Document rawOut = null;
		
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
			fitsXml = transform(mediaInfoFitsConfig+xsltTransform,rawOut);
		}
		else {
			//use generic transform
			fitsXml = transform(mediaInfoFitsConfig+genericTransform,rawOut);
		}
		output = new ToolOutput(this,fitsXml,rawOut);
		//}
		return output;
	}
	
	public boolean testForMediaInfo() throws FitsToolCLIException {
		String output = CommandLine.exec(testCommand,null);
		if(output == null || output.length() == 0) {
			return false;
		}
		else {
			return true;
		}
	}
	

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}
	
}

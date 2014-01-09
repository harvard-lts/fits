package edu.harvard.hul.ois.fits.tools.libdwg;

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

public class DwgDump extends ToolBase {

	private boolean isWindows=false;
	private boolean isLibDWGInstalled = false;
	private List<String> command = new ArrayList<String>(Arrays.asList("dwg-dump"));
	private List<String> dwgDumpTestCommand = Arrays.asList("which", "dwg-dump");
	private final static String TOOL_NAME = "DwgDump";
	private boolean enabled = true;
	public final static String xslt = Fits.FITS_HOME+"xml/libdwg/libdwgToFits.xslt";
    private static Logger logger = Logger.getLogger(DwgDump.class);

	public DwgDump() throws FitsException {
        logger.debug ("Initializing DwgDump");

		String osName = System.getProperty("os.name");
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		String versionOutput = null;
		List<String> infoCommand = new ArrayList<String>();
		if (osName.startsWith("Windows")) {
			isWindows=true;
			 logger.error ("Windows not supported. Not running dwg-dump");
			 throw new FitsToolException("dwg-dump cannot be used on this system");
		}else if (testOSForDwgDump()){
			isLibDWGInstalled=true;
			infoCommand.addAll(command);
		}else {
		    logger.error ("dwg-dump is not installed on this system");
			throw new FitsToolException("dwg-dump is not installed on this system");
		}
		infoCommand.add("-v");
		versionOutput = CommandLine.exec(infoCommand,null);	
		info.setVersion(versionOutput.trim());
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("dwg-dump starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		List<String> execCommand = new ArrayList<String>();
		if (isLibDWGInstalled && !isWindows) {
			execCommand.addAll(command);
			execCommand.add(file.getPath());
		}else {
			return null;
		}
		logger.debug("Launching dwg-dump, command = " + execCommand);
		String execOut = CommandLine.exec(execCommand,null);
		logger.debug("Finished running dwg-dump");

		Document rawOut = createXml(execOut);		
		
			
		Document fitsXml = fitsXml = transform(xslt,rawOut);

		output = new ToolOutput(this,fitsXml,rawOut);

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("dwg-dump finished on " + file.getName());
		return output;
	}
	
	public boolean testOSForDwgDump() throws FitsToolCLIException {
		String output = CommandLine.exec(dwgDumpTestCommand,null);
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
        out.write("<dwgdump>");
        //out.write("<rawOutput>\n"+StringEscapeUtils.escapeXml(execOut));	   
        //out.write("</rawOutput>");
        out.write("\n");
        if(execOut.contains("LibDWG does not support this version")){
        	
        }else if(!execOut.contains("-- DWG dumped data --")){
        	out.write("<valid>false</valid>");
        	out.write("\n");
        }else{
        	out.write("<valid>true</valid>");
        	out.write("\n");
        
	    	String[] lines = execOut.split("\n");
	    	String currentSection=null;
	    	out.write("<meta>");
			out.write("\n");
	    	for(String line : lines) {
	    		
	    		if(line.startsWith("-- ")){
	    			if(currentSection!=null){
	    				out.write("</"+currentSection+">");
	    				out.write("\n");
	    			}
	    			currentSection = escapeXML(line.substring(3,line.indexOf(" --")));
	    			out.write("<"+currentSection+">");
	    			out.write("\n");
	    		}else if(line.startsWith("   ")){
	    			if(line.contains(" = ")){
	    				String field = StringEscapeUtils.escapeXml(line.split("=")[0].trim());
	    				String value = StringEscapeUtils.escapeXml(line.split("=")[1].trim());
	    				out.write("<"+field+">"+value+"</"+field+">");
	    				out.write("\n");
	    			}else{
	    				String field = StringEscapeUtils.escapeXml(line.split(":")[0].trim());
	    				String value = StringEscapeUtils.escapeXml(line.split(":")[1].trim());
	    				out.write("<"+field+">"+value+"</"+field+">");
	    				out.write("\n");
	    			}
	    		}
	    		
	    	}
	    	if(currentSection!=null){
				out.write("</"+currentSection+">");
				out.write("\n");
			}
	    	out.write("</meta>");
			out.write("\n");
	    	
        }
        out.write("</dwgdump>");
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


	private String escapeXML(String s) {
		return StringEscapeUtils.escapeXml(s).replace(" ", "_");
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}
	
	public Boolean canIdentify() {
		return false;
	}
	
	
	
}

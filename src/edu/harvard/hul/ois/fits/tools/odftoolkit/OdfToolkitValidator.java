package edu.harvard.hul.ois.fits.tools.odftoolkit;

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
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;

public class OdfToolkitValidator extends ToolBase{
	public final static String xslt = Fits.FITS_HOME+"xml/odfToolkitValidator/odfToolkitValidatorToFits.xslt";
	private List<String> command = new ArrayList<String>(Arrays.asList("java","-jar",Fits.FITS_TOOLS+"odftoolkit/odfvalidator-1.1.7-incubating-SNAPSHOT-jar-with-dependencies.jar"));
	private final static String TOOL_NAME = "OdfToolkitValidator";
	private boolean enabled = true;
	   private static Logger logger = Logger.getLogger(OdfToolkitValidator.class);

	public OdfToolkitValidator() throws FitsToolException {
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		String versionOutput = null;
		List<String> infoCommand = new ArrayList<String>();
		infoCommand.addAll(command);
		infoCommand.add("-V");
		versionOutput = CommandLine.exec(infoCommand,null);	
		info.setVersion(versionOutput.trim().split(" ")[1]);
	}
	@Override
	public ToolOutput extractInfo(File file) throws FitsToolException {
	  try{
		long startTime = System.currentTimeMillis();
		List<String> execCommand = new ArrayList<String>();
		execCommand.addAll(command);
		execCommand.add(file.getPath());
		String execOut = CommandLine.exec(execCommand,null);
		Document rawOut = createXml(execOut);	
		Document fitsXml = transform(xslt, rawOut);
	    
		output = new ToolOutput(this, fitsXml,rawOut);

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		return output;
	  }catch(Exception e){
	    logger.error(e.getMessage(),e);
	    return null;
	  }

	}
	
	
	private Document createXml(String execOut) throws FitsToolException {    	
		
		System.out.println("\n\n\n\n"+execOut+"\n\n\n");
    	StringWriter out = new StringWriter();
    	
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("\n");
        out.write("<odfvalidator>");
        out.write("\n");
        
        out.write("<rawOutput>\n"+StringEscapeUtils.escapeXml(execOut));	   
        out.write("</rawOutput>");
        out.write("\n");
        
        if(execOut.trim()!=null && execOut.trim().length()>0){
        	out.write("<valid>false</valid>");
        }else{
        	out.write("<valid>true</valid>");
        }
        out.write("</odfvalidator>");
        out.write("\n");
        out.flush();
        try {
			out.close();
		} catch (IOException e) {
			throw new FitsToolException("Error closing OdfValidator XML output stream",e);
		}
        Document doc = null;
		try {
			doc = saxBuilder.build(new StringReader(out.toString()));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing OdfValidator XML Output",e);
		} 
        return doc;
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

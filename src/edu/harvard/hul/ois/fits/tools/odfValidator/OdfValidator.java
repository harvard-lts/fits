package edu.harvard.hul.ois.fits.tools.odfValidator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;

public class OdfValidator extends ToolBase{
	public final static String xslt = Fits.FITS_HOME+"xml/odfValidator/odfValidatorToFits.xslt";
	private List<String> command = new ArrayList<String>(Arrays.asList("java","-jar",Fits.FITS_TOOLS+"odfValidator/odfValidator.jar"));
	private final static String TOOL_NAME = "Validator";
	private boolean enabled = true;
	
	public OdfValidator() throws FitsToolException {
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		String versionOutput = null;
		List<String> infoCommand = new ArrayList<String>();
		infoCommand.addAll(command);
		infoCommand.add("-v");
		versionOutput = CommandLine.exec(infoCommand,null);	
		info.setVersion(versionOutput.trim());
	}
	@Override
	
	public ToolOutput extractInfo(File file) throws FitsToolException {
		long startTime = System.currentTimeMillis();
		List<String> execCommand = new ArrayList<String>();
		execCommand.addAll(command);
		execCommand.add("-f");
		execCommand.add(file.getPath());
		String execOut = CommandLine.exec(execCommand,null);
		
		
		Document rawOut = createXml(execOut);	
		Document fitsXml = transform(xslt, rawOut);
	    
		output = new ToolOutput(this, fitsXml,rawOut);

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		return output;

	}
	
	
	private Document createXml(String execOut) throws FitsToolException {    	
    	StringWriter out = new StringWriter();
    	
    	String valid = null;
    	String version = null;
    	
    	String[] temp = execOut.split("\n");
    	
    	for(String s : temp){
    		if(s.startsWith("valid:") && valid==null){
    			valid=s.substring(s.indexOf(":")+1).trim();
    		}
    		if(s.startsWith("version:") && version==null){
    			version=s.substring(s.indexOf(":")+1).trim();
    		}
    	}
    	
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("\n");
        out.write("<odfValidator>");
        out.write("\n");
        
        out.write("<rawOutput>\n"+StringEscapeUtils.escapeXml(execOut));	   
        out.write("</rawOutput>");
        out.write("\n");
        
        if(valid!=null){
        	out.write("<valid>"+valid+"</valid>");
        }
        if(version!=null){
        	out.write("<version>"+version+"</version>");
        }
        out.write("</odfValidator>");
        out.write("\n");
        out.flush();
        try {
			out.close();
		} catch (IOException e) {
			throw new FitsToolException("Error closing Validator XML output stream",e);
		}
        Document doc = null;
		try {
			doc = saxBuilder.build(new StringReader(out.toString()));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing Validator XML Output",e);
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

package edu.harvard.hul.ois.fits.tools.dbPreservationToolkit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;
import edu.harvard.hul.ois.fits.tools.utils.XmlUtils;

public class DbPreservationToolkit extends ToolBase{
	public final static String xslt = Fits.FITS_HOME+"xml/dbPreservationToolkit/dbPreservationToolkitToFits.xslt";
	private List<String> command = new ArrayList<String>(Arrays.asList("java","-jar",Fits.FITS_TOOLS+"dbPreservationToolkit/dbPreservationToolkit.jar","-o","characterization","-i","DBML"));
	private final static String TOOL_NAME = "DB Preservation Toolkit";
	private boolean enabled = true;
	
	public DbPreservationToolkit() throws FitsToolException {
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		/*
		String versionOutput = null;
		List<String> infoCommand = new ArrayList<String>();
		infoCommand.addAll(command);
		infoCommand.add("-v");
		versionOutput = CommandLine.exec(infoCommand,null);	
		*/
		info.setVersion("1.0");
	}
	@Override
	
	public ToolOutput extractInfo(File file)  {
		long startTime = System.currentTimeMillis();
		try{
			
			List<String> execCommand = new ArrayList<String>();
			execCommand.addAll(command);
			execCommand.add(file.getParent());
			String execOut = CommandLine.exec(execCommand,null);
			
			
			if(XmlUtils.isXMLValid(execOut, new File(Fits.FITS_HOME+"xml/dbPreservationToolkit/outputSchema.xsd"))){
				try{
					output = getFitsOutput(execOut);
					duration = System.currentTimeMillis()-startTime;
					runStatus = RunStatus.SUCCESSFUL;
					return output;
				}catch(Exception e){
					return null;
				}
			}else{
				try{
					execOut="<xml><out></out></xml>";
					output = getFitsOutput(execOut);
					duration = System.currentTimeMillis()-startTime;
					runStatus = RunStatus.SUCCESSFUL;
					return output;
				}catch(Exception e){
					return null;
				}
			}
		}catch(Exception e){
			String execOut="<xml><out></out></xml>";
			output = getFitsOutput(execOut);
			duration = System.currentTimeMillis()-startTime;
			runStatus = RunStatus.SUCCESSFUL;
			return output;
		}
	}

	private ToolOutput getFitsOutput(String execOut) {
		ToolOutput output = null;
		try{
			SAXBuilder sb=new SAXBuilder();
			Document rawOut=sb.build(new InputSource(new ByteArrayInputStream(execOut.getBytes("utf-8"))));
			Document fitsXml = transform(xslt, rawOut);
			output = new ToolOutput(this, fitsXml,rawOut);
		}catch(Exception e) {
		  execOut="<xml><out></out></xml>";
          output = getFitsOutput(execOut);
		}
		return output;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}

	public Boolean canIdentify() {
		return true;
	}

}

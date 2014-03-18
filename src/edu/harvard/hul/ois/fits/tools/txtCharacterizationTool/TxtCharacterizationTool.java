package edu.harvard.hul.ois.fits.tools.txtCharacterizationTool;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;

public class TxtCharacterizationTool extends ToolBase{
	public final static String xslt = Fits.FITS_HOME+"xml/txtCharacterizationTool/txtCharacterizationToolToFits.xslt";
	private final static String TOOL_NAME = "TXT Characterization Tool";
	private boolean enabled = true;
	private pt.keep.validator.txt.TxtCharacterizationTool keepValidator; 
	
	public TxtCharacterizationTool() throws FitsToolException {
		info = new ToolInfo();
		info.setName(TOOL_NAME);
		keepValidator = new pt.keep.validator.txt.TxtCharacterizationTool();
		info.setVersion(keepValidator.getVersion());
	}
	
	public ToolOutput extractInfo(File file) throws FitsToolException {
		long startTime = System.currentTimeMillis();
		List<String> encodings = Arrays.asList("UTF-8","ISO-8859-1","KOI8-R","WINDOWS-1250");
		
		String execOut = keepValidator.run(file,encodings,50);
		try{
			SAXBuilder sb=new SAXBuilder();
			Document rawOut=sb.build(new InputSource(new ByteArrayInputStream(execOut.getBytes("utf-8"))));
			Document fitsXml = transform(xslt, rawOut);
			output = new ToolOutput(this, fitsXml,rawOut);
			duration = System.currentTimeMillis()-startTime;
			runStatus = RunStatus.SUCCESSFUL;
			return output;
		}catch(Exception e){
			return null;
		}
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
	
	public static void main(String args[]){
      try{
        
        pt.keep.validator.txt.TxtCharacterizationTool keepValidator = new pt.keep.validator.txt.TxtCharacterizationTool();
        List<String> encodings = Arrays.asList("UTF-8","ISO-8859-1","KOI8-R","WINDOWS-1250");
        
        System.out.println(keepValidator.run(new File("/home/sleroux/Development/fits-testing/corpora/largeCorpora/1.txt"),encodings,50));


        System.out.println(keepValidator.getVersion());
      }catch(Exception e){
        e.printStackTrace();
      }
    }
}

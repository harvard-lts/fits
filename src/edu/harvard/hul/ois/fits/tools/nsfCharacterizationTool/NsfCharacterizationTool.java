package edu.harvard.hul.ois.fits.tools.nsfCharacterizationTool;

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

public class NsfCharacterizationTool extends ToolBase {
  private boolean osIsWindows = false;
  private List<String> winCommand = new ArrayList<String>(Arrays.asList(Fits.FITS_TOOLS+"nsf-characterization-tool/run.bat"));
  private List<String> unixCommand = new ArrayList<String>(Arrays.asList("perl",Fits.FITS_TOOLS+"nsf-characterization-tool/run.sh"));
  public final static String xslt = Fits.FITS_HOME + "xml/nsfCharacterizationTool/nsfCharacterizationToolToFits.xslt";
  private final static String TOOL_NAME = "NSF Characterization Tool";
  private boolean enabled = true;
  String lotusNotesFolder = Fits.config.getString("lotus_notes_folder");
  String lotusNotesJVMFolder = Fits.config.getString("lotus_notes_jvm_folder");
  public NsfCharacterizationTool() throws FitsToolException {
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Windows")) {
      osIsWindows = true;
    }else{
      osIsWindows = false;
    }
    info = new ToolInfo();
    info.setName(TOOL_NAME);
    String versionOutput = null;
    List<String> infoCommand = new ArrayList<String>();
    infoCommand.addAll(osIsWindows?winCommand:unixCommand);
    infoCommand.add(lotusNotesJVMFolder);
    infoCommand.add("lotusNotesFolder");
    versionOutput = CommandLine.exec(infoCommand, null);
    info.setVersion(versionOutput.trim());
  }

  @Override
  public ToolOutput extractInfo(File file) throws FitsToolException {
    long startTime = System.currentTimeMillis();
    List<String> execCommand = new ArrayList<String>();
    execCommand.addAll(osIsWindows?winCommand:unixCommand);
    execCommand.add(lotusNotesJVMFolder);
    execCommand.add("lotusNotesFolder");
    execCommand.add(file.getPath());
    String execOut = CommandLine.exec(execCommand, null);
    try {
      SAXBuilder sb = new SAXBuilder();
      Document rawOut =
          sb.build(new InputSource(new ByteArrayInputStream(execOut.getBytes("utf-8"))));
      Document fitsXml = transform(xslt, rawOut);
      output = new ToolOutput(this, fitsXml, rawOut);
      duration = System.currentTimeMillis() - startTime;
      runStatus = RunStatus.SUCCESSFUL;
      return output;
    } catch (Exception e) {
      e.printStackTrace();
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
}

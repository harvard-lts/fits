package edu.harvard.hul.ois.fits.tools.embarc;

import java.io.File;

import org.apache.log4j.Logger;
import org.jdom.Document;

import com.portalmedia.embarc.cli.DPXFileListHelper;
import com.portalmedia.embarc.parser.FileFormat;
import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

public class EmbARC extends ToolBase {
	private boolean enabled = true;
	private Fits fits;
	private static final Logger logger = Logger.getLogger(EmbARC.class);

	public EmbARC(Fits fits) throws FitsToolException {
		super();
		this.fits = fits;
		logger.debug ("Initializing embARC");
		info = new ToolInfo("EmbARC", "1.0.0", null); // TODO: get actual version code

		try {
			// TODO
		} catch (Exception e) {
			throw new FitsToolException("Error initilizing embARC", e);
		}
	}

	@Override
	public ToolOutput extractInfo(File file) throws FitsToolException {
		logger.debug("EmbARC.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		String absPath = file.getAbsolutePath();

		logger.debug("absPath: " + absPath);

		if (!isDPXFile(absPath)) {
			// TODO: bail out, non dpx file found
			logger.debug("Non DPX file input, bailing");
		}

		DPXFileInformation dpxFileInfo = DPXFileListHelper.createDPXFileInformation(absPath);
		logger.debug("dpxFileInfo.getFileData().toString():\n" + dpxFileInfo.getFileData().toString());

		ToolOutput output = createToolOutput(file);

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		logger.debug("EmbARC.extractInfo finished on " + file.getName());
		return output;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean value) {
		enabled = value;
	}

	private ToolOutput createToolOutput(File file) throws FitsToolException {
		// TODO

		Document fitsXml = new Document();
		Document rawOut = new Document();
		ToolOutput output = new ToolOutput(this, fitsXml, rawOut, fits);
		return output;
	}

	private boolean isDPXFile(String f) {
		FileFormat fileFormat = FileFormatDetection.getFileFormat(f);
		if (fileFormat == FileFormat.DPX) {
			return true;
		}
		return false;
	}

}

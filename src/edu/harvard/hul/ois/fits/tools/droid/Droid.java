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
package edu.harvard.hul.ois.fits.tools.droid;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.jdom.input.SAXBuilder;
import org.apache.log4j.Logger;

import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
//import uk.gov.nationalarchives.droid.AnalysisController;
//import uk.gov.nationalarchives.droid.IdentificationFile;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

/**  The principal glue class for invoking DROID under FITS.
 */
public class Droid extends ToolBase {

	//private uk.gov.nationalarchives.droid.Droid droid = null;
    //private BinarySignatureIdentifier droid = null;
	//public final static String xslt = Fits.FITS_HOME+"xml/droid/droid_to_fits.xslt";
	private boolean enabled = true;
	private DroidQuery droidQuery;
    private static Logger logger = Logger.getLogger(Droid.class);

	public Droid() throws FitsToolException {
        logger.debug ("Initializing Droid");
		info = new ToolInfo("Droid", getDroidVersion(), null);		

		String javaVersion = System.getProperty("java.version");
		if (javaVersion.startsWith ("1.8")) {
		    throw new FitsToolException ("DROID cannot run under Java 8");
		}
		try {
			String droid_conf = Fits.FITS_TOOLS+"droid"+File.separator;
			//URL droidConfig = new File(droid_conf+"DROID_config.xml").toURI().toURL();
			File sigFile = new File(droid_conf+Fits.config.getString("droid_sigfile"));
	        File tempDir = new File(Fits.FITS_TOOLS+"droid" + File.separator + "tmpdir");
	        try {
	            droidQuery = new DroidQuery (sigFile, tempDir);
	        }
	        catch (SignatureParseException e) {
	            throw new FitsToolException("Problem with DROID signature file");
	        }
			//droid = new uk.gov.nationalarchives.droid.Droid(droidConfig);
			//droid = new BinarySignatureIdentifier();
			//droid.setSignatureFile(sigFile);
		} catch (Exception e) {
			throw new FitsToolException("Error initilizing DROID",e);
		}
	}

	@Override
	public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("Droid.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		IdentificationResultCollection results;
		try {
		    results = droidQuery.queryFile(file);
		}
		catch (IOException e) {
		    throw new FitsToolException("DROID can't query file " + file.getAbsolutePath(),
		            e);
		}
		DroidToolOutputter outputter = new DroidToolOutputter(this, results);
		ToolOutput output = outputter.toToolOutput();
		
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("Droid.extractInfo finished on " + file.getName());
		return output;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}
	
	/** Make the SAXBuilder available to helper class */
    protected SAXBuilder getSaxBuilder () {
        return saxBuilder;
    }

	/* Get the version of DROID. This is about the cleanest I can manage. */
	private String getDroidVersion () {
		return "";
		/*
	    StringWriter sw = new StringWriter ();
	    PrintWriter pw = new PrintWriter (sw);
	    VersionCommand vcmd = new VersionCommand (pw);
	    try {
	        vcmd.execute ();
	    }
	    catch (Exception e) {
	        return "(Version unknown)";
	    }
	    return sw.toString().trim();
	    */
	}
    
}

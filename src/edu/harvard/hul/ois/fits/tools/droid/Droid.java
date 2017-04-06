//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.droid;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.jdom.input.SAXBuilder;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import uk.gov.nationalarchives.droid.command.action.VersionCommand;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;

/**  The principal glue class for invoking DROID under FITS.
 */
public class Droid extends ToolBase {

	private boolean enabled = true;
	private DroidQuery droidQuery;
    private Fits fits;

    private static final Logger logger = Logger.getLogger(Droid.class);

	public Droid(Fits fits) throws FitsToolException {
		super();
		this.fits = fits;
        logger.debug ("Initializing Droid");
		info = new ToolInfo("Droid", getDroidVersion(), null);

		try {
			String droid_conf = Fits.FITS_TOOLS_DIR+"droid"+File.separator;
			File sigFile = new File(droid_conf + fits.getConfig().getString("droid_sigfile"));
	        try {
	            droidQuery = new DroidQuery (sigFile);
	        }
	        catch (SignatureParseException e) {
	            throw new FitsToolException("Problem with DROID signature file");
	        }
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
		DroidToolOutputter outputter = new DroidToolOutputter(this, results, fits);
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
	}

}

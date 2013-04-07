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
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import org.jdom.Document;
import org.xml.sax.SAXException;

import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
//import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.core.signature.FileFormatHit;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.ResultHandler;
import uk.gov.nationalarchives.droid.results.handlers.BatchResultHandler;
//import uk.gov.nationalarchives.droid.IdentificationFile;
import uk.gov.nationalarchives.droid.submitter.SubmissionGateway;
import uk.gov.nationalarchives.droid.submitter.SubmissionQueue;
import uk.gov.nationalarchives.droid.command.action.VersionCommand;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

public class Droid extends ToolBase {

	//private uk.gov.nationalarchives.droid.Droid droid = null;
    //private BinarySignatureIdentifier droid = null;
	//public final static String xslt = Fits.FITS_HOME+"xml/droid/droid_to_fits.xslt";
	private boolean enabled = true;
	private DroidQuery droidQuery;
	
	public Droid() throws FitsToolException {

		info = new ToolInfo("Droid", getDroidVersion(), null);		

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
		long startTime = System.currentTimeMillis();
		IdentificationResultCollection results = droidQuery.queryFile(file);
		DroidToolOutputter outputter = new DroidToolOutputter(results);
		ToolOutput output = outputter.toToolOutput();
		
		//IdentificationFile idFile = droid.identify(file.getPath());
		/*List<FileIdentity> identities = new ArrayList();
		for(int i=0;i<idFile.getNumHits();i++) {
			FileFormatHit hit = idFile.getHit(i);
			FileIdentity identity = new FileIdentity(hit.getMimeType(),hit.getFileFormatName(),hit.getFileFormatVersion());
			//pronom id;
			identity.addExternalIdentifier("puid",hit.getFileFormatPUID());
			identities.add(identity);
		}	*/

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		return output;
	}



	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
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

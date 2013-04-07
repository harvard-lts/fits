package edu.harvard.hul.ois.fits.tools.droid;

import edu.harvard.hul.ois.fits.tools.ToolOutput;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;

/** This class generates the tool output for DROID. */
public class DroidToolOutputter {
    
    IdentificationResultCollection results;
    
    public DroidToolOutputter (IdentificationResultCollection results) {
        this.results = results;
    }
    
    public ToolOutput toToolOutput () {
        return null;  // TODO stub
    }
}

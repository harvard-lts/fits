//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//



/* Droid 6.1 has no nicely packaged way to make simple queries. This
 * class attempts to fill that gap for FITS, in a way that will let it
 * be lifted for other uses and perhaps incorporated into Droid itself.
 */
package edu.harvard.hul.ois.fits.tools.droid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

public class DroidQuery {

    private BinarySignatureIdentifier sigIdentifier = new BinarySignatureIdentifier();
    // Certain file types (possibly really large file), we only want to examine the beginning of the file.
    private long bytesToRead = -1;
    private List<String> fileExtensions;
    
    /** Create a DroidQuery object. This can be retained for any number of
     *  different queries.
     *
     * @param sigFile   File object for a Droid signature file
     * @param includeExts TODO
     * @param kbReadLimit TODO
     *
     *   @throws SignatureParseException
     */
    public DroidQuery (File sigFile, List<String> includeExts, long kbReadLimit)  throws SignatureParseException, FileNotFoundException    {
        if (!sigFile.exists()) {
            throw new FileNotFoundException ("Signature file " + sigFile.getAbsolutePath() + " not found");
        }
        sigIdentifier.setSignatureFile (sigFile.getAbsolutePath());
        sigIdentifier.init ();
        this.fileExtensions = includeExts;
        if (kbReadLimit > 0) {
        	this.bytesToRead = (kbReadLimit * 1024) - 1;
        }
    }

    /** Query a file and get back an XML response. */
    public IdentificationResultCollection queryFile (File fil)
            throws IOException {
    	
        // For certain file types, set max. number of bytes at beginning of file to process.
        // See https://groups.google.com/forum/#!msg/droid-list/HqN6lKOATJk/i-qTEI-XEwAJ;context-place=forum/droid-list
        // which indicates minimum number of bytes required to identify certain input file types.    	
    	long bytesToExamine = fil.length();
    	String filename = fil.getName();
    	int lastDot = filename.lastIndexOf('.');
    	if (lastDot > 0 && filename.length() > lastDot) {
    		String fileExtension = filename.substring(++lastDot).toLowerCase(); // examine extension past the last dot
    		if (fileExtensions != null && fileExtensions.contains(fileExtension) && bytesToRead > 0) {
    			bytesToExamine = Math.min(fil.length(), bytesToRead);
    		}
    	}
        RequestMetaData metadata = new RequestMetaData(bytesToExamine, fil.lastModified(), fil.getName());
        RequestIdentifier identifier = new RequestIdentifier (fil.toURI());
        FileInputStream in = null;
        FileSystemIdentificationRequest req = null;
        try {
            req = new FileSystemIdentificationRequest(metadata, identifier);
            in = new FileInputStream (fil);
            req.open(in);
            IdentificationResultCollection results;
            results = sigIdentifier.matchBinarySignatures(req);
            if (results.getResults().size() > 1) {
                sigIdentifier.removeLowerPriorityHits(results);
            }

            if(results.getResults().size() == 0) {
            	results = sigIdentifier.matchExtensions(req,false);
            }
            if (results.getResults().size() > 1) {
                sigIdentifier.removeLowerPriorityHits(results);
            }

    //        List<IdentificationResult> resultsList = results.getResults();
                // This gives us an unfiltered list of matching signatures
            return results;
        }
        finally {
            if (req != null) {
                req.close ();
            }
            if (in != null) {
                in.close();
            }
        }
    }
}

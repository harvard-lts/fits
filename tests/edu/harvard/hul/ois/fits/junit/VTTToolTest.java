/* 
 * Copyright 2016 Harvard University Library
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
package edu.harvard.hul.ois.fits.junit;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;

public class VTTToolTest {

	@Test  
	public void testVttRead() throws Exception {   
		Fits fits = new Fits();
		File input = new File("testfiles/simple_webvtt.vtt");
		FitsOutput fitsOut = fits.examine(input);
		List <FitsIdentity> identities = fitsOut.getIdentities();
		
		for(FitsIdentity identity : identities) {
			if(!identity.getMimetype().contains("text/vtt")) {
				fail("This should be identified as a WebVTT file with mimetype of text/vtt");
			}
		}

	}


	@Test  
	public void testInvalidVttRead() throws Exception {   
		Fits fits = new Fits();
		File input = new File("testfiles/invalid_webvtt.vtt");
		FitsOutput fitsOut = fits.examine(input);
		List <FitsIdentity> identities = fitsOut.getIdentities();
		
		// Since this is an invalid VTT file, it should be identified as "text/plain"
		for(FitsIdentity identity : identities) {
			if(!identity.getMimetype().contains("text/plain")) {
				fail("This should not be identified as a text/plain file");
			}
		}

	}    

}

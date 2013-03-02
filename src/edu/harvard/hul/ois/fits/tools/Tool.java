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
package edu.harvard.hul.ois.fits.tools;

import java.io.File;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.identity.ToolIdentity;


public interface Tool extends Runnable {
		
	/**
	 * Extracts the identification and metadata from the provided file
	 * @param file the file to have its metadata extracted
	 * @return tooloutput object containing the xml wrapping the tool raw output, fits compatible xml output
	 * and the fits FileIdentity
	 * @throws FitsToolException
	 */
	public ToolOutput extractInfo(File file) throws FitsToolException;
	
	/**
	 * Checks if the value for the given field name is the 
	 * default value that the tool would report if it doesn't
	 * understand the file that was processed
	 * @param identity the identity to test
	 * @return boolean indicating the file is a known or unknown type for the tool
	 */
	public boolean isIdentityKnown(ToolIdentity identity);
		
	/**
	 * Returns the information about the tool
	 * @return
	 */
	public ToolInfo getToolInfo();
		
	/**
	 * If the tool can identify mimetype and format of files.
	 * @return
	 */
	public Boolean canIdentify();
	
	/**
	 * Add a file extension that the tool should not process
	 * @param ext
	 */
	public void addExcludedExtension(String ext);
	
	/**
	 * Checks if the tool excluded extensions list contains the provided extension
	 * @param ext
	 * @return boolean
	 */
	public boolean hasExcludedExtension(String ext);
	
	public void resetOutput();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean value);
	
	public void setInputFile(File file);
	
	public ToolOutput getOutput();
}

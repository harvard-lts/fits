//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools;

import java.io.File;
import java.util.List;

import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.identity.ToolIdentity;

/** All FITS tools implement this interface. */
public interface Tool extends Runnable {

	public enum RunStatus {SHOULDNOTRUN,SHOULDRUN,FAILED,SUCCESSFUL};

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
	 * @return ToolInfo
	 */
	public ToolInfo getToolInfo();

	/**
	 * If the tool can identify mimetype and format of files.
	 * @return Boolean
	 */
	public Boolean canIdentify();

	/**
	 *  Returns the name of the tool object (not the name of the software).
	 */
	public String getName();

	/**
	 *  Sets the name of the tool object (not the name of the software).
	 */
	public void setName(String name);

	/**
	 * Add a file extension that the tool should not process
	 * @param ext
	 */
	public void addExcludedExtension(String ext);

	/**
	 * Add a file extension that the tool should not process
	 * @param ext
	 */
	public void addIncludedExtension(String ext);

	/**
	 * Checks if the tool excluded extensions list contains the provided extension
	 * @param ext
	 * @return boolean
	 */
	public boolean hasExcludedExtension(String ext);

	/**
	 * Checks if the tool included extensions list contains the provided extension
	 * @param ext
	 * @return boolean
	 */
	public boolean hasIncludedExtension(String ext);

	/**
	 * Checks if the tool uses an 'include-ext' list
	 * @return boolean
	 */
	public boolean hasIncludedExtensions();

	public boolean hasExcludedExtensions();

	/**
	 *  Applies the restrictions in a tools-used item to the tool
	 */
	public void applyToolsUsed (List<ToolBelt.ToolsUsedItem> toolsUsedItems);

	public void resetOutput();

	public boolean isEnabled();

	public void setEnabled(boolean value);

	public void setInputFile(File file);

	public ToolOutput getOutput();

	public long getDuration();

	public RunStatus getRunStatus();

	public void setRunStatus(RunStatus runStatus);

	/** Append any reported exceptions to a master list */
	public void addExceptions(List<Throwable> exceptions);
}

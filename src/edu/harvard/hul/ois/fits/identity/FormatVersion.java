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
package edu.harvard.hul.ois.fits.identity;

import edu.harvard.hul.ois.fits.tools.ToolInfo;

/** A simple wrapper for a format version, with the tool that identified it. 
 *  This is used where different tools may agree on the format but
 *  not on the version.
 */
public class FormatVersion {
	
	private ToolInfo toolInfo;
	private String value;
	
	public FormatVersion(String value, ToolInfo toolInfo) {
		this.value = value;
		this.toolInfo = toolInfo;
	}
	
	public ToolInfo getToolInfo() {
		return toolInfo;
	}
	public void setInfo(ToolInfo info) {
		this.toolInfo = info;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String version) {
		this.value = version;
	}


}

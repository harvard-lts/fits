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

public class ExternalIdentifier {
	
	private String name;
	private String value;
	private ToolInfo toolInfo;
	
	public ExternalIdentifier() {
		
	}
	
	public ExternalIdentifier(String name, String value, ToolInfo toolInfo) {
		this.name = name;
		this.value = value;
		this.toolInfo = toolInfo;
	}
	
	public ToolInfo getToolInfo() {
		return toolInfo;
	}

	public void setToolInfo(ToolInfo toolInfo) {
		this.toolInfo = toolInfo;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}

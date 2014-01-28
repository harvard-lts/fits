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

import java.util.ArrayList;
import java.util.List;

import edu.harvard.hul.ois.fits.tools.ToolInfo;

/** Information about a file's format, MIME type, and identifiers,
 *  as provided by a single tool. 
 */
public class ToolIdentity {
	
	//Some tools may report multiple mimetypes, formats or format versions
	private String mime = new String();
	private String format = new String();
	private FormatVersion formatVersion;
	private List<ExternalIdentifier> externalIds = new ArrayList<ExternalIdentifier>();
	private ToolInfo toolInfo;
	
	public ToolIdentity(String mime, String format, ToolInfo toolInfo) {
		this.mime = mime;
		this. format = format;
		formatVersion = null;
		this.toolInfo = toolInfo;
	}
	
	public ToolIdentity(String mime, String format, String formatVersion, ToolInfo toolInfo) {
		this.mime = mime;
		this. format = format;
		this. formatVersion = new FormatVersion(formatVersion,toolInfo);
		this.toolInfo = toolInfo;
	}
	
	public ToolInfo getToolInfo() {
		return toolInfo;
	}
	
	public String getMime() {
		return mime;
	}

	public String getFormat() {
		return format;
	}
	
	public void setFormatVersion(String formatVersion) {
		this.formatVersion = new FormatVersion(formatVersion,toolInfo);
	}
	
	public FormatVersion getFormatVersion() {
		return formatVersion;
	}
	
	public String getFormatVersionValue() {
		return formatVersion.getValue();
	}

	public List<ExternalIdentifier> getExternalIds() {
		return externalIds;
	}

	public void addExternalIdentifier(ExternalIdentifier id) {
		externalIds.add(id);
	}
	public void addExternalIdentifier(String name, String id) {
		externalIds.add(new ExternalIdentifier(name,id,toolInfo));
	}
	
	public boolean hasExternalIdentifier(ExternalIdentifier id) {
		for(ExternalIdentifier xident : externalIds) {
			if(xident.getName().equalsIgnoreCase(id.getName()) 
					&& xident.getValue().equalsIgnoreCase(id.getValue())) {
				return true;
			}
		}
		return false;
	}
}

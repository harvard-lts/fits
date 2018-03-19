//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

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
		this.format = format;
		formatVersion = null;
		this.toolInfo = toolInfo;
	}

	public ToolIdentity(String mime, String format, String formatVersion, ToolInfo toolInfo) {
		this.mime = mime;
		this.format = format;
		this.formatVersion = new FormatVersion(formatVersion,toolInfo);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ToolIdentity: [");
		sb.append(getToolInfo());
		sb.append(", mime: ");
		sb.append(getMime());
		sb.append(", format: ");
		sb.append(getFormat());
		sb.append("]");
		return sb.toString();
	}
}

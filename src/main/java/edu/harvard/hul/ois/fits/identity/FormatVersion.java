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

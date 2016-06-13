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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExternalIdentifier [name=");
		builder.append(name);
		builder.append(", value=");
		builder.append(value);
		builder.append(", toolInfo=");
		builder.append(toolInfo);
		builder.append("]");
		return builder.toString();
	}

}

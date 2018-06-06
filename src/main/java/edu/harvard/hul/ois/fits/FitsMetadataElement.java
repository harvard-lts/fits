//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits;

public class FitsMetadataElement {

	private String name;
	private String value;
	private String reportingToolName;
	private String reportingToolVersion;
	private String status;

	public FitsMetadataElement() {

	}

	public FitsMetadataElement(String name, String value, String tName, String tVersion, String status) {
		this.name = name;
		this.value = value;
		this.reportingToolName = tName;
		this.reportingToolVersion = tVersion;
		this.status = status;
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

	public String getReportingToolName() {
		return reportingToolName;
	}

	public void setReportingToolName(String reportingToolName) {
		this.reportingToolName = reportingToolName;
	}

	public String getReportingToolVersion() {
		return reportingToolVersion;
	}

	public void setReportingToolVersion(String reportingToolVersion) {
		this.reportingToolVersion = reportingToolVersion;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FitsMetadataElement [name=");
		builder.append(name);
		builder.append(", value=");
		builder.append(value);
		builder.append(", reportingToolName=");
		builder.append(reportingToolName);
		builder.append(", reportingToolVersion=");
		builder.append(reportingToolVersion);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}

}

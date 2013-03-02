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

}

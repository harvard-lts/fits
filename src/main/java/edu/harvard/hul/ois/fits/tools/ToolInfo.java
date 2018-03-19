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

/** Holder of identifying information about one tool.
 */
public class ToolInfo {

	public String name;
	public String version;
	public String date;
	public String note;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ToolInfo() {

	}

	public ToolInfo(String name, String version, String date) {
		this.name = name;
		this.version = version;
		this.date = date;
	}

	/** Returns the name of the software, not to be confused with
	 *  the name of the Tool object. */
	public String getName() {
		return name;
	}


    /** Sets the name of the software, not to be confused with
     *  the name of the Tool object. */
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	/** Returns the publication date reported by the software (or
	 *  which FITS has hard-coded based on available information). */
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	/** Returns a String with human-readable information about the tool. */
	public String print() {
		String value = "Name= "+name+"\nVersion= "+version+"\nDate= "+date+"\n";
		if(note != null && note.length() > 0) {
			value = value +"Note= " + note + "\n";
		}
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ToolInfo: [");
		sb.append("name: ");
		sb.append(getName());
		sb.append(", version: ");
		sb.append(getVersion());
		sb.append("]");
		return sb.toString();
	}

}

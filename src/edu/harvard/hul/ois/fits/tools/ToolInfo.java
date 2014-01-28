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

}

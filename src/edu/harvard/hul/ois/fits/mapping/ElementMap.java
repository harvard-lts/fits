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
package edu.harvard.hul.ois.fits.mapping;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

public class ElementMap {
	
	private String name;
	private List<String> mimetypes = new ArrayList<String>();
	private Hashtable<String,String> maps = new Hashtable<String,String>();
	private List<AttributeMap> attributes = new ArrayList<AttributeMap>();
	
	public ElementMap(Element element, List<String> mimetypes) {
		name = element.getAttributeValue("name");
		this.mimetypes = mimetypes;
		
		//Get element mappings
		List<Element> childMaps = element.getChildren("map");
		for(Element map : childMaps) {
			String from = map.getAttributeValue("from");
			String to   = map.getAttributeValue("to");
			maps.put(from,to);
		}
		// get any attribute mappings for the map
		List<Element> child_attributes = element.getChildren("attribute");
		for(Element attr : child_attributes) {
			attributes.add(new AttributeMap(attr));
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hashtable<String, String> getMaps() {
		return maps;
	}

	public void setMaps(Hashtable<String, String> maps) {
		this.maps = maps;
	}

	public List<AttributeMap> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeMap> attributes) {
		this.attributes = attributes;
	}

	public void addAttributeMap(AttributeMap attribute) {
		attributes.add(attribute);
	}
	
	public void addMap(String from, String to) {
		maps.put(from,to);
	}

	public AttributeMap getAttribute(String name) {
		for(AttributeMap attr : attributes) {
			if(attr.getName().equalsIgnoreCase(name)) {
				return attr;
			}
		}
		return null;
	}
	
	public boolean isForType(String mime) {
		if(mime != null && (mimetypes.contains(mime.toLowerCase()) || mimetypes.contains("all"))) {
			return true;
		}
		else {
			return false;
		}
	}
}

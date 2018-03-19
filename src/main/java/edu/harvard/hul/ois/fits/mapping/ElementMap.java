//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

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

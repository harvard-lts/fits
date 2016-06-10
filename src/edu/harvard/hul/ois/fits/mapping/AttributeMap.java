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

import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

public class AttributeMap {

	private String name;
	private Hashtable<String,String> maps = new Hashtable<String,String>();

	@SuppressWarnings("unchecked")
	public AttributeMap(Element element) {
		name = element.getAttributeValue("name");
		//Get element mappings
		List<Element> childMaps = element.getChildren("map");
		for(Element map : childMaps) {
			String from = map.getAttributeValue("from");
			String to   = map.getAttributeValue("to");
			maps.put(from,to);
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

	public void addMap(String from, String to) {
		maps.put(from,to);
	}

}

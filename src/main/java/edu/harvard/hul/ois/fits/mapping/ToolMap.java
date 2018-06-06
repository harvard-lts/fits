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
import java.util.Arrays;
import java.util.List;

import org.jdom.Element;

public class ToolMap {

	private String toolName;
	private List<ElementMap> elements = new ArrayList<ElementMap>();

	public ToolMap(Element element) {
		 toolName = element.getAttribute("name").getValue();

		 List<Element> tool_children = element.getChildren("mime");
		 for(Element mime : tool_children) {
			 String types = mime.getAttributeValue("type");
			 String[] alltypes = types.split(",");
			 List<Element> mime_children = mime.getChildren("element");
			 for(Element e : mime_children) {
				 ElementMap xmlMapElement = new ElementMap(e, Arrays.asList(alltypes));
				 elements.add(xmlMapElement);
			 }
		 }
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public List<ElementMap> getElements() {
		return elements;
	}

	public void setElements(List<ElementMap> elements) {
		this.elements = elements;
	}

	public void addElement(ElementMap element) {
		elements.add(element);
	}

	public ElementMap getXmlMapElement(String name, String mime) {
		for(ElementMap xmlMapElement : elements) {
			if(xmlMapElement.getName().equalsIgnoreCase(name)
					&& xmlMapElement.isForType(mime)) {
				return xmlMapElement;
			}
		}
		return null;
	}
}

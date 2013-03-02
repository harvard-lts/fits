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
import java.util.Arrays;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

public class ToolMap {
	
	private String toolName;
	private String toolVersion;
	private List<ElementMap> elements = new ArrayList<ElementMap>();
	
	public ToolMap(Element element) {
		 toolName = element.getAttribute("name").getValue();
		 toolVersion = null;
		 //version is optional
		 Attribute vAttr = element.getAttribute("version");
		 if(vAttr != null) {
			 toolVersion = vAttr.getValue();
		 }

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
	public String getToolVersion() {
		return toolVersion;
	}
	public void setToolVersion(String toolVersion) {
		this.toolVersion = toolVersion;
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


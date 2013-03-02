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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.ToolInfo;

public class FitsXmlMapper {
	
	public static final String FITS_XML_MAP_PATH = Fits.FITS_XML+"fits_xml_map.xml";
	private List<ToolMap> toolMaps = new ArrayList<ToolMap>();
	
	public FitsXmlMapper() throws JDOMException, IOException {
		 SAXBuilder saxBuilder = new SAXBuilder();
		 Document doc = saxBuilder.build(FITS_XML_MAP_PATH);	
		 List<Element> tElements = doc.getRootElement().getChildren("tool");		 
		 for(Element tElement : tElements) {
			 ToolMap xmlMap = new ToolMap(tElement);
			 toolMaps.add(xmlMap);
		 }
	}
	
	public Document applyMap(Tool tool,Document doc) {
		//apply mapping
		ToolMap map = getElementMapsForTool(tool.getToolInfo());
		//If no maps exist for this tool return original doc
		if(map == null) {
			return doc;
		}
		//get mimetype from first identity in doc
		String mime = "";
		try {
			Element e = (Element)XPath.selectSingleNode(doc,"//identity");
			if(e != null) {
				mime = e.getAttributeValue("mimetype");
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		//iterate through all elements in doc
		Element root = doc.getRootElement();
		for(Element child : (List<Element>)root.getChildren()) {
			doMapping(map,child,mime);
		}
		return doc;
	}
	
	private void doMapping(ToolMap map_t, Element element, String mime) {
		List<Element> children = element.getChildren();
		for(Element element2 : children) {
			doMapping(map_t, element2,mime);
		}
		
		//get the maps for the element name in the given tool maps for the provided mime type
		ElementMap map_e = map_t.getXmlMapElement(element.getName(), mime);
		if(map_e != null) {
			//check if the map contains a mapped element value
			String newValue = map_e.getMaps().get(element.getText());
			if(newValue != null) {
				element.setText(newValue);
			}
			//also check all attributes for element
			List<Attribute> attributes = element.getAttributes();
			for(Attribute attr : attributes) {
				AttributeMap map_a = map_e.getAttribute(attr.getName());
				if(map_a != null) {
					String newAttrValue = map_a.getMaps().get(attr.getValue());
					if(newAttrValue != null) {
						attr.setValue(newAttrValue);
					}
				}
			}
		}
	}
	
	private ToolMap getElementMapsForTool(ToolInfo tInfo) {
		for(ToolMap map : toolMaps) {
			String tName = map.getToolName();
			String tVersion = map.getToolVersion();
			if(tName.equalsIgnoreCase(tInfo.getName()) && tVersion == null) {
				return map;
			}
			else if(tVersion != null && tVersion.equalsIgnoreCase(tInfo.getVersion())) {
				return map;
			}
		}
		return null;
	}

}

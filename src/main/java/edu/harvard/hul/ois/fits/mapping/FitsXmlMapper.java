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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.ToolInfo;

/**
 * Transform FITS output as configured in fits_xml_map.xml. Transformations will happen only
 * if configured in the XML file. Otherwise, the output will pass directly through without transformation.
 */
public class FitsXmlMapper {

	private static final String FITS_XML_MAP_PATH = Fits.FITS_XML_DIR+"fits_xml_map.xml";
	private List<ToolMap> toolMaps = new ArrayList<ToolMap>();
    private static Logger logger = Logger.getLogger(FitsXmlMapper.class);

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
				mime = e.getAttributeValue("mimetype"); // (This code is never accessed from any of the unit tests. Is it ever?)
			}
		} catch (JDOMException e) {
			logger.error("Error parsing XML with XPath expression.", e);
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
			if(tName.equalsIgnoreCase(tInfo.getName())) {
				return map;
			}
		}
		return null;
	}

}

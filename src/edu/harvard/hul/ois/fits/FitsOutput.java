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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.identity.FormatVersion;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.Tool.RunStatus;
import edu.harvard.hul.ois.fits.tools.ToolBelt;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.ots.schemas.AES.AudioObject;
import edu.harvard.hul.ois.ots.schemas.DocumentMD.DocumentMD;
import edu.harvard.hul.ois.ots.schemas.MIX.Mix;
import edu.harvard.hul.ois.ots.schemas.TextMD.TextMD;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;


/**
 * This class acts as a wrapper aroudn the fitsXML JDOM Document and provides
 * convenience methods for converting the FITS XML format to standard technical 
 * metdata schemas, accessing identification, file info, and metadata elements
 * @author spencer
 *
 */
public class FitsOutput {
	
	private Document fitsXml;          // This is in the FITS XML format
	private List<Exception> caughtExceptions = new ArrayList<Exception>();
	private Namespace ns = Namespace.getNamespace(Fits.XML_NAMESPACE);
	private XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
	
	//for backwards compatibility with older FITS clients
	public static String VERSION = Fits.VERSION;
	
	public FitsOutput(String fitsXmlStr) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Reader in = new StringReader(fitsXmlStr);
		Document fitsXml = builder.build(in);
		this.fitsXml = fitsXml;
	}
	
	public FitsOutput(Document fitsXml) {
		this.fitsXml = fitsXml;
	}
		
	public void setFitsXml(Document fitsXml) {
		this.fitsXml = fitsXml;
	}

	public Document getFitsXml() {
		return fitsXml;
	}
	
	public List<Exception> getCaughtExceptions() {
		return caughtExceptions;
	}

	public void setCaughtExceptions(List<Exception> caughtExceptions) {
		this.caughtExceptions = caughtExceptions;
	}
	
	@SuppressWarnings("unchecked")
	public List<FitsMetadataElement> getFileInfoElements() {
		Element root = fitsXml.getRootElement();
		Element fileInfo = root.getChild("fileinfo",ns);
		return buildMetadataList(fileInfo);
	}
	
	@SuppressWarnings("unchecked")
	public List<FitsMetadataElement> getFileStatusElements() {
		Element root = fitsXml.getRootElement();
		Element fileStatus = root.getChild("filestatus",ns);
		return buildMetadataList(fileStatus);
	}
		
	@SuppressWarnings("unchecked")
	public List<FitsMetadataElement> getTechMetadataElements() {
		Element root = fitsXml.getRootElement();
		Element metadata = (Element)root.getChild("metadata",ns);
		if(metadata.getChildren().size() > 0) {
			Element techMetadata = (Element)root.getChild("metadata",ns).getChildren().get(0);
			return buildMetadataList(techMetadata);
		}
		else {
			return null;
		}
	}
	
	public String getTechMetadataType() {
		Element root = fitsXml.getRootElement();
		Element metadata = (Element)root.getChild("metadata",ns);
		if(metadata.getChildren().size() > 0) {
			Element techMetadata = (Element)root.getChild("metadata",ns).getChildren().get(0);
			return techMetadata.getName();
		}
		else {
			return null;
		}
	}
	
	public FitsMetadataElement getMetadataElement(String name) {
		try {			
			XPath xpath = XPath.newInstance("//fits:"+name);
			xpath.addNamespace("fits",Fits.XML_NAMESPACE);
			Element node = (Element)xpath.selectSingleNode(fitsXml);
			if(node != null) {
				FitsMetadataElement element = buildMetdataIElements(node);
				return element;
			}
		} catch (JDOMException e) {
			//do nothing
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<FitsMetadataElement> getMetadataElements(String name) {
		List<FitsMetadataElement> elements = new ArrayList<FitsMetadataElement>();
		try {
			XPath xpath = XPath.newInstance("//fits:"+name);
			xpath.addNamespace("fits",Fits.XML_NAMESPACE);
			List<Element> nodes = xpath.selectNodes(fitsXml);
			for(Element e : nodes) {
				elements.add(buildMetdataIElements(e));
			}
			return elements;
		} catch (JDOMException e) {
			//do nothing
		}
		return null;
	}
	
	public boolean hasMetadataElement(String name) {
		FitsMetadataElement element = getMetadataElement(name);
		if(element != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Boolean hasConflictingMetadataElements(String name) {
		List<FitsMetadataElement> elements = getMetadataElements(name);
		if(elements.size() > 1) {
			String elementStatus = elements.get(0).getStatus();
			if(elementStatus != null && elementStatus.equalsIgnoreCase("conflict")) {
				return true;
			}
			else {
				return false;
			}		
		}
		else {
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List buildMetadataList(Element parent) {
		List<FitsMetadataElement> data = new ArrayList<FitsMetadataElement>();
		if(parent == null) {
			return null;
		}
		for(Element child : (List<Element>)parent.getChildren()) {
			data.add(buildMetdataIElements(child));
		}
		return data;
	}
	
	private FitsMetadataElement buildMetdataIElements(Element node) {
		FitsMetadataElement element = new FitsMetadataElement();
		element.setName(node.getName());
		element.setValue(node.getValue());
		Attribute toolName = node.getAttribute("toolname");
		if(toolName != null) {
			element.setReportingToolName(toolName.getValue());
		}
		Attribute toolVersion = node.getAttribute("toolversion");
		if(toolVersion != null) {
			element.setReportingToolVersion(toolVersion.getValue());
		}
		Attribute status = node.getAttribute("status");
		if(status != null) {
			element.setStatus(status.getValue());
		}
		return element;
	}
	
	public void saveToDisk(String location) throws IOException {
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(location),"UTF-8");
		serializer.output(fitsXml, out);
		out.close();
	}
	
	public void output(OutputStream outstream) throws IOException {
		XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		OutputStreamWriter out = new OutputStreamWriter(outstream,"UTF-8");
		serializer.output(fitsXml, out);
		out.close();
	}
	
    public Boolean checkWellFormed() {
		FitsMetadataElement wellFormed = getMetadataElement("well-formed");	
		if(wellFormed != null) {
			return Boolean.valueOf(wellFormed.getValue());
		}
		return null;
    }
    
    public Boolean checkValid() {
		FitsMetadataElement valid = getMetadataElement("valid");	
		if(valid != null) {
			return Boolean.valueOf(valid.getValue());
		}
		return null;    
    }
    
    public String getErrorMessages() {
    	List<FitsMetadataElement> statusElements = getFileStatusElements();
    	String errorMessages = new String();
		for(FitsMetadataElement element : statusElements) {
			if(element.getName() == "message") {
				errorMessages = errorMessages+"\n"+element.getValue();
			}
		}
		return errorMessages;	
    }
		
    /** Return an XmlContent object representing the data from fitsXml. */
    public XmlContent getStandardXmlContent () {
        Element metadata = fitsXml.getRootElement().getChild("metadata",ns);
        
        if(metadata == null) {
        	return null;
        }
        
        XmlContentConverter conv = new XmlContentConverter ();
        
        //Element metadata = root.getChild("metadata");
        // This can have an image, document, text, or audio subelement.
        Element subElem = metadata.getChild ("image",ns);
        if (subElem != null) {
        	Element fileinfo = fitsXml.getRootElement().getChild("fileinfo",ns);
            // Process image metadata...
        	return (Mix) conv.toMix (subElem,fileinfo);
        }
        subElem = metadata.getChild ("text",ns);
        if (subElem != null) {
        	// Process text metadata...
        	return (TextMD) conv.toTextMD (subElem);
        }
        subElem = metadata.getChild ("document",ns);
        if (subElem != null) {
            // Process document metadata...
        	return (DocumentMD)conv.toDocumentMD (subElem);
        }
        subElem = metadata.getChild ("audio",ns);
        if (subElem != null) {
            // Process audio metadata...
        	return (AudioObject)conv.toAES (this,subElem);
        }
        return null;
    }
    
    public void addStandardCombinedFormat() throws XMLStreamException, IOException, FitsException {
		//get the normal fits xml output
		Namespace ns = Namespace.getNamespace(Fits.XML_NAMESPACE);
		
		Element metadata = (Element) fitsXml.getRootElement().getChild("metadata",ns); 
		Element techmd = null;
		if(metadata.getChildren().size() > 0) {
			techmd = (Element) metadata.getChildren().get(0);
		}

		//if we have technical metadata convert it to the standard form
		if(techmd != null && techmd.getChildren().size() > 0) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XmlContent xml = getStandardXmlContent();
			if(xml != null) {
				XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(baos);
				xml.output(sw);
			
				String stdxml = baos.toString("UTF-8");
				
				//convert the std xml back to a JDOM element so we can insert it back into the fitsXml Document
				try {
					StringReader sReader = new StringReader(stdxml);
					SAXBuilder saxBuilder = new SAXBuilder();
					Document stdXmlDoc = saxBuilder.build(sReader);
					Element stdElement = new Element("standard",ns);
					stdElement.addContent(stdXmlDoc.getRootElement().detach());
					techmd.addContent(stdElement);
					
				}
				catch(JDOMException e) {
					throw new FitsException("error converting standard XML", e);
				}
			}
		}
    }
    
	@SuppressWarnings("unchecked")
	public List<FitsIdentity> getIdentities() {
		List<FitsIdentity> identities = new ArrayList<FitsIdentity>();
		try {
			XPath xpath = XPath.newInstance("//fits:identity");
			Namespace ns = Namespace.getNamespace("fits",Fits.XML_NAMESPACE);
			xpath.addNamespace(ns);
			List<Element> identElements = xpath.selectNodes(fitsXml);
			for(Element element : identElements) {
				FitsIdentity fileIdentSect = new FitsIdentity();
				
				//get the identity attributes
				Attribute formatAttr = element.getAttribute("format");
				Attribute mimetypeAttr = element.getAttribute("mimetype");
				if(formatAttr != null) {
					fileIdentSect.setFormat(formatAttr.getValue());
				}
				if(mimetypeAttr != null) {
					fileIdentSect.setMimetype(mimetypeAttr.getValue());
				}
				
				//get the tool elements
				List<Element> toolElements = element.getChildren("tool",ns);
				for(Element toolElement : toolElements) {
					ToolInfo toolInfo = new ToolInfo();
					Attribute toolNameAttr = toolElement.getAttribute("toolname");
					Attribute toolVersionAttr = toolElement.getAttribute("toolversion");
					if(toolNameAttr != null) {
						toolInfo.setName(toolNameAttr.getValue());
					}
					if(toolVersionAttr != null) {
						toolInfo.setVersion(toolVersionAttr.getValue());
					}
					fileIdentSect.addReportingTool(toolInfo);
				}
				
				//get the version elements
				List<Element> versionElements = element.getChildren("version",ns);
				for(Element versionElement : versionElements) {
					ToolInfo toolInfo = new ToolInfo();
					Attribute toolNameAttr = versionElement.getAttribute("toolname");
					Attribute toolVersionAttr = versionElement.getAttribute("toolversion");
					if(toolNameAttr != null) {
						toolInfo.setName(toolNameAttr.getValue());
					}
					if(toolVersionAttr != null) {
						toolInfo.setVersion(toolVersionAttr.getValue());
					}
					String value = versionElement.getText();
					FormatVersion formatVersion = new FormatVersion(value,toolInfo);
					fileIdentSect.addFormatVersion(formatVersion);
				}
				
				//get the externalIdentifier elements
				List<Element> xIDElements = element.getChildren("externalIdentifier",ns);
				for(Element xIDElement : xIDElements) {
					String type = xIDElement.getAttributeValue("type");
					String value = xIDElement.getText();
					ToolInfo toolInfo = new ToolInfo();
					Attribute toolNameAttr = xIDElement.getAttribute("toolname");
					Attribute toolVersionAttr = xIDElement.getAttribute("toolversion");
					if(toolNameAttr != null) {
						toolInfo.setName(toolNameAttr.getValue());
					}
					if(toolVersionAttr != null) {
						toolInfo.setVersion(toolVersionAttr.getValue());
					}
					ExternalIdentifier xid = new ExternalIdentifier(type,value,toolInfo);
					fileIdentSect.addExternalID(xid);
				}
				identities.add(fileIdentSect);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return identities;
	}
	
	public void createStatistics(ToolBelt toolBelt, String ext, long totalExecutionTime) {
		Element root = fitsXml.getRootElement();
		Element statistics = new Element("statistics",ns); 

		for(Tool t: toolBelt.getTools()) {
			
			//if the tool should have been used for the file, else ignore it because it did not run
			ToolInfo info = t.getToolInfo();
			Element tool = new Element("tool",ns);
			tool.setAttribute("toolname", info.getName());
			tool.setAttribute("toolversion", info.getVersion());
			
			//if the tool ran successfully then output the execution time
			if(t.getRunStatus() == RunStatus.SUCCESSFUL) {
				tool.setAttribute("executionTime",String.valueOf(t.getDuration()));
			}
			//else if the tool should have run but never changed to a successful state
			else if (t.getRunStatus() == RunStatus.SHOULDRUN){
				tool.setAttribute("status","failed");
			}
			//else if the tool should have run but never changed to a successful state
			else if (t.getRunStatus() == RunStatus.SHOULDNOTRUN){
				tool.setAttribute("status","did not run");
			}
			
			statistics.addContent(tool);
			
		}
		statistics.setAttribute("fitsExecutionTime",String.valueOf(totalExecutionTime));
		
		root.addContent(statistics);
		
	}
}

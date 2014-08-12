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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.ToolIdentity;

/**
 *   The output created by a Tool. A ToolOutput object holds JDOM objects
 *   representing the FITS output and the raw form of the output.
 */
public class ToolOutput {	
	
	private Logger logger = Logger.getLogger(this.getClass());

    private static DocumentBuilderFactory docBuilderFactory;
    static 
    {
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        docBuilderFactory.setValidating(true);
        docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", Fits.FITS_HOME+Fits.internalOutputSchema);
    }

    private static Namespace ns = Namespace.getNamespace("fits",Fits.XML_NAMESPACE);

	//The FITS formatted XML
	private Document fitsXml = null;
	//Wrapper for raw output from the tool
	private Document toolOutput = null;
	//Reference to the tool the output was created with
	private Tool tool;
	//Identification data about the image
	private List<ToolIdentity> identity = new ArrayList<ToolIdentity>();
	
	/** Constructor
	 * 
	 *  @param tool       The Tool creating this output
	 *  @param fitsXml    JDOM Document following the FITS output schema
	 *  @param toolOutput Raw XML JDOM Document representing the original output
	 *                    of the tool
	 */
	public ToolOutput(Tool tool, Document fitsXml, Document toolOutput) throws FitsToolException {
		if(Fits.validateToolOutput && fitsXml !=null && !validateXmlOutput(fitsXml)) {
			throw new FitsToolException(tool.getToolInfo().getName()+" "+
					tool.getToolInfo().getVersion() + " produced invalid FITS XML output");
		}
		
		this.tool = tool;
		this.toolOutput = toolOutput;
		//map values and get identities from fitsXML if not null 
		if(fitsXml != null) {
			//fitsxml doc is mapped here before identities are extracted
			this.fitsXml = Fits.mapper.applyMap(tool,fitsXml);
			identity = createFileIdentities(fitsXml,tool.getToolInfo());
		}		
	}
	
	public ToolOutput(Tool tool, Document fitsXml) throws FitsToolException {
		this(tool,fitsXml,null);	
	}

	/** Returns the Tool that created this object */
	public Tool getTool() {
		return tool;
	}
	
	/** Returns the FITS-structured XML as a JDOM Document */
	public Document getFitsXml() {
		return fitsXml;
	}
	
	/** Sets the FITS-structured XML as a JDOM Document */
	public void setFitsXml(Document fitsXml) {
		this.fitsXml = fitsXml;
	}
	
	/** Returns the raw XML from the tool */
	public Document getToolOutput() {
		return toolOutput;
	}
	
	/** Returns a List of identity information objects about the file,
	 *  one for each reporting tool */
	public List<ToolIdentity> getFileIdentity() {
		return identity;
	}
	
	/** Add a tool's identity information on a file to the identity list 
	 */
	public void addFileIdentity(ToolIdentity id) {
		identity.add(id);
	}
	
	private boolean validateXmlOutput(Document output) {
		
		try {
//			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
//			docBuilderFactory.setNamespaceAware(true);
//			docBuilderFactory.setValidating(true);
//			docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
//			docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", Fits.FITS_HOME+Fits.internalOutputSchema);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.setErrorHandler (new ToolErrorHandler()); 
			
			XMLOutputter outputter = new XMLOutputter();
			String xml = outputter.outputString(output);
			
			docBuilder.parse(new InputSource(new StringReader(xml)));
			} 
			catch(Exception e) {
				logger.error("tool returned invalid XML",e);
				return false;
			} 
			return true;
		
		/*
		SAXBuilder builder =  new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
		builder.setFeature("http://apache.org/xml/features/validation/schema", true);
		builder.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
								Fits.FITS_HOME+Fits.internalOutputSchema);
		
		
		XMLOutputter outputter2 = new XMLOutputter(Format.getPrettyFormat());
		try {
			outputter2.output(output, System.out);
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			XMLOutputter outputter = new XMLOutputter();
			String xml = outputter.outputString(output);
			builder.build(new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			
			return false;
		} 
	    return true;*/
	}
	
	private List<ToolIdentity> createFileIdentities(Document dom, ToolInfo info) {
		List<ToolIdentity> identities = new ArrayList<ToolIdentity>();
		try {
			XPath xpath = XPath.newInstance("//fits:identity");
			xpath.addNamespace(ns);
			@SuppressWarnings("unchecked")
            List<Element> identElements = xpath.selectNodes(dom);
			for(Element element : identElements) {
				Attribute formatAttr = element.getAttribute("format");
				Attribute mimetypeAttr = element.getAttribute("mimetype");
				Element versionElement = element.getChild("version",ns);
				
				String format = null;
				String mimetype = null;
				String version = null;
				
				if(formatAttr != null) {
					format = formatAttr.getValue();
				}
				if(mimetypeAttr != null) {
					mimetype = mimetypeAttr.getValue();
				}
				if(versionElement != null) {
					version = versionElement.getText();
				}
				ToolIdentity identity = new ToolIdentity(mimetype,format,version,info);
				List<Element> xIDElements = element.getChildren("externalIdentifier",ns);
				for(Element xIDElement : xIDElements) {
					String type = xIDElement.getAttributeValue("type");
					String value = xIDElement.getText();
					ExternalIdentifier xid = new ExternalIdentifier(type,value,info);
					identity.addExternalIdentifier(xid);
				}
				identities.add(identity);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return identities;
	}
	
}

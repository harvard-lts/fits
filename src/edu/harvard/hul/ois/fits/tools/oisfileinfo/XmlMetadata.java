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
package edu.harvard.hul.ois.fits.tools.oisfileinfo;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

/** A FITS-native tool for getting informationa about XML files.
 */
public class XmlMetadata extends ToolBase {
	
    private final static Namespace xsiNS = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
    private final static Namespace fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);

    private final static String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	private final static String XML_FORMAT = "Extensible Markup Language";
	private final static String XML_MIME = "text/xml";
	
    private final static String TOOL_NAME = "OIS XML Metadata";
    private final static String TOOL_VERSION = "0.2";
    private final static String TOOL_DATE = "12/22/10";
    
	private boolean enabled = true;
	
    private static Logger logger = Logger.getLogger(XmlMetadata.class);
    
	public XmlMetadata() throws FitsToolException{
	    logger.debug ("Initializing XmlMetadata");
		info.setName(TOOL_NAME);
		info.setVersion(TOOL_VERSION);
		info.setDate(TOOL_DATE);
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {	
        logger.debug("XmlMetadata.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		Document doc = createXml(file);
		output = new ToolOutput(this,(Document)doc.clone(),doc);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("XmlMetadata.extractInfo finished on " + file.getName());
		return output;
	}
	
	private Document createXml(File file) throws FitsToolException {

		
		Element root = new Element("fits",fitsNS);
		root.setAttribute(new Attribute("schemaLocation","http://hul.harvard.edu/ois/xml/ns/fits/fits_output "+Fits.externalOutputSchema,xsiNS));
		//If the file is XML then parse and set text metadata
		if(file.getPath().toLowerCase().endsWith(".xml")) {
			Element metadata = new Element("metadata",fitsNS);
			Element textMetadata = new Element("text",fitsNS);
			
			Document xml = null;
			try {
				saxBuilder.setFeature("http://apache.org/xml/features/validation/schema",false);
				saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				xml = saxBuilder.build(file);
			} catch (Exception e) {
			    logger.error ("Error parsing "+file.getPath() +
			            ": " + e.getClass().getName());
				throw new FitsToolException("Error parsing "+file.getPath(),e);
			}
			if(xml != null) {
				//assume we have at least well formed XML
				Element identification = new Element("identification",fitsNS);
				Element identity = new Element("identity",fitsNS);
				identity.setAttribute("format",XML_FORMAT);
				identity.setAttribute("mimetype",XML_MIME);
				//add identity to identification section
				identification.addContent(identity);
				//add identification section to root
				root.addContent(identification);
								
				Element xmlRoot = xml.getRootElement();					
				String defaultNamespaceURI = xmlRoot.getNamespaceURI();
				@SuppressWarnings("unchecked")
				List<Namespace> namespaces = xmlRoot.getAdditionalNamespaces();
				String schemaPrefix = null;
				for(Namespace n : namespaces) {
					if(n.getURI().equalsIgnoreCase(XML_SCHEMA_INSTANCE)) {
						schemaPrefix = n.getPrefix();
						break;
					}
				}
				
				Namespace schemaNS = xmlRoot.getNamespace(schemaPrefix);
				String schemaLocations = null;
				String noNamespaceSchemaLocation = null;
				if(schemaNS != null) {
					schemaLocations = xmlRoot.getAttributeValue("schemaLocation",schemaNS);
					noNamespaceSchemaLocation = xmlRoot.getAttributeValue("noNamespaceSchemaLocation",schemaNS);
				}

				String schemaURI = null;
				if(schemaLocations != null && schemaLocations.length() > 0)  { 
					String[] locations = schemaLocations.split("\\s+");					
					for(int i=0;i<locations.length;i++) {
						if(locations[i].equalsIgnoreCase(defaultNamespaceURI)) {
							schemaURI = locations[i+1];
							break;
						}
					}		
				}
				else if(noNamespaceSchemaLocation != null && noNamespaceSchemaLocation.length() > 0)  { 
					schemaURI = noNamespaceSchemaLocation;
				}

				Element markupLanguage = new Element("markupLanguage",fitsNS);
				markupLanguage.setText(schemaURI);
				textMetadata.addContent(markupLanguage);

				//check for doctype DTD
				if(xml.getDocType() != null) {
					if(xml.getDocType().getSystemID() != null) {
						Element dtd = new Element("markupLanguage",fitsNS);
						dtd.setText(xml.getDocType().getSystemID());
						textMetadata.addContent(dtd);
					}
				}
				
				
				//add to metadata section
				metadata.addContent(textMetadata);
				//add metadata section to root
				root.addContent(metadata);
			}
		}
		
		return new Document(root);
    }
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}

}

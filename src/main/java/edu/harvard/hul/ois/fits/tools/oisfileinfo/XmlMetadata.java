//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.oisfileinfo;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import java.io.File;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A FITS-native tool for getting informationa about XML files.
 */
public class XmlMetadata extends ToolBase {

    private static final Namespace xsiNS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    private static final Namespace fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);

    private static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XML_FORMAT = "Extensible Markup Language";
    private static final String XML_MIME = "text/xml";

    private static final String TOOL_NAME = "OIS XML Metadata";
    private static final String TOOL_VERSION = "0.2";
    private static final String TOOL_DATE = "12/22/10";

    private boolean enabled = true;
    private final Fits fits;

    private static final Logger logger = LoggerFactory.getLogger(XmlMetadata.class);

    public XmlMetadata(Fits fits) throws FitsToolException {
        super();
        this.fits = fits;
        logger.debug("Initializing XmlMetadata");
        info.setName(TOOL_NAME);
        info.setVersion(TOOL_VERSION);
        info.setDate(TOOL_DATE);
    }

    public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("XmlMetadata.extractInfo starting on " + file.getName());
        long startTime = System.currentTimeMillis();
        Document doc = createXml(file);
        output = new ToolOutput(this, doc.clone(), doc, fits);
        duration = System.currentTimeMillis() - startTime;
        runStatus = RunStatus.SUCCESSFUL;
        logger.debug("XmlMetadata.extractInfo finished on " + file.getName());
        return output;
    }

    private Document createXml(File file) throws FitsToolException {

        Element root = new Element("fits", fitsNS);
        root.setAttribute(new Attribute(
                "schemaLocation",
                "http://hul.harvard.edu/ois/xml/ns/fits/fits_output " + fits.getExternalOutputSchema(),
                xsiNS));
        // If the file is XML then parse and set text metadata
        if (file.getPath().toLowerCase().endsWith(".xml")) {
            Element metadata = new Element("metadata", fitsNS);
            Element textMetadata = new Element("text", fitsNS);

            Document xml = null;
            try {
                saxBuilder.setFeature("http://apache.org/xml/features/validation/schema", false);
                saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                xml = saxBuilder.build(file);
            } catch (Exception e) {
                logger.error(
                        "Error parsing " + file.getPath() + ": " + e.getClass().getName());
                throw new FitsToolException("Error parsing " + file.getPath(), e);
            }
            if (xml != null) {
                // assume we have at least well formed XML
                Element identification = new Element("identification", fitsNS);
                Element identity = new Element("identity", fitsNS);
                identity.setAttribute("format", XML_FORMAT);
                identity.setAttribute("mimetype", XML_MIME);
                // add identity to identification section
                identification.addContent(identity);
                // add identification section to root
                root.addContent(identification);

                Element xmlRoot = xml.getRootElement();
                String defaultNamespaceURI = xmlRoot.getNamespaceURI();
                List<Namespace> namespaces = xmlRoot.getAdditionalNamespaces();
                String schemaPrefix = null;
                for (Namespace n : namespaces) {
                    if (n.getURI().equalsIgnoreCase(XML_SCHEMA_INSTANCE)) {
                        schemaPrefix = n.getPrefix();
                        break;
                    }
                }

                Namespace schemaNS = xmlRoot.getNamespace(schemaPrefix);
                String schemaLocations = null;
                String noNamespaceSchemaLocation = null;
                if (schemaNS != null) {
                    schemaLocations = xmlRoot.getAttributeValue("schemaLocation", schemaNS);
                    noNamespaceSchemaLocation = xmlRoot.getAttributeValue("noNamespaceSchemaLocation", schemaNS);
                }

                String schemaURI = null;
                if (schemaLocations != null && schemaLocations.length() > 0) {
                    String[] locations = schemaLocations.split("\\s+");
                    for (int i = 0; i < locations.length; i++) {
                        if (locations[i].equalsIgnoreCase(defaultNamespaceURI)) {
                            schemaURI = locations[i + 1];
                            break;
                        }
                    }
                } else if (noNamespaceSchemaLocation != null && noNamespaceSchemaLocation.length() > 0) {
                    schemaURI = noNamespaceSchemaLocation;
                }

                Element markupLanguage = new Element("markupLanguage", fitsNS);
                markupLanguage.setText(schemaURI);
                textMetadata.addContent(markupLanguage);

                // check for doctype DTD
                if (xml.getDocType() != null) {
                    if (xml.getDocType().getSystemID() != null) {
                        Element dtd = new Element("markupLanguage", fitsNS);
                        dtd.setText(xml.getDocType().getSystemID());
                        textMetadata.addContent(dtd);
                    }
                }

                // add to metadata section
                metadata.addContent(textMetadata);
                // add metadata section to root
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

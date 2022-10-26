//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits;

import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.identity.FormatVersion;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.Tool.RunStatus;
import edu.harvard.hul.ois.fits.tools.ToolBelt;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as a wrapper around the fitsXML JDOM Document and provides
 * convenience methods for converting the FITS XML format to standard technical
 * metadata schemas, accessing identification, file info, and metadata elements
 * @author spencer
 *
 */
public class FitsOutput {

    private Document fitsXml; // This is in the FITS XML format
    private List<Throwable> caughtThrowables = new ArrayList<>();
    private final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private final Namespace fitsNamespace = Namespace.getNamespace(Fits.XML_NAMESPACE);
    private final XPathFactory xFactory = XPathFactory.instance();

    private static final Logger logger = LoggerFactory.getLogger(FitsOutput.class);

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

    public List<Throwable> getCaughtThrowables() {
        return caughtThrowables;
    }

    public void setCaughtThrowables(List<Throwable> caughtThrowables) {
        this.caughtThrowables = caughtThrowables;
    }

    @SuppressWarnings("unchecked")
    public List<FitsMetadataElement> getFileInfoElements() {
        Element root = fitsXml.getRootElement();
        Element fileInfo = root.getChild("fileinfo", fitsNamespace);
        return buildMetadataList(fileInfo);
    }

    @SuppressWarnings("unchecked")
    public List<FitsMetadataElement> getFileStatusElements() {
        Element root = fitsXml.getRootElement();
        Element fileStatus = root.getChild("filestatus", fitsNamespace);
        return buildMetadataList(fileStatus);
    }

    @SuppressWarnings("unchecked")
    public List<FitsMetadataElement> getTechMetadataElements() {
        Element root = fitsXml.getRootElement();
        Element metadata = root.getChild("metadata", fitsNamespace);
        if (metadata.getChildren().size() > 0) {
            Element techMetadata =
                    root.getChild("metadata", fitsNamespace).getChildren().get(0);
            return buildMetadataList(techMetadata);
        } else {
            return null;
        }
    }

    public String getTechMetadataType() {
        Element root = fitsXml.getRootElement();
        Element metadata = root.getChild("metadata", fitsNamespace);
        if (metadata.getChildren().size() > 0) {
            Element techMetadata =
                    root.getChild("metadata", fitsNamespace).getChildren().get(0);
            return techMetadata.getName();
        } else {
            return null;
        }
    }

    public FitsMetadataElement getFileInfoElement(String name) {
        Element root = fitsXml.getRootElement();
        Element fileInfo = root.getChild("fileinfo", fitsNamespace);
        if (fileInfo.getChildren().size() > 0) {
            Element element = fileInfo.getChild(name, fitsNamespace);
            if (element != null) {
                return buildMetdataIElements(element);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public FitsMetadataElement getMetadataElement(String name) {
        XPathExpression<Element> expr = xFactory.compile("//fits:" + name, Filters.element(), null, fitsNamespace);
        Element node = expr.evaluateFirst(fitsXml);
        if (node != null) {
            FitsMetadataElement element = buildMetdataIElements(node);
            return element;
        }
        return null;
    }

    public List<FitsMetadataElement> getMetadataElements(String name) {
        List<FitsMetadataElement> elements = new ArrayList<>();
        XPathExpression<Element> expr = xFactory.compile("//fits:" + name, Filters.element(), null, fitsNamespace);
        List<Element> nodes = expr.evaluate(fitsXml);
        for (Element e : nodes) {
            elements.add(buildMetdataIElements(e));
        }
        return elements;
    }

    public boolean hasMetadataElement(String name) {
        FitsMetadataElement element = getMetadataElement(name);
        if (element != null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean hasConflictingMetadataElements(String name) {
        List<FitsMetadataElement> elements = getMetadataElements(name);
        if (elements.size() > 1) {
            String elementStatus = elements.get(0).getStatus();
            if (elementStatus != null && elementStatus.equalsIgnoreCase("conflict")) {
                return true;
            } else {
                return false;
            }
        } else {
            return null;
        }
    }

    private List buildMetadataList(Element parent) {
        List<FitsMetadataElement> data = new ArrayList<>();
        if (parent == null) {
            return null;
        }
        for (Element child : parent.getChildren()) {
            data.add(buildMetdataIElements(child));
        }
        return data;
    }

    private FitsMetadataElement buildMetdataIElements(Element node) {
        FitsMetadataElement element = new FitsMetadataElement();
        element.setName(node.getName());
        element.setValue(node.getValue());
        Attribute toolName = node.getAttribute("toolname");
        if (toolName != null) {
            element.setReportingToolName(toolName.getValue());
        }
        Attribute toolVersion = node.getAttribute("toolversion");
        if (toolVersion != null) {
            element.setReportingToolVersion(toolVersion.getValue());
        }
        Attribute status = node.getAttribute("status");
        if (status != null) {
            element.setStatus(status.getValue());
        }
        return element;
    }

    public void saveToDisk(String location) throws IOException {
        XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
        try (Writer out = Files.newBufferedWriter(Paths.get(location))) {
            serializer.output(fitsXml, out);
        }
    }

    public void output(OutputStream outstream) throws IOException {
        XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
        try (Writer out = new BufferedWriter(new OutputStreamWriter(outstream))) {
            serializer.output(fitsXml, out);
        }
    }

    public Boolean checkWellFormed() {
        FitsMetadataElement wellFormed = getMetadataElement("well-formed");
        if (wellFormed != null) {
            return Boolean.valueOf(wellFormed.getValue());
        }
        return null;
    }

    public Boolean checkValid() {
        FitsMetadataElement valid = getMetadataElement("valid");
        if (valid != null) {
            return Boolean.valueOf(valid.getValue());
        }
        return null;
    }

    public String getErrorMessages() {
        List<FitsMetadataElement> statusElements = getFileStatusElements();
        String errorMessages = new String();
        for (FitsMetadataElement element : statusElements) {
            if ("message".equals(element.getName())) {
                errorMessages = errorMessages + "\n" + element.getValue();
            }
        }
        return errorMessages;
    }

    /** Return an XmlContent object representing the data from fitsXml. */
    public XmlContent getStandardXmlContent() {

        Element metadata = fitsXml.getRootElement().getChild("metadata", fitsNamespace);

        if (metadata == null) {
            return null;
        }

        XmlContentConverter conv = new XmlContentConverter();

        // Element metadata = root.getChild("metadata");
        // This can have an image, document, text, or audio subelement.
        Element subElem = metadata.getChild("image", fitsNamespace);
        if (subElem != null) {
            Element fileinfo = fitsXml.getRootElement().getChild("fileinfo", fitsNamespace);
            // Process image metadata...
            return conv.toMix(subElem, fileinfo);
        }
        subElem = metadata.getChild("text", fitsNamespace);
        if (subElem != null) {
            // Process text metadata...
            return conv.toTextMD(subElem);
        }
        subElem = metadata.getChild("document", fitsNamespace);
        if (subElem != null) {
            // Process document metadata...
            return conv.toDocumentMD(subElem);
        }
        subElem = metadata.getChild("audio", fitsNamespace);
        if (subElem != null) {
            // Process audio metadata...
            return conv.toAES(this, subElem);
        }
        subElem = metadata.getChild("video", fitsNamespace);
        if (subElem != null) {
            // Process video metadata...
            return conv.toEbuCoreVideo(this, subElem);
        }
        subElem = metadata.getChild("container", fitsNamespace);
        if (subElem != null) {
            // Process container metadata...
            return conv.toContainerMD(subElem);
        }

        return null;
    }

    public void addStandardCombinedFormat() throws XMLStreamException, IOException, FitsException {
        // get the normal fits xml output
        Namespace ns = Namespace.getNamespace(Fits.XML_NAMESPACE);

        Element metadata = fitsXml.getRootElement().getChild("metadata", ns);
        Element techmd = null;
        if (metadata.getChildren().size() > 0) {
            techmd = metadata.getChildren().get(0);
        }

        // if we have technical metadata convert it to the standard form
        if (techmd != null && techmd.getChildren().size() > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XmlContent xml = getStandardXmlContent();
            if (xml != null) {
                XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(baos);
                xml.output(sw);

                String stdxml = baos.toString("UTF-8");

                // convert the std xml back to a JDOM element so we can insert it back into the fitsXml Document
                try {
                    StringReader sReader = new StringReader(stdxml);
                    SAXBuilder saxBuilder = new SAXBuilder();
                    Document stdXmlDoc = saxBuilder.build(sReader);
                    Element stdElement = new Element("standard", ns);
                    stdElement.addContent(stdXmlDoc.getRootElement().detach());
                    techmd.addContent(stdElement);

                } catch (JDOMException e) {
                    throw new FitsException("error converting standard XML", e);
                }
            }
        }
    }

    public List<FitsIdentity> getIdentities() {
        List<FitsIdentity> identities = new ArrayList<>();
        Namespace ns = Namespace.getNamespace("fits", Fits.XML_NAMESPACE);
        XPathExpression<Element> expr = xFactory.compile("//fits:identity", Filters.element(), null, ns);
        List<Element> identElements = expr.evaluate(fitsXml);
        for (Element element : identElements) {
            FitsIdentity fileIdentSect = new FitsIdentity();

            // get the identity attributes
            Attribute formatAttr = element.getAttribute("format");
            Attribute mimetypeAttr = element.getAttribute("mimetype");
            if (formatAttr != null) {
                fileIdentSect.setFormat(formatAttr.getValue());
            }
            if (mimetypeAttr != null) {
                fileIdentSect.setMimetype(mimetypeAttr.getValue());
            }

            // get the tool elements
            List<Element> toolElements = element.getChildren("tool", fitsNamespace);
            for (Element toolElement : toolElements) {
                ToolInfo toolInfo = new ToolInfo();
                Attribute toolNameAttr = toolElement.getAttribute("toolname");
                Attribute toolVersionAttr = toolElement.getAttribute("toolversion");
                if (toolNameAttr != null) {
                    toolInfo.setName(toolNameAttr.getValue());
                }
                if (toolVersionAttr != null) {
                    toolInfo.setVersion(toolVersionAttr.getValue());
                }
                fileIdentSect.addReportingTool(toolInfo);
            }

            // get the version elements
            List<Element> versionElements = element.getChildren("version", fitsNamespace);
            for (Element versionElement : versionElements) {
                ToolInfo toolInfo = new ToolInfo();
                Attribute toolNameAttr = versionElement.getAttribute("toolname");
                Attribute toolVersionAttr = versionElement.getAttribute("toolversion");
                if (toolNameAttr != null) {
                    toolInfo.setName(toolNameAttr.getValue());
                }
                if (toolVersionAttr != null) {
                    toolInfo.setVersion(toolVersionAttr.getValue());
                }
                String value = versionElement.getText();
                FormatVersion formatVersion = new FormatVersion(value, toolInfo);
                fileIdentSect.addFormatVersion(formatVersion);
            }

            // get the externalIdentifier elements
            List<Element> xIDElements = element.getChildren("externalIdentifier", fitsNamespace);
            for (Element xIDElement : xIDElements) {
                String type = xIDElement.getAttributeValue("type");
                String value = xIDElement.getText();
                ToolInfo toolInfo = new ToolInfo();
                Attribute toolNameAttr = xIDElement.getAttribute("toolname");
                Attribute toolVersionAttr = xIDElement.getAttribute("toolversion");
                if (toolNameAttr != null) {
                    toolInfo.setName(toolNameAttr.getValue());
                }
                if (toolVersionAttr != null) {
                    toolInfo.setVersion(toolVersionAttr.getValue());
                }
                ExternalIdentifier xid = new ExternalIdentifier(type, value, toolInfo);
                fileIdentSect.addExternalID(xid);
            }
            identities.add(fileIdentSect);
        }
        return identities;
    }

    public void createStatistics(ToolBelt toolBelt, long totalExecutionTime) {
        Element root = fitsXml.getRootElement();
        Element statistics = new Element("statistics", fitsNamespace);

        for (Tool t : toolBelt.getTools()) {

            // if the tool should have been used for the file, else ignore it because it did not run
            ToolInfo info = t.getToolInfo();
            Element tool = new Element("tool", fitsNamespace);
            tool.setAttribute("toolname", info.getName());
            tool.setAttribute("toolversion", info.getVersion());

            // if the tool ran successfully then output the execution time
            if (t.getRunStatus() == RunStatus.SUCCESSFUL) {
                tool.setAttribute("executionTime", String.valueOf(t.getDuration()));
            }
            // else if the tool should have run but never changed to a successful state
            else if (t.getRunStatus() == RunStatus.SHOULDRUN) {
                tool.setAttribute("status", "failed");
            }
            // else if the tool should have run but never changed to a successful state
            else if (t.getRunStatus() == RunStatus.SHOULDNOTRUN) {
                tool.setAttribute("status", "did not run");
            } else if (t.getRunStatus() == RunStatus.FAILED) {
                tool.setAttribute("status", "failed");
            }

            statistics.addContent(tool);
        }
        statistics.setAttribute("fitsExecutionTime", String.valueOf(totalExecutionTime));

        root.addContent(statistics);
    }

    /**
     * This is the version of FITS that created the input file of this instance.
     * @return The FITS version of the input file.
     */
    public String getFitsVersion() {

        String fitsVersion = null;
        Element root = fitsXml.getRootElement();
        Attribute fitsVersionAttr = root.getAttribute("version");
        if (fitsVersionAttr != null) {
            fitsVersion = fitsVersionAttr.getValue();
        }
        return fitsVersion;
    }
}

package edu.harvard.hul.ois.fits.tools.cad;

import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.IllegalNameException;

import java.util.*;

/**
 * Created by Isaac Simmons on 9/23/2015.
 */
public class CadToolResult {
    public final String toolname;
    public final String filename;
    public String uniqueId;
    public String mimetype;
    public String formatName;
    public String formatVersion;
    public String creationDate;
    public String modificationDate;
    public String creatingApplicationName;
    public String description;
    public String author;
    public String title;

    public Map<String, List<String>> customKeyValues = new HashMap<>();
    public List<Element> customElements = new ArrayList<>();

    public CadToolResult(String toolname, String filename) {
        this.toolname = toolname;
        this.filename = filename;
    }

    public void addKeyValue(String key, String value) {
        if (key == null || value == null || key.isEmpty() || value.isEmpty()) {
            return;
        }
        List<String> values = customKeyValues.get(key);
        if (values == null) {
            values = new ArrayList<>();
            customKeyValues.put(key, values);
        }
        values.add(value);
    }

    public void addElement(Element e) {
        customElements.add(e);
    }

    private static void appendElement(String elementName, String content, Element base) {
        if (content != null && !content.isEmpty()) {
            try {
                Element element;
                try {
                    element = new Element(elementName);
                } catch (IllegalNameException e) {
                    element = new Element("_" + elementName);
                }
                element.setText(content);
                base.addContent(element);
            } catch (IllegalDataException ignored) {
                System.out.println("Invalid XML data for: " + elementName);
            }
        }
    }

    public Element toXml() {
        final Element result = new Element("cad-tool-result");
        result.setAttribute("extractor", toolname);
        result.setAttribute("file", filename);

        final Element identity = new Element("identity");
        identity.setAttribute("mimetype", mimetype);
        identity.setAttribute("format", formatName);
        if (formatVersion != null) {
            identity.setAttribute("version", formatVersion);
        }
        result.addContent(identity);

        appendElement("unique-id", uniqueId, result);
        appendElement("created", creationDate, result);
        appendElement("modified", modificationDate, result);
        appendElement("creatingApplicationName", creatingApplicationName, result);
        appendElement("author", author, result);
        appendElement("title", title, result);
        appendElement("description", description, result);

        for(Map.Entry<String, List<String>> e: customKeyValues.entrySet()) {
            for(String value: e.getValue()) {
                appendElement(e.getKey(), value, result);
            }
        }

        for(Element e: customElements) {
            result.addContent(e);
        }

        return result;
    }
}

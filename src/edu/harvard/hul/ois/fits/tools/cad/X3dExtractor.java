package edu.harvard.hul.ois.fits.tools.cad;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import javax.activation.DataSource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Isaac Simmons on 9/13/2015.
 */
public class X3dExtractor extends CadExtractor {
    public static final String DEFAULT_MIMETYPE = "model/x3d";
    public static final String DEFAULT_FORMAT_NAME = "X3D (Extensible 3D) model xml text";

    //TODO: only works with XML encoding for now. Do I care about .x3dv or .wrl?

    public X3dExtractor() {
        super("x3d", ".x3d");
    }

    public static String reformatDate(String x3dDate) {
        final DateFormat x3dFormat = new SimpleDateFormat("dd MMMM yyyy");
        final DateFormat cadToolFormat = new SimpleDateFormat(CadTool.PREFERRED_SHORT_DATE_FORMAT);
        try {
            return cadToolFormat.format(x3dFormat.parse(x3dDate));
        } catch (ParseException ex) {
            return x3dDate;
        }
    }

    @Override
    public CadToolResult run(DataSource ds, String filename) throws IOException, ValidationException {
        final CadToolResult result = new CadToolResult(name, filename);
        final Document doc;
        try {
            doc = new SAXBuilder().build(ds.getInputStream());
        } catch (JDOMException e) {
            throw new ValidationException("Unable to parse X3D XML content", e);
        }
        if (doc.getDocType() == null || doc.getDocType().getSystemID() == null) {
            throw new ValidationException("No doctype system identifier defined for X3D XML content");
        }
        if (! doc.getDocType().getSystemID().startsWith("http://www.web3d.org/specifications/x3d-")) {
            throw new ValidationException("XML doctype system identifier doesn't look like X3D: " + doc.getDocType().getSystemID());
        }
        //TODO: think more about what my tools should output if given invalid files. Maybe it shouldn't propagate an exception all the way up, and instead just not return anything

        if (! "X3D".equals(doc.getRootElement().getName())) {
            throw new ValidationException("x3d document has incorrect root element: " + doc.getRootElement().getName());
        }

        result.mimetype = X3dExtractor.DEFAULT_MIMETYPE;
        result.formatName = X3dExtractor.DEFAULT_FORMAT_NAME;
        final String version = doc.getDocType().getSystemID().substring("http://www.web3d.org/specifications/x3d-".length());
        if (version.endsWith(".dtd")) {
            result.formatVersion = version.substring(0, version.length() - 4);
        }

        //Grab all headers from the X3D doc and shove them into our results
        final List headerNodes;
        try {
            headerNodes = XPath.newInstance("/X3D/head/meta").selectNodes(doc);
        } catch (JDOMException e) {
            throw new RuntimeException(e); //TODO: something better than just upgrading this to a runtime
        }
        for(Object headerNode: headerNodes) {
            if (headerNode instanceof Element) {
                final String name = ((Element) headerNode).getAttributeValue("name");
                final String value = ((Element) headerNode).getAttributeValue("content");
                if (name != null && value != null && !name.isEmpty() && !value.isEmpty()) {
                    switch(name) {
                        case "creator": result.author = value; break;
                        case "identifier": result.uniqueId = value; break;
                        case "generator": result.creatingApplicationName = value; break;
                        case "description": result.description = value; break;
                        case "title": result.title = value; break;
                        case "modified": result.modificationDate = reformatDate(value); break;
                        case "created": result.creationDate = reformatDate(value); break;
                        default: result.addKeyValue(name, value);
                    }
                }
            }
        }
        return result;
    }

    //TODO: I should be able to pull quite a few structural features out of this as well
}

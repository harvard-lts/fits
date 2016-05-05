package edu.harvard.hul.ois.fits.tools.cad;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

public class CadTool extends ToolBase {
    public static final String VERSION = "1.0.0";
    public static final String NAME = "CADTool";
    public static final String CADTOOL_XML_DIR = "cadtool";

    public static final String PREFERRED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mmX";
    public static final String PREFERRED_SHORT_DATE_FORMAT = "yyyy-MM-dd";

    private static final String CADTOOL_XSLT_RESOURCE = Fits.FITS_XML_DIR + CADTOOL_XML_DIR + File.separator + "/cadtool_to_fits.xslt";

    private final Map<String, CadExtractor> extractors;
    private final XSLTransformer transformer;
    private boolean enabled = true;

    public CadTool() throws FitsToolException {
        super();
        setName(CadTool.NAME);
        getToolInfo().setName(CadTool.NAME);
        getToolInfo().setVersion(VERSION);
        final Map<String, CadExtractor> temp = new HashMap<>();
        final CadExtractor[] allExtractors= new CadExtractor[] {
                new DwgExtractor(),
                new DxfExtractor(),
                new X3dExtractor(),
                new PdfExtractor()
        };
        final Set<String> extractorNames = new HashSet<>();
        for (CadExtractor extractor: allExtractors) {
            if (! extractorNames.add(extractor.getName())) {
                throw new FitsToolException("Tried to initialize with multiple cad extractors with same name: " +
                        extractor.getName());
            }
            for(String extension: extractor.getExtensions()) {
                if (temp.containsKey(extension)) {
                    throw new FitsToolException("Tried to initialize with multiple cad extractors (" +
                            extractor.getName() + ", " + temp.get(extension).getName() + ") for extension \"" +
                            extension + "\"");
                }
                temp.put(extension, extractor);
            }
        }
        extractors = Collections.unmodifiableMap(temp);

        try {
            this.transformer = new XSLTransformer(CADTOOL_XSLT_RESOURCE);
        } catch (XSLTransformException e) {
            throw new FitsToolException("Error initializing JDOM XSL Transformer");
        }
    }

    protected ToolOutput extractInfo(String filename, DataSource dataSource) throws FitsToolException {
        //TODO: this won't work for multi-part extensions (ie. blah.vrml.xml)
        //TODO: test sequentially starting with first period and stripping them one at a time
        long startTime = System.currentTimeMillis();

        final int lastPeriod = filename.lastIndexOf('.');
        if (lastPeriod == -1) {
            throw new FitsToolException("cadtool invoked on file with no extension: " + filename);
        }
        final String extension = filename.substring(lastPeriod).toLowerCase();
        if (! extractors.containsKey(extension)) {
            throw new FitsToolException("cadtool invoked on file with unsupported extension: " + filename);
        }
        final CadExtractor extractor = extractors.get(extension);
        final CadToolResult results;

        try {
            results = extractor.run(dataSource, filename);
        } catch (ValidationException e) {
            //TODO: maybe this shouldn't throw an exception but instead just return some "empty" output
            throw new FitsToolException("Error running cad extractor " + extractor.getName() + " on " + filename, e);
        } catch (IOException e) {
            e.printStackTrace();  //Printing here for debug since FitsToolExceptions don't inherit stack trace
            throw new FitsToolException("Error running cad extractor " + extractor.getName() + " on " + filename, e);
        }

        final Document toolOutput = new Document(results.toXml());
        final Document fitsOutput;
        try {
            fitsOutput = transformer.transform(toolOutput);
        } catch (JDOMException e) {
            e.printStackTrace(); //Printing here for debug since FitsToolExceptions don't inherit stack trace
            throw new FitsToolException("Error transforming tool output to fits output", e);
        }

        setRunStatus(RunStatus.SUCCESSFUL);
        this.duration = System.currentTimeMillis() - startTime;
        return new ToolOutput(this, fitsOutput, toolOutput);
    }

    @Override
    public ToolOutput extractInfo(File file) throws FitsToolException {
        return extractInfo(file.getName(), new FileDataSource(file));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static void main(String[] args) throws FitsToolException, IOException {
        //TODO: check args, print usage message
        final File file = new File(args[0]);
        final CadTool cadTool = new CadTool();
        final ToolOutput results = cadTool.extractInfo(file);
        new XMLOutputter(Format.getPrettyFormat()).output(results.getToolOutput(), System.out);
    }
}
package edu.harvard.hul.ois.fits.tools.cad;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tests.AbstractLoggingTest;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

public class TestCadSampleFiles extends AbstractLoggingTest {
	
	private static final Logger logger = Logger.getLogger(TestCadSampleFiles.class);
    private static final XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
    private static CadTool cadTool;

    public static final List<String> PDF_TEST_FILES = Arrays.asList(
            "testfiles/1344464123.pdf",
            "testfiles/1344465784.pdf",
            "testfiles/DominicNotman_HydraulicChuck.pdf",
            "testfiles/Kompas-Stanchion_eng.pdf",
            "testfiles/mayavi_conic_spiral.pdf",
            "testfiles/PDF3D_COMSOL_EigenvalueAnalysisOfACrankshaft.pdf"
    );

    public static final List<String> DWG_TEST_FILES = Arrays.asList(
            "testfiles/civil_example-imperial.dwg",
            "testfiles/Figure_A04.dwg",
            "testfiles/Pump_cover.dwg",
            "testfiles/visualization_-_sun_and_sky_demo.dwg",
            "testfiles/5217plan.dwg"
    );

    public static final List<String> X3D_TEST_FILES = Arrays.asList(
            "testfiles/5000points.x3d",
            "testfiles/extents.x3d",
            "testfiles/HelloWorld.x3d",
            "testfiles/NonplanarPolygons.x3d",
            "testfiles/test-ccwsolid.x3d",
            "testfiles/TriangleStripSet.x3d"
    );

    public static final List<String> DXF_TEST_FILES = Arrays.asList(
            "testfiles/5217plan.dxf",
            "testfiles/Bottom_plate.dxf",
            "testfiles/Pump_cover.dxf",
            "testfiles/R-126_strat_plan01.dxf"
    );

    public static final List<String> ALL_TEST_FILES = new ArrayList<>();
    static {
        ALL_TEST_FILES.addAll(PDF_TEST_FILES);
        ALL_TEST_FILES.addAll(DWG_TEST_FILES);
        ALL_TEST_FILES.addAll(X3D_TEST_FILES);
        ALL_TEST_FILES.addAll(DXF_TEST_FILES);
    }

<<<<<<< HEAD
	@BeforeClass
	public static void beforeClass() throws Exception {
		// Need to initialize FITS for static values used in CadTool.
		new Fits();
		cadTool = new CadTool();
	}
=======
    public TestCadSampleFiles() throws FitsToolException {
        cadTool = new CadTool();
    }
>>>>>>> 638d641... Fix all unit test results resulting from adding CadTool except PDF tests in DocMDXmlUnitTest.

    private Element testFiles(String elementName, Collection<String> files) throws FitsToolException {
        final Element results = new Element(elementName);
        for (String filename: files) {
            final File file = new File(filename);
            assertNotNull(file);
            final ToolOutput output = cadTool.extractInfo(file);
            results.addContent(output.getToolOutput().detachRootElement());
            results.addContent(output.getFitsXml().detachRootElement());
        }
        return results;
    }

    @Test
    public void testPdfFiles() throws IOException, FitsToolException {
        final Element results = testFiles("pdf-test-results", PDF_TEST_FILES);
        logResults(results);
    }

    @Test
    public void testDwgFiles() throws IOException, FitsToolException {
        final Element results = testFiles("dwg-test-results", DWG_TEST_FILES);
        logResults(results);
    }

    @Test
    public void testX3dFiles() throws IOException, FitsToolException {
        final Element results = testFiles("x3d-test-results", X3D_TEST_FILES);
        logResults(results);
    }

    @Test
    public void testDxfFiles() throws IOException, FitsToolException {
        final Element results = testFiles("dxf-test-results", DXF_TEST_FILES);
        logResults(results);
    }

    @Test
    @Ignore
    public void testAllFiles() throws IOException, FitsToolException {
        final Element results = testFiles("test-results", ALL_TEST_FILES);
        logResults(results);
    }

    /*
     * Send the output of the results to logger.
     */
    private void logResults(Element results) throws IOException {
        StringWriter sw = new StringWriter();
        out.output(results, sw);
        logger.info(sw.toString());
    }
}



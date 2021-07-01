package edu.harvard.hul.ois.fits.tools.tika;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.identity.ToolIdentity;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class TikaToolTest {

    private static final String RESOURCE_DIR = "src/test/resources/test-files/";

    private TikaTool tool;

    @Before
    public void setup() throws Exception {
        tool = new TikaTool(new Fits());
    }

    @Test
    public void shouldDetectFormatBasedOnFileName() throws FitsToolException {
        File file = new File(RESOURCE_DIR + "example.kml");
        ToolOutput output = tool.extractInfo(file);

        assertEquals(1, output.getFileIdentity().size());

        ToolIdentity identity = output.getFileIdentity().get(0);
        assertEquals("application/vnd.google-earth.kml+xml", identity.getMime());
        assertEquals("Keyhole Markup Language", identity.getFormat());
    }

}

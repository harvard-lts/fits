package edu.harvard.hul.ois.fits.tools.cad;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Isaac Simmons on 9/13/2015.
 */
public class DwgExtractor extends CadExtractor {
    public static final String DEFAULT_MIMETYPE = "image/vnd.dwg";
    public static final String DEFAULT_FORMAT_NAME = "AutoCad Drawing";

    private final MagicNumberValidator validator = MagicNumberValidator.string("AC10", false);

    public DwgExtractor() {
        super("dwg", ".dwg");
    }

    public static String versionSuffixLookup(String suffix) {
        switch(suffix) {
            case "01": return "AutoCAD R2.22";
            case "02": return "AutoCAD R2.50";
            case "03": return "AutoCAD R2.60";
            case "04": return "AutoCAD R9";
            case "06": return "AutoCAD R10";
            case "09": return "AutoCAD R11/R12";
            case "10": case "11": case "12": return "AutoCAD R13";
            case "13": case "14": return "AutoCAD R14";
            case "15": return "AutoCAD R2000";
            case "18": return "AutoCAD R2004";
            case "21": return "AutoCAD R2007";
            case "24": return "AutoCAD R2010";
            case "27": return "AutoCAD R2014";
            default: System.out.println("Unrecognized AutoCAD version suffix: " + suffix); return null;
        }
    }

    @Override
    public CadToolResult run(DataSource ds, String filename) throws IOException, ValidationException {
        final CadToolResult result = new CadToolResult(name, filename);
        try (final InputStream in = ds.getInputStream()) {
            validator.validate(in);
            result.mimetype = DwgExtractor.DEFAULT_MIMETYPE;
            result.formatName = DwgExtractor.DEFAULT_FORMAT_NAME;
            final byte[] versionBytes = MagicNumberValidator.readBytes(in, 2);
            result.formatVersion = DwgExtractor.versionSuffixLookup(new String(versionBytes, StandardCharsets.US_ASCII));
            return result;
        }
    }
}

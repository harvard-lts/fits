package edu.harvard.hul.ois.fits.tools.cad;

import org.jdom.Element;

import javax.activation.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Isaac Simmons on 9/13/2015.
 */
public class DxfExtractor extends CadExtractor {
    private static final int GROUPCODE_READAHEAD_LIMIT = 64;
    public static final String DEFAULT_MIMETYPE = "image/vnd.dxf";
    public static final String DEFAULT_FORMAT_NAME = "Drawing eXchange Format";

    public DxfExtractor() {
        super("dxf", ".dxf");
    }

    private static void seekToHeaderStart(BufferedReader reader) throws ValidationException, IOException {
        //Looking for lines that read, in sequence, {"0", "SECTION", "2", "HEADER"}
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if ("0".equals(line)) {
                line = reader.readLine();
                if (line == null) {
                    throw new ValidationException("Unexpected end of DXF file");
                }
                if (!"SECTION".equals(line.trim())) {
                    throw new ValidationException("Expected \"SECTION\" in DXF file but found: " +
                            line.substring(0, Math.min(line.length(), 40)));
                }
                line = reader.readLine();
                if (line == null) {
                    throw new ValidationException("Unexpected end of DXF file");
                }
                if (!"2".equals(line.trim())) {
                    throw new ValidationException("Expected groupcode \"2\" after SECTION entry but found: " +
                            line.substring(0, Math.min(line.length(), 40)));
                }
                line = reader.readLine();
                if (line == null) {
                    throw new ValidationException("Unexpected end of DXF file");
                }
                if ("HEADER".equals(line.trim())) {
                    return;
                }
            }
        }
        throw new ValidationException("No header encountered before end of DXF file");
    }

    private static String readHeaderVarName(BufferedReader reader) throws ValidationException, IOException {
        final String groupCode = reader.readLine();
        if (groupCode == null) {
            throw new ValidationException("Unexpected end of DXF file");
        }
        if ("0".equals(groupCode.trim())) {
            final String endsec = reader.readLine();
            if (endsec == null) {
                throw new ValidationException("Unexpected end of DXF file");
            }
            if (! "ENDSEC".equals(endsec.trim())) {
                throw new IOException("Expected \"ENDSEC\" at end of DXF header but got: " +
                        endsec.substring(0, Math.min(endsec.length(), 40)));
            }
            return null;
        }

        if (! "9".equals(groupCode.trim())) {
            throw new ValidationException("Unexpected group code in DXF header: " +
                    groupCode.substring(0, Math.min(groupCode.length(), 40)));
        }

        String varName = reader.readLine();
        if (varName == null) {
            throw new ValidationException("Unexpected end of DXF file");
        }
        varName = varName.trim();
        if (! varName.startsWith("$")) {
            throw new ValidationException("DXF header variable should begin with $: " +
                    varName.substring(0, Math.min(varName.length(), 40)));
        }
        return varName.substring(1);
    }

    private static List<String> readHeaderVarValues(BufferedReader reader) throws ValidationException, IOException {
        final List<String> values = new ArrayList<>();
        reader.mark(GROUPCODE_READAHEAD_LIMIT);
        String groupCode = reader.readLine();
        if (groupCode == null) {
            throw new ValidationException("Unexpected end of DXF file");
        }
        groupCode = groupCode.trim();

        while(!"9".equals(groupCode) && !"0".equals(groupCode)) {
            final String value = reader.readLine();
            if (value == null) {
                throw new ValidationException("Unexpected end of DXF file");
            }
            values.add(value.trim());
            reader.mark(GROUPCODE_READAHEAD_LIMIT);
            groupCode = reader.readLine();
            if (groupCode == null) {
                throw new ValidationException("Unexpected end of DXF file");
            }
            groupCode = groupCode.trim();
        }

        reader.reset(); //Put the stream back to before I read the last groupCode (0 or 9)
        if (values.isEmpty()) {
            throw new ValidationException("Got DXF header with no values");
        }
        return values;
    }

    private Map<String, List<String>> readHeader(InputStream in) throws IOException, ValidationException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        final Map<String, List<String>> headerValues = new HashMap<>();

        seekToHeaderStart(reader);

        String varName;
        while((varName = readHeaderVarName(reader)) != null) {
            headerValues.put(varName, readHeaderVarValues(reader));
        }

        return headerValues;
    }

    private static final float SECONDS_PER_DAY = 60 * 60 * 24;

    private static String translateDxfDate(String dxfDate) {
        final String[] split = dxfDate.split("\\.");
        final int days = Integer.parseInt(split[0]);
        final float fraction = Float.parseFloat("0." + split[1]);
        Calendar c = new GregorianCalendar(-4712, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, days);
        c.add(Calendar.SECOND, Math.round(SECONDS_PER_DAY * fraction));
        DateFormat df = new SimpleDateFormat(CadTool.PREFERRED_DATE_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(c.getTime());
    }

    @Override
    public CadToolResult run(DataSource ds, String filename) throws IOException, ValidationException {
        final CadToolResult result = new CadToolResult(name, filename);
        result.mimetype = DxfExtractor.DEFAULT_MIMETYPE;
        result.formatName = DxfExtractor.DEFAULT_FORMAT_NAME;

        final Map<String, List<String>> entries = readHeader(ds.getInputStream());

        final List<String> versionValues = entries.remove("ACADVER");
        if (versionValues != null && versionValues.size() == 1) {
            final String versionString = versionValues.get(0);
            result.formatVersion = DwgExtractor.versionSuffixLookup(versionString.substring(versionString.length() - 2));
        }

        List<String> createValues = entries.remove("TDUCREATE");
        if (createValues != null && createValues.size() == 1) {
            result.modificationDate = translateDxfDate(createValues.get(0));
            entries.remove("TDCREATE");
        } else {
            createValues = entries.remove("TDCREATE");
            if (createValues != null && createValues.size() == 1) {
                result.modificationDate = translateDxfDate(createValues.get(0));
            }
        }

        List<String> updateValues = entries.remove("TDUUPDATE");
        if (updateValues != null && updateValues.size() == 1) {
            result.modificationDate = translateDxfDate(updateValues.get(0));
            entries.remove("TDUPDATE");
        } else {
            updateValues = entries.remove("TDUPDATE");
            if (updateValues != null && updateValues.size() == 1) {
                result.modificationDate = translateDxfDate(updateValues.get(0));
            }
        }

        List<String> minExtentValues = entries.remove("EXTMIN");
        List<String> maxExtentValues = entries.remove("EXTMAX");
        if (maxExtentValues != null && minExtentValues != null && maxExtentValues.size() >= 2 && maxExtentValues.size() == minExtentValues.size()) {
            final Element extent = new Element("extent");
            elementloop: for (int i = 0; i < minExtentValues.size(); i++) {
                final double minVal = Double.parseDouble(minExtentValues.get(i));
                final double maxVal = Double.parseDouble(maxExtentValues.get(i));
                final double mag = Math.abs(maxVal - minVal);
                final Element dimension = new Element("dimension");
                dimension.setAttribute("magnitude", Double.toString(mag));
                switch(i) {
                    case 0:
                        dimension.setAttribute("axis", "x");
                        break;
                    case 1:
                        dimension.setAttribute("axis", "y");
                        break;
                    case 2:
                        dimension.setAttribute("axis", "z");
                        if (mag < 0.000000000001d) {  //Check against some very small value in case of double precision errors
                            break elementloop;
                        }
                        break;
                }
                extent.addContent(dimension);
            }
            result.addElement(extent);
        }

        final List<String> unitsValues = entries.remove("INSUNITS");
        if (unitsValues != null && unitsValues.size() == 1) {
            final String code = unitsValues.get(0);
            final String units;
            switch(code) {
                case "1": units = "Inches"; break;
                case "2": units = "Feet"; break;
                case "3": units = "Miles"; break;
                case "4": units = "Millimeters"; break;
                case "5": units = "Centimeters"; break;
                case "6": units = "Meters"; break;
                case "7": units = "Kilometers"; break;
                case "8": units = "Microinches"; break;
                case "9": units = "Mils"; break;
                case "10": units = "Yards"; break;
                case "11": units = "Angstroms"; break;
                case "12": units = "Nanometers"; break;
                case "13": units = "Microns"; break;
                case "14": units = "Decimeters"; break;
                case "15": units = "Decameters"; break;
                case "16": units = "Hectometers"; break;
                case "17": units = "Gigameters"; break;
                case "18": units = "Astronomical Units"; break;
                case "19": units = "Light Years"; break;
                case "20": units = "Parsecs"; break;
                default: units = null;
            }
            result.addKeyValue("default-units", units);
        }

        final List<String> measurementValues = entries.remove("MEASUREMENT");
        if (measurementValues != null && measurementValues.size() == 1) {
            final String code = measurementValues.get(0);
            switch(code) {
                case "0": result.addKeyValue("measurement-system", "Imperial"); break;
                case "1": result.addKeyValue("measurement-system", "Metric"); break;
            }
        }

        final List<String> uniqueIdValues = entries.remove("FINGERPRINTGUID");
        if (uniqueIdValues != null && uniqueIdValues.size() == 1) {
            final String id = uniqueIdValues.get(0);
            result.uniqueId = id;
        }

        final List<String> uniqueVersionIdValues = entries.remove("VERSIONGUID");
        if (uniqueVersionIdValues!= null && uniqueVersionIdValues.size() == 1) {
            final String versionId = uniqueVersionIdValues.get(0);
            result.addKeyValue("unique-version-id", versionId);
        }

        //For more values, see http://www.autodesk.com/techpubs/autocad/acad2000/dxf/header_section_group_codes_dxf_02.htm

////        And all the rest of the vaules
//        for (Map.Entry<String, List<String>> entry: entries.entrySet()) {
//            for (String value: entry.getValue()) {
//                result.addKeyValue(entry.getKey(), value);
//            }
//        }
        return result;
    }
}

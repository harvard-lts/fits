//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

/**
 *
 */
package edu.harvard.hul.ois.fits.tools.tika;

import com.google.gson.Gson;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

/**
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
 */
public final class MetadataFormatter {
    private static final MetadataFormatter INSTANCE = new MetadataFormatter();

    private MetadataFormatter() {}

    private static final NumberFormat FORMATTER = NumberFormat.getInstance();
    private static final Gson GSON = new Gson();

    public static MetadataFormatter getInstance() {
        return INSTANCE;
    }

    public String toJSON(Metadata metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        boolean first = true;
        for (String name : metadata.names()) {
            if (!first) {
                sb.append(",\n");
            } else {
                first = false;
            }
            sb.append(GSON.toJson(name));
            sb.append(":");
            sb.append(JSONValues(metadata.getValues(name)));
        }
        sb.append(" }");
        return sb.toString();
    }

    public static String toXML(Metadata metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append("<metadata>");
        String[] names = metadata.names();
        for (String name : names) {
            sb.append("<field name=\"");
            sb.append(StringEscapeUtils.escapeXml(name));
            sb.append("\">");
            sb.append(XMLValues(metadata.getValues(name)));
            sb.append("</field>");
        }
        sb.append("</metadata>");
        return sb.toString();
    }

    private static String JSONValues(String[] values) {
        StringBuilder sb = new StringBuilder();
        if (values.length > 1) {
            sb.append("[");
        }
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(GSON.toJson(formatValue(value)));
        }
        if (values.length > 1) {
            sb.append("]");
        }
        return sb.toString();
    }

    private static String XMLValues(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            sb.append("<value>");
            sb.append(formatValue(value));
            sb.append("</value>");
        }
        return sb.toString();
    }

    private static String formatValue(String value) {
        //		String retVal = "null";
        if (value != null && value.length() > 0) {
            // Is it a number?
            ParsePosition pos = new ParsePosition(0);
            FORMATTER.parse(value, pos);
            if (value.length() == pos.getIndex()) {
                // It's a number. Remove leading zeros and output
                value = value.replaceFirst("^0+(\\d)", "$1");
            }
            value = StringEscapeUtils.escapeXml(value);
        }
        return value;
    }

    public static String toXML(MediaType mediaType) {
        StringBuilder sb = new StringBuilder();
        sb.append("<mediaType>");
        sb.append("<type>");
        sb.append(mediaType.getType());
        sb.append("</type>");
        sb.append("<subType>");
        sb.append(mediaType.getSubtype());
        sb.append("</subType>");
        if (mediaType.hasParameters()) sb.append(XMLParameters(mediaType.getParameters()));
        sb.append("</mediaType>");
        return sb.toString();
    }

    public static String XMLParameters(Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("<parameters>");
        for (String name : parameters.keySet()) {
            sb.append("<parameter name=\"");
            sb.append(name);
            sb.append("\" value=\"");
            sb.append(parameters.get(name));
            sb.append("\"/>");
        }
        sb.append("</parameters>");
        return sb.toString();
    }

    public static String toJSON(MediaType mediaType) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":");
        sb.append(GSON.toJson(mediaType.getType()));
        sb.append(", \"subtype\":");
        sb.append(GSON.toJson(mediaType.getSubtype()));
        if (mediaType.hasParameters()) sb.append(JSONParameters(mediaType.getParameters()));
        sb.append("}");
        return sb.toString();
    }

    public static String JSONParameters(Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(", \"parameters\":{");
        boolean first = true;
        for (String name : parameters.keySet()) {
            if (!first) {
                sb.append(",\n");
            } else {
                first = false;
            }
            sb.append("\"");
            sb.append(name);
            sb.append("\":");
            sb.append(GSON.toJson(parameters.get(name)));
        }
        sb.append("}");
        return sb.toString();
    }
}

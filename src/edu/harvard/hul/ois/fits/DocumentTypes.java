/* 
 * 
 * This file is part of FITS (File Information Tool Set).
 * 
 * FITS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FITS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FITS.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.hul.ois.fits;

import java.util.HashMap;
import java.util.Map;

/** A class with static methods for managing document types. */
public class DocumentTypes {

    /** Enumeration of FITS document types. 
     * TODO this should be generic to FITS.
     */
    public enum Doctype {
        UNKNOWN,
        AUDIO,
        DOCUMENT,
        IMAGE,
        TEXT,
        VIDEO
    };
    
    /** Map of MIME types to FITS doctypes.
     *  exiftool_xslt_map.xml is a good reference to use. */
    private final static Map<String, Doctype> doctypeMap =
            new HashMap<String, Doctype> ();
    static {
        doctypeMap.put ("audio/basic", Doctype.AUDIO);
        doctypeMap.put ("audio/mid", Doctype.AUDIO);
        doctypeMap.put ("audio/mpeg", Doctype.AUDIO);
        doctypeMap.put ("audio/ogg", Doctype.AUDIO);
        doctypeMap.put ("audio/x-flac", Doctype.AUDIO);
        doctypeMap.put ("audio/x-pn-realaudio", Doctype.AUDIO);
        doctypeMap.put ("audio/wav", Doctype.AUDIO);
        doctypeMap.put ("audio/x-wave", Doctype.AUDIO);
        doctypeMap.put ("audio/x-mid", Doctype.AUDIO);
        doctypeMap.put ("audio/x-midi", Doctype.AUDIO);
        doctypeMap.put ("audio/x-wave", Doctype.AUDIO);
        doctypeMap.put ("audio/x-aiff", Doctype.AUDIO);

        doctypeMap.put ("application/epub+zip", Doctype.DOCUMENT);
        doctypeMap.put ("application/pdf", Doctype.DOCUMENT);
        doctypeMap.put ("application/rtf", Doctype.DOCUMENT);
        doctypeMap.put ("application/msword", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.ms-excel", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.ms-powerpoint", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.ms-works", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.openxmlformats-officedocument.wordprocessingml.document", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.openxmlformats-officedocument.wordprocessingml.template", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.openxmlformats-officedocument.spreadsheetml.template", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.openxmlformats-officedocument.presentationml.presentation", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.ms-word.document.macroEnabled.12", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.ms-powerpoint.presentation.macroEnabled.12", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.ms-powerpoint.template.macroEnabled.12", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.ms-powerpoint.slideshow.macroEnabled.12", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.oasis.opendocument.chart", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.oasis.opendocument.database", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.oasis.opendocument.formula", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.oasis.opendocument.presentation", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.oasis.opendocument.text", Doctype.DOCUMENT);   
        doctypeMap.put ("application/vnd.oasis.opendocument.spreadsheet", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.sun.xml.math", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.sun.xml.writer", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.stardivision.chart", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.stardivision.impress", Doctype.DOCUMENT);
        doctypeMap.put ("application/vnd.stardivision.writer", Doctype.DOCUMENT);
        doctypeMap.put ("application/wordperfect", Doctype.DOCUMENT);
        doctypeMap.put ("application/x-troff", Doctype.DOCUMENT);

        doctypeMap.put ("application/postscript", Doctype.IMAGE);
        doctypeMap.put ("image/bmp", Doctype.IMAGE);
        doctypeMap.put ("image/vnd.adobe.photoshop", Doctype.IMAGE);
        doctypeMap.put ("application/vnd.oasis.opendocument.graphics", Doctype.IMAGE);
        doctypeMap.put ("application/vnd.oasis.opendocument.image", Doctype.IMAGE);
        doctypeMap.put ("image/gif", Doctype.IMAGE);
        doctypeMap.put ("image/jp2", Doctype.IMAGE);
        doctypeMap.put ("image/jpx", Doctype.IMAGE);
        doctypeMap.put ("image/jpm", Doctype.IMAGE);
        doctypeMap.put ("image/jpeg", Doctype.IMAGE);
        doctypeMap.put ("image/png", Doctype.IMAGE);
        doctypeMap.put ("image/tiff", Doctype.IMAGE);
        doctypeMap.put ("image/x-icon", Doctype.IMAGE);

        doctypeMap.put ("text/css", Doctype.TEXT);
        doctypeMap.put ("text/html", Doctype.TEXT);
        doctypeMap.put ("text/plain", Doctype.TEXT);
        doctypeMap.put ("text/xml", Doctype.TEXT);
        
        doctypeMap.put ("application/mp4", Doctype.VIDEO);
        doctypeMap.put ("video/mj2", Doctype.VIDEO);
        doctypeMap.put ("video/mpeg", Doctype.VIDEO);
        doctypeMap.put ("video/quicktime", Doctype.VIDEO);
        doctypeMap.put ("video/x-m4v", Doctype.VIDEO);
        doctypeMap.put ("video/x-ms-asf", Doctype.VIDEO);
        doctypeMap.put ("video/x-msvideo", Doctype.VIDEO);

    }
    
    /* Select a document type based on the MIME type.  */
    public static Doctype mimeToDoctype (String mime) {
        // trim down to base type
        int semi = mime.indexOf (";");
        if (semi >= 0) {
            mime = mime.substring (0, semi);
        }
        Doctype dt = doctypeMap.get(mime);
        if (dt == null) {
            return Doctype.UNKNOWN;
        }
        else return dt;
    }

}

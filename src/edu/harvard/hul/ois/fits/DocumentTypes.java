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

    
    /** Map of MIME types to FITS doctypes */
    private final static Map<String, Doctype> doctypeMap =
            new HashMap<String, Doctype> ();
    static {
        doctypeMap.put ("audio/x-wave", Doctype.AUDIO);
        doctypeMap.put ("audio/x-aiff", Doctype.AUDIO);
        doctypeMap.put ("audio/ogg", Doctype.AUDIO);
        doctypeMap.put ("audio/x-flac", Doctype.AUDIO);

        doctypeMap.put ("application/pdf", Doctype.DOCUMENT);

        doctypeMap.put ("image/bmp", Doctype.IMAGE);
        doctypeMap.put ("application/photoshop", Doctype.IMAGE);
        doctypeMap.put ("image/jpeg", Doctype.IMAGE);
        doctypeMap.put ("image/png", Doctype.IMAGE);
        doctypeMap.put ("image/tiff", Doctype.IMAGE);

        doctypeMap.put ("text/plain", Doctype.TEXT);
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

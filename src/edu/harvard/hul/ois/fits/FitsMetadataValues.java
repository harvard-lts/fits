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

/** This class holds standard element names for FITS metadata output, as well
 *  as some standard values. All components are static.
 */
public class FitsMetadataValues {

    /** Standard document element names. */
    public final static String AUDIO = "audio";
    public final static String DOCUMENT = "document";
    public final static String IMAGE = "image";
    public final static String TEXT = "text";
    public final static String VIDEO = "video";
    
    /** Standard property element names. */
    public final static String AUDIO_ENCODING = "audioEncoding";
    public final static String AUTHOR = "author";
    public final static String BIT_DEPTH = "bitDepth";
    public final static String BIT_RATE = "bitRate";
    public final static String BLOCK_SIZE_MAX = "blockSizeMax";
    public final static String BLOCK_SIZE_MIN = "blockSizeMin";
    public final static String CHANNELS = "channels";
    public final static String CHARSET = "charset";
    public final static String COMPRESSION_SCHEME = "compressionScheme";
    public final static String BITS_PER_SAMPLE = "bitsPerSample";
    public final static String DURATION = "duration";
    public final static String IMAGE_HEIGHT = "imageHeight";
    public final static String IMAGE_WIDTH = "imageWidth";
    public final static String MARKUP_BASIS = "markupBasis";
    public final static String ORIENTATION = "orientation";
    public final static String PAGE_COUNT = "pageCount";
    public final static String SAMPLE_RATE = "sampleRate";
    public final static String SUBJECT = "subject";
    public final static String TITLE = "title";
    public final static String WORD_COUNT = "wordCount";
    
    /** Mapping from MIME types to standard names. This is an incomplete map,
     *  holding only cases where conversion is necessary. Expand as needed.
     */
    public final static Map<String, String> mimeToDescMap =
            new HashMap<String, String>();
    static {
        mimeToDescMap.put ("image/jpeg", "JPEG File Interchange Format");
    }
}

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
    
    /** Standard property element names. The idea is to include them
     *  all here, even the ones that are generated only by
     *  XSLT. */
    public final static String AUDIO_BITS_PER_SAMPLE = "audioBitsPerSample";  // 11-Apr-2013
    public final static String AUDIO_CHANNEL_TYPE = "audioChannelType";  // 11-Apr-2013
    public final static String AUDIO_COMPRESSOR = "audioCompressor";   // 11-Apr-2013
    public final static String AUDIO_ENCODING = "audioEncoding";
    public final static String AUDIO_SAMPLE_RATE = "audioSampleRate";  // 11-Apr-2013
    public final static String AUDIO_SAMPLE_TYPE = "audioSampleType";  // 12-Apr-2013
    public final static String AUTHOR = "author";
    public final static String BIT_DEPTH = "bitDepth";
    public final static String BIT_RATE = "bitRate";
    public final static String BLOCK_SIZE_MAX = "blockSizeMax";
    public final static String BLOCK_SIZE_MIN = "blockSizeMin";
    public final static String CHANNELS = "channels";
    public final static String CHARSET = "charset";
    public final static String COLOR_SPACE = "colorSpace";
    public final static String COMPRESSION_SCHEME = "compressionScheme";
    public final static String BITS_PER_SAMPLE = "bitsPerSample";
    public final static String DATA_FORMAT_TYPE = "dataFormatType";
    public final static String DURATION = "duration";
    public final static String FRAME_RATE = "frameRate";
    public final static String IMAGE_HEIGHT = "imageHeight";
    public final static String IMAGE_WIDTH = "imageWidth";
    public final static String MARKUP_BASIS = "markupBasis";
    public final static String ORIENTATION = "orientation";
    public final static String PIXEL_ASPECT_RATIO = "pixelAspectRatio";  // 12-Apr-2013
    public final static String ROTATION = "rotation";  // 11-Apr-2013
    public final static String PAGE_COUNT = "pageCount";
    public final static String SAMPLE_RATE = "sampleRate";
    public final static String SAMPLES_PER_PIXEL = "samplesPerPixel";
    public final static String SAMPLING_FREQUENCY_UNIT = "samplingFrequencyUnit";
    public final static String SUBJECT = "subject";
    public final static String TIME_SCALE = "timeScale"; // 11-Apr-2013
    public final static String TITLE = "title";
    public final static String VIDEO_COMPRESSOR = "videoCompressor";
    public final static String X_SAMPLING_FREQUENCY = "xSamplingFrequency";
    public final static String Y_SAMPLING_FREQUENCY = "YSamplingFrequency";
    public final static String WORD_COUNT = "wordCount";
    
    /** Standard compression values. */
    public final static String CMPR_NONE = "Uncompressed";
    public final static String CMPR_JP2 = "JPEG 2000";
    public final static String CMPR_JP2_LOSSY = "JPEG 2000 Lossy";
    public final static String CMPR_JP2_LOSSLESS = "JPEG 2000 Lossless";
    public final static String CMPR_CCITT1D = "CCITT 1D";
    public final static String CMPR_T4G3FAX = "T4/Group 3 Fax";
    public final static String CMPR_T6G4FAX = "T6/Group 4 Fax";
    public final static String CMPR_LZW = "LZW";
    public final static String CMPR_JPEG_OLD = "JPEG (old-style)";
    public final static String CMPR_JPEG = "JPEG";
    public final static String CMPR_ADOBE_DEFLATE = "Adobe Deflate";
    public final static String CMPR_JBIG = "JBIG";
    public final static String CMPR_CCITTRLEW = "CCITTRLEW";
    public final static String CMPR_PACKBITS = "PackBits";
    public final static String CMPR_NEXT = "NeXT";
    public final static String CMPR_THUNDERSCAN = "ThunderScan";
    public final static String CMPR_IT8CTPAD = "IT8CTPAD";
    public final static String CMPR_IT8LW = "IT8LW";
    public final static String CMPR_IT8MP = "IT8MP";
    public final static String CMPR_IT8BL = "IT8BL";
    public final static String CMPR_PIXAR_FILM = "PixarFilm";
    public final static String CMPR_PIXAR_LOG = "PixarLog";
    public final static String CMPR_DEFLATE = "Deflate";
    public final static String CMPR_DCS = "DCS";
    public final static String CMPR_SGILOG = "SGILog";
    public final static String CMPR_SGILOG24 = "SGILog24";
    
    /** Mapping from MIME types to standard names. This is an incomplete map,
     *  holding only cases where conversion is necessary. Expand as needed.
     */
    public final static Map<String, String> mimeToDescMap =
            new HashMap<String, String>();
    static {
        mimeToDescMap.put ("image/jpeg", "JPEG File Interchange Format");
    }
}

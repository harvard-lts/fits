//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class holds standard element names for FITS metadata output, as well
 *  as some standard values. All components are static.
 */
public class FitsMetadataValues {

    private static FitsMetadataValues instance;

    private static final Logger logger = LoggerFactory.getLogger(FitsMetadataValues.class);

    private String mimeMapProperties = Fits.FITS_XML_DIR + "mime_map.txt";
    private String formatMapProperties = Fits.FITS_XML_DIR + "format_map.txt";
    private String mimeToFormatMapProperties = Fits.FITS_XML_DIR + "mime_to_format_map.txt";

    private HashMap<String, String> mimeMap = new HashMap<String, String>();
    private HashMap<String, String> formatMap = new HashMap<String, String>();
    private HashMap<String, String> mimeToFormatMap = new HashMap<String, String>();

    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    public static final String DEFAULT_FORMAT = "Unknown Binary";

    /** Standard document element names. */
    public static final String AUDIO = "audio";

    public static final String DOCUMENT = "document";
    public static final String IMAGE = "image";
    public static final String TEXT = "text";
    public static final String VIDEO = "video";

    /** Standard property element names. The idea is to include them
     *  all here, even the ones that are generated only by
     *  XSLT. */
    public static final String APERTURE_VALUE = "apertureValue";

    public static final String AUDIO_BITS_PER_SAMPLE = "audioBitsPerSample"; // 11-Apr-2013
    public static final String AUDIO_CHANNEL_TYPE = "audioChannelType"; // 11-Apr-2013
    public static final String AUDIO_COMPRESSOR = "audioCompressor"; // 11-Apr-2013
    public static final String AUDIO_DATA_ENCODING = "audioDataEncoding";
    // public final static String AUDIO_ENCODING = "audioEncoding";
    public static final String AUDIO_SAMPLE_RATE = "audioSampleRate"; // 11-Apr-2013
    public static final String AUDIO_SAMPLE_TYPE = "audioSampleType"; // 12-Apr-2013
    public static final String AUTHOR = "author";
    public static final String AVG_BIT_RATE = "avgBitRate"; // audio
    public static final String AVG_PACKET_SIZE = "avgPacketSize";
    public static final String BIT_DEPTH = "bitDepth"; // video, audio
    public static final String BIT_RATE = "bitRate"; // video, audio
    public static final String BITS_PER_SAMPLE = "bitsPerSample"; // image
    public static final String BLOCK_SIZE_MAX = "blockSizeMax";
    public static final String BLOCK_SIZE_MIN = "blockSizeMin";
    public static final String BRIGHTNESS_VALUE = "brightnessValue";
    public static final String BYTE_ORDER = "byteOrder"; // for possible future use
    public static final String CATEGORY = "category";
    public static final String CFA_PATTERN = "cfaPattern"; // image
    public static final String CFA_PATTERN2 = "cfaPattern2"; // image
    public static final String CHANNELS = "channels"; // audio, maybe video
    public static final String CHARACTER_COUNT = "characterCount";
    public static final String CHARSET = "charset"; // text
    public static final String COLOR_SPACE = "colorSpace";
    public static final String COMPANY = "company";
    public static final String COMPRESSION_SCHEME = "compressionScheme";
    public static final String COPYRIGHT_NOTE = "copyrightNote";
    public static final String CREATED = "created";
    public static final String CREATING_APPLICATION_NAME = "creatingApplicationName";
    public static final String CREATING_APPLICATION_VERSION = "creatingApplicationVersion";
    public static final String DATA_FORMAT_TYPE = "dataFormatType";
    public static final String DESCRIPTION = "description";
    public static final String DIGITAL_CAMERA_MANUFACTURER = "digitalCameraManufacturer";
    public static final String DIGITAL_CAMERA_MODEL_NAME = "digitalCameraModelName";
    public static final String DIGITAL_CAMERA_SERIAL_NO = "digitalCameraSerialNo";
    public static final String DURATION = "duration";
    public static final String EXIF_VERSION = "exifVersion";
    public static final String HAS_EMBEDDED_RESOURCES = "hasEmbeddedResources";
    public static final String EXPOSURE_BIAS_VALUE = "exposureBiasValue";
    public static final String EXPOSURE_INDEX = "exposureIndex";
    public static final String EXPOSURE_PROGRAM = "exposureProgram";
    public static final String EXPOSURE_TIME = "exposureTime";
    public static final String FLASH = "flash";
    public static final String FLASH_ENERGY = "flashEnergy";
    public static final String FNUMBER = "fNumber";
    public static final String FOCAL_LENGTH = "focalLength";
    public static final String FRAME_RATE = "frameRate";
    public static final String GRAY_RESPONSE_UNIT = "grayResponseUnit";
    public static final String ICC_PROFILE_NAME = "iccProfileName";
    public static final String ICC_PROFILE_VERSION = "iccProfileVersion";
    public static final String IDENTIFIER = "identifier";
    public static final String IMAGE_COUNT = "graphicsCount";
    public static final String IMAGE_HEIGHT = "imageHeight";
    public static final String IMAGE_WIDTH = "imageWidth";
    public static final String IMAGE_PRODUCER = "imageProducer";
    public static final String ISO_SPEED_RATING = "isoSpeedRating";
    public static final String LANGUAGE = "language"; // may be useful someday
    public static final String LAST_MODIFIED = "lastmodified";
    public static final String LIGHT_SOURCE = "lightSource";
    public static final String LINE_COUNT = "lineCount"; // document
    public static final String MARKUP_BASIS = "markupBasis";
    public static final String MARKUP_BASIS_VERSION = "markupBasisVersion";
    public static final String MARKUP_LANGUAGE = "markupLanguage";
    public static final String MAX_APERTURE_VALUE = "maxApertureValue";
    public static final String MAX_BIT_RATE = "maxBitRate"; // audio
    public static final String MAX_PACKET_SIZE = "maxPacketSize";
    public static final String METERING_MODE = "meteringMode"; // image
    public static final String NUM_SAMPLES = "numSamples"; // audio
    public static final String OFFSET = "offset";
    public static final String ORIENTATION = "orientation"; // image, video
    public static final String PUBLISHER = "publisher";
    public static final String PIXEL_ASPECT_RATIO = "pixelAspectRatio"; // video
    public static final String ROTATION = "rotation"; // image, video
    public static final String PAGE_COUNT = "pageCount"; // document
    public static final String PARAGRAPH_COUNT = "paragraphCount"; // document
    public static final String IS_RIGHTS_MANAGED = "isRightsManaged";
    public static final String IS_PROTECTED = "isProtected";
    public static final String PRIMARY_CHROMATICITIES_BLUE_X = "primaryChromaticitiesBlueX";
    public static final String PRIMARY_CHROMATICITIES_BLUE_Y = "primaryChromaticitiesBlueY";
    public static final String PRIMARY_CHROMATICITIES_GREEN_X = "primaryChromaticitiesGreenX";
    public static final String PRIMARY_CHROMATICITIES_GREEN_Y = "primaryChromaticitiesGreenY";
    public static final String PRIMARY_CHROMATICITIES_RED_X = "primaryChromaticitiesRedX";
    public static final String PRIMARY_CHROMATICITIES_RED_Y = "primaryChromaticitiesRedY";
    public static final String QUALITY_LAYERS = "qualityLayers"; // JP2
    public static final String REFERENCE_BLACK_WHITE = "referenceBlackWhite";
    public static final String RESOLUTION_LEVELS = "resolutionLevels"; // JP2
    public static final String SAMPLE_RATE = "sampleRate"; // audio
    public static final String SAMPLES_PER_PIXEL = "samplesPerPixel";
    public static final String SAMPLING_FREQUENCY_UNIT = "samplingFrequencyUnit";
    public static final String SCANNER_MANUFACTURER = "scannerManufacturer";
    public static final String SCANNER_MODEL_NAME = "scannerModelName";
    public static final String SCANNER_MODEL_SERIAL_NO = "scannerModelSerialNo";
    public static final String SCANNING_SOFTWARE_NAME = "scanningSoftwareName";
    public static final String SENSING_METHOD = "sensingMethod";
    public static final String SHUTTER_SPEED_VALUE = "shutterSpeedValue";
    public static final String SIZE = "size";
    public static final String SPECTRAL_SENSITIVITY = "spectralSensitivity";
    public static final String SOFTWARE = "software";
    public static final String SUBJECT = "subject";
    public static final String TABLE_COUNT = "tableCount";
    public static final String TIMECODE = "timecode"; // SMPTE timecode, for future use
    public static final String TILE_HEIGHT = "tileHeight"; // image, esp. TIFF
    public static final String TILE_WIDTH = "tileWidth";
    public static final String TIME_SCALE = "timeScale"; // 11-Apr-2013
    public static final String TITLE = "title";
    public static final String VIDEO_COMPRESSOR = "videoCompressor";
    public static final String WORD_COUNT = "wordCount";
    public static final String X_SAMPLING_FREQUENCY = "xSamplingFrequency";
    public static final String Y_SAMPLING_FREQUENCY = "ySamplingFrequency";
    public static final String YCBCR_COEFFICIENTS = "YCbCrCoefficients";
    public static final String YCBCR_POSITIONING = "YCbCrPositioning";
    public static final String YCBCR_SUBSAMPLING = "YCbCrSubSampling";

    /** Standard compression values. */
    public static final String CMPR_NONE = "Uncompressed";

    public static final String CMPR_JP2 = "JPEG 2000";
    public static final String CMPR_JP2_LOSSY = "JPEG 2000 Lossy";
    public static final String CMPR_JP2_LOSSLESS = "JPEG 2000 Lossless";
    public static final String CMPR_CCITT1D = "CCITT 1D";
    public static final String CMPR_T4G3FAX = "T4/Group 3 Fax";
    public static final String CMPR_T6G4FAX = "T6/Group 4 Fax";
    public static final String CMPR_LZW = "LZW";
    public static final String CMPR_JPEG_OLD = "JPEG (old-style)";
    public static final String CMPR_JPEG = "JPEG";
    public static final String CMPR_ADOBE_DEFLATE = "Adobe Deflate";
    public static final String CMPR_JBIG = "JBIG";
    public static final String CMPR_CCITTRLEW = "CCITTRLEW";
    public static final String CMPR_PACKBITS = "PackBits";
    public static final String CMPR_NEXT = "NeXT";
    public static final String CMPR_THUNDERSCAN = "ThunderScan";
    public static final String CMPR_IT8CTPAD = "IT8CTPAD";
    public static final String CMPR_IT8LW = "IT8LW";
    public static final String CMPR_IT8MP = "IT8MP";
    public static final String CMPR_IT8BL = "IT8BL";
    public static final String CMPR_PIXAR_FILM = "PixarFilm";
    public static final String CMPR_PIXAR_LOG = "PixarLog";
    public static final String CMPR_DEFLATE = "Deflate";
    public static final String CMPR_DCS = "DCS";
    public static final String CMPR_SGILOG = "SGILog";
    public static final String CMPR_SGILOG24 = "SGILog24";

    private FitsMetadataValues() {

        mimeMap = parseFile(mimeMapProperties);
        formatMap = parseFile(formatMapProperties);
        mimeToFormatMap = parseFile(mimeToFormatMapProperties);
    }

    public static synchronized FitsMetadataValues getInstance() {
        if (instance == null) instance = new FitsMetadataValues();

        return instance;
    }

    /** Do some normalization on variant MIME types. */
    public String normalizeMimeType(String mime) {
        if (mime == null || mime.length() == 0) {
            return DEFAULT_MIMETYPE;
        }

        // Tika is the primary tool that sets the charset in the MIME and it throws off comparisons
        if (mime.contains("; charset=")) {
            mime = mime.replaceFirst("; charset=[^;]+", "");
        }

        String normMime = mimeMap.get(mime);
        if (normMime != null) {
            return normMime;
        } else {
            return mime;
        }
    }

    public String normalizeFormat(String format) {
        if (format == null || format.length() == 0) {
            return DEFAULT_FORMAT;
        }
        String normformat = formatMap.get(format);
        if (normformat != null) {
            return normformat;
        } else {
            return format;
        }
    }

    public String getFormatForMime(String mime) {
        if (mime == null || mime.length() == 0) {
            return DEFAULT_FORMAT;
        }
        return mimeToFormatMap.get(mime);
    }

    private HashMap<String, String> parseFile(String inputFile) {
        HashMap<String, String> map = new HashMap<String, String>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = in.readLine()) != null) {
                if (!line.startsWith("#") && !line.startsWith("\"#")) {
                    String[] parts = line.split("=");
                    if (parts.length != 2) {
                        logger.debug("Invalid map entry: " + line);
                        continue;
                    }
                    map.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading or parsing input file: " + inputFile, e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                } // nothing to do if exception when closing file
        }
        return map;
    }
}

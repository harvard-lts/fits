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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;


/** This class holds standard element names for FITS metadata output, as well
 *  as some standard values. All components are static.
 */
public class FitsMetadataValues {
	
	private static FitsMetadataValues instance;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private String mimeMapProperties = Fits.FITS_XML + "mime_map.txt";
	private String formatMapProperties = Fits.FITS_XML + "format_map.txt";
	private String mimeToFormatMapProperties = Fits.FITS_XML + "mime_to_format_map.txt";
	
    private HashMap<String, String> mimeMap = new HashMap<String, String>();
    private HashMap<String, String> formatMap = new HashMap<String, String>();
    private HashMap<String, String> mimeToFormatMap = new HashMap<String, String>();
    
	public final static String DEFAULT_MIMETYPE="application/octet-stream";
	public final static String DEFAULT_FORMAT="Unknown Binary";

    /** Standard document element names. */
    public final static String AUDIO = "audio";
    public final static String DOCUMENT = "document";
    public final static String IMAGE = "image";
    public final static String TEXT = "text";
    public final static String VIDEO = "video";
    
    /** Standard property element names. The idea is to include them
     *  all here, even the ones that are generated only by
     *  XSLT. */
    public final static String APERTURE_VALUE = "apertureValue";
    public final static String AUDIO_BITS_PER_SAMPLE = "audioBitsPerSample";  // 11-Apr-2013
    public final static String AUDIO_CHANNEL_TYPE = "audioChannelType";  // 11-Apr-2013
    public final static String AUDIO_COMPRESSOR = "audioCompressor";   // 11-Apr-2013
    public final static String AUDIO_DATA_ENCODING = "audioDataEncoding";
    //public final static String AUDIO_ENCODING = "audioEncoding";
    public final static String AUDIO_SAMPLE_RATE = "audioSampleRate";  // 11-Apr-2013
    public final static String AUDIO_SAMPLE_TYPE = "audioSampleType";  // 12-Apr-2013
    public final static String AUTHOR = "author";
    public final static String AVG_BIT_RATE = "avgBitRate";       // audio
    public final static String AVG_PACKET_SIZE = "avgPacketSize";
    public final static String BIT_DEPTH = "bitDepth";            // video, audio
    public final static String BIT_RATE = "bitRate";              // video, audio
    public final static String BITS_PER_SAMPLE = "bitsPerSample"; // image
    public final static String BLOCK_SIZE_MAX = "blockSizeMax";
    public final static String BLOCK_SIZE_MIN = "blockSizeMin";
    public final static String BRIGHTNESS_VALUE = "brightnessValue";
    public final static String BYTE_ORDER = "byteOrder";      // for possible future use
    public final static String CFA_PATTERN = "cfaPattern";    // image
    public final static String CFA_PATTERN2 = "cfaPattern2";  // image
    public final static String CHANNELS = "channels";          // audio, maybe video
    public final static String CHARSET = "charset";            // text
    public final static String COLOR_SPACE = "colorSpace";
    public final static String COMPRESSION_SCHEME = "compressionScheme";
    public final static String DATA_FORMAT_TYPE = "dataFormatType";
    public final static String DIGITAL_CAMERA_MANUFACTURER = "digitalCameraManufacturer";
    public final static String DIGITAL_CAMERA_MODEL_NAME = "digitalCameraModelName";
    public final static String DIGITAL_CAMERA_SERIAL_NO = "digitalCameraSerialNo";
    public final static String DURATION = "duration";
    public final static String EXIF_VERSION = "exifVersion";
    public final static String EXPOSURE_BIAS_VALUE = "exposureBiasValue";
    public final static String EXPOSURE_INDEX = "exposureIndex";
    public final static String EXPOSURE_PROGRAM = "exposureProgram";
    public final static String EXPOSURE_TIME = "exposureTime";
    public final static String FLASH = "flash";
    public final static String FLASH_ENERGY = "flashEnergy";
    public final static String FNUMBER = "fNumber";
    public final static String FOCAL_LENGTH = "focalLength";
    public final static String FRAME_RATE = "frameRate";
    public final static String GRAY_RESPONSE_UNIT = "grayResponseUnit";
    public final static String ICC_PROFILE_NAME = "iccProfileName";
    public final static String ICC_PROFILE_VERSION = "iccProfileVersion";
    public final static String IMAGE_HEIGHT = "imageHeight";
    public final static String IMAGE_WIDTH = "imageWidth";
    public final static String ISO_SPEED_RATING = "isoSpeedRating";
    public final static String LANGUAGE = "language";      // may be useful someday
    public final static String LIGHT_SOURCE = "lightSource";
    public final static String MARKUP_BASIS = "markupBasis";
    public final static String MARKUP_BASIS_VERSION = "markupBasisVersion";
    public final static String MARKUP_LANGUAGE = "markupLanguage";
    public final static String MAX_APERTURE_VALUE = "maxApertureValue";
    public final static String MAX_BIT_RATE = "maxBitRate";      // audio
    public final static String MAX_PACKET_SIZE = "maxPacketSize";
    public final static String METERING_MODE = "meteringMode";   // image
    public final static String NUM_SAMPLES = "numSamples";       // audio
    public final static String OFFSET = "offset";
    public final static String ORIENTATION = "orientation";       // image, video
    public final static String PIXEL_ASPECT_RATIO = "pixelAspectRatio";  // video
    public final static String ROTATION = "rotation";        // image, video
    public final static String PAGE_COUNT = "pageCount";     // document
    public final static String PRIMARY_CHROMATICITIES_BLUE_X = "primaryChromaticitiesBlueX";
    public final static String PRIMARY_CHROMATICITIES_BLUE_Y = "primaryChromaticitiesBlueY";
    public final static String PRIMARY_CHROMATICITIES_GREEN_X = "primaryChromaticitiesGreenX";
    public final static String PRIMARY_CHROMATICITIES_GREEN_Y = "primaryChromaticitiesGreenY";
    public final static String PRIMARY_CHROMATICITIES_RED_X = "primaryChromaticitiesRedX";
    public final static String PRIMARY_CHROMATICITIES_RED_Y = "primaryChromaticitiesRedY";
    public final static String QUALITY_LAYERS = "qualityLayers";    // JP2
    public final static String REFERENCE_BLACK_WHITE = "referenceBlackWhite";
    public final static String RESOLUTION_LEVELS = "resolutionLevels";   // JP2
    public final static String SAMPLE_RATE = "sampleRate";        // audio
    public final static String SAMPLES_PER_PIXEL = "samplesPerPixel";
    public final static String SAMPLING_FREQUENCY_UNIT = "samplingFrequencyUnit";
    public final static String SCANNER_MANUFACTURER = "scannerManufacturer";
    public final static String SCANNER_MODEL_NAME = "scannerModelName";
    public final static String SCANNING_SOFTWARE_NAME = "scanningSoftwareName";
    public final static String SENSING_METHOD = "sensingMethod";
    public final static String SHUTTER_SPEED_VALUE = "shutterSpeedValue";
    public final static String SPECTRAL_SENSITIVITY = "spectralSensitivity";
    public final static String SOFTWARE = "software";
    public final static String SUBJECT = "subject";
    public final static String TIMECODE = "timecode";   // SMPTE timecode, for future use
    public final static String TILE_HEIGHT = "tileHeight";   // image, esp. TIFF
    public final static String TILE_WIDTH = "tileWidth";
    public final static String TIME_SCALE = "timeScale"; // 11-Apr-2013
    public final static String TITLE = "title";
    public final static String VIDEO_COMPRESSOR = "videoCompressor";
    public final static String WORD_COUNT = "wordCount";
    public final static String X_SAMPLING_FREQUENCY = "xSamplingFrequency";
    public final static String Y_SAMPLING_FREQUENCY = "ySamplingFrequency";
    public final static String YCBCR_COEFFICIENTS = "YCbCrCoefficients";
    public final static String YCBCR_POSITIONING = "YCbCrPositioning";
    public final static String YCBCR_SUBSAMPLING = "YCbCrSubSampling";
    
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
    
    
    private FitsMetadataValues() {
    	
    	mimeMap = parseFile(mimeMapProperties);
    	formatMap = parseFile(formatMapProperties);
    	mimeToFormatMap = parseFile(mimeToFormatMapProperties);
    	
    }
    
	public static synchronized FitsMetadataValues getInstance() {
		if (instance == null)
			instance = new FitsMetadataValues();
		
		return instance;
	}

    /** Do some normalization on variant MIME types. */
    public String normalizeMimeType(String mime) {
        if (mime == null || mime.length()==0) {
            return DEFAULT_MIMETYPE;
        }
        String normMime = mimeMap.get(mime);
        if (normMime != null) {
            return normMime;
        }
        else {
            return mime;
        }
    }
    
    public String normalizeFormat(String format) {
        if (format == null || format.length()==0) {
            return DEFAULT_FORMAT;
        }
        String normformat = formatMap.get(format);
        if (normformat != null) {
            return normformat;
        }
        else {
            return format;
        }
    }
    
    public String getFormatForMime(String mime) {
        if (mime == null || mime.length()==0) {
            return DEFAULT_FORMAT;
        }
        return mimeToFormatMap.get(mime);
    }
    
    private HashMap<String,String> parseFile(String inputFile) {
    	HashMap<String,String> map = new HashMap<String,String>();
    	BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(inputFile));
			String line;
			while ((line = in.readLine()) != null) {
				if(!line.startsWith("#") && !line.startsWith("\"#") ) {
					String[] parts = line.split("=");
					if(parts.length != 2) {
						logger.debug("Invalid map entry: " + line);
						continue;
					}
					map.put(parts[0], parts[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(in != null) try {in.close(); } catch (IOException e) { }
		}
		return map;
    }
}

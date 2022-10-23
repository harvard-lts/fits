//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.mediainfo;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import java.io.File;
import java.io.StringReader;
import java.util.Map;
import org.jdom2.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  The glue class for invoking the MediaInfo native library under FITS.
 */
public class MediaInfo extends ToolBase {

    private static final String TOOL_NAME = "MediaInfo";
    private boolean enabled = true;
    private Fits fits;

    private static final String mediaInfoFitsConfig = Fits.FITS_XML_DIR + "mediainfo" + File.separator;
    private static final String xsltTransform = "mediainfo_video_to_fits.xslt";
    private static final String WINDOWS_NATIVE_LIB_PATH =
            "tools" + File.separator + "mediainfo" + File.separator + "windows" + File.separator + "64";
    private static final String OSX_NATIVE_LIB_PATH = "tools" + File.separator + "mediainfo" + File.separator + "mac";
    private static final String LINUX_NATIVE_LIB_PATH =
            "tools" + File.separator + "mediainfo" + File.separator + "linux";

    private static final Logger logger = LoggerFactory.getLogger(MediaInfo.class);
    private static MediaInfoNativeWrapper mi = null;

    /**
     * Instantiate this class.
     * Since this class is instantiated via Java Reflection, any exception
     * in this constructor will actually result in a InstantiationException, nto a FitsToolException.
     *
     * @param fits
     * @throws FitsToolException
     */
    public MediaInfo(Fits fits) throws FitsToolException {
        super();
        this.fits = fits;
        info = new ToolInfo();
        info.setName(TOOL_NAME);

        String fitsHome = Fits.FITS_HOME; // this will always have a trailing slash if not empty
        String nativeLibPath = "";

        // Set the JNA library path based upon the OS
        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        switch (ostype) {
            case Windows:
                // default setting
                if (fitsHome.equals(".")) {
                    fitsHome = System.getProperty("user.dir");
                }
                // Assume both a 64-bit operating system AND Java version, so default to 64-bit DLL location.
                // 32 bit is not supported so pointing to 64-bit DLL will likely result in an error.
                nativeLibPath = fitsHome + WINDOWS_NATIVE_LIB_PATH;
                break;
            case MacOS:
                nativeLibPath = fitsHome + OSX_NATIVE_LIB_PATH;
                break;
            case Linux:
                nativeLibPath = fitsHome + LINUX_NATIVE_LIB_PATH;
                break;
            case Other:
                logger.warn("Unsupported native support in MediaInfo for this OS");
                break;
        }

        try {
            System.setProperty("jna.library.path", nativeLibPath);
            String versionOutput = MediaInfoNativeWrapper.Option_Static("Info_Version");
            // Strip "MediaInfoLib - v" from the version
            info.setVersion(versionOutput.replace("MediaInfoLib - v", ""));

            // Initialize the native library
            mi = new MediaInfoNativeWrapper();

        } catch (Throwable e) {
            String jvmModel = System.getProperty("sun.arch.data.model");
            logger.error(
                    "Error loading native library for this operating system for tool: " + TOOL_NAME + ". ostype=["
                            + ostype + "] -- jvmModel=[" + jvmModel + "] -- nativeLibPath=[" + nativeLibPath + "]"
                            + (nativeLibPath.length() == 0 ? "" : " -- No native MediaInfo library for this OS"),
                    e);
            throw new FitsToolException();
        }
    }

    @Override
    public ToolOutput extractInfo(File file) throws FitsToolException {

        logger.debug("MediaInfo.extractInfo starting on " + file.getName());
        long startTime = System.currentTimeMillis();

        // TODO: should we initialize the library via a static block?
        // Initialize the library
        // MediaInfoViaJNA mi = new MediaInfoViaJNA();

        // Open the file with mediainfo native library
        try {
            if (!(mi.Open(file.getCanonicalPath()) > 0)) {
                throw new FitsToolException("Error opening " + file.getName());
            }
        } catch (Exception e) {
            throw new FitsToolException("Error opening " + file.getName(), e);
        }

        // --------------------------------------------------------------------
        // OUTPUT Options for the Native MediaInfo Library are:
        // 		(From MediaInfo_Inform.cpp)
        //
        // NOTE: Default is Text - when no options set
        //
        // "EBUCore"
        // "EBUCore_1.5"
        //
        // "MPEG-7"
        //
        // "PBCore"
        // "PBCore_1.2"
        // "PBCore2"
        // "PBCore_2.0"
        //
        // NOTE: "reVTMD is disabled due to its non-free licensing
        //
        // XML
        // HTML
        // CSV
        //
        // Separate details are available via the API on various data types/tracks
        //
        //	     "General"
        //	     "Video"
        //	     "Audio"
        //	     "Text"
        //	     "Chapters"
        //	     "Image"
        //	     "Menu"
        // --------------------------------------------------------------------
        //
        // No format, so output is pure text
        // String textOutput = mi.Inform();
        // System.out.println("\nTEXT:\n" + textOutput);
        //
        // Set the option:
        // Complete details
        // mi.Option("Complete", "1");
        //
        //// Complete = false, use a subset
        // mi.Option("Complete", "");

        // Get MediaInfoLib Output as standard RAW XML
        mi.Option("Complete", "1");
        mi.Option("Output", "OLDXML");
        String execOutRaw = mi.Inform();

        // DEBUG
        // System.out.println("\nMediaInfo RAW output:\n" + execOutRaw + "\n\n");

        // Get MediaInfoLib Output as standard XML
        mi.Option("Complete", "");
        mi.Option("Output", "OLDXML");
        String execOut = mi.Inform();

        //// Get MediaInfoLib Output as EBUCore 1.5
        // mi.Option("Output", "EBUCore_1.5");
        // String ebuOut = mi.Inform();;

        // DEBUG
        // System.out.println("\nMediaInfo output:\n" + execOut + "\n\n");

        // --------------------------------------------------------------------
        // Retrieve additional information for audio/video tracks not contained
        // in the general XML
        // --------------------------------------------------------------------
        MediaInfoUtil mutil = new MediaInfoUtil(mi);

        // Maps to hold data that are obtained via explicit MediaInfo API call.
        // These values are either not exposed in the XML returned by the Inform()
        // method call, or if the granularity of the value is not correct, such as
        // bitRate not returning the value as milliseconds, but rather as Mpbs
        Map<String, String> generalValuesDataMap = mutil.loadGeneralDataMap();
        Map<String, Map<String, String>> videoTrackValuesMap = mutil.loadVideoDataMap(generalValuesDataMap);
        Map<String, Map<String, String>> audioTrackValuesMap = mutil.loadAudioDataMap();

        mi.Close();

        // ====================================================================
        // Create a Document out of the generated XML text
        // And transform via XSLT
        // ====================================================================
        Document outputMetaDoc = createXml(execOut);
        Document rawOut = createXml(execOutRaw);

        Document fitsXml = transform(mediaInfoFitsConfig + xsltTransform, outputMetaDoc);

        //		//
        //		// DEBUG - write xml to file
        //		//
        //		try {
        //			XMLOutputter xmlOutput = new XMLOutputter();
        //
        //			// display nice nice
        //			xmlOutput.setFormat(Format.getPrettyFormat());
        //			xmlOutput.output(fitsXml, new FileWriter("fitsxml_so_far.xml"));
        //		} catch (IOException e) {
        //			e.printStackTrace();
        //		}

        // Revise the XML to include element data that was not returned either
        // via the MediaInfo XML or require revision for granularity
        mutil.reviseXmlData(fitsXml, videoTrackValuesMap, audioTrackValuesMap, generalValuesDataMap);

        mutil.removeEmptyElements(fitsXml);

        // DEBUG
        // String finalXml = new XMLOutputter(Format.getPrettyFormat()).outputString(fitsXml);
        // System.out.println("\nFINAL XML:\n" + finalXml);

        output = new ToolOutput(this, fitsXml, rawOut, fits);

        // DEBUG
        // String fitsOutputString = new XMLOutputter(Format.getPrettyFormat()).outputString(output.getFitsXml());

        duration = System.currentTimeMillis() - startTime;
        runStatus = RunStatus.SUCCESSFUL;
        logger.debug("MediaInfo.extractInfo finished on " + file.getName());

        return output;
    }

    private Document createXml(String out) throws FitsToolException {
        Document doc = null;
        try {
            doc = saxBuilder.build(new StringReader(out));
        } catch (Exception e) {
            throw new FitsToolException("Error parsing Mediainfo XML Output", e);
        }
        return doc;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean value) {
        enabled = value;
    }
}

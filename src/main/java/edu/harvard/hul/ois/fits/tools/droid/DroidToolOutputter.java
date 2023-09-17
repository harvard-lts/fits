//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.droid;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataValues;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;

/**
 * This class generates the tool output for DROID.
 *
 * @author <a href="http://www.garymcgath.com">Gary McGath</a>
 */
class DroidToolOutputter {

    private static final Namespace fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);

    private static final String UNKNOWN_FORMAT = "Unknown";
    private static final String ZIP_PUID = "x-fmt/263";

    private final ToolBase toolBase;
    private final Fits fits;
    private final DroidResult result;

    // The following fields are only relevant when the file was an archive containing other files. They're stored at
    // the class level because we need the values for both the fits output and the raw output and this avoids computing
    // them twice.
    private final Map<String, Long> countsByFormat;
    private final long originalSize;
    private final long fileSize;
    private final String compressionMethod;

    public DroidToolOutputter(ToolBase toolBase, Fits fits, DroidResult result) {
        this.toolBase = toolBase;
        this.fits = fits;
        this.result = result;

        if (!result.getInnerResults().isEmpty()) {
            countsByFormat = countByFormat(result.getInnerResults());
            originalSize = calculateTotalSize(result.getInnerResults());
            fileSize = fileSize(result.getFile());
            if (isZip(result.getPrimaryResult())) {
                compressionMethod = zipCompressionMethod(fileSize, originalSize);
            } else {
                compressionMethod = null;
            }
        } else {
            countsByFormat = Collections.emptyMap();
            originalSize = -1;
            fileSize = -1;
            compressionMethod = null;
        }
    }

    /** Produce a JDOM document with fits as its root element. This
     *  will contain just identification, not metadata elements.
     */
    public ToolOutput toToolOutput() throws FitsToolException {
        List<IdentificationResult> resList = result.getPrimaryResult().getResults();

        Document fitsXml = createToolData();
        Document rawOut = buildRawData(resList);
        return new ToolOutput(toolBase, fitsXml, rawOut, fits);
    }

    /** Create a base tool data document and add elements
     *  for each format. */
    private Document createToolData() {
        List<IdentificationResult> resList = result.getPrimaryResult().getResults();
        Element fitsElem = new Element("fits", fitsNS);
        Document toolDoc = new Document(fitsElem);
        Element idElem = new Element("identification", fitsNS);
        fitsElem.addContent(idElem);
        for (IdentificationResult res : resList) {
            String filePuid = res.getPuid();
            String formatName = res.getName();
            formatName = mapFormatName(formatName);
            String mimeType = res.getMimeType();

            if (FitsMetadataValues.getInstance().normalizeMimeType(mimeType) != null) {
                mimeType = FitsMetadataValues.getInstance().normalizeMimeType(mimeType);
            }

            if (formatName.equals("Digital Negative (DNG)")) {
                mimeType = "image/x-adobe-dng";
            }

            String version = res.getVersion();
            version = mapVersion(version);

            Element identityElem = new Element("identity", fitsNS);
            Attribute attr = new Attribute("format", formatName);
            identityElem.setAttribute(attr);

            if (mimeType != null) {
                attr = new Attribute("mimetype", mimeType);
                identityElem.setAttribute(attr);
            }

            // Is there anything to put into the fileinfo or metadata elements?
            // Both are optional, so they can be left out if they'd be empty.
            idElem.addContent(identityElem);

            // If there's a version, report it
            if (version != null && !version.isEmpty()) {
                Element versionElem = new Element("version", fitsNS);
                identityElem.addContent(versionElem);
                versionElem.addContent(version);
            }

            // If there's a PUID, report it as an external identifier
            if (filePuid != null) {
                Element puidElem = new Element("externalIdentifier", fitsNS);
                identityElem.addContent(puidElem);
                puidElem.addContent(filePuid);
                attr = new Attribute("type", "puid");
                puidElem.setAttribute(attr);
            }
        }

        List<IdentificationResultCollection> innerResults = result.getInnerResults();

        // The only time there will be a metadata section from DROID is when
        // there is an aggregator for ZIP files and there are file entries.
        if (!innerResults.isEmpty()) {
            Element metadataElem = new Element("metadata", fitsNS);
            fitsElem.addContent(metadataElem);
            Element containerElem = new Element("container", fitsNS);
            metadataElem.addContent(containerElem);

            Element origSizeElem = new Element("originalSize", fitsNS);
            origSizeElem.addContent(String.valueOf(originalSize));
            containerElem.addContent(origSizeElem);

            if (compressionMethod != null) {
                Element compressionMethodElem = new Element("compressionMethod", fitsNS);
                compressionMethodElem.addContent(compressionMethod);
                containerElem.addContent(compressionMethodElem);
            }

            Element entriesElem = new Element("entries", fitsNS);
            Attribute totalEntriesCountAttr = new Attribute("totalEntries", String.valueOf(innerResults.size()));
            entriesElem.setAttribute(totalEntriesCountAttr);
            containerElem.addContent(entriesElem);

            countsByFormat.forEach((format, count) -> {
                Element entryElem = new Element("format", fitsNS);
                Attribute nameAttr = new Attribute("name", format);
                entryElem.setAttribute(nameAttr);

                Attribute numberAttr = new Attribute("number", String.valueOf(count));
                entryElem.setAttribute(numberAttr);

                entriesElem.addContent(entryElem);
            });
        }

        return toolDoc;
    }

    /**
     * Groups and counts the results by format name.
     *
     * @param innerResults the identification results of files within an archive
     * @return a map of format names to the number of occurrences of that format
     */
    private Map<String, Long> countByFormat(List<IdentificationResultCollection> innerResults) {
        return innerResults.stream()
                .map(r -> {
                    if (r.getResults().isEmpty()) {
                        return UNKNOWN_FORMAT;
                    }
                    return mapFormatName(r.getResults().get(0).getName());
                })
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));
    }

    /**
     * Sums the combined file size based on the file size reported in the identification results.
     *
     * @param innerResults the identification results of files within an archive
     * @return total file size
     */
    private long calculateTotalSize(List<IdentificationResultCollection> innerResults) {
        return innerResults.stream()
                .map(IdentificationResultCollection::getFileLength)
                .reduce(0L, Long::sum);
    }

    /**
     * @return the file size of the target file on disk
     */
    private long fileSize(Path file) {
        try {
            return Files.size(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @return true if the target file was determined to be a zip file
     */
    private boolean isZip(IdentificationResultCollection identificationResult) {
        return identificationResult.getResults().stream()
                .map(IdentificationResult::getPuid)
                .anyMatch(ZIP_PUID::equals);
    }

    /**
     * @param fileSize the size of the file on disk
     * @param originalSize the reported size of all of the components of a file, this will be different from the file
     *                     size if the file is compressed
     * @return the zip compression method
     */
    private String zipCompressionMethod(long fileSize, long originalSize) {
        return fileSize < originalSize ? "deflate" : "stored";
    }

    private static String mapFormatName(String formatName) {
        if (formatName == null || formatName.length() == 0) {
            return FitsMetadataValues.DEFAULT_FORMAT;
        } else if (formatName.startsWith("JPEG2000") || formatName.startsWith("JP2 (JPEG 2000")) {
            return "JPEG 2000 JP2";
        } else if (formatName.startsWith("Exchangeable Image File Format (Compressed)")) {
            return "JPEG EXIF";
        } else if (formatName.startsWith("Exchangeable Image File Format (Uncompressed)")) {
            return "TIFF EXIF";
        } else if (formatName.contains("PDF/A")) {
            return "PDF/A";
        } else if (formatName.contains("PDF/X")) {
            return "PDF/X";
        } else if (formatName.contains("Portable Document Format")) {
            return "Portable Document Format";
        } else if (formatName.startsWith("Microsoft Word") && !formatName.equals("Microsoft Word for Windows")) {
            return "Microsoft Word Binary File Format";
        } else if (formatName.startsWith("Microsoft Excel") && !formatName.equals("Microsoft Excel for Windows")) {
            return "Microsoft Excel";
        } else if (FitsMetadataValues.getInstance().normalizeFormat(formatName) != null) {
            return FitsMetadataValues.getInstance().normalizeFormat(formatName);
        } else {
            return formatName;
        }
    }

    private String mapVersion(String version) {
        if (version == null || version.length() == 0) {
            return version;
        } else if (version.equals("1987a")) {
            return "87a";
        } else {
            return version;
        }
    }

    /**
     * Create "raw" XML. The DROID namespace is no longer meaningful. Does this have any
     * particular requirements beyond dumping as much data as might be useful?
     *
     * @throws FitsToolException
     * @throws SAXException
     */
    private Document buildRawData(List<IdentificationResult> resList) throws FitsToolException {
        StringWriter out = new StringWriter();

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("\n");
        out.write("<results>");
        out.write("\n");
        for (IdentificationResult res : resList) {
            String filePuid = res.getPuid();
            String formatName = res.getName();
            String mimeType = res.getMimeType();
            String version = res.getVersion();
            out.write("<result>");
            out.write("\n");
            out.write("<filePuid>" + filePuid + "</filePuid>");
            out.write("\n");
            out.write("<formatName>" + formatName + "</formatName>");
            out.write("\n");
            out.write("<mimeType>" + mimeType + "</mimeType>");
            out.write("\n");
            out.write("<version>" + version + "</version>");
            out.write("</result>");
        }

        var innerResults = result.getInnerResults();

        if (!innerResults.isEmpty()) {
            out.write("<container originalSize='");
            out.write(String.valueOf(originalSize));

            if (compressionMethod != null) {
                out.write("' method='");
                out.write(compressionMethod);
            }

            out.write("'>");
            out.write("\n");

            out.write("<entries totalEntries='");
            out.write(String.valueOf(innerResults.size()));
            out.write("'>");
            out.write("\n");

            countsByFormat.forEach((format, count) -> {
                out.write("<entry formatName='");
                out.write(format);
                out.write("' count='");
                out.write(String.valueOf(count));
                out.write("'></entry>");
                out.write("\n");
            });
            out.write("</entries>");
            out.write("\n");

            out.write("</container>");
            out.write("\n");
        }

        out.write("  </results>");
        out.write("\n");

        out.flush();

        try {
            out.close();
        } catch (IOException e) {
            throw new FitsToolException("Error closing DROID XML output stream", e);
        }

        Document doc = null;
        try {
            SAXBuilder saxBuilder = toolBase.getSaxBuilder();
            doc = saxBuilder.build(new StringReader(out.toString()));
        } catch (Exception e) {
            throw new FitsToolException("Error parsing DROID XML Output", e);
        }
        return doc;
    }
}

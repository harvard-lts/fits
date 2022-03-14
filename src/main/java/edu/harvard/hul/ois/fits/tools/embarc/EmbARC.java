/* 
 * Copyright 2022 Harvard University Library
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
package edu.harvard.hul.ois.fits.tools.embarc;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.portalmedia.embarc.cli.Main;
import com.portalmedia.embarc.cli.XmlWriterDpx;
import com.portalmedia.embarc.parser.FileFormat;
import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;
import com.portalmedia.embarc.parser.dpx.DPXFileListHelper;
import com.portalmedia.embarc.parser.dpx.DPXMetadata;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataValues;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.util.DateTimeUtil;

public class EmbARC extends ToolBase {
	private boolean enabled = true;
	private Fits fits;
	private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);

	private static final Logger logger = LoggerFactory.getLogger(EmbARC.class);

	public EmbARC(Fits fits) throws FitsToolException {
		super();
		this.fits = fits;
		logger.debug("Initializing embARC");
		info = new ToolInfo("embARC", Main.version, null);
	}

	@Override
	public ToolOutput extractInfo(File file) throws FitsToolException {
		logger.debug("embARC extractInfo starting on {}", file.getName());
		long startTime = System.currentTimeMillis();

		String absPath = file.getAbsolutePath();
		FileFormat fileFormat = FileFormatDetection.getFileFormat(absPath);

		// embARC should only run for dpx files
		if (fileFormat != FileFormat.DPX) {
			throw new FitsToolException("embARC processing failed: non-dpx file detected");
		}

		DPXFileInformation dpxFileInfo = DPXFileListHelper.createDPXFileInformation(absPath);

		Document fitsXml = createToolData(file, dpxFileInfo);
		Document rawData = null;
		try {
			rawData = createRawDataXml(dpxFileInfo);
		} catch (FitsToolException ex) {
			logger.debug("embARC caught error creating raw tool output {}", ex.toString());
		}
		ToolOutput output = new ToolOutput(this, fitsXml, rawData, fits);

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		logger.debug("embARC extractInfo finished on {}", file.getName());
		return output;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean value) {
		enabled = value;
	}

	private Document createToolData(File file, DPXFileInformation dpxFileInfo) {
		Element fitsElement = new Element("fits", fitsNS);
		Document toolDocument = new Document(fitsElement);

		Element identificationElement = createIdentificationElement(dpxFileInfo);
		fitsElement.addContent(identificationElement);

		Element fileInfoElement = createFileInfoElement(file, dpxFileInfo);
		fitsElement.addContent(fileInfoElement);

		Element metadataElement = createMetadataElement(dpxFileInfo);
		fitsElement.addContent(metadataElement);

		return toolDocument;
	}

	private Element createIdentificationElement(DPXFileInformation dpxFileInfo) {
		Element identificationElement = new Element("identification", fitsNS);
		Element identityElem = new Element("identity", fitsNS);

		String format = "Digital Picture Exchange";
		String mimeType = FitsMetadataValues.getInstance().normalizeMimeType(dpxFileInfo.getMimeType());

		identityElem.setAttribute(new Attribute("format", format));
		identityElem.setAttribute(new Attribute("mimetype", mimeType));

		identificationElement.addContent(identityElem);
		return identificationElement;
	}

	private Element createFileInfoElement(File file, DPXFileInformation dpxFileInfo) {
		DPXMetadata metadata = dpxFileInfo.getFileData();
		Element fileInfoElement = new Element("fileinfo", fitsNS);

		String copyrightNote = getValueForColumn(metadata, DPXColumn.COPYRIGHT_STATEMENT);
		String created = DateTimeUtil.standardize(getValueForColumn(metadata, DPXColumn.CREATION_DATETIME));
		String filePath = file.getAbsolutePath();
		String fileName = file.getName();
		String fileSize = Long.toString(file.length());

		if (!created.isEmpty()) {
			addElement(fileInfoElement, FitsMetadataValues.CREATED, created);
		}

		if (!copyrightNote.isEmpty()) {
			addElement(fileInfoElement, FitsMetadataValues.COPYRIGHT_NOTE, copyrightNote);
		}

		if (!filePath.isEmpty()) {
			addElement(fileInfoElement, "filepath", filePath);
		}

		if (!fileName.isEmpty()) {
			addElement(fileInfoElement, "filename", fileName);
		}

		if (!fileSize.isEmpty()) {
			addElement(fileInfoElement, FitsMetadataValues.SIZE, fileSize);
		}

		return fileInfoElement;
	}

	private Element createMetadataElement(DPXFileInformation dpxFileInfo) {
		DPXMetadata metadata = dpxFileInfo.getFileData();
		Element metadataElement = new Element("metadata", fitsNS);
		Element imageElement = new Element(FitsMetadataValues.IMAGE, fitsNS);

		String bitDepth = getValueForColumn(metadata, DPXColumn.BIT_DEPTH_1);
		String byteOrder = mapMagicNumberToByteOrder(getValueForColumn(metadata, DPXColumn.MAGIC_NUMBER));
		String colorSpace = mapDescriptorToColorSpace(getValueForColumn(metadata, DPXColumn.DESCRIPTOR_1));
		String linesPerImageElement = getValueForColumn(metadata, DPXColumn.LINES_PER_IMAGE_ELEMENT);
		String creator = getValueForColumn(metadata, DPXColumn.CREATOR);
		String pixelsPerLine = getValueForColumn(metadata, DPXColumn.PIXELS_PER_LINE);
		String orientation = mapImageOrientationToOrientation(getValueForColumn(metadata, DPXColumn.IMAGE_ORIENTATION));
		String inputDeviceName = getValueForColumn(metadata, DPXColumn.INPUT_DEVICE_NAME);
		String inputDeviceSerialNumber = getValueForColumn(metadata, DPXColumn.INPUT_DEVICE_SERIAL_NUMBER);

		if (!bitDepth.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.BITS_PER_SAMPLE, bitDepth);
		}

		if (!byteOrder.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.BYTE_ORDER, byteOrder);
		}

		if (!colorSpace.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.COLOR_SPACE, colorSpace);
		}

		if (!linesPerImageElement.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.IMAGE_HEIGHT, linesPerImageElement);
		}

		if (!pixelsPerLine.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.IMAGE_WIDTH, pixelsPerLine);
		}

		if (!creator.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.IMAGE_PRODUCER, creator);
		}

		if (!orientation.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.ORIENTATION, orientation);
		}

		if (!inputDeviceName.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.SCANNER_MODEL_NAME, inputDeviceName);
		}

		if (!inputDeviceSerialNumber.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.SCANNER_MODEL_SERIAL_NO, inputDeviceSerialNumber);
		}

		metadataElement.addContent(imageElement);
		return metadataElement;
	}

	private Document createRawDataXml(DPXFileInformation dpxFileInfo) throws FitsToolException {
		StringWriter out = new StringWriter();
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.write("\n");
		out.write("<embARC>");
		out.write("\n");
		out.write("<rawOutput>\n");
		String dpxXml = XmlWriterDpx.createDpxMetadataXml(dpxFileInfo);
		if (dpxXml != null) {
			out.write(dpxXml);
		}
		out.write("</rawOutput>");
		out.write("\n");
		out.write("</embARC>");
		out.write("\n");
		out.flush();

		try {
			out.close();
		} catch (IOException e) {
			throw new FitsToolException("Error closing embARC tool XML output stream");
		}

		try {
			return saxBuilder.build(new StringReader(out.toString()));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing embARC tool XML raw output");
		}
	}

	private String getValueForColumn(DPXMetadata metadata, DPXColumn column) {
		MetadataColumn metadataColumn = metadata.getColumn(column);
		String stdValue = metadataColumn.getStandardizedValue();
		if (metadataColumn == null || metadataColumn.isNull() || "NULL".equals(stdValue)) {
			return "";
		}
		return stripInvalidXmlChars(stdValue);
	}

	private String stripInvalidXmlChars(String input) {
		String invalidChars = "[^"
			+ "\u0001-\uD7FF"
			+ "\u0009\r\n"
			+ "\u0020-\uD7FF"
			+ "\uE000-\uFFFD"
			+ "\ud800\udc00-\udbff\udfff"
			+ "]";
		return input.replaceAll(invalidChars, "");
	}

	private void addElement(Element parent, String tag, String value ) {
		Element newElem = new Element(tag, fitsNS);
		newElem.addContent(value);
		parent.addContent(newElem);
	}

	private String mapMagicNumberToByteOrder(String magicNumber) {
		if (magicNumber.charAt(0) == 'S') {
			return "big endian";
		} else if (magicNumber.charAt(0) == 'X') {
			return "little endian";
		}
		return "";
	}

	private String mapDescriptorToColorSpace(String descriptor) {
		switch (descriptor) {
			case "0":
				return "User defined (or unspecified single component)";
			case "1":
				return "Red (R)";
			case "2":
				return "Green (G)";
			case "3":
				return "Blue (B)";
			case "4":
				return "Alpha (matte)";
			case "6":
				return "Luma (Y)";
			case "7":
				return "Color Difference (CB, CR, subsampled by two)";
			case "8":
				return "Depth (Z)";
			case "9":
				return "Composite video";
			case "50":
				return "R,G,B";
			case "51":
				return "R,G,B, Alpha (A)";
			case "52":
				return "A,B,G,R";
			case "100":
				return "CB, Y, CR, Y (4:2:2)";
			case "101":
				return "CB, Y, A, CR, Y, A (4:2:2:4)";
			case "102":
				return "CB, Y, CR (4:4:4)";
			case "103":
				return "CB, Y, CR, A (4:4:4:4)";
			case "150":
				return "User-defined 2-component element";
			case "151":
				return "User-defined 3-component element";
			case "152":
				return "User-defined 4-component element";
			case "153":
				return "User-defined 5-component element";
			case "154":
				return "User-defined 6-component element";
			case "155":
				return "User-defined 7-component element";
			case "156":
				return "User-defined 8-component element";
			default:
				return "";
		}
	}

	private String mapImageOrientationToOrientation(String imageOrientation) {
		switch (imageOrientation) {
			case "0":
				return "normal*";
			case "1":
				return "normal, image flipped";
			case "2":
				return "normal, image flipped, rotated 180°";
			case "3":
				return "normal, rotated 180°";
			case "4":
				return "normal, image flipped, rotated ccw 90°";
			case "5":
				return "normal, rotated cw 90°";
			case "6":
				return "normal, rotated ccw 90°";
			case "7":
				return "normal, image flipped, rotated cw 90°";
			default:
				return "";
		}
	}
}

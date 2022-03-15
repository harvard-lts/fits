package edu.harvard.hul.ois.fits.tools.embarc;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.portalmedia.embarc.cli.Main;
import com.portalmedia.embarc.cli.JsonWriterDpx;
import com.portalmedia.embarc.parser.dpx.DPXFileListHelper;
import com.portalmedia.embarc.parser.dpx.DPXMetadata;
import com.portalmedia.embarc.parser.FileFormat;
import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataValues;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

public class EmbARC extends ToolBase {
	private boolean enabled = true;
	private Fits fits;
	private final static Namespace fitsNS = Namespace.getNamespace (Fits.XML_NAMESPACE);

	private File inputFile;
	private FileFormat fileFormat;

	private static final Logger logger = LoggerFactory.getLogger(EmbARC.class);

	public EmbARC(Fits fits) throws FitsToolException {
		super();
		this.fits = fits;
		logger.debug("Initializing embARC");
		info = new ToolInfo("embARC", Main.version, null);
	}

	@Override
	public ToolOutput extractInfo(File file) throws FitsToolException {
		logger.debug("embARC extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();

		inputFile = file;
		String absPath = inputFile.getAbsolutePath();
		fileFormat = FileFormatDetection.getFileFormat(absPath);

		// embARC should only run for dpx files
		if (fileFormat != FileFormat.DPX) {
			throw new FitsToolException("embARC processing failed: non-dpx file detected");
		}

		DPXFileInformation dpxFileInfo = DPXFileListHelper.createDPXFileInformation(absPath);

		Document fitsXml = createToolData(dpxFileInfo);
		Document rawData = null;
		try {
			rawData = createRawDataXml(dpxFileInfo);
		} catch (FitsToolException ex) {}

		if (rawData == null) {
			rawData = createRawDataJson(dpxFileInfo);
		}
		ToolOutput output = new ToolOutput(this, fitsXml, rawData, fits);

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		logger.debug("embARC extractInfo finished on " + file.getName());
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

	private Document createToolData(DPXFileInformation dpxFileInfo) {
		Element fitsElement = new Element("fits", fitsNS);
		Document toolDocument = new Document(fitsElement);

		Element identificationElement = createIdentificationElement(dpxFileInfo);
		fitsElement.addContent(identificationElement);

		Element fileInfoElement = createFileInfoElement(dpxFileInfo);
		fitsElement.addContent(fileInfoElement);

		Element metadataElement = createMetadataElement(dpxFileInfo);
		fitsElement.addContent(metadataElement);

		return toolDocument;
	}

	private Element createIdentificationElement(DPXFileInformation dpxFileInfo) {
		Element identificationElement = new Element("identification", fitsNS);
		Element identityElem = new Element("identity", fitsNS);

		String format = fileFormat == FileFormat.DPX ? "Digital Picture Exchange" : "";
		String mimeType = FitsMetadataValues.getInstance().normalizeMimeType(dpxFileInfo.getMimeType());

		identityElem.setAttribute(new Attribute("format", format));
		identityElem.setAttribute(new Attribute("mimetype", mimeType));

		identificationElement.addContent(identityElem);
		return identificationElement;
	}

	private Element createFileInfoElement(DPXFileInformation dpxFileInfo) {
		DPXMetadata metadata = dpxFileInfo.getFileData();
		Element fileInfoElement = new Element("fileinfo", fitsNS);

		String copyrightNote = metadata.getColumn(DPXColumn.COPYRIGHT_STATEMENT).getStandardizedValue();
		String created = metadata.getColumn(DPXColumn.CREATION_DATETIME).getStandardizedValue();
		String filePath = inputFile.getAbsolutePath();
		String fileName = inputFile.getName();
		String fileSize = Long.toString(inputFile.length());

		if (created != null && !created.isEmpty()) {
			addElement(fileInfoElement, FitsMetadataValues.CREATED, stripInvalidXMLChars(created));
		}

		if (copyrightNote != null && !copyrightNote.isEmpty()) {
			addElement(fileInfoElement, FitsMetadataValues.COPYRIGHT_NOTE, stripInvalidXMLChars(copyrightNote));
		}

		if (filePath != null) {
			addElement(fileInfoElement, "filepath", filePath);
		}

		if (fileName != null) {
			addElement(fileInfoElement, "filename", fileName);
		}

		if (fileSize != null) {
			addElement(fileInfoElement, FitsMetadataValues.SIZE, fileSize);
		}

		return fileInfoElement;
	}

	private Element createMetadataElement(DPXFileInformation dpxFileInfo) {
		DPXMetadata metadata = dpxFileInfo.getFileData();
		Element metadataElement = new Element("metadata", fitsNS);
		Element imageElement = new Element(FitsMetadataValues.IMAGE, fitsNS);

		String bitDepth = metadata.getColumn(DPXColumn.BIT_DEPTH_1).getStandardizedValue();
		String byteOrder = mapMagicNumberToByteOrder(metadata.getColumn(DPXColumn.MAGIC_NUMBER).getStandardizedValue());
		String colorSpace = mapDescriptorToColorSpace(metadata.getColumn(DPXColumn.DESCRIPTOR_1).getStandardizedValue());
		String linesPerImageElement = metadata.getColumn(DPXColumn.LINES_PER_IMAGE_ELEMENT).getStandardizedValue();
		String creator = metadata.getColumn(DPXColumn.CREATOR).getStandardizedValue();
		String pixelsPerLine = metadata.getColumn(DPXColumn.PIXELS_PER_LINE).getStandardizedValue();
		String orientation = mapImageOrientationToOrientation(metadata.getColumn(DPXColumn.IMAGE_ORIENTATION).getStandardizedValue());
		String inputDeviceName = metadata.getColumn(DPXColumn.INPUT_DEVICE_NAME).getStandardizedValue();
		String inputDeviceSerialNumber = metadata.getColumn(DPXColumn.INPUT_DEVICE_SERIAL_NUMBER).getStandardizedValue();

		if (bitDepth != null && !bitDepth.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.BITS_PER_SAMPLE, stripInvalidXMLChars(bitDepth));
		}

		if (byteOrder != null && !byteOrder.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.BYTE_ORDER, stripInvalidXMLChars(byteOrder));
		}

		if (colorSpace != null && !colorSpace.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.COLOR_SPACE, stripInvalidXMLChars(colorSpace));
		}

		if (linesPerImageElement != null && !linesPerImageElement.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.IMAGE_HEIGHT, stripInvalidXMLChars(linesPerImageElement));
		}

		if (creator != null && !creator.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.IMAGE_PRODUCER, stripInvalidXMLChars(creator));
		}

		if (pixelsPerLine != null && !pixelsPerLine.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.IMAGE_WIDTH, stripInvalidXMLChars(pixelsPerLine));
		}

		if (orientation != null && !orientation.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.ORIENTATION, stripInvalidXMLChars(orientation));
		}

		if (inputDeviceName != null && !inputDeviceName.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.SCANNER_MODEL_NAME, stripInvalidXMLChars(inputDeviceName));
		}

		if (inputDeviceSerialNumber != null && !inputDeviceSerialNumber.isEmpty()) {
			addElement(imageElement, FitsMetadataValues.SCANNER_MODEL_SERIAL_NO, stripInvalidXMLChars(inputDeviceSerialNumber));
		}

		metadataElement.addContent(imageElement);
		return metadataElement;
	}

	private Document createRawDataJson(DPXFileInformation dpxFileInfo) {
		Element root = new Element("embARC");
		Element rawOutput = new Element("rawOutput");

		JSONObject dpxJson = JsonWriterDpx.createJsonFileObject(dpxFileInfo);
		rawOutput.addContent(dpxJson.toString(2));
		root.addContent(rawOutput);

		return new Document(root);
	}

	private Document createRawDataXml(DPXFileInformation dpxFileInfo) throws FitsToolException {
		JSONObject dpxJson = JsonWriterDpx.createJsonFileObject(dpxFileInfo);
		String dpxXmlString = XML.toString(dpxJson);

		StringWriter out = new StringWriter();
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.write("\n");
		out.write("<embARC>");
		out.write("\n");
		out.write("<rawOutput>\n");
		out.write(dpxXmlString);
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

		Document doc = null;
		try {
			doc = saxBuilder.build(new StringReader(out.toString()));
		} catch (Exception e) {
			throw new FitsToolException("Error parsing embARC tool XML raw output");
		}

		return doc;
	}

	private void addElement(Element parent, String tag, String value ) {
		Element newElem = new Element(tag, fitsNS);
		newElem.addContent(value);
		parent.addContent(newElem);
	}

	private String stripInvalidXMLChars(String input) {
		String invalidChars = "[^"
                + "\u0009\r\n"
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "\ud800\udc00-\udbff\udfff"
                + "]";
		String output = input.replaceAll(invalidChars, "");
		return output;
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
				return "left to right, top to bottom";
			case "1":
				return "right to left, top to bottom";
			case "2":
				return "left to right, bottom to top";
			case "3":
				return "right to left, bottom to top";
			case "4":
				return "top to bottom, left to right";
			case "5":
				return "top to bottom, right to left";
			case "6":
				return "bottom to top, left to right";
			case "7":
				return "bottom to top, right to left";
			default:
				return "";
		}
	}
}

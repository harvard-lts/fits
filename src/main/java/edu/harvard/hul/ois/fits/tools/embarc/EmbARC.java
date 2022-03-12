package edu.harvard.hul.ois.fits.tools.embarc;

import java.io.File;
import java.io.IOException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONObject;
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
	private DPXFileInformation dpxFileInfo;

	private static final Logger logger = LoggerFactory.getLogger(EmbARC.class);

	public EmbARC(Fits fits) throws FitsToolException {
		super();
		this.fits = fits;
		logger.debug("Initializing EmbARC");
		info = new ToolInfo("EmbARC", Main.version, null);

		try {
			// TODO
		} catch (Exception e) {
			throw new FitsToolException("Error initilizing embARC", e);
		}
	}

	@Override
	public ToolOutput extractInfo(File file) throws FitsToolException {
		logger.debug("EmbARC.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();

		inputFile = file;
		String absPath = inputFile.getAbsolutePath();
		fileFormat = FileFormatDetection.getFileFormat(absPath);

		if (fileFormat != FileFormat.DPX) {
			logger.debug("Non DPX file input, bailing");
			return null;
		}
		dpxFileInfo = DPXFileListHelper.createDPXFileInformation(absPath);

		Document fitsXml = createToolData();
		Document rawData = createRawData();
		ToolOutput output = new ToolOutput(this, fitsXml, rawData, fits);

		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		logger.debug("EmbARC.extractInfo finished on " + file.getName());
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

	private Document createToolData() {
		DPXMetadata metadata = dpxFileInfo.getFileData();

		Element fitsElement = new Element("fits", fitsNS);
        Document toolDocument = new Document(fitsElement);

        /* IDENTIFICATION */
        Element identificationElement = new Element("identification", fitsNS);
        Element identityElem = new Element("identity", fitsNS);

        String format = fileFormat == FileFormat.DPX ? "Digital Picture Exchange" : "";
        identityElem.setAttribute(new Attribute("format", format));

        String mimeType = FitsMetadataValues.getInstance().normalizeMimeType(dpxFileInfo.getMimeType());
        identityElem.setAttribute(new Attribute("mimetype", mimeType));

        identificationElement.addContent(identityElem);
        fitsElement.addContent(identificationElement);

        /* FILEINFO */
        Element fileInfoElement = createFileInfoElement(metadata);
        fitsElement.addContent(fileInfoElement);

        /* METADATA */
    	Element metadataElement = createMetadataElement(metadata);
    	fitsElement.addContent(metadataElement);

		return toolDocument;
	}
	
	private Element createFileInfoElement(DPXMetadata metadata) {
		String copyrightNote = metadata.getColumn(DPXColumn.COPYRIGHT_STATEMENT).getCurrentValue();
        String created = metadata.getColumn(DPXColumn.CREATION_DATETIME).getCurrentValue();
        String filePath = inputFile.getAbsolutePath();
        String fileName = inputFile.getName();
        String fileSize = Long.toString(inputFile.length());

        Element fileInfoElement = new Element("fileinfo", fitsNS);

        if (created != null) {
            Element createdElem = new Element(FitsMetadataValues.CREATED, fitsNS);
            createdElem.addContent(created);
            fileInfoElement.addContent(createdElem);
        }

        if (copyrightNote != null) {
            Element copyrightNoteElement = new Element(FitsMetadataValues.COPYRIGHT_NOTE, fitsNS);
            copyrightNoteElement.addContent(copyrightNote);
            fileInfoElement.addContent(copyrightNoteElement);
        }

        if (filePath != null) {
            Element filePathElement = new Element("filepath", fitsNS);
            filePathElement.addContent(filePath);
            fileInfoElement.addContent(filePathElement);
        }

        if (fileName != null) {
            Element fileNameElement = new Element("filename", fitsNS);
            fileNameElement.addContent(fileName);
            fileInfoElement.addContent(fileNameElement);
        }

        if (fileSize != null) {
            Element fileSizeElement = new Element(FitsMetadataValues.SIZE, fitsNS);
            fileSizeElement.addContent(fileSize);
            fileInfoElement.addContent(fileSizeElement);
        }

        return fileInfoElement;
	}

	private Element createMetadataElement(DPXMetadata metadata) {
		Element metadataElement = new Element("metadata", fitsNS);
		Element imageElement = new Element(FitsMetadataValues.IMAGE, fitsNS);
		Element videoElement = new Element(FitsMetadataValues.VIDEO, fitsNS);

		addSimpleElement(imageElement, FitsMetadataValues.BITS_PER_SAMPLE, metadata.getColumn(DPXColumn.BIT_DEPTH_1).getCurrentValue());
		addSimpleElement(imageElement, FitsMetadataValues.BYTE_ORDER, metadata.getColumn(DPXColumn.MAGIC_NUMBER).getCurrentValue());
		addSimpleElement(imageElement, FitsMetadataValues.COLOR_SPACE, metadata.getColumn(DPXColumn.DESCRIPTOR_1).getCurrentValue());
		addSimpleElement(imageElement, FitsMetadataValues.IMAGE_HEIGHT, metadata.getColumn(DPXColumn.LINES_PER_IMAGE_ELEMENT).getCurrentValue());
		addSimpleElement(imageElement, FitsMetadataValues.IMAGE_PRODUCER, metadata.getColumn(DPXColumn.CREATOR).getCurrentValue());
		addSimpleElement(imageElement, FitsMetadataValues.IMAGE_WIDTH, metadata.getColumn(DPXColumn.PIXELS_PER_LINE).getCurrentValue());
		addSimpleElement(imageElement, FitsMetadataValues.ORIENTATION, metadata.getColumn(DPXColumn.IMAGE_ORIENTATION).getCurrentValue());
		addSimpleElement(imageElement, FitsMetadataValues.SCANNER_MODEL_NAME, metadata.getColumn(DPXColumn.INPUT_DEVICE_NAME).getCurrentValue());
		addSimpleElement(imageElement, FitsMetadataValues.SCANNER_MODEL_SERIAL_NO, metadata.getColumn(DPXColumn.INPUT_DEVICE_SERIAL_NUMBER).getCurrentValue());

		metadataElement.addContent(imageElement);

		addSimpleElement(videoElement, FitsMetadataValues.BIT_DEPTH, metadata.getColumn(DPXColumn.BIT_DEPTH_1).getCurrentValue()); // TODO
		addSimpleElement(videoElement, FitsMetadataValues.FRAME_RATE, metadata.getColumn(DPXColumn.TEMPORAL_SAMPLING_RATE).getCurrentValue());
		addSimpleElement(videoElement, FitsMetadataValues.IMAGE_HEIGHT, metadata.getColumn(DPXColumn.LINES_PER_IMAGE_ELEMENT).getCurrentValue()); // TODO
		addSimpleElement(videoElement, FitsMetadataValues.IMAGE_WIDTH, metadata.getColumn(DPXColumn.PIXELS_PER_LINE).getCurrentValue()); // TODO
		addSimpleElement(videoElement, FitsMetadataValues.X_SAMPLING_FREQUENCY, metadata.getColumn(DPXColumn.HORIZONTAL_SAMPLING_RATE).getCurrentValue());
		addSimpleElement(videoElement, FitsMetadataValues.Y_SAMPLING_FREQUENCY, metadata.getColumn(DPXColumn.VERTICAL_SAMPLING_RATE).getCurrentValue());

		metadataElement.addContent(videoElement);

		return metadataElement;
	}

	private Document createRawData() {
		Document rawData = new Document();
		JSONObject dpxJson = JsonWriterDpx.createJsonFileObject(dpxFileInfo);
		Element jsonElement = new Element("embARC_JSON_Output");
		jsonElement.addContent(dpxJson.toString(2));
		rawData.addContent(jsonElement);
		return rawData;
	}

	private void addSimpleElement(Element parent, String tag, String value ) {
	    Element newElem = new Element(tag, fitsNS);
	    newElem.addContent(value);
	    parent.addContent(newElem);
	}
}

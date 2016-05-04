package edu.harvard.hul.ois.fits.tools.cad;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.jdom.Element;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Isaac Simmons on 8/27/2015.
 */
public class PdfExtractor extends CadExtractor {
    public static final String DEFAULT_MIMETYPE = "application/pdf";
    public static final String DEFAULT_FORMAT_NAME = "Portable Document Format";

    private final MagicNumberValidator validator = MagicNumberValidator.string("%PDF");

    public PdfExtractor() {
        super("pdf", ".pdf");
    }

    public static void pdfbox_validate(DataSource ds, CadToolResult result) throws IOException {
        ValidationResult validationResult;
        final PreflightParser parser = new PreflightParser(ds);
        PreflightDocument document = null;

        try {
            parser.parse();
            document = parser.getPreflightDocument();
            document.validate();
            validationResult = document.getResult();
            document.close();
        } catch (SyntaxValidationException e) {
            validationResult = e.getResult();
        } finally {
            if (document != null) {
                document.close();
            }
        }

        final Map<String, Map<String, Integer>> validationErrors = new HashMap<>();
        for (ValidationResult.ValidationError error : validationResult.getErrorsList()) {
            final String errorCode = error.getErrorCode();
            final String details = error.getDetails();

            Map<String, Integer> match = validationErrors.get(errorCode);
            if (match == null) {
                match = new HashMap<>();
                validationErrors.put(errorCode, match);
            }

            if (match.containsKey(details)) {
                match.put(details, match.get(details) + 1);
            } else {
                match.put(details, 1);
            }
        }
        for(Map.Entry<String, Map<String, Integer>> codeEntry: validationErrors.entrySet()) {
            final String errorCode = codeEntry.getKey();
            for(Map.Entry<String, Integer> detailEntry: codeEntry.getValue().entrySet()) {
                final Element element = new Element("pdf-a-validation-error");
                element.setAttribute("code", errorCode);
                element.setAttribute("details", detailEntry.getKey());
                element.setAttribute("count", Integer.toString(detailEntry.getValue()));
                result.addElement(element);
            }
        }
    }

    @Override
    public CadToolResult run(DataSource ds, String filename) throws IOException, ValidationException {
        final CadToolResult result = new CadToolResult(name, filename);

        validator.validate(ds.getInputStream());

        result.mimetype = PdfExtractor.DEFAULT_MIMETYPE;
        result.formatName = PdfExtractor.DEFAULT_FORMAT_NAME;
        //TODO: doesn't look like PDF revision is available to me from pdfbox

        //TODO: better handling of decryption failures

        try (final InputStream in = ds.getInputStream()) {
            final PDDocument doc = PDDocument.load(in);
            final PDDocumentInformation info = doc.getDocumentInformation();
            try {
                result.title = info.getTitle();
                result.author = info.getAuthor();
                result.addKeyValue("subject", info.getSubject());
                result.addKeyValue("keywords", info.getKeywords());
                if (info.getCreator() != null && info.getProducer() != null) {
                    result.creatingApplicationName = info.getProducer() + "/" + info.getCreator();
                } else {
                    if (info.getCreator() != null) {
                        result.creatingApplicationName = info.getCreator();
                    }
                    if (info.getProducer() != null) {
                        result.creatingApplicationName = info.getProducer();
                    }
                }
                if (info.getCreationDate() != null) {
                    final DateFormat df = new SimpleDateFormat(CadTool.PREFERRED_DATE_FORMAT);
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    result.creationDate = df.format(info.getCreationDate().getTime());
                }
                if (info.getModificationDate() != null) {
                    final DateFormat df = new SimpleDateFormat(CadTool.PREFERRED_DATE_FORMAT);
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    result.modificationDate = df.format(info.getModificationDate().getTime());
                }
            } catch (IOException ex) {
                System.out.println("Trouble parsing pdf metadata: " + ex.getMessage());
            }
            final PDDocumentCatalog cat = doc.getDocumentCatalog();

            for(COSObject o: doc.getDocument().getObjects()) {
                final COSBase item = o.getObject();
                if (item instanceof COSStream) {
                    final COSStream stream = (COSStream) item;
                    if (stream.containsKey(COSName.TYPE) && "3D".equals(stream.getNameAsString(COSName.TYPE))) {
                        final Element streamElement = new Element("embedded-3d-content");
                        if (stream.containsKey(COSName.SUBTYPE)) {
                            streamElement.setAttribute("type", stream.getNameAsString(COSName.SUBTYPE));
                        }
                        streamElement.setAttribute("bytes", Long.toString(stream.getFilteredLength()));
                        result.addElement(streamElement);
                        //TODO: actually pull the stream itself and decode it?
                    }
                }
            }

            //TODO: File attachments?

            boolean annotationPresent = false;
            pageloop: for (Object o: cat.getAllPages()) {
                if (o instanceof PDPage) {
                    final PDPage page = (PDPage) o;
                    for (PDAnnotation annotation: page.getAnnotations()) {
                        if ("3D".equals(annotation.getSubtype())) {
                            annotationPresent = true;
                            break pageloop;
                        }
                    }
                }
            }
            result.addKeyValue("pdf-3d-annotation", Boolean.toString(annotationPresent));
            doc.close();
        }
        pdfbox_validate(ds, result);
        return result;
    }
}

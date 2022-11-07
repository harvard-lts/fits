import org.apache.commons.configuration.ConfigurationException;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;
import uk.gov.nationalarchives.droid.container.httpservice.ContainerSignatureHttpService;
import uk.gov.nationalarchives.droid.core.interfaces.config.DroidGlobalConfig;
import uk.gov.nationalarchives.droid.core.interfaces.signature.SignatureType;
import uk.gov.nationalarchives.droid.signature.PronomSignatureService;
import uk.gov.nationalarchives.droid.signature.SignatureManagerImpl;
import uk.gov.nationalarchives.pronom.PronomService;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static uk.gov.nationalarchives.droid.core.interfaces.config.RuntimeConfig.DROID_TEMP_DIR;
import static uk.gov.nationalarchives.droid.core.interfaces.config.RuntimeConfig.DROID_USER;

/**
 * Updates the copies of the DROID signature files that are located in tools/droid, and updates all of the project's
 * fits.xml files to reference the new files. Additionally, FITS by default uses a slightly modified binary signature
 * file, see tools/droid/README.txt for details. These modifications are automatically applied to the new file.
 * <p>
 * The signature files are under source control and should be committed after updating them.
 * <p>
 * This script requires Java 11 and the dependencies defined in tool-poms/droid-sig-pom.xml to run.
 */
public class DroidSigFileUpdater {

    private static final Path ROOT = Paths.get("");
    private static final Path DROID_TEMP = ROOT.resolve("target/droid-temp");
    private static final Path TARGET_DIR = ROOT.resolve("tools/droid");
    private static final Path FITS_XML = ROOT.resolve("xml/fits.xml");
    private static final Path TEST_FITS_XML_DIR = ROOT.resolve("testfiles/properties");

    private static final Pattern SIG_FILE_PATTERN = Pattern.compile(
            "(DROID_SignatureFile_V(\\d+)(?:_Alt)?\\.xml|container-signature-(\\d+)\\.xml)");

    private static final Pattern BIN_REPLACE = Pattern.compile(
            "<droid_sigfile>.+</droid_sigfile>");
    private static final Pattern CONTAINER_REPLACE = Pattern.compile(
            "<droid_container_sigfile>.+</droid_container_sigfile>");

    /**
     * Maps FileFormat IDs to lists of IDs that the format should take priority over.
     * See tools/droid/README.txt for details
     */
    private static final Map<Integer, List<Integer>> SIG_MODS = Map.of(
            163, List.of(1893, 2418),
            1798, List.of(1983)
    );

    public static void main(String[] args) throws Exception {
        cleanDir(DROID_TEMP);
        new DroidSigFileUpdater().updateSignatureFiles();
    }

    private final XPathFactory xPathFactory;

    public DroidSigFileUpdater() {
        this.xPathFactory = XPathFactory.instance();
    }

    private void updateSignatureFiles() throws Exception {
        var sigManager = createSigManager();

        printf("Downloading latest DROID signature files");
        var binarySig = sigManager.downloadLatest(SignatureType.BINARY);
        var binarySigFile = binarySig.getFile();
        var containerSig = sigManager.downloadLatest(SignatureType.CONTAINER);

        printf("Installing binary sig file version %s", binarySig.getVersion());
        printf("Installing container sig file version %s", containerSig.getVersion());

        deleteOldSigFiles();

        var destBinarySigFile = Files.copy(binarySig.getFile(), TARGET_DIR.resolve(binarySigFile.getFileName()));
        var destContainerSigFile = Files.copy(containerSig.getFile(), TARGET_DIR.resolve(containerSig.getFile().getFileName()));
        var destAltBinarySigFile = writeAltBinarySigFile(destBinarySigFile);

        updateAllFitsXml(destAltBinarySigFile.getFileName().toString(),
                destContainerSigFile.getFileName().toString());
    }

    private void updateAllFitsXml(String binaryName, String containerName) throws IOException {
        updateSigFileReferences(FITS_XML, binaryName, containerName);
        try (var files = Files.list(TEST_FITS_XML_DIR)) {
            files.filter(f -> f.getFileName().toString().endsWith(".xml"))
                    .forEach(file -> {
                        try {
                            updateSigFileReferences(file, binaryName, containerName);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private void updateSigFileReferences(Path file, String binaryName, String containerName) throws IOException {
        printf("Updating signature references in %s", file);
        var contents = Files.readString(file);

        contents = BIN_REPLACE.matcher(contents)
                .replaceAll(String.format("<droid_sigfile>%s</droid_sigfile>", binaryName));
        contents = CONTAINER_REPLACE.matcher(contents)
                .replaceAll(String.format("<droid_container_sigfile>%s</droid_container_sigfile>", containerName));

        Files.writeString(file, contents, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void deleteOldSigFiles() throws IOException {
        try (var files = Files.list(TARGET_DIR)) {
            files.filter(f -> SIG_FILE_PATTERN.matcher(f.getFileName().toString()).matches())
                    .forEach(f -> {
                        try {
                            printf("Deleting old sig file: %s", f);
                            Files.deleteIfExists(f);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private SignatureManagerImpl createSigManager() throws IOException, ConfigurationException {
        System.setProperty(DROID_USER, DROID_TEMP.toAbsolutePath().toString());
        System.setProperty(DROID_TEMP_DIR, DROID_TEMP.toAbsolutePath().toString());

        var config = new DroidGlobalConfig();
        config.init();

        var pronomServiceFactory = new JaxWsProxyFactoryBean();
        pronomServiceFactory.setAddress("http://www.nationalarchives.gov.uk/pronom/service.asmx");

        var binaryService = new PronomSignatureService(pronomServiceFactory.create(PronomService.class),
                "DROID_SignatureFile_V%s.xml");
        binaryService.init(config);

        var containerService = new ContainerSignatureHttpService();
        containerService.init(config);

        return new SignatureManagerImpl(config, Map.of(
                SignatureType.BINARY, binaryService,
                SignatureType.CONTAINER, containerService
        ));
    }

    private Path writeAltBinarySigFile(Path binarySigFile) throws JDOMException, IOException {
        var altBinarySigFile = binarySigFile.getParent().resolve(binarySigFile.getFileName().toString()
                .replace(".xml", "_Alt.xml"));

        var builder = new SAXBuilder();
        var document = builder.build(binarySigFile.toFile());

        SIG_MODS.forEach((id, priorities) -> {
            var element = findFormat(document, id);
            priorities.forEach(oid -> {
                addPriorityOver(element, oid);
            });
        });

        var serializer = new XMLOutputter(Format.getPrettyFormat());
        try (Writer out = Files.newBufferedWriter(altBinarySigFile)) {
            serializer.output(document, out);
        }

        return altBinarySigFile;
    }

    private Element findFormat(Document document, int id) {
        var query = "//ns:FileFormat[@ID='" + id + "']";
        var expr = xPathFactory.compile(query, Filters.element(),
                null, Namespace.getNamespace("ns", document.getRootElement().getNamespace().getURI()));
        return expr.evaluateFirst(document);
    }

    private void addPriorityOver(Element element, int id) {
        element.addContent(new Element("HasPriorityOverFileFormatID",
                element.getNamespace()).setText(String.valueOf(id)));
    }

    private static void cleanDir(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectories(path);
            return;
        }

        try (var files = Files.walk(path)) {
            files.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            Files.createDirectories(path);
        }
    }

    private static void printf(String format, Object... args) {
        System.out.printf(format + "%n", args);
    }

}

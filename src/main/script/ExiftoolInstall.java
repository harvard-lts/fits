import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.Objects;
import java.util.Properties;

/**
 * Downloads and extracts exiftool to tools/exiftool. The version to install is configured in tools.properties.
 * If the currently installed version is equal to the version defined in tools.properties, then nothing happens.
 * Otherwise, the existing version is deleted and a new version is installed.
 * <p>
 * This script requires Java 11+ and Apache Commons Compress to extract the exiftool distribution archives.
 */
public class ExiftoolInstall {

    private static final Path PROJECT_ROOT = Paths.get("");
    private static final Path PROPS_FILE = PROJECT_ROOT.resolve("tools.properties");
    private static final Path TOOL_DIR = PROJECT_ROOT.resolve("tools/exiftool");
    private static final Path TOOL_VERSION_FILE = TOOL_DIR.resolve("version");
    private static final Path TOOL_UNIX_DIR = TOOL_DIR.resolve("perl");
    private static final Path TOOL_WINDOWS_DIR = TOOL_DIR.resolve("windows");

    private static final String PROP_PREFIX = "exiftool.";
    private static final String UNIX_PREFIX = PROP_PREFIX + "unix.";
    private static final String WINDOWS_PREFIX = PROP_PREFIX + "windows.";
    private static final String VERSION_PROP = PROP_PREFIX + "version";
    private static final String UNIX_URL_PROP = UNIX_PREFIX + "url";
    private static final String UNIX_MD5_PROP = UNIX_PREFIX + "md5";
    private static final String WINDOWS_URL_PROP = WINDOWS_PREFIX + "url";
    private static final String WINDOWS_MD5_PROP = WINDOWS_PREFIX + "md5";

    public static void main(String[] args) throws IOException {
        var props = new Properties();
        try (var is = Files.newInputStream(PROPS_FILE)) {
            props.load(is);
        }
        new ExiftoolInstall(props).execute();
    }

    private final Properties props;
    private final HttpClient httpClient;

    public ExiftoolInstall(Properties props) {
        this.props = props;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void execute() {
        var targetVersion = requireProp(VERSION_PROP);
        var currentVersion = readVersionFile();

        if (Objects.equals(targetVersion, currentVersion)) {
            printf("exiftool %s is already installed", targetVersion);
            System.exit(0);
        }

        printf("Installing exiftool %s", targetVersion);

        cleanToolDir();
        installUnix();
        installWindows();
        writeVersionFile(targetVersion);

        printf("Successfully installed exiftool %s", targetVersion);
    }

    private void installUnix() {
        var unixUrl = requireProp(UNIX_URL_PROP);
        var unixDigest = requireProp(UNIX_MD5_PROP);
        var unixFile = verifyChecksum(downloadArchive(unixUrl), unixDigest);
        var temp = TOOL_DIR.resolve("temp");
        extractTgz(unixFile, temp);
        try (var files = Files.list(temp)) {
            Files.move(files.findFirst().get(), TOOL_UNIX_DIR);
        } catch (Exception e) {
            throw new RuntimeException("Failed to install exiftool", e);
        }
        deleteFile(temp);
        deleteFile(unixFile);
    }

    private void installWindows() {
        var windowsUrl = requireProp(WINDOWS_URL_PROP);
        var windowsDigest = requireProp(WINDOWS_MD5_PROP);
        var windowsFile = verifyChecksum(downloadArchive(windowsUrl), windowsDigest);
        extractZip(windowsFile, TOOL_WINDOWS_DIR);
        try {
            Files.move(TOOL_WINDOWS_DIR.resolve("exiftool(-k).exe"), TOOL_WINDOWS_DIR.resolve("exiftool.exe"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to install exiftool", e);
        }
        deleteFile(windowsFile);
    }

    private void cleanToolDir() {
        try {
            if (Files.notExists(TOOL_DIR)) {
                Files.createDirectories(TOOL_DIR);
                return;
            }

            try (var files = Files.walk(TOOL_DIR)) {
                files.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                Files.createDirectories(TOOL_DIR);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to clean " + TOOL_DIR, e);
        }
    }

    private Path downloadArchive(String url) {
        var destination = TOOL_DIR.resolve(url.substring(url.lastIndexOf('/') + 1));
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        try {
            var response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofFile(destination));
            if (response.statusCode() != 200) {
                throw new RuntimeException(String.format(
                        "Failed to download file at %s. Status code: %s", url, response.statusCode()));
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file at " + url, e);
        }
    }

    private Path verifyChecksum(Path file, String expected) {
        try {
            var actual = MessageDigest.getInstance("MD5");
            try (var is = new DigestInputStream(new BufferedInputStream(Files.newInputStream(file)), actual)) {
                while (is.read() != -1) {
                    // nothing to do
                }
            }
            var digestBytes = actual.digest();
            var digestHex = new BigInteger(1, digestBytes).toString(16);
            if (!expected.equalsIgnoreCase(digestHex)) {
                throw new RuntimeException(String.format(
                        "MD5 checksum verification failed for %s. Expected: %s; Actual: %s", file, expected, digestHex));
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify checksum of file " + file, e);
        }
    }

    private void extractTgz(Path source, Path destination) {
        try (var is = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(
                Files.newInputStream(source))))) {
            extractArchive(is, destination);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract archive " + source, e);
        }
    }

    private void extractZip(Path source, Path destination) {
        try (var is = new ZipArchiveInputStream(new BufferedInputStream(
                Files.newInputStream(source)))) {
            extractArchive(is, destination);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract archive " + source, e);
        }
    }

    private void extractArchive(ArchiveInputStream is, Path destination) throws IOException {
        Files.createDirectories(destination);
        ArchiveEntry entry;

        while ((entry = is.getNextEntry()) != null) {
            var file = destination.resolve(entry.getName());
            if (entry.isDirectory()) {
                Files.createDirectories(file);
            } else {
                Files.copy(is, file);
            }
        }
    }

    private void deleteFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file " + file, e);
        }
    }

    private void writeVersionFile(String version) {
        try {
            Files.writeString(TOOL_VERSION_FILE, version,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write version file.", e);
        }
    }

    private String readVersionFile() {
        try  {
            return Files.readString(TOOL_VERSION_FILE).trim();
        } catch (IOException e) {
            // This likely means the tool is not installed or needs to be reinstalled
            return "";
        }
    }

    private void printf(String format, Object... args) {
        System.out.format(format + "%n", args);
    }

    private String requireProp(String name) {
        var value = props.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing property " + name);
        }
        return value;
    }

}
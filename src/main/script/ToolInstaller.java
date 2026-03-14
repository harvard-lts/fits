import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;

import java.io.BufferedInputStream;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * This script is used to install non-Maven based tools into the tools directory. It currently supports exiftool,
 * MediaInfo, and the Windows file utility. the tool.properties file in the project root defines what versions of the
 * tools to install, as well as the locations to download the tools from. The pom file at tool-poms/tool-installer-pom.xml
 * describes the dependencies and execution of this script. It is expected to be run as part of mvn generate-resources,
 * but can be invoked manually so long as the dependencies are on the classpath.
 */
public class ToolInstaller {

    public enum Tool {
        EXIFTOOL("exiftool", "exiftool", "perl", "perl", "windows"),
        MEDIA_INFO("mediainfo", "mediainfo", "linux", "mac", "windows/64"),
        FILE("file", "file_utility_windows", null, null, ""),
        JPYLYZER("jpylyzer", "jpylyzer", "unix", "unix", "windows");

        private final String name;
        private final String toolDir;
        private final String linuxDir;
        private final String macDir;
        private final String windowsDir;

        Tool(String name, String toolDir, String linuxDir, String macDir, String windowsDir) {
            this.name = name;
            this.toolDir = toolDir;
            this.linuxDir = linuxDir;
            this.macDir = macDir;
            this.windowsDir = windowsDir;
        }

        public String prop(String suffix) {
            return name + "." + suffix;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Tool fromString(String value) {
            return Arrays.stream(values())
                    .filter(v -> v.name.equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Unknown tool: " + value + ". Options: " + Arrays.asList(values())));
        }
    }

    private static final Path ROOT = Paths.get("");
    private static final Path TOOLS_ROOT = ROOT.resolve("tools");
    private static final Path PROPS_FILE = ROOT.resolve("tools.properties");

    private static final String VERSION_FILE_NAME = "version";

    private static final String UNIX = "unix.";
    private static final String LINUX = "linux.";
    private static final String MAC = "mac.";
    private static final String WINDOWS = "windows.";

    private static final String VERSION_SUFFIX = "version";
    private static final String URL_SUFFIX = "url";
    private static final String MD5_SUFFIX = "md5";

    public static void main(String[] args) throws IOException {
        var tools = Arrays.stream(args)
                .map(Tool::fromString)
                .collect(Collectors.toList());

        var props = new Properties();
        try (var is = Files.newInputStream(PROPS_FILE)) {
            props.load(is);
        }

        for (var tool : tools) {
            new ToolInstaller(tool, props).execute();
        }
    }

    private final Tool tool;
    private final Properties props;
    private final Path toolDir;
    private final HttpClient httpClient;

    public ToolInstaller(Tool tool, Properties props) {
        this.tool = tool;
        this.props = props;
        this.toolDir = TOOLS_ROOT.resolve(tool.toolDir);
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public void execute() throws IOException {
        var targetVersion = requireProp(tool.prop(VERSION_SUFFIX));
        var currentVersion = readVersionFile();

        if (Objects.equals(targetVersion, currentVersion)) {
            printf("%s %s is already installed", tool, targetVersion);
            return;
        }

        printf("Installing %s %s", tool, targetVersion);

        cleanToolDir();

        switch (tool) {
            case EXIFTOOL:
                installExiftool();
                break;
            case MEDIA_INFO:
                installMediaInfo();
                break;
            case FILE:
                installFileUtility();
                break;
            case JPYLYZER:
                installJpylyzer();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported tool: " + tool);
        }

        writeVersionFile(targetVersion);

        printf("Successfully installed %s %s", tool, targetVersion);
    }

    private void installExiftool() throws IOException {
        installExiftoolUnix();
        installExiftoolWindows();
    }

    private void installMediaInfo() throws IOException {
        installMediaInfoMac();
        installMediaInfoWindows();
        installMediaInfoLinux();
    }

    private void installMediaInfoMac() throws IOException {
        var archive = downloadAndVerify(MAC);
        var temp = toolDir.resolve("temp");
        extractTarBzip2(archive, temp);
        var target = Files.createDirectories(toolDir.resolve(tool.macDir));

        moveFilesToDir(target, temp,
                List.of("MediaInfoLib/libmediainfo.0.dylib", "MediaInfoLib/ReadMe.txt",
                        "MediaInfoLib/Changes.txt", "MediaInfoLib/History.txt"));

        Files.move(target.resolve("libmediainfo.0.dylib"), target.resolve("libmediainfo.dylib"));

        deleteDir(temp);
        Files.deleteIfExists(archive);
    }

    private void installMediaInfoWindows() throws IOException {
        var archive = downloadAndVerify(WINDOWS);
        var temp = toolDir.resolve("temp");
        extractZip(archive, temp);
        var target = Files.createDirectories(toolDir.resolve(tool.windowsDir));

        moveFilesToDir(target, temp,
                List.of("MediaInfo.dll", "ReadMe.txt", "Developers/Changes.txt", "Developers/History.txt"));

        deleteDir(temp);
        Files.deleteIfExists(archive);
    }

    private void installMediaInfoLinux() throws IOException {
        var target = Files.createDirectories(toolDir.resolve(tool.linuxDir));

        // Install libmediainfo
        var archive = downloadAndVerify(LINUX);
        var temp = toolDir.resolve("temp");
        extractAr(archive, temp);
        extractTarZst(temp.resolve("data.tar.zst"), temp);

        Files.move(temp.resolve("usr/lib/x86_64-linux-gnu/libmediainfo.so.0.0.0"),
                target.resolve("libmediainfo.so.0"));

        deleteDir(temp);
        Files.deleteIfExists(archive);

        // Install libzen
        archive = downloadAndVerify(LINUX + "zen.");
        temp = toolDir.resolve("temp");
        extractAr(archive, temp);
        extractTarZst(temp.resolve("data.tar.zst"), temp);

        Path libzen;
        try (var files = Files.list(temp.resolve("usr/lib/x86_64-linux-gnu"))) {
            libzen = files.filter(file -> file.getFileName().toString().startsWith("libzen.so.0."))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Failed to find libzen shared library"));
        }

        Files.move(libzen, target.resolve("libzen.so.0"));

        deleteDir(temp);
        Files.deleteIfExists(archive);

        // Install docs
        archive = downloadAndVerify(LINUX + "source.");
        temp = toolDir.resolve("temp");
        extractTarGzip(archive, temp);

        var prefix = "MediaInfo_DLL_GNU_FromSource/MediaInfoLib/";
        moveFilesToDir(target, temp,
                List.of(prefix + "ReadMe.txt", prefix + "Changes.txt", prefix + "History_DLL.txt"));

        deleteDir(temp);
        Files.deleteIfExists(archive);
    }

    private void installExiftoolUnix() throws IOException {
        var archive = downloadAndVerify(UNIX);
        var temp = toolDir.resolve("temp");
        extractTarGzip(archive, temp);
        try (var files = Files.list(temp)) {
            Files.move(files.findFirst().get(), toolDir.resolve(tool.linuxDir));
        }
        Files.deleteIfExists(temp);
        Files.deleteIfExists(archive);
    }

    private void installExiftoolWindows() throws IOException {
        var archive = downloadAndVerify(WINDOWS);
        var targetDir = toolDir.resolve(tool.windowsDir);
        extractZip(archive, targetDir);
        Path exe = Files.walk(targetDir)
                .filter(p -> p.getFileName().toString().equalsIgnoreCase("exiftool(-k).exe"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No exe found!"));
        Files.move(exe, targetDir.resolve("exiftool.exe"));
        Files.deleteIfExists(archive);
    }

    private void installFileUtility() throws IOException {
        var target = toolDir.resolve(tool.windowsDir);

        // Install file utility
        var archive = downloadAndVerify(WINDOWS);
        extractZip(archive, target);
        Files.deleteIfExists(archive);

        // Install deps
        archive = downloadAndVerify(WINDOWS + "deps.");
        extractZip(archive, target);
        Files.deleteIfExists(archive);
    }

    private void installJpylyzer() throws IOException {
        installJpylyzerWindows();
        installJpylyzerUnix();
    }

    private void installJpylyzerWindows() throws IOException {
        var archive = downloadAndVerify(WINDOWS);
        var targetDir = Files.createDirectories(toolDir.resolve(tool.windowsDir));
        var temp = toolDir.resolve("temp");
        extractZip(archive, temp);
        moveContentsToDir(temp.resolve("jpylyzer"), targetDir);
        deleteDir(targetDir.resolve("doc"));
        deleteDir(targetDir.resolve("example_files"));
        deleteDir(targetDir.resolve("license"));
        deleteDir(temp);
        Files.deleteIfExists(archive);
    }

    private void installJpylyzerUnix() throws IOException {
        var archive = downloadAndVerify(UNIX);
        var targetDir = Files.createDirectories(toolDir.resolve(tool.linuxDir));
        var temp = toolDir.resolve("temp");
        extractAr(archive, temp);
        extractTarXz(temp.resolve("data.tar.xz"), temp);
        Files.move(temp.resolve("usr/lib/python3/dist-packages/jpylyzer"), targetDir.resolve("jpylyzer"));
        deleteDir(temp);
        Files.deleteIfExists(archive);
    }

    private Path downloadAndVerify(String osPart) {
        var url = requireProp(tool.prop(osPart + URL_SUFFIX));
        var digest = requireProp(tool.prop(osPart + MD5_SUFFIX));
        return verifyChecksum(downloadArchive(url), digest);
    }

    private Path downloadArchive(String url) {
        var destination = toolDir.resolve(url.substring(url.lastIndexOf('/') + 1));
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

    private void extractTarBzip2(Path source, Path destination) {
        try (var is = new TarArchiveInputStream(new BZip2CompressorInputStream(new BufferedInputStream(
                Files.newInputStream(source))))) {
            extractArchive(is, destination);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract archive " + source, e);
        }
    }

    private void extractTarZst(Path source, Path destination) {
        try (var is = new TarArchiveInputStream(new ZstdCompressorInputStream(new BufferedInputStream(
                Files.newInputStream(source))))) {
            extractArchive(is, destination);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract archive " + source, e);
        }
    }

    private void extractTarXz(Path source, Path destination) {
        try (var is = new TarArchiveInputStream(new XZCompressorInputStream(new BufferedInputStream(
                Files.newInputStream(source))))) {
            extractArchive(is, destination);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract archive " + source, e);
        }
    }

    private void extractTarGzip(Path source, Path destination) {
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

    private void extractAr(Path source, Path destination) {
        try (var is = new ArArchiveInputStream(new BufferedInputStream(
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
                // This create is weirdly necessary because one of the file utility zips is malformed
                Files.createDirectories(file.getParent());
                Files.copy(is, file);
            }
        }
    }

    private void moveFilesToDir(Path target, Path source, List<String> sourceFiles) throws IOException {
        for (var file : sourceFiles) {
            var sourceFile = source.resolve(file);
            Files.move(sourceFile, target.resolve(sourceFile.getFileName()));
        }
    }

    private void moveContentsToDir(Path source, Path target) throws IOException {
        try (var files = Files.list(source)) {
            files.forEach(file -> {
                try {
                    Files.move(file, target.resolve(file.getFileName()));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    private void cleanToolDir() {
        try {
            deleteDir(toolDir);
            Files.createDirectories(toolDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to clean " + toolDir, e);
        }
    }

    private void deleteDir(Path dir) {
        if (Files.exists(dir)) {
            try (var files = Files.walk(dir)) {
                files.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private void writeVersionFile(String version) {
        try {
            Files.writeString(toolDir.resolve(VERSION_FILE_NAME), version,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write version file.", e);
        }
    }

    private String readVersionFile() {
        try  {
            return Files.readString(toolDir.resolve(VERSION_FILE_NAME)).trim();
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
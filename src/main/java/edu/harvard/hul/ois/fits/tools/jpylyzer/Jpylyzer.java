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

package edu.harvard.hul.ois.fits.tools.jpylyzer;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jpylyzer extends ToolBase {

    private static final Logger log = LoggerFactory.getLogger(Jpylyzer.class);

    private static final String TOOL_NAME = "jpylyzer";
    private static final Path TOOL_ROOT = Paths.get(Fits.FITS_TOOLS_DIR + TOOL_NAME);
    private static final Path UNIX_ROOT = TOOL_ROOT.resolve("unix");
    private static final Path WINDOWS_ROOT = TOOL_ROOT.resolve("windows");
    private static final String XSLT_FILE = Fits.FITS_XML_DIR + TOOL_NAME + File.separator + "jpylyzer_to_fits.xslt";

    private final Fits fits;
    private boolean enabled = true;
    private final Path workingDir;
    private final List<String> command;

    public Jpylyzer(Fits fits) throws FitsException {
        super();
        log.debug("Initializing jpylyzer");

        this.fits = fits;
        info = new ToolInfo();
        info.setName(TOOL_NAME);

        String osName = System.getProperty("os.name");

        if (osName.startsWith("Windows")) {
            workingDir = WINDOWS_ROOT;
            command = List.of(
                    WINDOWS_ROOT.resolve("jpylyzer.exe").toAbsolutePath().toString());
            info.setNote("jpylyzer for windows");
            log.debug("jpylyzer will use Windows environment");
        } else if (isPythonAvailable()) {
            workingDir = UNIX_ROOT;
            command = List.of("python", "-m", "jpylyzer");
            info.setNote("jpylyzer for unix");
            log.debug("jpylyzer will use Unix Python environment");
        } else {
            log.error("Python and Windows not supported, not running jpylyzer");
            throw new FitsToolException("jpylyzer cannot be used on this system");
        }

        List<String> infoCommand = new ArrayList<>(command);
        infoCommand.add("-v");
        String versionOutput =
                CommandLine.exec(infoCommand, workingDir.toAbsolutePath().toString());
        info.setVersion(versionOutput.trim());
    }

    private static boolean isPythonAvailable() {
        try {
            String output = CommandLine.exec(List.of("python", "--version"), null);
            if (output.startsWith("Python")) {
                log.info("Found: {}", output);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.debug("Failed to execute to detect python version", e);
            return false;
        }
    }

    @Override
    public ToolOutput extractInfo(File file) throws FitsToolException {
        log.debug("jpylyzer starting on {}", file.getName());
        long start = System.currentTimeMillis();

        List<String> execCommand = new ArrayList<>(command);
        execCommand.add(file.getAbsolutePath());

        log.debug("Launching jpylyzer, command = {}", execCommand);
        String execOut =
                CommandLine.exec(execCommand, workingDir.toAbsolutePath().toString(), false);
        log.debug("Finished running jpylyzer");

        Document rawOut = parse(execOut);
        Document fitsXml = transform(XSLT_FILE, rawOut);

        output = new ToolOutput(this, fitsXml, rawOut, fits);

        duration = System.currentTimeMillis() - start;
        runStatus = RunStatus.SUCCESSFUL;
        log.debug("jpylyzer finished on {} in {}ms", file.getName(), duration);
        return output;
    }

    private Document parse(String output) throws FitsToolException {
        try {
            return saxBuilder.build(new StringReader(output));
        } catch (Exception e) {
            throw new FitsToolException("Error parsing jpylyzer XML Output", e);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

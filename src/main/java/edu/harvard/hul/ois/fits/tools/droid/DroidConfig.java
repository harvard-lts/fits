//
// Copyright (c) 2023 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.droid;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.XMLConfiguration;

class DroidConfig {

    private Path sigFile;
    private Path containerSigFile;
    private Path tempDir;
    private Set<String> extsToLimitBytesRead = Collections.emptySet();
    private long byteReadLimit = -1;
    private boolean processZip = true;
    private boolean processTar = true;
    private boolean processGzip = true;
    private boolean processRar = true;
    private boolean process7zip = true;
    private boolean processIso = true;
    private boolean processBzip2 = true;
    private boolean processArc = true;
    private boolean processWarc = true;

    public static DroidConfig fromFitsConfig(XMLConfiguration fitsConfig) throws FitsToolException {
        var droidConfig = new DroidConfig();

        Path droidConfigDir = Paths.get(Fits.FITS_TOOLS_DIR + "droid");
        droidConfig.setSigFile(droidConfigDir.resolve(fitsConfig.getString("droid_sigfile")));
        droidConfig.setContainerSigFile(droidConfigDir.resolve(fitsConfig.getString("droid_container_sigfile")));

        String tempStr = fitsConfig.getString("process.tmpdir", System.getProperty("java.io.tmpdir"));
        droidConfig.setTempDir(tempStr == null ? null : Paths.get(tempStr));

        droidConfig.setExtsToLimitBytesRead(
                new HashSet<>((List<String>) (List<?>) fitsConfig.getList("droid_read_limit[@include-exts]")));
        String limit = fitsConfig.getString("droid_read_limit[@read-limit-kb]");
        long kbReadLimit = -1L;
        if (limit != null) {
            try {
                kbReadLimit = Long.parseLong(limit);
            } catch (NumberFormatException nfe) {
                throw new FitsToolException(
                        "Invalid long value in fits.xml droid_read_limit[@read-limit-kb]: " + limit, nfe);
            }
        }

        droidConfig.setByteReadLimit(kbReadLimit == -1 ? -1 : 1024 * kbReadLimit);

        droidConfig.setProcessZip(fitsConfig.getBoolean("droid.process.zip", true));
        droidConfig.setProcessTar(fitsConfig.getBoolean("droid.process.tar", true));
        droidConfig.setProcessGzip(fitsConfig.getBoolean("droid.process.gzip", true));
        droidConfig.setProcessArc(fitsConfig.getBoolean("droid.process.arc", true));
        droidConfig.setProcessWarc(fitsConfig.getBoolean("droid.process.warc", true));
        droidConfig.setProcessBzip2(fitsConfig.getBoolean("droid.process.bzip2", true));
        droidConfig.setProcess7zip(fitsConfig.getBoolean("droid.process.seven-zip", true));
        droidConfig.setProcessIso(fitsConfig.getBoolean("droid.process.iso", true));
        droidConfig.setProcessRar(fitsConfig.getBoolean("droid.process.rar", true));

        return droidConfig;
    }

    public Path getSigFile() {
        return sigFile;
    }

    public DroidConfig setSigFile(Path sigFile) {
        this.sigFile = sigFile;
        return this;
    }

    public Path getContainerSigFile() {
        return containerSigFile;
    }

    public DroidConfig setContainerSigFile(Path containerSigFile) {
        this.containerSigFile = containerSigFile;
        return this;
    }

    public Path getTempDir() {
        return tempDir;
    }

    public DroidConfig setTempDir(Path tempDir) {
        this.tempDir = tempDir;
        return this;
    }

    public Set<String> getExtsToLimitBytesRead() {
        return extsToLimitBytesRead;
    }

    public DroidConfig setExtsToLimitBytesRead(Set<String> extsToLimitBytesRead) {
        this.extsToLimitBytesRead = extsToLimitBytesRead;
        return this;
    }

    public long getByteReadLimit() {
        return byteReadLimit;
    }

    public DroidConfig setByteReadLimit(long byteReadLimit) {
        this.byteReadLimit = byteReadLimit;
        return this;
    }

    public boolean isProcessZip() {
        return processZip;
    }

    public DroidConfig setProcessZip(boolean processZip) {
        this.processZip = processZip;
        return this;
    }

    public boolean isProcessTar() {
        return processTar;
    }

    public DroidConfig setProcessTar(boolean processTar) {
        this.processTar = processTar;
        return this;
    }

    public boolean isProcessGzip() {
        return processGzip;
    }

    public DroidConfig setProcessGzip(boolean processGzip) {
        this.processGzip = processGzip;
        return this;
    }

    public boolean isProcessRar() {
        return processRar;
    }

    public DroidConfig setProcessRar(boolean processRar) {
        this.processRar = processRar;
        return this;
    }

    public boolean isProcess7zip() {
        return process7zip;
    }

    public DroidConfig setProcess7zip(boolean process7zip) {
        this.process7zip = process7zip;
        return this;
    }

    public boolean isProcessIso() {
        return processIso;
    }

    public DroidConfig setProcessIso(boolean processIso) {
        this.processIso = processIso;
        return this;
    }

    public boolean isProcessBzip2() {
        return processBzip2;
    }

    public DroidConfig setProcessBzip2(boolean processBzip2) {
        this.processBzip2 = processBzip2;
        return this;
    }

    public boolean isProcessArc() {
        return processArc;
    }

    public DroidConfig setProcessArc(boolean processArc) {
        this.processArc = processArc;
        return this;
    }

    public boolean isProcessWarc() {
        return processWarc;
    }

    public DroidConfig setProcessWarc(boolean processWarc) {
        this.processWarc = processWarc;
        return this;
    }
}

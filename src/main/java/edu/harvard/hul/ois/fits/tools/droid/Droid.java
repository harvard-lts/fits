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
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.droid.command.action.VersionCommand;
import uk.gov.nationalarchives.droid.container.ContainerFileIdentificationRequestFactory;
import uk.gov.nationalarchives.droid.container.ContainerSignatureDefinitions;
import uk.gov.nationalarchives.droid.container.ContainerSignatureFileReader;
import uk.gov.nationalarchives.droid.container.ole2.Ole2Identifier;
import uk.gov.nationalarchives.droid.container.ole2.Ole2IdentifierEngine;
import uk.gov.nationalarchives.droid.container.zip.ZipIdentifier;
import uk.gov.nationalarchives.droid.container.zip.ZipIdentifierEngine;
import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveFormatResolver;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveFormatResolverImpl;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ContainerIdentifierFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ContainerIdentifierFactoryImpl;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;
import uk.gov.nationalarchives.droid.signature.SaxSignatureFileParser;
import uk.gov.nationalarchives.droid.signature.SignatureParser;

/**  The principal glue class for invoking DROID under FITS.
 */
public class Droid extends ToolBase {

    private boolean enabled = true;
    private final Fits fits;
    private final List<String> includeExts;
    private long kbReadLimit;

    private static File sigFile;
    private static final BinarySignatureIdentifier sigIdentifier = new BinarySignatureIdentifier();
    private static final ContainerIdentifierFactory containerIdentifierFactory = new ContainerIdentifierFactoryImpl();
    private static final ArchiveFormatResolver containerFormatResolver = new ArchiveFormatResolverImpl();
    private static ContainerSignatureDefinitions containerSignatureDefinitions;
    private static final Map<String, Format> puidFormatMap = new HashMap<>(2500);

    private static final List<String> CONTAINER_TYPE_MIMETYPES = Arrays.asList("application/zip");

    private static final Logger logger = LoggerFactory.getLogger(Droid.class);

    public Droid(Fits fits) throws FitsToolException {
        super();
        this.fits = fits;
        logger.debug("Initializing Droid");
        info = new ToolInfo("Droid", getDroidVersion(), null);

        try {
            String droid_conf = Fits.FITS_TOOLS_DIR + "droid" + File.separator;
            XMLConfiguration config = fits.getConfig();
            // only need a single Droid signature file.
            if (sigFile == null) {
                synchronized (this) {
                    if (sigFile == null) {
                        sigFile = new File(droid_conf + config.getString("droid_sigfile"));
                        sigIdentifier.setSignatureFile(sigFile.getAbsolutePath());
                        sigIdentifier.init();

                        // The following is necessary to init the code that identifies formats like docx, xlsx, etc
                        SignatureParser sigParser = new SaxSignatureFileParser(sigFile.toURI());
                        sigParser.formats(format -> {
                            puidFormatMap.put(format.getPuid(), format);
                        });

                        String containerSigFile = droid_conf + config.getString("droid_container_sigfile");
                        ContainerSignatureFileReader signatureReader =
                                new ContainerSignatureFileReader(containerSigFile);

                        containerSignatureDefinitions = signatureReader.getDefinitions();

                        ZipIdentifierEngine zipIdentifierEngine = new ZipIdentifierEngine();
                        zipIdentifierEngine.setRequestFactory(new ContainerFileIdentificationRequestFactory());

                        ZipIdentifier zipIdentifier = new ZipIdentifier();
                        zipIdentifier.setContainerType("ZIP");
                        zipIdentifier.setContainerIdentifierFactory(containerIdentifierFactory);
                        zipIdentifier.setContainerFormatResolver(containerFormatResolver);
                        zipIdentifier.setDroidCore(sigIdentifier);
                        zipIdentifier.setIdentifierEngine(zipIdentifierEngine);
                        zipIdentifier.setSignatureReader(signatureReader);
                        zipIdentifier.init();

                        Ole2IdentifierEngine ole2IdentifierEngine = new Ole2IdentifierEngine();
                        ole2IdentifierEngine.setRequestFactory(new ContainerFileIdentificationRequestFactory());

                        Ole2Identifier ole2Identifier = new Ole2Identifier();
                        ole2Identifier.setContainerType("OLE2");
                        ole2Identifier.setContainerIdentifierFactory(containerIdentifierFactory);
                        ole2Identifier.setContainerFormatResolver(containerFormatResolver);
                        ole2Identifier.setDroidCore(sigIdentifier);
                        ole2Identifier.setIdentifierEngine(ole2IdentifierEngine);
                        ole2Identifier.setSignatureReader(signatureReader);
                        ole2Identifier.init();
                    }
                }
            }
            includeExts = (List<String>) (List<?>) config.getList("droid_read_limit[@include-exts]");
            String limit = config.getString("droid_read_limit[@read-limit-kb]");
            kbReadLimit = -1l;
            if (limit != null) {
                try {
                    kbReadLimit = Long.parseLong(limit);
                } catch (NumberFormatException nfe) {
                    throw new FitsToolException(
                            "Invalid long value in fits.xml droid_read_limit[@read-limit-kb]: " + limit, nfe);
                }
            }
        } catch (Throwable e) {
            throw new FitsToolException("Error initilizing DROID", e);
        }
    }

    @Override
    public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("Droid.extractInfo starting on " + file.getName());
        long startTime = System.currentTimeMillis();
        IdentificationResultCollection results;
        ContainerAggregator aggregator = null;
        try {
            DroidQuery droidQuery = new DroidQuery(
                    sigIdentifier,
                    containerIdentifierFactory,
                    containerFormatResolver,
                    puidFormatMap,
                    containerSignatureDefinitions,
                    includeExts,
                    kbReadLimit,
                    file);
            // the following will almost always return a single result
            results = droidQuery.queryFile();
            for (IdentificationResult res : results.getResults()) {
                String mimeType = res.getMimeType();

                if (FitsMetadataValues.getInstance().normalizeMimeType(mimeType) != null) {
                    mimeType = FitsMetadataValues.getInstance().normalizeMimeType(mimeType);
                }

                String fileName = file.getName();
                int lastDot = fileName.lastIndexOf('.');
                String extension = "";
                if (lastDot > -1) {
                    extension = fileName.substring(lastDot + 1);
                }

                if (CONTAINER_TYPE_MIMETYPES.contains(mimeType) && "zip".equals(extension)) {
                    aggregator = droidQuery.queryContainerData(results);
                }
            }

        } catch (IOException e) {
            throw new FitsToolException("DROID can't query file " + file.getAbsolutePath(), e);
        } catch (SignatureParseException e) {
            throw new FitsToolException("Problem with DROID signature file");
        }
        DroidToolOutputter outputter = new DroidToolOutputter(this, results, fits, aggregator);
        ToolOutput output = outputter.toToolOutput();

        duration = System.currentTimeMillis() - startTime;
        runStatus = RunStatus.SUCCESSFUL;
        logger.debug("Droid.extractInfo finished on " + file.getName());
        return output;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    private String getDroidVersion() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        VersionCommand vcmd = new VersionCommand(pw);
        try {
            vcmd.execute();
        } catch (Exception e) {
            return "(Version unknown)";
        }
        return sw.toString().trim();
    }
}

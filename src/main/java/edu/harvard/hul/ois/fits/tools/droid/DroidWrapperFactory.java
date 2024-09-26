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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import uk.gov.nationalarchives.droid.container.ContainerFileIdentificationRequestFactory;
import uk.gov.nationalarchives.droid.container.ContainerSignatureFileReader;
import uk.gov.nationalarchives.droid.container.ole2.Ole2Identifier;
import uk.gov.nationalarchives.droid.container.ole2.Ole2IdentifierEngine;
import uk.gov.nationalarchives.droid.container.zip.ZipIdentifier;
import uk.gov.nationalarchives.droid.container.zip.ZipIdentifierEngine;
import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArcArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveFormatResolverImpl;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveHandlerFactoryImpl;
import uk.gov.nationalarchives.droid.core.interfaces.archive.BZipArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.BZipRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ContainerIdentifierFactoryImpl;
import uk.gov.nationalarchives.droid.core.interfaces.archive.FatArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.FatEntryRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.GZipArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.GZipRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ISOEntryRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ISOImageArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.RarArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.RarEntryRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.SevenZipArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.SevenZipRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.TarArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.TarEntryRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.TrueVfsArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.WarcArchiveHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.WebArchiveEntryRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ZipEntryRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.control.PauseAspect;
import uk.gov.nationalarchives.droid.core.interfaces.signature.SignatureFileException;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;
import uk.gov.nationalarchives.droid.signature.SaxSignatureFileParser;
import uk.gov.nationalarchives.droid.signature.SignatureParser;
import uk.gov.nationalarchives.droid.submitter.SubmissionQueue;
import uk.gov.nationalarchives.droid.submitter.SubmissionQueueData;

/**
 * Factory for generating {@link DroidWrapper} instances. This is necessary because {@link DroidWrapper} is not thread
 * safe, but many of the components that it uses are expensive and can be shared between instances. The setup in this
 * class is based on Droid's <a href="https://github.com/digital-preservation/droid/blob/master/droid-results/src/main/resources/META-INF/spring-results.xml">spring-result.xml</a>.
 */
class DroidWrapperFactory {

    private static DroidWrapperFactory instance;

    /**
     * Creates a new DroidWrapperFactory instance if one does not exist, or returns the existing instance if one does.
     *
     * @param config the droid config
     * @return the DroidWrapperFactory
     * @throws SignatureParseException
     * @throws SignatureFileException
     */
    public static synchronized DroidWrapperFactory getOrCreateFactory(DroidConfig config)
            throws SignatureParseException, SignatureFileException {
        if (instance == null) {
            instance = new DroidWrapperFactory(Objects.requireNonNull(config, "config cannot be null"));
        }
        return instance;
    }

    private final DroidConfig config;
    private final Map<String, Format> puidFormatMap;
    private final BinarySignatureIdentifier droid;
    private final ContainerSignatureFileReader signatureFileReader;
    private final ContainerIdentifierFactoryImpl containerIdentifierFactory;
    private final ArchiveFormatResolverImpl containerPuidResolver;
    private final ZipIdentifier zipContainerHandler;
    private final Ole2Identifier ole2ContainerHandler;
    private final ArchiveFormatResolverImpl archivePuidResolver;
    private final ZipEntryRequestFactory zipFactory;
    private final TarEntryRequestFactory tarFactory;
    private final SevenZipRequestFactory sevenZipFactory;
    private final BZipRequestFactory bzipFactory;
    private final GZipRequestFactory gzipFactory;
    private final WebArchiveEntryRequestFactory arcFactory;
    private final WebArchiveEntryRequestFactory warcFactory;
    private final ISOEntryRequestFactory isoFactory;
    private final RarEntryRequestFactory rarFactory;
    private final FatEntryRequestFactory fatFactory;

    private DroidWrapperFactory(DroidConfig config) throws SignatureParseException, SignatureFileException {
        this.config = config;

        // The following is necessary to init the code that identifies formats like docx, xlsx, etc
        puidFormatMap = new HashMap<>();
        SignatureParser sigParser =
                new SaxSignatureFileParser(config.getSigFile().toUri());
        sigParser.formats(format -> {
            puidFormatMap.put(format.getPuid(), format);
        });

        droid = new BinarySignatureIdentifier();
        droid.setSignatureFile(config.getSigFile().toAbsolutePath().toString());
        droid.init();

        signatureFileReader = new ContainerSignatureFileReader();
        signatureFileReader.setFilePath(
                config.getContainerSigFile().toAbsolutePath().toString());

        containerIdentifierFactory = new ContainerIdentifierFactoryImpl();
        containerPuidResolver = new ArchiveFormatResolverImpl();
        var containerFileIdentificationRequestFactory = new ContainerFileIdentificationRequestFactory();

        var zipIdentifierEngine = new ZipIdentifierEngine();
        zipIdentifierEngine.setRequestFactory(containerFileIdentificationRequestFactory);

        var ole2IdentifierEngine = new Ole2IdentifierEngine();
        ole2IdentifierEngine.setRequestFactory(containerFileIdentificationRequestFactory);

        zipContainerHandler = new ZipIdentifier();
        zipContainerHandler.setContainerType("ZIP");
        zipContainerHandler.setContainerIdentifierFactory(containerIdentifierFactory);
        zipContainerHandler.setContainerFormatResolver(containerPuidResolver);
        zipContainerHandler.setDroidCore(droid);
        zipContainerHandler.setIdentifierEngine(zipIdentifierEngine);
        zipContainerHandler.setSignatureReader(signatureFileReader);
        zipContainerHandler.init();

        ole2ContainerHandler = new Ole2Identifier();
        ole2ContainerHandler.setContainerType("OLE2");
        ole2ContainerHandler.setContainerIdentifierFactory(containerIdentifierFactory);
        ole2ContainerHandler.setContainerFormatResolver(containerPuidResolver);
        ole2ContainerHandler.setDroidCore(droid);
        ole2ContainerHandler.setIdentifierEngine(ole2IdentifierEngine);
        ole2ContainerHandler.setSignatureReader(signatureFileReader);
        ole2ContainerHandler.init();

        archivePuidResolver = new ArchiveFormatResolverImpl();
        archivePuidResolver.setPuids(Map.of(
                "ZIP", "x-fmt/263",
                "TAR", "x-fmt/265",
                "GZ", "x-fmt/266",
                "ARC", "x-fmt/219, fmt/410",
                "WARC", "fmt/289, fmt/1281, fmt/1355",
                "BZ", "x-fmt/267, x-fmt/268",
                "7Z", "fmt/484",
                "ISO", "fmt/468, fmt/1739",
                "RAR", "x-fmt/264, fmt/411",
                "FAT", "fmt/1087"));

        zipFactory = new ZipEntryRequestFactory();
        zipFactory.setTempDirLocation(config.getTempDir());
        tarFactory = new TarEntryRequestFactory();
        tarFactory.setTempDirLocation(config.getTempDir());
        sevenZipFactory = new SevenZipRequestFactory();
        sevenZipFactory.setTempDirLocation(config.getTempDir());
        bzipFactory = new BZipRequestFactory();
        bzipFactory.setTempDirLocation(config.getTempDir());
        gzipFactory = new GZipRequestFactory();
        gzipFactory.setTempDirLocation(config.getTempDir());
        arcFactory = new WebArchiveEntryRequestFactory();
        arcFactory.setTempDirLocation(config.getTempDir());
        warcFactory = new WebArchiveEntryRequestFactory();
        warcFactory.setTempDirLocation(config.getTempDir());
        isoFactory = new ISOEntryRequestFactory();
        isoFactory.setTempDirLocation(config.getTempDir());
        rarFactory = new RarEntryRequestFactory();
        rarFactory.setTempDirLocation(config.getTempDir());
        fatFactory = new FatEntryRequestFactory();
        fatFactory.setTempDirLocation(config.getTempDir());
    }

    /**
     * Creates a new {@link DroidWrapper} instance. {@link DroidWrapper} is NOT THREAD SAFE.
     *
     * @return {@link DroidWrapper}
     */
    public DroidWrapper createInstance() {
        var submissionGateway = new RecursionRestrictedSubmissionGateway();
        submissionGateway.setDroidCore(droid);
        submissionGateway.setContainerFormatResolver(containerPuidResolver);
        submissionGateway.setContainerIdentifierFactory(containerIdentifierFactory);
        submissionGateway.setArchiveFormatResolver(archivePuidResolver);
        submissionGateway.setPauseAspect(new PauseAspect());
        submissionGateway.setSubmissionQueue(new NoOpSubmissionQueue());

        // We need these threads to be daemon threads so that an application that uses FITS can exit. FITS has not
        // historically required that users shut it down, so without this user application would hang.
        submissionGateway.setExecutorService(Executors.newSingleThreadExecutor(new ThreadFactory() {
            private final ThreadFactory delegate = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable runnable) {
                var thread = delegate.newThread(runnable);
                thread.setDaemon(true);
                return thread;
            }
        }));

        submissionGateway.setProcessZip(config.isProcessZip());
        submissionGateway.setProcessTar(config.isProcessTar());
        submissionGateway.setProcessGzip(config.isProcessGzip());
        submissionGateway.setProcessArc(config.isProcessArc());
        submissionGateway.setProcessWarc(config.isProcessWarc());
        submissionGateway.setProcessBzip2(config.isProcessBzip2());
        submissionGateway.setProcess7zip(config.isProcess7zip());
        submissionGateway.setProcessIso(config.isProcessIso());
        submissionGateway.setProcessRar(config.isProcessRar());

        var resultHandler = new CollectingResultHandler();

        submissionGateway.setResultHandler(resultHandler);

        var zipHandler = new TrueVfsArchiveHandler();
        zipHandler.setDroidCore(submissionGateway);
        zipHandler.setResultHandler(resultHandler);
        zipHandler.setFactory(zipFactory);

        var tarHandler = new TarArchiveHandler();
        tarHandler.setDroidCore(submissionGateway);
        tarHandler.setResultHandler(resultHandler);
        tarHandler.setFactory(tarFactory);

        var sevenZipHandler = new SevenZipArchiveHandler();
        sevenZipHandler.setDroid(submissionGateway);
        sevenZipHandler.setResultHandler(resultHandler);
        sevenZipHandler.setFactory(sevenZipFactory);

        var bzipHandler = new BZipArchiveHandler();
        bzipHandler.setDroidCore(submissionGateway);
        bzipHandler.setResultHandler(resultHandler);
        bzipHandler.setFactory(bzipFactory);

        var gzHandler = new GZipArchiveHandler();
        gzHandler.setDroidCore(submissionGateway);
        gzHandler.setFactory(gzipFactory);

        var arcHandler = new ArcArchiveHandler();
        arcHandler.setDroidCore(submissionGateway);
        arcHandler.setResultHandler(resultHandler);
        arcHandler.setFactory(arcFactory);

        var warcHandler = new WarcArchiveHandler();
        warcHandler.setDroidCore(submissionGateway);
        warcHandler.setResultHandler(resultHandler);
        warcHandler.setFactory(warcFactory);

        var isoHandler = new ISOImageArchiveHandler();
        isoHandler.setDroid(submissionGateway);
        isoHandler.setResultHandler(resultHandler);
        isoHandler.setFactory(isoFactory);

        var rarHandler = new RarArchiveHandler();
        rarHandler.setDroid(submissionGateway);
        rarHandler.setResultHandler(resultHandler);
        rarHandler.setIdentificationRequestFactory(rarFactory);

        var fatHandler = new FatArchiveHandler();
        fatHandler.setDroid(submissionGateway);
        fatHandler.setResultHandler(resultHandler);
        fatHandler.setFactory(fatFactory);

        var archiveHandlerLocator = new ArchiveHandlerFactoryImpl();
        archiveHandlerLocator.setHandlers(Map.of(
                "ZIP", zipHandler,
                "TAR", tarHandler,
                "GZ", gzHandler,
                "ARC", arcHandler,
                "WARC", warcHandler,
                "BZ", bzipHandler,
                "7Z", sevenZipHandler,
                "ISO", isoHandler,
                "RAR", rarHandler,
                "FAT", fatHandler));

        submissionGateway.setArchiveHandlerFactory(archiveHandlerLocator);

        return new DroidWrapper(
                submissionGateway,
                resultHandler,
                puidFormatMap,
                config.getExtsToLimitBytesRead(),
                config.getByteReadLimit());
    }

    private static class NoOpSubmissionQueue implements SubmissionQueue {
        @Override
        public void add(RequestIdentifier request) {
            // noop
        }

        @Override
        public void remove(RequestIdentifier request) {
            // noop
        }

        @Override
        public void save() {
            // noop
        }

        @Override
        public SubmissionQueueData list() {
            // noop
            return null;
        }
    }
}

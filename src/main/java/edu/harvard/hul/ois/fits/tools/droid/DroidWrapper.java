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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.FilenameUtils;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultImpl;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;
import uk.gov.nationalarchives.droid.submitter.SubmissionGateway;

/**
 * Submits a file to Droid to identify and returns the identification results for the submitted file and any files that
 * it contains.
 * <p>
 * The intended use is to construct one instance of this class per thread using the {@link DroidWrapperFactory}. This
 * allows the reuse of expensive, thread-safe components.
 * <p>
 * This class is NOT THREAD SAFE.
 */
class DroidWrapper {

    private final SubmissionGateway submissionGateway;
    private final CollectingResultHandler resultHandler;
    private final Map<String, Format> puidFormatMap;
    private final Set<String> extsToLimitBytesRead;
    private final long byteReadLimit;

    /**
     * @param submissionGateway the Droid entry point
     * @param resultHandler the handler for collecting identification results
     * @param puidFormatMap the map of puids to formats
     * @param extsToLimitBytesRead set of file extensions where the number of bytes read should be restricted
     * @param byteReadLimit the max number of bytes to read of files with byte restrictions
     */
    public DroidWrapper(
            SubmissionGateway submissionGateway,
            CollectingResultHandler resultHandler,
            Map<String, Format> puidFormatMap,
            Set<String> extsToLimitBytesRead,
            long byteReadLimit) {
        this.submissionGateway = Objects.requireNonNull(submissionGateway, "submissionGateway cannot be null");
        this.resultHandler = Objects.requireNonNull(resultHandler, "resultHandler cannot be null");
        this.puidFormatMap = Objects.requireNonNull(puidFormatMap, "puidFormatMap cannot be null");
        this.extsToLimitBytesRead = Objects.requireNonNull(extsToLimitBytesRead, "extsToLimitBytesRead cannot be null");
        this.byteReadLimit = byteReadLimit;
    }

    /**
     * Submits a file to be analyzed by Droid, and returns the identification results of the file and any files that
     * it contains.
     * <p>
     * Recursion of archive formats is restricted to a depth of 1.
     *
     * @param file the file to analyze
     * @return the identification results
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public DroidResult analyze(Path file) throws IOException, InterruptedException, ExecutionException {
        var bytesToRead = Files.size(file);
        var filename = file.getFileName().toString();
        var ext = FilenameUtils.getExtension(file.getFileName().toString());

        if (byteReadLimit > 0 && extsToLimitBytesRead.contains(ext)) {
            bytesToRead = Math.min(byteReadLimit, bytesToRead);
        }

        var meta =
                new RequestMetaData(bytesToRead, Files.getLastModifiedTime(file).toMillis(), filename);
        var id = new RequestIdentifier(file.toUri());
        id.setParentId(DroidId.nextId());
        id.setParentPrefix("");
        var request = new FileSystemIdentificationRequest(meta, id);

        try {
            request.open(file);

            resultHandler.reset();
            submissionGateway.submit(request).get();
            submissionGateway.awaitFinished();

            var results = resultHandler.getResults();

            results.forEach(this::augmentContainerResults);

            List<IdentificationResultCollection> innerResults =
                    results.size() == 1 ? Collections.emptyList() : results.subList(1, results.size());

            return new DroidResult(file, results.get(0), innerResults);
        } finally {
            request.close();
        }
    }

    /**
     * Closes the object and any underlying resources
     *
     * @throws IOException
     */
    public void close() throws IOException {
        // TODO DROID I think we need a Tool.close() method
        submissionGateway.close();
    }

    /**
     * Modifies the result objects to include mime type and version. This is necessary because, for some reason Droid
     * does not include this information for files that were identified by container signature.
     *
     * @param result the result to modify
     */
    private void augmentContainerResults(IdentificationResultCollection result) {
        result.getResults().stream().filter(r -> r.getMimeType() == null).forEach(r -> {
            var format = puidFormatMap.get(r.getPuid());
            if (format != null) {
                var ri = (IdentificationResultImpl) r;
                ri.setName(format.getName());
                ri.setMimeType(format.getMimeType());
                ri.setVersion(format.getVersion());
            }
        });
    }
}

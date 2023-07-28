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

// TODO DROID not thread safe
class DroidWrapper {

    private final SubmissionGateway submissionGateway;
    private final CollectingResultHandler resultHandler;
    private final Map<String, Format> puidFormatMap;
    private final Set<String> extsToLimitBytesRead;
    private final long byteReadLimit;

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
        id.setParentId(1L);
        id.setParentPrefix("X");
        id.setAncestorId(1L);
        var request = new FileSystemIdentificationRequest(meta, id);

        try {
            request.open(file);

            resultHandler.reset();
            submissionGateway.submit(request).get();
            submissionGateway.awaitFinished();

            var results = resultHandler.getResults();

            results.forEach(this::augmentContainerResults);

            List<IdentificationResultCollection> containerResults =
                    results.size() == 1 ? Collections.emptyList() : results.subList(1, results.size());

            return new DroidResult(results.get(0), containerResults);
        } finally {
            request.close();
        }
    }

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

    public void close() throws IOException {
        submissionGateway.close();
    }
}

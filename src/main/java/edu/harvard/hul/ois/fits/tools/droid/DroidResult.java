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

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;

/**
 * Encapsulates the identification results of a file and any files that the file contains.
 */
class DroidResult {

    private final Path file;
    private final IdentificationResultCollection primaryResult;
    private final List<IdentificationResultCollection> innerResults;

    /**
     * @param file the file that was analyzed
     * @param primaryResult the primary identification result
     * @param innerResults the identifications result of any files the primary file contained
     */
    public DroidResult(
            Path file,
            IdentificationResultCollection primaryResult,
            List<IdentificationResultCollection> innerResults) {
        this.file = Objects.requireNonNull(file, "file cannot be null");
        this.primaryResult = Objects.requireNonNull(primaryResult, "primaryResult cannot be null");
        this.innerResults = List.copyOf(Objects.requireNonNull(innerResults, "innerResults cannot be null"));
    }

    /**
     * @return the file that was analyzed
     */
    public Path getFile() {
        return file;
    }

    /**
     * @return the primary identification result
     */
    public IdentificationResultCollection getPrimaryResult() {
        return primaryResult;
    }

    /**
     * @return the identification results for any files contained within the primary file, or an empty list
     */
    public List<IdentificationResultCollection> getInnerResults() {
        return innerResults;
    }
}

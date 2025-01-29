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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.ResourceId;
import uk.gov.nationalarchives.droid.core.interfaces.ResultHandler;
import uk.gov.nationalarchives.droid.core.interfaces.filter.Filter;

/**
 * Droid calls this class whenever it identifies a file, and we collect the results. This is necessary because it's
 * the only way to get the results from archive contents. Droid only returns the result for the specified file directly,
 * and will not return the results for any files that are contained within the specified file.
 * <p>
 * This class is NOT THREAD SAFE. You must use a different instance per thread, and you must call {@link #reset()}
 * between files.
 */
class CollectingResultHandler implements ResultHandler {

    private static final Logger log = LoggerFactory.getLogger(CollectingResultHandler.class);

    private final List<IdentificationResultCollection> results = new ArrayList<>();

    /**
     * Clears the accumulated results in preparation for processing a new file.
     */
    public void reset() {
        results.clear();
    }

    /**
     * @return the accumulated identification results
     */
    public List<IdentificationResultCollection> getResults() {
        return List.copyOf(results);
    }

    @Override
    public ResourceId handle(IdentificationResultCollection identificationResultCollection) {
        results.add(identificationResultCollection);
        return new ResourceId(DroidId.nextId(), "");
    }

    @Override
    public ResourceId handleDirectory(IdentificationResult identificationResult, ResourceId resourceId, boolean b) {
        return new ResourceId(DroidId.nextId(), "");
    }

    @Override
    public void handleError(IdentificationException e) {
        log.warn("DROID identification error", e);
    }

    @Override
    public void deleteCascade(Long aLong) {}

    @Override
    public void commit() {}

    @Override
    public void init() {}

    @Override
    public void setResultsFilter(Filter filter) {}
}

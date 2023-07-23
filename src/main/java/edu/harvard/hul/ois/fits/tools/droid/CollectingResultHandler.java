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

// TODO DROID
class CollectingResultHandler implements ResultHandler {

    private static final Logger log = LoggerFactory.getLogger(CollectingResultHandler.class);

    private final List<IdentificationResultCollection> results = new ArrayList<>();

    public void reset() {
        results.clear();
    }

    public List<IdentificationResultCollection> getResults() {
        return List.copyOf(results);
    }

    @Override
    public ResourceId handle(IdentificationResultCollection identificationResultCollection) {
        results.add(identificationResultCollection);
        return identificationResultCollection.getCorrelationId();
    }

    @Override
    public ResourceId handleDirectory(IdentificationResult identificationResult, ResourceId resourceId, boolean b) {
        return null;
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

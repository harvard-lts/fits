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

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Future;
import org.apache.commons.lang.StringUtils;
import uk.gov.nationalarchives.droid.core.interfaces.AsynchDroid;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.submitter.SubmissionGateway;

/**
 * When identifying the contents of an archive, Droid will recurse into nested archives. However, FITS prefers not
 * to recurse into inner archives, and this class is used to restrict the recursion.
 * <p>
 * Unfortunately, I had to implement this as a subclass of {@link SubmissionGateway} because {@link AsynchDroid} does
 * not define a close() method.
 */
class RecursionRestrictedSubmissionGateway extends SubmissionGateway {

    @Override
    public Future<IdentificationResultCollection> submit(IdentificationRequest identificationRequest) {
        // Droid inserts "!/" every time it enters an archive, so by counting the occurrences of that string we can
        // limit the recursion.
        var url = Optional.ofNullable(identificationRequest.getIdentifier())
                .map(RequestIdentifier::getUri)
                .map(URI::toString)
                .orElse("");
        var depth = StringUtils.countMatches(url, "!/");

        if (depth > 1) {
            return null;
        }

        return super.submit(identificationRequest);
    }
}

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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Every file Droid process needs an ID. These ids are normally assigned by adding the resource to a db, but FITS
 * doesn't use a DB. Instead, we just generate the id here.
 */
final class DroidId {

    private static final AtomicLong ID = new AtomicLong(1);

    private DroidId() {
        // noop
    }

    /**
     * @return new id
     */
    public static long nextId() {
        return ID.getAndIncrement();
    }
}

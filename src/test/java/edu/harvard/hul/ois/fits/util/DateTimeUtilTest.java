//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateTimeUtilTest {

    @Test
    public void convertKnownFormats() {
        assertEquals("2020-02-20T01:20:00", DateTimeUtil.standardize("2020-02-20T01:20"));
        assertEquals("2020-02-20T01:20:13", DateTimeUtil.standardize("2020-02-20T01:20:13"));
        assertEquals("2020-02-20T01:20:13Z", DateTimeUtil.standardize("2020-02-20T01:20:13Z"));
        assertEquals("2020-02-20T03:20:13Z", DateTimeUtil.standardize("2020-02-20T01:20:13-02:00"));
        assertEquals("2020-02-19T22:20:13Z", DateTimeUtil.standardize("2020-02-20T01:20:13+03:00"));
        assertEquals("2015-09-21T11:42:44.173", DateTimeUtil.standardize("2015-09-21T11:42:44.173000000"));

        assertEquals("2020-02-20T01:20:00", DateTimeUtil.standardize("2020-02-20 01:20"));
        assertEquals("2020-02-20T01:20:13", DateTimeUtil.standardize("2020-02-20 01:20:13"));
        assertEquals("2020-02-20T01:20:13Z", DateTimeUtil.standardize("2020-02-20 01:20:13Z"));
        assertEquals("2020-02-20T03:20:13Z", DateTimeUtil.standardize("2020-02-20 01:20:13-02:00"));
        assertEquals("2020-02-19T22:20:13Z", DateTimeUtil.standardize("2020-02-20 01:20:13+03:00"));
        assertEquals("2015-09-21T11:42:44.173", DateTimeUtil.standardize("2015-09-21 11:42:44.173000000"));

        assertEquals("2020-02-20T01:20:00", DateTimeUtil.standardize("2020:02:20 01:20"));
        assertEquals("2020-02-20T01:20:13", DateTimeUtil.standardize("2020:02:20 01:20:13"));
        assertEquals("2020-02-20T01:20:13Z", DateTimeUtil.standardize("2020:02:20 01:20:13Z"));
        assertEquals("2020-02-20T03:20:13Z", DateTimeUtil.standardize("2020:02:20 01:20:13-02:00"));
        assertEquals("2020-02-19T22:20:13Z", DateTimeUtil.standardize("2020:02:20 01:20:13+03:00"));
        assertEquals("2015-09-21T11:42:44.173", DateTimeUtil.standardize("2015:09:21 11:42:44.173000000"));

        assertEquals("2020-02-20T01:20:00", DateTimeUtil.standardize("2020.02.20 01:20"));
        assertEquals("2020-02-20T01:20:13", DateTimeUtil.standardize("2020.02.20 01:20:13"));
        assertEquals("2020-02-20T01:20:13Z", DateTimeUtil.standardize("2020.02.20 01:20:13Z"));
        assertEquals("2020-02-20T03:20:13Z", DateTimeUtil.standardize("2020.02.20 01:20:13-02:00"));
        assertEquals("2020-02-19T22:20:13Z", DateTimeUtil.standardize("2020.02.20 01:20:13+03:00"));
        assertEquals("2015-09-21T11:42:44.173", DateTimeUtil.standardize("2015.09.21 11:42:44.173000000"));

        assertEquals("2020-02-20", DateTimeUtil.standardize("2020-02-20"));
        assertEquals("2020-02-20", DateTimeUtil.standardize("2020:02:20"));
        assertEquals("2020-02-20", DateTimeUtil.standardize("2020.02.20"));
        assertEquals("2020-02-20", DateTimeUtil.standardize("20200220"));

        assertEquals("2022", DateTimeUtil.standardize("2022"));

        assertEquals("2008-06-03T11:05:30Z", DateTimeUtil.standardize("Tue, 3 Jun 2008 11:05:30 GMT"));
    }

    @Test
    public void doNotConvertUnknownFormats() {
        assertEquals("2020-02-20T0120", DateTimeUtil.standardize("2020-02-20T0120"));
        assertEquals("bogus", DateTimeUtil.standardize("bogus"));
        assertEquals("2020-02-20 01", DateTimeUtil.standardize("2020-02-20 01"));
    }

}

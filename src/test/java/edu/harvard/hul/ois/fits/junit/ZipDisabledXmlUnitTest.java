/*
 * Copyright 2016 Harvard University Library
 *
 * This file is part of FITS (File Information Tool Set).
 *
 * FITS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FITS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FITS.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.hul.ois.fits.junit;

import edu.harvard.hul.ois.fits.tests.AbstractXmlUnitTest;
import org.junit.Test;

/**
 * These tests compare actual FITS output with expected output on ZIP files.
 * These tests should be run with &lt;display-tool-output&gt;false&lt;/display-tool-output&gt; in fits.xml.
 *
 * @author dan179
 */
public class ZipDisabledXmlUnitTest extends AbstractXmlUnitTest {

    @Override
    protected String fitsConfigFile() {
        return "fits_archives_disabled.xml";
    }

    @Test
    public void testCompressedZipFile() throws Exception {
        testFile("assorted-files.zip", fits, OutputType.DEFAULT);
    }
}

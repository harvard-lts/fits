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

import edu.harvard.hul.ois.fits.tests.AbstractOutputTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * These generated FITS output with tool output on the given Adobe Illustrator files.
 * These tests should be run with &lt;display-tool-output&gt;false&lt;/display-tool-output&gt; in fits.xml.
 *
 * @author dan179
 */
@Ignore("These tests have no asserts and just generate output files")
public class AdobeIllustratorTest extends AbstractOutputTest {

    @Test
    public void testtestAdobeIllustratorFile() throws Exception {
        writeOutput("MMS-82A.08.12.02.ai");
    }
}

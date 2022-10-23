/*
 * Copyright 2022 Harvard University Library
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

import static org.junit.Assert.fail;

import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.tests.AbstractOutputTest;
import java.util.List;
import org.junit.Test;

public class DpxTest extends AbstractOutputTest {

    @Test
    public void testDpxFormatDetection() throws Exception {
        String inputFilename = "00266.dpx";
        FitsOutput fitsOut = writeOutput(inputFilename);
        List<FitsIdentity> identities = fitsOut.getIdentities();

        for (FitsIdentity identity : identities) {
            if (!identity.getMimetype().contains("image/x-dpx")) {
                fail(inputFilename + " should be identified as a DPX file with mimetype of image/x-dpx");
            }
        }
    }
}

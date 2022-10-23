/*
 * Copyright 2014 Harvard University Library
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

@Ignore("These tests have no asserts and just generate output files")
public class VideoStdSchemaTest extends AbstractOutputTest {

    @Test
    public void testVideo_DV() throws Exception {
        writeOutput("FITS-SAMPLE-26.mov");
    }

    @Test
    public void testMp4() throws Exception {
        writeOutput("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_3_1.mp4");
    }

    @Test
    public void testVideo() throws Exception {
        writeOutput("FITS-SAMPLE-44_1_1_4_4_4_6_1_1_2_4_8_1_2_1_1.mov");
    }

    @Test
    public void testMxfVideo() throws Exception {
        writeOutput("freeMXF-mxf1a.mxf");
    }
}

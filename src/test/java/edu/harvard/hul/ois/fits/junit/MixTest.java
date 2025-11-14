/*
 * Copyright 2009 Harvard University Library
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
public class MixTest extends AbstractOutputTest {

    @Test
    public void testMIX() throws Exception {
        writeOutput("topazscanner.tif");
    }

    @Test
    public void testUncompressedTif() throws Exception {
        writeOutput("4072820.tif");
    }

    @Test
    public void testMixJpg() throws Exception {
        writeOutput("3426592.jpg");
    }

    @Test
    public void testJpgExif() throws Exception {
        writeOutput("ICFA.KC.BIA.1524-small.jpg");
    }

    @Test
    public void testJpgExif2() throws Exception {
        writeOutput("JPEGTest_20170591--JPEGTest_20170591.jpeg");
    }

    @Test
    public void testJpgJfif() throws Exception {
        writeOutput("gps.jpg");
    }

    @Test
    public void testTwoPageTiff() throws Exception {
        writeOutput("W00EGS1016782-I01JW30--I01JW300001__0001.tif");
    }

    @Test
    public void testPyramidalTiff() throws Exception {
        writeOutput("x059f0483-original_file.ptif");
    }

    @Test
    public void testJp2_1() throws Exception {
        writeOutput("test.jp2");
    }

    @Test
    public void testJp2_2() throws Exception {
        writeOutput("006607203_00018.jp2");
    }

    @Test
    public void testNotWellFormedJp2() throws Exception {
        writeOutput("2339337_not_well_formed.jp2");
    }
}

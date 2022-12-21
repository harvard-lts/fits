/*
 * Copyright 2015 Harvard University Library
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

import static edu.harvard.hul.ois.fits.FitsPaths.PROPS_DIR;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.tests.AbstractXmlUnitTest;
import java.io.File;
import org.junit.Test;

public class AudioStdSchemaXmlUnitTest extends AbstractXmlUnitTest {

    @Test
    public void testFlac() throws Exception {
        testFile("test.flac");
    }

    @Test
    public void testAudioChunk() throws Exception {
        testFile("testchunk.wav", fits, OutputType.DEFAULT);
    }

    @Test
    public void testAudioMD_noMD5() throws Exception {
        // use an alternate fits.xml file where a MD5 checksum is not generated
        File fitsConfigFile = new File(PROPS_DIR + "fits_no_md5_audio.xml");
        Fits fits = new Fits(null, fitsConfigFile);

        testFile("test.wav", fits, OutputType.DEFAULT);
    }
}

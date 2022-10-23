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
public class ZipXmlUnitTest extends AbstractXmlUnitTest {

    @Test
    public void testUncompressedZipFile() throws Exception {
        testFile("32044020597662.zip", fits, OutputType.DEFAULT);
    }

    @Test
    public void testCompressedZipFile() throws Exception {
        testFile("assorted-files.zip", fits, OutputType.DEFAULT);
    }

    @Test
    public void testSingleFileZipFile() throws Exception {
        testFile("40415587.zip");
    }

    @Test
    public void testCompressedEncryptedZipFile() throws Exception {
        testFile("compressed-encrypted.zip"); // 0sample1-compressed
    }

    @Test
    public void testUncompressedEncryptedZipFile() throws Exception {
        testFile("uncompressed-encrypted.zip");
    }

    @Test
    public void testEmbeddedDirectoriesZipFile() throws Exception {
        testFile("multiple-file-types-and-folders.zip");
    }
}

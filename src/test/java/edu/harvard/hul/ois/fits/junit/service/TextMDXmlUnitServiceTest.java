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
package edu.harvard.hul.ois.fits.junit.service;

import edu.harvard.hul.ois.fits.tests.AbstractWebAppTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * These tests compare actual FITS output with expected output on text files.
 * NOTE: This is an integration test that requires a running web application with the
 * FITS Service running.
 *
 * @author dan179
 */
@Ignore
public class TextMDXmlUnitServiceTest extends AbstractWebAppTest {

    @Test
    public void testUTF16TextMD() throws Exception {
        testFileInWebApp("utf16.txt");
    }

    @Test
    public void testPlainText() throws Exception {
        testFileInWebApp("plain-text.txt");
    }

    @Test
    public void testCsv() throws Exception {
        testFileInWebApp("random_data.csv");
    }
}

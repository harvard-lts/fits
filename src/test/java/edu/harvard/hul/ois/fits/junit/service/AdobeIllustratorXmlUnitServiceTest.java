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
package edu.harvard.hul.ois.fits.junit.service;

import edu.harvard.hul.ois.fits.tests.AbstractWebAppTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * These tests compare actual FITS output with expected output on Adobe Illustrator files.
 * NOTE: This is an integration test that requires a running web application with the
 * FITS Service running.
 *
 * @author dan179
 */
@Ignore
public class AdobeIllustratorXmlUnitServiceTest extends AbstractWebAppTest {

    @Test
    public void testAdobeIllustratorFile() throws Exception {
        testFileInWebApp("MMS-82A.08.12.02.ai");
    }
}

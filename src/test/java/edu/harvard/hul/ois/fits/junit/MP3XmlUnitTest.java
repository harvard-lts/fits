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

import edu.harvard.hul.ois.fits.tests.AbstractXmlUnitTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class MP3XmlUnitTest extends AbstractXmlUnitTest {

    @Override
    protected String[] getIgnoredXmlElements() {
        String[] ignored = super.getIgnoredXmlElements();
        List<String> additionalIgnored = new ArrayList<>(Arrays.asList(ignored));
        additionalIgnored.add("ID");
        additionalIgnored.add("audioObjectRef");
        additionalIgnored.add("formatRef");
        additionalIgnored.add("faceRef");
        additionalIgnored.add("faceRegionRef");
        additionalIgnored.add("ownerRef");

        return additionalIgnored.toArray(new String[additionalIgnored.size()]);
    }

    @Test
    public void testMP3() throws Exception {
        testFile("Jess26676_Pugua.mp3");
    }
}

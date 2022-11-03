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

package edu.harvard.hul.ois.fits;

import org.jdom2.output.Format;

public final class FitsFormat {

    private FitsFormat() {
        // noop
    }

    /**
     * Using this format is necessary to ensure that JDOM pretty prints the XML, but also preserves whitespace
     * within text elements. We want to preserve whitespace because it is significant for some fields, such as
     * codec codes.
     *
     * @return the xml format that should be used for serializing jdom to xml
     */
    public static Format xmlFormat() {
        return Format.getPrettyFormat().setTextMode(Format.TextMode.TRIM_FULL_WHITE);
    }
}

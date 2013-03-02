/**********************************************************************
 * Copyright (c) 2009 by the President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Contact information
 *
 * Office for Information Systems
 * Harvard University Library
 * Harvard University
 * Cambridge, MA  02138
 * (617)495-3724
 * hulois@hulmail.harvard.edu
 **********************************************************************/

package edu.harvard.hul.ois.fits;

import edu.harvard.hul.ois.ots.schemas.DocumentMD.DocumentMD;
import edu.harvard.hul.ois.ots.schemas.DocumentMD.DocumentMD.Feature;

import org.jdom.Element;

public class DocumentMDModel {

    protected DocumentMD docMD;
    
    protected DocumentMDModel () {
        docMD = new DocumentMD ();
    }
    
    /** Adds a feature to the document metadata. The element name needs to
     *  be the same as a value of the Enum Feature, or nothing will happen.
     */
    protected void addFeature (Element featureElem) {
        String name = featureElem.getName ();
        String text = featureElem.getText().trim();
        if ("yes".equals(text)) {
            for (Feature ftr: Feature.values()) {
                if (ftr.toString().equals (name)) {
                    docMD.addFeature(ftr);
                    break;
                }
            }
        }
    }
}

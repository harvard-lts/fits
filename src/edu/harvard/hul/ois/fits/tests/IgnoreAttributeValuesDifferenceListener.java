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
package edu.harvard.hul.ois.fits.tests;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

public class IgnoreAttributeValuesDifferenceListener implements DifferenceListener {
	
	 private static final int[] IGNORE_VALUES = new int[] {
		   DifferenceConstants.ATTR_VALUE.getId(),
		   DifferenceConstants.ATTR_VALUE_EXPLICITLY_SPECIFIED.getId()
		   };


	public int differenceFound(Difference diff) {
		if (isIgnoredDifference(diff)) {
			return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
		} else {
			return RETURN_ACCEPT_DIFFERENCE;
		}
	}


	public void skippedComparison(Node arg0, Node arg1) {
		// do nothing here

	}
	
	private boolean isIgnoredDifference(Difference difference) {
		int differenceId = difference.getId();
		for (int i=0; i < IGNORE_VALUES.length; ++i) {
			if (differenceId == IGNORE_VALUES[i]) {
				return true;
			}
		}
		return false;
	}

}

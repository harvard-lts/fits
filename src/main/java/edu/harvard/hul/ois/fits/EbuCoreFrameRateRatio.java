//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits;

/** This class handles the creation of a numerator and denominator for a Video
 * Frame Rate, based upon the value passed in, as well as the transformation of
 * actual the value passed in based upon the following logic:
 *
 * If FrameRate from MediaInfo is a whole number, then the following is true:
 *
 *   frameRate = the value remains the same
 *   numerator = 1
 *   denominator = 1
 *
 * Otherwise the following is true:
 *
 *   frameRate = the value is rounded to the nearest whole integer
 *   numerator = 1000
 *   denominator = 1001
 *
 *
 * For example, if the value passed in is 20, then both the the numerator and
 * denominator are set to 1, and the frame rate is unchanged (20).
 * If the value passed in is 29.970 fps, the numerator is set to 1000, the
 * denominator is set to 1001, and the value is transformed into 30.
 */
public class EbuCoreFrameRateRatio {

	private String numerator = "1";
	private String denominator = "1";
	private String value;
	private final static String DECIMAL = ".";
	private final static String SPACE = " ";

	public EbuCoreFrameRateRatio(String value) {

		// the string might have fps, or a identifier at the end, so we need to
		// remove it
		String[] parts = value.split(SPACE);
		this.value = parts[0];

		// If a decimal value, revise members
    	if(value.contains(DECIMAL)) {
        	this.numerator = "1000";
        	this.denominator = "1001";

        	// Round FrameRate to whole number
            Double dblValue = null;
            try {
                dblValue = Double.parseDouble (this.value);
            }
            catch (NumberFormatException e) {}

            int a = (int) Math.round(dblValue);

        	this.value = ""+a;
    	}

	}

	public String getValue() {
		return value;
	}

	public String getNumerator() {
		return numerator;
	}

	public String getDenominator() {
		return denominator;
	}

}

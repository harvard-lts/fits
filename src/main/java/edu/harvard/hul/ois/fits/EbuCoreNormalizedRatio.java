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

import java.math.BigDecimal;

import org.apache.commons.lang.math.NumberUtils;

/** This class handles the transformation of the numerator and denominator
 * contained within a Ratio used in Ebucore XML to normalized values.
 * For example, if the numerator is 2.22 and the denominator is 1, the
 * numerator is converted to 222 and the denominator is converted to 100,
 * thus eliminating the decimal places, and essentially transforming them into
 * integers.
 *
 * If the value passed in is null, or invalid, we will get a numerator and
 * denominator value of 0.
 */
public class EbuCoreNormalizedRatio {

	private int normalizedNumerator;
	private int normalizedDenominator;
	public final static String DECIMAL = ".";
	public final static String COLON = ":";

	public EbuCoreNormalizedRatio(String ratioStr) {

		// This should not happen
		if(ratioStr == null || ratioStr.length() < 1)
	    	return;

		// Handles the conversion of a single decimal value to an x:y ratio, if
		// necessary
		ratioStr = convertDecimalToRatio(ratioStr);

		// If we don't have a proper ratio, just return
		// TODO: Should we throw an exception here?
		if(!ratioStr.contains(COLON))
			return;

		String[] splitValues = ratioStr.split(EbuCoreNormalizedRatio.COLON);
		// TODO: throw exception if there are not 2 pieces
		if (splitValues != null && splitValues.length == 2) {
			// Normalize the ratio, if necessary
			normalizeRatio(splitValues[0], splitValues[1]);
		}

	}

	private void normalizeRatio(String numerator, String denominator) {

		// Verify that the strings passed in are both numbers
		if(!NumberUtils.isNumber(numerator) || !NumberUtils.isNumber(denominator)) {
			return;
		}

		// We only need to normalize the values if one contains a decimal
		if(numerator.contains(DECIMAL) || denominator.contains(DECIMAL)) {
			BigDecimal numBD = new BigDecimal(numerator);
			BigDecimal denomBD = new BigDecimal(denominator);

			double multiplier = getMultiplierForRatio(numBD, denomBD);
			this.normalizedNumerator = numBD.multiply(new BigDecimal(multiplier)).intValue();
			this.normalizedDenominator = denomBD.multiply(new BigDecimal(multiplier)).intValue();

		}
		// Must be an integer
		else {
			// set the denormalized value
			this.normalizedNumerator = new Integer(numerator).intValue();
			this.normalizedDenominator = new Integer(denominator).intValue();
		}


	}

	/**
	 * If the AspectRatio is a single decimal value, such as "1.067" this will
	 * convert it to an x:y ratio. Otherwise it will return the original ratioStr
	 *
	 * @param ratioStr The decimal representation of the aspect ratio to convert
	 * @return
	 */
	private String convertDecimalToRatio(String ratioStr) {

		// If this isn't a decimal value such as 1.067, we don't need to convert it
		if (!NumberUtils.isNumber(ratioStr) || !ratioStr.contains(DECIMAL)) {
			return ratioStr;
		}
		// Convert the value to an x:y representation by revising it to ratioStr:1
		return  ratioStr + COLON + "1";
	}

	/**
	 * Return the value required to normalize a ratio. For example, if the
	 * numerator is 2.22 and the denominator is 1, the multiplier will be
	 * 100. This is the number used to multiply both the numerator and the
	 * denominator to remove any decimal positions.
	 *
	 * @param numBD The numerator as a BigDecimal
	 * @param denomBD The denominator as a BigDecimal
	 * @return
	 */
	private double getMultiplierForRatio(BigDecimal numBD, BigDecimal denomBD) {

		int numDecimal = getNumberOfDecimalPlaces(numBD);
		int tmpNumDecimal = getNumberOfDecimalPlaces(denomBD);
		if(tmpNumDecimal > numDecimal)
			numDecimal = tmpNumDecimal;
		return Math.pow(10, numDecimal);
	}

	/**
	 * Returns the number of decimal places in the number.
	 *
	 * @param bigDec
	 * @return
	 */
	private int getNumberOfDecimalPlaces(BigDecimal bigDec) {
		String strTrim = bigDec.stripTrailingZeros().toPlainString();
		int index = strTrim.indexOf(DECIMAL);
		return index < 0 ? 0 : strTrim.length() - index - 1;
	}

	public int getNormalizedNumerator() {
		return normalizedNumerator;
	}

	public int getNormalizedDenominator() {
		return normalizedDenominator;
	}

}

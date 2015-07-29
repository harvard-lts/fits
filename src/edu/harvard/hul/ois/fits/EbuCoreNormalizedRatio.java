package edu.harvard.hul.ois.fits;

import java.math.BigDecimal;

/** This class handles the transformation of the numerator and denominator
 * contained within a Ratio used in Ebucore XML to nomalized values.
 * For example, if the numerator is 2.22 and the denominator is 1, the 
 * numerator is converted to 222 and the denominator is converted to 100,
 * thus eliminating the decimal places, and essentially transforming them into
 * integers.
 */
public class EbuCoreNormalizedRatio {

	private int normalizedNumerator;
	private int normalizedDenominator;
	private final static String DECIMAL = ".";

	public EbuCoreNormalizedRatio(String numerator, String denominator) {
		//super();

		// TODO: verify that the string passed in is a number
		//
		//if(!NumberUtils.isNumber(numerator)) {
		//	
		//}

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

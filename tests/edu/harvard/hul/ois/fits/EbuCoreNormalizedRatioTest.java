/* 
 * Copyright 2014 Harvard University Library
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EbuCoreNormalizedRatioTest{

    @Test  
	public void testNormalAspectRatioC() throws Exception {   
    	String aspectRatio = "4:3";
    	EbuCoreNormalizedRatio ratio = new EbuCoreNormalizedRatio(aspectRatio);
    	assertEquals(ratio.getNormalizedNumerator(), 4);
    	assertEquals(ratio.getNormalizedDenominator(), 3);
	}
    
    @Test  
	public void testDecimalAspectRatioC() throws Exception {   
    	String aspectRatio = "1.067";
    	EbuCoreNormalizedRatio ratio = new EbuCoreNormalizedRatio(aspectRatio);
    	assertEquals(ratio.getNormalizedNumerator(), 1067);
    	assertEquals(ratio.getNormalizedDenominator(), 1000);
	}
    
    @Test  
	public void testMixedAspectRatioC() throws Exception {   
    	String aspectRatio = "2.22";
    	EbuCoreNormalizedRatio ratio = new EbuCoreNormalizedRatio(aspectRatio);
    	assertEquals(ratio.getNormalizedNumerator(), 222);
    	assertEquals(ratio.getNormalizedDenominator(), 100);
	}    
    
    @Test
    public void testInvalidNullAspectRatio() {
    	String aspectRatio = null;
    	EbuCoreNormalizedRatio ratio = new EbuCoreNormalizedRatio(aspectRatio);
    	
    	assertEquals(ratio.getNormalizedNumerator(), 0);
    	assertEquals(ratio.getNormalizedDenominator(), 0);    	
    }

    @Test
    public void testInvalidRatio() {
    	String aspectRatio = "abc123";
    	EbuCoreNormalizedRatio ratio = new EbuCoreNormalizedRatio(aspectRatio);
    	
    	assertEquals(ratio.getNormalizedNumerator(), 0);
    	assertEquals(ratio.getNormalizedDenominator(), 0);    	
    }
    
    @Test
    public void testInvalidRatio_2() {
    	String aspectRatio = "123:23.bc";
    	EbuCoreNormalizedRatio ratio = new EbuCoreNormalizedRatio(aspectRatio);
    	
    	assertEquals(ratio.getNormalizedNumerator(), 0);
    	assertEquals(ratio.getNormalizedDenominator(), 0);    	
    }    
}

/* 
 * Copyright 2015 Harvard University Library
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

public enum AudioFormatElements {

	soundField ("soundField"),
	bitRate ("bitRate"),
	bitRateMode ("bitRateMode"),
	samplingRate ("samplingRate"),
	sampleSize ("sampleSize"),
	channels("channels"),
	byteOrder ("byteOrder"),
	delay ("delay"),
	compression ("compression"),

	// NOTE the difference between the name and the ebucoreName so that .name()
	// and .getEbucoreName() are different
	trackSize ("streamSize"),
	numSamples ("sampleCount"),
	
	duration ("duration");
   	
	private String ebucoreName;
    
	AudioFormatElements(String ebcoreName) {
        this.ebucoreName = ebcoreName;
    }
    
    public String getEbucoreName () {
        return ebucoreName;
    }
    
//    static public AudioFormatElements lookup(String name) {
//    	AudioFormatElements retMethod = null;
//    	for(AudioFormatElements method : AudioFormatElements.values()) {
//    		if (method.Name().equals(name)) {
//    			retMethod = method;
//    			break;
//    		}
//    	}
//    	return retMethod;
//    }
}

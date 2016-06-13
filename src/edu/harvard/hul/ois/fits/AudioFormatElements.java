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

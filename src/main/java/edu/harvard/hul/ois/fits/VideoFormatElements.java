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

public enum VideoFormatElements {
	width ("width"),
	height ("height"),
	frameRate ("frameRate"),
	bitRate ("bitRate"),
	bitRateMax ("bitRateMax"),
	bitRateMode ("bitRateMode"),
	scanningFormat ("scanningFormat"),
	videoDataEncoding ("videoDataEncoding"),
	aspectRatio ("aspectRatio"),
	chromaSubsampling ("chromaSubsampling"),
	colorspace ("colorspace"),
	frameRateMode ("frameRateMode"),
	byteOrder ("byteOrder"),
	delay ("delay"),
	compression ("compression"),

	// NOTE the difference between the name and the ebucoreName so that .name()
	// and .getEbucoreName() are different
	trackSize ("streamSize"),

	broadcastStandard ("broadcastStandard"),
	frameCount ("frameCount"),
	bitDepth ("bitDepth"),
	duration ("duration");

	private String name;

	VideoFormatElements(String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    //static public VideoFormatElements lookup(String name) {
    //	VideoFormatElements retMethod = null;
    //	for(VideoFormatElements method : VideoFormatElements.values()) {
    //		if (method.Name().equals(name)) {
    //			retMethod = method;
    //			break;
    //		}
    //	}
    //	return retMethod;
    //}
}

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
	streamSize ("streamSize"),
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

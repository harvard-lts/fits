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
package edu.harvard.hul.ois.fits.tools.utils;

public class XsltFunctions {
	
	public static String getMessageString(String message, String subMessage, String severity, String offset) {
	
		if(subMessage != null && subMessage.length() > 0) {
			message = message + " " + subMessage;
		}
		if(severity != null && severity.length() > 0) {
			message = message + " severity=" + severity;
		}
		if(offset != null && offset.length() > 0) {
			message = message + " offset=" + offset;
		}
		
		return message;
	}
	
	/**
	 * Assists with some of the streaming file types (wav/mp3/video) where
	 * bitrate and total bytes are given
	 * 
	 * @param bytes
	 * @param bytesPerSec
	 */
	public static Object getDuration(double bytes, double bytesPerSec) {
		long msecs = (long) ((bytes / bytesPerSec) * 1000);
		return formatDuration(msecs);
	}
	

	private static final long SECS = 1000;

	private static final long MINS = SECS * 60;

	private static final long HRS = MINS * 60;
	
	public static String formatDuration(long number) {
		long hrs = number / HRS;
		number = number % HRS;
		long mins = number / MINS;
		number = number % MINS;
		long secs = number / SECS;
		number = number % SECS;

		return hrs + ":" + mins + ":" + secs + ":" + number;
	}

}

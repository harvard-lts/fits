//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.utils;

import org.apache.commons.io.FilenameUtils;

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

	public static String getFileNameFromUrl(String url) {
        return FilenameUtils.getName(url);
	}

	public static String getFileExtension(String url) {
        return FilenameUtils.getExtension(url);
	}

}

//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.ffident;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * helper class that tries to identify the file format for a given file
 * or byte array representing the first bytes of a file. <h3>Usage</h3>
 *
 * @author Marco Schmidt, Modified for use by FITS by Spencer McEwen
 */
public class FormatIdentification {
	private static List<FormatDescription> descriptions;
	private static int minBufferSize;
	private static final Logger logger = LoggerFactory.getLogger(FormatIdentification.class);

	public FormatIdentification(String configFile) throws FileNotFoundException {
		init(configFile);
	}

	public FormatDescription identify(byte[] data) {
		if (data == null || data.length < 1) {
			return null;
		}
		Iterator<FormatDescription> iter = descriptions.iterator();
		while (iter.hasNext()) {
			FormatDescription desc = (FormatDescription) iter.next();
			if (desc.matches(data)) {
				return desc;
			}
		}
		return null;
	}

	public FormatDescription identify(File file) {
		if (!file.isFile()) {
			return null;
		}
		long size = file.length();
		int numBytes;
		if (size > minBufferSize) {
			numBytes = minBufferSize;
		} else {
			numBytes = (int) size;
		}
		byte[] data = new byte[numBytes];
		RandomAccessFile in = null;
		try {
			in = new RandomAccessFile(file, "r");
			in.readFully(data);
			in.close();
		} catch (IOException ioe) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ioe) {
				//
			}
		}
		return identify(data);
	}

	private static void init(String configFile) throws FileNotFoundException {
		descriptions = new ArrayList<FormatDescription>();
		minBufferSize = 1;

		FileReader fr = new FileReader(configFile);

		try {
			FormatDescriptionReader in = new FormatDescriptionReader(fr);

			FormatDescription desc;
			while ((desc = in.read()) != null) {
				byte[] magic = desc.getMagicBytes();
				Integer offset = desc.getOffset();
				if (magic != null && offset != null
						&& offset.intValue() + magic.length > minBufferSize) {
					minBufferSize = offset.intValue() + magic.length;
				}
				descriptions.add(desc);
			}

		} catch (IOException e) {
			logger.error("Problem reading file: " + configFile, e);
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				// nothing to do if exception other than log
				logger.info("Problem closing FileReader for file: " + configFile, e);
			}
		}
	}
}

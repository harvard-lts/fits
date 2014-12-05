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
package edu.harvard.hul.ois.fits.tools.ffident;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * helper class that tries to identify the file format for a given file
 * or byte array representing the first bytes of a file. <h3>Usage</h3>
 * 
 * <pre>
 * FormatIdentification identifier = new FormatIdentification(String config);
 * FormatDescription desc = identifier.identify(&quot;file&quot;);
 * if (desc != null) {
 * 	System.out.println(desc.getShortName());
 * 
 * </pre>
 * 
 * @author Marco Schmidt, Modified for use by FITS by Spencer McEwen
 */
public class FormatIdentification {
	private static List<FormatDescription> descriptions;
	private static int minBufferSize;

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

		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

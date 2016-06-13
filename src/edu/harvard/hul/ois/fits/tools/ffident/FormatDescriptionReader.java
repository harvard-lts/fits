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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Read {@link edu.harvard.hul.ois.fits.tools.ffident.FormatDescription} objects from a semicolon-separated text file.
 * @author Marco Schmidt
 */
public class FormatDescriptionReader
{
	private BufferedReader in;

	public FormatDescriptionReader(Reader reader)
	{
		in = new BufferedReader(reader);
	}

	public FormatDescription read() throws IOException
	{
		String line;
		do
		{
			line = in.readLine();
			if (line == null)
			{
				return null;
			}
		}
		while (line.length() < 1 || line.charAt(0) == '#');
		String[] items = line.split(";");
		if (items == null || items.length < 8)
		{
			throw new IOException("Could not interpret line: " +
				line);
		}
		FormatDescription desc = new FormatDescription();
		desc.setGroup(items[0]);
		desc.setShortName(items[1]);
		desc.setLongName(items[2]);
		desc.addMimeTypes(items[3]);
		desc.addFileExtensions(items[4]);
		desc.setOffset(new Integer(items[5]));
		desc.setMagicBytes(items[6]);
		desc.setMinimumSize(new Integer(items[7]));
		return desc;
	}
}

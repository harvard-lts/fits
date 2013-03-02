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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Read {@link metadata.FormatDescription} objects from a semicolon-separated text file.
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

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

import java.io.*;

class StreamGobbler extends Thread {
	private InputStream is;
	private OutputStream os;

	/** Constructor with no output stream. Output from the process
	 *  will be discarded.
	 */
	StreamGobbler(InputStream is) {
		this(is, null);
	}

	/** Constructor with output stream. Output from the process will
	 *  be sent to it on a line-by-line basis.
	 */
	StreamGobbler(InputStream is, OutputStream redirect) {
		this.is = is;
		this.os = redirect;
	}

	public void run() {
		try {
			PrintWriter pw = null;
			if (os != null)
				pw = new PrintWriter(os);

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (pw != null)
					pw.println(line);
			}
			if (pw != null)
				pw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
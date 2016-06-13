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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StreamGobbler extends Thread {
	private InputStream is;
	private OutputStream os;

	private static final Logger logger = LoggerFactory.getLogger(StreamGobbler.class);

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
			logger.error("Error reading input command.", ioe);
		}
	}
}

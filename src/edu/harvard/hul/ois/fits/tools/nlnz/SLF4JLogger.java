//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.nlnz;

import java.io.IOException;

import org.apache.log4j.Logger;

import nz.govt.natlib.meta.log.Log;
import nz.govt.natlib.meta.log.LogLevel;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * A logger that logs to SLF4J and approximates the log levels of the NLNZ Metadata Extractor
 *
 * @author dan179
 */
public class SLF4JLogger implements Log {

	/*
	 * Copyright 2006 The National Library of New Zealand
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may
	 * not use this file except in compliance with the License. You may obtain a
	 * copy of the License at
	 *
	 * http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 */
	private static final Logger logger = Logger.getLogger(SLF4JLogger.class);

	public SLF4JLogger() {
		super();
	}

	public void logMessage(LogMessage message) {
		LogLevel logLevel = message.getLevel();
		int level = logLevel.getLevel();

		if (level == LogMessage.CRITICAL.getLevel() ) {
			logger.fatal( formatMessage(message, false) );
		} else if (level == LogMessage.ERROR.getLevel() ) {
			logger.error( formatMessage(message, false) );
		} else if (level == LogMessage.DEBUG.getLevel() ) {
			logger.debug( formatMessage(message, false) );
		} else if (level == LogMessage.INFO.getLevel() ) {
			logger.info( formatMessage(message, false) );
		} else if (level == LogMessage.WORTHLESS_CHATTER.getLevel() ) {
			logger.trace( formatMessage(message, false) );
		} else {
			// unknown level -- show full content of LogMessage
			// arbitrarily picking 'warn'
			logger.warn( formatMessage(message, true) );
		}
	}

	public void suspendEvents(boolean suspend) {
		// no-op
	}

	public void close() throws IOException {
		// no-op
	}

	// Somehow spit out all that might be in one of their log messages.
	private String formatMessage(LogMessage msg, boolean verbose) {
		StringBuilder sb = new StringBuilder("NLNZ Logging -- ");
		if ( !verbose ) {
			sb.append(msg.getMessage());
		} else {
			sb.append("Source: [");
			sb.append(msg.getSource());
			sb.append("], Message: [");
			sb.append(msg.getMessage());
			sb.append("], Commen:t [");
			sb.append(msg.getComment());
			sb.append("], Level: [");
			sb.append(msg.getLevel());
			sb.append("]");
		}
		return sb.toString();
	}
}

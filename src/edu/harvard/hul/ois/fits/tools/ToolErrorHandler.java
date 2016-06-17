//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.log4j.Logger;

/** Output error reports from validation of FITS output */
public class ToolErrorHandler implements ErrorHandler {

    private static Logger logger = Logger.getLogger(ToolErrorHandler.class);

    @Override
    public void error(SAXParseException e) throws SAXException {
        logger.error("SAX error", e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        logger.fatal("Fatal SAX error", e);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
    	logger.warn("SAX warning", e);
    }
}

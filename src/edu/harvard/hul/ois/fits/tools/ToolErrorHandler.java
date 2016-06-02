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

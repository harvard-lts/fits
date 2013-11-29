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
        reportIt (e, "SAX error: ");
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        reportIt (e, "Fatal SAX error: ");

    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        reportIt (e, "SAX warning: ");

    }
    
    private void reportIt (SAXParseException e, String m) {
        StringWriter wtr = new StringWriter();
        PrintWriter pwtr = new PrintWriter (wtr);
        e.printStackTrace(pwtr);
        String message = m + e.getMessage() + "\n" +
                wtr.toString();
        logger.error(message);
        pwtr.close();
    }

}

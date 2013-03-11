package edu.harvard.hul.ois.fits.tools.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolInfo;

import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class Tika extends ToolBase {

    private final static String TOOL_NAME = "Tika";

    public Tika() throws FitsToolException {
        // TODO Auto-generated constructor stub
        info = new ToolInfo();
        info.setName(TOOL_NAME);
    }

    public ToolOutput extractInfo(File file) throws FitsToolException {
        // TODO Auto-generated method stub
        Parser parser = new AutoDetectParser();
        ContentHandler handler = null;
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        FileInputStream instrm;
        try {
            instrm = new FileInputStream (file);
        }
        catch (FileNotFoundException e) {
            throw new FitsToolException ("Can't open file", e);
        }
        try {
            
        }
        finally {
            try {
                instrm.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            parser.parse (instrm, handler, metadata, parseContext);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (TikaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // TODO convert the information in metadata to FITS output.
        String [] propertyNames = metadata.names();
        // TODO look through these values to better understand what Tika returns.
        
        return null;
    }

    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setEnabled(boolean value) {
        // TODO Auto-generated method stub

    }

}

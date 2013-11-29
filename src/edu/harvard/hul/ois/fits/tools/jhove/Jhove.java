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
package edu.harvard.hul.ois.fits.tools.jhove;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jdom.Document;
import org.apache.log4j.Logger;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.XmlUtils;
import edu.harvard.hul.ois.fits.tools.utils.XsltTransformMap;
import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.handler.XmlHandler;

/**  The glue class for invoking JHOVE under FITS.
 */
public class Jhove extends ToolBase {
	
    private App jhoveApp;
    private JhoveBase jhove;
    private XmlHandler xh; 
    private String jhoveConf;
    private boolean enabled = true;
    private static final Logger logger = Logger.getLogger(Jhove.class);

    public final static Calendar calendar = GregorianCalendar.getInstance();
    
    public final static String jhoveFitsConfig = Fits.FITS_XML+"jhove"+File.separator;
	
	public Jhove() throws FitsException {
        logger.debug ("Initializing Jhove");

		try {
            //Initialize Jhove  
            File config = new File(Fits.FITS_XML+"jhove"+File.separator+"jhove.conf");
            //= new File((this.getClass().getResource("jhove.conf")).toURI());
            jhoveConf = config.getPath();
            jhove = new JhoveBase ();
            jhove.init (jhoveConf, "org.apache.xerces.parsers.SAXParser");
            jhove.setChecksumFlag(false);   
            jhove.setSignatureFlag(false);
            jhove.setShowRawFlag(false);
      	    xh = new XmlHandler();
      	    jhoveApp = new App ("Jhove","1.5", new int[] {2009, 12, 23}, "","");
            xh.setApp(jhoveApp);
            xh.setBase(jhove);   		
		}
		catch (JhoveException e) {
		    logger.error ("Error initializing Jhove: " + e.getClass().getName());
			throw new FitsToolException("Error initializing Jhove",e);
		}

		//initialize tool info
		info = new ToolInfo(jhoveApp.getName(),jhoveApp.getRelease(),jhove.getDate().toString());		
		transformMap = XsltTransformMap.getMap(jhoveFitsConfig+"jhove_xslt_map.xml");
	}
	
    /**
     * Invokes Jhove in 'signature mode', where the header of the file is examined to determine
     * the format, and thus which Jhove Module should be used to validate the file
     * @param file the File object to characterize
     * @return the dom representation of the Jhove XML
     * @throws Exception
     */
    private Document characterize(File file) throws Exception {
    	jhove.setSignatureFlag(true);
    	Document dom = getFileInfo(file,null);	
    	jhove.setSignatureFlag(false);
		return dom;
    }
	
    /**
     * Invokes Jhove against a file, returning the DOM representation of the Jhove XML.
     * @param file the File object to operate on
     * @param mod the module to use
     * @return Document object
     * @throws Exception
     */
    private Document getFileInfo(File file,Module mod) throws Exception {
        String filepath = file.getAbsolutePath();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter out2 = new OutputStreamWriter(out,"UTF-8");
		PrintWriter pWriter = new PrintWriter(out2);
		xh.setWriter(pWriter);	
		jhove.process(jhoveApp, mod, xh, filepath);  		
		pWriter.close();
		out2.close();				
		Document dom = saxBuilder.build(new StringReader(out.toString()));		
		out.close();   	
		return dom;
    }
    
	/**
	 * processes the file with Jhove using the automatically determined module
	 * @throws FitsToolException 
	 */
	public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("Jhove.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		Document dom = null;
		try {
			dom = characterize(file);
			String jhoveModule = XmlUtils.getDomValue(dom,"reportingModule");
			Module mod = jhove.getModule(jhoveModule);
			dom = getFileInfo(file,mod);
		} catch (Exception e) {
		    logger.error("Jhove error while processing "+file.getName() + ": " +
                    e.getClass().getName() + ", message = " + e.getMessage());
			throw new FitsToolException("Jhove error while processing "+file.getName(),e);
		}
		catch (OutOfMemoryError e) {
            logger.error("Jhove OutOfMemoryError while processing "+file.getName());
			throw new FitsToolException("Jhove OutOfMemoryError while processing "+file.getName());
		}
		String format = XmlUtils.getDomValue(dom,"format");
		String xsltTransform = (String)transformMap.get(format.toUpperCase());

		/* debug code
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			outputter.output(dom, System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-------------------------------------------------------------------------------------");
		 */
		
		Document fitsXml = null;
		if(xsltTransform != null) {
			fitsXml = transform(jhoveFitsConfig+xsltTransform,dom);
		}
		else {
			fitsXml = transform(jhoveFitsConfig+"jhove_text_to_fits.xslt",dom);
		}
		
		/*
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			outputter.output(fitsXml, System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		output = new ToolOutput(this,fitsXml,dom);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("Jhove.extractInfo finished on " + file.getName());
		return output;
	}
/*
	public boolean isIdentityKnown(FileIdentity identity) {
		if(identity == null
				|| identity.getMime() == null 
				|| identity.getMime().length() == 0
				|| identity.getFormat() == null 
				|| identity.getFormat().length() == 0) {
			return false;
		}
		String format = identity.getFormat();
		String mime = identity.getMime();
		if((format.equals("bytestream") || format.equals("Unknown Binary"))
				&& mime.equals("application/octet-stream")) {
			return false;
		}
		else {
			return true;
		}
	}*/
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}
}

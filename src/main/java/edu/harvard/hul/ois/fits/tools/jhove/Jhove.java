//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//


package edu.harvard.hul.ois.fits.tools.jhove;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;

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
    private Fits fits;

    private final static String jhoveFitsConfig = Fits.FITS_XML_DIR + "jhove" + File.separator;
    private static final Logger logger = Logger.getLogger(Jhove.class);

	public Jhove(Fits fits) throws FitsException {
		super();
		this.fits = fits;
        logger.debug ("Initializing Jhove");

		try {
            //Initialize Jhove
            File config = new File(Fits.FITS_XML_DIR+"jhove"+File.separator+"jhove.conf");
            //= new File((this.getClass().getResource("jhove.conf")).toURI());
            jhoveConf = config.getPath();
            jhove = new JhoveBase ();
            jhove.init (jhoveConf, "org.apache.xerces.parsers.SAXParser");
            jhove.setChecksumFlag(false);
            jhove.setSignatureFlag(false);
            jhove.setShowRawFlag(false);
      	    xh = new XmlHandler();
            jhoveApp = new App ("Jhove","1.20.1", new int[] {2018, 03, 29}, "","");
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
			throw new FitsToolException("Jhove OutOfMemoryError while processing "+file.getName(), e);
		}
		String format = XmlUtils.getDomValue(dom,"format");
		String xsltTransform = (String)transformMap.get(format.toUpperCase());

		Document fitsXml = null;
		if(xsltTransform != null) {
			fitsXml = transform(jhoveFitsConfig+xsltTransform,dom);
		}
		else {
			fitsXml = transform(jhoveFitsConfig+"jhove_text_to_fits.xslt",dom);
		}

		output = new ToolOutput(this,fitsXml,dom, fits);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("Jhove.extractInfo finished on " + file.getName());
		return output;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}
}

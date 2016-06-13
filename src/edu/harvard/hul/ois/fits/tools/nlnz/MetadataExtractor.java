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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.XsltTransformMap;
import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.fx.ParserListener;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.harvester.DTDXmlParserListener;
import nz.govt.natlib.meta.log.LogManager;

/**  The glue class for invoking the NLNZ Metadata Extractor under FITS.
 */
public class MetadataExtractor extends ToolBase {

    private final static String TOOL_NAME = "NLNZ Metadata Extractor";
    private final static String TOOL_VERSION = "3.6GA";
    private final static String TOOL_DATE = "06/05/2014";

	public static String nlnzFitsConfig;
	private boolean enabled = true;
    private static final Logger logger = Logger.getLogger(MetadataExtractor.class);

    static {
    	nlnzFitsConfig = Fits.FITS_XML_DIR+"nlnz"+File.separator+"fits"+File.separator;
    	logger.debug("nlnzFitsConfig: " + nlnzFitsConfig);
    }

	public MetadataExtractor() throws FitsException {
        logger.debug ("Initializing MetadataExtractor");
		info = new ToolInfo(TOOL_NAME,TOOL_VERSION,TOOL_DATE);
		transformMap = XsltTransformMap.getMap(nlnzFitsConfig+"nlnz_xslt_map.xml");

		// HACK: need to set custom ClassLoader in NLNZ Config class so that it can find class names on Class.forName() call.
		ClassLoader cl = MetadataExtractor.class.getClassLoader();
		Config.setClassLoader(cl); // customized Config class; NOT the one supplied by NLNZ
		// Use custom logger so that NLNZ code doesn't log to System.out by default
		// (see what happens in nz.govt.natlib.meta.log.LogManager source code)
		LogManager.getInstance().addLog(new SLF4JLogger());
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("MetadataExtractor.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		Document dom = null;
		//Document rawDom = null;

		// Make sure the Harvester System is initialized.
		//Config.getInstance();

		String baseUrl = Fits.FITS_XML_DIR+"nlnz";
		Config.getInstance().setXMLBaseURL(baseUrl);

		// Get the appropriate adapter.
		DataAdapter adapter = AdapterFactory.getInstance().getAdapter(file);

		//The adapter's DTD to use for output
		String outDTD = adapter.getOutputType();

		//output stream to hold raw output from adapter
		ByteArrayOutputStream adapterOutput = new ByteArrayOutputStream(2048);

		//holder for the transformed adapter output
		//ByteArrayOutputStream tAdapterOutput = new ByteArrayOutputStream(2048);

		// Set up the parser context and listener to hold the adapter output
		ParserContext pContext = new ParserContext();

		ParserListener listener= new DTDXmlParserListener(adapterOutput, outDTD == null ? null
				: Config.getInstance().getXMLBaseURL() + "/" + outDTD);
		pContext.addListener(listener);

		// Attempt to harvest the metadata.
		try {
			// Extract the metadata.
			adapter.adapt(file, pContext);
			dom = saxBuilder.build(new StringReader(adapterOutput.toString()));
		}
		catch (JDOMException e) {
            logger.error("Error parsing NLNZ Metadata Extractor XML output: " + e.getClass().getName());
			throw new FitsToolException("Error parsing NLNZ Metadata Extractor XML output",e);
		}
		catch (Exception e) {
			// harvesting metadata failed
            logger.error("NLNZ Metadata Extractor error while harvesting file: " + e.getClass().getName());
			throw new FitsToolException("NLNZ Metadata Extractor error while harvesting file "+file.getName(),e);
		}
		finally {
			//done with the adapter output streams so close them
			try {
				adapterOutput.close();
				//tAdapterOutput.close();
			} catch (IOException e) {
			    logger.error("Error closing NLNZ Metadata Extractor XML output stream: " + e.getClass().getName());
				throw new FitsToolException("Error closing NLNZ Metadata Extractor XML output stream",e);
			}
		}

		//FileIdentity identity = null;
		Document fitsXml = null;
		if(dom != null) {
			String format = dom.getRootElement().getName();
			//String format = XmlUtils.getDomValue(dom,"Format");
			if(format != null) {
				String xsltTransform = (String)transformMap.get(format.toUpperCase());
				if(xsltTransform != null) {
					fitsXml = transform(nlnzFitsConfig+xsltTransform,dom);
				}
			}
		}

		//XmlUtils.printToConsole(dom);

		output = new ToolOutput(this,fitsXml,dom);
		duration = System.currentTimeMillis()-startTime;
        logger.debug("MetadataExtractor.extractInfo finished on " + file.getName());
		runStatus = RunStatus.SUCCESSFUL;
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
		if(format == null || mime.equalsIgnoreCase("file/unknown")) {
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

/* 
ll * Copyright 2009 Harvard University Library
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
package edu.harvard.hul.ois.fits.tools.nlnz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.apache.log4j.Logger;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.fx.ParserListener;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.harvester.DTDXmlParserListener;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.XsltTransformMap;

/**  The glue class for invoking the NLNZ Metadata Extractor under FITS.
 */
public class MetadataExtractor extends ToolBase {
	
    private final static String TOOL_NAME = "NLNZ Metadata Extractor";
    private final static String TOOL_VERSION = "3.4GA";
    private final static String TOOL_DATE = "12/21/2007";
    
	public final static String nlnzFitsConfig = Fits.FITS_XML+"nlnz"+File.separator+"fits"+File.separator;
	private boolean enabled = true;
    private static final Logger logger = Logger.getLogger(MetadataExtractor.class);
	
	public MetadataExtractor() throws FitsException {	
        logger.debug ("Initializing MetadataExtractor");
		info = new ToolInfo(TOOL_NAME,TOOL_VERSION,TOOL_DATE);
		transformMap = XsltTransformMap.getMap(nlnzFitsConfig+"nlnz_xslt_map.xml");
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("MetadataExtractor.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		Document dom = null;
		//Document rawDom = null;

		// Make sure the Harvester System is initialized.
		//Config.getInstance();
		
		Config.getInstance().setXMLBaseURL(Fits.FITS_XML+"nlnz");

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

			//transformer to convert raw output to nlnz_presmet format
			//TransformProcessor transformer = TransformProcessor.getInstance(outDTD, "nlnz_presmet.xsd");
						
			//get the adapter output as a byte array
			//byte[] adapterByteOutput = adapterOutput.toByteArray();
			
			//temporary to get output prior to nlnz xslt conversion
			//Document tmpDom = saxBuilder.build(new StringReader(new String(adapterByteOutput)));
			//XmlUtils.printToConsole(tmpDom);
		
			//transform adapter output byte array
			//transformer.transform(new ByteArrayInputStream(adapterByteOutput),tAdapterOutput);
			
			//convert the transformed output to a dom object
			//InputSource is = new InputSource(new ByteArrayInputStream(tAdapterOutput.toByteArray()));
			//is.setEncoding("UTF-8");
			
			dom = saxBuilder.build(new StringReader(adapterOutput.toString()));
			
			/*
			//convert the adapter byte array output to a dom object  
			is = new InputSource(new ByteArrayInputStream(adapterByteOutput));
			is.setEncoding("UTF-8");
			try {
				rawDom = docBuilder.parse(is);
			} catch (SAXException e) {
				e.printStackTrace();
			}*/
					
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

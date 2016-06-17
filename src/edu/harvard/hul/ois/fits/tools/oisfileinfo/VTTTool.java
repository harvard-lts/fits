//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.oisfileinfo;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.apache.log4j.Logger;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

/**
 * This is a class used to parse and validate VTT video caption files
 */
public class VTTTool extends ToolBase {

	private boolean enabled = true;
	private final static Namespace fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);
	private final static Namespace xsiNS = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	private static final Logger logger = Logger.getLogger(VTTTool.class);

	private final static String VTT_TOOL_VERSION = "0.1";
	private final static String VTT_IDENTIFIER = "WEBVTT";

	public VTTTool() throws FitsToolException {
		info.setName("VTT Tool");
		info.setVersion(VTT_TOOL_VERSION);
		info.setDate("02/23/16");
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
		logger.debug("VTTTool.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		Document doc = createXml(file);
		output = new ToolOutput(this,(Document)doc.clone(),doc);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
		logger.debug("VTTTool.extractInfo finishing on " + file.getName());
		return output;
	}

	private Document createXml(File file) throws FitsToolException {

		Element root = new Element("fits",fitsNS);
		root.setAttribute(new Attribute("schemaLocation","http://hul.harvard.edu/ois/xml/ns/fits/fits_output "+Fits.externalOutputSchema,xsiNS));

		if(file.getPath().toLowerCase().endsWith(".vtt")) {

			boolean isVtt = false;
			try	{

				Scanner scanner = new Scanner(new FileReader(file));
				try {
					// Should be on the 1st line
					String firstLine = scanner.nextLine();

					// Identifier should be on the 1st line
					if(firstLine.trim().contains(VTT_IDENTIFIER)) {
						isVtt = true;
					}
				}
				finally {
					//ensure the underlying stream is always closed
					scanner.close();
				}
			}
			catch (Exception e)	{
				throw new FitsToolException("Error parsing VTT file", e);
			}

			if(isVtt) {
				Element identification = new Element("identification",fitsNS);
				Element identity = new Element("identity",fitsNS);
				identity.setAttribute("format","WebVTT");
				identity.setAttribute("mimetype","text/vtt");
				identification.addContent(identity);
				root.addContent(identification);

				Element fileInfo = new Element("fileinfo",fitsNS);

				//add file info section to root
				root.addContent(fileInfo);

				// There really isn't any metadata to set
				// Element metadata = new Element("metadata",fitsNS);
				// Element textMetadata = new Element("text",fitsNS);

				////add to metadata section
				//metadata.addContent(textMetadata);

				////add metadata section to root
				//root.addContent(metadata);
			}


		}

		return new Document(root);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}

}

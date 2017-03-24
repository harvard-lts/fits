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
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import com.twmacinta.util.MD5;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

/** The FileInfo tool uses Java system calls to get basic information about
 *  a file, as well as calculating an MD5 digest.
 *
 *  @see <a href="http://www.twmacinta.com/myjava/fast_md5.php">Fast MD5 Implementation in Java</a>
 */
public class FileInfo extends ToolBase {

    private final static String TOOL_NAME = "OIS File Information";
    private final static String TOOL_VERSION = "0.2";
    private final static String TOOL_DATE = "12/13/2013";

    private static Logger logger = Logger.getLogger(FileInfo.class);

    private final static Namespace xsiNS = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
    private final static Namespace fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);

    private boolean enabled = true;
    private Fits fits;

	public FileInfo(Fits fits) throws FitsToolException{
		super();
        logger.debug ("Initializing FileInfo");
        this.fits = fits;
        info.setName(TOOL_NAME);
		info.setVersion(TOOL_VERSION);
		info.setDate(TOOL_DATE);
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("FileInfo.extractInfo starting on " + file.getName());
        long startTime = System.currentTimeMillis();
		Document doc = createXml(file);
		output = new ToolOutput(this,(Document)doc.clone(),doc, fits);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("FileInfo.extractInfo finished on " + file.getName());
		return output;
	}

	private Document createXml(File file) throws FitsToolException {


		Element root = new Element("fits",fitsNS);
		root.setAttribute(new Attribute("schemaLocation",
										"http://hul.harvard.edu/ois/xml/ns/fits/fits_output " + fits.getExternalOutputSchema(),
										xsiNS));
		//fileinfo section
		Element fileInfo = new Element("fileinfo",fitsNS);
		//filepath
		Element filepath = new Element("filepath",fitsNS);
		filepath.setText(file.getAbsolutePath());
		fileInfo.addContent(filepath);
		//filename
		Element fileName = new Element("filename",fitsNS);
		fileName.setText(file.getName());
		fileInfo.addContent(fileName);
		//size
		Element size = new Element("size",fitsNS);
		size.setText(String.valueOf(file.length()));
		fileInfo.addContent(size);
		//Calculate the MD5 checksum
		if (fits.getConfig().getBoolean("output.enable-checksum")) {
			@SuppressWarnings("unchecked")
			List<String> checsumExcludes = (List<String>)(List<?>)fits.getConfig().getList("output.checksum-exclusions[@exclude-exts]");
			String ext = FilenameUtils.getExtension(file.getPath());
			if(!hasExcludedExtensionForMD5(ext, checsumExcludes)) {
				try {
					String md5Hash = MD5.asHex(MD5.getHash(new File(file.getPath())));
					Element signature = new Element("md5checksum",fitsNS);
					signature.setText(md5Hash);
					fileInfo.addContent(signature);
				} catch (IOException e) {
					throw new FitsToolException("Could not calculate the MD5 for "+file.getPath(),e);
				}
			}

		}
		//fslastmodified
		Element fslastmodified = new Element("fslastmodified",fitsNS);
		fslastmodified.setText(String.valueOf(file.lastModified()));
		fileInfo.addContent(fslastmodified);
		root.addContent(fileInfo);

		return new Document(root);
    }

	// NOTE: This check is separate from the tool extension exclusions
	public boolean hasExcludedExtensionForMD5(String ext, List<String> excludedExtensions) {
		for(String extension : excludedExtensions) {
			if(extension.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}

	public Boolean canIdentify() {
		return false;
	}


	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}
}

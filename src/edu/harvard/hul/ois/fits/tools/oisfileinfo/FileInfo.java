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
package edu.harvard.hul.ois.fits.tools.oisfileinfo;

import java.io.File;
import java.io.IOException;

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
	
	public FileInfo() throws FitsToolException{
        logger.debug ("Initializing FileInfo");
        info.setName(TOOL_NAME);
		info.setVersion(TOOL_VERSION);
		info.setDate(TOOL_DATE);
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {	
        logger.debug("FileInfo.extractInfo starting on " + file.getName());
        long startTime = System.currentTimeMillis();
		Document doc = createXml(file);
		output = new ToolOutput(this,(Document)doc.clone(),doc);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug("FileInfo.extractInfo finished on " + file.getName());
		return output;
	}
	
	private Document createXml(File file) throws FitsToolException {
		
		
		Element root = new Element("fits",fitsNS);
		root.setAttribute(new Attribute("schemaLocation","http://hul.harvard.edu/ois/xml/ns/fits/fits_output "+Fits.externalOutputSchema,xsiNS));
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
		if (Fits.config.getBoolean("output.enable-checksum")) {
			try {
				String md5Hash = MD5.asHex(MD5.getHash(new File(file.getPath())));
				Element signature = new Element("md5checksum",fitsNS);
				signature.setText(md5Hash);
				fileInfo.addContent(signature);
			} catch (IOException e) {
				throw new FitsToolException("Could not calculate the MD5 for "+file.getPath(),e);
			}
		}
		//fslastmodified
		Element fslastmodified = new Element("fslastmodified",fitsNS);
		fslastmodified.setText(String.valueOf(file.lastModified()));
		fileInfo.addContent(fslastmodified);
		root.addContent(fileInfo);
		
		return new Document(root);
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

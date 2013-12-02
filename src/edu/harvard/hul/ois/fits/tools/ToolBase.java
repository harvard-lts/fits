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
package edu.harvard.hul.ois.fits.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.jdom.DocumentWrapper;

import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.Document;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.identity.ToolIdentity;

/** An abstract class implementing the Tool interface, the base
 *  for all FITS tools. 
 */
public abstract class ToolBase implements Tool {
	
	protected ToolInfo info = null;
	protected ToolOutput output = null;
	protected SAXBuilder saxBuilder;
	protected TransformerFactory tFactory;
    protected Hashtable<String,String> transformMap;
    protected File inputFile;
    protected long duration;
    protected RunStatus runStatus;
    protected String name;
    
    private List<String> excludedExtensions;
    private List<String> includedExtensions;
    
    private Exception caughtException;
	
	public ToolBase() throws FitsToolException {
		info = new ToolInfo();
		tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
		saxBuilder = new SAXBuilder();
		excludedExtensions = new ArrayList<String>();
		includedExtensions = new ArrayList<String>();
	}
	
	/** Returns the name. This should be the name of the tool class,
	 *  without the package prefix.
	 */
	public String getName () {
	    return name;
	}
	
	/** Sets the name. This should be the name of the tool class, without
	 *  the package prefix (e.g., "Droid" or "Jhove").
	 */
	public void setName (String n) {
	    name = n;
	}
	
	/** Returns identifying information about the tool. */
	public ToolInfo getToolInfo() {
		return info;
	}
	
	/** Returns true if the tool provides identification information.
	 *  Override if a value other than true should ever be returned. */
	public Boolean canIdentify() {
		return true;
	}
	
	/** Specifies the file to be processed. */
	public void setInputFile(File file) {
		inputFile = file;
	}
	
	public boolean isIdentityKnown(ToolIdentity identity) {
		if(!canIdentify()) {
			return false;
		}
		//identity and mimetype must not be null or empty strings for an identity to be "known"		
		if(identity == null
				|| identity.getMime() == null 
				|| identity.getMime().length() == 0
				|| identity.getFormat() == null 
				|| identity.getFormat().length() == 0) {
			return false;
		}
		String format = identity.getFormat();
		String mime = identity.getMime();
		if(format.equals("Unknown Binary") || mime.equals("application/octet-stream")) {
			return false;
		}
		else {
			return true;
		}
	}
		
	public Boolean validate(File files_or_dir, ToolIdentity identity) {
		return null;
	}

	public Document transform(String xslt, Document input) throws FitsToolException {
		Document doc = null;
		try {
			Configuration config = ((TransformerFactoryImpl)tFactory).getConfiguration();
			DocumentWrapper docw = new DocumentWrapper(input,null,config);
			JDOMResult out = new JDOMResult();
			Templates templates = tFactory.newTemplates(new StreamSource(xslt));
			Transformer transformer = templates.newTransformer();
			transformer.transform(docw, out);
			doc = out.getDocument();
		}
		catch(Exception e) {
			throw new FitsToolException(info.getName()+": Error converting output using "+xslt,e);
		}
		return doc;
	}
	
	public void addExcludedExtension(String ext) {
		excludedExtensions.add(ext);
	}
	
	public boolean hasExcludedExtension(String ext) {
		for(String extension : excludedExtensions) {
			if(extension.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}
	
	public void addIncludedExtension(String ext) {
		includedExtensions.add(ext);
	}
	
	public boolean hasIncludedExtensions() {
		if(includedExtensions.size() > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hasExcludedExtensions() {
		if(excludedExtensions.size() > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hasIncludedExtension(String ext) {
		for(String extension : includedExtensions) {
			if(extension.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}
	
	/** Take the list of tools-used items and modify the included and
	 *  excluded extensions if applicable. included-exts and
	 *  excluded-exts attributes take priority.
	 */
	public void applyToolsUsed (List<ToolBelt.ToolsUsedItem> toolsUsedItems) {
	    for (ToolBelt.ToolsUsedItem tui : toolsUsedItems) {
 	        if (tui.toolNames.contains (name)) {
	            // If a tool is
	            // listed by tui, we add its extensions to
	            // included-extensions unless it's in excluded-extensions.
	            // If a tool is not listed by tui, we add its extensions
	            // to excluded-extensions unless it's in included-extensions.
 	            // If there are no included extensions, that means all extensions
 	            // are allowed unless excluded, so we don't add anything.
	            for (String ext : tui.extensions) {
	                if (hasIncludedExtensions()) {
    	                if (!containsIgnoreCase(includedExtensions,ext) &&
    	                        !containsIgnoreCase(excludedExtensions,ext)) {
    	                    includedExtensions.add (ext);
    	                }
	                }
	            }
	        }
	        else {
	            for (String ext : tui.extensions) {
	                if (!containsIgnoreCase(excludedExtensions,ext) &&
	                        !containsIgnoreCase(includedExtensions,ext)) {
	                    excludedExtensions.add(ext);
	                }
	            }
	        }
	    }
	}
	
	/* A case-independent surrogate for List.contains */
	private boolean containsIgnoreCase (List<String> lst, String s) {
	    for (String s1 : lst) {
	        if (s1.equalsIgnoreCase (s)) {
	            return true;
	        }
	    }
	    return false;
	}
	public ToolOutput getOutput() {
		return output;
	}
	
	public void resetOutput() {
		output = null;
	}
	
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public RunStatus getRunStatus() { 
		return runStatus;
	}
	
	public void setRunStatus(RunStatus runStatus) {
		this.runStatus = runStatus;
	}
	
	/** Save an exception for reporting. */
	public void setCaughtException (Exception e) {
	    caughtException = e;
	}
	
	/** Append any reported exceptions to a master list.
	 *  This is called after the tool has finished running.
	 *  
	 *  @param exceptions   List of Exceptions. Exceptions may be appended
	 *         to it by this call.
	 */
    public void addExceptions(List<Exception> exceptions) {
        if (caughtException != null) {
            exceptions.add (caughtException);
        }
    }

	public void run() {
		try {
			//java.util.Date time = new java.util.Date();
			//System.out.println(new java.sql.Time(time.getTime()) + " STARTING "+this.getClass());
			output = extractInfo(inputFile);
			//java.util.Date time2 = new java.util.Date();
			//System.out.println(new java.sql.Time(time2.getTime()) +" FINISHED "+this.getClass());
		} catch (FitsToolException e) {
		    setCaughtException (e);
			e.printStackTrace();
			//System.err.println(e.getMessage());
		}
	}

}

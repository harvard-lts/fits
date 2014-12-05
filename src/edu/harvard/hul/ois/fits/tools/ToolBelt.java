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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;

public class ToolBelt {
	
    private static Logger logger = Logger.getLogger(ToolBelt.class);
    
    /** The representation of one tools-used element in the config file */
    public class ToolsUsedItem {
        public List<String> extensions;
        public List<String> toolNames;
        
        public ToolsUsedItem (List<String> exts, List<String> tools) {
            extensions = exts;
            toolNames = tools;
        }
    }
    
	private List<Tool> tools;
	
	public ToolBelt(String configFile) throws FitsConfigurationException {
		XMLConfiguration config = null;
		try {
			config = new XMLConfiguration(configFile);
		} catch (ConfigurationException e) {
			throw new FitsConfigurationException("Error reading "+configFile,e);
		}
	
		// Collect the tools-used elements
		List<ToolsUsedItem> toolsUsedList = processToolsUsed(config);
		
		tools = new ArrayList<Tool>();
		
		// get number of tools
		int size = config.getList("tools.tool[@class]").size();
		// for each tools get the class path and any excluded extensions
		for(int i=0;i<size;i++) {
			String tClass = config.getString("tools.tool("+i+")[@class]");
			@SuppressWarnings("unchecked")
			List<String> excludes = config.getList("tools.tool("+i+")[@exclude-exts]");
			@SuppressWarnings("unchecked")
			List<String> includes = config.getList("tools.tool("+i+")[@include-exts]");
			Tool t = null;
			try {
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(tClass);
				t = (Tool)c.newInstance();
			}
			catch(Exception e) {
			    // Can't use this tool, but continue anyway.
				//throw new FitsConfigurationException("Error initializing "+tClass,e);
			    logger.error ("Thread "+Thread.currentThread().getId() +
			    				" error initializing " + tClass +  
			    				": " + e.getClass().getName() + 
			    				"  Message: " + e.getMessage());
			    continue;
			}
			if(t != null) {
			    t.setName(bareClassName(tClass));
				for(String ext : excludes) {
					t.addExcludedExtension(ext);
				}
				for(String ext : includes) {
					t.addIncludedExtension(ext);
				}
				// Modify included and excluded extensions by tools-used
                t.applyToolsUsed (toolsUsedList);
				tools.add(t);
			}
		}
	}
	
	public List<Tool> getTools() {
		return tools;
	}
	
	public void printToolInfo() {
		printToolInfo(false,System.out);
	}
	public void printToolInfo(boolean sysInfo, PrintStream p) {
		if(sysInfo) {
			//system info
			p.println("OS Name = "+System.getProperty("os.name"));
			p.println("OS Arch = "+System.getProperty("os.arch"));
			p.println("OS Version = "+System.getProperty("os.version"));
			p.println("------------------------------------");
		}
		
		for(Tool t : tools) {
			p.print(t.getToolInfo().print());
		}

	}
	
	/* Process the tools-used elements and return a list of
	 * ... something */
	private List<ToolsUsedItem> processToolsUsed (XMLConfiguration config) {
	    int size = config.getList("tools-used[@exts]").size();
	    List<ToolsUsedItem> results = new ArrayList<ToolsUsedItem> (size);
	    for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            List<String> exts = config.getList("tools-used("+i+")[@exts]");
            @SuppressWarnings("unchecked")
            List<String> tools = config.getList("tools-used("+i+")[@tools]");
            results.add (new ToolsUsedItem (exts, tools));
	    }
	    return results;      // TODO stub
	}
	
	/* Extract the last component of the class name to use as the 
	 * tool's name field */
	private String bareClassName(String cname) {
	    int n = cname.lastIndexOf(".");
	    return cname.substring(n + 1);
	}
}

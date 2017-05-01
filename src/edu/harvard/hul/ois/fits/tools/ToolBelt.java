//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools;


import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.tools.utils.ParentLastClassLoader;

public class ToolBelt {

	// Represent the URL of the fit.jar file (or class directory) where this file lives.
	// This is used for building custom class loaders representing all FITS class files.
	private URL fitsUrl;

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

	/**
	 * Constructor
	 *
	 * @param config XMLConfiguration of FITS configuration file.
	 * @param fits Fits for referencing member variables.
	 */
	public ToolBelt(XMLConfiguration config, Fits fits) {
		init(config, fits);
	}

	/*
	 * Common initialization of all constructors.
	 */
	private void init(XMLConfiguration config, Fits fits) {

		fitsUrl = getClass().getProtectionDomain().getCodeSource().getLocation();

		// Collect the tools-used elements
		List<ToolsUsedItem> toolsUsedList = processToolsUsed(config);

		tools = new ArrayList<Tool>();

		// get number of tools
		int size = config.getList("tools.tool[@class]").size();
		ClassLoader savedClassLoader = ToolBelt.class.getClassLoader();
		// for each tools get the class path and any excluded extensions
		for(int i=0;i<size;i++) {
			String tClass = config.getString("tools.tool("+i+")[@class]");
			@SuppressWarnings("unchecked")
			List<String> excludes = (List<String>)(List<?>)config.getList("tools.tool("+i+")[@exclude-exts]");
			@SuppressWarnings("unchecked")
			List<String> includes = (List<String>)(List<?>)config.getList("tools.tool("+i+")[@include-exts]");
			@SuppressWarnings("unchecked")
			List<String> classpathDirs = (List<String>)(List<?>)config.getList("tools.tool("+i+")[@classpath-dirs]");

			ClassLoader toolClassLoader = null;
			try {
				// If fits.xml Tool element contains values in the classpath-dirs attribute that return files,
				// it's necessary to create and use a custom class loader.
				// Otherwise, just use the system (application) class loader.
				// Will need to replace original class loader after (possibly) using a different one with current thread.
				toolClassLoader = createClassLoader(classpathDirs);
				if (toolClassLoader != null) {
					Thread.currentThread().setContextClassLoader(toolClassLoader);
				} else {
					toolClassLoader = savedClassLoader;
				}

				logger.debug("Will attempt to load class: " + tClass + " -- with ClassLoader: " + toolClassLoader.getClass().getName());
				Class<?> toolClass = Class.forName(tClass, true, toolClassLoader);

				// Looooong debugging block if using custom class loader
				// verify the tool's class loader is the custom one and that it's class loader has
				// loaded the Tool interface from the main class loader so it can be cast here to Tool
				if (toolClassLoader != savedClassLoader && logger.isTraceEnabled()) { // yes, we want an this type of exact equals comparison
					ClassLoader tcl = toolClass.getClassLoader();
					logger.trace("Specific tool: " + tClass + " -- ClassLoader: " + tcl);

					// Tool interface loaded from custom class loader
					Class<?> toolInterfaceCustomClassLoader = Class.forName("edu.harvard.hul.ois.fits.tools.Tool", true, tcl);
					logger.trace("Tool ClassLoader via custom class loader: " + toolInterfaceCustomClassLoader.getClassLoader());

					// Tool interface loaded from system class loader which will be used for casting immediately below.
					Class<?> toolInterfaceClass = Tool.class.getClassLoader().loadClass(Tool.class.getName());
					logger.trace("Tool ClassLoader " + Tool.class.getName() + " -- ClassLoader: " + Tool.class.getClassLoader());

					// verify that specific tool from custom class loader can be assigned to Tool
					boolean isAssignable = toolInterfaceClass.isAssignableFrom(toolClass);
					logger.trace("Tool from custom ClassLoader isAssignableFrom(toolClass): " + isAssignable);
				}

				Tool t = createToolClassInstance(toolClass, fits);

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
			} catch(ReflectiveOperationException |
					MalformedURLException ex) {
			    // Can't use this tool, but continue anyway.
				//throw new FitsConfigurationException("Error initializing "+tClass,e);
			    logger.error ("Thread "+Thread.currentThread().getId() +
			    				" error initializing " + tClass +
			    				": " + ex.getClass().getName() +
			    				"  Message: " + ex.getMessage(), ex);
			    continue;
			} finally {
				// ***** IMPORTANT: set back original ClassLoader if changed *****
				if (Thread.currentThread().getContextClassLoader() != savedClassLoader) {
					Thread.currentThread().setContextClassLoader(savedClassLoader);
				}
			}
		}
	}
	
	/*
	 * Instantiate a Tool class using Reflection by passing Fits into the constructor.
	 * Note: All Tool class implementations can have a 1-argument constructor with Fits as the argument.
	 * If it does not, then the standard newInstance() method fall-back will be used.
	 */
	private Tool createToolClassInstance(Class<?> toolClass, Fits fits) throws ReflectiveOperationException {
		Object instanceOfTheClass = null;
		try {
			Constructor<?> ctor = toolClass.getConstructor(Fits.class);
			instanceOfTheClass = ctor.newInstance(fits);
			logger.debug("1-arg constructor for instantiating tool class: " + toolClass.getName());
		} catch (ReflectiveOperationException e) {
			// now try a no-arg constructor
			logger.debug("No Fits 1-arg constructor for tool class: " + toolClass.getName() + " -- trying no-arg constructor...");
			instanceOfTheClass = toolClass.newInstance();
			logger.debug("no-arg constructor for instantiating tool class: " + toolClass.getName());
		}
		return (Tool)instanceOfTheClass;
	}

	public List<Tool> getTools() {
		return tools;
	}

	public void printToolInfo(boolean includeSysInfo) {
		if(includeSysInfo) {
			//system info
			logger.info("OS Name = "+System.getProperty("os.name"));
			logger.info("OS Arch = "+System.getProperty("os.arch"));
			logger.info("OS Version = "+System.getProperty("os.version"));
			logger.info("------------------------------------");
		}

		for(Tool t : tools) {
			logger.info(t.getToolInfo().print());
		}

	}

	/* Process the tools-used elements and return a list of
	 * ... something */
	private List<ToolsUsedItem> processToolsUsed (XMLConfiguration config) {
	    int size = config.getList("tools-used[@exts]").size();
	    List<ToolsUsedItem> results = new ArrayList<ToolsUsedItem> (size);
	    for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            List<String> exts = (List<String>)(List<?>)config.getList("tools-used("+i+")[@exts]");
            @SuppressWarnings("unchecked")
            List<String> tools = (List<String>)(List<?>)config.getList("tools-used("+i+")[@tools]");
            results.add (new ToolsUsedItem (exts, tools));
	    }
	    return results;
	}

	/* Extract the last component of the class name to use as the
	 * tool's name field */
	private String bareClassName(String cname) {
	    int n = cname.lastIndexOf(".");
	    return cname.substring(n + 1);
	}

	/*
	 * Create a custom class loader specifically for a set of resources as designated by a list of directories
	 * which are specified for each tool in fits.xml. If the parameter is null or empty OR
	 * there are no resources within any of the list of given directories then this method will return null.
	 *
	 * @return custom class loader ONLY if JAR files in tool-specific lib directory; <code>null</code> otherwise.
	 */
	private ClassLoader createClassLoader(List<String> classpathDirs) throws MalformedURLException {

		if (classpathDirs == null || classpathDirs.isEmpty()) {
			return null;
		}

		// collect all files from all specified directories
		List<URL> directoriesUrls = new ArrayList<URL>();
		for (String dir : classpathDirs) {
			List<URL> urls = gatherClassLoaderUrls(null, dir);
			// special case: If a root directory contains artifacts then add that root directory
			// so as to create a custom class loader.
			if (urls != null & !urls.isEmpty()) {
				File dirFile = getFileFromName(dir);
				urls.add(  dirFile.toURI().toURL() );
			}
			directoriesUrls.addAll( urls );
		}

		// If nothing returned from directories then return null; Don't create ClassLoader if nothing to load.
		if (directoriesUrls.isEmpty()) {
			return null;
		}

		// Create list of resources for custom ClassLoader.
		List<URL> classLoaderUrls = new ArrayList<URL>();
		// Must always add FITS classes first
		classLoaderUrls.add(fitsUrl);
		// add all other resources next
		classLoaderUrls.addAll(directoriesUrls);
		logger.debug("URL's" + classLoaderUrls);

		final ParentLastClassLoader cl = new ParentLastClassLoader(classLoaderUrls);

		return cl;
	}

	/*
	 * Recursive method to create a list of URL's of all files in a directory and all of its sub-directories.
	 * All files that end in ".txt" or begin with "." will be ignored. Also, simple directory URL's will not be added.
	 */
	private List<URL> gatherClassLoaderUrls(List<URL> urls, String rootDir) throws MalformedURLException {

		if (urls == null) {
			urls = new ArrayList<URL>();
		}

		File dirFile = getFileFromName(rootDir);
		File[] directoryListing = dirFile.listFiles();
		if (directoryListing != null) {
			for (File file : directoryListing) {
				// recursive call to sub-directories
				if (file.isDirectory()) {
					gatherClassLoaderUrls(urls, rootDir + File.separator + file.getName());
					logger.debug("finished with directory: " + file.getAbsolutePath());

				} else if (!file.exists() || !file.isFile() || !file.canRead() ||
						file.getName().endsWith(".txt") ||file.getName().startsWith(".", 0)) {
					// ignoring any .txt files -- used as placeholder for directories, OSX .DS_Store, etc.
					logger.debug("Not processing file: " + file.getName());
					continue;
				}
				urls.add( file.toURI().toURL() );
			}
		}
		return urls;
	}

	/*
	 * Creates and returns a File object from a full path to a directory (or file).
	 */
	private File getFileFromName(String dirName) throws MalformedURLException {

		String fullPathPrefix = StringUtils.isEmpty(Fits.FITS_HOME) ? "" : Fits.FITS_HOME + File.separator;
		File dirFile = new File(fullPathPrefix + dirName);
		return dirFile;
	}
}

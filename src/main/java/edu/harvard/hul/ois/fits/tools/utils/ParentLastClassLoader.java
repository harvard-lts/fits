//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom class loader that attempts to load classes/resources from a set of URL's first before
 * delegating to the parent class loader if not found. The URL's can be JAR files or configuration files.
 * The bulk of the code from this class is taken from https://dzone.com/articles/java-classloader-handling .
 * Additionally, in order to circumvent parent-last class loading, there is a list of fully-qualified
 * class names and packages to be loaded by the parent loader first. Any class that either matches one of
 * these names or is a member of one of these packages is delegated to the parent class loader.
 * This allows for avoiding a ClassCastException when casting classes loaded from different class loaders.
 *
 * A use case is where this class loader loads an interface and a class that it implements. If this class
 * is passed to a thread loaded by the system class loader and is then cast to the same-named interface
 * loaded by the system class loader, there will be a ClassCastException. By attempting to load the interface
 * listed in the "exclusion list" of this custom class loader it will instead be loaded from the system
 * class loader. As a result the class can then be cast to the interface contained within the thread loaded
 * by the system class loader.
 *
 * Note: All logging statements are wrapped in a check to see if logging is enabled since this class is used
 * intensively. Even if a logging level is not enabled the full String that *would* be logged needs to be created
 * anyway causing unnecessary CPU load.
 *
 * @author David Neiman
 */
public class ParentLastClassLoader extends ClassLoader {

    private static final Logger logger = LoggerFactory.getLogger( ParentLastClassLoader.class );

    private static List<String> loadByParentClassLoader;
	private ChildClassLoader childClassLoader;

	static {
		loadByParentClassLoader = new ArrayList<String>();
		loadByParentClassLoader.add("edu.harvard.hul.ois.fits.Fits"); // So there's access to FITS_HOME from all class loaders.
		loadByParentClassLoader.add("edu.harvard.hul.ois.fits.exceptions.FitsToolException");
		loadByParentClassLoader.add("edu.harvard.hul.ois.fits.tools.Tool");
		loadByParentClassLoader.add("edu.harvard.hul.ois.fits.tools.ToolInfo");
		loadByParentClassLoader.add("org.apache.xerces");
		loadByParentClassLoader.add("org.w3c");
		loadByParentClassLoader.add("org.jdom");
	}

	/**
	 * Construct this class loader with the list of URL's which will be searched
	 * first for loading classes and resources.
	 *
	 * @param classpathUrls List of URL's.
	 */
	public ParentLastClassLoader(List<URL> classpathUrls){
		super(Thread.currentThread().getContextClassLoader());
		URL[] urls = classpathUrls.toArray(new URL[classpathUrls.size()]);
		childClassLoader = new ChildClassLoader(urls, new DetectClass(this.getParent()));
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException{
		try	{
			// Attempt to load class locally first
			if (logger.isTraceEnabled()) {
				logger.trace("looking to loadClass() class name: " + name);
			}
			Class<?> clazz = childClassLoader.findClass(name);
			return clazz;
		} catch (ClassNotFoundException e) {
			if (logger.isTraceEnabled()) {
				logger.trace("ClassNotFoundException caught -- attempting to loadClass() in class loader: " +
						super.getClass().getSimpleName());
			}
			return super.loadClass(name, resolve);
		}
	}

	@Override
	 public URL getResource(String name) {
		// Attempt to load resource locally first.
		URL url = childClassLoader.findResource(name);
		if (logger.isTraceEnabled()) {
			logger.trace("looking to getResource() resource name: " + name);
			logger.trace("found resource: " + name);
		}
		return url;
	}

	// Class that wraps URLClassLoader so as to check locally first rather than parent first.
	private static class ChildClassLoader extends URLClassLoader {

		private DetectClass realParent;

		private ChildClassLoader(URL[] urls, DetectClass realParent) {
			super(urls, null);
			this.realParent = realParent;
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			try	{
				// If attempting to load one of "exclusion" classes, go directly to parent class loader.
				if (loadByParentClassLoader.contains(name) || beginsWith(name)) {
					if (logger.isTraceEnabled()) {
						logger.trace("****** SPECIAL -- going directly to parent ClassLoader for class : " + name);
					}
					Class<?> clazz = realParent.loadClass(name);
					if (logger.isTraceEnabled()) {
						logger.trace("***** SPECIAL --  Found in : " + realParent.getClass().getSimpleName());
					}
					return clazz;
				}

				// See if class has already been loaded
				if (logger.isTraceEnabled()) {
					logger.trace(" findLoadedClass(name) to see if class already loaded: " + name);
				}
				Class<?> loaded = super.findLoadedClass(name);
				if (loaded != null) {
					if (logger.isTraceEnabled()) {
						logger.trace("Already loaded - found class in: " + this.getClass().getSimpleName());
					}
					return loaded;
				}

				// Not already loaded so attempt to load.
				String superClassLoaderName = super.getClass().getSimpleName();
				if (logger.isTraceEnabled()) {
					logger.trace("NOT already loaded: attemt to load from: " + superClassLoaderName);
				}
				Class<?> clazz = super.findClass(name);
				// will only reach here if class found, otherwise ClassNotFoundException will have been thrown
				if (logger.isTraceEnabled()) {
					logger.trace("loaded class in: " + super.getClass().getSimpleName());
				}
				return clazz;
			} catch (ClassNotFoundException e){
				String parentClassLoaderName = realParent.getParent().getClass().getSimpleName();
				if (logger.isTraceEnabled()) {
					logger.trace("ClassNotFoundException, attempt to load from: " + parentClassLoaderName);
				}
				Class<?> clazz = realParent.loadClass(name);
				if (logger.isTraceEnabled()) {
					logger.trace("Loaded class " + name + " from: " + parentClassLoaderName);
				}
				return clazz;
			}
		}

		@Override
		public URL findResource(String name) {
			URL url = super.findResource(name);
			if (logger.isTraceEnabled()) {
				logger.trace("looking in findResource() for: " + name + " in class loader: " + super.getClass().getSimpleName());
				logger.trace( (url == null ? "NOT" : "") + " found resource: " + name );
			}
			if (url == null) {
				url = realParent.getResource(name);
				if (logger.isTraceEnabled()) {
					logger.trace("looking in parent class loader: " + realParent.getClass().getName() +
							" -- found: " + (url == null ? "NO" : "YES"));
				}
			}
			return url;
		}
	}

	// Wrapper for parent class loader.
	private static class DetectClass extends ClassLoader {

		private DetectClass(ClassLoader parent) {
			super(parent);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}
	}

	// check if class name begins with any of the package names in "exclusion" list
	private static boolean beginsWith(String className) {
		boolean isContained = false;
		if (className != null) {
			for (String val : loadByParentClassLoader) {
				if (className.startsWith(val)) {
					isContained = true;
					break;
				}
			}
		}
		return isContained;
	}
}

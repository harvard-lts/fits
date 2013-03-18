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
package edu.harvard.hul.ois.fits.tools.utils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;

public class XsltTransformMap {
	
	public static Hashtable<String,String> getMap(String config) throws FitsConfigurationException {
		Hashtable<String,String> mappings = new Hashtable<String,String>();
		XMLConfiguration conf = null;
		try {
			conf = new XMLConfiguration(config);
		} catch (ConfigurationException e) {
			throw new FitsConfigurationException("Error reading "+config+"fits.xml",e);
		}
		
		@SuppressWarnings("rawtypes")
		List fields = conf.configurationsAt("map");
		for(@SuppressWarnings("rawtypes")
		Iterator it = fields.iterator(); it.hasNext();)	{
		    HierarchicalConfiguration sub = (HierarchicalConfiguration) it.next();
		    // sub contains now all data about a single field
		    String format = sub.getString("[@format]");
		    String transform = sub.getString("[@transform]");
		    mappings.put(format,transform);
		}
		return mappings;
	}

}

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

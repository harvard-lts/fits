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
package edu.harvard.hul.ois.fits.consolidation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.ToolIdentity;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.identity.FormatVersion;
import edu.harvard.hul.ois.fits.tools.Tool;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.XmlUtils;

public class OISConsolidator implements ToolOutputConsolidator {

    private static Namespace xsiNS = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");        

    private Logger logger = Logger.getLogger(this.getClass());
    
	private boolean reportConflicts;
	private boolean displayToolOutput;
	private Document formatTree;
	
	private final static int CONFLICT = 0;
	private final static int SINGLE_RESULT = 1;
	private final static int ALL_AGREE = 2;
	
	static private final String REAL_NUMBER = "^[-+]?\\d+(\\.\\d+)?$";
	
	static private final List<String> repeatableElements =  new ArrayList<String>(Arrays.asList("linebreak"));  ;
	
	private final static Namespace fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);
	
	public OISConsolidator() throws FitsConfigurationException {
		
		reportConflicts = Fits.config.getBoolean("output.report-conflicts",true);
		displayToolOutput = Fits.config.getBoolean("output.display-tool-output",false);
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			formatTree = saxBuilder.build(Fits.FITS_XML+"fits_format_tree.xml");
		} catch (Exception e) {
			throw new FitsConfigurationException("",e);
		} 
		//fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);
	}

	private Element findAnElement(List<ToolOutput> results, String xpath_query, boolean useChildren) {
		for(ToolOutput result : results) {			
			Document dom = result.getFitsXml();
			try {
				//only look at non null dom structures
				if(dom != null) {
					XPath xpath = XPath.newInstance(xpath_query);
					xpath.addNamespace("fits",Fits.XML_NAMESPACE);
					Element e = (Element)xpath.selectSingleNode(dom);
					if(e != null && e.getChildren().size() > 0) {
						if(useChildren) {
							e = (Element)e.getChildren().get(0);
						}
						List children = e.getChildren();
						if(children.size()>0) {
							Element child = (Element)children.get(0);
							return child;
						}
					}
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}	
		}
		return null;
	}
	
	/**
	 * Removes null and unknown output from ToolOutput results
	 * @param results
	 * @return
	 */
	private List<ToolOutput> cullResults(List<ToolOutput> results) {
		List<ToolOutput> newResults = new ArrayList<ToolOutput>();
		for(ToolOutput result : results) {
			if(result != null) {
				Tool t = result.getTool();
				//if the tool can't identify files, or if it can and all identities are good
				if(!t.canIdentify() || (t.canIdentify() && allIdentitiesAreGood(result))) {
					newResults.add(result);
				}
				else {
					logger.debug("tossing " + t.getName() + " identification because of invalid identification");
				}
			}
		}
		return newResults;
	}
	
	/**
	 * Skips null identities and returns the first tools unknown Identities that are found
	 * @param results
	 * @return
	 */
	private List<FitsIdentity> getFirstUnknownIdentity(List<ToolOutput> results) {
		List<FitsIdentity> identities = new ArrayList<FitsIdentity>();
		for(ToolOutput result : results) {	
			//if result is null skip it
			if(result == null) {
				continue;
			}
			List<ToolIdentity> toolIdentities = result.getFileIdentity();
			if(result.getTool().canIdentify() && toolIdentities != null && toolIdentities.size() > 0) {
				for(ToolIdentity fileIdent : toolIdentities) {	
					identities.add(new FitsIdentity(fileIdent));
				}			
				break;
			}
		}
		return identities;
	}
	
	/**
	 * Skips null identities and returns the first tools unknown Identities that are found
	 * @param results
	 * @return
	 */
	private List<FitsIdentity> getFirstPartialIdentity(List<ToolOutput> results) {
		List<FitsIdentity> identities = new ArrayList<FitsIdentity>();
		for(ToolOutput result : results) {	
			//if result is null skip it
			if(result == null) {
				continue;
			}
			List<ToolIdentity> toolIdentities = result.getFileIdentity();
			if(result.getTool().canIdentify() && toolIdentities != null && toolIdentities.size() > 0) {			
				if(isPartialIdentity(toolIdentities)) {
					for(ToolIdentity fileIdent : toolIdentities) {	
						identities.add(new FitsIdentity(fileIdent));
					}
					break;
				}
				
			}
		}
		return identities;
	}
	
	private boolean allIdentitiesAreGood(ToolOutput result) {
		List<ToolIdentity> identities = result.getFileIdentity();
		if(identities.size() == 0) {
			return false;
		}
		Tool t = result.getTool();
		for(ToolIdentity ident : identities) {
			if(t.isIdentityKnown(ident)) {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	private List<Element> mergeXmlesults(List<ToolOutput> results, Element element) {
		//holder for consolidated elements
		List<Element> consolidatedElements = new ArrayList<Element>();
		//Get the element from each ToolOutput result 
		List<Element> fitsElements = new ArrayList<Element>();
		for(ToolOutput result : results) {
			Document dom = result.getFitsXml();
			//if dom is null there is nothing to merge
			if(dom == null) {
				continue;
			}
			ToolInfo toolInfo = result.getTool().getToolInfo();
			try {
				XPath xpath = XPath.newInstance("//fits:"+element.getName());
				xpath.addNamespace("fits",Fits.XML_NAMESPACE);
				Element e = (Element)xpath.selectSingleNode(dom);
				if(e != null) {
					e.setAttribute("toolname",toolInfo.getName());
					e.setAttribute("toolversion",toolInfo.getVersion());
					fitsElements.add(e);
					e.getParent().removeContent(e);
				}
			}
			catch(JDOMException e) {
				e.printStackTrace();
			}
		}	
		
		//remove any unknown values
		fitsElements = removeUnknowns(fitsElements);
		//if there are no elements after removing unknowns just return null
		if(fitsElements.size() == 0) {
			return fitsElements;
		}
		int equalityResult = testEquality(fitsElements);
		if(equalityResult == ALL_AGREE) {
			//since all tools agreed, or all conflicts could be resolved 
			//return the element without the identifying tool name and version
			Element e = fitsElements.get(0);
			//Commented out so tool name and version are always displayed
			//e.removeAttribute("toolname");
			//e.removeAttribute("toolversion");
			consolidatedElements.add(e);
		}
		else if(equalityResult == SINGLE_RESULT) {
			Element e = fitsElements.get(0);
			e.setAttribute("status","SINGLE_RESULT");
			consolidatedElements.add(e);
		}
		//if configured to report conflicts return all values in fitsElements
		else if(equalityResult == CONFLICT && reportConflicts && !isRepeatableElement(fitsElements)) {
			for(Element e : fitsElements) {
				e.setAttribute("status","CONFLICT");
			}
			//to flatten any matches into single results
			consolidatedElements = consolidateFitsElements(fitsElements);
		}
		else {
			//pick first value using preferred tool list
			Element e = fitsElements.get(0);
			consolidatedElements.add(e);
		}			
		return consolidatedElements;	
	}
	
	private boolean isRepeatableElement(List<Element> fitsElements) {
		String name = fitsElements.get(0).getName();
		if(repeatableElements.contains(name)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private List<Element> consolidateFitsElements(List<Element> fitsElements) {
		List<Element> conElements = new ArrayList<Element>();
		for(Element e : fitsElements) {
			ListIterator<Element> iter = conElements.listIterator();
			boolean anyMatches = false;
			while ( iter.hasNext() ) {			
				//compare e with conElement
				Element conElement = iter.next();
				if(conElement.getText().equalsIgnoreCase(e.getText())) {
					//elements match so don't add it
					anyMatches = true;
					break;
				}
			}
			if(!anyMatches) {
				iter.add(e);
			}
		}
		return conElements;
	}
	
	private synchronized List<Element> removeUnknowns(List<Element> fitsElements) {
		ListIterator<Element> iter = fitsElements.listIterator();
		while ( iter.hasNext() ) {	
			Element e = iter.next();
			if(e == null || e.getValue() == null || e.getText().length() == 0) {
				//remove the element from the list
				iter.remove();
				//fitsElements.remove(e);
			}
		}
		return fitsElements;
	}
	
	private int testEquality(List<Element> fitsElements) {
		if(fitsElements.size() == 1) {
			return SINGLE_RESULT;
		}
		Element e = fitsElements.get(0);
		String eText = e.getText();
		for(int i=1;i<fitsElements.size();i++) {
			Element ee = fitsElements.get(i);
			//if both match as strings
			if(eText.equalsIgnoreCase(ee.getText())) {
				continue;
			}
			//else check as digits
			else if(eText.matches(REAL_NUMBER) && ee.getText().matches(REAL_NUMBER)) {
				Double e_d = Double.parseDouble(eText);
				Double ee_d =  Double.parseDouble(ee.getText());				
				if(e_d.compareTo(ee_d) == 0) {
					continue;
				}
				else {
					return CONFLICT;
				}
			}
			else {
				return CONFLICT;
			}
		}
		return ALL_AGREE;
	}
	
	private boolean parentContainsChild(Element parent, String childName) {
		List<Element> children = parent.getChildren();
		for(Element e : children) {
			if(e.getName().equalsIgnoreCase(childName)) {
				return true;
			}
		}
		return false;
	}
	
	private List<ToolIdentity> getAllIdentities(List<ToolOutput> results) {
		List<ToolIdentity> identities = new ArrayList<ToolIdentity>();
		for(ToolOutput result : results) {			
			if(result.getTool().canIdentify()) {
				List<ToolIdentity> identList = result.getFileIdentity();
				for(ToolIdentity ident : identList) {
					identities.add(ident);
				}
			}
		}
		return identities;
	}
	
	private boolean identitiesMatch(ToolIdentity a, FitsIdentity b) {
		//if format and mimetype match
		if(a.getFormat().equalsIgnoreCase(b.getFormat())
				&& a.getMime().equalsIgnoreCase(b.getMimetype())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * merge from a to b
	 * @return
	 */
	private void mergeExternalIdentifiers(ToolIdentity a, FitsIdentity b) {
		//add external identifiers to consolidated element from the new item, checking for duplicates
		for(ExternalIdentifier xIdent : a.getExternalIds()) {
			if(!b.hasExternalIdentifier(xIdent)) {
				b.addExternalID(xIdent);
			}	
		}
	}
	
	private void mergeFormatVersions(ToolIdentity a, FitsIdentity b) {
		FormatVersion v = a.getFormatVersion();
		if(v != null && v.getValue() != null && !b.hasFormatVersion(v)) {
			b.addFormatVersion(v);
		}
	}
	
	private List<FitsIdentity> consolidateIdentities(List<ToolIdentity> identities) {
		List<FitsIdentity> consolidatedIdentities = new ArrayList<FitsIdentity>();
		for(ToolIdentity ident : identities) {
			ListIterator<FitsIdentity> iter = consolidatedIdentities.listIterator();
			boolean anyMatches = false;
			boolean formatTreeMatch = false;
			while ( iter.hasNext() ) {
				//compare ident with conIdent
				FitsIdentity identitySection = (FitsIdentity)iter.next();
				if(identitiesMatch(ident,identitySection)) {
					//Add external identifiers from ident to the existing item
					mergeExternalIdentifiers(ident,identitySection);				
					//add format versions from ident to the existing item
					mergeFormatVersions(ident,identitySection);
					//add reporting tools from ident to the existing item
					identitySection.addReportingTool(ident.getToolInfo());
					//we found a match
					anyMatches = true;
					break;
				}
				//if identities don't match check format tree for specific/generic formats
				else {			
					int matchCondition = checkFormatTree(ident,identitySection);
					if(matchCondition == -1) {
						logger.debug(ident.getFormat() + " is more specific than " + identitySection.getFormat() + " tossing out " + identitySection.getToolName());
						//ident is more specific.  Most specific identity should always
						// be first element in list
						// overwrite the formate with more specific format
						identitySection.setFormat(ident.getFormat());
						// overwrite the formate with more specific mimetype
						identitySection.setMimetype((ident.getMime()));
						//Add external identifiers from ident to the existing item
						mergeExternalIdentifiers(ident,identitySection);				
						//add format versions from ident to the existing item
						mergeFormatVersions(ident,identitySection);
						//add reporting tools from ident to the existing item
						identitySection.addReportingTool(ident.getToolInfo());
						//FitsIdentity newSection = new FitsIdentity(ident);
						//add new section
						//backup on one position
						//iter.previous();
						//add the new item
						//iter.add(newSection);
						//skip head to next item
						//iter.next();
						//delete
						//iter.remove();
						//indicate we found a match
						anyMatches = true;
						break;
					}
					// existing format is more specific
					else if(matchCondition == 1) {
						logger.debug(identitySection.getFormat() + " is more specific than " + ident.getFormat() + " tossing out " + ident.getToolInfo().getName());
						formatTreeMatch = true;
						//do nothing, keep going and add to consolidated identities if no other matches
					}
					//formats are not specific or generic version of one another
					else {
						//leave anyMatches as false so ident is added to consolidated identities
					}
				}
			}
			//if there were no matches to existing consolidated identities add it
			if(!anyMatches && !formatTreeMatch) {
				iter.add(new FitsIdentity(ident));
			}
		}
		return consolidatedIdentities;
	}
	
	/**
	 * Returns 1 if a  is more specific than b.  Returns -1 if b is more specific than a.
	 * If neither is more specific then 0 is returned.
	 * @param a
	 * @param b
	 * @return
	 */
	private int checkFormatTree(ToolIdentity a, FitsIdentity b) {
		
		//if formats are equal then just return
		if(a.getFormat().equals(b.getFormat())) {
			return 0;
		}
		
		//check if a is more specific than b
		Attribute a_attr = new Attribute("format",a.getFormat());
		Attribute b_attr = new Attribute("format",b.getFormat());
		
		Element root = formatTree.getRootElement();
		Element a_element = XmlUtils.getChildWithAttribute(root,a_attr);
		Element b_element = XmlUtils.getChildWithAttribute(root,b_attr);
		
		if(a_element != null && b_element != null) {
			//if a contains b's attribute, so b is more specific
			if(XmlUtils.getChildWithAttribute(a_element,b_attr) != null) {
				return 1;
			}
			//if b contains a's attribute, so a is more specific
			else if(XmlUtils.getChildWithAttribute(b_element,a_attr) != null) {
				return -1;
			}
			else {
				return 0;
			}
		}
		return 0;
	}
	
	private void filterToolOutput(FitsIdentity section, List<ToolOutput> results) {
		ListIterator<ToolOutput> iter = results.listIterator();
		while ( iter.hasNext() ) {			
			ToolOutput result = iter.next();
			//if the identity section doesn't contain the results from the tool remove it
			if(!section.hasOutputFromTool(result.getTool().getToolInfo())) {
				iter.remove();
			}
		}
	}
	
		
	/* (non-Javadoc)
	 * @see edu.harvard.hul.ois.fits.DataConsolidator#processResults(java.util.List)
	 */
	public FitsOutput processResults(List<ToolOutput> results) {
		//Remove any null results, or results from tools that have the capability to identify files,
		// but couldn't identify the file.
		List<ToolOutput> culledResults = cullResults(results);
			
		//start building the FITS xml document
		Document mergedDoc = new Document();
		Element fits = new Element("fits",fitsNS);

		fits.addNamespaceDeclaration(xsiNS);
		fits.setAttribute(new Attribute("schemaLocation",Fits.XML_NAMESPACE+" "+Fits.externalOutputSchema, xsiNS));
		
		fits.setAttribute("version", Fits.VERSION);
		DateFormat dateFormat = new SimpleDateFormat(); 
		Date date = new Date();
		fits.setAttribute("timestamp",dateFormat.format(date));
		
		Element identificationsection = new Element("identification",fitsNS);
		fits.addContent(identificationsection);
		mergedDoc.addContent(fits);
		
		String curSec;
		String curSecName;
		
		//check identities
			//one "identity" per unique combination of format and mimetype
		List<ToolIdentity> identities = getAllIdentities(culledResults);
		List<FitsIdentity> identitySections = new ArrayList<FitsIdentity>();
		boolean unknownStatus = false;
		boolean partialStatus = false;
		//If there are no known identities in the culled results
		if(identities.size() == 0) {
			//try to find a partial identity match in the original results
			identitySections = getFirstPartialIdentity(results);
			if(identitySections.size() == 0)  {			
				//get the first default unknown identity from original results
				unknownStatus = true;
				identitySections = getFirstUnknownIdentity(results);
			}
			else {
				partialStatus = true;
			}
			
		}
		else {
			//else merge the known identities
			identitySections = consolidateIdentities(identities);
		}

		for(FitsIdentity identSection : identitySections) {
			Element identElement = new Element("identity",fitsNS);
			Attribute identFormatAttr = new Attribute("format",identSection.getFormat());
			Attribute identMimeAttr = new Attribute("mimetype",identSection.getMimetype());
			Attribute fitsToolName = new Attribute("toolname",identSection.getToolName());
			Attribute fitsToolVersion = new Attribute("toolversion",identSection.getToolVersion());
		
			//set format and mimetype attributes
			identElement.setAttribute(identFormatAttr);
			identElement.setAttribute(identMimeAttr);
			identElement.setAttribute(fitsToolName);
			identElement.setAttribute(fitsToolVersion);
			
			//add reporting tools
			for(ToolInfo info : identSection.getReportingTools()) {
				Element tool = new Element("tool",fitsNS);
				tool.setAttribute("toolname", info.getName());
				tool.setAttribute("toolversion", info.getVersion());
				identElement.addContent(tool);
			}
			 // tools agree
			 boolean toolsAgree = (identitySections.size() == 1 && identSection.getReportingTools().size() > 1);

			//add format version
			
			List<FormatVersion> formatVersions = identSection.getFormatVersions();
			String formatStatus = null;
			if(formatVersions.size() > 1) {
				formatStatus = "CONFLICT";
			}

			//only add if there is a version number to report
			if(formatVersions.size() > 0) {
				for(FormatVersion version : formatVersions) {
					Element identVersion = new Element("version",fitsNS);
					identVersion.setAttribute("toolname",version.getToolInfo().getName());
					identVersion.setAttribute("toolversion",version.getToolInfo().getVersion());
					if(formatStatus != null) {
						identVersion.setAttribute("status",formatStatus);
					}
					identVersion.setText(version.getValue());
					identElement.addContent(identVersion);
				}
			}
			
			for(ExternalIdentifier xId : identSection.getExternalIdentifiers()) {
				Element externalID = new Element("externalIdentifier",fitsNS);
				ToolInfo xIdInfo = xId.getToolInfo();
				externalID.setAttribute("toolname",xIdInfo.getName());
				externalID.setAttribute("toolversion",xIdInfo.getVersion());
				externalID.setAttribute("type",xId.getName());
				externalID.setText(xId.getValue());
				identElement.addContent(externalID);
			}
			//set the status of the section
			String status = "";
			if(identitySections.size() > 1 && reportConflicts) {
				status = "CONFLICT";
			}
			else if((identitySections.size() == 1 && (!unknownStatus && !partialStatus && !toolsAgree)) || !reportConflicts) {
				status="SINGLE_RESULT";
			}
			else if(identitySections.size() == 1 && partialStatus) {
				status = "PARTIAL";
			}
			else if (!toolsAgree) {
				status = "UNKNOWN";
			}
			 
			 if (status != "") 
				 {
						identificationsection.setAttribute("status",status);
				 }
			
			identificationsection.addContent(identElement);
			
			//if conflicts are being reported continue, else break after 1 iteration
			if(reportConflicts) {
				continue;
			}
			else {
				break;
			}
		}
					
		//check fileinfo, do normal xml comparison.  Use all non-culled tool output
		curSec = "/fits:fits/fits:fileinfo";
		//curSec = "/fits/fileinfo";
		curSecName = "fileinfo";
		Element s = new Element(curSecName,fitsNS);
		fits.addContent(s);
		Element e = null;
		while((e = findAnElement(culledResults,curSec,false)) != null) {
			List<Element> fitsElements = mergeXmlesults(culledResults, e);
			for(Element fitsElement : fitsElements) {
				s.addContent(fitsElement);
			}
		}
		
		//Only use the output from tools that were able to identify
		// the file and are in the first identity section
		if(identitySections.size() > 0) {
			filterToolOutput(identitySections.get(0),culledResults);
		}
				
		//check filestatus, do normal xml comparison
		curSec = "/fits:fits/fits:filestatus";
		//curSec = "/fits/filestatus";
		curSecName = "filestatus";
		s = new Element(curSecName,fitsNS);
		fits.addContent(s);
		e = null;
		while((e = findAnElement(culledResults,curSec,false)) != null) {
			List<Element> fitsElements = mergeXmlesults(culledResults, e);
			for(Element fitsElement : fitsElements) {
				s.addContent(fitsElement);
			}
		}
					
		//check metadata/child
			//if child.getParent() !exist in mergedDoc, then create and add these elements to it.
			//  else, add to existing section in mergedDoc
			//then do normal xml comparison
		curSec = "/fits:fits/fits:metadata";
		//curSec = "/fits/metadata";
		curSecName = "metadata";
		s = new Element(curSecName,fitsNS);
		fits.addContent(s);
		e = null;
		while((e = findAnElement(culledResults,curSec,true)) != null) {
			Element eParent = e.getParentElement();
			Element metadataType = null;
			if(!parentContainsChild(s,eParent.getName())) {
				metadataType = new Element(eParent.getName(),fitsNS);
				s.addContent(metadataType);
			}
			else {
				metadataType = s.getChild(eParent.getName(),fitsNS);
			}
			List<Element> fitsElements = mergeXmlesults(culledResults, e);
			for(Element fitsElement : fitsElements) {
				metadataType.addContent(fitsElement);
			}
		}

		
		//Consolidate results from each tool
		// Check for identical and unknown values for each field
		// Check format tree for specific/generic formats
		// if conflicts exist && configured to report multiple values in conflict
			//report both values
		// else
			// pick value using preferred tool list ordering

		
		//while non empty results exist - check for elements in //identity, //info and //metadata (method for this)
		//Start with first result that is not empty (method for this to grab any element from these sections)
			//pull out an element
				//  /fits/summary/identity
				//  /fits/summary/info
				//  /fits/summary/metadata
			//look up and pull that element out of other results
			//do comparison
				//Identical?
				//unknown values?
				//consult tree?
				//configured to report multiple conflicting values?
					//use preferred ordering of tools
			//add result or results if configured to report multiples when conflict exists
			//to mergedDoc
		
			//continue with next element until result is empty
			//continue with all results until all are empty.
		
		if(displayToolOutput) {
			Element toolOutput = new Element("toolOutput",fitsNS);
			for(ToolOutput output : results) {
				if(output != null) {
					Element tool = new Element("tool",fitsNS);
					tool.setAttribute("name",output.getTool().getToolInfo().getName());
					tool.setAttribute("version",output.getTool().getToolInfo().getVersion());
					Document doc = output.getToolOutput();
					if(doc != null) {
						Element root = (Element)doc.getRootElement().detach();
						tool.addContent(root);
						toolOutput.addContent(tool);
						}
				}
			}
			fits.addContent(toolOutput);
		}
		
		FitsOutput result = new FitsOutput(mergedDoc);
		return result;
	}
	
	private boolean isPartialIdentity(List<ToolIdentity> identities) {
		ToolIdentity identity = identities.get(0);
		if(identity != null) {
			String mime = identity.getMime();
			String format = identity.getFormat();
			
			boolean validMime = (mime != null && mime.length()>0);
			boolean validFormat = (format != null && format.length()>0);
			
			if((validMime && validFormat) || 
					(validMime && !validFormat) || 
					(validFormat && !validMime)) {
				if((mime.equals("application/octet-stream") 
						&& !format.equals("Unknown Binary"))
						|| 
						(format.equals("Unknown Binary") 
						&& !mime.equals("application/octet-stream"))) {
					return true;
				}
			}
		}
		return false;
	}

}

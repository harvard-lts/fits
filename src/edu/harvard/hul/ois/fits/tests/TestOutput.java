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
package edu.harvard.hul.ois.fits.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;



public class TestOutput {
	
	private static final String USAGE = "generatetestoutput [create | test] input_dir output_dir";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length != 3) {
			System.out.println("Invalid Syntax");
			System.out.println("Usage:\n"+ USAGE);
			System.exit(0);
		}
        
        if(args[0].equals("create")) {
        	createTestOutput(args[1],args[2]);
        }
        else if(args[0].equals("test")){
        	test(args[1],args[2]);
        }
        else {
	    	System.out.println("Usage:\n"+ USAGE);
	    	System.exit(0);       	
        }
 	}
	
	private static void createTestOutput(String input, String output) throws Exception {
		//prompt for confirmation
		System.out.print("This will erase all existing test output, continue? [y/n]: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String userConfirm = null;
		try {
			userConfirm = br.readLine();
		} catch (IOException ioe) {
		   System.out.println("IO error reading input");
		   System.exit(1);
		}

		if(!userConfirm.equalsIgnoreCase("y")) {
			System.out.println("exiting");
			System.exit(0);
		}
		
		File inputDir = new File(input);
		File outputDir = new File(output);
		
		//delete all existing output
		for(File outputFile : outputDir.listFiles()) {
			//only delete the .xml files
			if(!outputFile.getPath().endsWith(".xml")) {
				continue;
			}
			System.out.println("deleting " + outputFile.getPath());
			outputFile.delete();
		}
		
		Fits fits = new Fits("");
		
		//create new output for files in input dir
		for(File inputFile : inputDir.listFiles()) {
			//skip directories
			if(inputFile.isDirectory()) {
				continue;
			}
			System.out.println("processing " + inputFile.getPath());
			FitsOutput fitsOutput = fits.examine(inputFile);
			fitsOutput.saveToDisk(outputDir.getPath()+File.separator+inputFile.getName()+".xml");
		}
		
		System.out.println("All Done");

	}
	
	private static void test(String input, String output) throws Exception {
	   	Fits fits = new Fits("");
    	SAXBuilder builder = new SAXBuilder();  	
    	XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());  	
    	
		File inputDir =new File(input);
		File outputDir =new File(output);
		
		int passed = 0;
		int failed = 0;
				
		for(File inputFile : inputDir.listFiles()) {
			//skip directories
			if(inputFile.isDirectory()) {
				continue;
			}
			StringWriter sw = new StringWriter();
			
			FitsOutput fitsOut = fits.examine(inputFile);
			serializer.output(fitsOut.getFitsXml(), sw);
			String actualStr = sw.toString(); 
			
			File outputFile = new File(outputDir + File.separator + inputFile.getName()+".xml");
			if(!outputFile.exists()) {
				System.err.println("Not Found: "+outputFile.getPath());
				continue;
			}
			Document expectedXml = builder.build(new FileInputStream(outputFile));
			sw = new StringWriter();
			serializer.output(expectedXml, sw);
			String expectedStr = sw.toString();
			
			//get the file name in case of a failure
			FitsMetadataElement item = fitsOut.getMetadataElement("filename");
		    DifferenceListener myDifferenceListener = new IgnoreAttributeValuesDifferenceListener();
		    Diff diff = new Diff(expectedStr,actualStr);
		    diff.overrideDifferenceListener(myDifferenceListener);

		    if(diff.similar()) {
		    	System.out.println("PASS: "+item.getValue());
		    	passed++;
		    }
		    else {
		    	System.err.println("FAIL: "+item.getValue());
		    	failed++;
		    }
		}
		System.out.println("All Done");
		System.out.println(passed + " tests passed");
		if(failed != 0) {
			System.err.println(failed + " tests failed");
		}
		else {
			System.out.println(failed + " tests failed");
		}

	}

}

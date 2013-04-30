package edu.harvard.hul.ois.fits;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.hul.ois.fits.exceptions.FitsException;


public class FitsJarMain {

	
	 public static void main(String[] args) throws IOException {
	     if (args.length != 2) {
	         throw new RuntimeException("Usage: FitsJarMain <fits-config-dir> <input-file>");
	     }

	     File file = new File(args[1]);
	     String filePath = file.getAbsolutePath();
	     File configDir = new File(args[0]);
	     String configDirPath = configDir.getAbsolutePath();
	     if (!file.exists()) {
	         throw new IllegalArgumentException("Arg file !exist: " + filePath);
	     }
	     if(!configDir.exists()) {
	    	 throw new IllegalArgumentException("FITS configuration !exist: " + configDirPath);
	     }
	     
	     try {
			addPath(configDir.getPath()+"/xml/nlnz");	
		}
	    catch (Exception e1) {
			e1.printStackTrace();
		}

	     OutputStream out = new FileOutputStream(filePath + ".xml");
	     try {
		     Fits fits = new Fits(configDirPath); 
		     FitsOutput result = fits.examine(file);
		     Document doc = result.getFitsXml();
		     XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
		     serializer.output(doc, out);
	     }
	     catch(FitsException e) {
	    	 e.printStackTrace();
	     }
	     finally {
	    	 out.close(); 
	     }
	     
	 }
	 
	public static void addPath(String s) throws Exception {
		File f = new File(s);
		URL u = f.toURI().toURL();
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL",new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[] { u });

	}

}

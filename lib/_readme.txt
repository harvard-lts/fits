All sub-directories of the "lib" directory are for tool-specific JAR files for the specific tool as listed in fits.xml within each <tool> element in the "classpath-dirs" attribute.
All resources loaded from each sub-directory will be loaded in a child-first ClassLoader so as to isolate these JAR classes and resources from other tools.
"classpath-dirs" attribute values in fits.xml may also point to other directories containing other types of required resources.
See edu.harvard.hul.ois.fits.tools.utils.ParentLastClassLoader.java for technical implementation.

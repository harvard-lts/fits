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
package edu.harvard.hul.ois.fits.tools.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataValues;
import edu.harvard.hul.ois.fits.exceptions.FitsToolCLIException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import edu.harvard.hul.ois.fits.tools.utils.CommandLine;

public class FFMpeg extends ToolBase {

    private static final Logger logger = Logger.getLogger(FFMpeg.class);
    
    private static final String SUPPORTED_VERSION = "2.6.2";
    
    private List<String> toolCommand = new ArrayList<String>();
    private final static String xslt = Fits.FITS_XML + "/ffmpeg/ffmpeg_to_fits.xslt";
    private boolean enabled = true;
    
	public FFMpeg() throws FitsToolException {
        logger.debug("Initializing FFMpeg");
		String osName = System.getProperty("os.name");
		List<String> infoCommand = new ArrayList<String>();
		if (osName.startsWith("Windows")) {
            infoCommand.add("ffprobe.exe");
            toolCommand.add("ffprobe.exe");
        }
        else {
            infoCommand.add("ffprobe");
            toolCommand.add("ffprobe");
        }
		infoCommand.add("-version");
		toolCommand.add("-show_format");
        toolCommand.add("-show_streams");
        toolCommand.add("-print_format");
        toolCommand.add("compact");
		
        try {
        	String versionOutput = CommandLine.exec(infoCommand, null);
        	String version = "?";
            if (versionOutput.startsWith("ffprobe")) {
                version = versionOutput.substring(8);
                if (version.startsWith("version")) {
                    version = version.substring(8);
                    int idx = version.indexOf(' ');
                    if (idx != -1) {
                        version = version.substring(0, idx);
                    }
                }
            }
            if (!SUPPORTED_VERSION.equals(version)) {
            	logger.debug("FFMpeg can be installed in not supported version. The supported version is " + SUPPORTED_VERSION);
            }
        	info = new ToolInfo("FFmpeg", version, null);
        } catch (FitsToolCLIException e) {
        	throw new FitsToolException("FFMpeg is probably not installed or it's not added to PATH", e);
		}
	}

	@Override
	public ToolOutput extractInfo(File file) throws FitsToolException {
	    logger.debug ("FFMpeg.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
        List<String> execCommand = new ArrayList<String>();
        execCommand.addAll(toolCommand);
        execCommand.add(file.getPath());
        
		logger.debug("Launching FFmpeg, command = " + execCommand);
		String execOut = CommandLine.exec(execCommand, null);
		logger.debug("Finished running FFmpeg");
      
        String formatAbbr = null;
        String formatName = null;
        String ext = null;
        String type = null;		// audio/video
        String avDuration = null;	
        String[] lines = execOut.split(System.getProperty("line.separator"));
        
        for (String line : lines) {
        	if (line.startsWith("  Duration:")) {
        		avDuration = retrieveDuration(line);
        	}
            if (line.startsWith("format|")) {
                String[] parts = line.split("\\|"); 
                for (String part : parts) {
                	if (part.startsWith("format_name")) {
                		formatAbbr = retrieveValue(part);
                		continue;
                	}
                	if (part.startsWith("filename")) {	// it's filepath in fact
                    	ext = FilenameUtils.getExtension(retrieveValue(part));
                        continue;
                    }
                	if (part.startsWith("format_long_name")) {
                		formatName = retrieveValue(part);
                        continue;
                    }
                }
            }
            if (line.startsWith("stream|")) {
                String[] parts = line.split("\\|"); 
                for (String part : parts) {
                	if (part.startsWith("codec_type")) {
                		String codecType = retrieveValue(part);
                		if (codecType != null) {
                			if (type == null || !type.equals("video")) {
                				type = codecType;
                			}
                			break;
                		}
                	}
                }
            }
        }
        formatAbbr = getSingleFormatAbbr(formatAbbr, ext);
        String mimetype = mapMimetype(type, formatAbbr);
        formatName = mapFormatName(formatName, formatAbbr);
        
        // don't recognize files with no duration (text, documents, images, ...)
        if (avDuration == null) {
            execOut = "";
            formatAbbr = "";
        }
            
        Document rawOut = createXml(execOut, mimetype, formatName);       
        Document fitsXml = transform(xslt, rawOut);
        
        output = new ToolOutput(this, fitsXml, rawOut);
        
        duration = System.currentTimeMillis() - startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug ("FFMpeg.extractInfo finished on " + file.getName());
        
        //------------------------
        XMLOutputter serializer = new XMLOutputter( Format.getPrettyFormat() );
        try {
			serializer.output( output.getFitsXml(), System.out );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //-------------------------
		
        return output;
	}

    private String retrieveValue(String part) {
        int idx = part.indexOf('=');
        if ((idx != -1) && (part.length() > idx + 1)) {
            return part.substring(idx + 1).trim();
        }
        return null;
    }
    
    private String retrieveDuration(String line) {
    	String duration = line.substring(11);
		int idx = duration.indexOf(',');
		if (idx != -1) {
			duration = duration.substring(0, idx).trim();
			if (!duration.equals("N/A")) {
				return duration;
			}
		}
		return null;
    }
    
    private String getSingleFormatAbbr(String formatAbbr, String ext) {
		if (formatAbbr != null) {
			String[] formats = formatAbbr.split(",");
			if (formats.length > 1) {
				if (ext != null && ext.length() > 0) {
					for (String format : formats) {
						if (format.equals(ext)) {
							formatAbbr = format;
							break;
						}
					}
				} else {
					formatAbbr = formats[0];
				}
			}
		}
		return formatAbbr;
    }
    
    private String mapMimetype(String type, String formatAbbr) {
   	 	String mimetype = "application/octet-stream";
        if (type != null && formatAbbr != null) {
            mimetype = type + "/" + formatAbbr;
        }
        return FitsMetadataValues.getInstance().normalizeMimeType(mimetype);
    }
    
    private String mapFormatName(String formatName, String formatAbbr) {
    	if (formatName == null) {
        	return "Unknown Binary";
        } else {
            // there is an ambiguity in the case of MPEG-4
            if (formatName.equals("QuickTime / MOV")) {
                if (formatAbbr != null) {
                    if (formatAbbr.equals("mov")) {
                    	formatName = "Quicktime";
                    } else if (formatAbbr.equals("mp4") || formatAbbr.equals("m4a") || formatAbbr.equals("3gp") || formatAbbr.equals("3g2")) {
                    	formatName = "MPEG-4";
                    } else if (formatAbbr.equals("mj2")) {
                    	formatName = "MJ2 (Motion JPEG 2000)";
                    }
                }
            // also in case of ASF
            } else if (formatName.equals("ASF (Advanced / Active Streaming Format)")) {
            	 if (formatAbbr != null) {
            		 if (formatAbbr.equals("wmv") || formatAbbr.equals("asf")) {
                     	formatName = "WMV";
                     } else if (formatAbbr.equals("wma")) {
                     	formatName = "WMA";
                     }
            	 }
            }
            return FitsMetadataValues.getInstance().normalizeFormat(formatName);
        }
    }
    
    private Document createXml(String execOut, String mimetype, String formatName) throws FitsToolException {       
        StringWriter out = new StringWriter();
        
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("\n");
        out.write("<ffmpeg>");
        out.write("\n");
        
        String[] lines = execOut.split(System.getProperty("line.separator"));
        for(String line : lines) {
            if (line.startsWith("format|") || line.startsWith("stream|")) {
                out.write("<" + line.substring(0, 6) + ">");
                out.write("\n");
                if (line.startsWith("format|")) {
                    out.write("<fits_mimetype>");
                    out.write(StringEscapeUtils.escapeXml(mimetype));
                    out.write("</fits_mimetype>");
                    out.write("\n");
                    out.write("<fits_format>");
                    out.write(StringEscapeUtils.escapeXml(formatName));
                    out.write("</fits_format>");
                    out.write("\n");
                }
                String[] parts = line.split("\\|");
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length ; i++) {
                        int idx = parts[i].indexOf('=');
                        if ((idx != -1) && (parts[i].length() > idx + 1)) {
                            String field = parts[i].substring(0, idx).trim();
                            field = field.replace(':', '.');
                            String value = parts[i].substring(idx + 1).trim();
                            out.write("<" + field + ">");
                            if (field.equals("filename")) {
                            	value = FilenameUtils.getName(value);
                            }
                            out.write(StringEscapeUtils.escapeXml(value));
                            out.write("</" + field + ">");
                            out.write("\n");
                        }
                    }
                }
                out.write("</" + line.substring(0, 6) + ">");
                out.write("\n");
            }
        }
        out.write("</ffmpeg>");
        out.write("\n");
        
        out.flush();
        try {
            out.close();
        } catch (IOException e) {
            throw new FitsToolException("Error closing ffmpeg XML output stream", e);
        }
        Document doc = null;
        try {
            doc = saxBuilder.build(new StringReader(out.toString()));
        } catch (Exception e) {
            throw new FitsToolException("Error parsing ffmpeg XML Output", e);
        } 
        return doc;
    }
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean value) {
		enabled = value;	
	}

}
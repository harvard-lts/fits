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
package edu.harvard.hul.ois.fits.tools.oisfileinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.apache.log4j.Logger;

import edu.harvard.hcl.hclaps.bwav.WAVEFile;
import edu.harvard.hcl.hclaps.bwav.chunks.FormatChunk;
import edu.harvard.hcl.hclaps.util.ByteConvertor;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

/**
 * This uses the audio file parsing library hclaps.jar provided by Dave Ackerman  
 * @author spencer
 *
 */
public class AudioInfo extends ToolBase {
	
    private final static String TOOL_NAME = "OIS Audio Information";
    private final static String TOOL_VERSION = "0.1";
    private final static String TOOL_DATE = "2/17/11";
    
	private boolean enabled = true;
	private static Namespace fitsNS = Namespace.getNamespace(Fits.XML_NAMESPACE);
	private static Namespace xsiNS = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	
    private static Logger logger = Logger.getLogger(AudioInfo.class);
    
	public AudioInfo() throws FitsToolException {
		info.setName(TOOL_NAME);
		info.setVersion(TOOL_VERSION);
		info.setDate(TOOL_DATE);
	}

	public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug ("AudioInfo.extractInfo starting on " + file.getName());
		long startTime = System.currentTimeMillis();
		Document doc = createXml(file);
		output = new ToolOutput(this,(Document)doc.clone(),doc);
		duration = System.currentTimeMillis()-startTime;
		runStatus = RunStatus.SUCCESSFUL;
        logger.debug ("AudioInfo.extractInfo finished on " + file.getName());
		return output;
	}
	
	private Document createXml(File file) throws FitsToolException {

		Element root = new Element("fits",fitsNS);
		root.setAttribute(new Attribute("schemaLocation","http://hul.harvard.edu/ois/xml/ns/fits/fits_output "+Fits.externalOutputSchema,xsiNS));
		
		//If the file is a wav then parse and set audio metadata
		int magicNum = -1;
		try {
			magicNum = tasteMagicNumber(file);
		}
		catch(IOException e) {
			throw new FitsToolException("Error getting magic number for file", e);
		}
		if(magicNum == 0x57415645) {
			doWav(root,file);
		}
		else {
			//magicNum = 0x2E524D46 do RA?
		}
		
		return new Document(root);
    }
	
	private Element doWav(Element root, File file) throws FitsToolException {
		WAVEFile wav = null;
		try {
			wav = new WAVEFile(file.getPath());
		}
		catch (FileNotFoundException e) {
			throw new FitsToolException("File "+file.getName() + " not found",e);
		}
		
		Element metadata = new Element("metadata",fitsNS);
		Element audioMetadata = new Element("audio",fitsNS);
		
		Element identification = new Element("identification",fitsNS);
		Element identity = new Element("identity",fitsNS);
		identity.setAttribute("format","Waveform Audio");
		identity.setAttribute("mimetype","audio/x-wave");
		//add identity to identification section
		identification.addContent(identity);
		//add identification section to root
		root.addContent(identification);
		
		Element numSamples = new Element("numSamples",fitsNS);
		numSamples.setText(String.valueOf(wav.dataChunk().getNumberOfSampleFrames()));
		audioMetadata.addContent(numSamples);
						
		Element samplesPerSecond = new Element("sampleRate",fitsNS);
		samplesPerSecond.setText(String.valueOf(wav.formatChunk().getSamplesPerSecond()));
		audioMetadata.addContent(samplesPerSecond);	
		
		Element audioDataEncoding = new Element("audioDataEncoding",fitsNS);
		audioDataEncoding.setText(String.valueOf(FormatChunk.FormatTagDescription.getFormatTagDescription(wav.formatChunk().getFormatTag())));
		audioMetadata.addContent(audioDataEncoding);	
		
		Element blockAlign = new Element("blockAlign",fitsNS);
		blockAlign.setText(String.valueOf(wav.formatChunk().getBlockAlign()));
		audioMetadata.addContent(blockAlign);	
		
		Element time = new Element("time",fitsNS);
		if(wav.bextChunk() != null) {
			time.setText(String.valueOf(wav.bextChunk().getTimeReference()));
		}
		audioMetadata.addContent(time);	
		
		Element numChannels = new Element("channels",fitsNS);
		numChannels.setText(String.valueOf(wav.formatChunk().getNumberOfChannels()));
		audioMetadata.addContent(numChannels);	
				
		Element bitDepth = new Element("bitDepth",fitsNS);
		bitDepth.setText(String.valueOf(wav.formatChunk().getBitsPerSample()));
		audioMetadata.addContent(bitDepth);	
		
		Element wordSize = new Element("wordSize",fitsNS);
		wordSize.setText(String.valueOf(wav.formatChunk().getBlockAlign() / wav.formatChunk().getNumberOfChannels()));
		audioMetadata.addContent(wordSize);	
		
		Element offset = new Element("offset",fitsNS);
		offset.setText(Long.toString(wav.dataChunk().getPositionInFile() + 8));
		audioMetadata.addContent(offset);	
		
		Element soundField = new Element("soundField",fitsNS);
		if (wav.formatChunk().getNumberOfChannels() == 1) {
			soundField.setText("MONO");
		}
		else if (wav.formatChunk().getNumberOfChannels() == 2) {
			soundField.setText("STEREO");
		}
		else {
			soundField.setText("SURROUND");
		}
		 
		//add to metadata section
		metadata.addContent(audioMetadata);
		//add metadata section to root
		root.addContent(metadata);
		
		return root;
	}

	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;		
	}
	
	protected int tasteMagicNumber(File file) throws FileNotFoundException, IOException {
		byte[] mn = new byte[12];
		int rval = -1;
		
		FileInputStream fis = new FileInputStream(file);
		fis.read(mn);
		fis.close();
		
		byte[] buffer = new byte[4];
		for (int i = 0; i < 4; i++)
		{
			buffer[i] = mn[i];
		}
		
		rval = (int)ByteConvertor.uIntForBytes(buffer, ByteConvertor.BIG);
		
		if (rval == 0x52494646) //RIFF
		{
			buffer = new byte[4];
			for (int i = 8; i < 12; i++)
			{
				buffer[i - 8] = mn[i];
			}
			rval = (int)ByteConvertor.uIntForBytes(buffer, ByteConvertor.BIG);
		}
		
		return rval;
	}
}

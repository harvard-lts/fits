/**********************************************************************
 * Copyright (c) 2009 by the President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Contact information
 *
 * Office for Information Systems
 * Harvard University Library
 * Harvard University
 * Cambridge, MA  02138
 * (617)495-3724
 * hulois@hulmail.harvard.edu
 **********************************************************************/

package edu.harvard.hul.ois.fits;

import edu.harvard.hul.ois.ots.schemas.VideoMD.CalibrationInfo;
import edu.harvard.hul.ois.ots.schemas.VideoMD.Compression;
import edu.harvard.hul.ois.ots.schemas.VideoMD.FileData;
import edu.harvard.hul.ois.ots.schemas.VideoMD.Frame;
import edu.harvard.hul.ois.ots.schemas.VideoMD.Sound;
import edu.harvard.hul.ois.ots.schemas.VideoMD.Use;
import edu.harvard.hul.ois.ots.schemas.VideoMD.VariableRate;
import edu.harvard.hul.ois.ots.schemas.VideoMD.VideoInfo;
import edu.harvard.hul.ois.ots.schemas.VideoMD.VideoMD;
import edu.harvard.hul.ois.ots.schemas.XmlContent.DecimalElement;
import edu.harvard.hul.ois.ots.schemas.XmlContent.IntegerElement;
import edu.harvard.hul.ois.ots.schemas.XmlContent.LongElement;
import edu.harvard.hul.ois.ots.schemas.XmlContent.StringElement;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;

public class VideoMDModel {

    protected VideoMD videoMD;
    
    private FileData fileData;
    private Frame frame;
    private Compression compression;
    private VideoInfo videoInfo;
    private CalibrationInfo calibrationInfo;
    
    public VideoMDModel() throws XmlContentException {
        videoMD = new VideoMD();
        Use use = new Use("Other", FileData.ELEMENT_USE_NAME);
        getFileData().addUse(use);
        StringElement otherUse = new StringElement("unknown", FileData.ELEMENT_OTHERUSE_NAME);
        getFileData().addOtherUse(otherUse);
    }
        
    private FileData getFileData() throws XmlContentException {
        if (fileData == null) {
            fileData = new FileData(VideoMD.ELEMENT_FILEDATA_NAME);
            videoMD.setFileData(fileData);
        }
        return fileData;
    }
    
    private Frame getFrame() throws XmlContentException {
        if (frame == null) {
            frame = new Frame(FileData.ELEMENT_FRAME_NAME);
            getFileData().setFrame(frame);
        }
        return frame;
    }
    
    private Compression getCompression() throws XmlContentException {
        if (compression == null) {
            compression = new Compression(FileData.ELEMENT_COMPRESSION_NAME);
            getFileData().addCompression(compression);
        }
        return compression;
    }
    
    private VideoInfo getVideoInfo() throws XmlContentException {
        if (videoInfo == null) {
            videoInfo = new VideoInfo(VideoMD.ELEMENT_VIDEOINFO_NAME);
            videoMD.setVideoInfo(videoInfo);
        }
        return videoInfo;
    }
    
    private CalibrationInfo getCalibrationInfo() throws XmlContentException {
        if (calibrationInfo == null) {
            calibrationInfo = new CalibrationInfo(VideoMD.ELEMENT_CALIBRATIONINFO_NAME);
            videoMD.setCalibrationInfo(calibrationInfo);
        }
        return calibrationInfo;
    }
    
    
    protected void setDuration(String duration) throws XmlContentException {
        StringElement durationElement = new StringElement(duration, VideoInfo.ELEMENT_DURATION_NAME);
        getVideoInfo().addDuration(durationElement);
    }
    
    protected void setBitDepth(Integer bitDepth) throws XmlContentException {
        IntegerElement bitDepthElement = new IntegerElement(bitDepth, FileData.ELEMENT_BITSPERSAMPLE_NAME);
        getFileData().addBitsPerSample(bitDepthElement);
    }
    
    protected void setBitRate(Long bitRate) throws XmlContentException {
        VariableRate dataRateElement = new VariableRate(bitRate, FileData.ELEMENT_DATARATE_NAME);
        dataRateElement.setUnit("bps");
        getFileData().setDataRate(dataRateElement);
    }

    protected void setFrameRate(String frameRate) throws XmlContentException {
        double rate = 0;
        String[] fraction = frameRate.split("/");
        if (fraction.length == 2) {
            double nominator, denominator;
            try {
                nominator = Double.parseDouble(fraction[0]);
                denominator = Double.parseDouble(fraction[1]);
            } catch (NumberFormatException e) {
                return;
            }
            if (denominator != 0) {
                rate = nominator / denominator;
            } else {
                rate = nominator;
            }
        } else {
            try {
                rate = Double.parseDouble(fraction[0]);
            } catch (NumberFormatException e) {
                return;
            }
        }
        rate = Math.round(rate * 1000) / 1000.0;
        DecimalElement frameRateElement = new DecimalElement(rate, Frame.ELEMENT_FRAMERATE_NAME);
        getFrame().setFrameRate(frameRateElement);
    }
    
    protected void setSize(Long size) throws XmlContentException {
        LongElement sizeElement = new LongElement(size, FileData.ELEMENT_SIZE_NAME);
        getFileData().setSize(sizeElement);
    }

    protected void setFrameWidth(int width) throws XmlContentException {
        IntegerElement widthElement = new IntegerElement(width, Frame.ELEMENT_PIXELSHORIZONTAL_NAME);
        getFrame().setPixelsHorizontal(widthElement);
    }
    
    protected void setFrameHeight(int height) throws XmlContentException { 
        IntegerElement heightElement = new IntegerElement(height, Frame.ELEMENT_PIXELSVERTICAL_NAME);
        getFrame().setPixelsVertical(heightElement);
    }
    
    protected void setNumberOfFrames(String numFrames) throws XmlContentException {
        StringElement durationElement = new StringElement(numFrames, FileData.ELEMENT_DURATION_NAME);
        getFileData().addDuration(durationElement);
    }
    
    protected void setCodecName(String codecName) throws XmlContentException {
        StringElement codecNameElement = new StringElement(codecName, Compression.ELEMENT_CODECNAME_NAME);
        getCompression().setCodecName(codecNameElement);
    }

    protected void setCodecCreatorApplication(String codecCreatorApp) throws XmlContentException {
        StringElement codecCreatorAppElement = new StringElement(codecCreatorApp, Compression.ELEMENT_CODECCREATORAPP_NAME);
        getCompression().setCodecCreatorApp(codecCreatorAppElement);
    }

    protected void setSampleAspectRatio(String sar) throws XmlContentException {
        StringElement sarElement = new StringElement(sar, Frame.ELEMENT_PAR_NAME);
        getFrame().setPAR(sarElement);
    }

    protected void setDisplayAspectRatio(String dar) throws XmlContentException {
        StringElement darElement = new StringElement(dar, Frame.ELEMENT_DAR_NAME);
        getFrame().setDAR(darElement);
    }

    protected void setSignalFormat(String signalFormat) throws XmlContentException {
        StringElement signalFormatElement = new StringElement(signalFormat, FileData.ELEMENT_SAMPLING_NAME);
        getFileData().addSampling(signalFormatElement);
    }

    protected void setSound(String sound) throws XmlContentException {
        Sound soundElement = new Sound(sound, FileData.ELEMENT_SOUND_NAME);
        getFileData().addSound(soundElement);
    }

}
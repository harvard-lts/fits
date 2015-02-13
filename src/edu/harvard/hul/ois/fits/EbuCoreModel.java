/* 
 * Copyright 2015 Harvard University Library
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
package edu.harvard.hul.ois.fits;

//import java.util.UUID;

import edu.harvard.hul.ois.ots.schemas.Ebucore.AudioFormatExtended;
import edu.harvard.hul.ois.ots.schemas.Ebucore.ContainerFormat;
import edu.harvard.hul.ois.ots.schemas.Ebucore.CoreMetadata;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Duration;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Format;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Start;
import edu.harvard.hul.ois.ots.schemas.Ebucore.EbuCoreMain;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;

public class EbuCoreModel {
	
    protected EbuCoreMain ebucoreMain;
    protected Format format;
    protected AudioFormatExtended audioFmtExt;
    protected ContainerFormat containerFormat;
    protected Duration duration;
    protected Start start;
    
    //protected final String ebucoreID = "EBUCORE_"+UUID.randomUUID().toString();
    //protected final String faceID = "FACE_"+UUID.randomUUID().toString();
    //protected final String regionID = "REGION_"+UUID.randomUUID().toString();
    //protected final String formatRegionID = "FORMAT_REGION_"+UUID.randomUUID().toString();    
    
    protected EbuCoreModel () throws XmlContentException {
    	
    	//set up base ebucore main object structure    	
    	ebucoreMain = new EbuCoreMain ();
    	
    	//ebucoreMain.setSchemaVersion("1.0.0");
    	//ebucoreMain.setID(ebucoreID);
        //ebucoreMain.setDisposition("");
        //Identifier ident = new Identifier("","primaryIdentifier");
        //ident.setIdentifierType("FILE_NAME");
        //video.setPrimaryIdentifier(ident);

        containerFormat = new ContainerFormat("containerFormat");
        start = new Start("start");
        
        duration = new Duration("duration");	
		
		format = new Format("format");
		format.setContainerFormat(containerFormat);
		format.setStart(start);
		format.setDuration(duration);
		
		// TODO: Do we need this
		// format.setAudioFormatExtended(audioFmtExt);
		
        CoreMetadata cm = new CoreMetadata("coreMetadata");
        cm.setFormat(format);

        ebucoreMain.setFormat(cm);
    }
	
	
//    protected void setFormat(String format, String version) throws XmlContentException {
//    	Format formatElem = new Format(format);
//    	if(version != null && version.length() > 0) {
//    		formatElem.setAttribute("specificationVersion", version);
//    	}
//    	ebucoreMain.setFormat(formatElem);
//    }
//    
//    protected void setBitRate(String bitrate) throws XmlContentException {
//    	ebucoreMain.setBitrate(bitrate);
//    }  
    
    
//    protected void setVideoFormat(Element videoTrack) {
//    	
//    }

}

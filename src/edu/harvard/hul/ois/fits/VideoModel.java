package edu.harvard.hul.ois.fits;

import java.util.UUID;

import edu.harvard.hul.ois.ots.schemas.AES.Identifier;
import edu.harvard.hul.ois.ots.schemas.Ebucore.VideoObject;
import edu.harvard.hul.ois.ots.schemas.Ebucore.Format;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;

public class VideoModel {
	
    protected VideoObject video;
    
    protected final String videoObjectID = "VIDEO_OBJECT_"+UUID.randomUUID().toString();
    //protected final String faceID = "FACE_"+UUID.randomUUID().toString();
    //protected final String regionID = "REGION_"+UUID.randomUUID().toString();
    //protected final String formatRegionID = "FORMAT_REGION_"+UUID.randomUUID().toString();    
    
    protected VideoModel () throws XmlContentException {
    	
    	//set up base video object structure    	
        video = new VideoObject ();
        video.setSchemaVersion("1.0.0");
        video.setID(videoObjectID);
        //video.setDisposition("");
        Identifier ident = new Identifier("","primaryIdentifier");
        ident.setIdentifierType("FILE_NAME");
        //video.setPrimaryIdentifier(ident);
    }
	
	
    protected void setFormat(String format, String version) throws XmlContentException {
    	Format formatElem = new Format(format);
    	if(version != null && version.length() > 0) {
    		formatElem.setAttribute("specificationVersion", version);
    	}
    	video.setFormat(formatElem);
    }	

}

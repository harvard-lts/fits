package edu.harvard.hul.ois.fits;

public enum FormatElements {
	size ("fileSize"),
	filename ("fileName"),
	mimeType ("mimeType"),
	location ("location"),
	bitRate ("bitRate"),
	dateCreated ("dateCreated"),
	dateModified ("dateModified"),
	formatProfile ("formatProfile"),
	format ("format"),
	duration ("duration");
   	
	private String ebucoreName;
    
	FormatElements(String ebucoreName) {
        this.ebucoreName = ebucoreName;
    }
    
    public String getEbucoreName () {
        return ebucoreName;
    }
    
    static public FormatElements lookup(String name) {
    	FormatElements retMethod = null;
    	for(FormatElements method : FormatElements.values()) {
    		if (method.name().equals(name)) {
    			retMethod = method;
    			break;
    		}
    	}
    	return retMethod;
    } 
    
}
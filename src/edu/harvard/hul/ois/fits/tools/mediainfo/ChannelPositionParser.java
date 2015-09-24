/**********************************************************************
 * Copyright (c) 2015 by the President and Fellows of Harvard College
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
package edu.harvard.hul.ois.fits.tools.mediainfo;

import java.util.ArrayList;
import java.util.List;

import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;

//
// This class is used to parse the Channael Position String returned by 
// MediaInfo and convert it into a list of X/Y coordinated to be used by
// the Ebucore schema.
//
// Sample Strings are as below:
//
// "Front: C",
// "Front: C C",
// "Front: L R",
// "Front: L R",
// "Front: L R",
// "Front: L C R",
// "Front: L R, Back: C",
// "Front: L C R, Back: C",
// "Front: L R, Side: L R",
// "Front: L C R, Side: L R",
// "Front: L C C R, Side: L R",
// "Front: L C R, Side: L R",
// "Front: L R, Side: L R, Back: L R",
// "Front: L C R, Side: L R, Back: L R",
// "Front: L R, Side: L R, Back: L C C R",
// "Front: L C R, Side: L R, Back: L C R"
//
public class ChannelPositionParser {
	
	private static final String FRONT_POSITION = "Front:";
	private static final String BACK_POSITION = "Back:";
	private static final String SIDE_POSITION = "Side:";
	
	/**
	 * 
	 * @param channelsStr - the channel position string returned by MediaInfo
	 * @return
	 * @throws Exception
	 */
	public List<ChannelPositionWrapper> getChannelsFromString(String channelsStr) 
			throws XmlContentException {
		
		// Make sure we have something here
		if(channelsStr == null || channelsStr.length() == 0) {
			throw new XmlContentException("Channel String " + channelsStr + "is null or empty");
		}
		
		ArrayList <ChannelPositionWrapper>channelList = new ArrayList<ChannelPositionWrapper>();
		
		String[] positionCoordStr = channelsStr.split(",");
		for(String coord : positionCoordStr ) {
			
			if(coord.contains(":")) {
				String[] location = coord.split(":");
				String position = location[1].trim();
				
				if(location != null && location.length != 0) {
					if(coord.contains(FRONT_POSITION)) {
						channelList.addAll(parseDirection(position, FRONT_POSITION.replace(":", "")));
					}
					else if(coord.contains(BACK_POSITION)) {
						channelList.addAll(parseDirection(position, BACK_POSITION.replace(":", "")));
					}
					else if(coord.contains(SIDE_POSITION)) {
						channelList.addAll(parseDirection(position, SIDE_POSITION.replace(":", "")));
					}
					else {
						throw new XmlContentException(coord + " Contains an invalid position identifier");
					}
				}
			}
			else {
				if(coord.equalsIgnoreCase("LFE"))
					continue;
				
				// LFE has XY positiona of 0,0
				channelList.add(new ChannelPositionWrapper(coord, 0, 0));				
			}


		} // for(String coord
		
		return channelList;
		
	}
	
	private static List<ChannelPositionWrapper> parseDirection(String position, String positionSegment) 
		throws  XmlContentException {
		ArrayList <ChannelPositionWrapper>directionList = new ArrayList<ChannelPositionWrapper>();
		
		String[] positionCoordStr = position.split(" ");
		
		for(String coord : positionCoordStr ) {
			LCREnum lcrEnum = LCREnum.lookup(coord);
			
			// TODO: Should we throw an exception here
			// because we did not find a match?
			if(lcrEnum == null) {
				//continue;
				throw new XmlContentException(coord + " is not a valid position ");
			}
			String lcr = lcrEnum.getName();
			int posX = ChannelPositionEnum.lookup(lcr.toUpperCase()).getPosition();
			int posY = ChannelPositionEnum.lookup(positionSegment.toUpperCase()).getPosition();
			
			directionList.add(new ChannelPositionWrapper(
					LCREnum.lookup(coord).getName() + " " + positionSegment,
					posX, posY));
		}
		return directionList;
				
	}
	
	enum LCREnum {
	    L("Left"), C("Center"), R("Right");
	    
		private String name;
	    
		LCREnum(String name) {
	        this.name = name;
	    }
	    
	    public String getName () {
	        return name;
	    }
	    
	    static public LCREnum lookup(String name) {
	    	LCREnum retMethod = null;
	    	for(LCREnum method : LCREnum.values()) {
	    		if (method.name().equals(name)) {
	    			retMethod = method;
	    			break;
	    		}
	    	}
	    	return retMethod;
	    }
	    
	}
	
	enum ChannelPositionEnum {
	    LEFT(-100), CENTER(0), RIGHT(100), FRONT(0), SIDE(100), BACK(200);
	    
	    private int position;
	    private ChannelPositionEnum(int p) {
	        position = p;
	    }
	    public int getPosition() {
	        return position;
	    }
	    
	    static public ChannelPositionEnum lookup(String name) {
	    	ChannelPositionEnum retMethod = null;
	    	for(ChannelPositionEnum method : ChannelPositionEnum.values()) {
	    		if (method.name().equals(name)) {
	    			retMethod = method;
	    			break;
	    		}
	    	}
	    	return retMethod;
	    }	    
	}

}

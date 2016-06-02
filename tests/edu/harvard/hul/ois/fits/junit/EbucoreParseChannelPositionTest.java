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
package edu.harvard.hul.ois.fits.junit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

import java.util.List;

import org.junit.Test;

import edu.harvard.hul.ois.fits.tools.mediainfo.ChannelPositionParser;
import edu.harvard.hul.ois.fits.tools.mediainfo.ChannelPositionWrapper;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContentException;

public class EbucoreParseChannelPositionTest {
	
	// Taken and revised from a MediaInfo header file
	private static final String[] DTS_ChannelPositions =
	{
	    "Front: C",
	    "Front: C C",
	    "Front: L R",
	    "Front: L R",
	    "Front: L R",
	    "Front: L C R",
	    "Front: L R, Back: C",
	    "Front: L C R, Back: C",
	    "Front: L R, Side: L R",
	    "Front: L C R, Side: L R",
	    "Front: L C C R, Side: L R",
	    "Front: L C R, Side: L R",
	    "Front: L R, Side: L R, Back: L R",
	    "Front: L C R, Side: L R, Back: L R",
	    "Front: L R, Side: L R, Back: L C C R",
	    "Front: L C R, Side: L R, Back: L C R"
	};
	
	// Taken and revised from a MediaInfo header file	
	private static final String[] MpegTs_DtsNeural_ChannelPositions_2 =
	{
		"Front: L R, LFE",
		"Front: L C R, LFE",
		"Front: L R, Side: L R, LFE",
		"Front: L C R, Side: L R, LFE",
		"Front: L C R, Side: L R, Back: C, LFE",
		"Front: L C R, Side: L R, Back: L R, LFE",
		"Front: L R, Side: L R, Back: C, LFE",
		"Front: L R, Side: L R, Back: L R, LFE",
	};
	
	@Test
	public void testParse() throws Exception {
		
		ChannelPositionParser app = new ChannelPositionParser();
		
		String channelsStr = "Front: L C R, Side: L R, LFE";

		List <ChannelPositionWrapper>channelList = app.getChannelsFromString(channelsStr);
		
		// Verify we have 6 channels
		assertEquals(channelList.size(), 6);
		
		// Channel number 1 Left Front	 	X: -100		Y: 0
		// Channel number 2 Center Front	X: 0 	  	Y: 0
		// Channel number 3 Right Front	 	X: 100 	  	Y: 0
		// Channel number 4 Left Side	 	X: -100 	Y: 100
		// Channel number 5 Right Side	 	X: 100 	  	Y: 100
		// Channel number 6  LFE	 		X: 0 	  	Y: 0
		ChannelPositionWrapper pos_1 = channelList.get(0);
		ChannelPositionWrapper pos_2 = channelList.get(1);
		ChannelPositionWrapper pos_3 = channelList.get(2);
		ChannelPositionWrapper pos_4 = channelList.get(3);
		ChannelPositionWrapper pos_5 = channelList.get(4);
		ChannelPositionWrapper pos_6 = channelList.get(5);
		
		assertEquals(pos_1.getXpos(), -100);
		assertEquals(pos_1.getYpos(), 0);
		
		assertEquals(pos_2.getXpos(), 0);
		assertEquals(pos_2.getYpos(), 0);
		
		assertEquals(pos_3.getXpos(), 100);
		assertEquals(pos_3.getYpos(), 0);
		
		assertEquals(pos_4.getXpos(), -100);
		assertEquals(pos_4.getYpos(), 100);
		
		assertEquals(pos_5.getXpos(), 100);
		assertEquals(pos_5.getYpos(), 100);
		
		assertEquals(pos_6.getXpos(), 0);
		assertEquals(pos_6.getYpos(), 0);
		
		
//		for(ChannelPositionWrapper channel : channelList) {
//			System.out.println("Channel number " + 
//					(channelList.indexOf(channel) +1) + " " + 
//					channel.getName() + "\t" +
//					" X: " + channel.getXpos() + " \t " +
//					" Y: " + channel.getYpos() );
//		}

	}
	
	@Test
	public void testParse_2() throws Exception {
		
		ChannelPositionParser app = new ChannelPositionParser();
		
		String channelsStr = "Front: L R";

		List <ChannelPositionWrapper>channelList = app.getChannelsFromString(channelsStr);

		// Verify we have 2 channels
		assertEquals(channelList.size(), 2);
		
		//Channel number 1 Left Front	 X: -100 	  Y: 0
		//Channel number 2 Right Front	 X: 100 	  Y: 0
		ChannelPositionWrapper pos_1 = channelList.get(0);
		ChannelPositionWrapper pos_2 = channelList.get(1);	
		
		assertEquals(pos_1.getXpos(), -100);
		assertEquals(pos_1.getYpos(), 0);
		
		assertEquals(pos_2.getXpos(), 100);
		assertEquals(pos_2.getYpos(), 0);		
		

//		for(ChannelPositionWrapper channel : channelList) {
//			System.out.println("Channel number " + 
//					(channelList.indexOf(channel) +1) + " " + 
//					channel.getName() + "\t" +
//					" X: " + channel.getXpos() + " \t " +
//					" Y: " + channel.getYpos() );
//		}

	}	
	
	@Test  
	public void testParseMediaInfoList() throws Exception {

		ChannelPositionParser app = new ChannelPositionParser();
		
		System.out.println("\n\nDTS_ChannelPositions");
		System.out.println("========================");		

		for (String position : DTS_ChannelPositions) {

			System.out.println("\n-- " + position + " -- ");

			List <ChannelPositionWrapper>channelList = app.getChannelsFromString(position);
			for(ChannelPositionWrapper channel : channelList) {
				System.out.println("Channel number " + 
						(channelList.indexOf(channel) +1) + " " + 
						channel.getName() + "\t" +
						" X: " + channel.getXpos() + " \t " +
						" Y: " + channel.getYpos() );
			}		

		}

	}
	
	@Test
	public void testParseMediaInfoList_2() throws Exception {
		
		ChannelPositionParser app = new ChannelPositionParser();
		
		System.out.println("\n\nMpegTs_DtsNeural_ChannelPositions_2");
		System.out.println("=======================================");			
		
		// MpegTs_DtsNeural_ChannelPositions_2
		for (String position : MpegTs_DtsNeural_ChannelPositions_2) {

			System.out.println("\n-- " + position + " -- ");

			List <ChannelPositionWrapper>channelList = app.getChannelsFromString(position);
			for(ChannelPositionWrapper channel : channelList) {
				System.out.println("Channel number " + 
						(channelList.indexOf(channel) +1) + " " + 
						channel.getName() + "\t" +
						" X: " + channel.getXpos() + " \t " +
						" Y: " + channel.getYpos() );
			}		

		}
	}


	@Test  
	public void testParseInvalidChannelPosition() throws Exception {

		ChannelPositionParser app = new ChannelPositionParser();

		// --------------------------------------------------------------------
		// Invalid Channel Positions
		// --------------------------------------------------------------------

		// TODO:
		// NOTE: Front A and Front B is invalid, but Front C is Valid ...
		// What should I do?
		String channelsStr = "Front: A B C, Side: L R, LFE";		
		System.out.println("\n\n-- " + channelsStr + " -- ");	
		List<ChannelPositionWrapper> channelList;
		try {
			channelList = app.getChannelsFromString(channelsStr);
			for(ChannelPositionWrapper channel : channelList ) {
				System.out.println("Channel number " + 
						(channelList.indexOf(channel) +1) + " " + 
						channel.getName() + "\t" +
						" X: " + channel.getXpos() + " \t " +
						" Y: " + channel.getYpos() );
			}
			fail("An exception should have occurred.");
		} catch ( XmlContentException e) {
			//e.printStackTrace();
			assertEquals( "A is not a valid position ", e.getMessage() );
		}


		// Bogus: is an invalid location, so the below throws an exception
		channelsStr = "Front: L C R, Bogus: L R, LFE";		
		System.out.println("\n\n-- " + channelsStr + " -- ");	
		try {
			channelList = app.getChannelsFromString(channelsStr);
			fail("An exception should have occurred.");
		} catch ( XmlContentException e) {
			//e.printStackTrace();
			assertEquals(" Bogus: L R Contains an invalid position identifier", e.getMessage());
		}

		//
		//
		// TODO: The below creates X/Y Position of 0, 0
		//
		// These are default values, should we detect an error ???

		// Invalid Channel String, so the below throws an exception
		channelsStr = "A Bogus Channel Position";		
		System.out.println("\n\n-- " + channelsStr + " -- ");
		channelList = null;
		try {
			channelList = app.getChannelsFromString(channelsStr);
		} catch ( XmlContentException e) {
			fail("No Exception should occur. Default values will be set.");
		}
		if (channelList == null || channelList.isEmpty()) {
			//System.out.println(channelsStr + " produces an empty channel list");
			fail("Default values should be be set.");			
		} 	

	}


}

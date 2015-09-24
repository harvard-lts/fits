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

public class ChannelPositionWrapper {
	
	private String name;		
	private int xPos;		
	private int yPos;
	
	public ChannelPositionWrapper(String name, int xPos, int yPos) {
		this.name=name;
		this.xPos=xPos;
		this.yPos=yPos;
	}

	public String getName() {return name;}
	public int getXpos() {return xPos;}
	public int getYpos() {return yPos;}
	
	//void setName(String name) {this.name=name;}
	public void setXpos(int xPos) {this.xPos=xPos;}
	public void setYpos(int yPos) {this.yPos=yPos;}

}

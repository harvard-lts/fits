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
package edu.harvard.hul.ois.fits.exceptions;

public class FitsException extends Exception {
	
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 1266363844796336485L;
	private Exception embeddedException = null;
	private String message;
	
	public FitsException() {
		super();
	}
    public FitsException(String message) {
        this();
        this.message = message;
    }
    public FitsException(String message, Exception e) {
        this();
        this.embeddedException = e;
        this.message = message;
        if (e.getMessage() != null){
            this.message = this.message + " (" + e.getMessage() + ")";
        }
    }
    
    public String getMessage() {
    	return message;
    }
    
    public Exception getEmbeddedException() {
    	return embeddedException;
    }
}
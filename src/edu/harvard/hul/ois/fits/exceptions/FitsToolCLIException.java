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

public class FitsToolCLIException extends FitsToolException {
		
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 7600086963317407645L;

	public FitsToolCLIException() {
		super();
	}
    public FitsToolCLIException(String message) {
        super(message);
    }
    public FitsToolCLIException(String message, Exception e) {
    	super(message,e);
    }

}
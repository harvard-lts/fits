//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

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

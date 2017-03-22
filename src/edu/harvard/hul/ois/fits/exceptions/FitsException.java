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

public class FitsException extends Exception {

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 1266363844796336485L;
	private Throwable embeddedException = null;
	private String message;

	public FitsException() {
		super();
	}
    public FitsException(String message) {
        this();
        this.message = message;
    }
    public FitsException(String message, Throwable e) {
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

    public Throwable getEmbeddedException() {
    	return embeddedException;
    }
}

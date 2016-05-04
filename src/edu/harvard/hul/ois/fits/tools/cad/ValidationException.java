package edu.harvard.hul.ois.fits.tools.cad;

/**
 * Created by Isaac Simmons on 9/13/2015.
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}

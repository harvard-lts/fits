/**
 * 
 */
package uk.ac.ox.bodleian.beam.tika;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import uk.ac.ox.bodleian.beam.tika.TikaDetail.TikaStatus;

/**
 * TODO: JavaDoc for TikaDetails.<p/>
 * TODO: Tests for TikaDetails.<p/>
 * TODO: Implementation for TikaDetails.<p/>
 * TODO: Re-factor TikaDetails package, with constants, immutability, hashCode and equals.<p/>
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1
 * 
 * Created 24 Jul 2012:11:32:02
 */

public final class TikaDetails {
	private TikaDetails() {
		throw new AssertionError("[TikaDetails] Should never be in defaul constructor.");
	}


	/**
	 * Factory method that creates a TikaDetails object from a Tika MediaType and an
	 * exception.  The method is meant for creation when content type detection has
	 * succeeded but parsing has failed.
	 * 
	 * @param mediaType the Media Type for the details
	 * @param cause an Exception indicating the reason for the parse error
	 * @return a new TikaDetails instance
	 */
	public static final TikaDetail getInstance(MediaType mediaType, Exception cause) {
		return new TikaDetailImpl(mediaType, cause);
	}
	
	/**
	 * @param mediaType
	 * @param metadata
	 * @return a Tika detail instance
	 */
	public static final TikaDetail getInstance(MediaType mediaType, Metadata metadata) {
		return new TikaDetailImpl(mediaType, metadata);
	}

	/**
	 * @param status
	 * @param cause
	 * @return a tika detail instance
	 */
	public static final TikaDetail getInstance(TikaStatus status, Exception cause) {
		return new TikaDetailImpl(status, cause);
	}
}

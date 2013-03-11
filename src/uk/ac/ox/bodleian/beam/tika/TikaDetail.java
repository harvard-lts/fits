/**
 * 
 */
package uk.ac.ox.bodleian.beam.tika;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

/**
 * TODO: JavaDoc for TikaDetail.<p/>
 * TODO: Tests for TikaDetail.<p/>
 * TODO: Implementation for TikaDetail.<p/>
 * 
 * @author  <a href="mailto:carl.wilson@keepitdigital.eu">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1
 * 
 * Created 12 Jul 2012:09:56:44
 */

public interface TikaDetail {
	/**
	 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
	 *          <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT SourceForge</a>
	 *          <a href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
	 * @version 0.1
	 * 
	 * Created 16 Jul 2012:14:16:08
	 */
	public enum TikaStatus {
		/** Indicates status is unknown */
		UNKNOWN,
		/** Indicates status is OK */
		OK,
		/** Indicates parse error when parsing with Apache Tika */
		PARSE_ERROR,
		/** Indicates that the item parsed was damaged, i.e. could not be read */
		DAMAGED;
	}

	/**
	 * @return the TIKA status
	 */
	public TikaStatus getTikaStatus();
	/**
	 * @return the Apache Tika Media Type
	 */
	public MediaType getMediaType();
	/**
	 * @return the Apache Tika metadata object associated with the entry
	 */
	public Metadata getMetadata();
	/**
	 * @return the exception that caused the damaged status
	 */
	public Exception getException();
}

/**
 * 
 */
package uk.ac.ox.bodleian.beam.tika;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;


/**
 * TODO: JavaDoc for TikaDetailImpl.<p/>
 * TODO: Tests for TikaDetailImpl.<p/>
 * TODO: Implementation for TikaDetailImpl.<p/>
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>
 *          <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT SourceForge</a>
 *          <a href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 * 
 * Created 12 Jul 2012:11:31:48
 */

public class TikaDetailImpl implements TikaDetail {
	private Exception cause = null;
	private TikaStatus tikaStatus = TikaStatus.UNKNOWN;
	private MediaType mediaType = null;
	private Metadata metadata = null;

	@SuppressWarnings("unused")
	private TikaDetailImpl() {/** disable */}
	TikaDetailImpl(MediaType mediaType, Exception cause) {
		this.tikaStatus = TikaStatus.PARSE_ERROR;
		this.mediaType = mediaType;
		this.metadata = new Metadata();
		this.cause = cause;
	}

	TikaDetailImpl(MediaType mediaType, Metadata metadata) {
		this.tikaStatus = TikaStatus.OK;
		this.mediaType = mediaType;
		this.metadata = metadata;
	}
	
	TikaDetailImpl(TikaStatus status, Exception cause) {
		this.tikaStatus = status;
		this.cause = cause;
	}
	/**
	 * @see uk.ac.ox.bodleian.beam.tika.TikaDetail#getTikaStatus()
	 */
	@Override
	public TikaStatus getTikaStatus() {
		return this.tikaStatus;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.tika.TikaDetail#getMediaType()
	 */
	@Override
	public MediaType getMediaType() {
		return this.mediaType;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.tika.TikaDetail#getMetadata()
	 */
	@Override
	public Metadata getMetadata() {
		return this.metadata;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.tika.TikaDetail#getException()
	 */
	@Override
	public Exception getException() {
		return this.cause;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TikaDetail [mediaType=" + this.mediaType
				+ ((this.tikaStatus == TikaStatus.OK) ? (", metadata=" + this.metadata) : (", tikaStatus="
						+ this.tikaStatus + ", cause=" + this.cause)) + "]";
	}
}

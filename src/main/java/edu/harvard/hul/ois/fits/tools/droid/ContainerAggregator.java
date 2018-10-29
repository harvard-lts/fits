//
// Copyright (c) 2017 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.droid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;

/**
 * This class aggregates data from Droid about container (ZIP) type files.
 * 
 * @author dan179
 */
public class ContainerAggregator {
	
	// Maps format type to number of these types of files with a ZIP file
	private Map<String, Integer> formatToCount;
	
	// Aggregated original size of all files contained within the ZIP file.
	private long originalSize;

	// Aggregated compressed size of all files contained within the ZIP file.
	// If ZIP is not compressed then should equal the original size.
	private long compressedSize;
	
	private boolean isEncrypted = false;
	
	private static final String UNKNOWN_FORMAT = "Unknown";

	public ContainerAggregator() {
		formatToCount = new TreeMap<>(); // order entries by key for the sake of XMLUnit tests
	}

	/**
	 * Aggregated original size of files with a ZIP file.
	 * 
	 * @return Original size of all files within ZIP file in bytes.
	 */
	public long getOriginalSize() {
		return originalSize;
	}

	/**
	 * Increment the calculated original size of the examined ZIP file by the original size of a contained file.
	 */
	public void incrementOriginalSize(long originalSize) {
		this.originalSize += originalSize;
	}

	/**
	 * Aggregated compressed size of files with a ZIP file.
	 * 
	 * @return Compressed size of all files within ZIP file in bytes.
	 */
	public long getCompressedSize() {
		return compressedSize;
	}

	/**
	 * Increment the compressed size of the examined ZIP file by the original size of a contained file.
	 */
	public void incrementCompressedSize(long compressedSize) {
		this.compressedSize += compressedSize;
	}
	
	/**
	 * Add a format type to this collection and increment count for this type.
	 */
	public void addFormat(String format) {
		if (format !=null) {
			Integer cnt = formatToCount.get(format);
			if (cnt == null) {
				formatToCount.put(format, 1);
			} else {
				cnt++;
				formatToCount.put(format, cnt);
			}
		}
	}
	
	public void incrementUnknownFormat() {
		Integer cnt = formatToCount.get(UNKNOWN_FORMAT);
		if (cnt == null) {
			formatToCount.put(UNKNOWN_FORMAT, 1);
		} else {
			cnt++;
			formatToCount.put(UNKNOWN_FORMAT, cnt);
		}
	}
	
	/**
	 * A Map of format type to number of each format type.
	 * 
	 * @return Format to count mapping
	 */
	public Map<String, Integer> getFormatCounts() {
		return Collections.unmodifiableMap(formatToCount);
	}
	
	/**
	 * Total number of all format types added to this collection.
	 * 
	 * @return Total number for formats added to this collection.
	 */
	public int getTotalEntriesCount() {
		int total = 0;
		for (Integer val : formatToCount.values()) {
			total += val;
		}
		return total;
	}
	
	/**
	 * The compression method as defined the Java ZipEntry. Currently only values for 'stored' (uncompressed)
	 * and 'deflate' (compressed) are used.
	 * 
	 * @return The value corresponding to
	 * @see java.util.zip.ZipEntry
	 */
	public int getCompressionMethod() {
		return getCompressedSize() < getOriginalSize() ? ZipEntry.DEFLATED : ZipEntry.STORED;
	}

	/**
	 * Whether the container being examined is encrypted.
	 */
	public boolean isEncrypted() {
		return isEncrypted;
	}

	/**
	 * Sets whether this container being examined is encrypted.
	 */
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContainerAggregator [formatToCount=");
		builder.append(formatToCount);
		builder.append("]");
		builder.append(" total count: ");
		builder.append(getTotalEntriesCount());
		builder.append(", originalSize: ");
		builder.append(originalSize);
		builder.append(", compressedSize: ");
		builder.append(compressedSize);
		builder.append(", isEncrypted: ");
		builder.append(isEncrypted);
		return builder.toString();
	}

}

/**
 * This file has been modified by Harvard University, June, 2017, for the purposes of incorporating
 * into the FITS application. The original can be found here: https://github.com/digital-preservation/droid
 * 
 * Copyright (c) 2016, The National Archives <pronom@nationalarchives.gsi.gov.uk>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following
 * conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of the The National Archives nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.harvard.hul.ois.fits.tools.droid;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.log4j.Logger;

import uk.gov.nationalarchives.droid.command.action.CommandExecutionException;
import uk.gov.nationalarchives.droid.container.ContainerSignatureDefinitions;
import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;
import uk.gov.nationalarchives.droid.core.interfaces.resource.ZipEntryIdentificationRequest;

/**
 * Identifier for files held in a ZIP archive.
 * 
 * @author rbrennan
 */
public class ZipArchiveContentIdentifier extends ArchiveContentIdentifier {

	 private static final Logger logger = Logger.getLogger(ZipArchiveContentIdentifier.class);
	 
    /**
     * 
     * @param binarySignatureIdentifier     binary signature identifier
     * @param containerSignatureDefinitions container signatures
     * @param path                          current archive path 
     * @param slash                         local path element delimiter
     * @param slash1                        local first container prefix delimiter
     */
    public ZipArchiveContentIdentifier(final BinarySignatureIdentifier binarySignatureIdentifier,
            final ContainerSignatureDefinitions containerSignatureDefinitions,
            final String path, final String slash, final String slash1) {
    
            super(binarySignatureIdentifier, containerSignatureDefinitions,
        path, slash, slash1, false);
    }
    
    /**
     * @param uri The URI of the file to identify
     * @param request The Identification Request
     * @return The aggregated data of the examined ZIP file
     * @throws CommandExecutionException When an exception happens during execution
     * @throws CommandExecutionException When an exception happens during archive file access
     */
    public ContainerAggregator identify(final URI uri, final IdentificationRequest  request)
        throws CommandExecutionException {

        final String newPath = makeContainerURI("zip", request.getFileName());
        setSlash1("");
        InputStream zipIn = null;
        ContainerAggregator aggregator = new ContainerAggregator();
        try {
            zipIn = request.getSourceInputStream();
            final ZipArchiveInputStream in = new ZipArchiveInputStream(zipIn);
            try {
                ZipArchiveEntry entry = null;
                Integer compressionMethod = null;
                while ((entry = (ZipArchiveEntry) in.getNextZipEntry()) != null) {
                    final String name = entry.getName();
                    if (!entry.isDirectory()) {
                        final RequestMetaData metaData = new RequestMetaData(entry.getSize(), 2L, name);
                        final RequestIdentifier identifier = new RequestIdentifier(uri);
                        final ZipEntryIdentificationRequest zipRequest =
                            new ZipEntryIdentificationRequest(metaData, identifier, getTmpDir(), false);
                        
                        if (compressionMethod != null && !compressionMethod.equals(entry.getMethod())) {
                        	logger.warn("Different compression method: " + compressionMethod + ", entry method: " + entry.getMethod());
                        }

                        compressionMethod = entry.getMethod(); // throws UnsupportedZipFeatureException
                        expandContainer(zipRequest, in, newPath, aggregator);  // zipRequest.size() is uncompressed
                        logger.debug("zipRequest size(): " + zipRequest.size() + " -- entry.getCompressedSize(): " + entry.getCompressedSize() + " -- entry.getSize(): " + entry.getSize());
                        if (entry.getCompressedSize() > 0) {
                        	aggregator.incrementCompressedSize(entry.getCompressedSize());
                        }
                        // in some situations the value returned is -1
                        if (entry.getSize() > 0) {
                        	aggregator.incrementOriginalSize(entry.getSize());
                        } else if (zipRequest.size() > 0) {
                        	aggregator.incrementOriginalSize(zipRequest.size());
                        }
                    }
                }
            } catch (UnsupportedZipFeatureException e) {
            	// For now this indicates that we're attempting (and failing) to read from an encrypted ZIP file.
            	aggregator.setEncrypted(true);
            } finally {
                if (in != null) {
                    in.close();
                }
                // shows collection of files within ZIP file
                logger.debug("--------------");
                logger.debug(aggregator);
                logger.debug("--------------");
            }
        } catch (IOException ioe) {
        	logger.warn(ioe + " (" + newPath + ")"); // continue after corrupt archive 
        } finally {
            if (zipIn != null) {
                try {
                    zipIn.close();
                } catch (IOException ioe) {
                    throw new CommandExecutionException(ioe.getMessage(), ioe);
                }
            }
        }
        return aggregator;
    }
}


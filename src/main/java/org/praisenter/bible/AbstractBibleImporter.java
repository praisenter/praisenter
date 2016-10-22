/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.bible;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract class for importing bibles into the bible library.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class AbstractBibleImporter implements BibleImporter {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Helper method to fully read a given input stream into a byte[].
	 * <p>
	 * This is primarily used with the ZipInputStream class to ensure that it's not
	 * closed by whatever reader is used to parse the file.
	 * @param stream the stream to read
	 * @return byte[]
	 * @throws IOException if an IO error occurs
	 */
	static final byte[] read(InputStream stream) throws IOException {
		// read the whole file into memory since the SAX Parser will close the stream
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[4096];
		while ((nRead = stream.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
		buffer.flush();
		byte[] content = buffer.toByteArray();
		try {
			buffer.close();
		} catch (Exception ex) {
			LOGGER.warn("Failed to close output buffer.", ex);
		}
		return content;
	}
}

package org.praisenter.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Streams {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private Streams() {}
	
	/**
	 * Helper method to fully read a given input stream into a byte[].
	 * <p>
	 * This is primarily used with the ZipInputStream class to ensure that it's not
	 * closed by whatever reader is used to parse the file.
	 * @param stream the stream to read
	 * @return byte[]
	 * @throws IOException if an IO error occurs
	 */
	public static final byte[] read(InputStream stream) throws IOException {
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

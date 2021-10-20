package org.praisenter.utility;

import java.io.InputStream;
import java.nio.file.Path;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

public enum MimeType {
	XML("application/xml"),
	JSON("application/json"),
	ZIP("application/zip");
	
	private final String mimeType;
	
	private MimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	// helpers

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Tika TIKA = new Tika();
	
	/**
	 * Attempts to reconcile the mime types detected by Tika and mime.types.
	 * <p>
	 * What we are looking here is whether we agree on image, audio, or video mostly.
	 * The actual file types aren't really of concern.
	 * @param mime the mime.types mime type
	 * @param tika the Tika mime type
	 * @return String
	 */
	private static final String reconcileMimeTypes(String mime, String tika) {
		boolean hasTika = !StringManipulator.isNullOrEmpty(tika);
		boolean hasMime = !StringManipulator.isNullOrEmpty(mime) && !mime.equalsIgnoreCase("application/octet-stream");
		
		if (hasTika && !hasMime) {
			// this indicates mime.types didn't know what it is
			// so use whatever tika gave us as long as it's non-null/empty
			return tika;
		} else if (hasTika && hasMime) {
			if (mime.contains("ogg")) {
				// then we can't trust tika (apparently it only detects ogg audio
				// and when run against an ogg video it returns audio/xxx)
				return mime;
			}
			return tika;
		}
		return mime;
	}
	
	/**
	 * Returns the mime-type for the given path.
	 * @param path the path
	 * @return String
	 */
	public static final String get(Path path) {
		// get the mime type based on the mime.types mapping
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		String mime = map.getContentType(path.toString());
		
		// get the mime type based on what Tika thinks
		String tika = null;
		try {
			tika = TIKA.detect(path);
		} catch (Exception ex) {
			LOGGER.warn("Failed to detect mime type of path using Tika.", ex);
		}
		
		// try to reconcile
		return reconcileMimeTypes(mime, tika);
	}
	
	/**
	 * Returns the mime-type for the given file name.
	 * @param name the file name
	 * @return String
	 */
	public static final String get(String name) {
		// get the mime type based on the mime.types mapping
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		String mime = map.getContentType(name);
		
		// get the mime type based on what Tika thinks
		String tika = TIKA.detect(name);
		
		// try to reconcile
		return reconcileMimeTypes(mime, tika);
	}
	
	/**
	 * Returns the mime-type for the given stream.
	 * @param stream the stream
	 * @return String
	 */
	public static final String get(InputStream stream) {
		return MimeType.get(stream, null);
	}
	
	/**
	 * Returns the mime-type for the given stream.
	 * @param stream the stream
	 * @param fileName the file name; can be null
	 * @return String
	 */
	public static final String get(InputStream stream, String fileName) {
		try {
			if (StringManipulator.isNullOrEmpty(fileName)) {
				return TIKA.detect(stream);
			} else {
				return TIKA.detect(stream, fileName);
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed to detect mime type of input stream using Tika.", ex);
		}
		return null;
	}
	
	/**
	 * Returns true if the given path matches this mime-type.
	 * @param path the path
	 * @return boolean
	 */
	public final boolean check(Path path) {
		String mimeType = get(path);
		if (mimeType == null) return false;
		return this.mimeType.equalsIgnoreCase(mimeType);
	}
	
	/**
	 * Returns true if the given file name matches this mime-type.
	 * @param name the file name
	 * @return boolean
	 */
	public final boolean check(String name) {
		String mimeType = get(name);
		if (mimeType == null) return false;
		return this.mimeType.equalsIgnoreCase(mimeType);
	}
	
	/**
	 * Returns true if the given stream matches this mime-type.
	 * @param stream the stream
	 * @return boolean
	 */
	public final boolean check(InputStream stream) {
		return this.check(stream, null);
	}
	
	/**
	 * Returns true if the given stream matches this mime-type.
	 * @param stream the stream
	 * @param fileName the file name
	 * @return boolean
	 */
	public final boolean check(InputStream stream, String fileName) {
		String mimeType = get(stream, fileName);
		if (mimeType == null) return false;
		return this.mimeType.equalsIgnoreCase(mimeType);
	}
	
	/**
	 * Returns true if the given mime type is equal to this mime type.
	 * @param mimeType the mime type
	 * @return boolean
	 */
	public final boolean is(String mimeType) {
		return this.mimeType.equalsIgnoreCase(mimeType);
	}
}

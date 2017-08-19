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
package org.praisenter.utility;

import java.io.InputStream;
import java.nio.file.Path;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

/**
 * Helper enum to determine the mime-type of files.
 * @author William Bittle
 * @version 3.0.0
 */
public enum MimeType {
	// enum definition
	
	/** XML mimetype */
	XML("application/xml"),
	
	/** JSON mimetype */
	JSON("application/json"),
	
	/** ZIP mimetype */
	ZIP("application/zip");
	
	/** The mime-type of the enum */
	private final String mimeType;
	
	/**
	 * Minimal constructor.
	 * @param mimeType the mime type
	 */
	private MimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	// helpers

	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The Tika object */
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
		try {
			return TIKA.detect(stream);
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
		String mimeType = get(stream);
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

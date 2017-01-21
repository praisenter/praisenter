package org.praisenter.utility;

import java.nio.file.Path;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

public enum MimeType {
	XML("application/xml"),
	ZIP("application/zip");
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final Tika TIKA = new Tika();
	
	private final String mimeType;
	
	private MimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public static final String get(Path path) {
		String mimeType = null;
		try {
			mimeType = TIKA.detect(path);
		} catch (Exception ex) {
			LOGGER.warn("Failed to detect mime type using Tika.", ex);
			// fallback
			FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
			mimeType = map.getContentType(path.toString());
		}
		return mimeType;
	}
	
	public static final String get(String name) {
		String mimeType = TIKA.detect(name);
		// fallback
		if (mimeType == null) {
			FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
			mimeType = map.getContentType(name);
		}
		return mimeType;
	}
	
//	public static final String get(String fileName) {
//		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
//		String mimeType = map.getContentType(fileName);
//		// check the mimeType
//		if (mimeType.toLowerCase().equals("application/octet-stream")) {
//			// this indicates that it didn't know the mime type
//		}
//	}
	
	public final boolean check(Path path) {
		String mimeType = get(path);
		if (mimeType == null) return false;
		return this.mimeType.equals(mimeType.toLowerCase());
	}
	
	public final boolean check(String name) {
		String mimeType = get(name);
		if (mimeType == null) return false;
		return this.mimeType.equals(mimeType.toLowerCase());
	}
	
//	public final boolean check(String fileName) {
//		String mimeType = MimeType.get(fileName);
//		if (mimeType == null) return false;
//		return this.mimeType.equals(mimeType.toLowerCase());
//	}
}

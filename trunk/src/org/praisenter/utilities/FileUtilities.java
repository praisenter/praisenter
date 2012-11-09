package org.praisenter.utilities;

import java.io.File;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

/**
 * Utility class for file handling.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileUtilities {
	/**
	 * Returns the lower case extension of the given file.
	 * <p>
	 * Returns null if the file has no extension.
	 * @param file the file
	 * @return String
	 */
	public static final String getExtension(File file) {
		return FileUtilities.getExtension(file.getName());
	}
	
	/**
	 * Returns the lower case extension of the given file name.
	 * <p>
	 * Returns null if the file name has no extension.
	 * @param fileName the file name
	 * @return String
	 */
	public static final String getExtension(String fileName) {
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot > 0 && lastDot < fileName.length() - 1) {
			return fileName.substring(lastDot + 1).toLowerCase();
		}
		
		return null;
	}
	
	/**
	 * Returns the mime type of the given file.
	 * @param filePath the file path and name
	 * @return String
	 */
	public static final String getContentType(String filePath) {
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		return map.getContentType(filePath);
	}
}

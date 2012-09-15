package org.praisenter.utilities;

import java.io.File;

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
}

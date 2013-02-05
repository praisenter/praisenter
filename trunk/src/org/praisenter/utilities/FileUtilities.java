/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.utilities;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

/**
 * Utility class for file handling.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class FileUtilities {
	/** Hidden default constructor */
	private FileUtilities() {}
	
	/** The default file system */
	private static final FileSystem DEFAULT = FileSystems.getDefault();
	
	/**
	 * Returns the separator for files.
	 * @return String
	 */
	public static final String getFileSeparator() {
		return DEFAULT.getSeparator();
	}
	
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

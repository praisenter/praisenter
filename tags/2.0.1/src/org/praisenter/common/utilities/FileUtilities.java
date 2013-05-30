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
package org.praisenter.common.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.praisenter.common.CreateFileException;

/**
 * Utility class for file handling.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class FileUtilities {
	/** The default file system */
	private static final FileSystem DEFAULT = FileSystems.getDefault();
	
	/** Hidden default constructor */
	private FileUtilities() {}
	
	/**
	 * Returns the separator for files.
	 * @return String
	 */
	public static final String getSeparator() {
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

	/**
	 * Creates the given folder if it doesn't exist.
	 * @param folder the folder
	 */
	public static final void createFolder(String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			// create the directory if it doesn't exist
			file.mkdirs();
		}
	}
	
	/**
	 * Copies the given file on the classpath to the given location using the same name.
	 * <p>
	 * Returns true if the copy was successful.
	 * <p>
	 * If the file exists, this method will overwrite the contents of the file.
	 * @param classpath the path in the classpath to the file
	 * @param filename the file name of the file in the classpath
	 * @param folder the destination folder
	 * @throws CreateFileException thrown if a new file could not be created
	 * @throws FileNotFoundException thrown if the classpath file was not found or the destination file was not found
	 * @throws IOException thrown if an IO error occurs
	 */
	public static final void copyFileFromClasspath(String classpath, String filename, String folder) throws CreateFileException, FileNotFoundException, IOException {
		// get the classpath resource
		try (InputStream is = FileUtilities.class.getResourceAsStream(classpath + filename)) {
			// see if we found the classpath resource
			if (is == null) {
				throw new FileNotFoundException(classpath + filename);
			}
			
			// create the file for the resource to go into
			File file = new File(folder + FileUtilities.getSeparator() + filename);
			
			// see if the file exists
			if (!file.exists()) {
				// attempt to create it
				if (!file.createNewFile()) {
					throw new CreateFileException();
				}
			}
			
			// copy the contents of the file
			try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				 BufferedInputStream bis = new BufferedInputStream(is);)	{
				int bytesRead = 0;
				byte[] buffer = new byte[1024];
				while ((bytesRead = bis.read(buffer, 0, buffer.length)) > 0) {
					bos.write(buffer, 0, bytesRead);
				}
				bos.flush();
			}
		}
	}
}

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Utility class for handling generic zip file operations.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class ZipUtilities {
	/** The local buffer size */
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * Unzips the given zip file into the given folder retaining the folder structure of the zip.
	 * <p>
	 * This method does not use the name of the zip file as the top level folder.
	 * @param zip the zip to unzip
	 * @param folder the folder to place the contents of the zip
	 * @throws ZipException thrown if the zip file is invalid
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws IOException thrown if an IO error occurs
	 * @throws IllegalStateException thrown if the zip file is closed prematurely
	 * @throws SecurityException thrown if a security check fails
	 */
	public static final void unzip(String zip, String folder) throws ZipException, FileNotFoundException, IOException, IllegalStateException, SecurityException {
		// verify input
		if (zip == null || zip.length() == 0 
		 || folder == null || folder.length() == 0) {
			// in any of these cases just silently return
			return;
		}
		
		// get a file reference to the zip
		try (ZipFile zipFile = new ZipFile(zip)) {
			// get all the entries
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			
			// loop through all the entries
			while (entries.hasMoreElements()) {
				// get the entry
				ZipEntry entry = entries.nextElement();
				String currentEntry = entry.getName();
				File destFile = new File(folder, currentEntry);
				File destinationParent = destFile.getParentFile();

				// create the path if necessary
				destinationParent.mkdirs();

				// check for file type
				if (!entry.isDirectory()) {
					int bytesRead;
					// establish buffer for writing file
					byte data[] = new byte[BUFFER_SIZE];
					
					// read the entry and write it to the location
					try (BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
						 FileOutputStream fos = new FileOutputStream(destFile);
						 BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);) {
						// read and write until last byte is encountered
						while ((bytesRead = is.read(data, 0, BUFFER_SIZE)) != -1) {
							dest.write(data, 0, bytesRead);
						}
					}
				}
			}
		}
	}
}

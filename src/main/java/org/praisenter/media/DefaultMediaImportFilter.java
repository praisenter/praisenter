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
package org.praisenter.media;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * The default media import filter which simply copies the source to the target.
 * @author William Bittle
 * @version 3.0.0
 */
public class DefaultMediaImportFilter implements MediaImportFilter {
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaImportFilter#getTarget(java.nio.file.Path, java.lang.String, org.praisenter.media.MediaType)
	 */
	@Override
	public Path getTarget(Path location, String name, MediaType type) {
		// by default it should be the file name in the location
		return location.resolve(name);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaImportFilter#filter(java.nio.file.Path, java.nio.file.Path, org.praisenter.media.MediaType)
	 */
	@Override
	public void filter(Path source, Path target, MediaType type) throws TranscodeException, FileAlreadyExistsException, IOException {
		// just copy from source to target
		
		// see if we can use the same name in the destination file
		if (Files.exists(target)) {
			throw new FileAlreadyExistsException(target.toAbsolutePath().toString());
		}
		
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}
}

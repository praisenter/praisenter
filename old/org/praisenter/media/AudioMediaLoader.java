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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.media.tools.FFProbeMediaMetadata;
import org.praisenter.data.media.tools.MediaToolExecutionException;

/**
 * {@link MediaLoader} that loads audio media.
 * @author William Bittle
 * @version 3.0.0
 */
public final class AudioMediaLoader extends AbstractMediaLoader implements MediaLoader {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public AudioMediaLoader(MediaLibraryContext context) {
		super(context);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String mimeType) {
		if (mimeType != null && mimeType.startsWith("audio")) {
			// ffmpeg/humble does not support midi
			if (mimeType.contains("midi")) {
				return false;
			}
			// handle any mimetype
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.nio.file.Path)
	 */
	@Override
	public MediaLoadResult load(Path path) throws MediaImportException {
		if (Files.exists(path) && Files.isRegularFile(path)) {
			// get the metadata
			try {
				FFProbeMediaMetadata metadata = this.context.getTools().ffprobeExtractMetadata(path);

				if (!metadata.hasAudio()) {
					LOGGER.error("No audio stream present in file: '{}'", path.toAbsolutePath().toString());
					throw new MediaImportException("No audio stream was found in the file '" + path.toAbsolutePath().toString() + "'.");
				}
				
				final Media media = Media.forAudio(
						path, 
						metadata.getFormat(), 
						metadata.getLength(), 
						null);
				
				LOGGER.debug("Audio media '{}' loaded", path);
				return new MediaLoadResult(media);
			} catch (InterruptedException e) {
				LOGGER.error("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' was interrupted.", e);
				throw new MediaImportException("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' was interrupted.", e);
			} catch (MediaToolExecutionException | IOException e) {
				LOGGER.error("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' failed.", e);
				throw new MediaImportException("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' failed.", e);
			}
		} else {
			throw new MediaImportException(new FileNotFoundException(path.toAbsolutePath().toString()));
		}
	}
}

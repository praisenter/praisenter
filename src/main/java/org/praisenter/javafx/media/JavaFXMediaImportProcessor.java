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
package org.praisenter.javafx.media;

import java.io.IOException;
import java.nio.file.Path;

import org.praisenter.MediaType;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.media.DefaultMediaImportProcessor;
import org.praisenter.media.MediaImportException;
import org.praisenter.media.MediaImportProcessor;
import org.praisenter.media.MediaLibraryContext;
import org.praisenter.tools.ToolExecutionException;

/**
 * {@link MediaImportProcessor} used to transcode audio and video into the formats playable by JavaFX.
 * @author William Bittle
 * @version 3.0.0
 */
public final class JavaFXMediaImportProcessor extends DefaultMediaImportProcessor implements MediaImportProcessor {
	/** The video target extension (will be treated as the destination format as well) */
	public static final String DEFAULT_VIDEO_EXTENSION = ".mp4";
	
	/** The audio target extension (will be treated as the destination format as well) */
	public static final String DEFAULT_AUDIO_EXTENSION = ".m4a";
	
	/** The default FFmpeg command for transcoding */
	public static final String DEFAULT_TRANSCODE_COMMAND = "{ffmpeg} -v fatal -i {source} -y -ignore_unknown {target}";
	
	/** The context */
	private final MediaLibraryContext context;
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public JavaFXMediaImportProcessor(MediaLibraryContext context) {
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.DefaultMediaImportFilter#getTarget(java.nio.file.Path, java.lang.String, org.praisenter.media.MediaType)
	 */
	@Override
	public Path getTarget(Path location, String name, MediaType type) {
		// check if transcoding is enabled
		if (this.context.getConfiguration().getBoolean(Setting.MEDIA_TRANSCODING_ENABLED, true)) {
			// we have to transcode audio/video to what JavaFX can support
			if (type == MediaType.VIDEO || type == MediaType.AUDIO) {
				// get the new extension
				String ext = 
						type == MediaType.VIDEO 
						? this.context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_VIDEO_EXTENSION, DEFAULT_VIDEO_EXTENSION)
						: this.context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_AUDIO_EXTENSION, DEFAULT_AUDIO_EXTENSION);
				if (!name.toLowerCase().endsWith(ext)) {
					name += ext;
				}
			}
		}
		return super.getTarget(location, name, type);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.DefaultMediaImportProcessor#process(java.nio.file.Path, java.nio.file.Path, org.praisenter.MediaType)
	 */
	@Override
	public void process(Path source, Path target, MediaType type) throws MediaImportException {
		// is transcoding enabled?
		if (this.context.getConfiguration().getBoolean(Setting.MEDIA_TRANSCODING_ENABLED, true)) {
			// is the media audio or video?
			if (type == MediaType.VIDEO || type == MediaType.AUDIO) {
				// NOTE: attempting to read and play media here to verify whether it was playable by JavaFX failed.
				// it would play here, but not when attached to a visible MediaView. So for now we'll just have to
				// always transcode the video first
				
				// transcode to supported formats
				this.transcode(source, target, type);
				return;
			}
		}
		super.process(source, target, type);
	}
	
	/**
	 * Transcodes the given source file from its current format to a supported format using FFmpeg CLI.
	 * <p>
	 * This method blocks until transcoding is complete.
	 * @param source the source file
	 * @param target the target file
	 * @param type the media type
	 * @throws MediaImportException if an error occurs during transcoding
	 */
	private final void transcode(Path source, Path target, MediaType type) throws MediaImportException {
		// split the command by whitespace
		String command = (type == MediaType.VIDEO 
				 ? this.context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_VIDEO_COMMAND, DEFAULT_TRANSCODE_COMMAND)
				 : this.context.getConfiguration().getString(Setting.MEDIA_TRANSCODING_AUDIO_COMMAND, DEFAULT_TRANSCODE_COMMAND));
		
		try {
			this.context.getTools().ffmpegTranscode(command, source, target);
		} catch (IOException | ToolExecutionException ex) {
			throw new MediaImportException("Failed to transcode media '" + source.toAbsolutePath().toString() + "'.", ex);
		} catch (InterruptedException ex) {
			throw new MediaImportException("Failed to transcode media '" + source.toAbsolutePath().toString() + "' due to the process being interrupted.", ex);
		}
	}
}

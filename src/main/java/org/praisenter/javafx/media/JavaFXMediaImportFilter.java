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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.javafx.configuration.Configuration;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.media.FFmpegMediaImportFilter;
import org.praisenter.media.MediaImportFilter;
import org.praisenter.media.MediaType;
import org.praisenter.media.TranscodeException;

/**
 * {@link MediaImportFilter} used to transcode audio and video into the formats playable by JavaFX.
 * @author William Bittle
 * @version 3.0.0
 */
public final class JavaFXMediaImportFilter extends FFmpegMediaImportFilter implements MediaImportFilter {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The video target extension (will be treated as the destination format as well) */
	public static final String DEFAULT_VIDEO_EXTENSION = ".mp4";
	
	/** The audio target extension (will be treated as the destination format as well) */
	public static final String DEFAULT_AUDIO_EXTENSION = ".m4a";
	
	/** The default FFmpeg command for transcoding */
	public static final String DEFAULT_COMMAND = "{ffmpeg} -i {source} -y -ignore_unknown {target}";
	
	/** The application configuration */
	private final Configuration configuration;
	
	/**
	 * Minimal constructor.
	 * @param path the path to place the files required for filtering
	 * @param configuration the application configuration
	 */
	public JavaFXMediaImportFilter(Path path, Configuration configuration) {
		super(path);
		this.configuration = configuration;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.DefaultMediaImportFilter#getTarget(java.nio.file.Path, java.lang.String, org.praisenter.media.MediaType)
	 */
	@Override
	public Path getTarget(Path location, String name, MediaType type) {
		// check if transcoding is enabled
		if (this.configuration.getBoolean(Setting.MEDIA_TRANSCODING_ENABLED, true)) {
			// we have to transcode audio/video to what JavaFX can support
			if (type == MediaType.VIDEO || type == MediaType.AUDIO) {
				// get the new extension
				String ext = 
						type == MediaType.VIDEO 
						? this.configuration.getString(Setting.MEDIA_TRANSCODING_VIDEO_EXTENSION, DEFAULT_VIDEO_EXTENSION)
						: this.configuration.getString(Setting.MEDIA_TRANSCODING_AUDIO_EXTENSION, DEFAULT_AUDIO_EXTENSION);
				if (!name.toLowerCase().endsWith(ext)) {
					name += ext;
				}
			}
		}
		return super.getTarget(location, name, type);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.DefaultMediaImportFilter#filter(java.nio.file.Path, java.nio.file.Path, org.praisenter.media.MediaType)
	 */
	@Override
	public void filter(Path source, Path target, MediaType type) throws TranscodeException, IOException {
		// is transcoding enabled?
		if (this.configuration.getBoolean(Setting.MEDIA_TRANSCODING_ENABLED, true)) {
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
		super.filter(source, target, type);
	}
	
	/**
	 * Transcodes the given source file from its current format to a supported format using FFmpeg CLI.
	 * <p>
	 * This method blocks until transcoding is complete.
	 * @param source the source file
	 * @param target the target file (with extension to determine output format)
	 * @param type the media type; should only be {@link MediaType#VIDEO} or {@link MediaType#AUDIO}
	 * @throws TranscodeException
	 */
	private final void transcode(Path source, Path target, MediaType type) throws TranscodeException {
		// shouldn't happen, but lets plan for it
		if (this.ffmpeg == null) {
			throw new TranscodeException("FFmpeg executable not available.");
		}
		
		List<String> command = new ArrayList<String>();
		
		// split the command by whitespace
		String[] parts = 
				(type == MediaType.VIDEO 
				 ? this.configuration.getString(Setting.MEDIA_TRANSCODING_VIDEO_COMMAND, DEFAULT_COMMAND)
				 : this.configuration.getString(Setting.MEDIA_TRANSCODING_AUDIO_COMMAND, DEFAULT_COMMAND)
				).split("\\s+");
		// add all the parts to the command list replacing {x} with the appropriate path
		for (String part : parts) {
			String token = part.trim().toLowerCase();
			if (token.equals("{ffmpeg}")) {
				command.add(this.ffmpeg.toAbsolutePath().toString());
			} else if (token.equals("{source}")) {
				command.add(source.toAbsolutePath().toString());
			} else if (token.equals("{target}")) {
				command.add(target.toAbsolutePath().toString());
			} else {
				command.add(part.trim());
			}
		}
		
		// run the command
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process process = null;
		
		try {
			LOGGER.info("Starting FFmpeg process with command: " + String.join(" ", command));
			process = pb.start();
			LOGGER.info("Waiting for FFmpeg to complete transcoding...");
			
			// we must read the input streams otherwise they fill up
			// and the sub process will hang
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line = null;
	        StringBuilder s = new StringBuilder();
	        // readLine is a blocking call so this will pause us until the
	        // executable completes or encounters an error
	        while((line = input.readLine()) != null) {            
	            s.append(line).append(Constants.NEW_LINE);
	        }
	        
	        try {
	        	// just in case i guess
	        	int exitCode = process.waitFor();
	        	LOGGER.info("FFmpeg completed with exitcode = " + exitCode);
	        	try {
					input.close();
				} catch (IOException e) {}
	        	
	        	if (exitCode != 0) {
	        		LOGGER.error(s.toString());
	    			throw new TranscodeException("Please refer to the log for FFmpeg output.");
	    		}
	        } catch (InterruptedException ex) {
	        	throw new TranscodeException("The thread was interrupted while wait for the transcoding to complete.", ex);
	        }
		} catch (IOException ex) {
			throw new TranscodeException("Failed to transcode file '" + source.toAbsolutePath().toString() + "'.", ex);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}
}

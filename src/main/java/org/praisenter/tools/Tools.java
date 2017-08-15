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
package org.praisenter.tools;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.LockMap;
import org.praisenter.json.JsonIO;
import org.praisenter.media.CodecType;
import org.praisenter.media.MediaCodec;
import org.praisenter.media.MediaFormat;
import org.praisenter.media.MediaLibrary;
import org.praisenter.tools.ffmpeg.FFprobeMediaMetadata;
import org.praisenter.utility.CommandLine;
import org.praisenter.utility.ImageManipulator;
import org.praisenter.utility.RuntimeProperties;
import org.praisenter.utility.StringManipulator;

import com.fasterxml.jackson.databind.JsonNode;

// FEATURE (M) To reduce the size of Praisenter, maybe we can compress the binaries and decompress when we start

/**
 * Helper class to perform operations supported by tools.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Tools {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The FFmpeg folder */
	private static final String FFMPEG_DIR = "ffmpeg";
	
	// data
	
	/** The root path to the tools */
	public final Path path;
	
	/** The locks for the tools */
	public final LockMap<String> lockMap;
	
	/** The path to the ffmpeg binary */
	public final Path ffmpeg;
	
	/** The path to the ffprobe binary */
	public final Path ffprobe;
	
	/**
	 * Creates a new tool library at the specified path.
	 * @param path the path
	 * @return {@link Tools}
	 */
	public static final Tools open(Path path) {
		Tools tools = new Tools(path);
		tools.initialize();
		return tools;
	}
	
	/**
	 * Constructor.
	 * @param path the root path
	 */
	private Tools(Path path) {
		this.path = path;
		
		this.lockMap = new LockMap<String>();
		
		// choose the correct FFmpeg binary
		if (RuntimeProperties.IS_WINDOWS_OS && (RuntimeProperties.IS_32 || RuntimeProperties.IS_64)) {
			this.ffmpeg = path.resolve(FFMPEG_DIR).resolve("ffmpeg.exe");
			this.ffprobe = path.resolve(FFMPEG_DIR).resolve("ffprobe.exe");
		} else if (RuntimeProperties.IS_MAC_OS && RuntimeProperties.IS_64) {
			this.ffmpeg = path.resolve(FFMPEG_DIR).resolve("ffmpeg");
			this.ffprobe = path.resolve(FFMPEG_DIR).resolve("ffprobe");
		} else if (RuntimeProperties.IS_LINUX_OS && (RuntimeProperties.IS_32 || RuntimeProperties.IS_64)) {
			this.ffmpeg = path.resolve(FFMPEG_DIR).resolve("ffmpeg");
			this.ffprobe = path.resolve(FFMPEG_DIR).resolve("ffprobe");
		} else {
			this.ffmpeg = null;
			this.ffprobe = null;
		}
	}
	
	/**
	 * Make sure the tools exist on the file system.
	 */
	private void initialize() {
		// extract the appropriate binary for the system
		if (this.ffmpeg != null && this.ffprobe != null) {
			try {
				Path ffmpegFolder = this.ffmpeg.getParent();
				Files.createDirectories(ffmpegFolder);
				if (RuntimeProperties.IS_WINDOWS_OS && RuntimeProperties.IS_32) {
					if (!Files.exists(this.ffmpeg)) Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/windows32/ffmpeg.exe"), this.ffmpeg, StandardCopyOption.REPLACE_EXISTING);
					if (!Files.exists(this.ffprobe)) Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/windows32/ffprobe.exe"), this.ffprobe, StandardCopyOption.REPLACE_EXISTING);
				} else if (RuntimeProperties.IS_WINDOWS_OS && RuntimeProperties.IS_64) {
					if (!Files.exists(this.ffmpeg)) Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/windows64/ffmpeg.exe"), this.ffmpeg, StandardCopyOption.REPLACE_EXISTING);
					if (!Files.exists(this.ffprobe))Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/windows64/ffprobe.exe"), this.ffprobe, StandardCopyOption.REPLACE_EXISTING);
				} else if (RuntimeProperties.IS_MAC_OS) {
					if (!Files.exists(this.ffmpeg)) Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/mac64/ffmpeg"), this.ffmpeg, StandardCopyOption.REPLACE_EXISTING);
					if (!Files.exists(this.ffprobe))Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/mac64/ffprobe"), this.ffprobe, StandardCopyOption.REPLACE_EXISTING);
				} else if (RuntimeProperties.IS_LINUX_OS && RuntimeProperties.IS_32) {
					if (!Files.exists(this.ffmpeg)) Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/linux32/ffmpeg"), this.ffmpeg, StandardCopyOption.REPLACE_EXISTING);
					if (!Files.exists(this.ffprobe))Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/linux32/ffprobe"), this.ffprobe, StandardCopyOption.REPLACE_EXISTING);
				} else if (RuntimeProperties.IS_LINUX_OS && RuntimeProperties.IS_64) {
					if (!Files.exists(this.ffmpeg)) Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/linux64/ffmpeg"), this.ffmpeg, StandardCopyOption.REPLACE_EXISTING);
					if (!Files.exists(this.ffprobe))Files.copy(MediaLibrary.class.getResourceAsStream("/org/praisenter/tools/ffmpeg/linux64/ffprobe"), this.ffprobe, StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (IOException ex) {
				LOGGER.error("Failed to copy FFmpeg or FFprobe executables to the file system.", ex);
			}
		} else {
			LOGGER.error("Cannot determine FFmpeg binary based on operating system {} and architecture {}.", RuntimeProperties.OPERATING_SYSTEM, RuntimeProperties.ARCHITECTURE);
		}
	}
	
	/**
	 * Returns a lock for the ffmpeg binary.
	 * @return Object
	 */
	private Object getFFmpegLock() {
		return this.lockMap.get("ffmpeg");
	}
	
	/**
	 * Returns a lock for the ffprobe binary.
	 * @return Object
	 */
	private Object getFFprobeLock() {
		return this.lockMap.get("ffprobe");
	}
	
	/**
	 * Parses the given command and replaces parameters with their values.
	 * @param template the command string
	 * @param parameters the parameters
	 * @return List&lt;String&gt;
	 */
	private List<String> parseCommand(String template, Map<String, String> parameters) {
		// replace any tokens
		String[] tokens = template.split("\\s+");
		
		List<String> command = new ArrayList<String>();
		for (String token : tokens) {
			if (!StringManipulator.isNullOrEmpty(token)) {
				String match = token.toLowerCase().trim();
				String replacement = parameters.get(match);
				if (!StringManipulator.isNullOrEmpty(replacement)) {
					command.add(replacement);
				} else {
					command.add(token);
				}
			}
		}
		
		return command;
	}
	
	/**
	 * Uses the FFmpeg tool to transcode the given source file to the given target file using the given command template.
	 * @param template the command template
	 * @param source the source file
	 * @param target the target file
	 * @throws IOException if an IO error occurs
	 * @throws InterruptedException if the process is interrupted while waiting for it to complete
	 * @throws ToolExecutionException if the tools fails to perform its action
	 */
	public void ffmpegTranscode(String template, Path source, Path target) throws IOException, InterruptedException, ToolExecutionException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("{ffmpeg}", this.ffmpeg.toAbsolutePath().toString());
		parameters.put("{source}", source.toAbsolutePath().toString());
		parameters.put("{target}", target.toAbsolutePath().toString());
		
		synchronized (this.getFFmpegLock()) {
			CommandLine.execute(this.parseCommand(template, parameters));
		}
	}
	
	/**
	 * Uses the FFmpeg tool to extract frames from the given video file and returns the best one based on a Luminance metric.
	 * @param template the command template
	 * @param media the video file
	 * @return BufferedImage
	 * @throws IOException if an IO error occurs
	 * @throws InterruptedException if the process is interrupted while waiting for it to complete
	 * @throws ToolExecutionException if the tools fails to perform its action
	 */
	public BufferedImage ffmpegExtractFrame(String template, Path media) throws IOException, InterruptedException, ToolExecutionException {
		// create a unique identifer for naming;
		String id = UUID.randomUUID().toString().replaceAll("-", "");
		Path tempArea = this.path.resolve(id);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("{ffmpeg}", this.ffmpeg.toAbsolutePath().toString());
		parameters.put("{media}", media.toAbsolutePath().toString());
		parameters.put("{frame}", tempArea.resolve("frame%02d.jpg").toAbsolutePath().toString());
		
		synchronized (this.getFFmpegLock()) {
			try {
				// create the temp folder for easy clean up
				Files.createDirectories(tempArea);
				
				// run the command
				CommandLine.execute(this.parseCommand(template, parameters));
				
				// scan the frames and pick the best
				BufferedImage best = null;
				double score = 0;
				try (DirectoryStream<Path> dir = Files.newDirectoryStream(tempArea)) {
					for (Path path : dir) {
						if (Files.isRegularFile(path) && path.getFileName().toString().startsWith("frame")) {
							try {
								BufferedImage image = ImageIO.read(path.toFile());
								double logAvgLuminance = ImageManipulator.getLogAverageLuminance(image);
								
								// how far from the middle is the score?
								double distance = Math.abs(logAvgLuminance - 0.5);
								if (distance < score || best == null) {
									LOGGER.debug("Best Log Average Luminance {} from {}.", logAvgLuminance, path.toAbsolutePath().toString());
									best = image;
									score = distance;
								}
							} catch (IOException ex) {
								LOGGER.error("Failed to read frame from media '" + media.toAbsolutePath().toString() + "'.", ex);
							}
						}
					}
				}
				
				return best;
			} finally {
				Files.walk(tempArea)
			     .sorted((a, b) -> b.compareTo(a)) // reverse; files before dirs
			     .forEach(p -> {
			        try { 
			        	Files.delete(p); 
			        } catch(IOException e) {
			        	LOGGER.warn("Failed to delete the temp file '" + p.toAbsolutePath().toString() + "'.", e);
			        }
			     });
			}
		}
	}
	
	/**
	 * Uses the FFprobe tool to read the metadata of the given media.
	 * @param media the media file
	 * @return {@link FFprobeMediaMetadata}
	 * @throws IOException if an IO error occurs
	 * @throws InterruptedException if the process is interrupted while waiting for it to complete
	 * @throws ToolExecutionException if the tools fails to perform its action
	 */
	public FFprobeMediaMetadata ffprobeExtractMetadata(Path media) throws IOException, InterruptedException, ToolExecutionException {
		// create a unique identifer for naming;
		String id = UUID.randomUUID().toString().replaceAll("-", "");
		Path tempArea = this.path.resolve(id);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("{ffprobe}", this.ffprobe.toAbsolutePath().toString());
		parameters.put("{media}", media.toAbsolutePath().toString());
		parameters.put("{output}", tempArea.resolve("metadata.json").toAbsolutePath().toString());
		
		synchronized (this.getFFprobeLock()) {
			// read the output json to get the metadata
			try {
				// create the temp folder for easy clean up
				Files.createDirectories(tempArea);
				
				// run the command
				String json = CommandLine.execute(this.parseCommand("{ffprobe} -v quiet -print_format json -show_format -show_streams {media}", parameters));
				
				JsonNode root = JsonIO.read(json, JsonNode.class);
				
				JsonNode format = root.get("format");
				JsonNode streams = root.get("streams");
				JsonNode techName = format.get("format_name");
				JsonNode longName = format.get("format_long_name");
				JsonNode duration = format.get("duration");
				JsonNode width = null;
				JsonNode height = null;
				boolean hasVideo = false;
				boolean hasAudio = false;
				
				String name = techName != null ? techName.asText() : null;
				String desc = longName != null ? longName.asText() : null;
				
				List<MediaCodec> codecs = new ArrayList<MediaCodec>();
				Iterator<JsonNode> it = streams.elements();
				while (it.hasNext()) {
					JsonNode stream = it.next();
					
					JsonNode codeName = stream.get("codec_name");
					JsonNode codeDesc = stream.get("codec_long_name");
					JsonNode type = stream.get("codec_type");
					
					String cn = codeName != null ? codeName.asText() : null;
					String cd = codeDesc != null ? codeDesc.asText() : null;
					
					CodecType codecType = null;
					if ("video".equals(type.asText())) {
						codecType = CodecType.VIDEO;
						
						hasVideo = true;
						width = stream.get("width");
						height = stream.get("height");
					} else if ("audio".equals(type.asText())) {
						codecType = CodecType.AUDIO;
						hasAudio = true;
					}
					
					if (codecType != null) {
						MediaCodec codec = new MediaCodec(codecType, cn, cd);
						codecs.add(codec);
					}
				}
				
				MediaFormat mf = new MediaFormat(name, desc, codecs);
				
				return new FFprobeMediaMetadata(
						mf,
						width != null ? width.asInt() : 0,
						height != null ? height.asInt() : 0,
						duration != null ? duration.asLong() : 0,
						hasVideo,
						hasAudio);
			} finally {
				Files.walk(tempArea)
				     .sorted((a, b) -> b.compareTo(a)) // reverse; files before dirs
				     .forEach(p -> {
				        try { 
				        	Files.delete(p); 
				        } catch(IOException e) {
				        	LOGGER.warn("Failed to delete the temp file '" + p.toAbsolutePath().toString() + "'.", e);
				        }
				     });
			}
		}
	}
}

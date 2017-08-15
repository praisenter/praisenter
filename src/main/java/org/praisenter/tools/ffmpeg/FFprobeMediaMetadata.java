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
package org.praisenter.tools.ffmpeg;

import org.praisenter.media.MediaFormat;

/**
 * Represents media metadata extracted from the FFprobe tool.
 * @author William Bittle
 * @version 3.0.0
 */
public final class FFprobeMediaMetadata {
	/** The file's format */
	private final MediaFormat format;
	
	/** The file's width in pixels (if applicable) */
	private final int width;
	
	/** The file's height in pixels (if applicable) */
	private final int height;
	
	/** The file's length in seconds (if applicable) */
	private final long length;
	
	/** True if the media contains video */
	private final boolean video;
	
	/** True if the media contains audio */
	private final boolean audio;

	/**
	 * Full constructor.
	 * @param format the format
	 * @param width the width; can be zero
	 * @param height the height; can be zero
	 * @param length the length; can be zero
	 * @param video true if video is present
	 * @param audio true if audio is present
	 */
	public FFprobeMediaMetadata(MediaFormat format, int width, int height, long length, boolean video, boolean audio) {
		this.format = format;
		this.width = width;
		this.height = height;
		this.length = length;
		this.video = video;
		this.audio = audio;
	}

	/**
	 * Returns the format.
	 * @return {@link MediaFormat}
	 */
	public MediaFormat getFormat() {
		return this.format;
	}

	/**
	 * Returns the width.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the length.
	 * @return long
	 */
	public long getLength() {
		return this.length;
	}

	/**
	 * Returns true if video is present.
	 * @return boolean
	 */
	public boolean hasVideo() {
		return this.video;
	}
	
	/**
	 * Returns true if audio is present.
	 * @return boolean
	 */
	public boolean hasAudio() {
		return this.audio;
	}
}

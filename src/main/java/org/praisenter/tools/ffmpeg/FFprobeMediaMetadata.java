package org.praisenter.tools.ffmpeg;

import org.praisenter.media.MediaFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	public FFprobeMediaMetadata(MediaFormat format, int width, int height, long length, boolean video, boolean audio) {
		super();
		this.format = format;
		this.width = width;
		this.height = height;
		this.length = length;
		this.video = video;
		this.audio = audio;
	}

	public MediaFormat getFormat() {
		return this.format;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public long getLength() {
		return this.length;
	}

	public boolean hasVideo() {
		return this.video;
	}
	
	public boolean hasAudio() {
		return this.audio;
	}
}

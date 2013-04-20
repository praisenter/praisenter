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
package org.praisenter.media;

/**
 * Represents an object containing the configuration for a {@link MediaPlayer}.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class MediaPlayerConfiguration {
	/** The media output width */
	protected int width;
	
	/** The media output height */
	protected int height;
	
	/** True if the media should be converted at read time */
	protected boolean readTimeVideoConversionEnabled;
	
	/** True if the media should loop */
	protected boolean loopEnabled;
	
	/** True if audio is muted */
	protected boolean audioMuted;
	
	/** The desired volume as a percentage of the media's volume */
	protected double volume;
	
	/**
	 * Default constructor.
	 */
	public MediaPlayerConfiguration() {
		this.width = 0;
		this.height = 0;
		this.readTimeVideoConversionEnabled = false;
		this.loopEnabled = false;
		this.audioMuted = false;
		this.volume = 100.0;
	}
	
	/**
	 * Copy constructor.
	 * @param configuration the configuration to copy
	 */
	public MediaPlayerConfiguration(MediaPlayerConfiguration configuration) {
		this.width = configuration.width;
		this.height = configuration.height;
		this.readTimeVideoConversionEnabled = configuration.readTimeVideoConversionEnabled;
		this.loopEnabled = configuration.loopEnabled;
		this.audioMuted = configuration.audioMuted;
		this.volume = configuration.volume;
	}
	
	/**
	 * Returns true if the media should be looped.
	 * @return boolean
	 */
	public boolean isLoopEnabled() {
		return this.loopEnabled;
	}

	/**
	 * Sets whether the media loops or not.
	 * @param loopEnabled true if the media should loop
	 */
	public void setLoopEnabled(boolean loopEnabled) {
		this.loopEnabled = loopEnabled;
	}

	/**
	 * Returns true if the audio of the media is muted.
	 * @return boolean
	 */
	public boolean isAudioMuted() {
		return this.audioMuted;
	}

	/**
	 * Sets whether the media's audio is muted or not.
	 * @param audioMuted true if the audio should be muted
	 */
	public void setAudioMuted(boolean audioMuted) {
		this.audioMuted = audioMuted;
	}
	
	/**
	 * Returns the volume percent of the media's volume.
	 * @return double
	 */
	public double getVolume() {
		return this.volume;
	}
	
	/**
	 * Sets the volume percent between 0 and 100.
	 * @param volume the volume percent
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}

	/**
	 * Returns the width of the media.
	 * @return int
	 * @since 2.0.1
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Sets the width of the media.
	 * @param width the width in pixels
	 * @since 2.0.1
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns the height of the media.
	 * @return int
	 * @since 2.0.1
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Sets the height of the media
	 * @param height the height in pixels
	 * @since 2.0.1
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * Returns true if read-time video conversion is enabled.
	 * @return boolean
	 * @since 2.0.1
	 */
	public boolean isReadTimeVideoConversionEnabled() {
		return this.readTimeVideoConversionEnabled;
	}
	
	/**
	 * Toggles read-time conversion of video frames.
	 * @param flag true if read-time conversion should be enabled
	 * @since 2.0.1
	 */
	public void setReadTimeVideoConversionEnabled(boolean flag) {
		this.readTimeVideoConversionEnabled = flag;
	}
}

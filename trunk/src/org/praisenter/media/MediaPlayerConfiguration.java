package org.praisenter.media;

/**
 * Represents an object containing the configuration for a {@link MediaPlayer}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MediaPlayerConfiguration {
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
		this.loopEnabled = false;
		this.audioMuted = false;
		this.volume = 100.0;
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
}

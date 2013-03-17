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
 * Represents a player that can play timed media (like video and audio).
 * @param <E> the {@link PlayableMedia} type
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
// TODO [MEDIUM] MEDIA add an option to fade out audio during the transition (perhaps fade in as well)
// TODO [LOW] MEDIA maybe add a way to set the start time and end time?
public interface MediaPlayer<E extends PlayableMedia> {
	/**
	 * The enumeration of states the media player can be in.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	public static enum State {
		/** The stopped state */
		STOPPED,
		
		/** The playing state */
		PLAYING,
		
		/** The paused state */
		PAUSED,
		
		/** This state is useful for clean up only */
		ENDED
	}
	
	// properties
	
	/**
	 * Returns true if this player is in the playing state.
	 * @return boolean
	 */
	public abstract boolean isPlaying();
	
	/**
	 * Returns true if this player is in the paused state.
	 * @return boolean
	 */
	public abstract boolean isPaused();
	
	/**
	 * Returns true if this player is in the stopped state.
	 * @return boolean
	 */
	public abstract boolean isStopped();
	
	/**
	 * Sets the media to be played.
	 * <p>
	 * If media is already being played, it will be stopped and the
	 * given media will played.
	 * <p>
	 * Returns true if the media was opened and the player is ready
	 * for playback.
	 * @param media the media to play
	 * @return boolean
	 */
	public abstract boolean setMedia(E media);
	
	/**
	 * Returns the media the player is playing.
	 * @return E
	 */
	public abstract E getMedia();
	
	/**
	 * Returns the media player configuration.
	 * <p>
	 * This returns a copy of the current configuration. You can modify
	 * this copy and then call the {@link #setConfiguration(MediaPlayerConfiguration)}
	 * method to set the new configuration options.
	 * @return {@link MediaPlayerConfiguration}
	 */
	public abstract MediaPlayerConfiguration getConfiguration();
	
	/**
	 * Sets the media player configuration.
	 * <p>
	 * This controls looping, muted audio, video scaling, etc.
	 * @param configuration the configuration
	 */
	public abstract void setConfiguration(MediaPlayerConfiguration configuration);
	
	// controls
	
	/**
	 * Begins playback of the media.
	 */
	public abstract void play();
	
	/**
	 * Stops playback of the media.
	 * <p>
	 * This will reset the position in the media to the beginning.
	 */
	public abstract void stop();
	
	/**
	 * Pauses playback of the media.
	 */
	public abstract void pause();
	
	/**
	 * Resumes playback of the media.
	 */
	public abstract void resume();
	
	/**
	 * Seeks the media to the given position.
	 * @param position the position
	 */
	public abstract void seek(long position);
	
	/**
	 * Returns the current position in the media.
	 * @return long
	 */
	public abstract long getPosition();
	
	/**
	 * Ends any playback and releases any resources for
	 * this player.
	 * <p>
	 * After this method is called, media can no longer
	 * be played from this player.
	 */
	public abstract void release();
	
	// events
	
	/**
	 * Adds the given listener to the list of listeners.
	 * @param listener the listener to add
	 */
	public abstract void addMediaPlayerListener(MediaPlayerListener listener);
	
	/**
	 * Removes the given listener from the list of listeners.
	 * @param listener the listener to remove
	 * @return boolean true if the listener was removed
	 */
	public abstract boolean removeMediaPlayerListener(MediaPlayerListener listener);
}

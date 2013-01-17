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
package org.praisenter.media.player;

import java.util.List;

import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerConfiguration;
import org.praisenter.media.MediaPlayerListener;

/**
 * Abstract audio media player.
 * <p>
 * Different audio media types may require different players. For example, sampled sound
 * is played directly through a line, but midi sound must be synthesized first.
 * @param <E> the {@link AbstractAudioMedia} type
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class AbstractAudioPlayer<E extends AbstractAudioMedia> implements MediaPlayer<E> {
	/** The max volume for the audio = {@value #MAX_VOLUME} */
	public static final double MAX_VOLUME = 100.0;
	
	/** The min volume for the audio = {@value #MIN_VOLUME} */
	public static final double MIN_VOLUME = 0.0;
	
	/** The current state */
	protected State state = State.STOPPED;

	/** The audio media to play */
	protected E media;
	
	/** The media player configuration */
	protected MediaPlayerConfiguration configuration;

	/** The list of media player listeners */
	protected List<MediaPlayerListener> listeners;
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#setMedia(org.praisenter.media.PlayableMedia)
	 */
	@Override
	public boolean setMedia(E media) {
		this.media = media;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#getMedia()
	 */
	@Override
	public E getMedia() {
		return this.media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isPaused()
	 */
	public boolean isPaused() {
		return this.state == State.PAUSED;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isPlaying()
	 */
	public boolean isPlaying() {
		return this.state == State.PLAYING;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isStopped()
	 */
	@Override
	public boolean isStopped() {
		return this.state == State.STOPPED;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#getConfiguration()
	 */
	@Override
	public MediaPlayerConfiguration getConfiguration() {
		return new MediaPlayerConfiguration(this.configuration);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#setConfiguration(org.praisenter.media.MediaPlayerConfiguration)
	 */
	@Override
	public void setConfiguration(MediaPlayerConfiguration configuration) {
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#addMediaPlayerListener(org.praisenter.media.MediaPlayerListener)
	 */
	@Override
	public void addMediaPlayerListener(MediaPlayerListener listener) {
		this.listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#removeMediaPlayerListener(org.praisenter.media.MediaPlayerListener)
	 */
	@Override
	public boolean removeMediaPlayerListener(MediaPlayerListener listener) {
		return this.listeners.remove(listener);
	}
}

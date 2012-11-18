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
 * @version 1.0.0
 * @since 1.0.0
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
		return this.configuration;
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

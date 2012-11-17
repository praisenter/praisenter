package org.praisenter.media.player;

import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerConfiguration;
import org.praisenter.media.MediaPlayerListener;


public abstract class AbstractAudioPlayer<E extends AbstractAudioMedia> implements MediaPlayer<E> {
	/** Value to loop the audio continuously = {@value #LOOP_INFINITE} */
	public static final int LOOP_INFINITE = Integer.MIN_VALUE;

	/** The max volume for the audio = {@value #MAX_VOLUME} */
	public static final double MAX_VOLUME = 100.0;
	
	/** The min volume for the audio = {@value #MIN_VOLUME} */
	public static final double MIN_VOLUME = 0.0;
	
	/** The current state */
	protected State state = State.STOPPED;

	/** The audio media to play */
	protected E media;
	
	/** The current volume */
	protected double volume = 50.0;

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
	
//	
//	/**
//	 * Closes the audio if the audio is not already in the in the {@link State#CLOSED} state.
//	 * <p>
//	 * This method will release resources to allow another audio object to play.  There are
//	 * a finite number of resources for playing audio which will differ depending on the
//	 * system.
//	 * @see #open()
//	 */
//	public abstract void close();
//
//	/**
//	 * Releases any audio system resources.
//	 * <p>
//	 * This method is normally called from the close method and should not be called
//	 * directly.  This method should release the resources that are not used to re-open
//	 * the audio and should release the resources necessary for audio play back to allow
//	 * other audio to play.
//	 */
//	protected abstract void release();
//	
//	/**
//	 * Resets the audio's state.
//	 * <p>
//	 * This should never be called directly.
//	 */
//	protected abstract void reset();

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
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#setConfiguration(org.praisenter.media.MediaPlayerConfiguration)
	 */
	@Override
	public void setConfiguration(MediaPlayerConfiguration configuration) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#addMediaPlayerListener(org.praisenter.media.MediaPlayerListener)
	 */
	@Override
	public void addMediaPlayerListener(MediaPlayerListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#removeMediaPlayerListener(org.praisenter.media.MediaPlayerListener)
	 */
	@Override
	public boolean removeMediaPlayerListener(MediaPlayerListener listener) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Sets the volume.
	 * @param volume in the range [0, 100]
	 */
	public synchronized void setVolume(double volume) {
		this.volume = volume;
	}
	
	/**
	 * Returns the current volume in the range [0, 100].
	 * @return double
	 */
	public double getVolume() {
		return this.volume;
	}
}

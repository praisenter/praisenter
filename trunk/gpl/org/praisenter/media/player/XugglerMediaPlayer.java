package org.praisenter.media.player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerListener;
import org.praisenter.media.PlayableMedia;
import org.praisenter.media.XugglerPlayableMedia;
import org.praisenter.media.XugglerVideoMedia;
import org.praisenter.media.MediaPlayer.State;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

public class XugglerMediaPlayer implements MediaPlayer<XugglerPlayableMedia> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerMediaPlayer.class);
	
	/** The state of the media player */
	protected State state; 
	
	/** The video/audio synchronization clock */
	protected XugglerMediaClock clock;
	
	/** True if the media should loop */
	protected boolean looped;
	
	/** True if the media is muted */
	protected boolean muted;

	/** The list of media player listeners */
	protected List<MediaPlayerListener> listeners;
	
	// the media
	
	protected XugglerPlayableMedia media;
	
	// threads
	
	protected XugglerMediaReaderThread mediaReaderThread;
	protected XugglerMediaPlayerThread mediaPlayerThread;
	protected XugglerAudioPlayerThread audioPlayerThread;
	
	// TODO some suggest synching based on the audio output timestamps
	public XugglerMediaPlayer() {
		this.state = State.STOPPED;
		this.clock = new XugglerMediaClock();
		this.listeners = new ArrayList<>();
		this.looped = true;
		
		// start the threads
		this.mediaReaderThread = new DefaultMediaReaderThread();
		this.mediaPlayerThread = new DefaultMediaPlayerThread();
		this.audioPlayerThread = new XugglerAudioPlayerThread();
		
		this.audioPlayerThread.start();
		this.mediaPlayerThread.start();
		this.mediaReaderThread.start();
	}
	
	/**
	 * Initializes the playback of the given media.
	 * @param media the media
	 */
	private void initializeMedia(XugglerPlayableMedia media) {
		// get the Xuggler objects from the media
		IContainer container = media.getContainer();
		IStreamCoder audioCoder = media.getAudioCoder();
		IStreamCoder videoCoder = null;
		if (media instanceof XugglerVideoMedia) {
			XugglerVideoMedia vMedia = (XugglerVideoMedia)media;
			videoCoder = vMedia.getVideoCoder();
		}
		
		// initialize the playback threads
		this.audioPlayerThread.initialize(audioCoder);
		this.mediaPlayerThread.initialize(videoCoder != null, audioCoder != null);
		this.mediaReaderThread.initialize(container, videoCoder, audioCoder);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isTypeSupported(java.lang.Class)
	 */
	@Override
	public <T extends PlayableMedia> boolean isTypeSupported(Class<T> clazz) {
		if (XugglerPlayableMedia.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#getMedia()
	 */
	@Override
	public XugglerPlayableMedia getMedia() {
		return this.media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#setMedia(org.praisenter.media.PlayableMedia)
	 */
	@Override
	public void setMedia(XugglerPlayableMedia media) {
		// stop any playback
		this.stop();
		// assign the media
		this.initializeMedia(media);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isLooped()
	 */
	@Override
	public boolean isLooped() {
		return this.looped;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#setLooped(boolean)
	 */
	@Override
	public void setLooped(boolean looped) {
		this.looped = looped;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isMuted()
	 */
	@Override
	public boolean isMuted() {
		return this.muted;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#setMuted(boolean)
	 */
	@Override
	public void setMuted(boolean muted) {
		this.muted = muted;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#play()
	 */
	@Override
	public void play() {
		this.state = State.PLAYING;
		this.setPaused(false);
		System.out.println("--Playing");
	}
	
	private void setPaused(boolean flag) {
		this.audioPlayerThread.setPaused(flag);
		this.mediaPlayerThread.setPaused(flag);
		this.mediaReaderThread.setPaused(flag);
	}
	
	/**
	 * Stops the current playback and seeks to the beginning and
	 * beings playback.
	 */
	private void loop() {
		System.out.println("--Looping");
		stop();
		play();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#pause()
	 */
	@Override
	public void pause() {
		this.state = State.PAUSED;
		this.setPaused(true);
		System.out.println("--Paused");
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#resume()
	 */
	@Override
	public void resume() {
		this.state = State.PLAYING;
		this.setPaused(false);
		System.out.println("--Resumed");
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#stop()
	 */
	@Override
	public void stop() {
		this.state = State.STOPPED;
		// only pause the reading thread
		this.mediaReaderThread.setPaused(true);
		
		System.out.println("--Seeking");
		this.mediaReaderThread.loop();
		
		this.mediaPlayerThread.drainBuffers();
		this.audioPlayerThread.drain();
		System.out.println("--Stopped");
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#seek(long)
	 */
	@Override
	public void seek(long position) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isPaused()
	 */
	@Override
	public boolean isPaused() {
		return this.state == State.PAUSED;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isPlaying()
	 */
	@Override
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

	/**
	 * Notifies any listeners of the video image event.
	 * @param image the video image
	 */
	private void notifyListeners(BufferedImage image) {
		for (MediaPlayerListener listener : this.listeners) {
			listener.onVideoPicture(image);
		}
	}

	// wire up threads
	
	private class DefaultMediaReaderThread extends XugglerMediaReaderThread {
		@Override
		protected void onStreamEnd() {
			if (looped) {
				XugglerMediaPlayer.this.loop();
			}
		}
		
		@Override
		protected void queueAudioImage(XugglerTimedData<byte[]> samples) {
			mediaPlayerThread.queueAudioSamples(samples);
		}
		
		@Override
		protected void queueVideoImage(XugglerTimedData<BufferedImage> image) {
			mediaPlayerThread.queueVideoImage(image);
		}
	}
	
	private class DefaultMediaPlayerThread extends XugglerMediaPlayerThread {
		@Override
		protected void playAudio(byte[] samples) {
			audioPlayerThread.queue(samples);
		}
		
		@Override
		protected void playVideo(BufferedImage image) {
			notifyListeners(image);
		}
	}
}

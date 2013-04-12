/*
 * Praisenter: A free open source church presentation software.
 * Copyright (C) 2012-2013  William Bittle  http://www.praisenter.org/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.praisenter.media.player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerConfiguration;
import org.praisenter.media.MediaPlayerListener;
import org.praisenter.media.VideoMediaPlayerListener;
import org.praisenter.media.XugglerPlayableMedia;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Class used to play {@link XugglerPlayableMedia}.
 * <p>
 * This class uses a number of threads to read and playback the given media.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class XugglerMediaPlayer implements MediaPlayer<XugglerPlayableMedia> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerMediaPlayer.class);
	
	/** The state of the media player */
	protected State state; 
	
	/** The player configuration */
	protected MediaPlayerConfiguration configuration;

	/** The list of media player listeners */
	protected List<MediaPlayerListener> listeners;
	
	// the media
	
	/** The media to play */
	protected XugglerPlayableMedia media;
	
	// threads
	
	/** The media reader thread */
	protected XugglerMediaReaderThread mediaReaderThread;
	
	/** The media player thread */
	protected XugglerMediaPlayerThread mediaPlayerThread;
	
	/** The audio player thread */
	protected XugglerAudioPlayerThread audioPlayerThread;
	
	/**
	 * Default constructor.
	 */
	public XugglerMediaPlayer() {
		this.state = State.STOPPED;
		this.listeners = new ArrayList<>();
		this.configuration = new MediaPlayerConfiguration();
		
		// we override the thread classes below to provide the wiring up
		// required for them to communicate
		
		// we also override the onStopped methods so that they call the next
		// thread to be ended to force them to end in sequence
		
		// create the threads
		this.mediaReaderThread = new XugglerMediaReaderThread() {
			@Override
			protected void queueVideoImage(XugglerVideoData image) {
				mediaPlayerThread.queueVideoImage(image);
			}
			@Override
			protected void queueAudioImage(XugglerAudioData samples) {
				mediaPlayerThread.queueAudioSamples(samples);
			}
			@Override
			protected void onMediaEnd() {
				XugglerMediaPlayer.this.loop();
			}
			@Override
			protected void onThreadStopped() {
				super.onThreadStopped();
				LOGGER.debug("MediaReaderThread Ended");
				mediaPlayerThread.end();
			}
		};
		this.mediaPlayerThread = new XugglerMediaPlayerThread() {
			@Override
			protected void playVideo(BufferedImage image) {
				notifyListeners(image);
			}
			@Override
			protected void playAudio(byte[] samples) {
				audioPlayerThread.queue(samples);
			}
			@Override
			protected void onThreadStopped() {
				super.onThreadStopped();
				LOGGER.debug("MediaPlayerThread Ended");
				audioPlayerThread.end();
			}
		};
		this.audioPlayerThread = new XugglerAudioPlayerThread() {
			protected void onThreadStopped() {
				super.onThreadStopped();
				LOGGER.debug("AudioPlayerThread Ended");
				onRelease();
			}
		};
		
		// start the threads
		this.audioPlayerThread.start();
		this.mediaPlayerThread.start();
		this.mediaReaderThread.start();
	}
	
	/**
	 * Initializes the playback of the given media.
	 * <p>
	 * Returns true if the media was opened successfully and is ready for playback.
	 * @param media the media
	 * @return boolean
	 */
	private boolean initializeMedia(XugglerPlayableMedia media) {
		// assign the media
		this.media = media;
		
		// open the media
		
		// create the video container object
		IContainer container = IContainer.make();

		// open the container format
		String filePath = media.getFile().getFullPath();
		if (container.open(filePath, IContainer.Type.READ, null) < 0) {
			LOGGER.error("Could not open file [" + media.getFile().getRelativePath() + "].  Unsupported container format.");
			this.media = null;
			if (container != null) {
				container.close();
			}
			return false;
		}
		LOGGER.debug("Media file opened width container format: " + container.getContainerFormat().getInputFormatLongName());

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		LOGGER.debug("Stream count: " + container.getContainerFormat().getInputFormatLongName());

		// loop over the streams to find the first video stream
		IStreamCoder videoCoder = null;
		IStreamCoder audioCoder = null;
		for (int i = 0; i < numStreams; i++) {
			IStream stream = container.getStream(i);
			// get the coder for the stream
			IStreamCoder coder = stream.getStreamCoder();
			// see if the coder is a video coder
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				// if so, break from the loop
				videoCoder = coder;
			}
			if (stream.getStreamCoder().getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                audioCoder = coder;
            }
		}
		
		if (videoCoder == null && audioCoder == null) {
			LOGGER.error("No audio or video stream found.");
			this.media = null;
			if (container != null) {
				container.close();
			}
			return false;
		}
		
		// see if we have a video stream
		if (videoCoder != null) {
			// open the coder to read the video data
			String codecName = "Unknown";
			ICodec codec = videoCoder.getCodec();
			if (codec != null) {
				codecName = codec.getLongName();
			}
			if (videoCoder.open(null, null) < 0) {
				LOGGER.error("Could not open video decoder for: " + codecName);
				this.media = null;
				if (container != null) {
					container.close();
				}
				return false;
			}
			LOGGER.debug("Video coder opened with format: " + codecName);
		} else {
			LOGGER.debug("No video stream in container.");
		}
		
		// see if we have an audio stream
		if (audioCoder != null) {
			String codecName = "Unknown";
			ICodec codec = audioCoder.getCodec();
			if (codec != null) {
				codecName = codec.getLongName();
			}
			if (audioCoder.open(null, null) < 0) {
				LOGGER.error("Could not open audio decoder for: " + codecName);
				this.media = null;
				if (container != null) {
					container.close();
				}
				return false;
			}
			LOGGER.debug("Audio coder opened with format: " + codecName);
		} else {
			LOGGER.debug("No audio stream in container.");
		}
		
		// initialize the playback threads
		boolean downmix = this.audioPlayerThread.initialize(audioCoder);
		if (downmix) {
			LOGGER.info("Downmixing required.");
		}
		this.mediaPlayerThread.initialize(videoCoder != null, audioCoder != null);
		this.mediaReaderThread.initialize(container, videoCoder, audioCoder, downmix);
		
		return true;
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
	public boolean setMedia(XugglerPlayableMedia media) {
		if (media == null) {
			return false;
		}
		// stop any playback
		this.stop();
		// assign the media
		return this.initializeMedia(media);
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
		this.audioPlayerThread.setVolume(configuration.getVolume());
		this.audioPlayerThread.setMuted(configuration.isAudioMuted());
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#play()
	 */
	@Override
	public void play() {
		if (this.media != null) {
			this.state = State.PLAYING;
			this.setPaused(false);
			LOGGER.debug("Playing");
		}
	}
	
	/**
	 * Pauses or unpauses the media threads.
	 * @param flag true if the threads should be paused
	 */
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
		// stop, but drain whats left
		boolean reset = stop(true);
		// check if we should loop
		if (this.configuration.isLoopEnabled()) {
			// make sure we were able to reset
			// the media to its start position
			if (reset) {
				LOGGER.debug("Looping");
				play();
				return;
			}
			LOGGER.warn("Loop failed, stopping media playback.");
		}
		// if we don't loop (or it failed) then we need to pause all the threads
		this.audioPlayerThread.setPaused(true);
		this.mediaPlayerThread.setPaused(true);
		this.mediaReaderThread.setPaused(true);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#pause()
	 */
	@Override
	public void pause() {
		if (this.state == State.PLAYING) {
			this.state = State.PAUSED;
			this.setPaused(true);
			LOGGER.debug("Paused");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#resume()
	 */
	@Override
	public void resume() {
		if (this.state == State.PAUSED) {
			this.state = State.PLAYING;
			this.setPaused(false);
			LOGGER.debug("Resumed");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#stop()
	 */
	@Override
	public void stop() {
		this.stop(false);
	}
	
	/**
	 * Stops the playback of the media optionally draining or
	 * flushing any remaining audio/video frames.
	 * <p>
	 * Returns true if the media was successfully reset to the 
	 * beginning of the media.
	 * @param drain true if the queued media should be drained
	 * @return boolean
	 */
	private boolean stop(boolean drain) {
		// if we are paused or playing we can stop the media and reset it
		if (this.state == State.PLAYING || this.state == State.PAUSED) {
			this.state = State.STOPPED;
			// only pause the reading thread
			this.mediaReaderThread.setPaused(true);
			
			LOGGER.debug("Seeking");
			boolean looped = this.mediaReaderThread.loop();
			
			if (drain) {
				LOGGER.debug("Draining");
				this.mediaPlayerThread.drain();
				this.audioPlayerThread.drain();
			} else {
				LOGGER.debug("Flushing");
				this.mediaPlayerThread.flush();
				this.audioPlayerThread.flush();
			}
			
			LOGGER.debug("Stopped");
			return looped;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#seek(long)
	 */
	@Override
	public void seek(long position) {
		// FIXME implement seeking
		
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#getPosition()
	 */
	@Override
	public long getPosition() {
		// FIXME implement seeking
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#release()
	 */
	@Override
	public void release() {
		this.stop();
		this.state = State.ENDED;
		this.listeners = null;
		// we overrode the end method of the reader class in
		// our anonymous inner class to call the end() methods
		// in sequence
		if (this.mediaReaderThread != null) {
			this.mediaReaderThread.end();
		}
	}
	
	/**
	 * Called when the last thread of this player has been released.
	 */
	protected void onRelease() {
		this.mediaReaderThread = null;
		this.mediaPlayerThread = null;
		this.audioPlayerThread = null;
		this.configuration = null;
		this.listeners = null;
		this.media = null;
		LOGGER.debug("Released");
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
		if (this.listeners != null) {
			for (MediaPlayerListener listener : this.listeners) {
				if (listener instanceof VideoMediaPlayerListener) {
					((VideoMediaPlayerListener)listener).onVideoImage(image);
				}
			}
		}
	}
}

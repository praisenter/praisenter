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
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.praisenter.common.threading.PausableThread;

/**
 * Thread used for synchronized playback of audio and video media.
 * <p>
 * This class is not designed to be used separately from the {@link XugglerMediaPlayer} class.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class XugglerMediaPlayerThread extends PausableThread {
	/** The maximum video buffer size */
	protected static final int SOFT_MAXIMUM_VIDEO_BUFFER_SIZE = 5;
	
	/** The maximum video buffer size 1080p ~ 6MB per frame; 100 frames would be 600MB */
	protected static final int HARD_MAXIMUM_VIDEO_BUFFER_SIZE = 100;
	
	/** The target video buffer size (the number of video frames needed before playback) wait on 3 */
	protected static final int TARGET_VIDEO_BUFFER_SIZE = 3;
	
	/** The target audio buffer size (the number of audio sample frames needed before playback) only wait on one */
	protected static final int TARGET_AUDIO_BUFFER_SIZE	= 1;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerMediaPlayerThread.class);
	
	// timing
	
	/** The video/audio synchronization clock */
	protected XugglerMediaClock clock;

	// state
	
	/** True if we can expect video frames */
	protected boolean hasVideo;
	
	/** True if we can expect audio samples */
	protected boolean hasAudio;
	
	// buffering
	
	/** The media data queue */
	protected Queue<XugglerTimedData> buffer;
	
	/** The current video buffer size */
	protected int videoBufferSize;
	
	/** The current audio buffer size */
	protected int audioBufferSize;
	
	// threading
	
	/** The lock to modify the buffer and sizes */
	protected Object bufferLock;
	
	/** The lock used to block if the buffer is currently full */
	protected Object bufferFullLock;
	
	/** The lock used to block on while draining is in progress */
	protected Object bufferDrainLock;
	
	/** True if the thread is currently draining its buffer */
	protected boolean draining;

	/**
	 * Default constructor.
	 */
	public XugglerMediaPlayerThread() {
		super("XugglerMediaPlayerThread");
		
		this.clock = new XugglerMediaClock();
		this.hasVideo = false;
		this.hasAudio = false;
		
		this.buffer = new PriorityQueue<XugglerTimedData>();
		this.videoBufferSize = 0;
		this.audioBufferSize = 0;
		
		this.bufferLock = new Object();
		this.bufferFullLock = new Object();
		this.bufferDrainLock = new Object();
		
		this.draining = false;
	}
	
	/**
	 * Initializes this player.
	 * @param hasVideo true if we can expect video
	 * @param hasAudio true if we can expect audio
	 */
	public void initialize(boolean hasVideo, boolean hasAudio) {
		this.hasVideo = hasVideo;
		this.hasAudio = hasAudio;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#setPaused(boolean)
	 */
	@Override
	public void setPaused(boolean flag) {
		// normal stuff
		super.setPaused(flag);
		// if we are unpausing we need to reset the player clock
		if (!flag) {
			this.clock.reset();
		}
		// since we could be blocking on the queue
		// we need to interrupt it to notify of the state change
		this.interrupt();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#end()
	 */
	@Override
	public void end() {
		// normal stuff
		super.end();
		// since we could be blocking on the queue
		// we need to interrupt it to notify of the state change
		this.interrupt();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.threading.PausableThread#onThreadStopped()
	 */
	@Override
	protected void onThreadStopped() {
		super.onThreadStopped();
		
		this.audioBufferSize = 0;
		this.buffer.clear();
		this.buffer = null;
		this.bufferDrainLock = null;
		this.bufferFullLock = null;
		this.bufferLock = null;
		this.clock = null;
		this.draining = false;
		this.hasAudio = false;
		this.hasVideo = false;
		this.videoBufferSize = 0;
	}
	
	/**
	 * Queues the given video image for playback.
	 * <p>
	 * This method will block the thread executing this method if the buffer is full.
	 * @param image the image
	 */
	public void queueVideoImage(XugglerVideoData image) {
		// check if the buffer is full
		while (this.isVideoBufferFull()) {
			// then wait on the buffer to reduce its size
			synchronized (this.bufferFullLock) {
				try {
					this.bufferFullLock.wait();
				} catch (InterruptedException e) {
					// if this gets interrupted, then
					// just add the data to the queue
					// and continue;
					break;
				}
			}
		}
		// increment the buffer size and add the data to the buffer
		// in one atomic operation
		synchronized (this.bufferLock) {
			// increment the video buffer size count
			this.videoBufferSize++;
			// add the image to the queue
			this.buffer.offer(image);
			// notify of the buffer changing
			this.bufferLock.notify();
		}
	}
	
	/**
	 * Queues the given audio data for playback.
	 * <p>
	 * This method will block the thread executing this method if the buffer is full.
	 * @param samples the audio samples
	 */
	public void queueAudioSamples(XugglerAudioData samples) {
		// check if the buffer is full
		while (this.isAudioBufferFull()) {
			// then wait on the buffer to reduce its size
			synchronized (this.bufferFullLock) {
				try {
					this.bufferFullLock.wait();
				} catch (InterruptedException e) {
					// if this gets interrupted, then
					// just add the data to the queue
					// and continue;
					break;
				}
			}
		}
		// increment the buffer size and add the data to the buffer
		// in one atomic operation
		synchronized (this.bufferLock) {
			// increment the audio buffer size count
			this.audioBufferSize++;
			// add the samples to the queue
			this.buffer.offer(samples);
			// notify of the buffer changing
			this.bufferLock.notify();
		}
	}
	
	/**
	 * Returns true if the video buffer is full.
	 * @return boolean
	 */
	private boolean isVideoBufferFull() {
		// we need to look at the buffer sizes together
		synchronized (this.bufferLock) {
			int vbs = this.videoBufferSize;
			int abs = this.audioBufferSize;
			
			LOGGER.trace(vbs);
			// check if we have reached the hard limit, 
			// then we need to start dropping frames
			if (vbs >= HARD_MAXIMUM_VIDEO_BUFFER_SIZE) {
				// we need to drop frames if we still haven't gotten any audio
				// (and there is audio in the video) because otherwise we will
				// fill up the queue with potentially huge video images eating
				// up tons of memory
				
				// only drop video frames since thats what's overflowing
				Iterator<XugglerTimedData> it = this.buffer.iterator();
				while (it.hasNext()) {
					XugglerTimedData data = it.next();
					if (data.getData() instanceof BufferedImage) {
						it.remove();
						vbs = --this.videoBufferSize;
						LOGGER.warn("Dropped video frame due to memory limits. Timestamp: " + data.getTimestamp());
						break;
					}
				}
			}
			// the video buffer is only full if we are at the maximum
			// buffer size AND there is audio to be played, if not, we
			// may be waiting on more audio to play and we dont want to 
			// block
			if (vbs >= SOFT_MAXIMUM_VIDEO_BUFFER_SIZE) {
				if (this.hasAudio) {
					if (abs >= TARGET_AUDIO_BUFFER_SIZE) {
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the audio buffer is full.
	 * @return boolean
	 */
	private boolean isAudioBufferFull() {
		return false;
	}
	
	/**
	 * Returns true if the buffer is ready for playback.
	 * @return boolean
	 */
	private boolean isBufferReady() { 
		// we need to look at the buffer sizes together
		synchronized (this.bufferLock) {
			boolean vbr = this.isVideoBufferReady(this.videoBufferSize);
			boolean abr = this.isAudioBufferReady(this.audioBufferSize);
			// make sure we have both audio and video (if thats what we are playing)
			if (this.draining) {
				return (vbr && this.hasVideo) || (abr && this.hasAudio);
			}
			return vbr && abr;
		}
	}
	
	/**
	 * Returns true if the video buffer is ready.
	 * @param videoBufferSize the current video buffer size
	 * @return boolean
	 */
	private boolean isVideoBufferReady(int videoBufferSize) {
		if (this.hasVideo) {
			if (this.draining) {
				// don't block if we are draining unless its zero
				return videoBufferSize > 0;
			}
			return videoBufferSize >= TARGET_VIDEO_BUFFER_SIZE;
		}
		return true;
	}
	
	/**
	 * Returns true if the audio buffer is ready.
	 * @param audioBufferSize the current audio buffer size
	 * @return boolean
	 */
	private boolean isAudioBufferReady(int audioBufferSize) {
		if (this.hasAudio) {
			if (this.draining) {
				// don't block if we are draining unless its zero
				return audioBufferSize > 0;
			}
			return audioBufferSize >= TARGET_AUDIO_BUFFER_SIZE;
		}
		return true;
	}
	
	/**
	 * Drains the player of all buffer entries.
	 * <p>
	 * This method blocks until all entries have been played.
	 */
	public void drain() {
		// set the draining flag
		this.draining = true;
		// don't return until draining has completed
		while (this.draining) {
			synchronized (this.bufferDrainLock) {
				try {
					this.bufferDrainLock.wait(100);
				} catch (InterruptedException e) {
					// just ignore the exception and break
					// from the loop, throwing away anything
					// we still needed to play
					this.draining = false;
					break;
				}
			}
			// its possible that just before we initiated 
			// the wait on this thread, that the playing thread 
			// emptied the buffer.  We need to make sure we
			// should still continue
			synchronized (this.bufferLock) {
				this.bufferLock.notify();
			}
		}
		// clear everything after draining
		this.flush();
	}
	
	/**
	 * Flushes the player of all buffer entries.
	 * <p>
	 * This method will throw away any buffer entries that have not been played.
	 */
	public void flush() {
		// wait on the buffer full lock
		synchronized (this.bufferLock) {
			this.buffer.clear();
			this.audioBufferSize = 0;
			this.videoBufferSize = 0;
			// notify this thread of the change
			this.bufferLock.notify();
		}
		this.clock.reset();
	}
	
	/**
	 * Attempts to synchronize the given timestamp with the CPU clock.
	 * <p>
	 * Returns true if this thread was able to sleep the calculated time, false if
	 * it was interrupted.
	 * @param timestamp the timestamp in microseconds
	 * @param isVideo true if the given timestamp is for video
	 * @return boolean
	 */
	private boolean synchronize(long timestamp, boolean isVideo) {
		// get the time required to sleep
		long syncTime = this.clock.getSynchronizationTime(timestamp, isVideo);
		// clamp the sleep time
		if (syncTime > 1000) {
			LOGGER.warn("Clamped synchronization time for timestamp: " + timestamp + " time: " + syncTime + " to 1000 ms.");
			syncTime = 1000;
		}
		if (syncTime > 0) {
			try {
				Thread.sleep(syncTime);
			} catch (InterruptedException e) {
				// this shouldn't happen unless we were paused
				// or stopped, so just continue execution
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#executeTask()
	 */
	@Override
	protected void executeTask() {
		// make sure the buffer is ready
		while (!this.isBufferReady()) {
			// check if we were draining the buffers
			if (this.draining) {
				// we were, so clear the draining flag
				// and notify the thread that initiated the
				// drain
				this.draining = false;
				synchronized (this.bufferDrainLock) {
					this.bufferDrainLock.notify();
				}
			}
			// wait until the buffer fills
			synchronized (this.bufferLock) {
				try {
					bufferLock.wait();
				} catch (InterruptedException e) {
					// we may have been stopped or paused
				}
			}
			if (this.isStopped() || this.isPaused()) {
				return;
			}
		}
		
		// make sure we get something from the queue and update the sizes
		// in one atomic operation
		XugglerTimedData data = null;
		synchronized (this.bufferLock) {
			// the buffer is ready, lets get some data
			data = this.buffer.poll();
			// check for null
			if (data != null) {
				// update the size depending on the type
				if (data.getData() instanceof BufferedImage) {
					this.videoBufferSize--;
				} else {
					this.audioBufferSize--;
				}
			}
		}
		// see if we got some data
		if (data != null) {
			// notify that we updated the buffer sizes
			synchronized (this.bufferFullLock) {
				this.bufferFullLock.notify();
			}
		} else {
			// just continue if we didnt get anything
			return;
		}
		
		// get the media type
		boolean isVideo = data.getData() instanceof BufferedImage;
		
		// synchronize the playback of the data with the system clock
		// and its timestamp
		LOGGER.trace((isVideo ? "Video " : "Audio ") + data.getTimestamp());
		if (synchronize(data.getTimestamp(), isVideo)) {
			// check the data type to figure out how to play it
			if (isVideo) {
//				LOGGER.debug("Playing video: v(" + this.videoBufferSize + ") a(" + this.audioBufferSize + ")");
				// send the image off to the media listeners
				this.playVideo((BufferedImage)data.getData());
			} else {
//				LOGGER.debug("Playing audio: v(" + this.videoBufferSize + ") a(" + this.audioBufferSize + ")");
				// its sound data and we need to play it using JavaSound on yet another thread
				this.playAudio((byte[])data.getData());
			}
		} else {
			// if we got interrupted while sleeping we just need to drop the
			// data.  we most likely were interrupted due to a pause or stop
			// in both cases we can throw the frame away
			LOGGER.warn("Sleep interrupted. Dropping " + (isVideo ? "video" : "audio") + " frame (" + data.getTimestamp() + ").");
		}
	}
	
	/**
	 * Called when some audio samples are ready for playback.
	 * @param samples the samples to play
	 */
	protected abstract void playAudio(byte[] samples);
	
	/**
	 * Called when a video image is ready for display.
	 * @param image the image
	 */
	protected abstract void playVideo(BufferedImage image);
}

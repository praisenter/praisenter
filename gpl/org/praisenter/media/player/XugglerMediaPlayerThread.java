package org.praisenter.media.player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.sound.sampled.SourceDataLine;

import org.praisenter.media.MediaPlayerListener;
import org.praisenter.thread.PausableThread;

public abstract class XugglerMediaPlayerThread extends PausableThread {
	protected static final int MAXIMUM_VIDEO_BUFFER_SIZE = 5;
	
	/** The video/audio synchronization clock */
	protected XugglerMediaClock clock;
	
	// buffering
	
	protected Queue<XugglerTimedData<?>> buffer;
	protected int videoBufferSize;
	protected int audioBufferSize;
	protected Object bufferLock;
	protected Object bufferFullLock;
	protected Object bufferDrainLock;
	
	protected boolean hasVideo;
	protected boolean hasAudio;
	
	protected boolean draining;

	/** The list of media player listeners */
	protected List<MediaPlayerListener> listeners;
	
	public XugglerMediaPlayerThread() {
		super("XugglerMediaPlayerThread");
		
		this.clock = new XugglerMediaClock();
		
		this.buffer = new PriorityQueue<>();
		this.listeners = new ArrayList<>();
		
		this.videoBufferSize = 0;
		this.audioBufferSize = 0;
		
		this.bufferLock = new Object();
		this.bufferFullLock = new Object();
		this.bufferDrainLock = new Object();
		
		this.hasVideo = false;
		this.hasAudio = false;
		
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
	
	/**
	 * Queues the given video image for playback.
	 * <p>
	 * This method will block the thread executing this method if the buffer is full.
	 * @param image the image
	 */
	public void queueVideoImage(XugglerTimedData<BufferedImage> image) {
		// check if the buffer is full
		while (this.isVideoBufferFull()) {
			// then wait on the buffer to reduce its size
			synchronized (this.bufferFullLock) {
				try {
					this.bufferFullLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
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
	public void queueAudioSamples(XugglerTimedData<byte[]> samples) {
		// check if the buffer is full
		while (this.isAudioBufferFull()) {
			// then wait on the buffer to reduce its size
			synchronized (this.bufferFullLock) {
				try {
					this.bufferFullLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			// the video buffer is only full if we are at the maximum
			// buffer size AND there is audio to be played, if not, we
			// may be waiting on more audio to play and we dont want to 
			// block
			if (vbs > MAXIMUM_VIDEO_BUFFER_SIZE) {
				if (this.hasAudio) {
					if (abs > 0) {
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
				return videoBufferSize > 0;
			}
			return videoBufferSize > 2;
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
				return audioBufferSize > 0;
			}
			return audioBufferSize > 2;
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
					this.bufferDrainLock.wait();
				} catch (InterruptedException e) {
					// just ignore the exception and break
					// from the loop, throwing away anything
					// we still needed to play
					break;
				}
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
		this.buffer.clear();
		this.clock.reset();
		
		// wait on the buffer full lock
		synchronized (this.bufferLock) {
			this.audioBufferSize = 0;
			this.videoBufferSize = 0;
			// notify this thread of the change
			this.bufferLock.notify();
		}
	}
	
	/**
	 * Attempts to synchronize the given timestamp with the CPU clock.
	 * <p>
	 * Returns true if this thread was able to sleep the calculated time, false if
	 * it was interrupted.
	 * @param timestamp the timestamp in microseconds
	 * @return boolean
	 */
	private boolean synchronize(long timestamp) {
		long syncTime = this.clock.getSynchronizationTime(timestamp, true);
		if (syncTime > 1000) {
			System.out.println("Clamped sleep time " + syncTime);
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
					if (this.isPaused() || this.isStopped()) {
						// if we have then return
						return;
					}
				}
			}
		}
		
		// make sure we get something from the queue and update the sizes
		// in one atomic operation
		XugglerTimedData<?> data = null;
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
		}
		
		// synchronize the playback of the data with the system clock
		// and its timestamp
		if (synchronize(data.getTimestamp())) {
			// check the data type to figure out how to play it
			if (data.getData() instanceof BufferedImage) {
				// send the image off to the media listeners
				this.playVideo((BufferedImage)data.getData());
			} else {
				// its sound data and we need to play it using JavaSound on yet another thread
				this.playAudio((byte[])data.getData());
			}
		} else {
			// if we got interrupted while sleeping we need to add the data
			// back to the queue
			synchronized (this.bufferLock) {
				this.buffer.offer(data);
				// and increment back the sizes
				if (data.getData() instanceof BufferedImage) {
					this.videoBufferSize++;
				} else {
					this.audioBufferSize++;
				}
			}
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

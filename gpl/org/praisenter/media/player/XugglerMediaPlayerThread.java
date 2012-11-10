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
	protected static final int MAXIMUM_VIDEO_BUFFER_SIZE = 3;
	
	/** The video/audio synchronization clock */
	protected XugglerMediaClock clock;
	
	// buffering
	
	protected Queue<XugglerTimedData<?>> buffer;
	protected int videoBufferSize;
	protected int audioBufferSize;
	protected Object bufferLock;
	protected Object bufferFullLock;
	
	protected boolean hasVideo;
	protected boolean hasAudio;
	protected boolean draining;

	/** The list of media player listeners */
	protected List<MediaPlayerListener> listeners;
	
	public XugglerMediaPlayerThread() {
		super("XugglerMediaPlayerThread");
		
		this.clock = new XugglerMediaClock();
		this.draining = false;
		
		this.buffer = new PriorityQueue<>();
		this.listeners = new ArrayList<>();
		
		this.videoBufferSize = 0;
		this.audioBufferSize = 0;
		
		this.bufferLock = new Object();
		this.bufferFullLock = new Object();
		
		this.hasVideo = false;
		this.hasAudio = false;
	}
	
	public void initialize(boolean hasVideo, boolean hasAudio) {
		this.hasVideo = hasVideo;
		this.hasAudio = hasAudio;
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
					// TODO Auto-generated catch block
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
		if (this.draining) {
			return true;
		}
		// get the buffer sizes
		int vbs = 0;
		int abs = 0;
		// we need to look at the buffer sizes together
		synchronized (this.bufferLock) {
			vbs = this.videoBufferSize;
			abs = this.audioBufferSize;
		}
		// the video buffer is only full if we are at the maximum
		// buffer size AND there is audio to be played, if not, we
		// may be waiting on more audio to play and we dont want to 
		// block
		if (vbs > MAXIMUM_VIDEO_BUFFER_SIZE && abs > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the audio buffer is full.
	 * @return boolean
	 */
	private boolean isAudioBufferFull() {
		if (this.draining) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the buffer is ready for playback.
	 * @return boolean
	 */
	private boolean isBufferReady() {
		// get the buffer states
		boolean vbr = false;
		boolean abr = false;
		// we need to look at the buffer sizes together
		synchronized (this.bufferLock) {
			vbr = this.isVideoBufferReady(this.videoBufferSize);
			abr = this.isAudioBufferReady(this.audioBufferSize);
		}
		if (this.draining) {
			// if we are draining, then just return true if either type
			// of data is buffered
			return vbr || abr;
		} else {
			// make sure we have both audio and video (if thats what we are playing)
			return vbr && abr;
		}
	}
	
	private boolean isVideoBufferReady(int videoBufferSize) {
		if (this.hasVideo) {
			return videoBufferSize > 0;
		}
		return true;
	}
	
	private boolean isAudioBufferReady(int audioBufferSize) {
		if (this.hasAudio) {
			return audioBufferSize > 0;
		}
		return true;
	}
	
	public void drainBuffers() {
		this.draining = true;
		// notify this thread of the change
		synchronized (this.bufferLock) {
			this.bufferLock.notify();
		}
		// wait on the buffer full lock
		while (this.buffer.size() > 0) {
			synchronized (this.bufferFullLock) {
				try {
					this.bufferFullLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		this.clock.reset();
		this.draining = false;
	}
	
	public void flushBuffers() {
		this.buffer.clear();
		this.clock.reset();
	}
	
	/**
	 * Attempts to synchronize the given timestamp with the CPU clock.
	 * @param timestamp the timestamp in microseconds
	 */
	private void synchronize(long timestamp) {
		long syncTime = clock.getSynchronizationTime(timestamp, false);
		if (syncTime > 1000) {
			System.out.println("Clamped sleep time " + syncTime);
			syncTime = 1000;
		}
		if (syncTime > 0) {
			try {
				Thread.sleep(syncTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#executeTask()
	 */
	@Override
	protected void executeTask() {
		// make sure the buffer is ready
		while (!this.isBufferReady()) {
			// if its not, then wait on it
			synchronized (this.bufferLock) {
				try {
					bufferLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			if (data == null) {
				return;
			}
			// update the size depending on the type
			if (data.getData() instanceof BufferedImage) {
				this.videoBufferSize--;
			} else {
				this.audioBufferSize--;
			}
			// notify that we updated the buffer sizes
			synchronized (this.bufferFullLock) {
				this.bufferFullLock.notifyAll();
			}
		}
		
		// synchronize the playback of the data with the system clock
		// and its timestamp
		synchronize(data.getTimestamp());
		// check the data type to figure out how to play it
		if (data.getData() instanceof BufferedImage) {
			// send the image off to the media listeners
			this.playVideo((BufferedImage)data.getData());
		} else {
			// its sound data and we need to play it using JavaSound on yet another thread
			this.playAudio((byte[])data.getData());
		}
	}
	
	protected abstract void playAudio(byte[] samples);
	
	protected abstract void playVideo(BufferedImage image);
}

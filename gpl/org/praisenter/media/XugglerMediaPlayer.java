package org.praisenter.media;

import java.awt.image.BufferedImage;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

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
	
	// container
	
	protected IContainer container;
	
	// video
	
	protected IStreamCoder videoCoder;
	protected IConverter videoConverter;
	
	// audio
	
	protected IStreamCoder audioCoder;
	protected SourceDataLine audioLine;
	
	// threads
	
	protected final int BUFFER_LIMIT = 8;
	protected BlockingQueue<XugglerTimedData<?>> dataQueue = new PriorityBlockingQueue<>(BUFFER_LIMIT);
	protected Object queueFullMonitor = new Object();
	
	protected MediaReaderThread readerThread;
	protected MediaPlayerThread playerThread;
	protected AudioThread audioThread;
	protected Object threadLock = new Object();

	/** Media player lock for state changes */
	protected Object stateLock = new Object();
	
	// walk through each packet of the container format
	protected IPacket packet = IPacket.make();
	
	protected IVideoPicture picture;
	protected IAudioSamples samples;
	
	
	// TODO some suggest synching based on the audio output timestamps
	public XugglerMediaPlayer() {
		this.state = State.STOPPED;
		this.clock = new XugglerMediaClock();
		this.listeners = new ArrayList<>();
		this.looped = true;
		
		// start the threads
		this.readerThread = new MediaReaderThread();
		this.playerThread = new MediaPlayerThread();
		this.audioThread = new AudioThread();
		
		this.readerThread.start();
		this.playerThread.start();
		this.audioThread.start();
	}
	
	/**
	 * Initializes the playback of the given media.
	 * @param media the media
	 */
	private void initializeMedia(XugglerPlayableMedia media) {
		// get the Xuggler objects from the media
		this.container = media.getContainer();
		this.audioCoder = media.getAudioCoder();
		if (media instanceof XugglerVideoMedia) {
			XugglerVideoMedia vMedia = (XugglerVideoMedia)media;
			this.videoCoder = vMedia.getVideoCoder();
		}
		
		// create the image converter for the video
		if (videoCoder != null) {
			this.picture = IVideoPicture.make(this.videoCoder.getPixelType(), this.videoCoder.getWidth(), this.videoCoder.getHeight());
			BufferedImage target = new BufferedImage(this.videoCoder.getWidth(), this.videoCoder.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			this.videoConverter = ConverterFactory.createConverter(target, this.videoCoder.getPixelType());
		}
		
		// get a JavaSound audio line for the audio
		if (audioCoder != null) {
			this.samples = IAudioSamples.make(1024, this.audioCoder.getChannels());
			boolean formatSupported = false;
			// attempt to use the media's audio format
			AudioFormat format = new AudioFormat(
					this.audioCoder.getSampleRate(), 
					(int)IAudioSamples.findSampleBitDepth(this.audioCoder.getSampleFormat()),
					this.audioCoder.getChannels(), 
					true, 
					false);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			
			// see if its supported
			if (!AudioSystem.isLineSupported(info)) {
				// if its not supported, this is typically due to the number of playback channels
				// lets try the same format with just 2 channels (stereo)
				format = new AudioFormat(
						this.audioCoder.getSampleRate(), 
						(int)IAudioSamples.findSampleBitDepth(this.audioCoder.getSampleFormat()),
						2, 
						true, 
						false);
				info = new DataLine.Info(SourceDataLine.class, format);
				if (AudioSystem.isLineSupported(info)) {
					formatSupported = true;
				}
			} else {
				formatSupported = true;
			}
			
			// verify the format is supported
			if (formatSupported) {
				try{
					// create and open JavaSound
					this.audioLine = (SourceDataLine)AudioSystem.getLine(info);
					this.audioLine.open(format);
					this.audioLine.start();
				} catch (LineUnavailableException e) {
					LOGGER.warn("Audio line not available: ", e);
					this.audioCoder = null;
					this.audioLine = null;
					this.muted = true;
				}
			} else {
				this.audioCoder = null;
				this.audioLine = null;
				this.muted = true;
			}
		}
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
		System.out.println("--Playing");
		synchronized (this.stateLock) {
			this.stateLock.notifyAll();
		}
	}
	
	/**
	 * Stops the current playback and seeks to the beginning and
	 * beings playback.
	 */
	private void loop() {
		stop();
		play();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#pause()
	 */
	@Override
	public void pause() {
		this.state = State.PAUSED;
		System.out.println("--Paused");
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#resume()
	 */
	@Override
	public void resume() {
		this.state = State.PLAYING;
		this.clock.reset();
		System.out.println("--Resumed");
		synchronized (this.stateLock) {
			this.stateLock.notifyAll();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#stop()
	 */
	@Override
	public void stop() {
		this.state = State.STOPPED;
		
		System.out.println("pausing reader thread");
		// if the current thread is not the reader thread
		// then we need to wait until the thread has been paused
		if (this.readerThread != Thread.currentThread()) {
			System.out.println("not the reader thread");
			while (!this.readerThread.paused) {
				System.out.println("not paused yet");
				// wait on them to pause
				synchronized (this.threadLock) {
					try {
						this.threadLock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("--Stopped");
		
		System.out.println("--Seeking");
		if (this.videoCoder != null) {
			this.container.seekKeyFrame(this.videoCoder.getStream().getIndex(), 0, 0, 0, IContainer.SEEK_FLAG_ANY | IContainer.SEEK_FLAG_FRAME);
			System.out.println(this.videoCoder.getStream().getCurrentDts());
		}
		
		if (this.audioCoder != null) {
			this.container.seekKeyFrame(this.audioCoder.getStream().getIndex(), 0, 0, 0, IContainer.SEEK_FLAG_ANY);
			System.out.println(this.audioCoder.getStream().getCurrentDts());
		}
		
		this.dataQueue.clear();
		
		// restart the clock
		this.clock.reset();
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
	
	
	public void releaseResources() {
		if (container != null) {
            container.close();
        }
        if (videoCoder != null) {
            videoCoder.close();
        }
        if (audioCoder != null) {
            audioCoder.close();
        }
        if (this.audioLine != null) {
        	this.audioLine.close();
        }
	}
	
	private void notifyListeners(BufferedImage image) {
		for (MediaPlayerListener listener : this.listeners) {
			listener.onVideoPicture(image);
		}
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
	
	/**
	 * The thread used to read the media.
	 * <p>
	 * This thread will queue up the data in order and allow the audio and video threads to 
	 * playback the read data.  This thread will also buffer only a few items so that the
	 * entire media is not read into memory.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class MediaReaderThread extends Thread {
		protected boolean paused = false;
		
		public MediaReaderThread() {
			super("MediaReaderThread");
			this.setDaemon(true);
		}
		
		@Override
		public void run() {
			// run this thread forever, well until stopped
			while (true) {
				// see if the media player has been ended
				if (state == MediaPlayer.State.ENDED) {
					this.paused = true;
					synchronized (threadLock) {
						threadLock.notify();
					}
					return;
				}
				// check if we are playing
				while (state != MediaPlayer.State.PLAYING) {
					// obtain the lock
					synchronized (stateLock) {
						try {
							this.paused = true;
							synchronized (threadLock) {
								threadLock.notify();
								System.out.println("Reader thread stopped");
							}
							// wait until we are notified
							stateLock.wait();
						} catch (InterruptedException e) {
							// see if the media player has been ended
							if (state == MediaPlayer.State.ENDED) {
								return;
							}
						}
					}
				}
				this.paused = false;
				
				// otherwise lets read the next packet
				if (container.readNextPacket(packet) < 0) {
					// we are at the end of the media, do we need to loop?
					if (looped) {
						// reset everything
						loop();
						// restart this iteration
						continue;
					} else {
						// stop playback
						XugglerMediaPlayer.this.stop();
					}
				}
				
				// make sure the packet belongs to the stream we care about
				if (videoCoder != null && packet.getStreamIndex() == videoCoder.getStream().getIndex()) {
					int offset = 0;
					// decode the video
					while (offset < packet.getSize()) {
						int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
						if (bytesDecoded < 0) {
							break;
						}
						offset += bytesDecoded;

						// make sure that we have a full picture from the video first
						if (picture.isComplete()) {
							// convert the picture to an Java buffered image
							BufferedImage image = videoConverter.toImage(picture);
							// add the timed data to the queue
							System.out.println("Image " + picture.getTimeStamp());
							addToQueue(new XugglerTimedData<BufferedImage>(picture.getTimeStamp(), image));
						}
					}
				}
				
				if (audioCoder != null && packet.getStreamIndex() == audioCoder.getStream().getIndex()) {
	                int offset = 0;
	                while(offset < packet.getSize()) {
	                    int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
	                    if (bytesDecoded < 0) {
	                        break;
	                    }
	                    offset += bytesDecoded;
	                    if (samples.isComplete()) {
	                    	// get the sample data to send to JavaSound
	                    	byte[] data = samples.getData().getByteArray(0, samples.getSize());
	                    	// TODO see if we need to resample
	                    	data = AudioDownmixer.downmixToStereo(
									data, 
									(int)IAudioSamples.findSampleBitDepth(audioCoder.getSampleFormat()), 
									audioCoder.getChannels(),
									ByteOrder.LITTLE_ENDIAN);
	                    	// add the timed data to the queue
	                    	System.out.println("Audio " + samples.getTimeStamp());
	                    	addToQueue(new XugglerTimedData<byte[]>(samples.getTimeStamp(), data));
	                    }
	                }
	            }
			}
		}
		
		private void addToQueue(XugglerTimedData<?> data) {
			while (dataQueue.size() >= BUFFER_LIMIT) {
				try {
					synchronized (queueFullMonitor) {
						queueFullMonitor.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dataQueue.offer(data);
		}
	}
	
	/**
	 * Thread used to play the {@link XugglerTimedData} from the {@link MediaReaderThread}.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class MediaPlayerThread extends Thread {
		public MediaPlayerThread() {
			super("MediaPlayerThread");
			this.setDaemon(true);
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					XugglerTimedData<?> data = dataQueue.poll(100, TimeUnit.MILLISECONDS);
					if (state != MediaPlayer.State.PLAYING) {
						   synchronized (stateLock) {
							   stateLock.wait();
						   }
					   }
					if (data != null) {
						synchronized (queueFullMonitor) {
							queueFullMonitor.notify();
						}
						synchronize(data.getTimestamp());
						if (data.getData() instanceof BufferedImage) {
							// send the image off to the media listeners
							notifyListeners((BufferedImage)data.getData());
						} else {
							// its sound data and we need to play it using JavaSound on yet another thread
							audioThread.queue.offer((byte[])data.getData());
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private class AudioThread extends Thread {
		protected BlockingDeque<byte[]> queue = new LinkedBlockingDeque<>();
		
		public AudioThread() {
			super("AudioThread");
			this.setDaemon(true);
		}
		
		@Override
		public void run() {
		   try {
			   while(true) { 
				   if (state != MediaPlayer.State.PLAYING) {
					   synchronized (stateLock) {
						   stateLock.wait();
					   }
				   }
				   byte[] data = queue.take();
//				   queue.clear();
				   audioLine.write(data, 0, data.length);
			   }
	       } catch (InterruptedException ex) {
	    	   
	       }
		}
	}
}

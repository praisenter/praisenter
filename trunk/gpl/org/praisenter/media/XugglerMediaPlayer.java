package org.praisenter.media;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class XugglerMediaPlayer extends Thread {
	public static enum State {
		STOPPED,
		PLAYING,
		PAUSED
	}
	
	/** The state of the media player */
	protected State state; 
	
	/** The video/audio synchronization clock */
	protected XugglerMediaClock clock;
	
	/** True if the media should loop */
	protected boolean looped;
	
	/** True if the media is muted */
	protected boolean muted;

	/** Media player lock for state changes */
	protected Object lock = new Object();
	
	/** The list of media player listeners */
	protected List<MediaPlayerListener> listeners;
	
	// container
	
	protected IContainer container;
	
	// video
	
	protected IStreamCoder videoCoder;
	protected IConverter videoConverter;
	
	// audio
	protected IStreamCoder audioCoder;
	protected AudioInputStream audioStream;
	protected SourceDataLine audioLine;
	protected AudioThread audioThread;
	
	// TODO some suggest synching based on the audio output timestamps
	public XugglerMediaPlayer(XugglerVideoMedia media) {
		this(media.container,
			 media.videoCoder,
			 media.audioCoder);
	}
	
	private XugglerMediaPlayer(
			IContainer container,
			IStreamCoder videoCoder,
			IStreamCoder audioCoder) {
		super("VideoPlayerThread");
		this.setDaemon(true);
		
		this.state = State.STOPPED;
		this.clock = new XugglerMediaClock();
		this.listeners = new ArrayList<>();
		this.looped = true;
		
		this.container = container;
		this.videoCoder = videoCoder;
		this.audioCoder = audioCoder;
		this.audioThread = new AudioThread();
		this.audioThread.start();
		
		if (videoCoder != null) {
			BufferedImage target = new BufferedImage(this.videoCoder.getWidth(), this.videoCoder.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			this.videoConverter = ConverterFactory.createConverter(target, this.videoCoder.getPixelType());
		}
		
		if (audioCoder != null) {
			System.out.println(this.audioCoder.getStream().getStartTime());
			AudioFormat format = new AudioFormat(
					this.audioCoder.getSampleRate(), 
					(int)IAudioSamples.findSampleBitDepth(this.audioCoder.getSampleFormat()),
					this.audioCoder.getChannels(), 
					true, 
					false);
			try {
				
				AudioFormat test = new AudioFormat(48000, 16, 2, true, false);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, test);
				System.out.println(AudioSystem.isLineSupported(new DataLine.Info(SourceDataLine.class, test)));
				System.out.println(AudioSystem.isConversionSupported(test, format));
				if (AudioSystem.isLineSupported(info)) {
					this.audioLine = (SourceDataLine)AudioSystem.getLine(info);
					this.audioLine.open(test);
					this.audioLine.start();
				} else {
					System.out.println("audio line not supported");
				}
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#startPlayback()
	 */
	public void startPlayback() {
		if (!this.isAlive()) {
			synchronized (this.lock) {
				this.state = State.PLAYING;
			}
			this.start();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#endPlayback()
	 */
	public void endPlayback() {
		if (this.isAlive()) {
			synchronized (lock) {
				this.state = State.STOPPED;
				// TODO we should flush/stop the audio line
			}
			this.clock.reset();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#pausePlayback()
	 */
	public void pausePlayback() {
		if (this.isAlive()) {
			synchronized (this.lock) {
				if (this.state == State.PLAYING) {
					this.state = State.PAUSED;
					// TODO we should flush/stop the audio line
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#resumePlayback()
	 */
	public void resumePlayback() {
		if (this.isAlive()) {
			synchronized (this.lock) {
				if (this.state == State.PAUSED) {
					this.state = State.PLAYING;
					this.lock.notify();
				}
			}
		}
	}
	
	public void seekPlayback(long milliseconds) {
		
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
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// walk through each packet of the container format
		IPacket packet = IPacket.make();
		
		IVideoPicture picture = null;
		if (this.videoCoder != null) {
			picture = IVideoPicture.make(
					this.videoCoder.getPixelType(), 
					this.videoCoder.getWidth(), 
					this.videoCoder.getHeight());
		}
		
		IAudioSamples samples = null;
		if (this.audioCoder != null) {
			samples = IAudioSamples.make(
					1024, 
					this.audioCoder.getChannels());
		}
		
		// run this thread forever
		while (this.state != State.STOPPED) {
			// check if we are playing
			if (this.state == State.PAUSED) {
				// obtain the lock
				synchronized (lock) {
					try {
						// wait until we are notified
						lock.wait();
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			
			// otherwise lets read the next packet
			if (this.container.readNextPacket(packet) < 0) {
				// we are at the end of the media, do we need to loop?
				if (this.looped) {
					// seek the video
					if (this.videoCoder != null) {
						this.container.seekKeyFrame(this.videoCoder.getStream().getIndex(), 0, 0, 0, 0);
					}
					
					if (this.audioCoder != null) {
						this.container.seekKeyFrame(this.audioCoder.getStream().getIndex(), 0, 0, 0, 0);
						this.audioLine.flush();
					}
					
					// reset the sync clock
					this.clock.reset();
					continue;
				} else {
					// if not then set the state
					
					return;
				}
			}
			
			// make sure the packet belongs to the stream we care about
			if (this.videoCoder != null && packet.getStreamIndex() == this.videoCoder.getStream().getIndex()) {
				int offset = 0;
				// decode the video
				while (offset < packet.getSize()) {
					int bytesDecoded = this.videoCoder.decodeVideo(picture, packet, offset);
					if (bytesDecoded < 0) {
						break;
					}
					offset += bytesDecoded;

					// make sure that we have a full picture from the video first
					if (picture.isComplete()) {
						// convert the picture to an Java buffered image
						BufferedImage image = this.videoConverter.toImage(picture);
						// sync up the frame rate
						this.synchronize(picture.getTimeStamp());
						// notify any listeners
						notifyListeners(image);
					}
				}
			}
			
			if (this.audioCoder != null && this.audioLine != null && packet.getStreamIndex() == this.audioCoder.getStream().getIndex()) {
                int offset = 0;
                while(offset < packet.getSize()) {
                    int bytesDecoded = this.audioCoder.decodeAudio(samples, packet, offset);
                    if (bytesDecoded < 0) {
                        break;
                    }
                    offset += bytesDecoded;
                    if (samples.isComplete()) {
//                    	this.audioResampler.resample(samples1, samples, samples.getSize());
                    	byte[] data = samples.getData().getByteArray(0, samples.getSize());

                    	byte[] ndata = AudioDownmixer.downmixToStereo(
								data, 
								(int)IAudioSamples.findSampleBitDepth(this.audioCoder.getSampleFormat()), 
								this.audioCoder.getChannels(),
								ByteOrder.LITTLE_ENDIAN);
//                    	long syncTime = this.audioClock.getSynchronizationTime(samples.getTimeStamp(), false);
//                        try {
//							Thread.sleep(syncTime);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//                        try {
//							audioThread.queue.put(ndata);
//						} catch (InterruptedException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
                    	audioLine.write(ndata, 0, ndata.length);
                    }
                }
            }
		}
	}
	
	/**
	 * Attempts to synchronize the given timestamp with the CPU clock.
	 * @param timestamp the timestamp in microseconds
	 */
	private void synchronize(long timestamp) {
		long syncTime = this.clock.getSynchronizationTime(timestamp, false);
		try {
			Thread.sleep(syncTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//    private void seekAudio(long seek) {
//        // try to use xuggler bundled algorithm for supported codecs
//        IRational rational = this.audioCoder.getTimeBase();
//        long seekTime = seek * rational.getDenominator() / rational.getNumerator() / 1000;
//        long audioSeekOffset = 0;
//        if (container.seekKeyFrame(this.audioCoder.getStream().getIndex(), seekTime, IContainer.SEEK_FLAG_ANY) >= 0) {
//            audioSeekOffset = -this.audioCoder.getStream().getStartTime() * 1000;
//            return;
//        }
//        //xuggler doesn't have good positioning algorithm for audio. So here is my own one
//        audioCoder.close();
//        if (audioCoder.open(null, null) < 0) {
//            throw new RuntimeException("can't open codec");
//        }
//        
//        IPacket packet = IPacket.make();
//        while(container.readNextPacket(packet) >= 0) {
//            if (packet.getStreamIndex() == this.audioCoder.getStream().getIndex()) {
//                break;
//            }
//        }
//        long headerOffset = packet.getPosition();
//        audioSeekOffset = seek - this.audioCoder.getStream().getStartTime();
//        long bytesOffset =(audioSeekOffset * (container.getFileSize() - headerOffset)) / this.audioCoder.getStream().getDuration();
//        container.seekKeyFrame(this.audioCoder.getStream().getIndex(), bytesOffset, IContainer.SEEK_FLAG_BYTE);
//        audioSeekOffset *= 1000;
//    }
//	
//	private void seekVideo(long seek) {
//		 IRational rational = videoCoder.getTimeBase();
//	        long seekTime = seek * rational.getDenominator() / rational.getNumerator() / 1000;
//	        container.seekKeyFrame(videoCoder.getStream().getIndex(), 0, seekTime, seekTime, 0);
//	        long pictureTime = seek * 1000;
//	        
//	        IPacket packet = IPacket.make();
//	        while(container.readNextPacket(packet) >= 0) {
//	            if (packet.getStreamIndex() == videoCoder.getStream().getIndex()) {
//	            	IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
//	                int offset = 0;
//	                while(offset < packet.getSize()) {
//	                    int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
//	                    if (bytesDecoded < 0) {
//	                    	System.out.println("problems");
////	                        throw new RuntimeException("got error decoding video in:"  + fileName);
//	                    }
//	                    offset += bytesDecoded;
//
//	                    if (picture.isComplete() && picture.getTimeStamp() >= pictureTime) {
//	                        return;
//	                    }
//	                }
//	            }
//	        }
//	}
	
	public void addMediaPlayerListener(MediaPlayerListener listener) {
		this.listeners.add(listener);
	}
	
	public boolean removeMediaPlayerListener(MediaPlayerListener listener) {
		return this.listeners.remove(listener);
	}
	
	private void notifyListeners(BufferedImage image) {
		for (MediaPlayerListener listener : this.listeners) {
			listener.updated(image);
		}
	}
	
	private class AudioThread extends Thread {
		protected BlockingDeque<byte[]> queue = new LinkedBlockingDeque<>(5);
		
		public AudioThread() {
			super("AudioThread");
			this.setDaemon(true);
		}
		
		@Override
		public void run() {
		   try {
			   while(true) { 
				   byte[] data = queue.take();
//				   queue.clear();
				   audioLine.write(data, 0, data.length);
			   }
	       } catch (InterruptedException ex) {
	    	   
	       }
		}
	}
}

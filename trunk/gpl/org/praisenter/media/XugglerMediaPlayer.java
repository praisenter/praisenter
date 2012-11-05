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
	
	protected State state; 
	protected XugglerMediaClock videoClock;
	protected XugglerMediaClock audioClock;
	
	protected boolean loop;
	protected boolean playVideo;
	protected boolean playAudio;

	protected Object lock = new Object();
	
	protected List<MediaPlayerListener> listeners;
	
	// container
	
	protected IContainer container;
	
	// video
	
	protected IStreamCoder videoCoder;
	protected IConverter videoConverter;
	protected VideoThread videoThread;
	
	// audio
	protected IStreamCoder audioCoder;
	protected AudioInputStream audioStream;
	protected SourceDataLine audioLine;
	protected AudioThread audioThread;
	
	public XugglerMediaPlayer(
			IContainer container,
			IStreamCoder videoCoder,
			IStreamCoder audioCoder) {
		super("VideoPlayerThread");
		this.setDaemon(true);
		
		this.state = State.STOPPED;
		this.videoClock = new XugglerMediaClock();
		this.audioClock = new XugglerMediaClock();
		this.listeners = new ArrayList<>();
		this.loop = true;
		
		this.container = container;
		this.videoCoder = videoCoder;
		this.videoThread = new VideoThread();
		this.videoThread.start();
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
	
	public void beginPlayback() {
		if (!this.isAlive()) {
			synchronized (this.state) {
				this.state = State.PLAYING;
			}
			this.start();
		}
	}
	
	public void endPlayback() {
		if (this.isAlive()) {
			synchronized (state) {
				this.state = State.STOPPED;
			}
			this.videoClock.reset();
		}
	}
	
	public void pausePlayback() {
		if (this.isAlive()) {
			synchronized (this.state) {
				if (this.state == State.PLAYING) {
					this.state = State.PAUSED;
				}
			}
		}
	}
	
	public void resumePlayback() {
		if (this.isAlive()) {
			synchronized (this.state) {
				if (this.state == State.PAUSED) {
					this.state = State.PLAYING;
					this.state.notify();
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
				if (this.loop) {
//					seekVideo(0);
//					seekAudio(0);
//				    
					// seeke the video
					if (this.videoCoder != null) {
						this.container.seekKeyFrame(this.videoCoder.getStream().getIndex(), 0, 0, 0, 0);
						System.out.println(this.videoCoder.getStream().getStartTime());
						System.out.println(this.videoCoder.getStream().getTimeBase());
					}
					
					if (this.audioCoder != null) {
						this.container.seekKeyFrame(this.audioCoder.getStream().getIndex(), 0, 0, 0, 0);
						this.audioLine.flush();
						System.out.println(this.audioCoder.getStream().getStartTime());
						System.out.println(this.audioCoder.getStream().getTimeBase());
					}
					
					this.videoClock.reset();
					this.audioClock.reset();
					
					packet.reset();
					
					continue;
				} else {
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
						// And finally, convert the picture to an Java buffered image
//						BufferedImage target = new BufferedImage(picture.getWidth(), picture.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//						IConverter converter = ConverterFactory.createConverter(target, picture.getPixelType());
						BufferedImage image = this.videoConverter.toImage(picture);
						// sync up the frame rate
						long syncTime = this.videoClock.getSynchronizationTime(picture.getTimeStamp(), false);
						this.videoThread.notify(image, syncTime);
//						try {
//							Thread.sleep(syncTime);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						// notify any listeners
//						notifyListeners(image);
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
                    	// FIXME really we should mix the channels into 2 rather than just select 2 channels
                    	// FIXME the audio needs to be reset (i think this is blocking the loop back)
                    	// select only the left and right front channels
//                    	int nc = samples.getChannels();
//                    	byte[] rdata = new byte[data.length / nc * 2];
//                    	int p = 0;
//                    	for (int i = 0; i < data.length;) {
//                    		rdata[p++] = data[i];
//                    		if (((i + 1) % nc) == 2) {
//                    			i += nc - 1;
//                    		} else {
//                    			i++;
//                    		}
//                    	}
//                    	int nc = samples.getChannels();
//                    	ByteBuffer buf = ByteBuffer.allocate(data.length / nc * 2);
//                    	buf.order(ByteOrder.LITTLE_ENDIAN);
//                    	ByteBuffer tmp = ByteBuffer.allocate(12);
//                    	tmp.order(ByteOrder.LITTLE_ENDIAN);
//                    	for (int j = 0; j < data.length; j+=12) {
//                    		tmp.put(data, j, 12);
////                    		buf.put(data[j]);
////                    		buf.put(data[j+1]);
////                    		buf.put(data[j+2]);
////                    		buf.put(data[j+3]);
//                    		
//                    		// get the front left/right
//                    		int lf = tmp.getShort(0) & 0xFFFFFFFF;
//                    		int rf = tmp.getShort(2) & 0xFFFFFFFF;
//                    		
//                    		// get the rear left/right
//                    		int lr = tmp.getShort(4) & 0xFFFFFFFF;
//                    		int rr = tmp.getShort(6) & 0xFFFFFFFF;
//                    		
//                    		// get the center front
//                    		int cf = tmp.getShort(8) & 0xFFFFFFFF;
//                    		
//                    		// get the sub
//                    		int su = tmp.getShort(10) & 0xFFFFFFFF;
//                    				
////                    		short l = (short)(lf + 0.71 * cf + 0.71 * su - lr);
////                    		short r = (short)(rf + 0.71 * cf + 0.71 * su - rr);
//                    		
//                    		short l = (short)(lr - (rr * 0.5) + (cf * 0.5) + lf - (su * 0.5));
//                    		short r = (short)(rr - (lr * 0.5) + (cf * 0.5) + rf - (su * 0.5));
//                    		
//                    		buf.putShort(l);
//                    		buf.putShort(r);
//                    		tmp.clear();
//                    	}
//                    	this.audioLine.write(data, 0, data.length);
                    	
//                    	long syncTime = this.audioClock.getSynchronizationTime(samples.getTimeStamp(), false);
//                        try {
//							Thread.sleep(syncTime);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
                        try {
							audioThread.queue.put(AudioDownmixer.downmixToStereo(
									data, 
									(int)IAudioSamples.findSampleBitDepth(this.audioCoder.getSampleFormat()), 
									this.audioCoder.getChannels()));
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    }
                }
            }
		}
	}
	
    private void seekAudio(long seek) {
        // try to use xuggler bundled algorithm for supported codecs
        IRational rational = this.audioCoder.getTimeBase();
        long seekTime = seek * rational.getDenominator() / rational.getNumerator() / 1000;
        long audioSeekOffset = 0;
        if (container.seekKeyFrame(this.audioCoder.getStream().getIndex(), seekTime, IContainer.SEEK_FLAG_ANY) >= 0) {
            audioSeekOffset = -this.audioCoder.getStream().getStartTime() * 1000;
            return;
        }
        //xuggler doesn't have good positioning algorithm for audio. So here is my own one
        audioCoder.close();
        if (audioCoder.open(null, null) < 0) {
            throw new RuntimeException("can't open codec");
        }
        
        IPacket packet = IPacket.make();
        while(container.readNextPacket(packet) >= 0) {
            if (packet.getStreamIndex() == this.audioCoder.getStream().getIndex()) {
                break;
            }
        }
        long headerOffset = packet.getPosition();
        audioSeekOffset = seek - this.audioCoder.getStream().getStartTime();
        long bytesOffset =(audioSeekOffset * (container.getFileSize() - headerOffset)) / this.audioCoder.getStream().getDuration();
        container.seekKeyFrame(this.audioCoder.getStream().getIndex(), bytesOffset, IContainer.SEEK_FLAG_BYTE);
        audioSeekOffset *= 1000;
    }
	
	private void seekVideo(long seek) {
		 IRational rational = videoCoder.getTimeBase();
	        long seekTime = seek * rational.getDenominator() / rational.getNumerator() / 1000;
	        container.seekKeyFrame(videoCoder.getStream().getIndex(), 0, seekTime, seekTime, 0);
	        long pictureTime = seek * 1000;
	        
	        IPacket packet = IPacket.make();
	        while(container.readNextPacket(packet) >= 0) {
	            if (packet.getStreamIndex() == videoCoder.getStream().getIndex()) {
	            	IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
	                int offset = 0;
	                while(offset < packet.getSize()) {
	                    int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
	                    if (bytesDecoded < 0) {
	                    	System.out.println("problems");
//	                        throw new RuntimeException("got error decoding video in:"  + fileName);
	                    }
	                    offset += bytesDecoded;

	                    if (picture.isComplete() && picture.getTimeStamp() >= pictureTime) {
	                        return;
	                    }
	                }
	            }
	        }
	}
	
	private void notifyListeners(BufferedImage image) {
		for (MediaPlayerListener listener : this.listeners) {
			listener.updated(image);
		}
	}
	
	private class VideoThread extends Thread {
		protected BlockingDeque<BufferedImage> queue = new LinkedBlockingDeque<>();
		protected Object lock = new Object();
		protected BufferedImage image;
		protected long delay;
		
		public VideoThread() {
			super("VideoThread");
			this.setDaemon(true);
		}
		
		public void notify(BufferedImage image, long delay) {
//			queue.add(image);
			this.image = image;
			this.delay = delay;
			synchronized (lock) {
				lock.notify();
			}
		}
		
		@Override
		public void run() {
			try {
				   while(true) { 
					   synchronized (lock) {
						   lock.wait();
					   }
//					   BufferedImage image = queue.take();
					   Thread.sleep(delay);
					   notifyListeners(image);
				   }
		       } catch (InterruptedException ex) {
		    	   
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
				   byte[] data = queue.take();
//				   queue.clear();
				   audioLine.write(data, 0, data.length);
			   }
	       } catch (InterruptedException ex) {
	    	   
	       }
		}
	}
}

package org.praisenter.media.player;

import java.awt.image.BufferedImage;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.praisenter.media.AudioDownmixer;
import org.praisenter.thread.PausableThread;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * Media reader thread that reads the media and sends the data to the 
 * appropriate media player thread.
 * <p>
 * This class will read the media a fast as possible.  Therefore its recommended that the
 * receiving class of the queueXXX methods block this thread to prevent overflow of the
 * buffers and out of memory errors.
 * <p>
 * This class is not designed to be used separately from the {@link XugglerMediaPlayer} class.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class XugglerMediaReaderThread extends PausableThread {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerMediaReaderThread.class);
	
	// media objects
	
	/** The media container */
	protected IContainer container;
	
	/** The video decoder */
	protected IStreamCoder videoCoder;
	
	/** The video pixel converter */
	protected IConverter videoConverter;
	
	/** The audio decoder */
	protected IStreamCoder audioCoder;
	
	/** True if audio down mixing needs to be done */
	protected boolean downmix;
	
	// reusables
	
	/** Data packet for reading the media */
	protected IPacket packet;
	
	/** Video picture data */
	protected IVideoPicture picture;
	
	/** Audio samples data */
	protected IAudioSamples samples;
	
//	protected long lastVideoTimestamp;
//	protected long lastAudioTimestamp;
//	protected long endVideoTimestamp;
//	protected long endAudioTimestamp;
	
	/**
	 * Default constructor.
	 */
	public XugglerMediaReaderThread() {
		super("XugglerMediaReaderThread");
		
//		this.lastVideoTimestamp = -1;
//		this.lastAudioTimestamp = -1;
//		this.endVideoTimestamp = -1;
//		this.endAudioTimestamp = -1;
	}
	
	/**
	 * Initializes the reader thread with the given media.
	 * @param container the media container
	 * @param videoCoder the media video decoder
	 * @param audioCoder the media audio decoder
	 * @param downmix true if audio down mixing must be done
	 */
	public void initialize(IContainer container, IStreamCoder videoCoder, IStreamCoder audioCoder, boolean downmix) {
		// assign the local variables
		this.container = container;
		this.videoCoder = videoCoder;
		this.audioCoder = audioCoder;
		this.downmix = downmix;
		
		// create a packet for reading
		this.packet = IPacket.make();
		
		// create the image converter for the video
		if (videoCoder != null) {
			this.picture = IVideoPicture.make(this.videoCoder.getPixelType(), this.videoCoder.getWidth(), this.videoCoder.getHeight());
			BufferedImage target = new BufferedImage(this.videoCoder.getWidth(), this.videoCoder.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			this.videoConverter = ConverterFactory.createConverter(target, this.videoCoder.getPixelType());
		}
		
		// create a resuable container for the samples
		if (audioCoder != null) {
			this.samples = IAudioSamples.make(1024, this.audioCoder.getChannels());
		}
	}
	
	/**
	 * Called when the reader needs to be reset to the beginning of the media.
	 * @return boolean true if the media was reset successfully
	 */
	public boolean loop() {
//		this.endAudioTimestamp = this.lastAudioTimestamp;
//		this.endVideoTimestamp = this.lastVideoTimestamp;
		
		if (this.container != null) {
			int r = this.container.seekKeyFrame(-1, 0, this.container.getStartTime(), this.container.getStartTime(), IContainer.SEEK_FLAG_BACKWARDS);
			if (r < 0) {
				IError error = IError.make(r);
				LOGGER.error(error);
			} else {
				return true;
			}
//			if (container.seekKeyFrame(videoCoder.getStream().getIndex(), 0, 0, 0, 0) < 0) {
//				
//			}
//			// seek the audio, if available
//			if (audioCoder != null) {
//				if (container.seekKeyFrame(audioCoder.getStream().getIndex(), 0, 0, 0, 0) < 0) {
//					
//				}
//			}
//			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#end()
	 */
	@Override
	public void end() {
		super.end();
		
		this.interrupt();
		
		if (this.container != null) {
			this.container.close();
		}
		if (this.videoCoder != null) {
			this.videoCoder.close();
		}
		if (this.audioCoder != null) {
			this.audioCoder.close();
		}
		
		this.container = null;
		this.videoCoder = null;
		this.audioCoder = null;
		this.videoConverter = null;
		this.downmix = false;
		this.packet = null;
		this.picture = null;
		this.samples = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#executeTask()
	 */
	@Override
	protected void executeTask() {
		// otherwise lets read the next packet
		int r = 0;
		if ((r = this.container.readNextPacket(this.packet)) < 0) {
			// check why we got back < 0
			IError error = IError.make(r);
			if (error.getType() == IError.Type.ERROR_INTERRUPTED) {
				LOGGER.warn("Media reader thread interrupted while reading the next packet.");
				// just keep going
				return;
			} else {
				// we are at the end of the media, do we need to loop?
				this.onMediaEnd();
				return;
			}
		}
		
		// see if this packet is for the video stream
		if (this.videoCoder != null && this.packet.getStreamIndex() == this.videoCoder.getStream().getIndex()) {
			int offset = 0;
			// decode the video
			while (offset < this.packet.getSize()) {
				int bytesDecoded = this.videoCoder.decodeVideo(this.picture, this.packet, offset);
				if (bytesDecoded < 0) {
					break;
				}
				offset += bytesDecoded;

				// make sure that we have a full picture from the video first
				if (this.picture.isComplete()) {
//					System.out.println("Video " + this.picture.getTimeStamp() + " " + this.packet.getDts() * this.videoCoder.getTimeBase().getDenominator() / this.videoCoder.getTimeBase().getNumerator());
//					System.out.println("Image " + this.picture.getTimeStamp());
					// store the last timestamp read
//					this.lastVideoTimestamp = this.picture.getTimeStamp();
					// skip the frame if its past the end timestamp
//					if (this.endVideoTimestamp > 0 && this.picture.getTimeStamp() > this.endVideoTimestamp) {
//						System.out.println("Dropped image: " + this.picture.getTimeStamp());
//						continue;
//					}
					// convert the picture to an Java buffered image
					BufferedImage image = this.videoConverter.toImage(this.picture);
					this.queueVideoImage(new XugglerVideoData(this.picture.getTimeStamp(), image));
				}
			}
		}
		
		// see if this packet is for the audio stream
		if (this.audioCoder != null && this.packet.getStreamIndex() == this.audioCoder.getStream().getIndex()) {
            int offset = 0;
            while(offset < packet.getSize()) {
                int bytesDecoded = this.audioCoder.decodeAudio(this.samples, this.packet, offset);
                if (bytesDecoded < 0) {
                    break;
                }
                offset += bytesDecoded;
                if (this.samples.isComplete()) {
//                	System.out.println("Audio " + this.samples.getTimeStamp() + " " + this.packet.getDts() * this.audioCoder.getTimeBase().getDenominator() / this.audioCoder.getTimeBase().getNumerator());
//                	System.out.println("Audio: " + this.packet.getDuration() * this.audioCoder.getTimeBase().getDenominator() / this.audioCoder.getTimeBase().getNumerator() / 1000);
                	// store the last timestamp read
//                	this.lastAudioTimestamp = this.samples.getTimeStamp();
					// skip the samples if its past the end timestamp
//                	if (this.endAudioTimestamp > 0 && this.samples.getTimeStamp() > this.endAudioTimestamp) {
//                		System.out.println("Dropped samples: " + this.samples.getTimeStamp());
//                		continue;
//                	}
                	// get the sample data to send to JavaSound
                	byte[] data = this.samples.getData().getByteArray(0, this.samples.getSize());
                	// see if we need to downmix
                	if (this.downmix) {
                    	data = AudioDownmixer.downmixToStereo(
								data, 
								(int)IAudioSamples.findSampleBitDepth(this.audioCoder.getSampleFormat()), 
								this.audioCoder.getChannels(),
								ByteOrder.LITTLE_ENDIAN);
                	}
                	this.queueAudioImage(new XugglerAudioData(this.samples.getTimeStamp(), data));
                }
            }
        }
	}
	
	/**
	 * This method is called when a video image has been read and converted.
	 * @param image the video image
	 */
	protected abstract void queueVideoImage(XugglerVideoData image);
	
	/**
	 * This method is called when a block of audio samples have been read and (possibly) downmixed.
	 * @param samples the audio samples
	 */
	protected abstract void queueAudioImage(XugglerAudioData samples);
	
	/**
	 * This is called when the reader has reached the end of the media.
	 */
	protected abstract void onMediaEnd();
}

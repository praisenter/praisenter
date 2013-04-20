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

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.praisenter.common.threading.PausableThread;
import org.praisenter.media.AudioDownmixer;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
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
 * @version 2.0.1
 * @since 2.0.0
 */
public abstract class XugglerMediaReaderThread extends PausableThread {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerMediaReaderThread.class);
	
	// config
	
	/** The media output width */
	protected int outputWidth;
	
	/** The media output height */
	protected int outputHeight;
	
	/** True if video frames should be converted */
	protected boolean videoConversionEnabled;
	
	/** The media width */
	private int width;
	
	/** The media height */
	private int height;
	
	/** True if the media should be scaled to the output dimensions */
	private boolean scale;
	
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

	/**
	 * Default constructor.
	 */
	public XugglerMediaReaderThread() {
		super("XugglerMediaReaderThread");
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
		this.outputWidth = 0;
		this.outputHeight = 0;
		this.videoConversionEnabled = false;
		this.scale = false;
		this.container = container;
		this.videoCoder = videoCoder;
		this.audioCoder = audioCoder;
		this.downmix = downmix;
		
		// create a packet for reading
		this.packet = IPacket.make();
		
		// create the image converter for the video
		if (videoCoder != null) {
			this.width = this.videoCoder.getWidth();
			this.height = this.videoCoder.getHeight();
			IPixelFormat.Type type = this.videoCoder.getPixelType();
			this.picture = IVideoPicture.make(type, this.width, this.height);
			BufferedImage target = new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);
			this.videoConverter = ConverterFactory.createConverter(target, type);
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
		if (this.container != null) {
			int r = this.container.seekKeyFrame(-1, 0, this.container.getStartTime(), this.container.getStartTime(), IContainer.SEEK_FLAG_BACKWARDS | IContainer.SEEK_FLAG_ANY);
			if (r < 0) {
				IError error = IError.make(r);
				LOGGER.error(error);
			} else {
				return true;
			}
//			if (this.videoCoder != null) {
//				int r = this.container.seekKeyFrame(
//						this.videoCoder.getStream().getIndex(), 
//						this.videoCoder.getStream().getStartTime(), 
//						this.videoCoder.getStream().getStartTime(), 
//						0, 
//						IContainer.SEEK_FLAG_BACKWARDS | IContainer.SEEK_FLAG_ANY);
//				if (r < 0) {
//					IError error = IError.make(r);
//					LOGGER.error(error);
//					return false;
//				}
//			} else
//			// seek the audio, if available
//			if (audioCoder != null) {
//				int r = this.container.seekKeyFrame(
//						this.audioCoder.getStream().getIndex(), 
//						this.audioCoder.getStream().getStartTime(), 
//						this.audioCoder.getStream().getStartTime(), 
//						0, 
//						IContainer.SEEK_FLAG_BACKWARDS | IContainer.SEEK_FLAG_ANY);
//				if (r < 0) {
//					IError error = IError.make(r);
//					LOGGER.error(error);
//					return false;
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
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.threading.PausableThread#onThreadStopped()
	 */
	@Override
	protected void onThreadStopped() {
		super.onThreadStopped();
		
		if (this.videoCoder != null) {
			this.videoCoder.close();
		}
		if (this.audioCoder != null) {
			this.audioCoder.close();
		}
		if (this.container != null) {
			this.container.close();
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
					
					// check if a conversion will be made between the image type and the device
					GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice gdev = genv.getDefaultScreenDevice();
					GraphicsConfiguration gconf = gdev.getDefaultConfiguration();
					// we are going to convert the image if the sample model is not the same or 
					// if we need to scale the image down.  We do this here since the reader thread
					// is always way faster than the player
					if (this.videoConversionEnabled && (!gconf.getColorModel().isCompatibleSampleModel(image.getSampleModel()) || this.scale)) {
						int width = image.getWidth();
						int height = image.getHeight();
						// do we need to scale
						if (this.scale) {
							// if so, use the output width/height
							width = this.outputWidth;
							height = this.outputHeight;
						}
						BufferedImage cimage = gconf.createCompatibleImage(width, height);
						Graphics2D g = cimage.createGraphics();
						if (this.scale) {
							// make sure we scale the image along with converting it
							g.drawImage(image, 0, 0, width, height, null);
						} else {
							// only do the image conversion
							g.drawImage(image, 0, 0, null);
						}
						g.dispose();
						// use the converted image
						image = cimage;
					}
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.common.threading.PausableThread#onThreadResume()
	 */
	@Override
	protected void onThreadResume() {
		if (this.scale && this.videoConversionEnabled) {
			LOGGER.debug("Scaling video from " + this.width + "x" + this.height + " to " + this.outputWidth + "x" + this.outputHeight);
		}
	}
	
	/**
	 * Sets the output dimensions of the media reader.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @since 2.0.1
	 */
	public void setOutputDimensions(int width, int height) {
		this.outputWidth = width;
		this.outputHeight = height;
		
		// we want to scale down the image sent to the video player to reduce memory but only
		// when the output size is smaller than the video size.  We do not want to scale up
		// the image as this is actually slower than passing a small image down to the graphics
		// hardware and letting it scale it.
		this.scale = width < this.width || height < this.height;
	}
	
	/**
	 * Toggles the conversion of color space and size during reading of
	 * video frames.
	 * @param flag true if conversion should be performed
	 * @since 2.0.1
	 */
	public void setVideoConversionEnabled(boolean flag) {
		this.videoConversionEnabled = flag;
	}
}

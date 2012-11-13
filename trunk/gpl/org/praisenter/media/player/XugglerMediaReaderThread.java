package org.praisenter.media.player;

import java.awt.image.BufferedImage;
import java.nio.ByteOrder;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.praisenter.media.AudioDownmixer;
import org.praisenter.thread.PausableThread;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IIndexEntry;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public abstract class XugglerMediaReaderThread extends PausableThread {
	// container
	
	protected IContainer container;
	
	// video
	
	protected IStreamCoder videoCoder;
	protected IConverter videoConverter;
	
	// audio
	
	protected IStreamCoder audioCoder;
	
	// reusables
	protected IPacket packet = IPacket.make();
	protected IVideoPicture picture;
	protected IAudioSamples samples;
	
	protected long lastVideoTimestamp;
	protected long lastAudioTimestamp;
	protected long endVideoTimestamp;
	protected long endAudioTimestamp;
	
	public XugglerMediaReaderThread() {
		super("XugglerMediaReaderThread");
		
		this.lastVideoTimestamp = -1;
		this.lastAudioTimestamp = -1;
		this.endVideoTimestamp = -1;
		this.endAudioTimestamp = -1;
	}
	
	public void initialize(IContainer container, IStreamCoder videoCoder, IStreamCoder audioCoder) {
		// get the Xuggler objects from the media
		this.container = container;
		this.videoCoder = videoCoder;
		this.audioCoder = audioCoder;
		
		// create the image converter for the video
		if (videoCoder != null) {
			this.picture = IVideoPicture.make(this.videoCoder.getPixelType(), this.videoCoder.getWidth(), this.videoCoder.getHeight());
			BufferedImage target = new BufferedImage(this.videoCoder.getWidth(), this.videoCoder.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			this.videoConverter = ConverterFactory.createConverter(target, this.videoCoder.getPixelType());
			
//			System.out.println("Indexes: " + videoCoder.getStream().getNumIndexEntries());
//			List<IIndexEntry> entries = videoCoder.getStream().getIndexEntries();
//			for (IIndexEntry entry : entries) {
//				System.out.println("pos = " + entry.getPosition() + " time = " + entry.getTimeStamp() + " iskey = " + entry.isKeyFrame());
//			}
		}
		
		// get a JavaSound audio line for the audio
		if (audioCoder != null) {
			this.samples = IAudioSamples.make(1024, this.audioCoder.getChannels());
		}
	}
	
	public void loop() {
		this.endAudioTimestamp = this.lastAudioTimestamp;
		this.endVideoTimestamp = this.lastVideoTimestamp;
		
		if (this.container != null) {
			this.container.seekKeyFrame(-1, 0, 0, 0, IContainer.SEEK_FLAG_ANY | IContainer.SEEK_FLAG_BACKWARDS);
//			if (this.audioCoder != null) {
//				this.container.seekKeyFrame(this.audioCoder.getStream().getIndex(), 0, 0, 0, IContainer.SEEK_FLAG_ANY);
//			}
//			if (this.videoCoder != null) {
//				this.container.seekKeyFrame(this.videoCoder.getStream().getIndex(), 0, 0, 0, IContainer.SEEK_FLAG_ANY);
//			}
		}
	}
	
	@Override
	protected void executeTask() {
		// otherwise lets read the next packet
		int r = 0;
		if ((r = this.container.readNextPacket(this.packet)) < 0) {
			// we are at the end of the media, do we need to loop?
			this.onStreamEnd();
			return;
		}
		
		// make sure the packet belongs to the stream we care about
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
//					if (firstTimeStamp < 0) {
//						firstTimeStamp = this.picture.getTimeStamp();
//					}
					if (this.endVideoTimestamp < 0 || this.endVideoTimestamp > this.picture.getTimeStamp()) {
						// convert the picture to an Java buffered image
						BufferedImage image = this.videoConverter.toImage(this.picture);
						this.lastVideoTimestamp = this.picture.getTimeStamp();
						System.out.println("Image " + this.picture.getTimeStamp());
						this.queueVideoImage(new XugglerTimedData<BufferedImage>(this.picture.getTimeStamp(), image));
					} else {
						System.out.println("Dropped image: " + this.picture.getTimeStamp());
					}
				}
			}
		}
		
		if (this.audioCoder != null && this.packet.getStreamIndex() == this.audioCoder.getStream().getIndex()) {
            int offset = 0;
            while(offset < packet.getSize()) {
                int bytesDecoded = this.audioCoder.decodeAudio(this.samples, this.packet, offset);
                if (bytesDecoded < 0) {
                    break;
                }
                offset += bytesDecoded;
                if (this.samples.isComplete()) {
//                	if (firstTimeStamp < 0) {
//						firstTimeStamp = samples.getTimeStamp();
//					}
                	if (this.endAudioTimestamp < 0 || this.endAudioTimestamp > this.samples.getTimeStamp()) {
                    	// get the sample data to send to JavaSound
                    	byte[] data = this.samples.getData().getByteArray(0, this.samples.getSize());
                    	// TODO see if we need to resample
                    	data = AudioDownmixer.downmixToStereo(
								data, 
								(int)IAudioSamples.findSampleBitDepth(this.audioCoder.getSampleFormat()), 
								this.audioCoder.getChannels(),
								ByteOrder.LITTLE_ENDIAN);
                    	System.out.println("Audio " + this.samples.getTimeStamp());
                    	this.lastAudioTimestamp = this.samples.getTimeStamp();
                    	this.queueAudioImage(new XugglerTimedData<byte[]>(this.samples.getTimeStamp(), data));
                	} else {
                		System.out.println("Dropped samples: " + this.samples.getTimeStamp());
                	}
                }
            }
        }
	}
	
	protected abstract void queueVideoImage(XugglerTimedData<BufferedImage> image);
	protected abstract void queueAudioImage(XugglerTimedData<byte[]> samples);
	protected abstract void onStreamEnd();
}

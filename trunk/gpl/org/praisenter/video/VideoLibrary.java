package org.praisenter.video;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class VideoLibrary {
	private static final Map<String, Video> videos = new HashMap<>();
	
	public static synchronized Video getVideo(String filename) {
		
		Video video = videos.get(filename);
		if (video == null) {
			video = new Video();
			
			video.file = filename;
			// load the first frame of the video
			video.firstFrame = getFirstFrame(filename);
			
			videos.put(filename, video);
		}
		
		return video;
	}
	
	private static BufferedImage getFirstFrame(String url) {
		BufferedImage image = null;
		
		// create the video container object
		IContainer container = IContainer.make();

		// open the container format
		if (container.open(url, IContainer.Type.READ, null) < 0) {
			throw new IllegalArgumentException("could not open file: " + url);
		}
		System.out.println("Container format: " + container.getContainerFormat().getInputFormatLongName());

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();

		// loop over the streams to find the first video stream
		int videoStreamId = -1;
		IStreamCoder videoCoder = null;
		for (int i = 0; i < numStreams; i++) {
			IStream stream = container.getStream(i);
			// get the coder for the stream
			IStreamCoder coder = stream.getStreamCoder();
			// see if the coder is a video coder
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				// if so, break from the loop
				videoStreamId = i;
				videoCoder = coder;
				System.out.println("Video codec: " + coder.getCodec().getLongName());
				break;
			}
		}

		// make sure we found a video stream
		if (videoStreamId == -1) {
			throw new RuntimeException("could not find video stream in container: " + url);
		}

		// open the coder to read the video data
		if (videoCoder.open(null, null) < 0) {
			throw new RuntimeException("could not open video decoder for container: " + url);
		}

		// walk through each packet of the container format
		IPacket packet = IPacket.make();
//		long firstTimestampInStream = Global.NO_PTS;
//		long systemClockStartTime = 0;
		while (container.readNextPacket(packet) >= 0) {
			// make sure the packet belongs to the stream we care about
			if (packet.getStreamIndex() == videoStreamId) {
				// create a new picture for the video data to be stored in
				IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
				int offset = 0;
				// decode the video
				while (offset < packet.getSize()) {
					int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
					if (bytesDecoded < 0) {
						throw new RuntimeException("got error decoding video in: " + url);
					}
					offset += bytesDecoded;

					// make sure that we have a full picture from the video first
					if (picture.isComplete()) {
//						/**
//						 * We could just display the images as quickly as we
//						 * decode them, but it turns out we can decode a lot
//						 * faster than you think.
//						 * 
//						 * So instead, the following code does a poor-man's
//						 * version of trying to match up the frame-rate
//						 * requested for each IVideoPicture with the system
//						 * clock time on your computer.
//						 * 
//						 * Remember that all Xuggler IAudioSamples and
//						 * IVideoPicture objects always give timestamps in
//						 * Microseconds, relative to the first decoded item. If
//						 * instead you used the packet timestamps, they can be
//						 * in different units depending on your IContainer, and
//						 * IStream and things can get hairy quickly.
//						 */
//						if (firstTimestampInStream == Global.NO_PTS) {
//							// This is our first time through
//							firstTimestampInStream = picture.getTimeStamp();
//							// get the starting clock time so we can hold up frames until the right time.
//							systemClockStartTime = System.currentTimeMillis();
//						} else {
//							long systemClockCurrentTime = System.currentTimeMillis();
//							long millisecondsClockTimeSinceStartofVideo = systemClockCurrentTime - systemClockStartTime;
//							// compute how long for this frame since the first frame in the stream.
//							// remember that IVideoPicture and IAudioSamples timestamps are always in MICROSECONDS,
//							// so we divide by 1000 to get milliseconds.
//							long millisecondsStreamTimeSinceStartOfVideo = (picture.getTimeStamp() - firstTimestampInStream) / 1000;
//							// and we give ourselves 50 ms of tolerance
//							final long millisecondsTolerance = 50;
//							final long millisecondsToSleep = (millisecondsStreamTimeSinceStartOfVideo - (millisecondsClockTimeSinceStartofVideo + millisecondsTolerance));
//							if (millisecondsToSleep > 0) {
//								try {
//									Thread.sleep(millisecondsToSleep);
//								} catch (InterruptedException e) {
//									// we might get this when the user closes
//									// the dialog box, so
//									// just return from the method.
//									return null;
//								}
//							}
//						}

						// And finally, convert the picture to an Java buffered image
						BufferedImage target = new BufferedImage(picture.getWidth(), picture.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
						System.out.println("Video pixel format: " + picture.getPixelType());
						IConverter converter = ConverterFactory.createConverter(target, picture.getPixelType());
						image = converter.toImage(picture);
						break;
					}
				}
				
				if (image != null) {
					break;
				}
			} else {
//				/*
//				 * This packet isn't part of our video stream, so we just
//				 * silently drop it.
//				 */
//				do {
//				} while (false);
			}

		}
		/*
		 * Technically since we're exiting anyway, these will be cleaned up by
		 * the garbage collector... but because we're nice people and want to be
		 * invited places for Christmas, we're going to show how to clean up.
		 */
		if (videoCoder != null) {
			videoCoder.close();
			videoCoder = null;
		}
		if (container != null) {
			container.close();
			container = null;
		}
		return image;
	}
}

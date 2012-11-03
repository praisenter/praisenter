package org.praisenter.media;

import java.awt.Dimension;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.Media;
import org.praisenter.media.PlayableMedia;
import org.praisenter.media.Thumbnail;
import org.praisenter.utilities.ImageUtilities;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class VideoMedia extends AbstractVideoMedia implements Media, PlayableMedia {
	protected BufferedImage firstFrame;
	protected BufferedImage currentFrame;
	
	// video play properties
	
	protected List<PlayableMediaListener> listeners;
	protected int videoStreamId;
	protected IContainer container;
	protected IStreamCoder videoCoder;
	
	protected Object lock = new Object();
	protected VideoPlayerThread thread;
	
	// TODO audio
//	protected IStreamCoder audioCoder;
	
	public VideoMedia(FileProperties fileProperties, BufferedImage firstFrame, IContainer container, IStreamCoder videoCoder, int videoStreamId) {
		super(fileProperties);
		this.firstFrame = firstFrame;
		this.currentFrame = firstFrame;
		this.listeners = new ArrayList<PlayableMediaListener>();
		this.container = container;
		this.videoCoder = videoCoder;
		this.videoStreamId = videoStreamId;
		this.thread = new VideoPlayerThread();
	}

	@Override
	public void play() {
		if (!this.thread.isAlive()) this.thread.start();
		this.thread.playing = true;
		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void stop() {
		this.thread.playing = false;
	}

	@Override
	public void pause() {
		this.thread.playing = false;
	}

	@Override
	public void seek(long position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BufferedImage getCurrentFrame() {
		return this.currentFrame;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.AbstractVideoMedia#getFirstFrame()
	 */
	@Override
	public BufferedImage getFirstFrame() {
		return this.firstFrame;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public Thumbnail getThumbnail(Dimension size) {
		// resize the image to a thumbnail size
		BufferedImage image = ImageUtilities.getUniformScaledImage(this.firstFrame, size.width, size.height, AffineTransformOp.TYPE_BILINEAR);
		// return the thumbnail
		return new Thumbnail(this.fileProperties, this.type, image);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.PlayableMedia#addMediaListener(org.praisenter.media.PlayableMediaListener)
	 */
	@Override
	public void addMediaListener(PlayableMediaListener listener) {
		this.listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.PlayableMedia#removeMediaListener(org.praisenter.media.PlayableMediaListener)
	 */
	@Override
	public boolean removeMediaListener(PlayableMediaListener listener) {
		return this.listeners.remove(listener);
	}
	
	private class VideoPlayerThread extends Thread {
		protected boolean playing;
		
		public VideoPlayerThread() {
			super("VideoPlayerThread");
			this.setDaemon(true);
			this.setPriority(Thread.MAX_PRIORITY);
			this.playing = false;
		}
		
		@Override
		public void run() {
			long firstTimestampInStream = Global.NO_PTS;
		    long systemClockStartTime = 0;
		 // once we are playing, then we can continue to loop over the stream
			// walk through each packet of the container format
			IPacket packet = IPacket.make();
			System.out.println("0");
			while (true) {
				// check if we are playing
				if (!playing) {
					// obtain the lock
					synchronized (lock) {
						try {
							// wait until we are notified
							System.out.println("waiting");
							lock.wait();
						} catch (InterruptedException e) {
							break;
						}
					}
				}
				
				if (container.readNextPacket(packet) >= 0) {
//					// reset the container to loop
//					container.seekKeyFrame(videoStreamId, 0, 0);
//					firstTimestampInStream = Global.NO_PTS;
//					systemClockStartTime = 0;
//				} else {
					// make sure the packet belongs to the stream we care about
					if (packet.getStreamIndex() == videoStreamId) {
						// create a new picture for the video data to be stored in
						IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
						int offset = 0;
						// decode the video
						while (offset < packet.getSize()) {
							int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
							if (bytesDecoded < 0) {
								// FIXME error
//									throw new RuntimeException("got error decoding video in: " + url);
								break;
							}
							offset += bytesDecoded;

							// make sure that we have a full picture from the video first
							if (picture.isComplete()) {
								/**
								 * We could just display the images as quickly as we
								 * decode them, but it turns out we can decode a lot
								 * faster than you think.
								 * 
								 * So instead, the following code does a poor-man's
								 * version of trying to match up the frame-rate
								 * requested for each IVideoPicture with the system
								 * clock time on your computer.
								 * 
								 * Remember that all Xuggler IAudioSamples and
								 * IVideoPicture objects always give timestamps in
								 * Microseconds, relative to the first decoded item. If
								 * instead you used the packet timestamps, they can be
								 * in different units depending on your IContainer, and
								 * IStream and things can get hairy quickly.
								 */
								if (firstTimestampInStream == Global.NO_PTS) {
									// This is our first time through
									firstTimestampInStream = picture.getTimeStamp();
									// get the starting clock time so we can hold up frames until the right time.
									systemClockStartTime = System.currentTimeMillis();
								} else {
									long systemClockCurrentTime = System.currentTimeMillis();
									long millisecondsClockTimeSinceStartofVideo = systemClockCurrentTime - systemClockStartTime;
									// compute how long for this frame since the first frame in the stream.
									// remember that IVideoPicture and IAudioSamples timestamps are always in MICROSECONDS,
									// so we divide by 1000 to get milliseconds.
									long millisecondsStreamTimeSinceStartOfVideo = (picture.getTimeStamp() - firstTimestampInStream) / 1000;
									// and we give ourselves 50 ms of tolerance
									final long millisecondsTolerance = 50;
									final long millisecondsToSleep = (millisecondsStreamTimeSinceStartOfVideo - (millisecondsClockTimeSinceStartofVideo + millisecondsTolerance));
									if (millisecondsToSleep > 0) {
										try {
											Thread.sleep(millisecondsToSleep);
										} catch (InterruptedException e) {
											// we might get this when the user closes
											// the dialog box, so
											// just return from the method.
											// FIXME handle this
											e.printStackTrace();
											return;
										}
									}
								}

								// And finally, convert the picture to an Java buffered image
								BufferedImage target = new BufferedImage(picture.getWidth(), picture.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//								System.out.println("Video pixel format: " + picture.getPixelType());
								IConverter converter = ConverterFactory.createConverter(target, picture.getPixelType());
								currentFrame = converter.toImage(picture);
								notifyListeners();
							}
						}
					}
				} else {
					System.out.println("end");
					container.seekKeyFrame(videoStreamId, 1, IContainer.SEEK_FLAG_FRAME);
					firstTimestampInStream = Global.NO_PTS;
					packet = IPacket.make();
				}
//					/*
//					 * Technically since we're exiting anyway, these will be cleaned up by
//					 * the garbage collector... but because we're nice people and want to be
//					 * invited places for Christmas, we're going to show how to clean up.
//					 */
//					if (videoCoder != null) {
//						videoCoder.close();
//						videoCoder = null;
//					}
//					if (container != null) {
//						container.close();
//						container = null;
//					}
			}
		}
	}
	
	private void notifyListeners() {
		for (PlayableMediaListener listener : this.listeners) {
			listener.updated();
		}
	}
}

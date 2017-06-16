/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.media;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.InvalidFormatException;
import org.praisenter.ThumbnailSettings;
import org.praisenter.utility.ImageManipulator;

import io.humble.video.Codec;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerFormat;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

/**
 * {@link MediaLoader} that loads video media.
 * @author William Bittle
 * @version 3.0.0
 */
public final class VideoMediaLoader extends AbstractMediaLoader implements MediaLoader {
	/** The class-level logger */
	private static Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param thumbnailSettings the thumbnail settings
	 */
	public VideoMediaLoader(ThumbnailSettings thumbnailSettings) {
		super(thumbnailSettings);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String mimeType) {
		if (mimeType != null && mimeType.contains("video")) {
			// handle any mimetype
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.nio.file.Path)
	 */
	@Override
	public Media load(Path path) throws IOException, FileNotFoundException, InvalidFormatException {
		LOGGER.debug("Video media '{}' loading", path);
		if (Files.exists(path) && Files.isRegularFile(path)) {
			Demuxer demuxer = null;
			try {
				demuxer = Demuxer.make();
				demuxer.open(path.toString(), null, false, true, null, null);
				
				final DemuxerFormat format = demuxer.getFormat();
				final long length = demuxer.getDuration() / 1000 / 1000;
				
				MediaCodec video = null;
				MediaCodec audio = null;
				int width = 0;
				int height = 0;
				BufferedImage image = null;
				BufferedImage thumb = null;
				
				final int streams = demuxer.getNumStreams();
				for (int i = 0; i < streams; i++) {
					final DemuxerStream stream = demuxer.getStream(i);
					final Decoder decoder = stream.getDecoder();
					if (video == null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {
						// get the width and height
						width = decoder.getWidth();
						height = decoder.getHeight();
						// get the codec
						final Codec codec = decoder.getCodec();
						video = new MediaCodec(CodecType.VIDEO, codec.getName(), codec.getLongName());
						// attempt to read the first image of the stream
						try {
							LOGGER.debug("Video media '{}' - searching for best frame.", path);
							image = readBestFrame(path, demuxer, decoder, i);
							LOGGER.debug("Video media '{}' - creating thumbnail.", path);
							thumb = createThumbnail(image);
							
							// draw on the thumb nail to make it look like
							// a piece of film
							drawFilmOnFrame(thumb);
						} catch (Exception e) {
							LOGGER.warn("Failed to read first frame of video '" + path.toAbsolutePath().toString() + ": " + e.getMessage(), e);
							image = null;
							thumb = null;
						}
					}
					if (audio == null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_AUDIO) {
						final Codec codec = decoder.getCodec();
						audio = new MediaCodec(CodecType.AUDIO, codec.getName(), codec.getLongName());
					}
				}
				
				// we must have a video, audio is optional
				if (video == null) {
					LOGGER.error("No video stream present on file: '{}'", path.toAbsolutePath().toString());
					throw new NoVideoInMediaException(path.toAbsolutePath().toString());
				}
				
				final MediaCodec[] codecs;
				if (audio != null) {
					codecs = new MediaCodec[] { video, audio };
				} else {
					codecs = new MediaCodec[] { video };
				}
				
				final MediaFormat mf = new MediaFormat(format.getName().toLowerCase(), format.getLongName(), codecs);
				final Media media = Media.forVideo(path, mf, width, height, length, audio != null, null, thumb, image);
				LOGGER.debug("Video media '{}' loaded", path);
				return media;
			} catch (InterruptedException ex) {
				throw new IOException(ex);
			} finally {
				if (demuxer != null) {
					try {
						demuxer.close();
					} catch (Exception e) {
						// just eat them
						LOGGER.warn("Failed to close demuxer on: '{}': {}", path.toAbsolutePath().toString(), e.getMessage());
					}
				}
			}
		} else {
			throw new FileNotFoundException(path.toAbsolutePath().toString());
		}
	}
	
	/**
	 * Returns a frame of the video that would be "best" for a thumbnail and frame.
	 * <p>
	 * Ideally this method will return a frame of the video that is not too light or dark with
	 * enough detail to allow easy recognition.
	 * @param path the path to the media
	 * @param demuxer the demuxer
	 * @param decoder the decoder
	 * @param streamIndex the stream index
	 * @return BufferedImage
	 * @throws IOException if an IO error occurs
	 * @throws InterruptedException if reading of a packet from the file is interrupted
	 */
	private static final BufferedImage readBestFrame(Path path, Demuxer demuxer, Decoder decoder, int streamIndex) throws IOException, InterruptedException {
		
		// we are using a lightness metric to determine the "best" frame for a video
		// we will attempt to examine [max] number of frames before giving up and taking
		// the best one
		// if the video container doesn't support seeking then we'll just take the best
		// one of the first [max] number of frames
		
		decoder.open(null, null);
		
	    final MediaPicture picture = MediaPicture.make(
	    		decoder.getWidth(),
	            decoder.getHeight(),
	            decoder.getPixelFormat());
	    
	    final MediaPictureConverter converter = 
	            MediaPictureConverterFactory.createConverter(
	                MediaPictureConverterFactory.HUMBLE_BGR_24,
	                decoder.getPixelFormat(),
	                decoder.getWidth(),
	                decoder.getHeight(),
	                decoder.getWidth(),
	                decoder.getHeight());
	    
        final MediaPacket packet = MediaPacket.make();
        BufferedImage image = null;
        BufferedImage highest = null;
        
        // lightness will be the metric choosen
        double lightness = 0;
        // the maximum frames to inspect
        int max = 20;
        
        // duration in microseconds
        long duration = demuxer.getDuration();
        // look at roughly 20 frames of the video
        long increment = duration / max;
        long seekpos = 0;
        
        int iterations = 0;
        boolean seekFailed = false;
        
		while (demuxer.read(packet) >= 0) {
			/**
			 * Now we have a packet, let's see if it belongs to our video stream
			 */
			if (packet.getStreamIndex() == streamIndex) {
				/**
				 * A packet can actually contain multiple sets of samples (or
				 * frames of samples in decoding speak). So, we may need to call
				 * decode multiple times at different offsets in the packet's
				 * data. We capture that here.
				 */
				int offset = 0;
				int bytesRead = 0;
				do {
					bytesRead += decoder.decode(picture, packet, offset);
					if (picture.isComplete()) {
						iterations++;
						image = converter.toImage(image, picture);
						double l = ImageManipulator.getLogAverageLuminance(image);
						
						// keep the one closest to the middle
						double distance = Math.abs(l - 0.5);
						if (distance < lightness || highest == null) {
							lightness = distance;
							// swap the storage
							BufferedImage temp = highest;
							highest = image;
							image = temp;
						}
						
						if (!seekFailed) {
							seekpos += increment;
							if (seekpos >= duration) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("Best distance for '" + path.toAbsolutePath().toString() + "' is " + lightness + " at " + picture.getFormattedTimeStamp());
								}
								return highest;
							}
					        int r = demuxer.seek(-1, 0, seekpos, duration, 0);
					        if (r < 0) {
					        	seekFailed = true;
					        	LOGGER.warn("Seek failed for '" + path.toAbsolutePath().toString() + "' with error code: " + r);
					        }
					        decoder.flush();
						}
						
						if (iterations > max) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Best distance for '" + path.toAbsolutePath().toString() + "' is " + lightness + " at " + picture.getFormattedTimeStamp());
							}
							return highest;
						}
					}
					offset += bytesRead;
				} while (offset < packet.getSize());
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Best distance for '" + path.toAbsolutePath().toString() + "' is " + lightness + " at " + picture.getFormattedTimeStamp());
		}
		return highest;
	}
	
	/**
	 * Draws onto the given image to make it look like film.
	 * @param image the image to draw on
	 */
	private void drawFilmOnFrame(BufferedImage image) {
		final int w = image.getWidth();
		final int h = image.getHeight();
		// FEATURE (L) Make video "film" settings dependent on size of thumbnails
		final int lineWidth = 2;
		final int edgeWidth = 5;
		final int blockHeight = 5;
		final int dividerWidth = 4;
		final int n = h / (blockHeight + dividerWidth);
		final int s = (h - n * (blockHeight + dividerWidth) + dividerWidth) / 2;
		
		Graphics2D g = image.createGraphics();
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, w, lineWidth);
		g.fillRect(0, h - lineWidth, w, lineWidth);
		g.drawRect(lineWidth * 2 + edgeWidth, lineWidth, w - (lineWidth * 2 + edgeWidth) * 2 - 1, h - lineWidth * 2 - 1);
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, lineWidth * 2 + edgeWidth, h);
		g.fillRect(w - lineWidth * 2 - edgeWidth, 0, lineWidth * 2 + edgeWidth, h);
		
		g.setBackground(new Color(0,0,0,0));
		for (int i = 0; i < n; i++) {
			int y = s + (blockHeight + dividerWidth) * i;
			g.clearRect(lineWidth, y, edgeWidth, blockHeight);
			g.clearRect(w - lineWidth - edgeWidth, y, edgeWidth, blockHeight);
		}
		
		g.dispose();
	}
}

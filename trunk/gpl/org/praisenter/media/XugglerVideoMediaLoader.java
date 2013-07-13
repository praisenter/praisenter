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
package org.praisenter.media;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * Media loader for videos using the Xuggler API.
 * @author William Bittle
 * @version 2.0.2
 * @since 2.0.0
 */
public class XugglerVideoMediaLoader implements VideoMediaLoader {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerVideoMediaLoader.class);
	
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
	 * @see org.praisenter.media.MediaLoader#getSupportedFormats()
	 */
	@Override
	public List<Pair<String, String>> getSupportedContainerFormats() {
		List<Pair<String, String>> out = new ArrayList<Pair<String, String>>();
		Collection<IContainerFormat> formats = IContainerFormat.getInstalledInputFormats();
		for (IContainerFormat format : formats) {
			out.add(Pair.of(format.getInputFormatShortName(), format.getInputFormatLongName()));
		}
		return out;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#getMediaType()
	 */
	@Override
	public Class<XugglerVideoMedia> getMediaType() {
		return XugglerVideoMedia.class;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String, java.lang.String)
	 */
	@Override
	public XugglerVideoMedia load(String basePath, String filePath) throws MediaException {
		// create the video container object
		IContainer container = IContainer.make();

		// open the container format
		if (container.open(filePath, IContainer.Type.READ, null) < 0) {
			LOGGER.error("Could not open container: [" + filePath + "].");
			throw new UnsupportedMediaException();
		}
		// convert to seconds
		long length = container.getDuration() / 1000 / 1000;
		String format = container.getContainerFormat().getInputFormatLongName() + " [";
		LOGGER.debug("Video file opened. Container format: " + container.getContainerFormat().getInputFormatLongName());

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		LOGGER.debug("Stream count: " + numStreams);

		// loop over the streams to find the first video stream
		IStreamCoder videoCoder = null;
		IStreamCoder audioCoder = null;
		for (int i = 0; i < numStreams; i++) {
			IStream stream = container.getStream(i);
			// get the coder for the stream
			IStreamCoder coder = stream.getStreamCoder();
			// see if the coder is a video coder
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				// if so, break from the loop
				videoCoder = coder;
			}
			// check for an audio stream
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				audioCoder = coder;
			}
		}
		
		// make sure we have a video stream
		if (videoCoder == null) {
			LOGGER.error("No video coder found in container: [" + filePath + "].");
			throw new UnsupportedMediaException();
		}

		// open the coder to read the video data
		int videoWidth = videoCoder.getWidth();
		int videoHeight = videoCoder.getHeight();		
		String codecName = "Unknown";
		ICodec codec = videoCoder.getCodec();
		if (codec != null) {
			codecName = codec.getLongName();
		}
		format += codecName;
		if (videoCoder.open(null, null) < 0) {
			LOGGER.error("Could not open coder with codec name: [" + codecName + "].");
			throw new UnsupportedMediaException();
		}		
		LOGGER.debug("Video coder opened with format: " + codecName);
		
		// check audio
		boolean hasAudio = false;
		if (audioCoder != null) {
			hasAudio = true;
			format += " " + audioCoder.getCodec().getLongName();
		}
		
		format += "]";
		
		// get the first frame of the video
		BufferedImage firstFrame = loadFirstFrame(container, videoCoder);
		LOGGER.debug("First frame read");
		
		VideoMediaFile file = new VideoMediaFile(
				basePath,
				filePath,
				format,
				videoWidth,
				videoHeight,
				length,
				hasAudio);
		
		// create the media object
		XugglerVideoMedia media = new XugglerVideoMedia(file, firstFrame);
		
		// clean up
		if (videoCoder != null) {
			videoCoder.close();
		}
		if (container != null) {
			container.close();
		}
		
		// return the media
		return media;
	}
	
	/**
	 * Loads the first frame of the given video and then seeks back to the beginning of the stream.
	 * @param container the video container
	 * @param videoCoder the video stream coder
	 * @return BufferedImage
	 * @throws MediaException thrown if an error occurs during decoding
	 */
	private BufferedImage loadFirstFrame(IContainer container, IStreamCoder videoCoder) throws MediaException {
		// walk through each packet of the container format
		IPacket packet = IPacket.make();
		while (container.readNextPacket(packet) >= 0) {
			// make sure the packet belongs to the stream we care about
			if (packet.getStreamIndex() == videoCoder.getStream().getIndex()) {
				// create a new picture for the video data to be stored in
				IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
				int offset = 0;
				// decode the video
				while (offset < packet.getSize()) {
					int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
					if (bytesDecoded < 0) {
						LOGGER.error("No bytes found in container.");
						throw new MediaException();
					}
					offset += bytesDecoded;

					// make sure that we have a full picture from the video first
					if (picture.isComplete()) {
						// convert the picture to an Java buffered image
						BufferedImage target = new BufferedImage(picture.getWidth(), picture.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
						IConverter converter = ConverterFactory.createConverter(target, picture.getPixelType());
						return converter.toImage(picture);
					}
				}
			}
		}
		
		return null;
	}
}

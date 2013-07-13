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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Media loader for audio using the Xuggler API.
 * @author William Bittle
 * @version 2.0.2
 * @since 2.0.0
 */
public class XugglerAudioMediaLoader implements AudioMediaLoader {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerAudioMediaLoader.class);
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String mimeType) {
		if (mimeType != null && mimeType.contains("audio")) {
			// ffmpeg/xuggler does not support midi
			if (mimeType.contains("midi")) {
				return false;
			}
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
	public Class<XugglerAudioMedia> getMediaType() {
		return XugglerAudioMedia.class;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String, java.lang.String)
	 */
	@Override
	public XugglerAudioMedia load(String basePath, String filePath) throws MediaException {
		// create the video container object
		IContainer container = IContainer.make();

		// open the container format
		if (container.open(filePath, IContainer.Type.READ, null) < 0) {
			LOGGER.error("Could not open container: [" + filePath + "].");
			throw new UnsupportedMediaException(filePath);
		}
		// convert to seconds
		long length = container.getDuration() / 1000 / 1000;
		String format = container.getContainerFormat().getInputFormatLongName() + " [";
		LOGGER.debug("Audio file opened. Container format: " + container.getContainerFormat().getInputFormatLongName());

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		LOGGER.debug("Stream count: " + numStreams);

		// loop over the streams to find the first audio stream
		IStreamCoder audioCoder = null;
		for (int i = 0; i < numStreams; i++) {
			IStream stream = container.getStream(i);
			// get the coder for the stream
			IStreamCoder coder = stream.getStreamCoder();
			// check for an audio stream
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				audioCoder = coder;
			}
		}
		
		// make sure we have a video stream
		if (audioCoder == null) {
			LOGGER.error("No audio coder found in container: [" + filePath + "].");
			throw new UnsupportedMediaException(filePath);
		}
		
		// check audio
		if (audioCoder != null) {
			format += " " + audioCoder.getCodec().getLongName();
		}
		format += "]";
		
		AudioMediaFile file = new AudioMediaFile(
				basePath,
				filePath,
				format,
				length);
		
		// create the media object
		XugglerAudioMedia media = new XugglerAudioMedia(file);
		
		// clean up
		if (container != null) {
			container.close();
		}
		
		// return the media
		return media;
	}
}

package org.praisenter.media;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.praisenter.resources.Messages;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Media loader for audio using the Xuggler API.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
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
	 * @see org.praisenter.media.MediaLoader#getMediaType()
	 */
	@Override
	public Class<XugglerAudioMedia> getMediaType() {
		return XugglerAudioMedia.class;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String)
	 */
	@Override
	public XugglerAudioMedia load(String filePath) throws MediaException {
		// create the video container object
		IContainer container = IContainer.make();

		// open the container format
		if (container.open(filePath, IContainer.Type.READ, null) < 0) {
			throw new MediaException(MessageFormat.format(Messages.getString("media.loader.ex.container.format"), filePath));
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
			throw new MediaException(MessageFormat.format(Messages.getString("media.loader.ex.noAudio"), filePath));
		}
		
		// check audio
		if (audioCoder != null) {
			format += " " + audioCoder.getCodec().getLongName();
		}
		format += "]";
		
		AudioMediaFile file = new AudioMediaFile(
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

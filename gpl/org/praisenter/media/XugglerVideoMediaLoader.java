package org.praisenter.media;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.praisenter.xml.FileProperties;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * Media loader for videos using the Xuggler API.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerVideoMediaLoader implements VideoMediaLoader {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerVideoMediaLoader.class);
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String mimeType) {
		// handle any mimetype
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#getMediaType()
	 */
	@Override
	public Class<XugglerVideoMedia> getMediaType() {
		return XugglerVideoMedia.class;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String)
	 */
	// FIXME translate
	@Override
	public XugglerVideoMedia load(String filePath) throws MediaException {
		// create the video container object
		IContainer container = IContainer.make();

		// open the container format
		if (container.open(filePath, IContainer.Type.READ, null) < 0) {
			throw new MediaException("Could not open file [" + filePath + "].  Unsupported container format.");
		}
		LOGGER.debug("Video file opened. Container format: " + container.getContainerFormat().getInputFormatLongName());

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		LOGGER.debug("Stream count: " + numStreams);

		// loop over the streams to find the first video stream
		IStreamCoder videoCoder = null;
		for (int i = 0; i < numStreams; i++) {
			IStream stream = container.getStream(i);
			// get the coder for the stream
			IStreamCoder coder = stream.getStreamCoder();
			// see if the coder is a video coder
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				// if so, break from the loop
				videoCoder = coder;
			}
		}
		
		// make sure we have a video stream
		if (videoCoder == null) {
			throw new MediaException("No video stream in container.");
		}

		// open the coder to read the video data
		String codecName = "Unknown";
		ICodec codec = videoCoder.getCodec();
		if (codec != null) {
			codecName = codec.getLongName();
		}
		if (videoCoder.open(null, null) < 0) {
			throw new MediaException("Could not open video decoder for: " + codecName);
		}		
		LOGGER.debug("Video coder opened with format: " + codecName);
		
		// get the first frame of the video
		BufferedImage firstFrame = loadFirstFrame(container, videoCoder);
		LOGGER.debug("First frame read");
		
		// create the media object
		XugglerVideoMedia media = new XugglerVideoMedia(FileProperties.getFileProperties(filePath), firstFrame);
		
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
						// FIXME translate
						throw new MediaException("Error decoding video stream");
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

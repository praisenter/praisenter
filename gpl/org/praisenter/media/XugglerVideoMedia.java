package org.praisenter.media;

import java.awt.Dimension;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ImageUtilities;
import org.praisenter.xml.FileProperties;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Represents a video media type using the Xuggler library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerVideoMedia extends AbstractVideoMedia implements Media, PlayableMedia, XugglerPlayableMedia {
	/** The first frame of the video */
	protected BufferedImage firstFrame;
	
	/** The current frame of the video */
	protected BufferedImage currentFrame;
	
	// video play properties
	
	/** The video format container */
	protected IContainer container;
	
	/** The video coder */
	protected IStreamCoder videoCoder;
	
	/** The audio coder */
	protected IStreamCoder audioCoder;

	/**
	 * Full constructor.
	 * @param fileProperties the file properties
	 * @param firstFrame the first frame of the video
	 * @param container the video format container
	 * @param videoCoder the video coder
	 * @param audioCoder the audio coder; can be null
	 */
	public XugglerVideoMedia(
			FileProperties fileProperties, 
			BufferedImage firstFrame, 
			IContainer container, 
			IStreamCoder videoCoder, 
			IStreamCoder audioCoder) {
		super(fileProperties);
		this.firstFrame = firstFrame;
		this.currentFrame = firstFrame;
		this.container = container;
		this.videoCoder = videoCoder;
		this.audioCoder = audioCoder;
	}
	
	@Override
	public IContainer getContainer() {
		return this.container;
	}
	
	@Override
	public IStreamCoder getAudioCoder() {
		return this.audioCoder;
	}
	
	/**
	 * Returns the stream coder for the video stream.
	 * <p>
	 * Returns null if no stream available.
	 * @return IStreamCoder
	 */
	public IStreamCoder getVideoCoder() {
		return this.videoCoder;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.AbstractVideoMedia#getCurrentFrame()
	 */
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
	public MediaThumbnail getThumbnail(Dimension size) {
		// resize the image to a thumbnail size
		BufferedImage image = ImageUtilities.getUniformScaledImage(this.firstFrame, size.width, size.height, AffineTransformOp.TYPE_BILINEAR);
		// return the thumbnail
		return new MediaThumbnail(this.fileProperties, image, this.type);
	}
}

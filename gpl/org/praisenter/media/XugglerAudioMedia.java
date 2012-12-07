package org.praisenter.media;

import java.awt.Dimension;

import org.praisenter.images.Images;

/**
 * Represents an audio media type using the Xuggler library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerAudioMedia extends AbstractAudioMedia implements Media, PlayableMedia, XugglerPlayableMedia {
	/**
	 * Full constructor.
	 * @param file the file information
	 */
	public XugglerAudioMedia(AudioMediaFile file) {
		super(file);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public MediaThumbnail getThumbnail(Dimension size) {
		return new MediaThumbnail(this.file, Images.SAMPLED_AUDIO, MediaType.AUDIO);
	}
}

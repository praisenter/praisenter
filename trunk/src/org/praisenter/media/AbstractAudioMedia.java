package org.praisenter.media;

/**
 * Base class for audio media.
 * <p>
 * Extend this class to allow audio media to be played.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractAudioMedia extends AbstractMedia implements Media, PlayableMedia {
	/**
	 * Full constructor.
	 * @param file the file information
	 */
	public AbstractAudioMedia(AudioMediaFile file) {
		super(file, MediaType.AUDIO);
	}
}

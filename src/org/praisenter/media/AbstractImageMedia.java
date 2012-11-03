package org.praisenter.media;

/**
 * Represents a media type that displays images.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractImageMedia extends AbstractMedia implements Media {
	/**
	 * Minimal constructor.
	 * @param type the media type
	 * @param fileProperties the file properties
	 */
	public AbstractImageMedia(FileProperties fileProperties, MediaType type) {
		super(fileProperties, type);
	}
}

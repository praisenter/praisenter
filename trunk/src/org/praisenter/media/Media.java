package org.praisenter.media;

import java.awt.Dimension;

/**
 * Represents a generic media object.
 * <p>
 * Media can be images, video, audio, html pages, etc.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Media {
	/**
	 * Returns the media type.
	 * @return {@link MediaType}
	 */
	public abstract MediaType getType();
	
	/**
	 * Returns the file information.
	 * @return {@link MediaFile}
	 */
	public abstract MediaFile getFile();
	
	/**
	 * Returns a thumbnail of the media.
	 * @param size the desired thumbnail size
	 * @return {@link MediaThumbnail}
	 */
	public abstract MediaThumbnail getThumbnail(Dimension size);
}

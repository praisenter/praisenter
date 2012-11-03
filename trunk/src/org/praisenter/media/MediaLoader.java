package org.praisenter.media;

/**
 * Represents a class that can load a type of media.
 * @param <E> the {@link MediaType}
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MediaLoader<E extends Media> {
	/**
	 * Returns true if the given mime type is supported.
	 * @param mimeType the mime type
	 * @return boolean
	 */
	public boolean isSupported(String mimeType);
	
	/**
	 * Loads the given media.
	 * @param filePath the file path and name
	 * @return E
	 * @throws MediaException thrown if the media could not be read
	 */
	public E load(String filePath) throws MediaException;
}

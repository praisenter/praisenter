package org.praisenter.media;

/**
 * Custom generic media exception typically thrown during playback of media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MediaPlaybackException extends MediaException {
	/** The version id */
	private static final long serialVersionUID = 4082557128947462978L;

	/**
	 * Default constructor.
	 */
	public MediaPlaybackException() {
		super();
	}

	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public MediaPlaybackException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public MediaPlaybackException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public MediaPlaybackException(Throwable cause) {
		super(cause);
	}
}

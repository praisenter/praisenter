package org.praisenter.media;

/**
 * Custom generic media exception for media loading.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MediaException extends Exception {
	/** The version id */
	private static final long serialVersionUID = -8534598000033731978L;

	/**
	 * Default constructor.
	 */
	public MediaException() {
		super();
	}

	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public MediaException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public MediaException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public MediaException(Throwable cause) {
		super(cause);
	}
}

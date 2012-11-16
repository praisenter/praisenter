package org.praisenter.slide;

/**
 * Custom exception thrown when a {@link Slide} copy fails.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideCopyException extends Exception {
	/** The version id */
	private static final long serialVersionUID = -2995727869373289205L;

	/**
	 * Default constructor.
	 */
	public SlideCopyException() {
		super();
	}

	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public SlideCopyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public SlideCopyException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public SlideCopyException(Throwable cause) {
		super(cause);
	}
}

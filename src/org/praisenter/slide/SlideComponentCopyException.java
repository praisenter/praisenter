package org.praisenter.slide;

/**
 * Custom exception thrown when a {@link SlideComponent} copy fails.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideComponentCopyException extends Exception {
	/** The version id */
	private static final long serialVersionUID = -3292574981760496922L;

	/**
	 * Default constructor.
	 */
	public SlideComponentCopyException() {
		super();
	}

	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public SlideComponentCopyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public SlideComponentCopyException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public SlideComponentCopyException(Throwable cause) {
		super(cause);
	}
}

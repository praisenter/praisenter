package org.praisenter.slide;

/**
 * Custom generic exception thrown from the {@link SlideLibrary}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideLibraryException extends Exception {
	/** The version id */
	private static final long serialVersionUID = 6674820224531640070L;

	/**
	 * Default constructor.
	 */
	public SlideLibraryException() {
		super();
	}

	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public SlideLibraryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public SlideLibraryException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public SlideLibraryException(Throwable cause) {
		super(cause);
	}
}

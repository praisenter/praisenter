package org.praisenter.media;

/**
 * Custom generic media exception typically thrown from the {@link MediaLibrary}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MediaLibraryException extends MediaException {
	/** The version id */
	private static final long serialVersionUID = 6674820224531640070L;

	/**
	 * Default constructor.
	 */
	public MediaLibraryException() {
		super();
	}

	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public MediaLibraryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public MediaLibraryException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public MediaLibraryException(Throwable cause) {
		super(cause);
	}
}

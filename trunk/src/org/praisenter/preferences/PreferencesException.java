package org.praisenter.preferences;

/**
 * Thrown if preferences fail to be loaded or saved.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PreferencesException extends Exception {
	/** The version id */
	private static final long serialVersionUID = -3292574981760496922L;

	/**
	 * Default constructor.
	 */
	public PreferencesException() {
		super();
	}

	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public PreferencesException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public PreferencesException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public PreferencesException(Throwable cause) {
		super(cause);
	}
}

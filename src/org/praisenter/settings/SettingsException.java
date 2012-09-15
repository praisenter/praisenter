package org.praisenter.settings;

/**
 * Wrapper exception for other exceptions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SettingsException extends Exception {
	/** The version id */
	private static final long serialVersionUID = 3794714350287312393L;

	/**
	 * Default constructor.
	 */
	public SettingsException() {
		super();
	}
	
	/**
	 * Full constructor.
	 * @param message the message
	 * @param throwable the root exception
	 */
	public SettingsException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public SettingsException(String message) {
		super(message);
	}
	
	/**
	 * Optional constructor.
	 * @param throwable the root exception
	 */
	public SettingsException(Throwable throwable) {
		super(throwable);
	}
}

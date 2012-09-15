package org.praisenter.data.errors;

/**
 * Represents an exception for the email service has been configured, but a configuration
 * setting is invalid.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidConfigurationException extends Exception {
	/** The version id */
	private static final long serialVersionUID = -2312218395571553293L;

	/**
	 * Default constructor.
	 */
	public InvalidConfigurationException() {
		super();
	}
	
	/**
	 * Full constructor.
	 * @param message the message
	 * @param throwable the root exception
	 */
	public InvalidConfigurationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public InvalidConfigurationException(String message) {
		super(message);
	}
	
	/**
	 * Optional constructor.
	 * @param throwable the root exception
	 */
	public InvalidConfigurationException(Throwable throwable) {
		super(throwable);
	}
}

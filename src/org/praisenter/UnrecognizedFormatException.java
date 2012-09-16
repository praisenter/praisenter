package org.praisenter;

/**
 * Exception thrown when a file does not match the expected format.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class UnrecognizedFormatException extends Exception {

	/** The version id */
	private static final long serialVersionUID = 5549378148489540081L;

	/**
	 * Default constructor.
	 */
	public UnrecognizedFormatException() {
		super();
	}
	
	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public UnrecognizedFormatException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public UnrecognizedFormatException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public UnrecognizedFormatException(Throwable cause) {
		super(cause);
	}
}

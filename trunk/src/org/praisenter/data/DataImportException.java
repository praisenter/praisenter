package org.praisenter.data;

/**
 * Exception thrown when a data import fails.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataImportException extends Exception {

	/** The version id */
	private static final long serialVersionUID = 5549378148489540081L;

	/**
	 * Default constructor.
	 */
	public DataImportException() {
		super();
	}
	
	/**
	 * Full constructor.
	 * @param message the message
	 * @param cause the root exception
	 */
	public DataImportException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public DataImportException(String message) {
		super(message);
	}

	/**
	 * Optional constructor.
	 * @param cause the root exception
	 */
	public DataImportException(Throwable cause) {
		super(cause);
	}
}

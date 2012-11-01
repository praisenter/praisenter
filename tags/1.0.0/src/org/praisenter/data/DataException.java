package org.praisenter.data;

/**
 * Represents an exception caused in the data layer.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataException extends Exception {
	/** The version id */
	private static final long serialVersionUID = -2312218395571553293L;

	/**
	 * Default constructor.
	 */
	public DataException() {
		super();
	}
	
	/**
	 * Full constructor.
	 * @param message the message
	 * @param throwable the root exception
	 */
	public DataException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	/**
	 * Optional constructor.
	 * @param message the message
	 */
	public DataException(String message) {
		super(message);
	}
	
	/**
	 * Optional constructor.
	 * @param throwable the root exception
	 */
	public DataException(Throwable throwable) {
		super(throwable);
	}
}

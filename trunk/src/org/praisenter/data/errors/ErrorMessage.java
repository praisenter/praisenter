package org.praisenter.data.errors;

import java.util.Date;

import org.praisenter.utilities.ExceptionUtilities;
import org.praisenter.utilities.SystemUtilities;

/**
 * Represents an error message produced by the application
 * that the user has requested to be sent to the developer/customer service.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ErrorMessage {
	/** New error message id */
	protected static final int NEW_ERROR_ID = -1;
	
	/** The message id */
	protected int id;
	
	/** The java version */
	protected String javaVersion;
	
	/** The java vendor */
	protected String javaVendor;
	
	/** The host operating system */
	protected String os;
	
	/** The host architecture */
	protected String architecture;
	
	/** The error message */
	protected String message;
	
	/** The error stacktrace */
	protected String stacktrace;
	
	/** The contact information */
	protected String contact;
	
	/** The user's description of the problem */
	protected String description;
	
	/** The date/time of entry */
	protected Date timestamp;
	
	/**
	 * Optional constructor.
	 * @param message the error message
	 * @param exception the exception
	 */
	protected ErrorMessage(String message, Exception exception) {
		this(message, ExceptionUtilities.getStackTrace(exception));
	}
	
	/**
	 * Optional constructor.
	 * @param message the error message
	 * @param stacktrace the stacktrace
	 */
	protected ErrorMessage(String message, String stacktrace) {
		this(message, stacktrace, null, null);
	}
	
	/**
	 * Optional constructor.
	 * @param message the error message
	 * @param exception the exception
	 * @param contact the contact information
	 * @param description the user description
	 */
	protected ErrorMessage(String message, Exception exception, String contact, String description) {
		this(message, ExceptionUtilities.getStackTrace(exception), contact, description);
	}
	
	/**
	 * Optional constructor.
	 * @param message the error message
	 * @param stacktrace the stacktrace
	 * @param contact the contact information
	 * @param description the user description
	 */
	protected ErrorMessage(String message, String stacktrace, String contact, String description) {
		this.id = NEW_ERROR_ID;
		this.javaVersion = SystemUtilities.getJavaVersion();
		this.javaVendor = SystemUtilities.getJavaVendor();
		this.os = SystemUtilities.getOperatingSystem();
		this.architecture = SystemUtilities.getArchitecture();
		this.message = message;
		this.stacktrace = stacktrace;
		this.contact = contact;
		this.description = description;
		this.timestamp = new Date();
	}
	
	/**
	 * Full constructor.
	 * @param id the message id
	 * @param javaVersion the java version
	 * @param javaVendor the java vendor
	 * @param os the host operating system
	 * @param architecture the architecture
	 * @param message the error message
	 * @param stacktrace the stacktrace
	 * @param contact the contact information
	 * @param description the user description
	 * @param timestamp the entry timestamp
	 */
	protected ErrorMessage(int id, String javaVersion, String javaVendor, String os, String architecture, 
			String message, String stacktrace, String contact, String description, Date timestamp) {
		this.id = id;
		this.javaVersion = javaVersion;
		this.javaVendor = javaVendor;
		this.os = os;
		this.architecture = architecture;
		this.message = message;
		this.stacktrace = stacktrace;
		this.contact = contact;
		this.description = description;
		this.timestamp = timestamp;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ErrorMessage[Id=").append(this.id)
		  .append("|JavaVersion=").append(this.javaVersion)
		  .append("|JavaVendor=").append(this.javaVendor)
		  .append("|OS=").append(this.os)
		  .append("|Architecture=").append(this.architecture)
		  .append("|Message=").append(this.message)
		  .append("|Stacktrace=").append(this.stacktrace)
		  .append("|Contact=").append(this.contact)
		  .append("|Description=").append(this.description)
		  .append("|Timestamp=").append(this.timestamp)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the error id.
	 * @return int
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns the java version.
	 * @return String
	 */
	public String getJavaVersion() {
		return this.javaVersion;
	}
	
	/**
	 * Returns the java vendor.
	 * @return String
	 */
	public String getJavaVendor() {
		return this.javaVendor;
	}
	
	/**
	 * Returns the operating system.
	 * @return String
	 */
	public String getOs() {
		return this.os;
	}
	
	/**
	 * Returns the architecture.
	 * @return String
	 */
	public String getArchitecture() {
		return this.architecture;
	}
	
	/**
	 * Returns the error message.
	 * @return String
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Returns the stacktrace.
	 * @return String
	 */
	public String getStacktrace() {
		return this.stacktrace;
	}
	
	/**
	 * Returns the contact information.
	 * @return String
	 */
	public String getContact() {
		return this.contact;
	}
	
	/**
	 * Returns the user's description.
	 * @return String
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Returns the entry timestamp.
	 * @return Date
	 */
	public Date getTimestamp() {
		return this.timestamp;
	}
}

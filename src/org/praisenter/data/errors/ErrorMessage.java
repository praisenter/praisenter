/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

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

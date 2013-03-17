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
package org.praisenter.slide.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Helper class used to get text resources from the properties files.
 * <p>
 * This class is used by all classes to get the appropriate resources.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Messages {
	/** Static logger */
	private static final Logger LOGGER = Logger.getLogger(Messages.class);
	
	/** The resource bundle containing the text resources */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".messages");
	
	/**
	 * Hidden constructor.
	 */
	private Messages() {}
	
	/**
	 * Returns the value of the given key.
	 * <p>
	 * Returns a blank string if the key is null, the key is not found, or the type in the
	 * property file is not string.
	 * @param key the key
	 * @return String the value
	 */
	public static final String getString(String key) {
		try {
			return BUNDLE.getString(key);
		} catch (NullPointerException ex) {
			LOGGER.warn(ex);
		} catch (MissingResourceException ex) {
			LOGGER.warn(ex);
		} catch (ClassCastException ex) {
			LOGGER.warn(ex);
		}
		return "";
	}
}

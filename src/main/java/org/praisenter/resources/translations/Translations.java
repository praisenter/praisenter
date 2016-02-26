/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.resources.translations;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO desired locale needs to be set first.  so we need to load a config file and be able to save settings to it.
/**
 * Helper class to retrieve transations by key.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Translations {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The supported locales */
	public static final Locale[] SUPPORTED_LOCALES = new Locale[] {
		// add more here as you add more messages_x.properties
		Locale.US
	};
	
	/** The base bundle name */
	private static final String BUNDLE_BASE_NAME = Translations.class.getPackage().getName() + ".messages";
	
	/** The default locale neutral bundle (in our case english) */
	private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
	
	/** The default locale bundle */
	private static final ResourceBundle LOCALE_BUNDLE;
	
	static {
		// set the default locale bundle
		Locale defaultLocale = Locale.getDefault();
		ResourceBundle bundle = DEFAULT_BUNDLE;
		try {
			// attempt to load it
			bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, defaultLocale);
		} catch (MissingResourceException ex) {
			LOGGER.warn("Couldn't find messages.properties file for locale '{}', using default.", defaultLocale.toString());
		}
		LOCALE_BUNDLE = bundle;
	}

	/** Hidden default constructor */
	private Translations() {}
	
	/**
	 * Returns the translation for the given key.
	 * <p>
	 * This method will always return a string, either the current locale's translation,
	 * the default translation, or empty string.
	 * @param key the key
	 * @return String
	 */
	public static final String getTranslation(String key) {
		// find the key in the current locale
		Locale locale = Locale.getDefault();
		try {
			return LOCALE_BUNDLE.getString(key);
		} catch (MissingResourceException ex) {
			LOGGER.warn("Failed to find key '" + key + "' for locale '" + locale.toString() + "'.");
		}
		
		// find the key in the default locale
		try {
			return DEFAULT_BUNDLE.getString(key);
		} catch (MissingResourceException ex) {
			LOGGER.warn("Failed to find key '" + key + "' in the default bundle.");
		}
		
		// last resort just return a default string
		return "";
	}
}

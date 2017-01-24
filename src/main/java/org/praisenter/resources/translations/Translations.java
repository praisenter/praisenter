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

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;

/**
 * Helper class to retrieve transations by key.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Translations {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The base bundle name */
	private static final String BUNDLE_BASE_NAME = Translations.class.getPackage().getName() + ".messages";

	/** A regex for matching translation file names */
	private static final Pattern TRANSLATION_PATTERN = Pattern.compile("^messages_(.+)\\.properties$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	
	/** The control to use for loading translations */
	private static final ResourceBundle.Control DEFAULT_CONTROL = new FileSystemControl();
	
	/** The default locale neutral bundle (in our case english) */
	private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
	
	/** The default locale bundle */
	private static final ResourceBundle LOCALE_BUNDLE;
	
	static {
		try {
			Files.createDirectories(Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH));
		} catch (Exception ex) {
			LOGGER.warn("Failed to create translations directory.", ex);
		}
		
		// set the default locale bundle
		Locale defaultLocale = Locale.getDefault();
		ResourceBundle bundle = DEFAULT_BUNDLE;
		try {
			bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, defaultLocale, DEFAULT_CONTROL);
		} catch (MissingResourceException ex) {
			LOGGER.warn("Couldn't find messages.properties file for locale '{}', using default.", defaultLocale.toString());
		}
		
		// set the locale bundle
		LOCALE_BUNDLE = bundle;
	}

	/**
	 * Loads the supported locales and any locales in the locales directory.
	 * @return List&lt;Locale&gt;
	 */
	public static final List<Locale> getAvailableLocales() {
		Set<Locale> locales = new HashSet<Locale>();
		
		// default support
		locales.add(Locale.ENGLISH);
		
		// look in the locales directory for any additional translations
		Path localeDir = Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH);
		if (Files.exists(localeDir) && Files.isDirectory(localeDir)) {
			try (DirectoryStream<Path> paths = Files.newDirectoryStream(localeDir)) {
				Iterator<Path> it = paths.iterator();
				while (it.hasNext()) {
					Path path = it.next();
					try {
						if (Files.isRegularFile(path)) {
							// attempt to read the name
							String fileName = path.getFileName().toString().toLowerCase();
							Matcher m = TRANSLATION_PATTERN.matcher(fileName);
							if (m.matches()) {
								String lang = m.group(1);
								Locale locale = Locale.forLanguageTag(lang);
								locales.add(locale);
							}
						}
					} catch (Exception ex) {
						LOGGER.warn("Failed to check type of path for path '" + path + "'.", ex);
					}
				}
			} catch (Exception e) {
				LOGGER.warn("Failed to iterate translations in '" + localeDir + "'.", e);
			}
		}
		
		List<Locale> list = new ArrayList<Locale>(locales);
		Collections.sort(list, new Comparator<Locale>() {
			@Override
			public int compare(Locale o1, Locale o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});
		
		return Collections.unmodifiableList(list);
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
	public static final String get(String key) {
		// find the key in the current locale
		Locale locale = Locale.getDefault();
		try {
			// this bundle will fallback to the default bundle when the key is not found
			return LOCALE_BUNDLE.getString(key);
		} catch (MissingResourceException ex) {
			LOGGER.warn("Failed to find key '" + key + "' for locale '" + locale.toString() + "' and in the default translation.");
		}
		
		// last resort just return the key itself
		return key;
	}
}

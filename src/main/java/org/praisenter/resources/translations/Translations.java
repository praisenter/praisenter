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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
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
	
	/** The supported locales */
	public static final Locale[] SUPPORTED_LOCALES;
	
	/** The base bundle name */
	private static final String BUNDLE_BASE_NAME = Translations.class.getPackage().getName() + ".messages";
	
	/** The default locale neutral bundle (in our case english) */
	private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
	
	/** The default locale bundle */
	private static final ResourceBundle LOCALE_BUNDLE;
	
	static {
		try {
			Files.createDirectories(Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// assign the supported locales
		SUPPORTED_LOCALES = Translations.getSupportedLocales();
		
		// set the default locale bundle
		Locale defaultLocale = Locale.getDefault();
		ResourceBundle bundle = null;
		try {
			// attempt to load it
			// FIXME doesn't fall back to other property files
			bundle = new PropertyResourceBundle(new InputStreamReader(new FileInputStream(Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH + "messages_" + defaultLocale.toLanguageTag() + ".properties").toFile()), Charset.forName("UTF-8")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (bundle == null) {
			try {
				bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, defaultLocale);
			} catch (MissingResourceException ex) {
				LOGGER.warn("Couldn't find messages.properties file for locale '{}', using default.", defaultLocale.toString());
			}
		}
		
		if (bundle == null) {
			bundle = DEFAULT_BUNDLE;
		}
		
		LOCALE_BUNDLE = bundle;
	}

	private static final Locale[] getSupportedLocales() {
		List<Locale> locales = new ArrayList<Locale>();
		// default support
		locales.add(Locale.ENGLISH);
		locales.add(new Locale("es"));
		
		Pattern p = Pattern.compile("messages_(.+)\\.properties", Pattern.CASE_INSENSITIVE);
		Path localeDir = Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH);
		try (DirectoryStream<Path> paths = Files.newDirectoryStream(localeDir)) {
			Iterator<Path> it = paths.iterator();
			while (it.hasNext()) {
				Path path = it.next();
				if (Files.isRegularFile(path)) {
					// attempt to read the name
					String fileName = path.getFileName().toString().toLowerCase();
					if (fileName.endsWith(".properties")) {
						Matcher m = p.matcher(fileName);
						if (m.matches()) {
							String lang = m.group(1);
							Locale locale = Locale.forLanguageTag(lang);
							locales.add(locale);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return locales.toArray(new Locale[0]);
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
		
		// last resort just return a default string
		return "";
	}
}

package org.praisenter.resources.translations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO desired locale will be set on command line
public final class Translations {
	private static final Logger LOGGER = LogManager.getLogger(Translations.class);
	
	public static final Locale[] SUPPORTED_LOCALES = new Locale[] {
		Locale.US
	};
	private static final String BUNDLE_BASE_NAME = Translations.class.getPackage().getName() + ".messages";
	private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
	
	private static final Map<Locale, ResourceBundle> BUNDLES;
	
	static {
		// load the bundles for the supported locales
		Map<Locale, ResourceBundle> bundles = new HashMap<Locale, ResourceBundle>();
		for (Locale locale : SUPPORTED_LOCALES) {
			ResourceBundle bundle = DEFAULT_BUNDLE;
			try {
				bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
			} catch (MissingResourceException ex) {
				LOGGER.warn("Couldn't find messages.properties file for locale '" + locale.toString() + "', using the root.");
			}
			bundles.put(locale, bundle);
		}
		
		// add the default locale
		Locale defaultLocale = Locale.getDefault();
		if (!bundles.containsKey(defaultLocale)) {
			ResourceBundle bundle = DEFAULT_BUNDLE;
			try {
				bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, defaultLocale);
			} catch (MissingResourceException ex) {
				LOGGER.warn("Couldn't find messages.properties file for locale '" + defaultLocale.toString() + "', using default.");
			}
			bundles.put(defaultLocale, bundle);
		}
		
		BUNDLES = Collections.unmodifiableMap(bundles);
	}

	public static final String getTranslation(String key) {
		// find the key in the current locale
		Locale locale = Locale.getDefault();
		try {
			return BUNDLES.get(locale).getString(key);
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

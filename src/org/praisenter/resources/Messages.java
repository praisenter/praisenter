package org.praisenter.resources;

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
	
	// change this value to messages_test.properties to test the text translation
	
	/** The resource bundle containing the text resources */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org.praisenter.resources.messages");
	
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

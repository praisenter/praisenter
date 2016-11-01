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
package org.praisenter.javafx.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.javafx.styles.Theme;
import org.praisenter.xml.XmlIO;

/**
 * Represents the storage mechanism for application settings.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.NONE)
public final class Configuration {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The data storage */
	@XmlElement(name = "settings")
	private final Map<Setting, String> settings = new HashMap<Setting, String>();
	
	// restart required
	
	/** The currently in use language */
	private Locale language;
	
	/** The currently in use theme */
	private Theme theme;
	
	// for batch
	
	/** The batch mode flag */
	private boolean batching;
	
	/**
	 * Hidden constructor for new configuration.
	 */
	private Configuration() {
		this.batching = false;
		this.language = Locale.getDefault();
		this.theme = Theme.DEFAULT;

		// set default language/theme
		this.set(Setting.GENERAL_LANGUAGE, this.language.toLanguageTag());
		this.set(Setting.GENERAL_THEME, this.theme.toString());
		
		// set other defaults
		this.set(Setting.BIBLE_PRIMARY, null);
		this.set(Setting.BIBLE_SECONDARY, null);
		this.set(Setting.BIBLE_SHOW_RENUMBER_WARNING, "true");
	}
	
	/**
	 * Loads the current configuration or return a new default configuration.
	 * @return {@link Configuration}
	 */
	public static final Configuration load() {
		Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
		if (Files.exists(path)) {
			try {
				Configuration conf = XmlIO.read(path, Configuration.class);
				conf.language = Locale.forLanguageTag(conf.get(Setting.GENERAL_LANGUAGE));
				conf.theme = Theme.valueOf(conf.get(Setting.GENERAL_THEME));
				if (conf.theme == null) {
					conf.theme = Theme.DEFAULT;
				}
				return conf;
			} catch (Exception ex) {
				LOGGER.info("Failed to load configuration. Using default configuration.", ex);
			}
		} else {
			LOGGER.info("No configuration found. Using default configuration.");
		}
		
		Configuration conf = new Configuration();
		
		// try to save it
		try {
			XmlIO.save(path, conf);
		} catch (Exception ex) {
			LOGGER.warn("Failed to save default configuration.", ex);
		}
		
		return conf;
	}
	
	/**
	 * Saves this configuration.
	 * @throws JAXBException if the configuration cannot be serialized
	 * @throws IOException if an IO error occurs
	 */
	private final void save() throws JAXBException, IOException {
		Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
		XmlIO.save(path, this);
	}
	
	// public interface
	
	/**
	 * Sets the given setting to the given value.
	 * @param setting the setting
	 * @param value the value
	 */
	public void set(Setting setting, String value) {
		this.settings.put(setting, value);
		if (!this.batching) {
			try  {
				this.save();
			} catch (Exception ex) {
				LOGGER.error("Failed to save configuration after assigning " + setting + " to " + value);
			}
		}
	}

	/**
	 * Returns true if the given setting is present and non-null.
	 * @param setting the setting
	 * @return boolean
	 */
	public boolean isSet(Setting setting) {
		return this.settings.containsKey(setting) && this.settings.get(setting) != null;
	}
	
	/**
	 * Returns the given settings value or null if not present.
	 * @param setting the setting
	 * @return String
	 */
	public String get(Setting setting) {
		return this.settings.get(setting);
	}

	/**
	 * Removes the given setting.
	 * @param setting the setting
	 */
	public void remove(Setting setting) {
		this.settings.remove(setting);
		if (!this.batching) {
			try  {
				this.save();
			} catch (Exception ex) {
				LOGGER.error("Failed to save configuration after removing " + setting);
			}
		}
	}
	
	/**
	 * Begins batch mode where incremental changes are not saved until
	 * {@link #endBatch()} is called.
	 */
	public void beginBatch() {
		this.batching = true;
	}
	
	/**
	 * Ends batch mode and saves all changes.
	 */
	public void endBatch() {
		this.batching = false;
		try  {
			this.save();
		} catch (Exception ex) {
			LOGGER.error("Failed to save configuration after batch configuration.");
		}
	}
	
	// convenience set
	
	/**
	 * Sets the given setting to the given boolean value.
	 * @param setting the setting
	 * @param value the value
	 */
	public void setBoolean(Setting setting, boolean value) {
		this.set(setting, String.valueOf(value));
	}
	
	/**
	 * Sets the given setting to the given byte value.
	 * @param setting the setting
	 * @param value the value
	 */
	public void setByte(Setting setting, byte value) {
		this.set(setting, String.valueOf(value));
	}

	/**
	 * Sets the given setting to the given short value.
	 * @param setting the setting
	 * @param value the value
	 */
	public void setShort(Setting setting, short value) {
		this.set(setting, String.valueOf(value));
	}
	
	/**
	 * Sets the given setting to the given int value.
	 * @param setting the setting
	 * @param value the value
	 */
	public void setInt(Setting setting, int value) {
		this.set(setting, String.valueOf(value));
	}
	
	/**
	 * Sets the given setting to the given long value.
	 * @param setting the setting
	 * @param value the value
	 */
	public void setLong(Setting setting, long value) {
		this.set(setting, String.valueOf(value));
	}
	
	/**
	 * Sets the given setting to the given double value.
	 * @param setting the setting
	 * @param value the value
	 */
	public void setDouble(Setting setting, double value) {
		this.set(setting, String.valueOf(value));
	}
	
	/**
	 * Sets the given setting to the given float value.
	 * @param setting the setting
	 * @param value the value
	 */
	public void setFloat(Setting setting, float value) {
		this.set(setting, String.valueOf(value));
	}
	
	// convenience get
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a boolean.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return boolean
	 */
	public boolean getBoolean(Setting setting, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(this.settings.get(setting));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a byte.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return byte
	 */
	public byte getByte(Setting setting, byte defaultValue) {
		try {
			return Byte.parseByte(this.settings.get(setting));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a short.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return short
	 */
	public short getShort(Setting setting, short defaultValue) {
		try {
			return Short.parseShort(this.settings.get(setting));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a int.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return int
	 */
	public int getInt(Setting setting, int defaultValue) {
		try {
			return Integer.parseInt(this.settings.get(setting));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a long.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return long
	 */
	public long getLong(Setting setting, long defaultValue) {
		try {
			return Long.parseLong(this.settings.get(setting));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a double.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return double
	 */
	public double getDouble(Setting setting, double defaultValue) {
		try {
			return Double.parseDouble(this.settings.get(setting));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a float.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return float
	 */
	public float getFloat(Setting setting, float defaultValue) {
		try {
			return Float.parseFloat(this.settings.get(setting));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	// helpers
	
	/**
	 * Returns the current language.
	 * @return Locale
	 */
	public Locale getLanguage() {
		return this.language;
	}

	/**
	 * Sets the current language.
	 * <p>
	 * NOTE: The application must be restarted to see this change.
	 * @param locale the locale
	 */
	public void setLanguage(Locale locale) {
		this.set(Setting.GENERAL_LANGUAGE, locale != null ? locale.toLanguageTag() : Locale.getDefault().toLanguageTag());
	}

	/**
	 * Returns the current theme.
	 * @return {@link Theme}
	 */
	public Theme getTheme() {
		return this.theme;
	}
	
	/**
	 * Sets the current theme.
	 * <p>
	 * NOTE: The application must be restarted to see this change.
	 * @param theme the theme
	 */
	public void setTheme(Theme theme) {
		this.set(Setting.GENERAL_THEME, theme != null ? theme.toString() : Theme.DEFAULT.toString());
	}
}
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.themes.Theme;
import org.praisenter.xml.XmlIO;

/**
 * Represents the storage mechanism for application settings.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.NONE)
// we have to define any custom types that might be saved in settings
@XmlSeeAlso({
	Display.class,
	Resolution.class,
	ResolutionSet.class,
	UUID.class
})
public final class Configuration extends SettingMap<Void> {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The other settings */
	@XmlElement(name = "settings", required = false)
	private final Map<Setting, Object> settings;
	
	/**
	 * Hidden constructor for new configuration.
	 */
	private Configuration() {
		this.settings = new ConcurrentHashMap<Setting, Object>();

		// set default language/theme
		this.settings.put(Setting.APP_LANGUAGE, Locale.getDefault().toLanguageTag());
		this.settings.put(Setting.APP_THEME, Theme.DEFAULT.getName());
		
		// set other defaults
		this.settings.put(Setting.BIBLE_SHOW_RENUMBER_WARNING, true);
		this.settings.put(Setting.BIBLE_SHOW_REORDER_WARNING, true);
		this.settings.put(Setting.MEDIA_TRANSCODING_ENABLED, true);
		this.settings.put(Setting.MEDIA_TRANSCODING_VIDEO_EXTENSION, JavaFXMediaImportFilter.DEFAULT_VIDEO_EXTENSION);
		this.settings.put(Setting.MEDIA_TRANSCODING_AUDIO_EXTENSION, JavaFXMediaImportFilter.DEFAULT_AUDIO_EXTENSION);
		this.settings.put(Setting.MEDIA_TRANSCODING_VIDEO_COMMAND, JavaFXMediaImportFilter.DEFAULT_COMMAND);
		this.settings.put(Setting.MEDIA_TRANSCODING_AUDIO_COMMAND, JavaFXMediaImportFilter.DEFAULT_COMMAND);
		
		ResolutionSet resolutions = new ResolutionSet();
		resolutions.addAll(Arrays.asList(Resolution.DEFAULT_RESOLUTIONS));
		this.settings.put(Setting.DISPLAY_RESOLUTIONS, resolutions);
	}
	
	/**
	 * Loads the current configuration or return a new default configuration.
	 * @return {@link Configuration}
	 */
	public static Configuration load() {
		Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
		if (Files.exists(path)) {
			try {
				return XmlIO.read(path, Configuration.class);
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
	public final synchronized void save() throws JAXBException, IOException {
		Path path = Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH);
		XmlIO.save(path, this);
	}
	
	// public interface
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#set(org.praisenter.javafx.configuration.Setting, java.lang.String)
	 */
	public Void set(Setting setting, Object value) {
		if (value == null) {
			this.settings.remove(setting);
		} else {
			this.settings.put(setting, value);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#isSet(org.praisenter.javafx.configuration.Setting)
	 */
	public boolean isSet(Setting setting) {
		return this.settings.containsKey(setting) && this.settings.get(setting) != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#get(org.praisenter.javafx.configuration.Setting)
	 */
	public Object get(Setting setting) {
		return this.settings.get(setting);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#remove(org.praisenter.javafx.configuration.Setting)
	 */
	public Void remove(Setting setting) {
		this.settings.remove(setting);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#setAll(java.util.Map)
	 */
	@Override
	protected Void setAll(Map<Setting, Object> settings) {
		for (Setting key : settings.keySet()) {
			Object value = settings.get(key);
			if (value != null) {
				this.settings.put(key, value);
			} else {
				this.settings.remove(key);
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#getAll()
	 */
	@Override
	protected Map<Setting, Object> getAll() {
		return Collections.unmodifiableMap(this.settings);
	}
}
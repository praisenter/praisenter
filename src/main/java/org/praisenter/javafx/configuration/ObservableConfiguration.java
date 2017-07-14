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

import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.javafx.themes.Theme;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Represents an observable version of the {@link Configuration} class.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ObservableConfiguration extends SettingMap<AsyncTask<Void>> {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// data
	
	/** The configuration */
	private final Configuration configuration;

	// restart required
	
	/** The currently in use language */
	private final Locale language;
	
	/** The currently in use theme */
	private final Theme theme;
	
	// observables
	
	/** An observable version of the settings */
	private final ObservableMap<Setting, Object> settings = FXCollections.observableHashMap();
	
	/** An observable list of resolutions (stored in settings but not observable as such) */
	private final ObservableList<Resolution> resolutions = FXCollections.observableArrayList();
	
	/** Ah observable list of display assignments (stored in settings but not observable as such) */
	private final ObservableList<Display> displays = FXCollections.observableArrayList();
	
	// to make sure callers don't modify the lists directly
	
	/** An observable list of resolutions (stored in settings but not observable as such) */
	private final ObservableList<Resolution> readonlyResolutions = FXCollections.unmodifiableObservableList(this.resolutions);
	
	/** Ah observable list of display assignments (stored in settings but not observable as such) */
	private final ObservableList<Display> readonlyDisplays = FXCollections.unmodifiableObservableList(this.displays);
	
	/**
	 * Minimal constructor.
	 * @param configuration the configuration to wrap
	 */
	public ObservableConfiguration(Configuration configuration) {
		// set the configuration
		this.configuration = configuration;
		
		// add all the settings to the observable map
		this.settings.putAll(configuration.getAll());
		
		// set language
		this.language = Locale.forLanguageTag(configuration.getString(Setting.APP_LANGUAGE, Locale.getDefault().toLanguageTag()));
		
		// set theme
		String name = configuration.getString(Setting.APP_THEME, null);
		Theme theme = Theme.DEFAULT;
		for (Theme t : Theme.getAvailableThemes()) {
			if (t.getName().equalsIgnoreCase(name)) {
				theme = t;
				break;
			}
		}
		this.theme = theme;
		
		// add all resolutions to the observable list
		ResolutionSet resolutions = this.configuration.getObject(Setting.DISPLAY_RESOLUTIONS, ResolutionSet.class, null);
		if (resolutions != null) {
			this.resolutions.addAll(resolutions);
		}
		
		// add all display assignments
		Displays displays = this.configuration.getObject(Setting.DISPLAY_ASSIGNMENTS, Displays.class, null);
		if (displays != null) {
			this.displays.addAll(displays);
		}
	}
	
	/**
	 * Updates the configuration and observables to reflect the given change.
	 * @param setting the setting to change
	 * @param value the setting's new value
	 */
	private void update(Setting setting, Object value) {
		if (setting == null) {
			return;
		}
		this.settings.put(setting, value);
		this.configuration.set(setting, value);
		
		if (setting == Setting.DISPLAY_RESOLUTIONS) {
			if (value != null && value instanceof ResolutionSet) {
				this.resolutions.setAll((ResolutionSet)value);
			} else {
				this.resolutions.clear();
			}
		} else if (setting == Setting.DISPLAY_ASSIGNMENTS) {
			if (value != null && value instanceof Displays) {
				this.displays.setAll((Displays)value);
			} else {
				this.displays.clear();
			}
		}
	}
	
	/**
	 * Updates the configuration and observables to reflect the given changes.
	 * @param settings a map of settings changes
	 */
	private void update(Map<Setting, Object> settings) {
		if (settings == null) {
			return;
		}
		this.settings.putAll(settings);
		this.configuration.setAll(settings);
		
		for (Setting setting : settings.keySet()) {
			Object value = settings.get(setting);
			if (setting == Setting.DISPLAY_RESOLUTIONS) {
				ObservableConfiguration.this.resolutions.setAll((ResolutionSet)value);
			} else if (setting == Setting.DISPLAY_ASSIGNMENTS) {
				ObservableConfiguration.this.displays.setAll((Displays)value);
			}
		}
	}

	// normal settings
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#set(org.praisenter.javafx.configuration.Setting, java.lang.String)
	 */
	@Override
	protected AsyncTask<Void> set(Setting setting, Object value) {
		this.update(setting, value);
		return this.save();
	}

	/**
	 * Saves the current configuration.
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> save() {
		return AsyncTaskFactory.single(() -> {
			try {
				this.configuration.save();
			} catch (Exception ex) {
				LOGGER.error("Failed to save configuration.", ex);
				throw ex;
			}
			return null;
		});
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#get(org.praisenter.javafx.configuration.Setting)
	 */
	@Override
	protected Object get(Setting setting) {
		return this.configuration.get(setting);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#getAll()
	 */
	@Override
	protected Map<Setting, Object> getAll() {
		return this.configuration.getAll();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#setAll(java.util.Map)
	 */
	@Override
	protected AsyncTask<Void> setAll(Map<Setting, Object> settings) {
		this.update(settings);
		return this.save();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#isSet(org.praisenter.javafx.configuration.Setting)
	 */
	@Override
	public boolean isSet(Setting setting) {
		return this.configuration.isSet(setting);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#remove(org.praisenter.javafx.configuration.Setting)
	 */
	public AsyncTask<Void> remove(Setting setting) {
		this.update(setting, null);
		return this.save();
	}
	
	// observables
	
	/**
	 * Returns the settings for observation.
	 * @return ObservableMap&lt;{@link Setting}, Object&gt;
	 */
	public ObservableMap<Setting, Object> getSettings() {
		return this.settings;
	}
	
	// helpers
	
	/**
	 * Returns an observable list of resolutions.
	 * @return ObservableList&lt;{@link Resolution}&gt;
	 */
	public ObservableList<Resolution> getResolutions() {
		return this.readonlyResolutions;
	}
	
	/**
	 * Returns an observable list of display assignments.
	 * @return ObservableList&lt;{@link Display}&gt;
	 */
	public ObservableList<Display> getDisplays() {
		return this.readonlyDisplays;
	}

	// restart required
	
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
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> setLanguage(Locale locale) {
		return this.set(Setting.APP_LANGUAGE, locale != null ? locale.toLanguageTag() : Locale.getDefault().toLanguageTag());
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
	 * @return {@link AsyncTask}&lt;Void&gt;
	 */
	public AsyncTask<Void> setTheme(Theme theme) {
		return this.set(Setting.APP_THEME, theme != null ? theme.getName() : Theme.DEFAULT.getName());
	}
}

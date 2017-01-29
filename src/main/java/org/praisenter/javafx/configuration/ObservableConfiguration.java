package org.praisenter.javafx.configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.javafx.themes.Theme;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

public final class ObservableConfiguration extends SettingMap<AsyncTask<Void>> {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// data
	
	private final Configuration configuration;

	// restart required
	
	/** The currently in use language */
	private final Locale language;
	
	/** The currently in use theme */
	private final Theme theme;
	
	// observables
	
	private final ObservableMap<Setting, Object> settings = FXCollections.observableHashMap();
	private final ObservableMap<Setting, Object> roSettings = FXCollections.unmodifiableObservableMap(settings);
	
	private final ObservableList<Resolution> resolutions = FXCollections.observableArrayList();
	private final ObservableList<Resolution> readonly = FXCollections.unmodifiableObservableList(resolutions);
	
	public ObservableConfiguration(Configuration configuration) {
		// load the configuration
		this.configuration = configuration;
		
		// set all settings
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
		
		// set resolutions
		Resolutions resolutions = this.configuration.getObject(Setting.DISPLAY_RESOLUTIONS, null);
		if (resolutions != null) {
			this.resolutions.addAll(resolutions);
		}
	}
	
	private void update(Setting setting, Object value) {
		if (setting == null) {
			return;
		}
		
		this.settings.put(setting, value);
		this.configuration.set(setting, value);
		
		if (setting == Setting.DISPLAY_RESOLUTIONS) {
			if (value != null && value instanceof Resolutions) {
				Resolutions resolutions = (Resolutions)value;
				this.resolutions.setAll(resolutions);
			} else {
				this.resolutions.clear();
			}
		}
	}
	
	private void update(Map<Setting, Object> settings) {
		if (settings == null) {
			return;
		}
		
		this.configuration.setAll(settings);
		this.settings.putAll(settings);
		
		if (settings.containsKey(Setting.DISPLAY_RESOLUTIONS)) {
			Object value = settings.get(Setting.DISPLAY_RESOLUTIONS);
			if (value != null && value instanceof Resolutions) {
				Resolutions resolutions = (Resolutions)value;
				this.resolutions.setAll(resolutions);
			} else {
				this.resolutions.clear();
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
		return AsyncTaskFactory.single(() -> {
			try {
				this.configuration.save();
			} catch (Exception ex) {
				LOGGER.error("Failed to save configuration after assigning '" + setting + "' to '" + value + "'.", ex);
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
		return AsyncTaskFactory.single(() -> {
			try {
				this.configuration.save();
			} catch (Exception ex) {
				LOGGER.error("Failed to save configuration after committing a batch.");
				throw ex;
			}
			return null;
		});
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
		return AsyncTaskFactory.single(() -> {
			try {
				this.configuration.save();
			} catch (Exception ex) {
				LOGGER.error("Failed to save configuration after removing '" + setting + "'.", ex);
				throw ex;
			}
			return null;
		});
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
	 */
	public AsyncTask<Void> setTheme(Theme theme) {
		return this.set(Setting.APP_THEME, theme != null ? theme.getName() : Theme.DEFAULT.getName());
	}
	
	public ObservableMap<Setting, Object> getSettings() {
		return this.roSettings;
	}
	
	public ObservableList<Resolution> getResolutions() {
		return this.readonly;
	}
}

package org.praisenter.settings;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Represents a grouping of settings that has its own file.
 * @author William Bittle
 * @param <T> the settings type
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class RootSettings<T extends RootSettings<T>> extends Settings {
	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(RootSettings.class);
	
	/** True if this instance of a settings is a temporary one */
	protected boolean temporary;
	
	/**
	 * Default constructor.
	 */
	public RootSettings() {
		this(new Properties());
	}
	
	/**
	 * Full constructor.
	 * @param properties the properties
	 */
	public RootSettings(Properties properties) {
		super(properties);
		this.temporary = false;
	}
	
	/**
	 * Returns the singleton instance of this class.
	 * @return {@link Settings}
	 */
	protected abstract T getSingletonInstance();
	
	/**
	 * Returns the file name.
	 * @return String
	 */
	protected abstract String getFileName();
	
	/**
	 * Returns the file name and location for this settings class
	 * persistent store.
	 * @return String
	 */
	protected abstract String getFileNameLocation();

	/**
	 * Returns a new instance of this settings object.
	 * @return Settings
	 */
	protected abstract T getNewInstance();

	/**
	 * Sets all the settings back to their defaults.
	 * @throws SettingsException thrown if an error occurred while assigning a setting
	 */
	public abstract void setDefaultSettings() throws SettingsException;
	
	/**
	 * Sets the properties for any {@link PartialSettings}.
	 * @param properties the properties
	 */
	protected void setParialSettingsProperties(Properties properties) {}
	
	/**
	 * Loads the saved settings from the file into this settings object.
	 * <p>
	 * If the settings file doesn't exist, the default settings are applied.
	 */
	protected void load() {
		// check local folder
		File file = new File(this.getFileNameLocation());
		if (file.exists()) {
			// then read from the file
			try {
				Properties properties = new Properties();
				// load the settings from file
				properties.load(new BufferedInputStream(new FileInputStream(file)));
				// set the properties
				this.properties = properties;
				this.setParialSettingsProperties(properties);
				// exit (no need to save)
				LOGGER.info("Configuration file [" + this.getFileNameLocation() + "] found and loaded successfully.");
				return;
			} catch (FileNotFoundException e) {
				LOGGER.error("The configuration file: '" + file.getAbsolutePath() + "' cannot be read from or is a directory.", e);
			} catch (IOException e) {
				LOGGER.error("An IO error occurred when reading the file: '" + file.getAbsolutePath() + "'", e);
			}
		}
		
		// then check for the default config in the classpath
		LOGGER.warn("Configuration file [" + this.getFileNameLocation() + "] was not found. Using default configuration.");
		InputStream is = RootSettings.class.getResourceAsStream("/" + this.getFileName());
		if (is != null) {
			try {
				// then use the classpath config
				Properties properties = new Properties();
				// load the settings from file
				properties.load(new BufferedInputStream(is));
				// set the properties
				this.properties = properties;
				this.setParialSettingsProperties(properties);
				LOGGER.info("Classpath configuration file [" + this.getFileName() + "] was found and loaded successfully.");
				
				// save the config to a file
				try {
					this.save();
					LOGGER.info("Classpath configuration file [" + this.getFileName() + "] was copied to [" + this.getFileNameLocation() + "] successfully.");
				} catch (SettingsException e) {
					LOGGER.error(e);
				}
				return;
			}  catch (IOException e) {
				LOGGER.error("An IO error occurred when reading the file: 'classpath/" + this.getFileName() + "'", e);
			}
		} else {
			LOGGER.warn("Classpath configuration file [" + this.getFileName() + "] was not found.");
		}
		
		// then use default configuration
		try {
			// set default props
			this.setDefaultSettings();
			
			// save the config to a file
			try {
				this.save();
				LOGGER.info("Default configuration file [" + this.getFileName() + "] was saved to [" + this.getFileNameLocation() + "] successfully.");
			} catch (SettingsException e) {
				LOGGER.error(e);
			}
		} catch (SettingsException e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * Saves the settings to a persistent store and returns true if successful.
	 * <p>
	 * If this settings object is a temporary settings object, then this method will
	 * also update the settings singleton.
	 * @throws SettingsException thrown if an error occurred while saving the settings
	 */
	public void save() throws SettingsException {
		// check for temporary settings
		if (this.temporary) {
			// copy settings over to the current instance
			T settings = this.getSingletonInstance();
			// clear the settings cache
			settings.settings.clear();
			// set the settings again
			settings.setSettings(this);
		}
		this.save(this.getFileNameLocation());
	}
	
	/**
	 * Saves this settings object to the specified file.
	 * @param fileNameLocation the file name and location to save the file
	 * @throws SettingsException if an exception occurs during saving
	 */
	protected void save(String fileNameLocation) throws SettingsException {
		// get a handle on the file
		File file = new File(fileNameLocation);
		// see if it exists
		if (!file.exists()) {
			LOGGER.info("File: [" + file.getAbsolutePath() + "] was not found. Creating new file.");
			// if not then create it
			try {
				if (file.createNewFile()) {
					LOGGER.info("File: [" + file.getAbsolutePath() + "] created successfully.");
				} else {
					LOGGER.warn("Settings could not be created, file: [" + file.getAbsolutePath() + "] already exists.");
				}
			} catch (IOException e) {
				throw new SettingsException("An error occurred while creating the file: [" + file.getAbsolutePath() + "]", e);
			}
		}
		
		try {
			// save the settings
			this.properties.store(new BufferedOutputStream(new FileOutputStream(file)), this.getClass().getName());
			LOGGER.info("File: [" + file.getAbsolutePath() + "] saved successfully.");
		} catch (FileNotFoundException e) {
			throw new SettingsException("The file: [" + file.getAbsolutePath() + "] was not found.  Cannot save settings.", e);
		} catch (IOException e) {
			throw new SettingsException("An error occurred while writting to the file: [" + file.getAbsolutePath() + "]", e);
		}
	}
	
	/**
	 * Returns a copy of the settings object to allow modifications
	 * without changing the current settings.
	 * @return E
	 * @throws SettingsException thrown if an error occurred while assigning a setting
	 */
	public T getTemporarySettings() throws SettingsException {
		// create a new settings object
		T settings = this.getNewInstance();
		// set the temporary flag
		settings.temporary = true;
		// copy the settings over (this does a string copy so we dont have to
		// worry about deep/shallow copy problems)
		settings.setSettings(this);
		// return the new settings object
		return settings;
	}
}

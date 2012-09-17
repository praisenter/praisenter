package org.praisenter.settings;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.praisenter.transitions.Swap;
import org.praisenter.utilities.WindowUtilities;

/**
 * General settings for the application.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class GeneralSettings extends RootSettings<GeneralSettings> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(GeneralSettings.class);
	
	/** The file name */
	private static final String FILE_NAME = "GeneralSettings.properties";
	
	/** The settings file location and name */
	private static final String FILE_NAME_LOCATION = "config/" + FILE_NAME;
	
	/** The instance of the settings */
	private static final GeneralSettings instance = GeneralSettings.loadSettings();
	
	// settings keys
	
	/** Property key for the primary display id */
	private static final String KEY_PRIMARY_DISPLAY_ID = "Display.Primary.Id";
	
	/** Property key for the primary display size */
	private static final String KEY_PRIMARY_DISPLAY_SIZE = "Display.Primary.Size";
	
	/** Property key for the default send transition */
	private static final String KEY_DEFAULT_SEND_TRANSITION = "Send.Transition.Default";
	
	/** Property key for the default send transition duration */
	private static final String KEY_DEFAULT_SEND_TRANSITION_DURATION = "Send.Transition.Duration.Default";
	
	/** Property key for the default clear transition */
	private static final String KEY_DEFAULT_CLEAR_TRANSITION = "Clear.Transition.Default";
	
	/** Property key for the default clear transition duration */
	private static final String KEY_DEFAULT_CLEAR_TRANSITION_DURATION = "Clear.Transition.Duration.Default";

	/** Property key for the default bible id */
	private static final String KEY_DEFAULT_BIBLE_ID = "Bible.Default";
	
	/** Property key to include/exclude the apocrypha books */
	private static final String KEY_INCLUDE_APOCRYPHA = "Bible.IncludeApocrypha";
	
	/**
	 * Returns the instance of the {@link GeneralSettings}.
	 * @return {@link GeneralSettings}
	 */
	public static final GeneralSettings getInstance() {
		return GeneralSettings.instance;
	}
	
	/**
	 * Loads the {@link GeneralSettings}.
	 * @return {@link GeneralSettings}
	 */
	private static final GeneralSettings loadSettings() {
		// create a new settings instance
		GeneralSettings settings = new GeneralSettings();
		// load the saved settings or default settings
		settings.load();
		// return the default settings
		return settings;
	}
	
	/**
	 * Default constructor.
	 */
	private GeneralSettings() {}
	
	/**
	 * Full constructor.
	 * @param properties the initial properties
	 */
	private GeneralSettings(Properties properties) {
		super(properties);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getFileNameLocation()
	 */
	@Override
	protected String getFileNameLocation() {
		return GeneralSettings.FILE_NAME_LOCATION;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getNewInstance()
	 */
	@Override
	protected GeneralSettings getNewInstance() {
		return new GeneralSettings();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getSingletonInstance()
	 */
	@Override
	protected GeneralSettings getSingletonInstance() {
		return GeneralSettings.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#setDefaultSettings()
	 */
	@Override
	public void setDefaultSettings() throws SettingsException {
		GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
		if (devices.length > 1) {
			// set the second display as the default
			this.setPrimaryDisplay(devices[1]);
		} else {
			this.setPrimaryDisplay(null);
		}
		this.setApocryphaIncluded(false);
		this.setDefaultBibleId(0);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setProperties(java.util.Properties)
	 */
	@Override
	protected void setParialSettingsProperties(Properties properties) {}
	
	// settings methods
	
	/**
	 * Returns the primary display device.
	 * <p>
	 * If the primary display device does not exist or is not set this method will return null.
	 * @return GraphicsDevice
	 */
	public GraphicsDevice getPrimaryDisplay() {
		return this.getGraphicsDevice(GeneralSettings.KEY_PRIMARY_DISPLAY_ID);
	}
	
	/**
	 * Returns the primary display device or the default device if the primary isn't set or doesn't
	 * exist.
	 * <p>
	 * This method will never return null.
	 * @return {@link GraphicsDevice}
	 */
	public GraphicsDevice getPrimaryOrDefaultDisplay() {
		GraphicsDevice device = this.getGraphicsDevice(GeneralSettings.KEY_PRIMARY_DISPLAY_ID);
		if (device == null) {
			return WindowUtilities.getSecondaryDevice();
		}
		return device;
	}
	
	/**
	 * Sets the primary display device.
	 * @param device the device
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setPrimaryDisplay(GraphicsDevice device) throws SettingsException {
		this.setSetting(GeneralSettings.KEY_PRIMARY_DISPLAY_ID, device);
		this.setPrimaryDisplaySize(device);
	}
	
	/**
	 * Clears the primary display cache.
	 * <p>
	 * This is useful when the available devices has been modified.
	 */
	public void clearPrimaryDisplayCache() {
		this.settings.remove(GeneralSettings.KEY_PRIMARY_DISPLAY_ID);
	}
	
	/**
	 * Returns the primary display's size.
	 * <p>
	 * If the primary display is not available or not assigned we return the size of the
	 * default primary display.
	 * @return Dimension
	 */
	public Dimension getPrimaryDisplaySize() {
		Dimension size = this.getDimensionSetting(GeneralSettings.KEY_PRIMARY_DISPLAY_SIZE);
		if (size == null) {
			GraphicsDevice device = this.getPrimaryOrDefaultDisplay();
			try {
				this.setPrimaryDisplay(device);
			} catch (SettingsException e) {
				// just log the error
				LOGGER.error(e);
			}
		}
		return size;
	}
	
	/**
	 * Sets the primary display's size.
	 * <p>
	 * This should only be called from the {@link #setPrimaryDisplay(GraphicsDevice)} method to
	 * keep them in sync.  This setting is primarily for retaining the chosen display size even
	 * when the chosen display no longer exists.
	 * @param device the primary display device
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	private void setPrimaryDisplaySize(GraphicsDevice device) throws SettingsException {
		DisplayMode mode = device.getDisplayMode();
		Dimension size = WindowUtilities.getDimension(mode);
		// set the setting
		this.setSetting(GeneralSettings.KEY_PRIMARY_DISPLAY_SIZE, size);
	}
	
	/**
	 * Returns the default bible id.
	 * <p>
	 * Returns zero if not set.
	 * @return the default bible id
	 */
	public int getDefaultBibleId() {
		return this.getIntegerSetting(KEY_DEFAULT_BIBLE_ID);
	}
	
	/**
	 * Sets the default bible id.
	 * @param id the bible id
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setDefaultBibleId(int id) throws SettingsException {
		this.setSetting(KEY_DEFAULT_BIBLE_ID, id);
	}
	
	/**
	 * Returns true if the apocrypha should be included.
	 * @return boolean
	 */
	public boolean isApocryphaIncluded() {
		return this.getBooleanSetting(KEY_INCLUDE_APOCRYPHA);
	}
	
	/**
	 * Sets whether the apocrypha should be included or not.
	 * @param flag true if the apocrypha should be included
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setApocryphaIncluded(boolean flag) throws SettingsException {
		this.setSetting(KEY_INCLUDE_APOCRYPHA, flag);
	}
	
	public String getDefaultSendTransition() {
		String st = this.getStringSetting(KEY_DEFAULT_SEND_TRANSITION);
		if (st == null) {
			st = Swap.class.getSimpleName();
		}
		return st;
	}
	
	public void setDefaultSendTransition(String name) throws SettingsException {
		this.setSetting(KEY_DEFAULT_SEND_TRANSITION, name);
	}
	
	public String getDefaultClearTransition() {
		String ct = this.getStringSetting(KEY_DEFAULT_CLEAR_TRANSITION);
		if (ct == null) {
			ct = Swap.class.getSimpleName();
		}
		return ct;
	}
	
	public void setDefaultClearTransition(String name) throws SettingsException {
		this.setSetting(KEY_DEFAULT_CLEAR_TRANSITION, name);
	}
	
	public int getDefaultSendTransitionDuration() {
		return this.getIntegerSetting(KEY_DEFAULT_SEND_TRANSITION_DURATION);
	}
	
	public void setDefaultSendTransitionDuration(int duration) throws SettingsException {
		this.setSetting(KEY_DEFAULT_SEND_TRANSITION_DURATION, duration);
	}

	public int getDefaultClearTransitionDuration() {
		return this.getIntegerSetting(KEY_DEFAULT_CLEAR_TRANSITION_DURATION);
	}
	
	public void setDefaultClearTransitionDuration(int duration) throws SettingsException {
		this.setSetting(KEY_DEFAULT_CLEAR_TRANSITION_DURATION, duration);
	}
}

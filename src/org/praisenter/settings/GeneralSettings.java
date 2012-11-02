package org.praisenter.settings;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.praisenter.display.RenderQuality;
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
	
	/** The instance of the settings */
	private static final GeneralSettings instance = GeneralSettings.loadSettings();
	
	// settings keys
	
	/** Property key for the primary display id */
	private static final String KEY_PRIMARY_DISPLAY_ID = "Display.Primary.Id";
	
	/** Property key for the primary display size */
	private static final String KEY_PRIMARY_DISPLAY_SIZE = "Display.Primary.Size";
	
	/** Property key for the render quality */
	private static final String KEY_RENDER_QUALITY = "Display.Render.Quality";
	
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
	 * @see org.praisenter.settings.RootSettings#getFileName()
	 */
	@Override
	protected String getFileName() {
		return GeneralSettings.FILE_NAME;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getNewInstance()
	 */
	@Override
	protected GeneralSettings getNewInstance() {
		return new GeneralSettings();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getSingletonInstance()
	 */
	@Override
	protected GeneralSettings getSingletonInstance() {
		return GeneralSettings.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setDefaultSettings()
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
	}
	
	// settings methods
	
	/**
	 * Returns the primary display device.
	 * <p>
	 * If the primary display device does not exist or is not set this method will return null.
	 * @return GraphicsDevice
	 */
	public GraphicsDevice getPrimaryDisplay() {
		return this.getGraphicsDeviceSetting(GeneralSettings.KEY_PRIMARY_DISPLAY_ID);
	}
	
	/**
	 * Returns the primary display device or the default device if the primary isn't set or doesn't
	 * exist.
	 * <p>
	 * This method will never return null.
	 * @return {@link GraphicsDevice}
	 */
	public GraphicsDevice getPrimaryOrDefaultDisplay() {
		GraphicsDevice device = this.getGraphicsDeviceSetting(GeneralSettings.KEY_PRIMARY_DISPLAY_ID);
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
				return this.getDimensionSetting(GeneralSettings.KEY_PRIMARY_DISPLAY_SIZE);
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
	 * Returns the render quality.
	 * @return {@link RenderQuality}
	 */
	public RenderQuality getRenderQuality() {
		return this.getRenderQualitySetting(KEY_RENDER_QUALITY);
	}
	
	/**
	 * Sets the render quality.
	 * @param quality the render quality
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setRenderQuality(RenderQuality quality) throws SettingsException {
		this.setSetting(KEY_RENDER_QUALITY, quality);
	}
}

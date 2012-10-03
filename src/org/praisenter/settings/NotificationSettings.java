package org.praisenter.settings;

import java.awt.Color;
import java.awt.Font;
import java.util.Properties;

import org.praisenter.display.CompositeType;
import org.praisenter.display.FontScaleType;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;
import org.praisenter.display.TextAlignment;
import org.praisenter.utilities.ColorUtilities;
import org.praisenter.utilities.FontManager;

/**
 * Settings for a display that shows bible verses.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class NotificationSettings extends RootSettings<NotificationSettings> {
	/** The file name */
	private static final String FILE_NAME = "NotificationSettings.properties";
	
	/** The instance of the settings */
	private static final NotificationSettings instance = NotificationSettings.loadSettings();

	// settings keys
	
	/** Property key for the default wait period */
	private static final String KEY_DEFAULT_WAIT_PERIOD = "Notification.WaitPeriod";
	
	/**
	 * Returns the instance of the {@link NotificationSettings}.
	 * @return {@link NotificationSettings}
	 */
	public static final NotificationSettings getInstance() {
		return NotificationSettings.instance;
	}
	
	/**
	 * Loads the {@link NotificationSettings}.
	 * @return {@link NotificationSettings}
	 */
	private static final NotificationSettings loadSettings() {
		// create a new settings instance
		NotificationSettings settings = new NotificationSettings();
		// load the saved settings or default settings
		settings.load();
		// return the default settings
		return settings;
	}
	
	/** The text {@link PartialSettings} */
	protected TextComponentSettings textSettings;
	
	/**
	 * Default constructor.
	 */
	private NotificationSettings() {
		this(new Properties());
	}
	
	/**
	 * Full constructor.
	 * @param properties the initial properties
	 */
	private NotificationSettings(Properties properties) {
		super(properties);
		// no prefix for the background
		this.textSettings = new TextComponentSettings("Notification", this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setDefaultSettings()
	 */
	@Override
	public void setDefaultSettings() throws SettingsException {
		// general
		this.textSettings.setBounds(null);
		this.textSettings.setVisible(true);
		// color
		this.textSettings.setBackgroundColor(ColorUtilities.TRANSPARENT);
		this.textSettings.setBackgroundColorCompositeType(CompositeType.UNDERLAY);
		this.textSettings.setBackgroundColorVisible(false);
		// image
		this.textSettings.setBackgroundImage(null);
		this.textSettings.setBackgroundImageVisible(false);
		this.textSettings.setBackgroundImageScaleQuality(ScaleQuality.BILINEAR);
		this.textSettings.setBackgroundImageScaleType(ScaleType.NONUNIFORM);
		// text
		this.textSettings.setTextColor(Color.WHITE);
		this.textSettings.setTextFont(FontManager.getDefaultFont().deriveFont(Font.BOLD, 50));
		this.textSettings.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		this.textSettings.setTextAlignment(TextAlignment.CENTER);
		this.textSettings.setTextWrapped(true);
		this.textSettings.setPadding(0);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getFileName()
	 */
	@Override
	protected String getFileName() {
		return NotificationSettings.FILE_NAME;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getNewInstance()
	 */
	@Override
	protected NotificationSettings getNewInstance() {
		return new NotificationSettings();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getSingletonInstance()
	 */
	@Override
	protected NotificationSettings getSingletonInstance() {
		return NotificationSettings.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setParialSettingsProperties(java.util.Properties)
	 */
	@Override
	protected void setParialSettingsProperties(Properties properties) {
		this.textSettings.properties = properties;
	}
	
	/**
	 * Returns the text {@link PartialSettings}.
	 * @return {@link TextComponentSettings}
	 */
	public TextComponentSettings getTextSettings() {
		return this.textSettings;
	}
	
	/**
	 * Returns the default wait period.
	 * @return int
	 */
	public int getDefaultWaitPeriod() {
		return this.getIntegerSetting(KEY_DEFAULT_WAIT_PERIOD);
	}
	
	/**
	 * Sets the default wait period.
	 * @param millis the default wait period in milliseconds
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setDefaultWaitPeriod(int millis) throws SettingsException {
		this.setSetting(KEY_DEFAULT_WAIT_PERIOD, millis);
	}
}

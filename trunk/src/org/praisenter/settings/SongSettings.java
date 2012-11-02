package org.praisenter.settings;

import java.awt.Color;
import java.awt.Font;
import java.util.Properties;

import org.praisenter.display.CompositeType;
import org.praisenter.display.FontScaleType;
import org.praisenter.display.GraphicsComponent;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;
import org.praisenter.display.HorizontalTextAlignment;
import org.praisenter.display.VerticalTextAlignment;
import org.praisenter.transitions.Fade;
import org.praisenter.transitions.easing.CubicEasing;
import org.praisenter.utilities.ColorUtilities;
import org.praisenter.utilities.FontManager;

/**
 * Settings for a display that shows songs.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SongSettings extends RootSettings<SongSettings> {
	/** The file name */
	private static final String FILE_NAME = "SongSettings.properties";
	
	/** The instance of the settings */
	private static final SongSettings instance = SongSettings.loadSettings();

	// settings keys

	/** Property key for the default send transition */
	private static final String KEY_DEFAULT_SEND_TRANSITION = "Songs.Send.Transition.Default";
	
	/** Property key for the default send transition duration */
	private static final String KEY_DEFAULT_SEND_TRANSITION_DURATION = "Songs.Send.Transition.Duration.Default";

	/** Property key for the send transition easing */
	private static final String KEY_SEND_EASING = "Songs.Send.Easing";
	
	/** Property key for the default clear transition */
	private static final String KEY_DEFAULT_CLEAR_TRANSITION = "Songs.Clear.Transition.Default";
	
	/** Property key for the default clear transition duration */
	private static final String KEY_DEFAULT_CLEAR_TRANSITION_DURATION = "Songs.Clear.Transition.Duration.Default";

	/** Property key for the clear transition easing */
	private static final String KEY_CLEAR_EASING = "Songs.Clear.Easing";
	
	/**
	 * Returns the instance of the {@link SongSettings}.
	 * @return {@link SongSettings}
	 */
	public static final SongSettings getInstance() {
		return SongSettings.instance;
	}
	
	/**
	 * Loads the {@link SongSettings}.
	 * @return {@link SongSettings}
	 */
	private static final SongSettings loadSettings() {
		// create a new settings instance
		SongSettings settings = new SongSettings();
		// load the saved settings or default settings
		settings.load();
		// return the default settings
		return settings;
	}
	
	/** The {@link PartialSettings} for the image background */
	protected GraphicsComponentSettings<GraphicsComponent> backgroundSettings;
	
	/** The text {@link PartialSettings} */
	protected TextComponentSettings textSettings;
	
	/**
	 * Default constructor.
	 */
	private SongSettings() {
		this(new Properties());
	}
	
	/**
	 * Full constructor.
	 * @param properties the initial properties
	 */
	private SongSettings(Properties properties) {
		super(properties);
		// no prefix for the background
		this.backgroundSettings = new GraphicsComponentSettings<GraphicsComponent>("Background", this);
		this.textSettings = new TextComponentSettings("Text", this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setDefaultSettings()
	 */
	@Override
	public void setDefaultSettings() throws SettingsException {
		this.setDefaultSendTransition(Fade.ID);
		this.setDefaultSendTransitionDuration(400);
		this.setSendEasing(CubicEasing.ID);
		
		this.setDefaultClearTransition(Fade.ID);
		this.setDefaultClearTransitionDuration(300);
		this.setClearEasing(CubicEasing.ID);
		
		// background
		{
			// general
			// don't default bounds
			this.backgroundSettings.setVisible(true);
			// color
			this.backgroundSettings.setBackgroundColor(Color.BLUE);
			this.backgroundSettings.setBackgroundColorCompositeType(CompositeType.UNDERLAY);
			this.backgroundSettings.setBackgroundColorVisible(true);
			// image
			this.backgroundSettings.setBackgroundImageFileName(null);
			this.backgroundSettings.setBackgroundImageVisible(false);
			this.backgroundSettings.setBackgroundImageScaleQuality(ScaleQuality.BILINEAR);
			this.backgroundSettings.setBackgroundImageScaleType(ScaleType.NONUNIFORM);
		}
		
		// text
		{
			// general
			this.textSettings.setBounds(null);
			this.textSettings.setVisible(true);
			// color
			this.textSettings.setBackgroundColor(ColorUtilities.TRANSPARENT);
			this.textSettings.setBackgroundColorCompositeType(CompositeType.UNDERLAY);
			this.textSettings.setBackgroundColorVisible(false);
			// image
			this.textSettings.setBackgroundImageFileName(null);
			this.textSettings.setBackgroundImageVisible(false);
			this.textSettings.setBackgroundImageScaleQuality(ScaleQuality.BILINEAR);
			this.textSettings.setBackgroundImageScaleType(ScaleType.NONUNIFORM);
			// text
			this.textSettings.setTextColor(Color.WHITE);
			this.textSettings.setTextFont(FontManager.getDefaultFont().deriveFont(Font.BOLD, 50));
			this.textSettings.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
			this.textSettings.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
			this.textSettings.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
			this.textSettings.setTextWrapped(false);
			this.textSettings.setPadding(0);
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getFileName()
	 */
	@Override
	protected String getFileName() {
		return SongSettings.FILE_NAME;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getNewInstance()
	 */
	@Override
	protected SongSettings getNewInstance() {
		return new SongSettings();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getSingletonInstance()
	 */
	@Override
	protected SongSettings getSingletonInstance() {
		return SongSettings.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setParialSettingsProperties(java.util.Properties)
	 */
	@Override
	protected void setParialSettingsProperties(Properties properties) {
		this.backgroundSettings.properties = properties;
		this.textSettings.properties = properties;
	}
	
	/**
	 * Returns the image background {@link PartialSettings}.
	 * @return {@link GraphicsComponentSettings}
	 */
	public GraphicsComponentSettings<GraphicsComponent> getBackgroundSettings() {
		return backgroundSettings;
	}
	
	/**
	 * Returns the scripture title {@link PartialSettings}.
	 * @return {@link TextComponentSettings}
	 */
	public TextComponentSettings getTextSettings() {
		return this.textSettings;
	}
	
	/**
	 * Returns the default send transition id.
	 * @return int
	 */
	public int getDefaultSendTransition() {
		return this.getIntegerSetting(KEY_DEFAULT_SEND_TRANSITION);
	}
	
	/**
	 * Sets the default send transition.
	 * @param id the transition id
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setDefaultSendTransition(int id) throws SettingsException {
		this.setSetting(KEY_DEFAULT_SEND_TRANSITION, id);
	}
	
	/**
	 * Returns the default clear transition.
	 * @return int
	 */
	public int getDefaultClearTransition() {
		return this.getIntegerSetting(KEY_DEFAULT_CLEAR_TRANSITION);
	}
	
	/**
	 * Sets the default clear transition.
	 * @param id the transition id
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setDefaultClearTransition(int id) throws SettingsException {
		this.setSetting(KEY_DEFAULT_CLEAR_TRANSITION, id);
	}
	
	/**
	 * Returns the default send transition duration.
	 * @return int
	 */
	public int getDefaultSendTransitionDuration() {
		return this.getIntegerSetting(KEY_DEFAULT_SEND_TRANSITION_DURATION);
	}
	
	/**
	 * Sets the default send transition duration.
	 * @param duration the duration in milliseconds
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setDefaultSendTransitionDuration(int duration) throws SettingsException {
		this.setSetting(KEY_DEFAULT_SEND_TRANSITION_DURATION, duration);
	}

	/**
	 * Returns the default clear transition duration.
	 * @return int
	 */
	public int getDefaultClearTransitionDuration() {
		return this.getIntegerSetting(KEY_DEFAULT_CLEAR_TRANSITION_DURATION);
	}
	
	/**
	 * Sets the default clear transition duration.
	 * @param duration the duration in milliseconds
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setDefaultClearTransitionDuration(int duration) throws SettingsException {
		this.setSetting(KEY_DEFAULT_CLEAR_TRANSITION_DURATION, duration);
	}
	
	/**
	 * Returns the send transition easing.
	 * @return int
	 */
	public int getSendEasing() {
		return this.getIntegerSetting(KEY_SEND_EASING);
	}
	
	/**
	 * Sets the send transition easing.
	 * @param id the easing id
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setSendEasing(int id) throws SettingsException {
		this.setSetting(KEY_SEND_EASING, id);
	}

	/**
	 * Returns the clear transition easing.
	 * @return int
	 */
	public int getClearEasing() {
		return this.getIntegerSetting(KEY_CLEAR_EASING);
	}
	
	/**
	 * Sets the clear transition easing.
	 * @param id the easing id
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setClearEasing(int id) throws SettingsException {
		this.setSetting(KEY_CLEAR_EASING, id);
	}
}

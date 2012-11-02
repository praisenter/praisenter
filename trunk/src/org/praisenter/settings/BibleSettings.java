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
 * Settings for a display that shows bible verses.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class BibleSettings extends RootSettings<BibleSettings> {
	/** The file name */
	private static final String FILE_NAME = "BibleSettings.properties";
	
	/** The instance of the settings */
	private static final BibleSettings instance = BibleSettings.loadSettings();

	// settings keys
	
	/** Property key for the default bible id */
	private static final String KEY_DEFAULT_PRIMARY_BIBLE_ID = "Bible.Primary.Default";
	
	/** Property key for the default secondary bible id */
	private static final String KEY_DEFAULT_SECONDARY_BIBLE_ID = "Bible.Secondary.Default";
	
	/** Property key to use/not use a second bible */
	private static final String KEY_USE_SECONDARY_BIBLE = "Bible.Secondary.Use";

	/** Property key to include/exclude the apocrypha books */
	private static final String KEY_INCLUDE_APOCRYPHA = "Bible.IncludeApocrypha";
	
	/** Property key for the default send transition */
	private static final String KEY_DEFAULT_SEND_TRANSITION = "Bible.Send.Transition.Default";
	
	/** Property key for the default send transition duration */
	private static final String KEY_DEFAULT_SEND_TRANSITION_DURATION = "Bible.Send.Transition.Duration.Default";
	
	/** Property key for the send transition easing */
	private static final String KEY_SEND_EASING = "Bible.Send.Easing";
	
	/** Property key for the default clear transition */
	private static final String KEY_DEFAULT_CLEAR_TRANSITION = "Bible.Clear.Transition.Default";
	
	/** Property key for the default clear transition duration */
	private static final String KEY_DEFAULT_CLEAR_TRANSITION_DURATION = "Bible.Clear.Transition.Duration.Default";
	
	/** Property key for the clear transition easing */
	private static final String KEY_CLEAR_EASING = "Bible.Clear.Easing";
	
	/**
	 * Returns the instance of the {@link BibleSettings}.
	 * @return {@link BibleSettings}
	 */
	public static final BibleSettings getInstance() {
		return BibleSettings.instance;
	}
	
	/**
	 * Loads the {@link BibleSettings}.
	 * @return {@link BibleSettings}
	 */
	private static final BibleSettings loadSettings() {
		// create a new settings instance
		BibleSettings settings = new BibleSettings();
		// load the saved settings or default settings
		settings.load();
		// return the default settings
		return settings;
	}
	
	/** The {@link PartialSettings} for the image background */
	protected GraphicsComponentSettings<GraphicsComponent> backgroundSettings;
	
	/** The scripture title {@link PartialSettings} */
	protected TextComponentSettings scriptureTitleSettings;
	
	/** The scripture text {@link PartialSettings} */
	protected TextComponentSettings scriptureTextSettings;
	
	
	/**
	 * Default constructor.
	 */
	private BibleSettings() {
		this(new Properties());
	}
	
	/**
	 * Full constructor.
	 * @param properties the initial properties
	 */
	private BibleSettings(Properties properties) {
		super(properties);
		// no prefix for the background
		this.backgroundSettings = new GraphicsComponentSettings<GraphicsComponent>("Background", this);
		this.scriptureTitleSettings = new TextComponentSettings("ScriptureTitle", this);
		this.scriptureTextSettings = new TextComponentSettings("ScriptureText", this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setDefaultSettings()
	 */
	@Override
	public void setDefaultSettings() throws SettingsException {
		this.setApocryphaIncluded(false);
		this.setDefaultPrimaryBibleId(0);
		this.setDefaultSecondaryBibleId(0);
		this.setSecondaryBibleInUse(false);
		
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
		
		// scripture title
		{
			// general
			this.scriptureTitleSettings.setBounds(null);
			this.scriptureTitleSettings.setVisible(true);
			// color
			this.scriptureTitleSettings.setBackgroundColor(ColorUtilities.TRANSPARENT);
			this.scriptureTitleSettings.setBackgroundColorCompositeType(CompositeType.UNDERLAY);
			this.scriptureTitleSettings.setBackgroundColorVisible(false);
			// image
			this.scriptureTitleSettings.setBackgroundImageFileName(null);
			this.scriptureTitleSettings.setBackgroundImageVisible(false);
			this.scriptureTitleSettings.setBackgroundImageScaleQuality(ScaleQuality.BILINEAR);
			this.scriptureTitleSettings.setBackgroundImageScaleType(ScaleType.NONUNIFORM);
			// text
			this.scriptureTitleSettings.setTextColor(Color.WHITE);
			this.scriptureTitleSettings.setTextFont(FontManager.getDefaultFont().deriveFont(Font.BOLD, 50));
			this.scriptureTitleSettings.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
			this.scriptureTitleSettings.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
			this.scriptureTitleSettings.setVerticalTextAlignment(VerticalTextAlignment.TOP);
			this.scriptureTitleSettings.setTextWrapped(false);
			this.scriptureTitleSettings.setPadding(0);
		}
		
		// scripture text
		{
			// general
			this.scriptureTextSettings.setBounds(null);
			this.scriptureTextSettings.setVisible(true);
			// color
			this.scriptureTextSettings.setBackgroundColor(ColorUtilities.TRANSPARENT);
			this.scriptureTextSettings.setBackgroundColorCompositeType(CompositeType.UNDERLAY);
			this.scriptureTextSettings.setBackgroundColorVisible(false);
			// image
			this.scriptureTextSettings.setBackgroundImageFileName(null);
			this.scriptureTextSettings.setBackgroundImageVisible(false);
			this.scriptureTextSettings.setBackgroundImageScaleQuality(ScaleQuality.BILINEAR);
			this.scriptureTextSettings.setBackgroundImageScaleType(ScaleType.NONUNIFORM);
			// text
			this.scriptureTextSettings.setTextColor(Color.WHITE);
			this.scriptureTextSettings.setTextFont(FontManager.getDefaultFont().deriveFont(Font.BOLD, 40));
			this.scriptureTextSettings.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
			this.scriptureTextSettings.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
			this.scriptureTextSettings.setVerticalTextAlignment(VerticalTextAlignment.TOP);
			this.scriptureTextSettings.setTextWrapped(true);
			this.scriptureTextSettings.setPadding(0);
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getFileName()
	 */
	@Override
	protected String getFileName() {
		return BibleSettings.FILE_NAME;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getNewInstance()
	 */
	@Override
	protected BibleSettings getNewInstance() {
		return new BibleSettings();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#getSingletonInstance()
	 */
	@Override
	protected BibleSettings getSingletonInstance() {
		return BibleSettings.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setParialSettingsProperties(java.util.Properties)
	 */
	@Override
	protected void setParialSettingsProperties(Properties properties) {
		this.backgroundSettings.properties = properties;
		this.scriptureTitleSettings.properties = properties;
		this.scriptureTextSettings.properties = properties;
	}

	/**
	 * Returns the default bible id.
	 * <p>
	 * Returns zero if not set.
	 * @return the default bible id
	 */
	public int getDefaultPrimaryBibleId() {
		return this.getIntegerSetting(KEY_DEFAULT_PRIMARY_BIBLE_ID);
	}
	
	/**
	 * Sets the default bible id.
	 * @param id the bible id
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setDefaultPrimaryBibleId(int id) throws SettingsException {
		this.setSetting(KEY_DEFAULT_PRIMARY_BIBLE_ID, id);
	}

	/**
	 * Returns the default secondary bible id.
	 * <p>
	 * Returns zero if not set.
	 * @return the default secondary bible id
	 */
	public int getDefaultSecondaryBibleId() {
		return this.getIntegerSetting(KEY_DEFAULT_SECONDARY_BIBLE_ID);
	}
	
	/**
	 * Sets the default secondary bible id.
	 * @param id the bible id
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setDefaultSecondaryBibleId(int id) throws SettingsException {
		this.setSetting(KEY_DEFAULT_SECONDARY_BIBLE_ID, id);
	}
	
	/**
	 * Returns true if the secondary bible should be used in addition to the primary.
	 * @return boolean
	 */
	public boolean isSecondaryBibleInUse() {
		return this.getBooleanSetting(KEY_USE_SECONDARY_BIBLE);
	}
	
	/**
	 * Sets whether the secondary bible should be used in addition to the primary.
	 * @param flag true if the secondary bible should be used
	 * @throws SettingsException if an exception occurs while assigning the setting
	 */
	public void setSecondaryBibleInUse(boolean flag) throws SettingsException {
		this.setSetting(KEY_USE_SECONDARY_BIBLE, flag);
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
	public TextComponentSettings getScriptureTitleSettings() {
		return this.scriptureTitleSettings;
	}
	
	/**
	 * Returns the scripture text {@link PartialSettings}.
	 * @return {@link TextComponentSettings}
	 */
	public TextComponentSettings getScriptureTextSettings() {
		return this.scriptureTextSettings;
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

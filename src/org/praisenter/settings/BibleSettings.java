package org.praisenter.settings;

import java.awt.Color;
import java.awt.Font;
import java.util.Properties;

import org.praisenter.display.CompositeType;
import org.praisenter.display.FontScaleType;
import org.praisenter.display.GraphicsComponent;
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
			this.backgroundSettings.setBackgroundImage(null);
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
			this.scriptureTitleSettings.setBackgroundImage(null);
			this.scriptureTitleSettings.setBackgroundImageVisible(false);
			this.scriptureTitleSettings.setBackgroundImageScaleQuality(ScaleQuality.BILINEAR);
			this.scriptureTitleSettings.setBackgroundImageScaleType(ScaleType.NONUNIFORM);
			// text
			this.scriptureTitleSettings.setTextColor(Color.WHITE);
			this.scriptureTitleSettings.setTextFont(FontManager.getDefaultFont().deriveFont(Font.BOLD, 50));
			this.scriptureTitleSettings.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
			this.scriptureTitleSettings.setTextAlignment(TextAlignment.LEFT);
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
			this.scriptureTextSettings.setBackgroundImage(null);
			this.scriptureTextSettings.setBackgroundImageVisible(false);
			this.scriptureTextSettings.setBackgroundImageScaleQuality(ScaleQuality.BILINEAR);
			this.scriptureTextSettings.setBackgroundImageScaleType(ScaleType.NONUNIFORM);
			// text
			this.scriptureTextSettings.setTextColor(Color.WHITE);
			this.scriptureTextSettings.setTextFont(FontManager.getDefaultFont().deriveFont(Font.BOLD, 40));
			this.scriptureTextSettings.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
			this.scriptureTextSettings.setTextAlignment(TextAlignment.CENTER);
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
}

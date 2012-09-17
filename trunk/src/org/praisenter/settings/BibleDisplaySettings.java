package org.praisenter.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Properties;

import org.praisenter.display.BibleDisplay;
import org.praisenter.display.ColorBackgroundComponent;
import org.praisenter.display.FontScaleType;
import org.praisenter.display.ImageBackgroundComponent;
import org.praisenter.display.TextAlignment;
import org.praisenter.display.TextComponent;
import org.praisenter.resources.Messages;
import org.praisenter.utilities.FontManager;

/**
 * Settings for a display that shows bible verses.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class BibleDisplaySettings extends DisplaySettings<BibleDisplaySettings, BibleDisplay> {
	// TODO i dont like that these settings are so specific to displays
	/** The file name */
	private static final String FILE_NAME = "BibleDisplaySettings.properties";
	
	/** The settings file location and name */
	private static final String FILE_NAME_LOCATION = "config/" + FILE_NAME;
	
	/** The instance of the settings */
	private static final BibleDisplaySettings instance = BibleDisplaySettings.loadSettings();
	
	/**
	 * Returns the instance of the {@link BibleDisplaySettings}.
	 * @return {@link BibleDisplaySettings}
	 */
	public static final BibleDisplaySettings getInstance() {
		return BibleDisplaySettings.instance;
	}
	
	/**
	 * Loads the {@link BibleDisplaySettings}.
	 * @return {@link BibleDisplaySettings}
	 */
	private static final BibleDisplaySettings loadSettings() {
		// create a new settings instance
		BibleDisplaySettings settings = new BibleDisplaySettings();
		// load the saved settings or default settings
		settings.load();
		// return the default settings
		return settings;
	}
	
	/** The scripture title {@link PartialSettings} */
	protected TextSettings scriptureTitleSettings;
	
	/** The scripture text {@link PartialSettings} */
	protected TextSettings scriptureTextSettings;
	
	
	/**
	 * Default constructor.
	 */
	private BibleDisplaySettings() {
		this(new Properties());
	}
	
	/**
	 * Full constructor.
	 * @param properties the initial properties
	 */
	private BibleDisplaySettings(Properties properties) {
		super(properties);
		this.scriptureTitleSettings = new TextSettings("ScriptureTitle", this);
		this.scriptureTextSettings = new TextSettings("ScriptureText", this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#setDefaultSettings()
	 */
	@Override
	public void setDefaultSettings() throws SettingsException {
		super.setDefaultSettings();
		
		this.scriptureTitleSettings.setTextColor(Color.YELLOW);
		this.scriptureTitleSettings.setTextFont(FontManager.getDefaultFont().deriveFont(Font.BOLD, 50));
		this.scriptureTitleSettings.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		this.scriptureTitleSettings.setTextAlignment(TextAlignment.LEFT);
		this.scriptureTitleSettings.setTextWrapped(false);
		this.scriptureTitleSettings.setBounds(null);
		this.scriptureTitleSettings.setPadding(0);
		this.scriptureTitleSettings.setVisible(true);
		
		this.scriptureTextSettings.setTextColor(Color.WHITE);
		this.scriptureTextSettings.setTextFont(FontManager.getDefaultFont().deriveFont(Font.BOLD, 40));
		this.scriptureTextSettings.setTextFontScaleType(FontScaleType.BEST_FIT);
		this.scriptureTextSettings.setTextAlignment(TextAlignment.CENTER);
		this.scriptureTextSettings.setTextWrapped(true);
		this.scriptureTextSettings.setBounds(null);
		this.scriptureTextSettings.setPadding(0);
		this.scriptureTextSettings.setVisible(true);
	}
	
	/**
	 * Returns the scripture title {@link PartialSettings}.
	 * @return {@link TextSettings}
	 */
	public TextSettings getScriptureTitleSettings() {
		return this.scriptureTitleSettings;
	}
	
	/**
	 * Returns the scripture text {@link PartialSettings}.
	 * @return {@link TextSettings}
	 */
	public TextSettings getScriptureTextSettings() {
		return this.scriptureTextSettings;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getFileNameLocation()
	 */
	@Override
	protected String getFileNameLocation() {
		return BibleDisplaySettings.FILE_NAME_LOCATION;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getNewInstance()
	 */
	@Override
	protected BibleDisplaySettings getNewInstance() {
		return new BibleDisplaySettings();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#getSingletonInstance()
	 */
	@Override
	protected BibleDisplaySettings getSingletonInstance() {
		return BibleDisplaySettings.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.RootSettings#setParialSettingsProperties(java.util.Properties)
	 */
	@Override
	protected void setParialSettingsProperties(Properties properties) {
		this.colorBackgroundSettings.properties = properties;
		this.imageBackgroundSettings.properties = properties;
		this.scriptureTitleSettings.properties = properties;
		this.scriptureTextSettings.properties = properties;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.DisplaySettings#getDisplay(java.awt.Dimension)
	 */
	@Override
	public BibleDisplay getDisplay(Dimension displaySize) {
		// get the minimum dimension (typically the height)
		int maxd = displaySize.height;
		if (maxd > displaySize.width) {
			// the width is smaller so use it
			maxd = displaySize.width;
		}
		// set the default screen to text component padding
		final int margin = (int)Math.floor((double)maxd * 0.04);
		
		BibleDisplay display = new BibleDisplay(displaySize);
		
		// get sub settings
		ColorBackgroundSettings cSet = this.getColorBackgroundSettings();
		ImageBackgroundSettings iSet = this.getImageBackgroundSettings();
		TextSettings tSet = this.getScriptureTitleSettings();
		TextSettings bSet = this.getScriptureTextSettings(); 
		
		// create the default color background
		ColorBackgroundComponent colorBackground = display.createColorBackgroundComponent(Messages.getString("display.basic.background.color"));
		colorBackground.setColor(cSet.getColor());
		colorBackground.setVisible(cSet.isVisible());
		
		// create an empty image background
		ImageBackgroundComponent imageBackground = display.createImageBackgroundComponent(Messages.getString("display.basic.background.image"));
		imageBackground.setImage(iSet.getImage());
		imageBackground.setScaleQuality(iSet.getScaleQuality());
		imageBackground.setScaleType(iSet.getScaleType());
		imageBackground.setVisible(iSet.isVisible());
		
		// compute the default width, height and position
		final int h = displaySize.height - margin * 2;
		final int w = displaySize.width - margin * 2;
		
		final int tth = (int)Math.ceil((double)h * 0.25);
		final int th = h - tth - margin;
		
		// create the title text component
		Rectangle titleBounds = tSet.getBounds();
		if (titleBounds == null) {
			titleBounds = new Rectangle(margin, margin, w, tth);
		}
		TextComponent titleText = new TextComponent(Messages.getString("display.bible.title.name"), Messages.getString("display.bible.title.defaultText"), titleBounds.width, titleBounds.height, true);
		titleText.setX(titleBounds.x);
		titleText.setY(titleBounds.y);
		titleText.setTextColor(tSet.getTextColor());
		titleText.setTextAlignment(tSet.getTextAlignment());
		titleText.setTextFontScaleType(tSet.getTextFontScaleType());
		titleText.setTextWrapped(tSet.isTextWrapped());
		titleText.setPadding(tSet.getPadding());
		titleText.setTextFont(tSet.getTextFont());
		titleText.setVisible(tSet.isVisible());
		
		// create the text component
		Rectangle textBounds = bSet.getBounds();
		if (textBounds == null) {
			textBounds = new Rectangle(margin, tth + margin * 2, w, th);
		}
		TextComponent text = new TextComponent(Messages.getString("display.bible.body.name"), Messages.getString("display.bible.body.defaultText"), textBounds.width, textBounds.height, true);
		text.setX(textBounds.x);
		text.setY(textBounds.y);
		text.setTextColor(bSet.getTextColor());
		text.setTextAlignment(bSet.getTextAlignment());
		text.setTextFontScaleType(bSet.getTextFontScaleType());
		text.setTextWrapped(bSet.isTextWrapped());
		text.setPadding(bSet.getPadding());
		text.setTextFont(bSet.getTextFont());
		text.setVisible(bSet.isVisible());
		
		display.setColorBackgroundComponent(colorBackground);
		display.setImageBackgroundComponent(imageBackground);
		display.setScriptureTitleComponent(titleText);
		display.setScriptureTextComponent(text);
		
		return display;
	}
}

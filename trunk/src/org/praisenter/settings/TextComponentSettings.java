package org.praisenter.settings;

import java.awt.Color;
import java.awt.Font;

import org.praisenter.display.FontScaleType;
import org.praisenter.display.TextAlignment;
import org.praisenter.display.TextComponent;
import org.praisenter.utilities.FontManager;

/**
 * Represents the settings for a {@link TextComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TextComponentSettings extends GraphicsComponentSettings<TextComponent> {
	/** The default font applied to a {@link TextComponent} */
	public static final Font DEFAULT_FONT = FontManager.getDefaultFont().deriveFont(Font.PLAIN, 40.0f);

	/** The title text color key */
	private static final String KEY_TEXT_COLOR = "Text.Color";
	
	/** The title text font key */
	private static final String KEY_TEXT_FONT = "Text.Font";
	
	/** The title text font scale type key */
	private static final String KEY_TEXT_FONT_SCALE_TYPE = "Text.FontScaleType";
	
	/** The title text alignment key */
	private static final String KEY_TEXT_ALIGNMENT = "Text.Alignment";
	
	/** The title text wrapped key */
	private static final String KEY_TEXT_WRAPPED = "Text.Wrapped";
	
	/** The title padding key */
	private static final String KEY_PADDING = "Padding";
	
	/**
	 * Minimal constructor.
	 * @param root the settings this grouping belongs to
	 */
	public TextComponentSettings(RootSettings<?> root) {
		super(null, root);
	}
	
	/**
	 * Optional constructor.
	 * @param prefix the settings property prefix
	 * @param root the settings this grouping belongs to
	 */
	public TextComponentSettings(String prefix, RootSettings<?> root) {
		super(prefix, root);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.GraphicsComponentSettings#setSettings(org.praisenter.display.GraphicsComponent)
	 */
	@Override
	public void setSettings(TextComponent component) throws SettingsException {
		// set the super settings
		super.setSettings(component);
		
		// text settings
		this.setPadding(component.getPadding());
		this.setTextAlignment(component.getTextAlignment());
		this.setTextColor(component.getTextColor());
		this.setTextFont(component.getTextFont());
		this.setTextFontScaleType(component.getTextFontScaleType());
		this.setTextWrapped(component.isTextWrapped());
	}
	
	/**
	 * Returns the text color.
	 * @return Color
	 */
	public Color getTextColor() {
		return this.getColorSetting(this.prefix + TextComponentSettings.KEY_TEXT_COLOR);
	}
	
	/**
	 * Sets the text color
	 * @param color the color
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setTextColor(Color color) throws SettingsException {
		this.setSetting(this.prefix + TextComponentSettings.KEY_TEXT_COLOR, color);
	}
	
	/**
	 * Returns the text font.
	 * @return Font
	 */
	public Font getTextFont() {
		return this.getFontSetting(this.prefix + TextComponentSettings.KEY_TEXT_FONT);
	}
	
	/**
	 * Sets the text font.
	 * @param font the font
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setTextFont(Font font) throws SettingsException {
		this.setSetting(this.prefix + TextComponentSettings.KEY_TEXT_FONT, font);
	}
	
	/**
	 * Returns the text font scale type.
	 * @return {@link FontScaleType}
	 */
	public FontScaleType getTextFontScaleType() {
		return this.getFontScaleTypeSetting(this.prefix + TextComponentSettings.KEY_TEXT_FONT_SCALE_TYPE);
	}
	
	/**
	 * Sets the text font scale type.
	 * @param type the font scale type
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setTextFontScaleType(FontScaleType type) throws SettingsException {
		this.setSetting(this.prefix + TextComponentSettings.KEY_TEXT_FONT_SCALE_TYPE, type);
	}
	
	/**
	 * Returns the text alignment.
	 * @return {@link TextAlignment}
	 */
	public TextAlignment getTextAlignment() {
		return this.getTextAlignmentSetting(this.prefix + TextComponentSettings.KEY_TEXT_ALIGNMENT);
	}
	
	/**
	 * Sets the text alignment.
	 * @param alignment the text alignment
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setTextAlignment(TextAlignment alignment) throws SettingsException {
		this.setSetting(this.prefix + TextComponentSettings.KEY_TEXT_ALIGNMENT, alignment);
	}
	
	/**
	 * Returns true if the text is wrapped within the bounds.
	 * @return boolean
	 */
	public boolean isTextWrapped() {
		return this.getBooleanSetting(this.prefix + TextComponentSettings.KEY_TEXT_WRAPPED);
	}
	
	/**
	 * Enables or disables text wrapping.
	 * @param flag true if text wrapping should be enabled
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setTextWrapped(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + TextComponentSettings.KEY_TEXT_WRAPPED, flag);
	}
	
	/**
	 * Returns the text component's internal padding.
	 * @return int
	 */
	public int getPadding() {
		return this.getIntegerSetting(this.prefix + TextComponentSettings.KEY_PADDING);
	}
	
	/**
	 * Sets the text component's internal padding.
	 * @param padding the padding
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setPadding(int padding) throws SettingsException {
		this.setSetting(this.prefix + TextComponentSettings.KEY_PADDING, padding);
	}
}

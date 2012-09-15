package org.praisenter.settings;

import java.awt.Color;

import org.praisenter.display.ColorBackgroundComponent;

/**
 * Represents the settings for a {@link ColorBackgroundComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ColorBackgroundSettings extends ComponentSettings<ColorBackgroundComponent> {
	/** The background color key */
	private static final String KEY_COLOR = "Color";
	
	/** The background color visible key */
	private static final String KEY_VISIBLE = "Visible";
	
	/**
	 * Minimal constructor.
	 * @param prefix the settings property prefix
	 * @param root the settings this grouping belongs to
	 */
	public ColorBackgroundSettings(String prefix, RootSettings<?> root) {
		super(prefix, root);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ComponentSettings#setSettings(org.praisenter.display.DisplayComponent)
	 */
	@Override
	public void setSettings(ColorBackgroundComponent component) throws SettingsException {
		this.setColor(component.getColor());
		this.setVisible(component.isVisible());
	}
	
	/**
	 * Returns the color.
	 * @return Color
	 */
	public Color getColor() {
		return this.getColorSetting(this.prefix + ColorBackgroundSettings.KEY_COLOR);
	}
	
	/**
	 * Sets the color.
	 * @param color the color
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setColor(Color color) throws SettingsException {
		this.setSetting(this.prefix + ColorBackgroundSettings.KEY_COLOR, color);
	}
	
	/**
	 * Returns true if the component is visible.
	 * @return boolean
	 */
	public boolean isVisible() {
		return this.getBooleanSetting(this.prefix + ColorBackgroundSettings.KEY_VISIBLE);
	}
	
	/**
	 * Sets whether the component is visible or not.
	 * @param flag true if the component should be visible
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setVisible(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + ColorBackgroundSettings.KEY_VISIBLE, flag);
	}
}

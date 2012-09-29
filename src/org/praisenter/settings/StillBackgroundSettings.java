package org.praisenter.settings;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.praisenter.display.CompositeType;
import org.praisenter.display.StillBackgroundComponent;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;

/**
 * Represents the settings of a {@link StillBackgroundComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class StillBackgroundSettings extends ComponentSettings<StillBackgroundComponent> {
	/** The background color key */
	private static final String KEY_COLOR = "Color";
	
	/** The background color visible key */
	private static final String KEY_COLOR_VISIBLE = "Color.Visible";
	
	/** The background color visible key */
	private static final String KEY_COLOR_COMPOSITE_TYPE = "Color.CompositeType";
	
	/** The background image key */
	private static final String KEY_IMAGE = "Image";

	/** The background image visible key */
	private static final String KEY_IMAGE_VISIBLE = "Image.Visible";
	
	/** The background image scale quality key */
	private static final String KEY_IMAGE_SCALE_QUALITY = "Image.ScaleQuality";
	
	/** The background image scale type key */
	private static final String KEY_IMAGE_SCALE_TYPE = "Image.ScaleType";

	/** The background image visible key */
	private static final String KEY_VISIBLE = "Visible";
	
	/**
	 * Minimal constructor.
	 * @param prefix the settings property prefix
	 * @param root the settings this grouping belongs to
	 */
	public StillBackgroundSettings(String prefix, RootSettings<?> root) {
		super(prefix, root);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ComponentSettings#setSettings(org.praisenter.display.DisplayComponent)
	 */
	@Override
	public void setSettings(StillBackgroundComponent component) throws SettingsException {
		this.setColor(component.getColor());
		this.setColorCompositeType(component.getColorCompositeType());
		this.setColorVisible(component.isColorVisible());
		
		this.setImage(component.getImage());
		this.setImageScaleQuality(component.getImageScaleQuality());
		this.setImageScaleType(component.getImageScaleType());
		this.setImageVisible(component.isImageVisible());
		
		this.setVisible(component.isVisible());
	}

	/**
	 * Returns the color.
	 * @return Color
	 */
	public Color getColor() {
		return this.getColorSetting(this.prefix + StillBackgroundSettings.KEY_COLOR);
	}
	
	/**
	 * Sets the color.
	 * @param color the color
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setColor(Color color) throws SettingsException {
		this.setSetting(this.prefix + StillBackgroundSettings.KEY_COLOR, color);
	}

	/**
	 * Returns the color composite type.
	 * @return {@link CompositeType}
	 */
	public CompositeType getColorCompositeType() {
		return this.getCompositeTypeSetting(this.prefix + StillBackgroundSettings.KEY_COLOR_COMPOSITE_TYPE);
	}
	
	/**
	 * Sets the color composite type.
	 * @param type the color composite type
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setColorCompositeType(CompositeType type) throws SettingsException {
		this.setSetting(this.prefix + StillBackgroundSettings.KEY_COLOR_COMPOSITE_TYPE, type);
	}
	
	/**
	 * Returns true if the color is visible.
	 * @return boolean
	 */
	public boolean isColorVisible() {
		return this.getBooleanSetting(this.prefix + StillBackgroundSettings.KEY_COLOR_VISIBLE);
	}
	
	/**
	 * Sets the color to be visible or not.
	 * @param flag true if the color should be visible
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setColorVisible(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + StillBackgroundSettings.KEY_COLOR_VISIBLE, flag);
	}
	
	/**
	 * Returns the image.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.getImageSetting(this.prefix + StillBackgroundSettings.KEY_IMAGE);
	}
	
	/**
	 * Sets the image.
	 * @param image the image
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setImage(BufferedImage image) throws SettingsException {
		this.setSetting(this.prefix + StillBackgroundSettings.KEY_IMAGE, image);
	}
	
	/**
	 * Returns true if the image is visible.
	 * @return boolean
	 */
	public boolean isImageVisible() {
		return this.getBooleanSetting(this.prefix + StillBackgroundSettings.KEY_IMAGE_VISIBLE);
	}
	
	/**
	 * Sets the image to be visible or not.
	 * @param flag true if the image should be visible
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setImageVisible(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + StillBackgroundSettings.KEY_IMAGE_VISIBLE, flag);
	}
	
	/**
	 * Returns the image scale quality.
	 * @return {@link ScaleQuality}
	 */
	public ScaleQuality getImageScaleQuality() {
		return this.getScaleQualitySetting(this.prefix + StillBackgroundSettings.KEY_IMAGE_SCALE_QUALITY);
	}
	
	/**
	 * Sets the image scale quality.
	 * @param scaleQuality the image scale quality
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setImageScaleQuality(ScaleQuality scaleQuality) throws SettingsException {
		this.setSetting(this.prefix + StillBackgroundSettings.KEY_IMAGE_SCALE_QUALITY, scaleQuality);
	}
	
	/**
	 * Returns the image scale type.
	 * @return {@link ScaleType}
	 */
	public ScaleType getImageScaleType() {
		return this.getScaleTypeSetting(this.prefix + StillBackgroundSettings.KEY_IMAGE_SCALE_TYPE);
	}
	
	/**
	 * Sets the image scale type.
	 * @param scaleType the image scale type
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setImageScaleType(ScaleType scaleType) throws SettingsException {
		this.setSetting(this.prefix + StillBackgroundSettings.KEY_IMAGE_SCALE_TYPE, scaleType);
	}

	/**
	 * Returns true if the component is visible.
	 * @return boolean
	 */
	public boolean isVisible() {
		return this.getBooleanSetting(this.prefix + StillBackgroundSettings.KEY_VISIBLE);
	}
	
	/**
	 * Sets the component to be visible or not.
	 * @param flag true if the component should be visible
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setVisible(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + StillBackgroundSettings.KEY_VISIBLE, flag);
	}
}

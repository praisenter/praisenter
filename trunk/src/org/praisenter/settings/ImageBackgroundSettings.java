package org.praisenter.settings;

import java.awt.image.BufferedImage;

import org.praisenter.display.ImageBackgroundComponent;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;

/**
 * Represents the settings of a {@link ImageBackgroundComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageBackgroundSettings extends ComponentSettings<ImageBackgroundComponent> {
	/** The background image key */
	private static final String KEY_IMAGE = "Image";
	
	/** The background image visible key */
	private static final String KEY_VISIBLE = "Visible";
	
	/** The background image scale quality key */
	private static final String KEY_SCALE_QUALITY = "ScaleQuality";
	
	/** The background image scale type key */
	private static final String KEY_SCALE_TYPE = "ScaleType";
	
	/**
	 * Minimal constructor.
	 * @param prefix the settings property prefix
	 * @param root the settings this grouping belongs to
	 */
	public ImageBackgroundSettings(String prefix, RootSettings<?> root) {
		super(prefix, root);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ComponentSettings#setSettings(org.praisenter.display.DisplayComponent)
	 */
	@Override
	public void setSettings(ImageBackgroundComponent component) throws SettingsException {
		this.setImage(component.getImage());
		this.setScaleQuality(component.getScaleQuality());
		this.setScaleType(component.getScaleType());
		this.setVisible(component.isVisible());
	}
	
	/**
	 * Returns the image.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.getImageSetting(this.prefix + ImageBackgroundSettings.KEY_IMAGE);
	}
	
	/**
	 * Sets the image.
	 * @param image the image
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setImage(BufferedImage image) throws SettingsException {
		this.setSetting(this.prefix + ImageBackgroundSettings.KEY_IMAGE, image);
	}
	
	/**
	 * Returns true if the component is visible.
	 * @return boolean
	 */
	public boolean isVisible() {
		return this.getBooleanSetting(this.prefix + ImageBackgroundSettings.KEY_VISIBLE);
	}
	
	/**
	 * Sets the component to be visible or not.
	 * @param flag true if the component should be visible
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setVisible(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + ImageBackgroundSettings.KEY_VISIBLE, flag);
	}
	
	/**
	 * Returns the image scale quality.
	 * @return {@link ScaleQuality}
	 */
	public ScaleQuality getScaleQuality() {
		return this.getScaleQualitySetting(this.prefix + ImageBackgroundSettings.KEY_SCALE_QUALITY);
	}
	
	/**
	 * Sets the image scale quality.
	 * @param scaleQuality the image scale quality
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setScaleQuality(ScaleQuality scaleQuality) throws SettingsException {
		this.setSetting(this.prefix + ImageBackgroundSettings.KEY_SCALE_QUALITY, scaleQuality);
	}
	
	/**
	 * Returns the image scale type.
	 * @return {@link ScaleType}
	 */
	public ScaleType getScaleType() {
		return this.getScaleTypeSetting(this.prefix + ImageBackgroundSettings.KEY_SCALE_TYPE);
	}
	
	/**
	 * Sets the image scale type.
	 * @param scaleType the image scale type
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setScaleType(ScaleType scaleType) throws SettingsException {
		this.setSetting(this.prefix + ImageBackgroundSettings.KEY_SCALE_TYPE, scaleType);
	}
}

package org.praisenter.settings;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.praisenter.display.CompositeType;
import org.praisenter.display.GraphicsComponent;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;

/**
 * Represents the settings of a {@link GraphicsComponent}.
 * @param <E> the {@link GraphicsComponent} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class GraphicsComponentSettings<E extends GraphicsComponent> extends ComponentSettings<E> {
	// general
	
	/** The title bounds key */
	private static final String KEY_BOUNDS = "Bounds";

	/** The background image visible key */
	private static final String KEY_VISIBLE = "Visible";
	
	// color
	
	/** The background color key */
	private static final String KEY_COLOR = "Background.Color";
	
	/** The background color visible key */
	private static final String KEY_COLOR_VISIBLE = "Background.Color.Visible";
	
	/** The background color visible key */
	private static final String KEY_COLOR_COMPOSITE_TYPE = "Background.Color.CompositeType";
	
	// image
	
	/** The background image key */
	private static final String KEY_IMAGE = "Background.Image";

	/** The background image visible key */
	private static final String KEY_IMAGE_VISIBLE = "Background.Image.Visible";
	
	/** The background image scale quality key */
	private static final String KEY_IMAGE_SCALE_QUALITY = "Background.Image.ScaleQuality";
	
	/** The background image scale type key */
	private static final String KEY_IMAGE_SCALE_TYPE = "Background.Image.ScaleType";

	/**
	 * Minimal constructor.
	 * @param root the settings this grouping belongs to
	 */
	public GraphicsComponentSettings(RootSettings<?> root) {
		super(null, root);
	}
	
	/**
	 * Optional constructor.
	 * @param prefix the settings property prefix
	 * @param root the settings this grouping belongs to
	 */
	public GraphicsComponentSettings(String prefix, RootSettings<?> root) {
		super(prefix, root);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.ComponentSettings#setSettings(org.praisenter.display.DisplayComponent)
	 */
	@Override
	public void setSettings(E component) throws SettingsException {
		// general
		this.setBounds(component.getBounds());
		this.setVisible(component.isVisible());
		
		// color
		this.setBackgroundColor(component.getBackgroundColor());
		this.setBackgroundColorCompositeType(component.getBackgroundColorCompositeType());
		this.setBackgroundColorVisible(component.isBackgroundColorVisible());
		
		// image
		this.setBackgroundImage(component.getBackgroundImage());
		this.setBackgroundImageScaleQuality(component.getBackgroundImageScaleQuality());
		this.setBackgroundImageScaleType(component.getBackgroundImageScaleType());
		this.setBackgroundImageVisible(component.isBackgroundImageVisible());
	}

	// general
	
	/**
	 * Returns the bounds of the text component.
	 * @return Rectangle
	 */
	public Rectangle getBounds() {
		return this.getRectangleSetting(this.prefix + GraphicsComponentSettings.KEY_BOUNDS);
	}
	
	/**
	 * Sets the bounds of the text component.
	 * @param bounds the bounds
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setBounds(Rectangle bounds) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_BOUNDS, bounds);
	}

	/**
	 * Returns true if the component is visible.
	 * @return boolean
	 */
	public boolean isVisible() {
		return this.getBooleanSetting(this.prefix + GraphicsComponentSettings.KEY_VISIBLE);
	}
	
	/**
	 * Sets the component to be visible or not.
	 * @param flag true if the component should be visible
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setVisible(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_VISIBLE, flag);
	}
	
	// color
	
	/**
	 * Returns the color.
	 * @return Color
	 */
	public Color getBackgroundColor() {
		return this.getColorSetting(this.prefix + GraphicsComponentSettings.KEY_COLOR);
	}
	
	/**
	 * Sets the color.
	 * @param color the color
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setBackgroundColor(Color color) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_COLOR, color);
	}

	/**
	 * Returns the color composite type.
	 * @return {@link CompositeType}
	 */
	public CompositeType getBackgroundColorCompositeType() {
		return this.getCompositeTypeSetting(this.prefix + GraphicsComponentSettings.KEY_COLOR_COMPOSITE_TYPE);
	}
	
	/**
	 * Sets the color composite type.
	 * @param type the color composite type
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setBackgroundColorCompositeType(CompositeType type) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_COLOR_COMPOSITE_TYPE, type);
	}
	
	/**
	 * Returns true if the color is visible.
	 * @return boolean
	 */
	public boolean isBackgroundColorVisible() {
		return this.getBooleanSetting(this.prefix + GraphicsComponentSettings.KEY_COLOR_VISIBLE);
	}
	
	/**
	 * Sets the color to be visible or not.
	 * @param flag true if the color should be visible
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setBackgroundColorVisible(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_COLOR_VISIBLE, flag);
	}
	
	// image
	
	/**
	 * Returns the image.
	 * @return BufferedImage
	 */
	public BufferedImage getBackgroundImage() {
		return this.getImageSetting(this.prefix + GraphicsComponentSettings.KEY_IMAGE);
	}
	
	/**
	 * Sets the image.
	 * @param image the image
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setBackgroundImage(BufferedImage image) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_IMAGE, image);
	}
	
	/**
	 * Returns true if the image is visible.
	 * @return boolean
	 */
	public boolean isBackgroundImageVisible() {
		return this.getBooleanSetting(this.prefix + GraphicsComponentSettings.KEY_IMAGE_VISIBLE);
	}
	
	/**
	 * Sets the image to be visible or not.
	 * @param flag true if the image should be visible
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setBackgroundImageVisible(boolean flag) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_IMAGE_VISIBLE, flag);
	}
	
	/**
	 * Returns the image scale quality.
	 * @return {@link ScaleQuality}
	 */
	public ScaleQuality getBackgroundImageScaleQuality() {
		return this.getScaleQualitySetting(this.prefix + GraphicsComponentSettings.KEY_IMAGE_SCALE_QUALITY);
	}
	
	/**
	 * Sets the image scale quality.
	 * @param scaleQuality the image scale quality
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setBackgroundImageScaleQuality(ScaleQuality scaleQuality) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_IMAGE_SCALE_QUALITY, scaleQuality);
	}
	
	/**
	 * Returns the image scale type.
	 * @return {@link ScaleType}
	 */
	public ScaleType getBackgroundImageScaleType() {
		return this.getScaleTypeSetting(this.prefix + GraphicsComponentSettings.KEY_IMAGE_SCALE_TYPE);
	}
	
	/**
	 * Sets the image scale type.
	 * @param scaleType the image scale type
	 * @throws SettingsException if the setting failed to be assigned
	 */
	public void setBackgroundImageScaleType(ScaleType scaleType) throws SettingsException {
		this.setSetting(this.prefix + GraphicsComponentSettings.KEY_IMAGE_SCALE_TYPE, scaleType);
	}
}

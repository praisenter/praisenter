package org.praisenter.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Properties;

import org.praisenter.display.Display;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;

/**
 * Settings for a display with a color and image background.
 * @author William Bittle
 * @param <E> the {@link DisplaySettings} type
 * @param <T> the {@link Display} type
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class DisplaySettings<E extends DisplaySettings<E, T>, T> extends RootSettings<E> {
	/** The {@link PartialSettings} for the color background */
	protected ColorBackgroundSettings colorBackgroundSettings;
	
	/** The {@link PartialSettings} for the image background */
	protected ImageBackgroundSettings imageBackgroundSettings;
	
	/**
	 * Default constructor.
	 */
	protected DisplaySettings() {
		this(new Properties());
	}
	
	/**
	 * Full constructor.
	 * @param properties the initial properties
	 */
	protected DisplaySettings(Properties properties) {
		super(properties);
		this.colorBackgroundSettings = new ColorBackgroundSettings("ColorBackground", this);
		this.imageBackgroundSettings = new ImageBackgroundSettings("ImageBackground", this);
	}
	
	/**
	 * Returns a display for this display settings object.
	 * @param displaySize the target display size
	 * @return Display
	 */
	public abstract T getDisplay(Dimension displaySize);
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.Settings#setDefaultSettings()
	 */
	@Override
	public void setDefaultSettings() throws SettingsException {
		this.colorBackgroundSettings.setColor(Color.BLUE);
		this.colorBackgroundSettings.setVisible(true);
		
		this.imageBackgroundSettings.setImage(null);
		this.imageBackgroundSettings.setScaleQuality(ScaleQuality.BILINEAR);
		this.imageBackgroundSettings.setScaleType(ScaleType.NONUNIFORM);
		this.imageBackgroundSettings.setVisible(true);
	}
	
	/**
	 * Returns the color background {@link PartialSettings}.
	 * @return {@link ColorBackgroundSettings}
	 */
	public ColorBackgroundSettings getColorBackgroundSettings() {
		return colorBackgroundSettings;
	}
	
	/**
	 * Returns the image background {@link PartialSettings}.
	 * @return {@link ImageBackgroundSettings}
	 */
	public ImageBackgroundSettings getImageBackgroundSettings() {
		return imageBackgroundSettings;
	}
}

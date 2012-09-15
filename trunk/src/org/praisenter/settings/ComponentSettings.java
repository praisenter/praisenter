package org.praisenter.settings;

import org.praisenter.display.DisplayComponent;

/**
 * Represents settings for a {@link DisplayComponent}.
 * @author William Bittle
 * @param <E> the {@link DisplayComponent} type
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ComponentSettings<E extends DisplayComponent> extends PartialSettings {
	/**
	 * Minimal constructor.
	 * @param prefix the settings property prefix
	 * @param root the settings this grouping belongs to
	 */
	protected ComponentSettings(String prefix, RootSettings<?> root) {
		super(prefix, root);
	}
	
	/**
	 * Sets the settings in this settings object to the current state of the given component.
	 * @param component the component
	 * @throws SettingsException if an exception occurs while assigning a setting
	 */
	public abstract void setSettings(E component) throws SettingsException;
}

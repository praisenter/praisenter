package org.praisenter.panel.setup;

import java.beans.PropertyChangeListener;

import org.praisenter.settings.SettingsException;

/**
 * Represents a panel that allows editing of settings.
 * <p>
 * The panels can also listen for other settings changes via
 * the property listener interface.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SetupPanel extends PropertyChangeListener {
	/**
	 * Saves the settings configured by this panel.
	 * @throws SettingsException if an exception occurs while assigning a setting
	 */
	public void saveSettings() throws SettingsException;
}

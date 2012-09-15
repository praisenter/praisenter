package org.praisenter.settings;

import java.util.EventListener;

/**
 * Interface for listening to settings events.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SettingsListener extends EventListener {
	/** 
	 * Called when the settings have changed and been saved.
	 */
	public abstract void settingsSaved();
}

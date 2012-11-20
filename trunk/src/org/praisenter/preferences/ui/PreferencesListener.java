package org.praisenter.preferences.ui;

/**
 * Listener interface to listen for preference changed events.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PreferencesListener {
	/**
	 * Called when the preferences have been changed (and saved).
	 */
	public abstract void preferencesChanged();
}

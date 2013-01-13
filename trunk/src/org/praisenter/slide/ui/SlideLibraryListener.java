package org.praisenter.slide.ui;

/**
 * Represents a class that needs to updated when the slide library changes.
 * @author William Bittle
 * @version 2.0.0
 */
public interface SlideLibraryListener {
	/**
	 * Called when the slide library changes.
	 */
	public abstract void slideLibraryChanged();
}

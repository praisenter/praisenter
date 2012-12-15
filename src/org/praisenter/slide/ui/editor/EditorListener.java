package org.praisenter.slide.ui.editor;

import java.util.EventListener;

/**
 * Simple interface for listening to edit events.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public interface EditorListener extends EventListener {
	/**
	 * Called when an edit has been made in an editor.
	 * @param event the edit event details
	 */
	public void editPerformed(EditEvent event);
}

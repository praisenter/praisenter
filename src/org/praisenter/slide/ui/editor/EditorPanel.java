package org.praisenter.slide.ui.editor;

import javax.swing.JPanel;

/**
 * Represents an abstract panel in which editing is performed.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class EditorPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = -7037896714849865050L;
	
	/**
	 * Adds the given {@link EditorListener} to this panel.
	 * @param listener the listener to add
	 */
	public void addEditorListener(EditorListener listener) {
		this.listenerList.add(EditorListener.class, listener);
	}
	
	/**
	 * Removes the given {@link EditorListener} from this panel.
	 * @param listener the listener to remove
	 */
	public void removeEditorListener(EditorListener listener) {
		this.listenerList.remove(EditorListener.class, listener);
	}
	
	/**
	 * Notifies all {@link EditorListener}s of the an edit event.
	 */
	protected void notifyEditorListeners() {
		EditorListener[] listeners = this.listenerList.getListeners(EditorListener.class);
		EditEvent event = new EditEvent(this);
		for (EditorListener listener : listeners) {
			listener.editPerformed(event);
		}
	}
}

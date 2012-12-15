package org.praisenter.slide.ui.editor;

/**
 * Simple event object for storing the source of the event along
 * with any other event specific data.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class EditEvent {
	/** The source object of this event */
	protected Object source;
	
	/**
	 * Minimal constructor.
	 * @param source the source of this event
	 */
	public EditEvent(Object source) {
		this.source = source;
	}
	
	/**
	 * Returns the source of this event.
	 * @return Object
	 */
	public Object getSource() {
		return this.source;
	}
}

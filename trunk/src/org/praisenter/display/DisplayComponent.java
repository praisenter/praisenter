package org.praisenter.display;

import java.awt.Graphics2D;
import java.util.UUID;

/**
 * An abstract component used on a {@link Display}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class DisplayComponent {
	/** True if this component has been changed and needs to be updated */
	private boolean dirty = true;

	/** The unique component id */
	private final String id = UUID.randomUUID().toString();
	
	/** The component name */
	private String name;

	/** True if this component should be visible */
	protected boolean visible;
	
	/**
	 * Minimal constructor.
	 * @param name the name of this component
	 */
	public DisplayComponent(String name) {
		this.name = name;
		this.visible = true;
	}
	
	/**
	 * Renders the component to the given graphics object.
	 * @param graphics the graphics object to render to
	 */
	public abstract void render(Graphics2D graphics);
	
	/**
	 * Returns true if this component has been changed 
	 * and needs to be updated.
	 * @return boolean
	 */
	protected boolean isDirty() {
		return this.dirty;
	}
	
	/**
	 * Sets this component to be marked as dirty.  This tells the
	 * component that any cache resources must be updated.
	 * @param flag true if this component should be marked as dirty
	 */
	protected void setDirty(boolean flag) {
		this.dirty = flag;
	}
	
	/**
	 * Returns the unique id for this component.
	 * @return String
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Returns the user friendly name for this component.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns true if this component is visible.
	 * @return boolean
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Sets this component to visible or invisible.
	 * @param visible visible or not visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}

package org.praisenter.settings.ui;

import java.awt.Point;

import org.praisenter.display.FloatingDisplayComponent;

/**
 * Represents a {@link Command} that modifies the position and size of a {@link FloatingDisplayComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class BoundsCommand extends Command {
	/** The start location */
	protected Point start;
	
	/** The component */
	protected FloatingDisplayComponent component;
	
	/**
	 * Called when the action is begun.
	 * @param start the starting location
	 * @param component the component being modified
	 */
	public synchronized void begin(Point start, FloatingDisplayComponent component) {
		this.active = true;
		this.start = start;
		this.component = component;
	}
	
	/**
	 * Called when the action should be updated.
	 * @param end the end point
	 */
	public abstract void update(Point end);
	
	/**
	 * Called when the command should be ended
	 */
	public synchronized void end() {
		this.active = false;
		this.start = null;
		this.component = null;
	}
	
	/**
	 * Returns the current start location.
	 * <p>
	 * This may be updated by the {@link #update(Point)} method.
	 * @return Point
	 */
	public Point getStart() {
		return this.start;
	}
	
	/**
	 * Returns the component being modified.
	 * @return {@link FloatingDisplayComponent}
	 */
	public FloatingDisplayComponent getComponent() {
		return this.component;
	}
}

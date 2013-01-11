package org.praisenter.slide.ui.editor.command;

import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Represents a {@link Command} that modifies the position or size of an object.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 * @param <E> the data type
 */
public abstract class BoundsCommand<E extends BoundsCommandBeginArguments> extends Command<E, Point, Object> {
	/** The current point */
	protected Point current;
	
	/* (non-Javadoc)
	 * @see org.praisenter.command.Command#begin(java.lang.Object)
	 */
	@Override
	public void begin(E arguments) {
		super.begin(arguments);
		// set the current point
		this.current = arguments.getStart();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.command.Command#update(java.lang.Object)
	 */
	@Override
	public void update(Point arguments) {
		super.update(arguments);
		// update the current location
		this.current = arguments;
	}
	
	/**
	 * Returns the current location.
	 * @return Point
	 */
	public Point getCurrent() {
		return new Point(this.current);
	}
}

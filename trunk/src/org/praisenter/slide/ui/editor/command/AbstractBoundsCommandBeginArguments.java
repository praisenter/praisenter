package org.praisenter.slide.ui.editor.command;

import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Class used as input for the {@link Command#begin(Object)} method for {@link BoundsCommand}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class AbstractBoundsCommandBeginArguments implements BoundsCommandBeginArguments {
	/** The start position in slide space */
	protected Point start;

	/**
	 * Full constructor.
	 * @param start the start position in slide space
	 */
	public AbstractBoundsCommandBeginArguments(Point start) {
		this.start = start;
	}

	/**
	 * Returns the start position in slide space.
	 * @return Point
	 */
	public Point getStart() {
		return new Point(this.start);
	}
}

package org.praisenter.slide.ui.editor.command;

import java.awt.Dimension;
import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Class used as input for the {@link Command#begin(Object)} method for {@link BoundsCommand}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public interface BoundsCommandBeginArguments {
	/**
	 * Translates the object contained in the arguments.
	 * @param dx the change in x in pixels
	 * @param dy the change in y in pixels
	 */
	public abstract void translate(int dx, int dy);
	
	/**
	 * Resizes the object contained in the arguments.
	 * <p>
	 * Returns the amount the object was resized. This will always be
	 * dw, dh unless the object has a minimum size.
	 * @param dw the change in width in pixels
	 * @param dh the change in height in pixels
	 * @return Dimension
	 */
	public abstract Dimension resize(int dw, int dh);
	
	/**
	 * Returns the start position in slide space.
	 * @return Point
	 */
	public abstract Point getStart();
}

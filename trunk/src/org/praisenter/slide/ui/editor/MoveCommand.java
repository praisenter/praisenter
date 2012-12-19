package org.praisenter.slide.ui.editor;

import java.awt.Point;

import org.praisenter.slide.PositionedComponent;

/**
 * Represents a move {@link Command} on a {@link PositionedComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MoveCommand extends BoundsCommand {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		int dx = end.x - this.start.x;
		int dy = end.y - this.start.y;

		this.component.translate(dx, dy);
		this.start = end;
	}
}

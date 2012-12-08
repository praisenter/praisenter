package org.praisenter.slide.ui.editor;

import java.awt.Point;

import org.praisenter.slide.PositionedSlideComponent;

/**
 * Represents a resize width {@link Command} on a {@link PositionedSlideComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResizeWidthCommand extends BoundsCommand {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		this.component.resize(end.x - this.start.x, 0);
		this.start = end;
	}
}

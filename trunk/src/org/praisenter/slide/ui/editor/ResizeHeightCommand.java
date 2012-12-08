package org.praisenter.slide.ui.editor;

import java.awt.Point;

import org.praisenter.slide.PositionedSlideComponent;

/**
 * Represents a resize height {@link Command} on a {@link PositionedSlideComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResizeHeightCommand extends BoundsCommand {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		this.component.resize(0, end.y - this.start.y);
		this.start = end;
	}
}

package org.praisenter.panel.setup;

import java.awt.Point;

import org.praisenter.display.FloatingDisplayComponent;

/**
 * Represents a resize width {@link Command} on a {@link FloatingDisplayComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResizeWidthCommand extends BoundsCommand {
	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		this.component.resizeWidth(end.x - this.start.x);
		this.start = end;
	}
}

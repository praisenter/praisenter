package org.praisenter.settings.ui;

import java.awt.Point;

import org.praisenter.display.FloatingDisplayComponent;

/**
 * Represents a resize {@link Command} on a {@link FloatingDisplayComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResizeCommand extends BoundsCommand {
	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		this.component.resize(end.x - this.start.x, end.y - this.start.y);
		this.start = end;
	}
}

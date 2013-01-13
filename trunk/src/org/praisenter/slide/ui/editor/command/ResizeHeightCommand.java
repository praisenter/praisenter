package org.praisenter.slide.ui.editor.command;

import java.awt.Dimension;
import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Represents a resize height {@link Command}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ResizeHeightCommand extends ResizeCommand {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		int diff = end.y - this.current.y;
		
		if (this.prongLocation == ResizeProngLocation.BOTTOM) {
			this.beginArguments.resize(0, diff);
		} else if (this.prongLocation == ResizeProngLocation.TOP) {
			// to resize from the top, we need to increase the size
			// and reposition the component
			Dimension ds = this.beginArguments.resize(0, -diff);
			// only translate by the actual amount resized
			this.beginArguments.translate(0, -ds.height);
		}
		
		super.update(end);
	}
}

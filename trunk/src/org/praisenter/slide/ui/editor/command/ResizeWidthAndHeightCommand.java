package org.praisenter.slide.ui.editor.command;

import java.awt.Dimension;
import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Represents a resize {@link Command}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ResizeWidthAndHeightCommand extends ResizeCommand {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		int xdiff = end.x - this.current.x;
		int ydiff = end.y - this.current.y;
		
		if (this.prongLocation == ResizeProngLocation.BOTTOM_RIGHT) {
			this.beginArguments.resize(xdiff, ydiff);
		} else if (this.prongLocation == ResizeProngLocation.BOTTOM_LEFT) {
			// reposition the x coorindate and modify the size
			Dimension ds = this.beginArguments.resize(-xdiff, ydiff);
			// only translate by the actual amount resized
			this.beginArguments.translate(-ds.width, 0);
		} else if (this.prongLocation == ResizeProngLocation.TOP_LEFT) {
			// reposition both coorindates and modify the size
			Dimension ds = this.beginArguments.resize(-xdiff, -ydiff);
			// only translate by the actual amount resized
			this.beginArguments.translate(-ds.width, -ds.height);
		} else if (this.prongLocation == ResizeProngLocation.TOP_RIGHT) {
			// reposition the y coorindate and modify the size
			Dimension ds = this.beginArguments.resize(xdiff, -ydiff);
			// only translate by the actual amount resized
			this.beginArguments.translate(0, -ds.height);
		}
		
		super.update(end);
	}
}

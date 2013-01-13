package org.praisenter.slide.ui.editor.command;

import java.awt.Dimension;
import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Represents a resize width {@link Command}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ResizeWidthCommand extends ResizeCommand {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		int diff = end.x - this.current.x;
		
		if (this.prongLocation == ResizeProngLocation.LEFT) {
			// resize from the left should change the x position of the component
			// and resize the width as well so that the right side of the component
			// stays stationary
			Dimension ds = this.beginArguments.resize(-diff, 0);
			// only translate by the actual amount resized
			this.beginArguments.translate(-ds.width, 0);
		} else if (this.prongLocation == ResizeProngLocation.RIGHT) {
			this.beginArguments.resize(diff, 0);
		}
		
		super.update(end);
	}
}

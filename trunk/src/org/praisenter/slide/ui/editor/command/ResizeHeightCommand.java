package org.praisenter.slide.ui.editor.command;

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
			this.beginArguments.translate(0, diff);
			this.beginArguments.resize(0, -diff);
			// FIXME we only want to translate if the resize is not bounded by the minimum size (same with other resize commands)
		}
		
		super.update(end);
	}
}

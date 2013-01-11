package org.praisenter.slide.ui.editor.command;

import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Represents a move {@link Command}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class MoveCommand extends BoundsCommand<BoundsCommandBeginArguments> {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommand#update(java.awt.Point)
	 */
	public synchronized void update(Point end) {
		int dx = end.x - this.current.x;
		int dy = end.y - this.current.y;
		
		this.beginArguments.translate(dx, dy);
		
		super.update(end);
	}
}

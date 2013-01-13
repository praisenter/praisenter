package org.praisenter.slide.ui.editor.command;

import java.awt.Dimension;
import java.awt.Point;

import org.praisenter.command.Command;

/**
 * Decorator class used as input for the {@link Command#begin(Object)} method for {@link ResizeCommand}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ResizeCommandBeginArguments implements BoundsCommandBeginArguments {
	/** The resize prong location */
	protected ResizeProngLocation prongLocation;

	/** The decorated being arguments */
	protected BoundsCommandBeginArguments arguments;
	
	/**
	 * Full constructor.
	 * @param arguments the bounds arguments
	 * @param prongLocation the prong location
	 */
	public ResizeCommandBeginArguments(BoundsCommandBeginArguments arguments, ResizeProngLocation prongLocation) {
		this.arguments = arguments;
		this.prongLocation = prongLocation;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#translate(int, int)
	 */
	@Override
	public void translate(int dx, int dy) {
		this.arguments.translate(dx, dy);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#resize(int, int)
	 */
	@Override
	public Dimension resize(int dw, int dh) {
		return this.arguments.resize(dw, dh);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#getStart()
	 */
	@Override
	public Point getStart() {
		return this.arguments.getStart();
	}
	
	/**
	 * Returns the prong location.
	 * @return {@link ResizeProngLocation}
	 */
	public ResizeProngLocation getProngLocation() {
		return this.prongLocation;
	}
}

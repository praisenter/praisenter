package org.praisenter.slide.ui.editor.command;

/**
 * Abstract implementation of a resize command.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class ResizeCommand extends BoundsCommand<ResizeCommandBeginArguments> {
	/** The resize prong location */
	protected ResizeProngLocation prongLocation;
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommand#begin(org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments)
	 */
	@Override
	public synchronized void begin(ResizeCommandBeginArguments arguments) {
		super.begin(arguments);
		this.prongLocation = arguments.prongLocation;
	}
}

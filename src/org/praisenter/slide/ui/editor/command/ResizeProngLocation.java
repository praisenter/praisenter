package org.praisenter.slide.ui.editor.command;

/**
 * Enumeration of the resizing prong locations.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public enum ResizeProngLocation {
	/** The top resize prong */
	TOP(ResizeOperation.HEIGHT),
	
	/** The top-right resize prong */
	TOP_RIGHT(ResizeOperation.BOTH),
	
	/** The right resize prong */
	RIGHT(ResizeOperation.WIDTH),
	
	/** The bottom-right resize prong */
	BOTTOM_RIGHT(ResizeOperation.BOTH),
	
	/** The bottom resize prong */
	BOTTOM(ResizeOperation.HEIGHT),
	
	/** The bottom-left resize prong */
	BOTTOM_LEFT(ResizeOperation.BOTH),
	
	/** The left resize prong */
	LEFT(ResizeOperation.WIDTH),
	
	/** The top-left resize prong */
	TOP_LEFT(ResizeOperation.BOTH);
	
	/** The resize operation the prong performs */
	private ResizeOperation resizeOperation;
	
	/**
	 * Minimal constructor.
	 * @param resizeOperation the resize operation the prong should perform
	 */
	private ResizeProngLocation(ResizeOperation resizeOperation) {
		this.resizeOperation = resizeOperation;
	}
	
	/**
	 * Returns the resize operation the prong should perform when used.
	 * @return {@link ResizeOperation}
	 */
	public ResizeOperation getResizeOperation() {
		return this.resizeOperation;
	}
}

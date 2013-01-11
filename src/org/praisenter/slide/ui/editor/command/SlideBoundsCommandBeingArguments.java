package org.praisenter.slide.ui.editor.command;

import java.awt.Point;

import org.praisenter.command.Command;
import org.praisenter.slide.AbstractPositionedSlide;

/**
 * Class used as input for the {@link Command#begin(Object)} method for {@link BoundsCommand}s for {@link AbstractPositionedSlide}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideBoundsCommandBeingArguments extends AbstractBoundsCommandBeginArguments implements BoundsCommandBeginArguments {
	/** The slide */
	protected AbstractPositionedSlide slide;
	
	/**
	 * Full constructor.
	 * @param start the start point in slide space
	 * @param slide the slide
	 */
	public SlideBoundsCommandBeingArguments(Point start, AbstractPositionedSlide slide) {
		super(start);
		this.slide = slide;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#translate(int, int)
	 */
	@Override
	public void translate(int dx, int dy) {
		this.slide.translate(dx, dy);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.command.BoundsCommandBeginArguments#resize(int, int)
	 */
	@Override
	public void resize(int dw, int dh) {
		this.slide.resize(dw, dh);
	}
	
	/**
	 * Returns the slide.
	 * @return {@link AbstractPositionedSlide}
	 */
	public AbstractPositionedSlide getSlide() {
		return this.slide;
	}
}

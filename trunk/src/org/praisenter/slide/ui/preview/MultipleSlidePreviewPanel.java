package org.praisenter.slide.ui.preview;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.praisenter.slide.Slide;

/**
 * Represents a generic multi-slide preview panel.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class MultipleSlidePreviewPanel extends AbstractSlidePreviewPanel {
	/** The version id */
	private static final long serialVersionUID = -3624894619357850489L;

	/** The slides to render */
	protected List<Slide> slides;
	
	/** The spacing between the displays */
	protected int innerSpacing;
	
	/**
	 * Full constructor.
	 * @param innerSpacing the spacing between the displays
	 * @param nameSpacing the spacing between the display and its name
	 * @param includeDisplayName true if display names should be rendered
	 */
	public MultipleSlidePreviewPanel(int innerSpacing, int nameSpacing, boolean includeDisplayName) {
		super(nameSpacing, includeDisplayName);
		this.slides = new ArrayList<Slide>();
		this.innerSpacing = innerSpacing;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.preview.AbstractSlidePreviewPanel#paintPreview(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	protected void paintPreview(Graphics2D g2d, Rectangle bounds) {
		this.renderSlides(g2d, bounds);
	}
	
	/**
	 * Renders the slides.
	 * @param g2d the graphics object.
	 * @param bounds the total available rendering bounds
	 */
	protected abstract void renderSlides(Graphics2D g2d, Rectangle bounds);
	
	/**
	 * Adds the given slide to this preview panel.
	 * @param slide the slide to add
	 */
	public void addSlide(Slide slide) {
		this.slides.add(slide);
		this.invalidate();
	}
	
	/**
	 * Removes the given slide from this preview panel.
	 * @param slide the slide to remove
	 * @return boolean true if the slide was removed
	 */
	public boolean removeSlide(Slide slide) {
		boolean success = this.slides.remove(slide);
		this.invalidate();
		return success;
	}
	
	/**
	 * Shifts the slides by the given amount.
	 * @param n the shift amount
	 */
	public void shiftSlides(int n) {
		int size = this.slides.size();
		if (size > 1 && n < size) {
			Collections.rotate(this.slides, n);
		}
	}
	
	/**
	 * Clears all the slides from this preview panel.
	 */
	public void clearSlides() {
		this.slides.clear();
		this.invalidate();
	}
	
	/**
	 * Returns the number of slides in this preview panel.
	 * @return int
	 */
	public int getSlideCount() {
		return this.slides.size();
	}
	
	/**
	 * Gets the slide at the given index.
	 * @param index the index
	 * @return {@link Slide}
	 */
	public Slide getSlide(int index) {
		return this.slides.get(index);
	}
	
	/**
	 * Returns the spacing between the slides in pixels.
	 * @return int
	 */
	public int getInnerSpacing() {
		return this.innerSpacing;
	}
	
	/**
	 * Sets the spacing between the slides.
	 * @param spacing the spacing in pixels
	 */
	public void setInnerSpacing(int spacing) {
		this.innerSpacing = spacing;
		this.invalidate();
	}
}

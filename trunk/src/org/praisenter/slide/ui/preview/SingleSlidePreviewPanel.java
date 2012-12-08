package org.praisenter.slide.ui.preview;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.praisenter.slide.Slide;
import org.praisenter.slide.ui.SlidePreviewMetrics;

/**
 * Represents a generic single slide preview panel.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SingleSlidePreviewPanel extends AbstractSlidePreviewPanel {
	/** The version id */
	private static final long serialVersionUID = 1683646603420459379L;
	
	/** The slide to render */
	protected Slide slide;
	
	/**
	 * Default constructor.
	 */
	public SingleSlidePreviewPanel() {
		super(0, false);
		this.slide = null;
	}
	
	/**
	 * Constructor for creating a {@link SingleSlidePreviewPanel} with the slide name rendered.
	 * @param nameSpacing the spacing between the display and its name
	 */
	public SingleSlidePreviewPanel(int nameSpacing) {
		super(nameSpacing, true);
		this.slide = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.DisplayPreviewPanel#paintPreview(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	protected void paintPreview(Graphics2D g2d, Rectangle bounds) {
		// call the render display
		if (this.slide != null) {
			this.renderSlide(g2d, bounds);
		}
	}
	
	/**
	 * Renders the slide.
	 * @param g2d the graphics object.
	 * @param bounds the available rendering bounds
	 */
	protected void renderSlide(Graphics2D g2d, Rectangle bounds) {
		// get the slide metrics
		SlidePreviewMetrics metrics = this.getSlideMetrics(this.slide, bounds.width, bounds.height);
		
		// save the old transform
		AffineTransform oldTransform = g2d.getTransform();
		
		// apply the y translation
		g2d.translate(bounds.x, bounds.y);
		
		// render the slide
		this.renderSlide(g2d, this.slide, metrics);

		// reset the transform
		g2d.setTransform(oldTransform);
	}

	/**
	 * Returns the slide.
	 * @return {@link Slide}
	 */
	public Slide getSlide() {
		return this.slide;
	}
	
	/**
	 * Sets the slide to render.
	 * @param slide the slide
	 */
	public void setSlide(Slide slide) {
		this.slide = slide;
		this.invalidate();
	}
}

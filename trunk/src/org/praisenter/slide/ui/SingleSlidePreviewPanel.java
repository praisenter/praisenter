package org.praisenter.slide.ui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.praisenter.slide.Slide;

/**
 * Represents a generic single slide preview panel.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SingleSlidePreviewPanel extends AbstractSlidePreviewPanel {
	/** The version id */
	private static final long serialVersionUID = 1171231926175647703L;

	/** The slide to render */
	protected Slide slide;
	
	/** The display name */
	protected String name;

	/**
	 * Default constructor.
	 */
	public SingleSlidePreviewPanel() {
		this(0, null);
	}
	
	/**
	 * Constructor for creating a {@link SingleSlidePreviewPanel} with the slide name rendered.
	 * @param nameSpacing the spacing between the display and its name
	 * @param name the name of the display
	 */
	public SingleSlidePreviewPanel(int nameSpacing, String name) {
		super(nameSpacing, true);
		this.name = name;
		this.slide = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.DisplayPreviewPanel#paintPreview(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	protected void paintPreview(Graphics2D g2d, Rectangle bounds) {
		// call the render display
		this.renderSlide(g2d, bounds);
	}
	
	/**
	 * Renders the slide.
	 * @param g2d the graphics object.
	 * @param bounds the available rendering bounds
	 */
	protected void renderSlide(Graphics2D g2d, Rectangle bounds) {
		// get the display metrics
		SlidePreviewMetrics metrics = this.getSlideMetrics(g2d, this.slide, bounds.width, bounds.height);
		
		// save the old transform
		AffineTransform oldTransform = g2d.getTransform();
		
		// apply the y translation
		g2d.translate(bounds.x, bounds.y);
		
		// render the display
		this.renderSlide(g2d, this.slide, this.name, metrics);

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
	}
}

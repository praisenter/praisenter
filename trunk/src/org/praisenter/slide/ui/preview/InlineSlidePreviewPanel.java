package org.praisenter.slide.ui.preview;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.praisenter.slide.Slide;

/**
 * Represents a panel that shows a preview of slides on one line.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class InlineSlidePreviewPanel extends MultipleSlidePreviewPanel {
	/** The version id */
	private static final long serialVersionUID = -452033189762043654L;

	/**
	 * Constructor to create an {@link InlineSlidePreviewPanel} with slide names.
	 * @param innerSpacing the slide inner spacing
	 * @param nameSpacing the spacing between the slide and its name
	 */
	public InlineSlidePreviewPanel(int innerSpacing, int nameSpacing) {
		super(innerSpacing, nameSpacing, true);
	}
	
	/**
	 * Constructor to create an {@link InlineSlidePreviewPanel} without slide names.
	 * @param innerSpacing the slide inner spacing
	 */
	public InlineSlidePreviewPanel(int innerSpacing) {
		super(innerSpacing, 0, false);
	}
	
	/**
	 * Sets the minimum size of this component to the computed
	 * size using the given maximum slide dimension.
	 * @param maximum the maximum dimension of a slide
	 */
	public void setMinimumSize(int maximum) {
		Dimension size = this.getComputedSize(maximum);
		if (size != null) {
			this.setMinimumSize(size);
		}
	}
	
	/**
	 * Returns a dimension that will fit the slides given the maximum
	 * slide dimension.
	 * @param maximum the maximum slide dimension
	 * @return Dimension
	 */
	protected Dimension getComputedSize(int maximum) {
		int n = this.slides.size();

		// if there are no slides, then just return null
		if (n == 0) return null;
		
		// estimate the total width and total height
		double m = maximum;
		double w = 0;
		double h = 0;
		
		// loop over the slides
		for (int i = 0; i < n; i++) {
			// get the slide size
			Slide slide = this.slides.get(i);
			double sw = slide.getWidth();
			double sh = slide.getHeight();
			
			// compute the width/height scales
			double pw = m / sw;
			double ph = m / sh;
			
			// use the scaling factor that will scale the most
			double sc = pw < ph ? pw : ph;
			
			// compute the width of the slide with all the features
			double dw = sc * sw;
			double dh = sc * sh;
			// increment the width
			w += dw;
			// only choose the largest height
			if (dh > h) {
				h = dh;
			}
		}
		
		return new Dimension((int)Math.ceil(w), (int)Math.ceil(h));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.preview.MultipleSlidePreviewPanel#renderSlides(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	protected void renderSlides(Graphics2D g2d, Rectangle bounds) {
		int n = this.slides.size();
		
		// if there are no displays to render then just return
		if (n == 0) return;
		
		// the width/height of each display
		final int w = (bounds.width - this.innerSpacing * (n - 1));
		final int adw = w / n;
		final int adh = bounds.height;
		
		// compute the display metrics for all the displays
		// also compute the total width
		SlidePreviewMetrics[] metrics = new SlidePreviewMetrics[n];
		int tw = this.innerSpacing * (n - 1);
		for (int i = 0; i < n; i++) {
			metrics[i] = this.getSlideMetrics(this.slides.get(i), adw, adh);
			tw += metrics[i].totalWidth;
		}
		
		// save the old transform
		AffineTransform oldTransform = g2d.getTransform();
		
		// apply the y translation
		int px = bounds.width > tw ? (bounds.width - tw) / 2 : 0;
		g2d.translate(bounds.x + px, bounds.y);
		
		// preferably the displays are all the same aspect ratio
		// but we can't guarantee it
		for (int i = 0; i < n; i++) {
			Slide slide = this.slides.get(i);
			SlidePreviewMetrics displayMetrics = metrics[i];
			
			this.renderSlide(g2d, slide, displayMetrics);
			
			// apply the x translation of the width
			g2d.translate(displayMetrics.totalWidth + this.innerSpacing, 0);
		}
		
		// reset the transform
		g2d.setTransform(oldTransform);
	}
}

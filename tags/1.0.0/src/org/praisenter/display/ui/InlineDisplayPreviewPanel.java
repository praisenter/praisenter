package org.praisenter.display.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.praisenter.display.Display;

/**
 * Represents a panel that shows a preview of displays on one line.
 * @param <E> the display type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class InlineDisplayPreviewPanel<E extends Display> extends MultipleDisplayPreviewPanel<E> {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;

	/**
	 * Constructor to create an {@link InlineDisplayPreviewPanel} with display names.
	 * @param innerSpacing the display inner spacing
	 * @param nameSpacing the spacing between the display and its name
	 */
	public InlineDisplayPreviewPanel(int innerSpacing, int nameSpacing) {
		super(innerSpacing, nameSpacing, true);
	}
	
	/**
	 * Constructor to create an {@link InlineDisplayPreviewPanel} without display names.
	 * @param innerSpacing the display inner spacing
	 */
	public InlineDisplayPreviewPanel(int innerSpacing) {
		super(innerSpacing, 0, false);
	}
	
	/**
	 * Sets the minimum size of this component to the computed
	 * size using the given maximum display dimension.
	 * @param maximum the maximum dimension of a display
	 */
	public void setMinimumSize(int maximum) {
		Dimension size = this.getComputedSize(maximum);
		if (size != null) {
			this.setMinimumSize(size);
		}
	}
	
	/**
	 * Returns a dimension that will fit the displays given the maximum
	 * display dimension.
	 * @param maximum the maximum display dimension
	 * @return Dimension
	 */
	protected Dimension getComputedSize(int maximum) {
		int n = this.displays.size();

		// if there are no displays, then just return null
		if (n == 0) return null;
		
		// estimate the total width and total height
		double m = maximum;
		double w = 0;
		double h = 0;
		
		// loop over the displays
		for (int i = 0; i < n; i++) {
			// get the display size
			Display display = this.displays.get(i);
			Dimension size = display.getDisplaySize();
			
			// compute the width/height scales
			double pw = m / size.getWidth();
			double ph = m / size.getHeight();
			
			// use the scaling factor that will scale the most
			double sc = pw < ph ? pw : ph;
			
			// compute the width of the display with all the features
			double dw = sc * size.getWidth();
			double dh = sc * size.getHeight();
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
	 * @see org.praisenter.display.ui.MultipleDisplayPreviewPanel#renderDisplays(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	protected void renderDisplays(Graphics2D g2d, Rectangle bounds) {
		int n = this.displays.size();
		
		// if there are no displays to render then just return
		if (n == 0) return;
		
		// the width/height of each display
		final int w = (bounds.width - this.innerSpacing * (n - 1));
		final int adw = w / n;
		final int adh = bounds.height;
		
		// compute the display metrics for all the displays
		// also compute the total width
		DisplayPreviewMetrics[] metrics = new DisplayPreviewMetrics[n];
		int tw = this.innerSpacing * (n - 1);
		for (int i = 0; i < n; i++) {
			metrics[i] = this.getDisplayMetrics(g2d, this.displays.get(i), adw, adh);
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
			E display = this.displays.get(i);
			DisplayPreviewMetrics displayMetrics = metrics[i];
			
			String name = this.getDisplayName(display, i);
			this.renderDisplay(g2d, display, name, displayMetrics);
			
			// apply the x translation of the width
			g2d.translate(displayMetrics.totalWidth + this.innerSpacing, 0);
		}
		
		// reset the transform
		g2d.setTransform(oldTransform);
	}
}

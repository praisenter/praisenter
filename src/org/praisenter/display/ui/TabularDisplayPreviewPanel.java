package org.praisenter.display.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.praisenter.display.Display;
import org.praisenter.utilities.FontManager;

/**
 * Represents a panel that shows a preview of displays in a
 * tabular format.
 * @param <E> the display type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TabularDisplayPreviewPanel<E extends Display> extends MultipleDisplayPreviewPanel<E> {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;

	protected int columns;
	
	/** The list of display names */
	protected String[] displayNames;
	
	/**
	 * Constructor to create an {@link TabularDisplayPreviewPanel} with display names.
	 * @param innerSpacing the display inner spacing
	 * @param nameSpacing the spacing between the display and its name
	 * @param displayNames the display names
	 */
	public TabularDisplayPreviewPanel(int columns, int innerSpacing, int nameSpacing, String... displayNames) {
		super(innerSpacing, nameSpacing, true);
		this.columns = columns;
		this.displayNames = displayNames;
	}
	
	/**
	 * Constructor to create an {@link TabularDisplayPreviewPanel} without display names.
	 * @param innerSpacing the display inner spacing
	 */
	public TabularDisplayPreviewPanel(int columns, int innerSpacing) {
		this(columns, innerSpacing, 0, (String[])null);
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
	// FIXME this will set the size of the panel so that all displays can fit, this panel should then be placed in a scrollpane
	protected Dimension getComputedSize(int maximum) {
		int n = this.displays.size();

		// if there are no displays, then just return null
		if (n == 0) return null;
		
		// get the insets
		Insets insets = this.getInsets();
		
		// estimate the total width and total height
		double m = maximum;
		double w = insets.left + insets.right;
		double h = insets.top + insets.bottom;
		
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
			h += dh;
		}
		
		if (this.includeDisplayName) {
			// estimate the text height
			Rectangle2D bounds = FontManager.getDefaultFont().getMaxCharBounds(new FontRenderContext(new AffineTransform(), true, false));
			h += (bounds.getHeight() + this.nameSpacing) * (this.getNumberOfRows() - 1);
		}
		
		return new Dimension((int)Math.ceil(w), (int)Math.ceil(h));
	}
	
	protected int getNumberOfRows() {
		return (int)Math.ceil((double)this.displays.size() / (double)this.columns);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.DisplayPreviewPanel#getTotalAvailableRenderingBounds()
	 */
	@Override
	protected Rectangle getTotalAvailableRenderingBounds() {
		Rectangle bounds = super.getTotalAvailableRenderingBounds();
		// for tabular displays we need to remove the inner spacing
		bounds.width -= this.innerSpacing * (this.columns - 1);
		bounds.height -= this.innerSpacing * (this.getNumberOfRows() - 1);
		return bounds;
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
		final int adw = bounds.width / this.columns;
		final int adh = bounds.height;
		
		// compute the display metrics for all the displays
		// also compute the total width
		DisplayPreviewMetrics[] metrics = new DisplayPreviewMetrics[n];
		int tw = this.innerSpacing * (this.columns - 1) / 2;
		for (int i = 0; i < n; i++) {
			metrics[i] = this.getDisplayMetrics(g2d, this.displays.get(i), adw, adh);
			tw += metrics[i].totalWidth;
		}
		
		// save the old transform
		AffineTransform oldTransform = g2d.getTransform();
		
		// apply the y translation
		g2d.translate(bounds.x + (bounds.width - tw) / 2, bounds.y);
		
		// preferably the displays are all the same aspect ratio
		// but we can't guarantee it
		for (int i = 0; i < n; i++) {
			Display display = this.displays.get(i);
			DisplayPreviewMetrics displayMetrics = metrics[i];
			
			String name = null;
			if (this.displayNames != null && this.displayNames.length > i) {
				name = this.displayNames[i];
			}
			this.renderDisplay(g2d, display, name, displayMetrics);
			
			if ((i + 1) % this.columns == 0) {
				// FIXME the x coordinate needs to be reset to bounds.x + (bounds.width - tw) / 2
				g2d.translate(displayMetrics.totalWidth + this.innerSpacing, displayMetrics.totalHeight + this.innerSpacing);
			} else {
				// apply the x translation of the width
				g2d.translate(displayMetrics.totalWidth + this.innerSpacing, 0);
			}
		}
		
		// reset the transform
		g2d.setTransform(oldTransform);
	}
}

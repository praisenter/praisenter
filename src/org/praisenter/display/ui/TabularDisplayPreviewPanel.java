package org.praisenter.display.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JScrollPane;

import org.praisenter.display.Display;
import org.praisenter.utilities.FontManager;

/**
 * Represents a panel that shows a preview of displays in a tabular format.
 * @param <E> the display type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class TabularDisplayPreviewPanel<E extends Display> extends MultipleDisplayPreviewPanel<E> {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;

	protected int columns;
	
	protected JScrollPane scroller;
	
	/**
	 * Constructor to create an {@link TabularDisplayPreviewPanel} with display names.
	 * @param innerSpacing the display inner spacing
	 * @param nameSpacing the spacing between the display and its name
	 */
	public TabularDisplayPreviewPanel(int columns, int innerSpacing, int nameSpacing) {
		super(innerSpacing, nameSpacing, true);
		this.columns = columns;
	}
	
	/**
	 * Constructor to create an {@link TabularDisplayPreviewPanel} without display names.
	 * @param innerSpacing the display inner spacing
	 */
	public TabularDisplayPreviewPanel(int columns, int innerSpacing) {
		this(columns, innerSpacing, 0);
	}
	
	/**
	 * Returns a dimension that will fit the displays given the maximum
	 * display dimension.
	 * @param maximum the maximum display dimension
	 * @return Dimension
	 */
	// FIXME this will set the size of the panel so that all displays can fit, this panel should then be placed in a scrollpane
	// FIXME this isnt quite the way i want it.  The slides need to be sized as if they were using the inline display
	public Dimension getComputedSize(int maximum) {
		int n = this.displays.size();

		// if there are no displays, then just return null
		if (n == 0) return null;
		
		// estimate the total width and total height
		double m = maximum;
		double w = 0;
		double h = 0;
		
		double rw = 0.0;
		double rh = 0.0;
		
		double tw = maximum;
		if (this.scroller != null) {
			Insets insets = this.getInsets();
			//FIXME this should be the JScrollPane
//			Component container = this.getParent().getParent();
			tw = this.scroller.getSize().getWidth();
			tw /= this.columns;
		}
		
		// loop over the displays
		for (int i = 0; i < n; i++) {
			// get the display size
			Display display = this.displays.get(i);
			Dimension size = display.getDisplaySize();
			
			// compute the width/height scales
			double sc = tw / size.getWidth();
//			double ph = m / size.getHeight();
			
			// use the scaling factor that will scale the most
//			double sc = pw < ph ? pw : ph;
			
			// compute the width of the display with all the features
			double dw = sc * size.getWidth();
			double dh = sc * size.getHeight();
			// increment the width
			rw += dw;
			// take the max height for the row
			rh = rh < dh ? dh : rh;
			if ((i + 1) % this.columns == 0) {
				// only take the maximum row width
				w = w < rw ? rw : w;
				rw = 0.0;
				// increment the height
				h += rh;
				rh = 0.0;
			}
		}
		
//		w += this.innerSpacing * (this.columns - 1) + insets.left + insets.right;
		h += rh;
		return new Dimension((int)Math.ceil(w), (int)Math.ceil(h));
	}
	
	protected int getNumberOfRows() {
		return (int)Math.ceil((double)this.displays.size() / (double)this.columns);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.MultipleDisplayPreviewPanel#renderDisplays(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	protected void renderDisplays(Graphics2D g2d, Rectangle bounds) {
		int n = this.displays.size();
		
		// if there are no displays to render then just return
		if (n == 0) return;
		
		int rows = this.getNumberOfRows();
		final int w = bounds.width - this.innerSpacing * (this.columns - 1);
		final int h = bounds.height - this.innerSpacing * (rows - 1);
		
		// the width/height of each display
		final int adw = w / this.columns;
		final int adh = h / rows;
		
		// compute the display metrics for all the displays
		// also compute the total width
		DisplayPreviewMetrics[] metrics = new DisplayPreviewMetrics[n];
		int tw = 0;
		int rw = 0;
		int mrw = 0;
		for (int i = 0; i < n; i++) {
			metrics[i] = this.getDisplayMetrics(g2d, this.displays.get(i), adw, adh);
			rw += metrics[i].totalWidth;
			if ((i + 1) % this.columns == 0) {
				// keep the maximum row width
				mrw = mrw < rw ? rw : mrw;
				rw = 0;
			}
		}
		tw += mrw;
		
		// save the old transform
		AffineTransform oldTransform = g2d.getTransform();
		
		// apply the y translation
		g2d.translate(bounds.x + (w - tw - this.innerSpacing) / 2, bounds.y);
		
		// preferably the displays are all the same aspect ratio
		// but we can't guarantee it
		int x = 0;
		int mh = 0;
		for (int i = 0; i < n; i++) {
			E display = this.displays.get(i);
			DisplayPreviewMetrics displayMetrics = metrics[i];
			
			String name = this.getDisplayName(display, i);
			this.renderDisplay(g2d, display, name, displayMetrics);
			
			// use the maximum height
			mh = mh < displayMetrics.totalHeight ? displayMetrics.totalHeight : mh;
			
			int dx = displayMetrics.totalWidth + this.innerSpacing;
			if ((i + 1) % this.columns == 0) {
				// FIXME the x coordinate needs to be reset to bounds.x + (bounds.width - tw) / 2
				// i think keeping a total of the x translation and then translating back will do the job
				g2d.translate(-x, mh + this.innerSpacing);
				x = 0;
				mh = 0;
			} else {
				x += dx;
				// apply the x translation of the width
				g2d.translate(dx, 0);
			}
		}
		
		// reset the transform
		g2d.setTransform(oldTransform);
	}
	
	public void setScrollPane(JScrollPane scroller) {
		this.scroller = scroller;
		this.scroller.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension size = getComputedSize(300);
				setPreferredSize(size);
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}
}

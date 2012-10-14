package org.praisenter.display.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JScrollPane;

import org.praisenter.display.Display;

/**
 * Represents a panel that shows a preview of displays on one line.
 * @param <E> the display type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ScrollableInlineDisplayPreviewPanel<E extends InlineDisplayPreviewPanel<K>, K extends Display> extends JScrollPane {
	/** The version id */
	
	protected E panel;

	public ScrollableInlineDisplayPreviewPanel(E panel) {
		super(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		this.panel = panel;
		this.setPanelSize();
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				setPanelSize();
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}
	
	public void updatePanelSize() {
		setPanelSize();
	}
	
	protected void setPanelSize() {
		Dimension size = this.getComputedPanelSize();
		this.panel.setMinimumSize(size);
		this.panel.setPreferredSize(size);
	}
	
	protected Dimension getComputedPanelSize() {
		// get the size of this scroll pane
		Dimension size = this.getSize();
		Insets insets = this.panel.getInsets();
		
		// we want to use the height of the scroll pane as the height of the slides
		int sh = size.height - this.getHorizontalScrollBar().getSize().height;
		
		// we need to compute the real width of the panel using the maximum height
		int n = this.panel.displays.size();

		// if there are no displays, then just return the scroll pane's size
		if (n == 0) return size;
		
		// estimate the total width
		int rh = sh - insets.bottom - insets.top;
		int w = this.panel.innerSpacing * (n - 1) + insets.left + insets.right;
		
		// loop over the displays
		for (int i = 0; i < n; i++) {
			// get the display size
			Display display = this.panel.displays.get(i);
			Dimension ds = display.getDisplaySize();
			
			// compute height scale and use that metric for the width
//			double sc = rh / ds.getHeight();
			
			// compute the width of the display with all the features
//			double dw = sc * ds.getWidth();
			
			DisplayPreviewMetrics metrics = this.panel.getDisplayMetrics((Graphics2D)null, display, Integer.MAX_VALUE, rh);
			int dw = metrics.totalWidth;
			System.out.println("Computed: " + dw + " " + metrics.totalHeight);
			// increment the width
			w += dw;
		}
		
		System.out.println("Computed: " + w);
		
		return new Dimension(w, sh);
	}
}

/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.application.slide.ui.preview;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JScrollPane;

import org.praisenter.slide.Slide;

/**
 * A custom scroll panel for an {@link InlineSlidePreviewPanel}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ScrollableInlineSlidePreviewPanel extends JScrollPane {
	/** The version id */
	private static final long serialVersionUID = 2866956917883845725L;
	
	/** The panel to scroll */
	protected InlineSlidePreviewPanel panel;

	/**
	 * Full constructor.
	 * @param panel the panel to scroll
	 */
	public ScrollableInlineSlidePreviewPanel(InlineSlidePreviewPanel panel) {
		// pass the panel as the view
		super(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		this.panel = panel;
		
		// set the initial panel size
		this.setPanelSize();
		
		// listen for component resize events
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentResized(ComponentEvent e) {
				// on resize of the scroll panel, recompute the bounds of the panel
				setPanelSize();
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}
	
	/**
	 * Recomputes the panel size.
	 */
	public void updatePanelSize() {
		this.setPanelSize();
	}
	
	/**
	 * Sets the size of the panel to the computed size.
	 */
	protected void setPanelSize() {
		Dimension size = this.getComputedPanelSize();
		this.panel.setPreferredSize(size);
		this.panel.setMaximumSize(size);
		// revalidate the panel after the size has been set
		// so that the panel's layout is resized and the panel
		// is redrawn to the correct sizing
		this.panel.revalidate();
	}
	
	/**
	 * Computes the size of the panel depending on the size
	 * of this scroll panel.
	 * @return {@link Dimension}
	 */
	protected Dimension getComputedPanelSize() {
		// get the size of this scroll pane
		Dimension size = this.getSize();
		Insets insets = this.panel.getInsets();
		int sh = size.height;
		
		// we need to compute the real width of the panel using the maximum height
		int n = this.panel.slides.size();

		// if there are no slides, then just return the scroll pane's size
		if (n == 0) return size;
		
		// estimate the total width
		int rh = sh - insets.bottom - insets.top;
		int w = this.panel.innerSpacing * (n - 1) + insets.left + insets.right;
		int h = 0;
		
		// loop over the displays
		for (int i = 0; i < n; i++) {
			// get the slide size
			Slide slide = this.panel.slides.get(i);
			
			// attempt to size based on the height of the scrollable panel
			SlidePreviewMetrics metrics = this.panel.getSlideMetrics(slide, Integer.MAX_VALUE, rh);
			int dw = metrics.totalWidth;
			int dh = metrics.totalHeight;
			// increment the width
			w += dw;
			// take max height
			h = h < dh ? dh : h;
		}
		
		// add in the insets
		h += insets.bottom + insets.top;
		
		return new Dimension(w, h);
	}
	
	/**
	 * Sets the loading status of this scrollable preview panel.
	 * <p>
	 * Use the {@link #updatePanelSize()} and {@link #repaint()} methods to update
	 * the scrollable preview panel without the loading animation.
	 * @param flag true if the preview is loading
	 */
	public void setLoading(boolean flag) {
		if (flag) {
			// set the size of the view we are scrolling to the size
			// of this scroller panel
			// this will make the loading animation appear in the center
			// of the viewable area (as opposed to the center of the view's area)
			Dimension size = new Dimension(this.getSize());
			this.panel.setMinimumSize(size);
			this.panel.setPreferredSize(size);
			this.panel.setMaximumSize(size);
		} else {
			// if we are done loading then we need to resize the
			// view and reset the horizontal scrollbar position
			this.updatePanelSize();
			this.getHorizontalScrollBar().setValue(0);
		}
		// finally set the loading status on the view
		this.panel.setLoading(flag);
	}
	
	
}

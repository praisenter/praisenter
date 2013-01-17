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
package org.praisenter.slide.ui.preview;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.praisenter.slide.Slide;

/**
 * Represents a generic single slide preview panel.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
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

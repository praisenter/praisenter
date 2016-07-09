/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.slide;

import java.util.UUID;

import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideShadow;
import org.praisenter.slide.graphics.SlideStroke;

// FEATURE rotation

/**
 * Base interface for both {@link Slide}s and {@link SlideComponent}s.
 * @author William Bittle
 * @version 3.0.0
 */
public interface SlideRegion {
	/** The minimum size of a region */
	public static final int MIN_SIZE = 20;
	
	/**
	 * Return's the unique id.
	 * @return UUID
	 */
	public abstract UUID getId();
	
	/**
	 * Returns the x coordinate of this region.
	 * @return int
	 */
	public abstract int getX();
	
	/**
	 * Sets the x coordinate of this region.
	 * @param x the x coordinate
	 */
	public abstract void setX(int x);
	
	/**
	 * Returns the y coordinate of this region.
	 * @return int
	 */
	public abstract int getY();
	
	/**
	 * Sets the y coordinate of this region.
	 * @param y the y coordinate
	 */
	public abstract void setY(int y);
	
	/**
	 * Returns the width of this region.
	 * @return int
	 */
	public abstract int getWidth();
	
	/**
	 * Sets the width of this region.
	 * @param width the width
	 */
	public abstract void setWidth(int width);
	
	/**
	 * Returns the height of this region.
	 * @return int
	 */
	public abstract int getHeight();
	
	/**
	 * Sets the height of this region.
	 * @param height the height
	 */
	public abstract void setHeight(int height);
	
	/**
	 * Returns the background of this region.
	 * @return {@link SlidePaint}
	 */
	public abstract SlidePaint getBackground();
	
	/**
	 * Sets the background of this region.
	 * @param background the background
	 */
	public abstract void setBackground(SlidePaint background);
	
	/**
	 * Returns the border of this region.
	 * @return {@link SlideStroke}
	 */
	public abstract SlideStroke getBorder();
	
	/**
	 * Sets the border of this region.
	 * @param border the border
	 */
	public abstract void setBorder(SlideStroke border);
	
	/**
	 * Sets the transparency of the region.
	 * @param opacity the transparency in the range [0, 1]
	 */
	public abstract void setOpacity(double opacity);
	
	/**
	 * Returns the transparency of the region.
	 * @return double
	 */
	public abstract double getOpacity();
	
	/**
	 * Sets the shadow of the region.
	 * @param shadow the shadow
	 */
	public abstract void setShadow(SlideShadow shadow);
	
	/**
	 * Returns the shadow of the region.
	 * @return {@link SlideShadow}
	 */
	public abstract SlideShadow getShadow();

	/**
	 * Sets the glow of the region.
	 * @param glow the glow
	 */
	public abstract void setGlow(SlideShadow glow);
	
	/**
	 * Returns the glow of the region.
	 * @return {@link SlideShadow}
	 */
	public abstract SlideShadow getGlow();
	
	// other
	
	/**
	 * Adjusts the size and position of this region based on the
	 * given percentages.
	 * <p>
	 * This is primarily used when refitting a {@link Slide} to 
	 * a target width/height when created on a screen with a
	 * different resolution.
	 * @param pw the percent change in width
	 * @param ph the percent change in height
	 */
	public abstract void adjust(double pw, double ph);
	
	/**
	 * Resizes this component based on the given change in width
	 * and height.
	 * <p>
	 * This method returns a rectangle to indicate the new size
	 * of this region. {@link SlideRegion}s have minimum sizes
	 * to ensure that they can always be selected when editing.
	 * @param dw the change in width
	 * @param dh the change in height
	 * @return {@link Rectangle}
	 * @see #MIN_SIZE
	 */
	public abstract Rectangle resize(int dw, int dh);
	
	/**
	 * Translates this region by the given change in x and y.
	 * @param dx the change in x
	 * @param dy the change in y
	 */
	public abstract void translate(int dx, int dy);
	
	// transition
	
	/**
	 * Returns true if the backgrounds of this region and the given region
	 * are identical, indicating that they do not need to be transitioned
	 * with the rest of the region content.
	 * @param region the other region
	 * @return boolean
	 */
	public abstract boolean isBackgroundTransitionRequired(SlideRegion region);
	
	// copying
	
	/**
	 * Returns a deep copy of this region.
	 * <p>
	 * The copy is deep in the sense that all mutable objects
	 * will be deep copied and immutable objects will be the shallow
	 * copied.
	 * @return {@link SlideRegion}
	 */
	public abstract SlideRegion copy();
}

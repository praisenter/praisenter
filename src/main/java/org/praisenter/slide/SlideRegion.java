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

import java.util.Set;
import java.util.UUID;

import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.media.MediaComponent;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextPlaceholderComponent;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

// FEATURE (L) Add the ability to rotate components

/**
 * Base interface for both {@link Slide}s and {@link SlideComponent}s.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
	@Type(value = BasicSlide.class, name = "slide"),
	@Type(value = BasicTextComponent.class, name = "text"),
	@Type(value = CountdownComponent.class, name = "countdown"),
	@Type(value = DateTimeComponent.class, name = "datetime"),
	@Type(value = MediaComponent.class, name = "media"),
	@Type(value = TextPlaceholderComponent.class, name = "placeholder")
})
public interface SlideRegion {
	/** The minimum size of a region */
	public static final double MIN_SIZE = 20.0;
	
	/**
	 * Return's the unique id.
	 * @return UUID
	 */
	public abstract UUID getId();
	
	/**
	 * Attempts to return a human readable name for this region.
	 * @return String
	 */
	public abstract String getName();
	
	/**
	 * Returns the x coordinate of this region.
	 * @return double
	 */
	public abstract double getX();
	
	/**
	 * Sets the x coordinate of this region.
	 * @param x the x coordinate
	 */
	public abstract void setX(double x);
	
	/**
	 * Returns the y coordinate of this region.
	 * @return double
	 */
	public abstract double getY();
	
	/**
	 * Sets the y coordinate of this region.
	 * @param y the y coordinate
	 */
	public abstract void setY(double y);
	
	/**
	 * Returns the width of this region.
	 * @return double
	 */
	public abstract double getWidth();
	
	/**
	 * Sets the width of this region.
	 * @param width the width
	 */
	public abstract void setWidth(double width);
	
	/**
	 * Returns the height of this region.
	 * @return double
	 */
	public abstract double getHeight();
	
	/**
	 * Sets the height of this region.
	 * @param height the height
	 */
	public abstract void setHeight(double height);
	
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
	public abstract Rectangle resize(double dw, double dh);
	
	/**
	 * Returns the rectangular bounds of this region.
	 * @return Rectangle
	 */
	public abstract Rectangle getBounds();
	
	/**
	 * Translates this region by the given change in x and y.
	 * @param dx the change in x
	 * @param dy the change in y
	 */
	public abstract void translate(double dx, double dy);
	
	/**
	 * Returns true if any of the given media ids are used on this slide region.
	 * @param ids the media ids
	 * @return boolean
	 */
	public boolean isMediaReferenced(UUID... ids);
	
	/**
	 * Returns a set of all the referenced media ids.
	 * @return Set&lt;UUID&gt;
	 */
	public Set<UUID> getReferencedMedia();
	
	// copying
	
	/**
	 * Returns a deep copy of this region.
	 * @return {@link SlideRegion}
	 */
	public abstract SlideRegion copy();
	
	/**
	 * Returns an deep copy of this region.
	 * @param exact true if an exact copy should be made
	 * @return {@link SlideRegion}
	 */
	public abstract SlideRegion copy(boolean exact);
}

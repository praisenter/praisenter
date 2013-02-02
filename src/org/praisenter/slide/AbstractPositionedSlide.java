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
package org.praisenter.slide;

import java.awt.Dimension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Abstract implementation of a positioned and sized slide.
 * <p>
 * This type of slide differs from standard slides in that they
 * do not occupy the entire screen height and width.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractPositionedSlide extends BasicSlide {
	/** The x coordinate of this slide */
	@XmlAttribute(name = "X", required = true)
	protected int x;
	
	/** The y coordinate of this slide */
	@XmlAttribute(name = "Y", required = true)
	protected int y;

	/** The width of the target device */
	@XmlAttribute(name = "DeviceWidth", required = true)
	protected int deviceWidth;
	
	/** The height of the target device */
	@XmlAttribute(name = "DeviceHeight", required = true)
	protected int deviceHeight;

	/**
	 * Full constructor.
	 * @param name the name of the slide/template
	 * @param deviceWidth the width of the target device
	 * @param deviceHeight the height of the target device
	 * @param slideWidth the width of the slide
	 * @param slideHeight the height of the slide
	 */
	public AbstractPositionedSlide(String name, int deviceWidth, int deviceHeight, int slideWidth, int slideHeight) {
		super(name, slideWidth, slideHeight);
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
	}

	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * @param slide the slide to copy
	 */
	public AbstractPositionedSlide(AbstractPositionedSlide slide) {
		super(slide);
		this.x = slide.x;
		this.y = slide.y;
		this.deviceWidth = slide.deviceWidth;
		this.deviceHeight = slide.deviceHeight;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#adjustSize(int, int)
	 */
	public void adjustSize(int deviceWidth, int deviceHeight) {
		// compute the resize percentages
		double pw = (double)deviceWidth / (double)this.deviceWidth;
		double ph = (double)deviceHeight / (double)this.deviceHeight;
		
		int w = (int)Math.ceil(this.width * pw);
		int h = (int)Math.ceil(this.height * ph);
		
		super.adjustSize(w, h);
		
		// apply this
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
	}

	/**
	 * Translates this slide by the given amount.
	 * @param dx the change in x
	 * @param dy the change in y
	 */
	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}
	
	/**
	 * Resizes this slide by the given amount.
	 * <p>
	 * Returns a dimension including the amount the slide was resized.
	 * This is only relevant when the slide has reached its minimum size.
	 * @param dw the change in width
	 * @param dh the change in height
	 * @return Dimension
	 */
	public Dimension resize(int dw, int dh) {
		// save the old width/height
		int w = this.width;
		int h = this.height;
		
		// update
		this.width += dw;
		this.height += dh;
		this.background.resize(dw, dh);
		
		// validate the width/height
		if (this.width < Slide.MINIMUM_SIZE) {
			this.width = Slide.MINIMUM_SIZE;
		}
		if (this.height < Slide.MINIMUM_SIZE) {
			this.height = Slide.MINIMUM_SIZE;
		}
		
		// return the difference
		return new Dimension(this.width - w, this.height - h);
	}
	
	/**
	 * Returns the x coordinate for this slide in pixels.
	 * @return int
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Sets the x coordinate for this slide. 
	 * @param x the x coorindate in pixels
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the y coordinate for this slide in pixels.
	 * @return int
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Sets the y coordinate for this slide.
	 * @param y the y coordinate in pixels
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Returns the stored target device width.
	 * @return int
	 */
	public int getDeviceWidth() {
		return this.deviceWidth;
	}

	/**
	 * Sets the target device width.
	 * <p>
	 * This is used in creation of the thumbnail for the slide
	 * to ensure the positioning and size of the slide.
	 * @param deviceWidth the target device width
	 */
	public void setDeviceWidth(int deviceWidth) {
		this.deviceWidth = deviceWidth;
	}

	/**
	 * Returns the stored target device height.
	 * @return int
	 */
	public int getDeviceHeight() {
		return this.deviceHeight;
	}

	/**
	 * Sets the target device height.
	 * <p>
	 * This is used in creation of the thumbnail for the slide
	 * to ensure the positioning and size of the slide.
	 * @param deviceHeight the target device height
	 */
	public void setDeviceHeight(int deviceHeight) {
		this.deviceHeight = deviceHeight;
	}
}

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

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.LineStyle;

/**
 * Represents a generic slide component with positioning and border.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
// TODO [LOW] SLIDE-TEMPLATE add rotation
// TODO [LOW] SLIDE-TEMPLATE add effects (drop shadow)
@XmlRootElement(name = "GenericSlideComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class GenericComponent extends AbstractPositionedComponent implements BackgroundComponent, PositionedComponent, RenderableComponent, SlideComponent, Serializable {
	/** The version id */
	private static final long serialVersionUID = -3607390712044250672L;

	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected GenericComponent() {
		this("", 200, 200);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public GenericComponent(String name, int width, int height) {
		this(name, 0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param name the name of the component
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public GenericComponent(String name, int x, int y, int width, int height) {
		super(name, width, height);
		this.x = x;
		this.y = y;
		this.borderFill = new ColorFill(Color.BLACK);
		this.borderStyle = new LineStyle();
		this.borderVisible = false;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public GenericComponent(GenericComponent component) {
		super(component);
		this.x = component.x;
		this.y = component.y;
		this.borderFill = component.borderFill;
		this.borderStyle = component.borderStyle;
		this.borderVisible = component.borderVisible;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#copy()
	 */
	@Override
	public GenericComponent copy() {
		return new GenericComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractSlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		// render the background
		if (this.backgroundVisible) {
			this.renderBackground(g, this.x, this.y);
		}
		// render the border
		if (this.borderVisible) {
			this.renderBorder(g);
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		// render the background
		if (this.backgroundVisible) {
			this.renderBackground(g, this.x, this.y);
		}
		// render the border
		if (this.borderVisible) {
			this.renderBorder(g);
		}
	}
}

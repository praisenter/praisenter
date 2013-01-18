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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.FillTypeAdapter;
import org.praisenter.slide.graphics.LinearGradientFill;
import org.praisenter.slide.graphics.RadialGradientFill;

/**
 * Abstract implementation of the {@link RenderableComponent} interface.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({
	ColorFill.class,
	LinearGradientFill.class,
	RadialGradientFill.class
})
public abstract class AbstractRenderableComponent implements SlideComponent, RenderableComponent {
	/** The component name */
	@XmlElement(name = "Name")
	protected String name;

	/** The z-ordering of this component */
	@XmlAttribute(name = "Order")
	protected int order;
	
	/** The width of this component */
	@XmlAttribute(name = "Width", required = true)
	protected int width;
	
	/** The height of this component */
	@XmlAttribute(name = "Height", required = true)
	protected int height;

	/** The background fill (color or gradient or anything really) */
	@XmlElement(name = "BackgroundFill")
	@XmlJavaTypeAdapter(value = FillTypeAdapter.class)
	protected Fill backgroundFill;
	
	/** True if the background paint should be rendered */
	@XmlElement(name = "BackgroundVisible")
	protected boolean backgroundVisible;

	/**
	 * Default constructor.
	 * <p>
	 * This should only be used by JAXB.
	 */
	protected AbstractRenderableComponent() {
		this(Messages.getString("slide.component.unnamed"), 200, 200);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractRenderableComponent(String name, int width, int height) {
		this.name = name;
		this.order = 1;
		this.width = width;
		this.height = height;
		this.backgroundFill = new ColorFill(Color.BLACK);
		this.backgroundVisible = false;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public AbstractRenderableComponent(AbstractRenderableComponent component) {
		this.name = component.name;
		this.order = component.order;
		this.width = component.width;
		this.height = component.height;
		this.backgroundFill = component.backgroundFill;
		this.backgroundVisible = component.backgroundVisible;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#getWidth()
	 */
	@Override
	public int getWidth() {
		return this.width;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#getHeight()
	 */
	@Override
	public int getHeight() {
		return this.height;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#setWidth(int)
	 */
	@Override
	public void setWidth(int width) {
		this.width = width;
		if (this.width < Slide.MINIMUM_SIZE) {
			this.width = Slide.MINIMUM_SIZE;
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		this.height = height;
		if (this.height < Slide.MINIMUM_SIZE) {
			this.height = Slide.MINIMUM_SIZE;
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#resize(int, int)
	 */
	@Override
	public Dimension resize(int dw, int dh) {
		// save the old width/height
		int w = this.width;
		int h = this.height;
		
		// update
		this.width += dw;
		this.height += dh;
		
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

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#adjust(double, double)
	 */
	@Override
	public void adjust(double pw, double ph) {
		this.width = (int)Math.floor((double)this.width * pw);
		this.height = (int)Math.floor((double)this.height * ph);
	}
	
	// rendering
	
	/**
	 * Renders the background at the specified coordinates.
	 * @param g the graphics object to render to
	 * @param x the x coordinate to begin rendering
	 * @param y the y coordinate to begin rendering
	 */
	protected void renderBackground(Graphics2D g, int x, int y) {
		if (this.backgroundFill != null) {
			Paint oPaint = g.getPaint();
			Shape oClip = g.getClip();
			
			g.clipRect(x, y, this.width, this.height);
			// we need to make sure the background paint is sized to component
			Paint paint = this.backgroundFill.getPaint(x, y, this.width, this.height);
			g.setPaint(paint);
			g.fillRect(x, y, this.width, this.height);
			
			g.setClip(oClip);
			g.setPaint(oPaint);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#getBackgroundFill()
	 */
	public Fill getBackgroundFill() {
		return this.backgroundFill;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#setBackgroundFill(org.praisenter.slide.graphics.Fill)
	 */
	public void setBackgroundFill(Fill backgroundFill) {
		this.backgroundFill = backgroundFill;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#isBackgroundVisible()
	 */
	@Override
	public boolean isBackgroundVisible() {
		return this.backgroundVisible;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#setBackgroundVisible(boolean)
	 */
	@Override
	public void setBackgroundVisible(boolean visible) {
		this.backgroundVisible = visible;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#getOrder()
	 */
	@Override
	public int getOrder() {
		return this.order;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableComponent#setOrder(int)
	 */
	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#getName()
	 */
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}
}

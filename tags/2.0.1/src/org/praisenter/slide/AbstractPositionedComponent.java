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
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.FillTypeAdapter;
import org.praisenter.slide.graphics.LineStyle;
import org.praisenter.slide.resources.Messages;

/**
 * Represents an abstract implementation of a {@link PositionedComponent}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractPositionedComponent extends AbstractRenderableComponent implements PositionedComponent, RenderableComponent, SlideComponent, Serializable {
	/** The version id */
	private static final long serialVersionUID = -2964343161968696888L;

	/** The x coordinate of this component */
	@XmlAttribute(name = "X", required = true)
	protected int x;
	
	/** The y coordinate of this component */
	@XmlAttribute(name = "Y", required = true)
	protected int y;
	
	/** The border fill (color or gradient or anything really) */
	@XmlElement(name = "BorderFill")
	@XmlJavaTypeAdapter(value = FillTypeAdapter.class)
	protected Fill borderFill;
	
	/** The border stroke */
	@XmlElement(name = "BorderStyle", required = false, nillable = true)
	protected LineStyle borderStyle;
	
	/** True if the border is visible */
	@XmlElement(name = "BorderVisible", required = true, nillable = false)
	protected boolean borderVisible;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected AbstractPositionedComponent() {
		this(Messages.getString("slide.component.unnamed"), 200, 200);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractPositionedComponent(String name, int width, int height) {
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
	public AbstractPositionedComponent(String name, int x, int y, int width, int height) {
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
	public AbstractPositionedComponent(AbstractPositionedComponent component) {
		super(component);
		this.x = component.x;
		this.y = component.y;
		this.borderFill = component.borderFill;
		this.borderStyle = component.borderStyle;
		this.borderVisible = component.borderVisible;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableComponent#isTransitionRequired(org.praisenter.slide.RenderableComponent)
	 */
	@Override
	public boolean isTransitionRequired(RenderableComponent component) {
		if (component == null) return true;
		if (this == component) return false;
		
		// check the type
		if (component instanceof PositionedComponent) {
			PositionedComponent other = (PositionedComponent)component;
			// check the positioning and border stuff
		
			// check the position
			if (this.x != other.getX() || this.y != other.getY()) {
				return true;
			}
			
			if (this.borderVisible && other.isBorderVisible()) {
				// check the border fill
				if (this.borderFill != null && other.getBorderFill() != null) {
					if (!this.borderFill.equals(other.getBorderFill())) {
						return true;
					}
				} else if (this.borderFill != null || other.getBorderFill() != null) {
					// one is not null
					return true;
				}
				// check the line style
				if (this.borderStyle != null && other.getBorderStyle() != null) {
					if (!this.borderStyle.equals(other.getBorderStyle())) {
						return true;
					}
				} else if (this.borderStyle != null || other.getBorderStyle() != null) {
					// one is not null
					return true;
				}
			} else if (this.borderVisible || other.isBorderVisible()) {
				// one is visible and the other isn't, so we have to transition
				return true;
			}
		} else {
			// if the given component is not of type PositionedComponent (or a decendent) then
			// we most likely need to transition
			return true;
		}
		
		// else, pass on the test to the super class
		return super.isTransitionRequired(component);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#getX()
	 */
	@Override
	public int getX() {
		return this.x;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#getY()
	 */
	@Override
	public int getY() {
		return this.y;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#setX(int)
	 */
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#setY(int)
	 */
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#translate(int, int)
	 */
	@Override
	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableSlideComponent#adjust(double, double)
	 */
	@Override
	public void adjust(double pw, double ph) {
		super.adjust(pw, ph);
		// also adjust the positioning
		this.x = (int)Math.ceil((double)this.x * pw);
		this.y = (int)Math.ceil((double)this.y * ph);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#getBounds()
	 */
	@Override
	public Shape getBounds() {
		// later we may add rotation in the mix, but for now we can just return a rectangle
		return new Rectangle(this.x, this.y, this.width, this.height);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#getRectangleBounds()
	 */
	@Override
	public Rectangle getRectangleBounds() {
		return this.getBounds().getBounds();
	}
	
	// rendering
	
	/**
	 * Renders the border of this component.
	 * @param g the graphics object to render to
	 */
	protected void renderBorder(Graphics2D g) {
		if (this.borderFill != null && this.borderStyle != null) {
			Paint oPaint = g.getPaint();
			Stroke oStroke = g.getStroke();
			// we need to make sure the border paint is sized to component
			// we also need to increase the size of the gradient by half the line width
			int lw = (int)Math.ceil(this.borderStyle.getWidth() * 0.5f);
			Paint paint = this.borderFill.getPaint(this.x - lw, this.y - lw, this.width + 2 * lw, this.height + 2 * lw);
			g.setPaint(paint);
			
			Stroke stroke = this.borderStyle.getStroke();
			g.setStroke(stroke);
			
			g.drawRect(this.x, this.y, this.width, this.height);
			
			g.setStroke(oStroke);
			g.setPaint(oPaint);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#getBorderFill()
	 */
	@Override
	public Fill getBorderFill() {
		return this.borderFill;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#setBorderFill(org.praisenter.slide.Fill)
	 */
	@Override
	public void setBorderFill(Fill fill) {
		this.borderFill = fill;
	}
 
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#getBorderStyle()
	 */
	@Override
	public LineStyle getBorderStyle() {
		return this.borderStyle;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#setBorderStyle(org.praisenter.slide.LineStyle)
	 */
	@Override
	public void setBorderStyle(LineStyle borderStyle) {
		this.borderStyle = borderStyle;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#isBorderVisible()
	 */
	@Override
	public boolean isBorderVisible() {
		return this.borderVisible;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#setBorderVisible(boolean)
	 */
	@Override
	public void setBorderVisible(boolean visible) {
		this.borderVisible = visible;
	}
}

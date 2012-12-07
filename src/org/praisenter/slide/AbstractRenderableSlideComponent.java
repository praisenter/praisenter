package org.praisenter.slide;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.xml.PaintTypeAdapter;

/**
 * Abstract implementation of the {@link RenderableSlideComponent} interface.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractRenderableSlideComponent implements SlideComponent, RenderableSlideComponent {
	/** The width of this component */
	@XmlAttribute(name = "Width", required = true)
	protected int width;
	
	/** The height of this component */
	@XmlAttribute(name = "Height", required = true)
	protected int height;

	/** The background paint (color or gradient or anything really) */
	@XmlElement(name = "BackgroundPaint", required = false, nillable = true)
	@XmlJavaTypeAdapter(value = PaintTypeAdapter.class)
	protected Paint backgroundPaint;
	
	/** True if the background paint should be rendered */
	@XmlElement(name = "BackgroundPaintVisible", required = true, nillable = false)
	protected boolean backgroundPaintVisible;

	/** The component name */
	@XmlElement(name = "Name", required = true, nillable = false)
	protected String name;
	
	/** The z-ordering of this component */
	@XmlAttribute(name = "Order")
	protected int order;
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractRenderableSlideComponent(String name, int width, int height) {
		this.width = width;
		this.height = height;
		this.backgroundPaint = Color.WHITE;
		this.backgroundPaintVisible = false;
		this.order = 1;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public AbstractRenderableSlideComponent(AbstractRenderableSlideComponent component) {
		this.width = component.width;
		this.height = component.height;
		this.backgroundPaint = component.backgroundPaint;
		this.backgroundPaintVisible = component.backgroundPaintVisible;
		this.order = component.order;
		this.name = component.name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#getWidth()
	 */
	@Override
	public int getWidth() {
		return this.width;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#getHeight()
	 */
	@Override
	public int getHeight() {
		return this.height;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#setWidth(int)
	 */
	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#resize(int, int)
	 */
	@Override
	public void resize(int dw, int dh) {
		this.width += dw;
		this.height += dh;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#resize(double, double)
	 */
	@Override
	public void resize(double pw, double ph) {
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
		if (this.backgroundPaintVisible && this.backgroundPaint != null) {
			Paint oPaint = g.getPaint();
			g.setPaint(this.backgroundPaint);
			g.fillRect(x, y, this.width, this.height);
			g.setPaint(oPaint);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#getBackgroundPaint()
	 */
	public Paint getBackgroundPaint() {
		return this.backgroundPaint;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#setBackgroundPaint(java.awt.Paint)
	 */
	public void setBackgroundPaint(Paint backgroundPaint) {
		this.backgroundPaint = backgroundPaint;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#isBackgroundPaintVisible()
	 */
	@Override
	public boolean isBackgroundPaintVisible() {
		return this.backgroundPaintVisible;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#setBackgroundPaintVisible(boolean)
	 */
	@Override
	public void setBackgroundPaintVisible(boolean visible) {
		this.backgroundPaintVisible = visible;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#getOrder()
	 */
	@Override
	public int getOrder() {
		return this.order;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#setOrder(int)
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

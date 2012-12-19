package org.praisenter.slide;

import java.awt.Color;
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
		this.backgroundFill = new ColorFill(Color.WHITE);
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
		if (this.width <= 20) {
			this.width = 20;
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		this.height = height;
		if (this.height <= 20) {
			this.height = 20;
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#resize(int, int)
	 */
	@Override
	public void resize(int dw, int dh) {
		this.width += dw;
		this.height += dh;
		if (this.width <= 20) {
			this.width = 20;
		}
		if (this.height <= 20) {
			this.height = 20;
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#resize(double, double)
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
	 * @see org.praisenter.slide.RenderableSlideComponent#getBackgroundFill()
	 */
	public Fill getBackgroundFill() {
		return this.backgroundFill;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#setBackgroundFill(org.praisenter.slide.Fill)
	 */
	public void setBackgroundFill(Fill backgroundFill) {
		this.backgroundFill = backgroundFill;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#isBackgroundPaintVisible()
	 */
	@Override
	public boolean isBackgroundVisible() {
		return this.backgroundVisible;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.RenderableSlideComponent#setBackgroundPaintVisible(boolean)
	 */
	@Override
	public void setBackgroundVisible(boolean visible) {
		this.backgroundVisible = visible;
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

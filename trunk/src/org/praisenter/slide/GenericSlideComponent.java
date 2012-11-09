package org.praisenter.slide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.xml.PaintTypeAdapter;
import org.praisenter.xml.StrokeTypeAdapter;

/**
 * Represents a generic slide component with positioning and border.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "GenericSlideComponent")
public class GenericSlideComponent extends AbstractRenderableSlideComponent implements SlideComponent, RenderableSlideComponent, PositionedSlideComponent {
	/** The x coordinate of this component */
	@XmlAttribute(name = "X", required = true)
	protected int x;
	
	/** The y coordinate of this component */
	@XmlAttribute(name = "Y", required = true)
	protected int y;
	
	/** The border paint (color or gradient or anything really) */
	@XmlElement(name = "BorderPaint", required = false, nillable = true)
	@XmlJavaTypeAdapter(value = PaintTypeAdapter.class)
	protected Paint borderPaint;
	
	/** The border stroke */
	@XmlElement(name = "BorderStroke", required = false, nillable = true)
	@XmlJavaTypeAdapter(value = StrokeTypeAdapter.class)
	protected Stroke borderStroke;
	
	/** True if the border is visible */
	@XmlElement(name = "BorderVisible", required = true, nillable = false)
	protected boolean borderVisible;
	
	/**
	 * Minimal constructor.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public GenericSlideComponent(int width, int height) {
		this(0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public GenericSlideComponent(int x, int y, int width, int height) {
		super(width, height);
		this.x = x;
		this.y = y;
		this.borderPaint = Color.BLACK;
		this.borderStroke = new BasicStroke();
		this.borderVisible = true;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public GenericSlideComponent(GenericSlideComponent component) {
		super(component);
		this.x = component.x;
		this.y = component.y;
		this.borderPaint = component.borderPaint;
		this.borderStroke = component.borderStroke;
		this.borderVisible = component.borderVisible;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#copy()
	 */
	@Override
	public GenericSlideComponent copy() {
		return new GenericSlideComponent(this);
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
	 * @see org.praisenter.slide.PositionedSlideComponent#getBounds()
	 */
	@Override
	public Shape getBounds() {
		// TODO add rotation
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		// render the background
		super.renderPreview(g);
		// render the border
		this.renderBorder(g);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractSlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		// render the background
		super.render(g);
		// render the border
		this.renderBorder(g);
	}

	/**
	 * Renders the border of this component.
	 * @param g the graphics object to render to
	 */
	protected void renderBorder(Graphics2D g) {
		if (this.borderVisible && this.borderPaint != null && this.borderStroke != null) {
			Paint oPaint = g.getPaint();
			Stroke oStroke = g.getStroke();
			
			g.setPaint(this.borderPaint);
			g.setStroke(this.borderStroke);
			g.drawRect(this.x, this.y, this.width, this.height);
			
			g.setStroke(oStroke);
			g.setPaint(oPaint);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#getBorderPaint()
	 */
	@Override
	public Paint getBorderPaint() {
		return this.borderPaint;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#setBorderPaint(java.awt.Paint)
	 */
	@Override
	public void setBorderPaint(Paint paint) {
		this.borderPaint = paint;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#getBorderStroke()
	 */
	@Override
	public Stroke getBorderStroke() {
		return this.borderStroke;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.PositionedSlideComponent#setBorderStroke(java.awt.Stroke)
	 */
	@Override
	public void setBorderStroke(Stroke stroke) {
		this.borderStroke = stroke;
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

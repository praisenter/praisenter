package org.praisenter.slide;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

/**
 * Abstract implementation of the {@link SlideComponent} interface.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractSlideComponent implements SlideComponent {
	/** The width of this component */
	protected int width;
	
	/** The height of this component */
	protected int height;

	/** The background paint (color or gradient or anything really) */
	protected Paint backgroundPaint;
	
	/** True if the background paint should be rendered */
	protected boolean backgroundPaintVisible;
	
	/**
	 * Minimal constructor.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractSlideComponent(int width, int height) {
		this.width = width;
		this.height = height;
		this.backgroundPaint = Color.WHITE;
		this.backgroundPaintVisible = false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#getWidth()
	 */
	@Override
	public int getWidth() {
		return this.width;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#getHeight()
	 */
	@Override
	public int getHeight() {
		return this.height;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#setWidth(int)
	 */
	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#resize(int, int)
	 */
	@Override
	public void resize(int dw, int dh) {
		this.width += dw;
		this.height += dh;
	}

	// rendering
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		this.renderBackground(g, 0, 0);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		this.renderBackground(g, 0, 0);
	}

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
	 * @see org.praisenter.slide.SlideComponent#getBackgroundPaint()
	 */
	public Paint getBackgroundPaint() {
		return this.backgroundPaint;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#setBackgroundPaint(java.awt.Paint)
	 */
	public void setBackgroundPaint(Paint backgroundPaint) {
		this.backgroundPaint = backgroundPaint;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#isBackgroundPaintVisible()
	 */
	@Override
	public boolean isBackgroundPaintVisible() {
		return this.backgroundPaintVisible;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#setBackgroundPaintVisible(boolean)
	 */
	@Override
	public void setBackgroundPaintVisible(boolean visible) {
		this.backgroundPaintVisible = visible;
	}
}

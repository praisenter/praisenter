package org.praisenter.slide;

import java.awt.Graphics2D;
import java.awt.Paint;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.TextComponent;

/**
 * Represents a {@link SlideComponent} that is renderable.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlSeeAlso({ ImageMediaComponent.class, 
			  VideoMediaComponent.class, 
			  TextComponent.class,
			  GenericSlideComponent.class })
public interface RenderableSlideComponent extends SlideComponent {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#copy()
	 */
	@Override
	public RenderableSlideComponent copy();
	
	/**
	 * Returns the width of the component in pixels.
	 * @return int
	 */
	public abstract int getWidth();
	
	/**
	 * Returns the height of the component in pixels.
	 * @return int
	 */
	public abstract int getHeight();
	
	/**
	 * Sets the width of this component.
	 * @param width the width in pixels
	 */
	public abstract void setWidth(int width);
	
	/**
	 * Sets the height of this component.
	 * @param height the height in pixels
	 */
	public abstract void setHeight(int height);
	
	/**
	 * Resizes this component using the given deltas.
	 * @param dw the change in width in pixels
	 * @param dh the change in height in pixels
	 */
	public abstract void resize(int dw, int dh);
	
	/**
	 * Resizes this component using the given percentatges.
	 * @param pw the width percentage
	 * @param ph the height percentage
	 */
	public abstract void resize(double pw, double ph);
	
	// rendering
	
	/**
	 * Renders the preview version of this component.
	 * <p>
	 * Most components will render identically, however, some
	 * like the video component will not.
	 * @param g the graphics object to render to
	 */
	public abstract void renderPreview(Graphics2D g);
	
	/**
	 * Renders the component.
	 * @param g the graphics object to render to
	 */
	public abstract void render(Graphics2D g);
	
	/**
	 * Returns the paint used to paint the background.
	 * @return Paint
	 */
	public abstract Paint getBackgroundPaint();
	
	/**
	 * Sets the paint used to paint the background.
	 * @param paint the paint
	 */
	public abstract void setBackgroundPaint(Paint paint);
	
	/**
	 * Returns true if the background paint is visible (or will be rendered).
	 * @return boolean
	 */
	public abstract boolean isBackgroundPaintVisible();
	
	/**
	 * Sets the background paint to visible (or rendered).
	 * @param visible true if the background paint should be rendered
	 */
	public abstract void setBackgroundPaintVisible(boolean visible);
}
